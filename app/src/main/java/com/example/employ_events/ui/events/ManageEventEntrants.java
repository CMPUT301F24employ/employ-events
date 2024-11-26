package com.example.employ_events.ui.events;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

/*
Authors: Tina, Aasvi, Sahara.

The purpose of this fragment is to allow the organizer to view a list of their events
waitlisted, selected, registered and canceled entrants. It allows them to press the sample
button which will automatically sample from eligible entrants up until the event capacity.
It also allows them to press cancel entrants which will remove entrants that are invited but
have not registered (accepted their invite). Pressing sample again also allows for resample to
fill remaining spots. Pressing view entrants map leads to map fragment. Pressing send notifications
leads to notifications fragment.

US 02.05.02	As an organizer I want to set the system to sample a specified number of attendees to register for the event.
US 02.06.01	As an organizer I want to view a list of all chosen entrants who are invited to apply.
US 02.06.02	As an organizer I want to receive a list of all the cancelled entrants.
US 02.06.03	As an organizer I want to see a final list of entrants who enrolled for the event.
US 02.02.01	As an organizer I want to view the list of entrants who joined my event waiting list.
US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event.
US 02.05.03 As an organizer I want to be able to draw a replacement applicant from the pooling system when a previously
selected applicant cancels or rejects the invitation.
US 01.05.01 As an entrant I want another chance to be chosen from the waiting list if a selected user declines an invitation to sign up.
 */

/**
 * Fragment responsible for managing event entrants. This includes displaying entrants,
 * filtering based on status, and generating sample data for event entrants.
 */
public class ManageEventEntrants extends Fragment {

    private FragmentManageEventEntrantsBinding binding;
    private FirebaseFirestore db;
    private Button sendNotification, sampleEntrants, removeEntrants, viewEntrantMap;
    private String eventId;
    private RecyclerView entrantsList;
    private EntrantsAdapter entrantsAdapter;
    private TabLayout tabLayout;
    private List<Entrant> allEntrants = new ArrayList<>();
    private ListenerRegistration entrantsListener;

    /**
     * Inflates the view, initializes variables, fetches entrants and sets up tab layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageEventEntrantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        initializeViews();

        // Retrieve eventId from arguments passed to the fragment
        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
        }

        if (eventId != null) {
            // Fetch the entrants for the event
            fetchEntrants(eventId);

            // Button functionality to navigate to notification screen
            sendNotification.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putString("EVENT_ID", eventId);
                NavHostFragment.findNavController(ManageEventEntrants.this)
                        .navigate(R.id.action_manageEventEntrantsFragment_to_sendNotificationsScreen, args);
            });
        }

        // Sample entrants button functionality
        sampleEntrants.setOnClickListener(v -> {
            if (eventId != null) {
                fetchEventAndGenerateSample(eventId);
            } else {
                Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        viewEntrantMap.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("EVENT_ID", eventId);
            NavHostFragment.findNavController(ManageEventEntrants.this)
                    .navigate(R.id.action_manageEventEntrantsFragment_to_event_entrants_map, args);
        });

        removeEntrants.setOnClickListener(v -> {
            if (eventId != null) {
                fetchEventAndRemoveEntrants(eventId);
            } else {
                Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        setupTabLayout();

        return root;
    }

    /**
     * Initializes the view elements.
     */
    private void initializeViews() {
        sendNotification = binding.sendNotification;
        sampleEntrants = binding.sampleEntrants;
        removeEntrants = binding.removeEntrant;
        viewEntrantMap = binding.viewEntrantMap;
        entrantsList = binding.entrantsList;
        tabLayout = binding.tabLayout;

        // Initialize the adapter with an empty list for now
        entrantsAdapter = new EntrantsAdapter(getContext(), new ArrayList<>());
        entrantsList.setLayoutManager(new LinearLayoutManager(getContext()));
        entrantsList.setAdapter(entrantsAdapter);
    }

    /**
     * Fetches the entrants for a given event and populates the entrant list.
     * @param eventId The unique identifier of the event
     */
    private void fetchEntrants(String eventId) {
        entrantsListener = db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("ManageEventEntrants", "Error listening to entrants: " + error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Entrant> tempEntrants = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String userId = document.getId();
                            Entrant entrant = document.toObject(Entrant.class);
                            entrant.setUniqueID(userId);

                            db.collection("userProfiles").document(userId)
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            entrant.setName(userDocument.getString("name"));
                                            entrant.setEmail(userDocument.getString("email"));
                                        }
                                        tempEntrants.add(entrant);

                                        if (tempEntrants.size() == queryDocumentSnapshots.size()) {
                                            allEntrants = new ArrayList<>(tempEntrants);
                                            filterEntrantsByTab(tabLayout.getSelectedTabPosition());
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("ManageEventEntrants", "Error fetching user profile: " + e.getMessage()));
                        }
                    }
                });
    }

    /**
     * Fetches event data and generates a sample list of entrants based on event capacity.
     * @param eventId The unique identifier of the event
     */
    private void fetchEventAndGenerateSample(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error listening to event: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    // Generate the sample entrants list
                    event.setEntrantsList((ArrayList<Entrant>) allEntrants);
                    event.generateSample(true);

                    boolean isSampleSuccessful = event.getEntrantsList().size() ==
                            Math.min(event.getEventCapacity(), allEntrants.size());

                    if (isSampleSuccessful) {
                        Toast.makeText(getContext(), "Sampling successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Sampling unsuccessful :(", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Removes entrants with "selected" set to true.
     * @param eventId The unique identifier of the event whose entrants need to be updated.
     **/
    private void fetchEventAndRemoveEntrants(String eventId) {
        db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .whereEqualTo("onAcceptedList", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot entrant : querySnapshot.getDocuments()) {
                        entrant.getReference().update(
                                        "onAcceptedList", false,
                                        "onCancelledList", true
                                ).addOnSuccessListener(aVoid ->
                                        Log.d("ManageEventEntrants", "Entrant updated successfully"))
                                .addOnFailureListener(e ->
                                        Log.e("ManageEventEntrants", "Error updating entrant: " + e.getMessage()));
                    }
                }).addOnFailureListener(e ->
                        Log.e("ManageEventEntrants", "Error fetching entrants: " + e.getMessage()));
    }

    /**
     * Filters the entrants list based on the selected tab (status).
     * @param tabPosition The position of the selected tab (0 - Waitlisted, 1 - Selected, 2 - Cancelled, 3 - Registered)
     */
    private void filterEntrantsByTab(int tabPosition) {
        List<Entrant> filteredList = new ArrayList<>();

        String filterStatus;
        switch (tabPosition) {
            case 0:
                filterStatus = "Waitlisted";
                break;
            case 1:
                filterStatus = "Selected";
                break;
            case 2:
                filterStatus = "Cancelled";
                break;
            case 3:
                filterStatus = "Registered";
                break;
            default:
                filterStatus = "";
        }

        // Apply the filter based on entrant status
        for (Entrant entrant : allEntrants) {
            if ((filterStatus.equals("Waitlisted") && entrant.getOnWaitingList()) ||
                    (filterStatus.equals("Selected") && entrant.getOnAcceptedList()) ||
                    (filterStatus.equals("Registered") && entrant.getOnRegisteredList()) ||
                    (filterStatus.equals("Cancelled") && entrant.getOnCancelledList())) {
                filteredList.add(entrant);
            }
        }
        entrantsAdapter.updateEntrantsList(filteredList);
    }

    /**
     * Sets up the TabLayout and its tab selection listener for filtering entrants.
     */
    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterEntrantsByTab(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (entrantsListener != null) {
            entrantsListener.remove();
        }
        binding = null;
    }
}
