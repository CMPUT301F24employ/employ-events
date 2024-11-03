package com.example.employ_events.ui.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.employ_events.databinding.FragmentManageEventBinding;
import com.example.employ_events.ui.registeredEvents.DetailsViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Objects;

public class ManageEventFragment extends Fragment {
    private FragmentManageEventBinding binding;
    private FirebaseFirestore db;
    private String title, description, bannerUri;
    private Date eventDate, registrationOpenDate, registrationCloseDate;
    private Integer eventCapacity, waitingListCapacity, eventFee;
    private boolean geolocationRequired;
    private Button editEventButton, viewEntrantsButton, qrCodeButton;
    private TextView titleTV, descriptionTV, eventDateTV, registrationPeriodTV,
            eventCapacityTV, waitingListCapacityTV, eventFeeTV, geolocationRequiredTV;
    private ImageView bannerImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DetailsViewModel galleryViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        // Inflate the layout for this fragment
        binding = FragmentManageEventBinding.inflate(inflater, container, false);
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



        return root;
    }

    private void initializeViews() {
        bannerImage = binding.bannerImage;
        titleTV = binding.eventTitle;
        descriptionTV = binding.description;
        eventDateTV = binding.eventDate;
        registrationPeriodTV = binding.registrationPeriod;
        eventCapacityTV = binding.eventCapacity;
        waitingListCapacityTV = binding.waitingListCapacity;
        eventFeeTV = binding.fee;
        geolocationRequiredTV = binding.geolocationStatus;
    }

    private void displayDetails(DocumentSnapshot document, DetailsViewModel galleryViewModel) {
        titleTV.setText(Objects.requireNonNull(document.get("eventTitle")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), titleTV::setText);

        eventDateTV.setText(Objects.requireNonNull(document.get("eventDate")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), eventDateTV::setText);

        descriptionTV.setText(Objects.requireNonNull(document.get("description")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), descriptionTV::setText);

        String registrationPeriod = "Registration Period: " +
                Objects.requireNonNull(document.get("registrationStartDate")).toString() +
                " - " + Objects.requireNonNull(document.get("registrationDateDeadline")).toString();

        registrationPeriodTV.setText(registrationPeriod);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), registrationPeriodTV::setText);

        eventCapacityTV.setText(Objects.requireNonNull(document.get("eventCapacity")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), eventCapacityTV::setText);

        geolocationRequiredTV.setText(Objects.requireNonNull(document.getBoolean("geoLocation")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), eventCapacityTV::setText);

        if (document.get("limited") != null) {
            waitingListCapacityTV.setVisibility(View.VISIBLE);
            waitingListCapacityTV.setText(Objects.requireNonNull(document.get("limited")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), waitingListCapacityTV::setText);
        }

        if (document.get("limited") != null) {
            waitingListCapacityTV.setVisibility(View.VISIBLE);
            waitingListCapacityTV.setText(Objects.requireNonNull(document.get("limited")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), waitingListCapacityTV::setText);
        }

        if (document.get("bannerUri") != null) {
            bannerImage.setVisibility(View.VISIBLE);

        }



    }

}