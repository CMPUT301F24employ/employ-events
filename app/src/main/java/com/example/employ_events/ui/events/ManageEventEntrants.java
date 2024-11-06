package com.example.employ_events.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.employ_events.ui.events.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.Task;
//import com.google.firebase.firestore.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageEventEntrantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
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
                            String userId = document.getId();  // Assuming the document ID is the user ID
                            Entrant entrant = document.toObject(Entrant.class);

                            // Fetch the user's profile (name) using their userID
                            db.collection("userProfiles").document(userId)
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            String name = userDocument.getString("name");
                                            entrant.setName(name);
                                            String email = userDocument.getString("email");
                                            entrant.setEmail(email);

                                            // After setting the name, add the entrant to the list
                                            entrants.add(entrant);

                                            // If this is the last entrant, update the RecyclerView
                                            if (entrants.size() == queryDocumentSnapshots.size()) {
                                                entrantsAdapter.updateEntrantsList(entrants);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ManageEventEntrants", "Error fetching user profile: " + e.getMessage());
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "No entrants found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching entrants: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
