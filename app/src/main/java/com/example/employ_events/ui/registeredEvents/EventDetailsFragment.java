package com.example.employ_events.ui.registeredEvents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.employ_events.R;
import com.example.employ_events.databinding.EventDetailsBinding;
import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.events.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.Manifest;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/*
Authors: Tina, Jasleen.

This fragment is the page where a user can view the details, and is able to join and leave the event.
It displays a dialog warning if geolocation is required, which asks for proceed -> joins, cancel -> does not join.
It sends a user who has yet to set their name and email to the edit profile screen.

Have yet to implement checks that the registration start and end deadline are followed.

US 01.01.01	As an entrant, I want to join the waiting list for a specific event
US 01.01.02	As an entrant, I want to leave the waiting list for a specific event
US 01.08.01	As an entrant, I want to be warned before joining a waiting list that requires geolocation


https://developer.android.com/develop/sensors-and-location/location/permissions
 */

/**
 * The EventDetailsFragment is responsible for displaying the details of an event.
 * It allows users to join or leave the event waiting list.
 * This fragment retrieves event data from Firestore and handles user profile validation for joining the event.
 * If the user profile is incomplete (missing name or email), it prompts the user to edit their profile before
 * proceeding.
 * The fragment also handles the logic for showing or hiding the join/leave buttons based on the user's
 * current status in the event's entrants list.
 * If the event requires geolocation for joining, the fragment checks whether the user has granted the necessary location permissions.
 * If permission is granted, the user's location is retrieved and stored in Firestore.
 * If permission is denied, the user is prompted to grant the required permission.
 * If the event requires geolocation, the user will receive a warning before joining the event.
 */
public class EventDetailsFragment extends Fragment{

    private EventDetailsBinding binding;
    private TextView name, fee, description, date, facility, location,
            geolocation, period, waitingListCapacity, eventCapacity;
    private ImageView bannerImage;
    private String bannerUri, uniqueID, eventID;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private Button joinButton, leaveButton;
    private Event currentEvent;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    /**
     * @author Tina, Jasleen
     * Inflates the fragment's view and sets up the UI components.
     * It also fetches event data from Firestore and checks the user's status in the event's entrants list.
     */
     public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = EventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        initializeViews();

         // Retrieve event ID from arguments
         if (getArguments() != null) {
             eventID = getArguments().getString("EVENT_ID");
             if (eventID != null) {
                 loadEventDetails();
             }
         }

         requestPermissionLauncher = registerForActivityResult(
                 new ActivityResultContracts.RequestPermission(),
                 isGranted -> {
                     if (isGranted) fetchUserLocation();
                     else Toast.makeText(requireContext(), "Location permission is required to join.", Toast.LENGTH_SHORT).show();
                 }
         );

         joinButton.setOnClickListener(view -> {
             if (currentEvent == null) {
                 Toast.makeText(getContext(), "Event details not available.", Toast.LENGTH_SHORT).show();
                 return;
             }
             handleJoinButtonClick();
             updateJoinButtonVisibility();
         });

        leaveButton.setOnClickListener(view -> {
            leaveEvent();
            updateJoinButtonVisibility();
        });

        return root;
    }

    /**
     * @author Tina
     * Update the buttons visibility on resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (eventID != null) {
            updateJoinButtonVisibility();
        }
    }

    /**
     * @author Tina
     * Loads the event details by creating a new event - current event,
     * and calling the display details and button visibility methods.
     */
    private void loadEventDetails() {
        DocumentReference eventRef = eventsRef.document(eventID);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    currentEvent = new Event(
                            document.getString("eventTitle"),
                            document.getString("facilityID"),
                            document.getDate("eventDate"),
                            document.getDate("registrationDateDeadline"),
                            document.getDate("registrationStartDate"),
                            document.getBoolean("geoLocation"),
                            Objects.requireNonNull(document.getLong("eventCapacity")).intValue()
                    );
                    displayDetails(document);
                    updateJoinButtonVisibility();
                }
            }
        });
    }

    /**
     * @author Tina
     * Updates join button visibility based on current date and user status.
     */
    private void updateJoinButtonVisibility() {
        leaveButton.setVisibility(View.GONE);
        Date currentDate = new Date();

        // Check if the event is within the registration window
        if (currentEvent != null && currentEvent.getRegistrationStartDate().before(currentDate)
                && currentEvent.getRegistrationDateDeadline().after(currentDate)) {

            // Check if the user is part of any special lists (cancelled, accepted, registered)
            db.collection("events").document(eventID)
                    .collection("entrantsList").document(uniqueID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            DocumentSnapshot entrantDoc = task.getResult();
                            boolean isWaitlisted = entrantDoc.getBoolean("onWaitingList") != null && Boolean.TRUE.equals(entrantDoc.getBoolean("onWaitingList"));
                            boolean isCancelled = entrantDoc.getBoolean("onCancelledList") != null && Boolean.TRUE.equals(entrantDoc.getBoolean("onCancelledList"));
                            boolean isAccepted = entrantDoc.getBoolean("onAcceptedList") != null && Boolean.TRUE.equals(entrantDoc.getBoolean("onAcceptedList"));
                            boolean isRegistered = entrantDoc.getBoolean("onRegisteredList") != null && Boolean.TRUE.equals(entrantDoc.getBoolean("onRegisteredList"));

                            // If the user is on any of these lists, don't show the join button
                            if (isCancelled || isAccepted || isRegistered) {
                                if (isAdded()) { // Ensure fragment is still attached
                                    joinButton.setVisibility(View.GONE);
                                    if (isWaitlisted) {
                                        leaveButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                // If the user is not in any list, show the join button
                                if (isAdded()) {
                                    joinButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

        } else {
            // Hide the join button if the registration window is closed
            if (isAdded()) {
                joinButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @author Tina, Jasleen
     * Leaves the event waiting list by deleting the user's entrant record.
     * Gives user a warning and requires confirmation prior to leaving.
     */
    private void leaveEvent() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Leave Event")
                .setMessage("Are you sure you want to leave? Rejoining is not guaranteed.")
                .setPositiveButton("Yes", (dialog, which) -> db.collection("events")
                        .document(eventID).collection("entrantsList")
                        .document(uniqueID).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Left event.", Toast.LENGTH_SHORT).show();
                        }))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * @author Tina, Jasleen
     * Joins the event by adding the user to the entrants list.
     */
    private void joinEvent() {
        // Start by getting user profile
        db.collection("userProfiles").document(uniqueID).get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                        throw new Exception("User profile does not exist or failed to fetch.");
                    }
                    // Create the entrant object from the user profile
                    DocumentSnapshot profileDoc = task.getResult();
                    Entrant entrant = new Entrant(uniqueID);
                    entrant.setName(profileDoc.getString("name"));
                    entrant.setEmail(profileDoc.getString("email"));
                    entrant.setOnWaitingList(true);

                    // Now proceed to set the entrant data in the event
                    return db.collection("events").document(eventID)
                            .collection("entrantsList")
                            .document(uniqueID)
                            .set(entrant.toMap(), SetOptions.merge());
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully joined the event
                        if (isAdded()) { // Check if the fragment is still attached
                            Toast.makeText(getContext(), "Successfully joined!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle failure
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error joining event: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Log.e("EventDetailsFragment", "Error joining event", task.getException());
                    }
                });
    }

    /**
     * Initializes the UI views from the fragment's layout.
     */
    private void initializeViews() {
        name = binding.eventName;
        description = binding.eventDescription;
        fee = binding.fee;
        date = binding.eventDate;
        facility = binding.facilityName;
        location = binding.eventLocation;
        period = binding.registrationPeriod;
        geolocation = binding.geolocationStatus;
        bannerImage = binding.bannerImage;
        waitingListCapacity = binding.waitingListCapacity;
        eventCapacity = binding.eventCapacity;
        joinButton = binding.joinButton;
        leaveButton = binding.leaveButton;
    }

    /**
     * @author Tina
     * Displays the event details by populating the UI views with data from the Firestore document.
     * @param document The Firestore document containing the event data.
     */
    private void displayDetails(DocumentSnapshot document) {
        name.setText(document.getString("eventTitle"));
        description.setText(document.getString("description"));

        // Format and display event dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM dd yyyy hh:mm a", Locale.getDefault());
        date.setText(dateFormat.format(document.getDate("eventDate")));
        String registrationPeriod = "Registration Period: " + dateFormat.format(document.getDate("registrationStartDate")) +
                " - " + dateFormat.format(document.getDate("registrationDateDeadline"));
        period.setText(registrationPeriod);

        String EC = "Event Capacity: " + document.get("eventCapacity");
        eventCapacity.setText(EC);

        String Geo = "Geolocation required: " + document.getBoolean("geoLocation");
        geolocation.setText(Geo);

        // Optional fields
        if (document.get("fee") != null) {
            fee.setVisibility(View.VISIBLE);
            String f = "Fee: " + document.get("fee");
            fee.setText(f);
        }
        if (document.get("limited") != null) {
            waitingListCapacity.setVisibility(View.VISIBLE);
            String WLC = "Waiting List Capacity: " + document.get("limited");
            waitingListCapacity.setText(WLC);
        }
        if (document.get("bannerUri") != null) {
            bannerUri = document.getString("bannerUri");
            loadImageFromUrl(bannerUri);
        }

        // Fetch facility details
        String facilityID = document.getString("facilityID");
        if (facilityID != null) {
            db.collection("facilities").document(facilityID).get()
                    .addOnSuccessListener(facilityDoc -> {
                        location.setText(facilityDoc.getString("address"));
                        facility.setText(facilityDoc.getString("name"));
                    });
        }
    }

    /**
     * @author Tina
     * Loads an image from a URL and displays it in the bannerImage.
     * @param imageUrl The URL of the image to be loaded.
     */
    private void loadImageFromUrl(String imageUrl) {
        if (isAdded()) {
            // Proceed with image loading
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(bannerImage);
        }
    }

    /**
     * @author Tina
     * Handles the profile required check when join is clicked.
     * If user has a profile, handles event geolocation:
     * - required -> warning -> handle geolocation requirement.
     * - not required -> join event.
     */
    private void handleJoinButtonClick() {
        db.collection("userProfiles").document(uniqueID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot profileDoc = task.getResult();
                        String name = profileDoc.getString("name");
                        String email = profileDoc.getString("email");

                        if (name == null || email == null) {
                            // Send the user to create their profile if it does not exist.
                            Toast.makeText(getContext(), "Name and email required! Edit your profile and try again.", Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(this).navigate(R.id.action_eventDetailsFragment_to_nav_edit_profile);
                        } else if (currentEvent.getGeoLocation()) {
                            // Warn the user if geolocation is required for this event.
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Warning! Geolocation Required")
                                    .setMessage("Are you sure you want to join the waiting list?")
                                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                    .setPositiveButton("Proceed", (dialog, which) -> handleGeolocationRequirement())
                                    .show();
                        } else {
                            joinEvent();
                        }
                    }
                });
    }

    /**
     * Fetches the user's location and saves it if permission is granted.
     * @author Tina
     */
    private void fetchUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    saveUserLocationToEntrants(location.getLatitude(), location.getLongitude());
                }
            });
        }
    }

    /**
     * Saves the user's location to the entrants list.
     * @author Tina
     * @param latitude The user's latitude.
     * @param longitude The user's longitude.
     */
    private void saveUserLocationToEntrants(double latitude, double longitude) {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);

        // Merge the user's location to their user file.
        db.collection("events").document(eventID).collection("entrantsList")
                .document(uniqueID).set(locationData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> joinEvent())
                .addOnFailureListener(e -> {
                    Log.e("EventDetailsFragment", "Failed to save location", e);
                    Toast.makeText(getContext(), "Failed to save location. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Handles geolocation requirement for events.
     * @author Tina
     */
    private void handleGeolocationRequirement() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchUserLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

