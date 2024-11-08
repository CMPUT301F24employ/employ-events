package com.example.employ_events.ui.registeredEvents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.EventDetailsBinding;
import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.events.ManageEventViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Objects;

/*
This fragment is the page where a user can view the details, and is able to join and leave the event.
It displays a dialog warning if geolocation is required, which asks for proceed -> joins, cancel -> does not join.
It sends a user who has yet to set their name and email to the edit profile screen.

Have yet to implement checks that the registration start and end deadline are followed.

US 01.01.01	As an entrant, I want to join the waiting list for a specific event
US 01.01.02	As an entrant, I want to leave the waiting list for a specific event
US 01.08.01	As an entrant, I want to be warned before joining a waiting list that requires geolocation
 */

/**
 * The EventDetailsFragment is responsible for displaying the details of an event, including its title,
 * description, date, capacity, and other relevant information. It allows users to join or leave the event
 * waiting list by interacting with the UI components such as buttons and text views.
 * This fragment retrieves event data from Firestore and handles user profile validation for joining the event.
 * If the user profile is incomplete (missing name or email), it prompts the user to edit their profile before
 * proceeding. It also handles the logic for showing or hiding the join/leave buttons based on the user's current
 * status in the event's entrants list.
 * It also supports geolocation-based warnings if the event requires geolocation for joining.
 */
public class EventDetailsFragment extends Fragment{

    private EventDetailsBinding binding;
    private TextView name, fee, description, date, facility, location,
            geolocation, period, waitingListCapacity, eventCapacity;
    private ImageView bannerImage;
    private String bannerUri, uniqueID;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private Button joinButton, leaveButton;
    private Event currentEvent;


    /**
     * Inflates the fragment's view and sets up the UI components.
     * It also fetches event data from Firestore and checks the user's status in the event's entrants list.
     */
     public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ManageEventViewModel galleryViewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);

        binding = EventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);


        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        initializeViews();

        if (getArguments() != null) {
            String eventID = getArguments().getString("EVENT_ID");
            if (eventID != null) {
                DocumentReference eventRef = db.collection("events").document(eventID);
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
                                    document.getLong("eventCapacity").intValue()
                            );
                            displayDetails(document, galleryViewModel);
                        }
                    }
                });
                checkUserEntryStatus(eventID);
            }
        }

        joinButton.setOnClickListener(view -> {
            if (currentEvent == null) {
                Toast.makeText(getContext(), "Event details not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the required profile info exists; if not, navigate to edit profile.
            DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        if (document.getString("name") == null || document.getString("email") == null) {
                            Toast.makeText(getContext(), "Name and email required! Edit your profile and try again.", Toast.LENGTH_LONG).show();
                            // Navigate to edit profile and exit the join logic
                            NavHostFragment.findNavController(EventDetailsFragment.this)
                                    .navigate(R.id.action_eventDetailsFragment_to_nav_edit_profile);
                            return;  // Exit here to prevent further execution of join logic
                        } else {
                            // Profile is complete, proceed to join with geolocation warning if needed
                            if (currentEvent.getGeoLocation()) {
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Warning! Geolocation Required")
                                        .setMessage("Are you sure you want to join the waiting list?")
                                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                        .setPositiveButton("Proceed", (dialog, which) -> joinEvent(currentEvent))
                                        .show();
                            }
                            else {
                                joinEvent(currentEvent);
                            }
                        }
                    } else {
                        Log.e("EventDetailsFragment", "User profile document does not exist");
                    }
                } else {
                    Log.e("EventDetailsFragment", "Error getting user profile: ", task.getException());
                }
            });


        });

        leaveButton.setOnClickListener(view -> {
            if (currentEvent == null) {
                Toast.makeText(getContext(), "Event details not available", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Warning! Rejoining is not guaranteed.")
                    .setMessage("Are you sure you want to leave the waiting list?")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Proceed", (dialog, which) -> leaveEvent())
                    .show();
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            String eventID = getArguments().getString("EVENT_ID");
            if (eventID != null) {
                checkUserEntryStatus(eventID);
            }
        }
    }

    /**
     * Checks if the current user is already an entrant for the event.
     * Displays the appropriate button based on whether the user has joined or not.
     * If the user is on the cancelled list, both the join and leave buttons are hidden.
     * @param eventID The ID of the event to check for the entrant.
     */
    private void checkUserEntryStatus(String eventID) {
        db.collection("events").document(eventID).collection("entrantsList")
                .document(uniqueID)  // Directly reference the document by uniqueID
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot entrantDoc = task1.getResult();  // Get the document snapshot directly
                        if (entrantDoc != null && entrantDoc.exists()) {  // Check if the document exists
                            // Check if the entrant has been cancelled
                            if (Boolean.TRUE.equals(entrantDoc.getBoolean("onCancelledList"))) {
                                // Cancelled entrants are not allowed to rejoin.
                                joinButton.setVisibility(View.GONE);
                                leaveButton.setVisibility(View.GONE);
                            } else {
                                leaveButton.setVisibility(View.VISIBLE);
                                joinButton.setVisibility(View.GONE);  // Hide join button when the user has already joined
                            }
                        } else {
                            // No entrant found, show the join button
                            joinButton.setVisibility(View.VISIBLE);
                            leaveButton.setVisibility(View.GONE);  // Hide leave button when the user hasn't joined
                        }
                    } else {
                        // Handle the error
                        Log.w("Firestore", "Error getting documents.", task1.getException());
                    }
                });

    }

    /**
     * Leaves the event waiting list by deleting the user's entrant record.
     */
    private void leaveEvent() {
        Toast.makeText(getContext(), "You have left the waiting list.", Toast.LENGTH_SHORT).show();
        String eventID = getArguments().getString("EVENT_ID");
        eventsRef.document(eventID).collection("entrantsList").document(uniqueID).delete();
        leaveButton.setVisibility(View.GONE);
        joinButton.setVisibility(View.VISIBLE);
    }

    /**
     * Joins the event waiting list by adding the user as an entrant.
     * @param currentEvent The current event the user is joining.
     */
    private void joinEvent(Event currentEvent) {
        Entrant entrant = new Entrant();
        entrant.setUniqueID(uniqueID);

        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    entrant.setEmail(document.getString("email"));
                    entrant.setName(document.getString("name"));

                    // Now that entrant is fully populated, proceed with adding to the event
                    if (currentEvent.addEntrant(entrant)) {
                        Toast.makeText(getContext(), "You have successfully joined the event!", Toast.LENGTH_SHORT).show();
                        String eventID = requireArguments().getString("EVENT_ID");
                        eventsRef.document(eventID)
                                .collection("entrantsList")
                                .document(uniqueID)
                                .set(entrant.toMap(), SetOptions.merge());
                        leaveButton.setVisibility(View.VISIBLE);
                        joinButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), "Sorry, waiting list is full", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Handle the error
                Log.e("EventDetailsFragment", "Error getting documents: ", task.getException());
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
        facility = binding.facilityName; // do this and location separately
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
     * Displays the event details by populating the UI views with data from the Firestore document.
     * @param document The Firestore document containing the event data.
     * @param galleryViewModel The ViewModel used for observing live data.
     */
    private void displayDetails(DocumentSnapshot document, ManageEventViewModel galleryViewModel) {
        // Set optional fields to invisible until it is determined to be non-null.
        waitingListCapacity.setVisibility(View.GONE);
        fee.setVisibility(View.GONE);
        bannerImage.setVisibility(View.GONE);

        // Set fields to corresponding views.

        name.setText(Objects.requireNonNull(document.get("eventTitle")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), name::setText);

        // To format dates in user friendly format using month abbreviations and 12 hour clock with am/pm.
        Format formatter = new SimpleDateFormat("MMM-dd-yyyy hh:mm aa");
        String eventDate = formatter.format(document.getDate("eventDate"));
        String regOpenDate = formatter.format(document.getDate("registrationStartDate"));
        String regCloseDate = formatter.format(document.getDate("registrationDateDeadline"));

        date.setText(Objects.requireNonNull(eventDate));
        galleryViewModel.getText().observe(getViewLifecycleOwner(), date::setText);

        description.setText(Objects.requireNonNull(document.get("description")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), description::setText);

        String registrationPeriod = "Registration Period: " + regOpenDate + " - " + regCloseDate;
        period.setText(registrationPeriod);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), period::setText);

        String event_capacity = "Event Capacity: " + Objects.requireNonNull(document.get("eventCapacity"));
        eventCapacity.setText(event_capacity);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), eventCapacity::setText);

        String geoLocation = "Geolocation required: " + Objects.requireNonNull(document.getBoolean("geoLocation"));
        geolocation.setText(geoLocation);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), geolocation::setText);

        // Check the non required fields and set visible if it is not null.

        if (document.get("limited") != null) {
            String waitingList = "Waiting List Capacity: " + Objects.requireNonNull(document.get("limited"));
            waitingListCapacity.setVisibility(View.VISIBLE);
            waitingListCapacity.setText(waitingList);
            galleryViewModel.getText().observe(getViewLifecycleOwner(), waitingListCapacity::setText);
        }

        if (document.get("fee") != null) {
            String fee_ = "Fee: " + Objects.requireNonNull(document.get("fee"));
            fee.setVisibility(View.VISIBLE);
            fee.setText(fee_);
            galleryViewModel.getText().observe(getViewLifecycleOwner(), fee::setText);
        }

        if (document.get("bannerUri") != null) {
            bannerImage.setVisibility(View.VISIBLE);
            bannerUri = Objects.requireNonNull(document.get("bannerUri")).toString();
            loadImageFromUrl(bannerUri);
        }

        String facilityID = document.getString("facilityID");
        // Obtain facility name, pfp, and location and update correlating views.
        db.collection("facilities").document(Objects.requireNonNull(facilityID))
        .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document1 = task.getResult();
                if (document1 != null && document1.exists()) {
                    String location_ = document1.getString("address");
                    location.setText(location_);
                    galleryViewModel.getText().observe(getViewLifecycleOwner(), location::setText);
                    String name = document1.getString("name");
                    facility.setText(name);
                    galleryViewModel.getText().observe(getViewLifecycleOwner(), facility::setText);
                }
            }
        });
    }

    /**
     * Loads an image from a URL and displays it in the ImageView.
     * @param url The URL of the image to be loaded.
     */
    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                requireActivity().runOnUiThread(() -> bannerImage.setImageBitmap(bitmap));
            } catch (IOException e) {
                Log.e("EventDetailsFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

