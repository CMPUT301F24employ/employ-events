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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private EventDetailsBinding binding;
    private TextView name, fee, description;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private Button joinButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DetailsViewModel galleryViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        binding = EventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        assert getArguments() != null;
        String eventId = getArguments().getString("EVENT_ID");
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("waitinglist");


        initializeViews();

        assert eventId != null;
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    displayDetails(document, galleryViewModel);
                }
            }
        });


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
    }

    private void displayDetails(DocumentSnapshot document, DetailsViewModel galleryViewModel) {
        if (document.get("eventTitle") != null) {
            name.setText(Objects.requireNonNull(document.get("name")).toString());
            galleryViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

