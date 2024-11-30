package com.example.employ_events.ui.events;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import com.example.employ_events.ui.notifications.Notification;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
 */

/**
 * @author Tina, Sahara, Aasvi
 * Fragment responsible for managing event entrants. This includes displaying entrants,
 * filtering based on status, and generating sample data for event entrants.
 */
public class ManageEventEntrants extends Fragment {

    private FragmentManageEventEntrantsBinding binding;
    private FirebaseFirestore db;
    private Button sendNotification, sampleEntrants, removeEntrants, viewEntrantMap, removeAnEntrant;
    private String eventId, eventCapacity, eventName;
    private RecyclerView entrantsList;
    private EntrantsAdapter entrantsAdapter;
    private TabLayout tabLayout;
    private List<Entrant> allEntrants = new ArrayList<>();
    private ListenerRegistration entrantsListener, entrantListener;
    private TextView selectedRegisteredCount;

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

        EntrantsAdapter.OnItemClickListener listener = new EntrantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String entrantUniqueID) {
                Log.d("EntrantsFragment", "Entrant clicked with ID: " + entrantUniqueID);

                // When an entrant is clicked on the list and remove an entrant button is clicked:
                // Prompt the user with a confirmation if they would like to remove this entrant.
                removeAnEntrant.setOnClickListener(v -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Remove Entrant")
                            .setMessage("Are you sure you want to remove this entrant?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                deleteAnEntrant(entrantUniqueID);
                                entrantsAdapter.notifyDataSetChanged();
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            }
        };

        // Initialize the adapter with an empty list for now
        entrantsAdapter = new EntrantsAdapter(getContext(), new ArrayList<>(), listener);
        entrantsList.setLayoutManager(new LinearLayoutManager(getContext()));
        entrantsList.setAdapter(entrantsAdapter);


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



        // Hide the view entrants map button for non geolocation events.
        updateViewEntrantsMapVisibility();

        // Initializes the entrant count.
        updateCounts();

        // Sample entrants button functionality.
        sampleEntrants.setOnClickListener(v -> {
            if (eventId != null) {
                fetchEventAndGenerateSample(eventId);
                updateCounts();
                sendLotteryNotification();
            } else {
                Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // View entrants map functionality
        viewEntrantMap.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("EVENT_ID", eventId);
            NavHostFragment.findNavController(ManageEventEntrants.this)
                    .navigate(R.id.action_manageEventEntrantsFragment_to_event_entrants_map, args);
        });

        // Remove unregistered entrants functionality.
        removeEntrants.setOnClickListener(v -> {
            if (eventId != null) {
                fetchEventAndRemoveEntrants(eventId);
                updateCounts();
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
        removeEntrants = binding.removeEntrants;
        viewEntrantMap = binding.viewEntrantMap;
        entrantsList = binding.entrantsList;
        tabLayout = binding.tabLayout;
        selectedRegisteredCount = binding.selectedRegisteredCount;
        removeAnEntrant = binding.removeEntrant;
    }

    /**
     * Cancels an individual entrant.
     * @param entrantUniqueID The unique ID of the entrant to be removed.
     */
    private void deleteAnEntrant(String entrantUniqueID) {
        db.collection("events").document(eventId).collection("entrantsList").document(entrantUniqueID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("onCancelledList", true);
                            data.put("onWaitingList", false);
                            data.put("onRegisteredList", false);
                            data.put("onAcceptedList", false);
                            db.collection("events").document(eventId).collection("entrantsList")
                                    .document(entrantUniqueID).set(data, SetOptions.merge());
                        }
                    }
                });
    }

    private void updateCounts() {
        entrantListener = db.collection("events").document(eventId)
                .collection("entrantsList")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("LiveCounts", "Error listening for changes", error);
                        return;
                    }

                    if (snapshots != null) {
                        int acceptedRegisteredCount = 0;

                        for (DocumentSnapshot entrantDoc : snapshots.getDocuments()) {
                            // Increase count if entrant is selected or registered.
                            if (Boolean.TRUE.equals(entrantDoc.getBoolean("onAcceptedList")) ||
                                    Boolean.TRUE.equals(entrantDoc.getBoolean("onRegisteredList"))) {
                                acceptedRegisteredCount++;
                            }
                        }

                        // Update UI with live counts
                        if (isAdded()) {
                            String count = (acceptedRegisteredCount) + " Entrants / " + eventCapacity + " Entrant Capacity.";
                            selectedRegisteredCount.setText(count);
                        }
                    }
                });
    }

    /**
     * @author Tina
     * Checks the events geolocation requirement and hides the entrants map button if not required.
     */
    private void updateViewEntrantsMapVisibility() {
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            boolean geolocation = Boolean.TRUE.equals(document.getBoolean("geoLocation"));
                            if (!geolocation) {
                                viewEntrantMap.setVisibility(View.GONE);
                            }
                            eventName = document.getString("eventTitle");
                            eventCapacity = document.get("eventCapacity").toString();
                        }
                    }
        });
    }

    /**
     * @author Tina
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
     * @author Sahara, Tina
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
     * @author Tina
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
     * @author Tina, Aasvi
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
     * @author Tina, Aasvi
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

    /**
     * @author Sahara
     * Adds a notification to the user's profile in Firestore.
     *
     * @param userID     The ID of the user to add the notification to
     * @param notification The Notification object containing the message to be saved
     */
    private void addNotification(String userID, Notification notification) {
        db.collection("userProfiles")
                .document(userID)
                .collection("Notifications")
                .add(new HashMap<String, Object>() {{
                    put("Notification", notification);
                }})
                .addOnSuccessListener(aVoid ->
                        System.out.println("Document successfully written!")
                )
                .addOnFailureListener(e ->
                        System.err.println("Error writing document: " + e.getMessage())
                );
    }

    /**
     * @author Tina
     * Sends a notification to lottery "winners" and "losers" :
     * US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
     * US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
     */
    private void sendLotteryNotification() {
        db.collection("events").document(eventId).collection("entrantsList").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Access data from each document in 'entrantsList'
                                String entrantId = document.getId();
                                boolean isSelected = Boolean.TRUE.equals(document.getBoolean("onSelectedList"));
                                boolean notSelected = Boolean.TRUE.equals(document.getBoolean("onWaitingList"));
                                if (isSelected) {
                                    String message = "Congratulations, you have won the event lottery for " + eventName + "! Accept your invitation to secure your spot!";
                                    Notification notification = new Notification(eventId, message, false, "app_notification_channel");
                                    notification.sendNotification(this.getContext());
                                    addNotification(entrantId, notification);
                                }
                                else if (notSelected){
                                    String message = "Sorry, you didn't win the event lottery for " + eventName + ". You will automatically remain in the waiting list for another chance of being chosen!";
                                    Notification notification = new Notification(eventId, message, false, "app_notification_channel");
                                    notification.sendNotification(this.getContext());
                                    addNotification(entrantId, notification);
                                }

                            }
                        } else {
                            System.out.println("No entrants found in the subcollection.");
                        }
                    } else {
                        System.err.println("Error fetching entrants: " + task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (entrantsListener != null) {
            entrantsListener.remove();
        }
        if (entrantListener != null) {
            entrantListener.remove();
        }
        binding = null;
    }
}
