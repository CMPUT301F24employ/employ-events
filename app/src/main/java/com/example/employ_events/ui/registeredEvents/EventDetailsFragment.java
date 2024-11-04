package com.example.employ_events.ui.registeredEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employ_events.R;
import com.example.employ_events.databinding.EventDetailsBinding;
import com.example.employ_events.ui.events.ManageEventViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.employ_events.ui.events.ManageEventViewModel;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private EventDetailsBinding binding;
    private TextView name, fee, description, date, facility, location, geolocation, deadline;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private Button joinButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ManageEventViewModel galleryViewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);

        binding = EventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            //eventsRef = db.collection("waitinglist");
            if (eventId != null) {
                DocumentReference eventRef = db.collection("events").document(eventId);
                eventRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            displayDetails(document, galleryViewModel);
                        }
                    }
                });
            }
        }

        joinButton = binding.getRoot().findViewById(R.id.joinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        return root;
    }

    private void initializeViews() {
        name = binding.eventName;
        description = binding.eventDescription;
        fee = binding.fee;
        date = binding.eventDates;
        facility = binding.faciltyName;
        location = binding.eventLocation;
        deadline = binding.registrationDeadline;
        geolocation = binding.geolocationStatus;
    }

    private void displayDetails(DocumentSnapshot document, ManageEventViewModel galleryViewModel) {
        if (document.get("eventTitle") != null) {
            name.setText(Objects.requireNonNull(document.get("eventTitle")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        }

        if (document.get("eventDate") != null) {
            date.setText(Objects.requireNonNull(document.get("eventDate")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), date::setText);
        }

        if (document.get("registrationDateDeadline") != null) {
            deadline.setText(Objects.requireNonNull(document.get("registrationDateDeadline")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), deadline::setText);
        }

//        if (document.get("registrationDateDeadline") != null) {
//            deadline.setText(Objects.requireNonNull(document.get("registrationDateDeadline")).toString());
//            galleryViewModel.getText().observe(getViewLifecycleOwner(), deadline::setText);
//        }

        if (document.get("geoLocation") != null) {
            geolocation.setText(Objects.requireNonNull(document.get("geoLocation")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), geolocation::setText);
        }

        if (document.get("description") != null) {
            description.setText(Objects.requireNonNull(document.get("description")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), description::setText);
        }

        if (document.get("fee") != null) {
            fee.setText(Objects.requireNonNull(document.get("fee")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), fee::setText);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

