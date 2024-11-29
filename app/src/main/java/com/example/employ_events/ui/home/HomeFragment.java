package com.example.employ_events.ui.home;

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

import com.example.employ_events.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
/*
Implemented a dashboard like home screen.
Current issues: Meant to have buttons to navigate to respective screens but it bugs out
the nav drawer when trying to press the menu home button.
 */

/**
 * @author Tina
 * Fragment representing the home screen. Dashboard showing user stats such as:
 * - Organizer event count and entrant count
 * - Entrant joined event count and lottery win count
 * - Pending invitations count
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
    private boolean isTestMode = false; // Add flag to determine if we are in test mode


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

        // Check if we are in test mode (could be set in the tests or via a debug flag)
        isTestMode = (getActivity() != null && getActivity().getIntent().getBooleanExtra("isTestMode", false));

        startListeningForEntrantCount();
        fetchInvitationWinJoinCount();

        // Buttons are not functioning as expected, hide them for now.
        facilityButton.setVisibility(View.GONE);
        scanQrButton.setVisibility(View.GONE);
        invitationsButton.setVisibility(View.GONE);

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
                            String eventId = eventDoc.getId();
                            listenForEntrants(eventId); // Listen to entrants for this event
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
                        Log.e("FirestoreError", "Error fetching entrants for event: ", error);
                        return;
                    }

                    if (entrantSnapshot != null) {
                        int currentEntrantCount = entrantSnapshot.size();
                        Log.d("EntrantListener", "Current entrants in event " + eventId + ": " + currentEntrantCount);

                        // Check if the entrant count has changed since the last snapshot
                        if (!previousEntrantCounts.containsKey(eventId) || previousEntrantCounts.get(eventId) != currentEntrantCount) {
                            // If the count has changed, update the total entrants count
                            totalEntrantsCount += currentEntrantCount - (previousEntrantCounts.getOrDefault(eventId, 0));

                            // Log the updated total entrants count
                            Log.d("EntrantListener", "Updated total entrants count: " + totalEntrantsCount);

                            // Update the UI with the new total entrants count
                            String entrantC = "Total Event Entrants: " + totalEntrantsCount;
                            eventEntrantsCount.setText(entrantC);
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
            db.collection("events")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e("FirestoreError", "Error listening to events collection: ", error);
                            return;
                        }

                        if (querySnapshot != null) {
                            // Initialize count to 0 for each snapshot
                            invitationCount = 0;
                            winCount = 0;
                            joinCount = 0;
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String eventId = document.getId();

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
                                                if (onAcceptedList != null && onAcceptedList) {
                                                    invitationCount++;
                                                    winCount++;
                                                }
                                                else if (onRegisteredList != null && onRegisteredList) {
                                                    winCount++;
                                                }
                                                joinCount++;
                                            }

                                            // Update the textviews.
                                            String invC = "Pending Invitations: " + invitationCount;
                                            invitationsCount.setText(invC);

                                            String winC = "Total Event Lottery Wins: " + winCount;
                                            selectedCount.setText(winC);

                                            String joinC = "Total Events Joined: " + joinCount;
                                            anEntrantCount.setText(joinC);

                                        });
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
