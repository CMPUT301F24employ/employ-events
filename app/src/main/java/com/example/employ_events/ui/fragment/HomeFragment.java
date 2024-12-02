package com.example.employ_events.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
/*
Authors: Tina
Implemented a dashboard like home screen with live updating counts for user stats.
 */

/**
 * Fragment representing the home screen. Dashboard showing user stats such as:
 * - Organizer event count and entrant count
 * - Entrant joined event count and lottery win count
 * - Pending invitations count
 * @author Tina
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private String uniqueID;
    private Button facilityButton, scanQrButton, invitationsButton;
    private TextView eventCreateCount, eventEntrantsCount,
            anEntrantCount, selectedCount, invitationsCount;
    private ListenerRegistration eventCountListener, facilityListener;
    private Map<String, ListenerRegistration> entrantListeners = new HashMap<>();
    private Map<String, Integer> previousEntrantCounts = new HashMap<>(); // To track previous counts
    private int totalEntrantsCount = 0, invitationCount, winCount, joinCount;
    private boolean isTestMode = false; // Flag to determine if we are in test mode

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the 'isTestMode' extra from the Intent passed to MainActivity
        if (getActivity() != null && getActivity().getIntent() != null) {
            isTestMode = getActivity().getIntent().getBooleanExtra("isTestMode", false);
        }
    }

    /**
     * Inflates the fragment layout and sets up event listeners for UI components.
     *
     * @param inflater           the LayoutInflater object to inflate views
     * @param container          the parent view that contains the fragment's UI
     * @param savedInstanceState the saved instance state for restoring fragment state
     * @return the root view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews();

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // You can now use the 'isTestMode' flag
        if (!isTestMode) {
            startListeningForEntrantCount();
            fetchInvitationWinJoinCount();
        }

        facilityButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_facility,
                null,
                new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_home, true)  // This will remove Home from the back stack
                        .build()));

        scanQrButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_nav_home_to_scan_qr_code,
                null,
                new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_home, true)  // This will remove Home from the back stack
                        .build()));

        invitationsButton.setOnClickListener(v -> {
            // Navigate to Facility and clear Home from the back stack (optional)
            Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_invitations,
                    null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_home, true)  // This will remove Home from the back stack
                            .build());
        });

        return root;
    }

    /**
     * Initializes all view components.
     */
    private void initializeViews() {
        facilityButton = binding.manageFacilityButton;
        scanQrButton = binding.scanQrCodeButton;
        invitationsButton = binding.viewInvitationsButton;
        eventCreateCount = binding.eventCreateCount;
        eventEntrantsCount = binding.eventEntrantsCount;
        anEntrantCount = binding.anEntrantCount;
        selectedCount = binding.wonLotteryCount;
        invitationsCount = binding.invitationsCount;
    }

    /**
     * Listens to firebase to update the total event entrants count and event count.
     */
    private void startListeningForEntrantCount() {
        // Only start listening if not in test mode
        if (!isTestMode) {
            // Listen to the Facilities collection to get the facilityId for the organizerId
            facilityListener = db.collection("facilities")
                    .whereEqualTo("organizer_id", uniqueID)
                    .addSnapshotListener((facilitySnapshot, error) -> {
                        if (error != null) {
                            Log.e("FirestoreError", "Error fetching facility data: ", error);
                            return;
                        }

                        if (facilitySnapshot != null && !facilitySnapshot.isEmpty()) {
                            // Get the facilityId
                            String facilityId = facilitySnapshot.getDocuments().get(0).getId();

                            // Listen to the Events collection using the facilityId
                            listenForEvents(facilityId);
                        }
                    });
        }
    }

    /**
     * Listens to firebase to update the total event count.
     * @param facilityId The facilityID associated with the organizer.
     */
    private void listenForEvents(String facilityId) {
        // Listen to the Events collection with the facilityId
        eventCountListener = db.collection("events")
                .whereEqualTo("facilityID", facilityId)
                .addSnapshotListener((eventSnapshot, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error fetching event data: ", error);
                        return;
                    }
                    int eventCount = 0;
                    if (eventSnapshot != null) {
                        eventCount = eventSnapshot.size();
                        for (DocumentSnapshot eventDoc : eventSnapshot.getDocuments()) {
                            if (eventDoc.exists()) {
                                String eventId = eventDoc.getId();
                                listenForEntrants(eventId); // Listen to entrants for this event
                            }
                        }
                    }
                    String eventC = "Total Events Created: " + eventCount;
                    eventCreateCount.setText(eventC);
                });
    }

    /**
     * Listens for entrants in a specific event's entrantsList
     * @param eventId The ID of the specific event.
     */
    private void listenForEntrants(String eventId) {
        // Listen to the entrantsList subcollection of each event
        ListenerRegistration entrantListener = db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .addSnapshotListener((entrantSnapshot, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error fetching entrants for event " + eventId + ": ", error);
                        return;
                    }

                    if (entrantSnapshot != null) {
                        int currentEntrantCount = entrantSnapshot.size();
                        Log.d("EntrantListener", "Current entrants in event " + eventId + ": " + currentEntrantCount);

                        // Check if the entrant count has changed since the last snapshot
                        int previousCount = previousEntrantCounts.getOrDefault(eventId, 0);
                        if (previousCount != currentEntrantCount) {
                            // Update the total entrants count
                            synchronized (this) {
                                totalEntrantsCount += currentEntrantCount - previousCount;
                            }
                            Log.d("EntrantListener", "Updated total entrants count: " + totalEntrantsCount);

                            // Update the UI if the TextView is available
                            if (eventEntrantsCount != null) {
                                String entrantC = "Total Event Entrants: " + totalEntrantsCount;
                                eventEntrantsCount.setText(entrantC);
                            }
                        }

                        // Store the current entrant count for this event
                        previousEntrantCounts.put(eventId, currentEntrantCount);
                    } else {
                        Log.d("EntrantListener", "No entrants found for event " + eventId);
                    }
                });

        // Store the listener so it can be removed later if needed
        entrantListeners.put(eventId, entrantListener);
    }


    /**
     * Listens to and fetches the counts for:
     * - Pending invitations
     * - Events Joined
     * - Lottery Wins
     */
    private void fetchInvitationWinJoinCount() {
        // Only start listening if not in test mode
        if (!isTestMode) {
            // Listen to the 'events' collection
            db.collection("events")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e("FirestoreError", "Error listening to events collection: ", error);
                            return;
                        }

                        if (querySnapshot != null) {
                            // Initialize counts
                            invitationCount = 0;
                            winCount = 0;
                            joinCount = 0;

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                if (document.exists()) {
                                    String eventId = document.getId();

                                    // Ensure eventId is not null before proceeding
                                    if (eventId == null) {
                                        Log.e("FirestoreError", "Event ID is null. Skipping this event.");
                                        continue; // Skip the iteration if eventId is null
                                    }

                                    // Ensure uniqueID is not null before querying
                                    if (uniqueID == null) {
                                        Log.e("FirestoreError", "Unique ID is null. Skipping this check.");
                                        return; // Exit the method if uniqueID is null
                                    }

                                    // Listener for each user's document in the entrantsList subcollection
                                    db.collection("events")
                                            .document(eventId)
                                            .collection("entrantsList")
                                            .document(uniqueID)
                                            .addSnapshotListener((entrantSnapshot, entrantError) -> {
                                                if (entrantError != null) {
                                                    Log.e("FirestoreError", "Error listening to entrantsList: ", entrantError);
                                                    return;
                                                }

                                                if (entrantSnapshot != null && entrantSnapshot.exists()) {
                                                    Boolean onAcceptedList = entrantSnapshot.getBoolean("onAcceptedList");
                                                    Boolean onRegisteredList = entrantSnapshot.getBoolean("onRegisteredList");

                                                    // Logic for incrementing counts based on conditions
                                                    if (onAcceptedList != null && onAcceptedList) {
                                                        invitationCount++;
                                                        winCount++;
                                                    } else if (onRegisteredList != null && onRegisteredList) {
                                                        winCount++;
                                                    }
                                                    joinCount++;
                                                }

                                                // Update the UI with the counts
                                                String invC = "Pending Invitations: " + invitationCount;
                                                invitationsCount.setText(invC);

                                                String winC = "Total Event Lottery Wins: " + winCount;
                                                selectedCount.setText(winC);

                                                String joinC = "Total Events Joined: " + joinCount;
                                                anEntrantCount.setText(joinC);
                                            });
                                }
                            }
                        }
                    });
        }
    }


    /**
     * Cleans up the binding when the fragment's view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove all listeners
        if (eventCountListener != null) {
            eventCountListener.remove();
        }
        if (facilityListener != null) {
            facilityListener.remove();
        }
        for (ListenerRegistration listener : entrantListeners.values()) {
            listener.remove();
        }
        binding = null;
    }
}
