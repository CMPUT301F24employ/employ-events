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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsible for managing event entrants. This includes displaying entrants,
 * filtering based on status, and generating sample data for event entrants.
 */
public class ManageEventEntrants extends Fragment {

    private FragmentManageEventEntrantsBinding binding;
    private FirebaseFirestore db;
    private Button sendNotification, sampleEntrants, removeEntrant, viewEntrantMap;
    private String eventId;
    private RecyclerView entrantsList;
    private EntrantsAdapter entrantsAdapter;
    private TabLayout tabLayout;
    private List<Entrant> allEntrants = new ArrayList<>();

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

        setupTabLayout();

        return root;
    }

    /**
     * Initializes the view elements.
     */
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

    /**
     * Fetches the entrants for a given event and populates the entrant list.
     * @param eventId The unique identifier of the event
     */
    private void fetchEntrants(String eventId) {
        db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Entrant> tempEntrants = new ArrayList<>(); // Temporarily store entrants

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getId();
                            Entrant entrant = document.toObject(Entrant.class);
                            entrant.setUniqueID(userId);

                            // Fetch user profile for name and email
                            db.collection("userProfiles").document(userId)
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            entrant.setName(userDocument.getString("name"));
                                            entrant.setEmail(userDocument.getString("email"));
                                        }
                                        tempEntrants.add(entrant);

                                        // When all entrants are processed, assign to allEntrants and filter
                                        if (tempEntrants.size() == queryDocumentSnapshots.size()) {
                                            allEntrants = new ArrayList<>(tempEntrants);
                                            filterEntrantsByTab(tabLayout.getSelectedTabPosition());
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

    /**
     * Fetches event data and generates a sample list of entrants based on event capacity.
     * @param eventId The unique identifier of the event
     */
    private void fetchEventAndGenerateSample(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    event.setEntrantsList((ArrayList<Entrant>) allEntrants);
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
        binding = null;
    }
}
