package com.example.employ_events.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentManageEventEntrantsBinding;
import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.entrants.EntrantsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageEventEntrants extends Fragment {

    private FragmentManageEventEntrantsBinding binding;
    private FirebaseFirestore db;
    private Button sendNotification, sampleEntrants, removeEntrant, viewEntrantMap;
    private String eventId;
    private RecyclerView entrantsList;
    private EntrantsAdapter entrantsAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageEventEntrantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
        }

        if (eventId != null) {
            // Fetch the entrants for the event
            fetchEntrants(eventId);
            // Button functionality
            sendNotification.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putString("EVENT_ID", eventId);
                NavHostFragment.findNavController(ManageEventEntrants.this)
                        .navigate(R.id.action_manageEventEntrantsFragment_to_sendNotificationsScreen, args);
            });
        }


        sampleEntrants.setOnClickListener(v -> {
            if (eventId != null) {
                fetchEventAndGenerateSample(eventId);
            } else {
                Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void initializeViews() {
        sendNotification = binding.sendNotification;
        sampleEntrants = binding.sampleEntrants;
        removeEntrant = binding.removeEntrant;
        viewEntrantMap = binding.viewEntrantMap;
        entrantsList = binding.entrantsList;
        tabLayout = binding.tabLayout;

        // Initialize the adapter with an empty list for now
        entrantsAdapter = new EntrantsAdapter(getContext(), new ArrayList<>());
        entrantsList.setLayoutManager(new LinearLayoutManager(getContext()));
        entrantsList.setAdapter(entrantsAdapter);
    }

    private void fetchEntrants(String eventId) {
        db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Entrant> entrants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getId();
                            Entrant entrant = document.toObject(Entrant.class);

                            db.collection("userProfiles").document(userId)
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            entrant.setName(userDocument.getString("name"));
                                            entrant.setEmail(userDocument.getString("email"));
                                            entrants.add(entrant);

                                            if (entrants.size() == queryDocumentSnapshots.size()) {
                                                entrantsAdapter.updateEntrantsList(entrants);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ManageEventEntrants", "Error fetching user profile: " + e.getMessage());
                                        Toast.makeText(getContext(), "Error fetching some profiles", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "No entrants found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching entrants: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void fetchEventAndGenerateSample(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    event.generateSample();
                    Boolean isSampleSuccessful = Boolean.FALSE;
                    if (event.getEntrantsList().size() == (Math.min(event.getEventCapacity(), event.getEntrantsList().size()))){
                        isSampleSuccessful = Boolean.TRUE;
                    }
                    if (isSampleSuccessful) {
                        Toast.makeText(getContext(), "Sampling successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Sampling unsuccessful :(", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
