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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private EventDetailsBinding binding;
    private TextView name, fee, description, date, facility, location,
            geolocation, period, waitingListCapacity, eventCapacity;
    private ImageView bannerImage;
    private String bannerUri, uniqueID;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private Button joinButton, leaveButton;
    private Event currentEvent;

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

                            db.collection("events").document(eventID).collection("entrantsList")
                                    .whereEqualTo("uniqueID", uniqueID)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task1.getResult();
                                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                leaveButton.setVisibility(View.VISIBLE);
                                            } else {
                                                joinButton.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            // Handle the error
                                            Log.w("Firestore", "Error getting documents.", task.getException());
                                        }
                                    });
                        }
                    }
                });
            }
        }



        joinButton.setOnClickListener(view -> {
            if (currentEvent == null) {
                Toast.makeText(getContext(), "Event details not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Warn user if geolocation is required.
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
                    .setPositiveButton("Proceed", (dialog, which) -> leaveEvent(currentEvent))
                    .show();
        });
        return root;
    }
    private void leaveEvent(Event currentEvent) {
            Toast.makeText(getContext(), "You have left the waiting list.", Toast.LENGTH_SHORT).show();
            String eventID = getArguments().getString("EVENT_ID");
            eventsRef.document(eventID).collection("entrantsList").document(uniqueID).delete();
            leaveButton.setVisibility(View.GONE);
    }

    private void joinEvent(Event currentEvent) {
        Entrant entrant = new Entrant();
        entrant.setOnWaitingList(false);
        entrant.setOnAcceptedList(Boolean.FALSE);
        entrant.setOnCancelledList(Boolean.FALSE);
        entrant.setOnRegisteredList(false);
        entrant.setUniqueID(uniqueID);

        if (currentEvent.addEntrant(entrant)) {
            entrant.setOnWaitingList(Boolean.TRUE);
            Toast.makeText(getContext(), "You have successfully joined the event!", Toast.LENGTH_SHORT).show();
            String eventID = getArguments().getString("EVENT_ID");
            eventsRef.document(eventID).collection("entrantsList").document(uniqueID).set(entrant);
            leaveButton.setVisibility(View.VISIBLE);
            joinButton.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "Sorry, waiting list is full", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        name = binding.eventName;
        description = binding.eventDescription;
        fee = binding.fee;
        date = binding.eventDate;
        facility = binding.faciltyName; // do this and location separately
        location = binding.eventLocation;
        period = binding.registrationPeriod;
        geolocation = binding.geolocationStatus;
        bannerImage = binding.bannerImage;
        waitingListCapacity = binding.waitingListCapacity;
        eventCapacity = binding.eventCapacity;
        joinButton = binding.joinButton;
        leaveButton = binding.leaveButton;
    }

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
        facility.setText(facilityID);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), facility::setText);

        db.collection("facilities").document(Objects.requireNonNull(facilityID))
        .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document1 = task.getResult();
                if (document1 != null && document1.exists()) {
                    String location_ = document1.getString("address");
                    location.setText(location_);
                    galleryViewModel.getText().observe(getViewLifecycleOwner(), location::setText);

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

