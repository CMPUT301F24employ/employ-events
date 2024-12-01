package com.example.employ_events.ui.invitation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

/*
Authors: Tina
Allows users to view all their invitations, prevents user not being able
to accept / decline if deleted notification.
 */

/**
 * @author Tina
 * Fragment to display a list of invitations the user has accepted.
 * It fetches event data from Firestore, filters the invitations based on the user's status,
 * and binds the filtered list to a RecyclerView using an adapter.
 */
public class InvitationsListFragment extends Fragment {

    private RecyclerView recyclerView;
    private InvitationsAdapter adapter;
    private ArrayList<EventItem> invitationsList;
    private FirebaseFirestore db;
    private String uniqueID;

    /**
     * Called to create the view for this fragment. It initializes the RecyclerView,
     * fetches invitations from Firestore, and sets up the InvitationsAdapter.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitations_list, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // Fetch the filtered list of invitations
        invitationsList = new ArrayList<>();
        fetchSelectedInvitations();

        // Set up the InvitationsAdapter with the click listener
        adapter = new InvitationsAdapter(getContext(), invitationsList, event -> {
            if (event != null) {
                // Make sure event is not null
                Log.d("InvitationsListFragment", "Event ID: " + event.getEventId());

                // Pass the event ID to the next fragment
                Bundle bundle = new Bundle();
                bundle.putString("EVENT_ID", event.getEventId());
                Navigation.findNavController(rootView).navigate(R.id.action_nav_invitations_to_invitation_fragment, bundle);
            } else {
                Log.e("InvitationsListFragment", "Event is null");
            }
        });

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    /**
     * Fetches the list of accepted invitations for the current user from Firestore.
     * It queries the "events" collection and checks the "entrantsList" collection for the user
     * with the uniqueID to find accepted invitations.
     * Once found, the event name is added to the invitations list, and the adapter is notified to update the UI.
     */
    private void fetchSelectedInvitations() {
        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error listening to events collection: ", error);
                        return;
                    }

                    if (querySnapshot != null) {
                        invitationsList.clear();  // Clear the list to prevent duplicate entries
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            if (document.exists()) {
                                String eventId = document.getId();
                                String eventName = document.getString("eventTitle");

                                // Set up a listener for each user's document in the entrantsList subcollection
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
                                                if (onAcceptedList != null && onAcceptedList) {
                                                    // Add to list only if not already present
                                                    EventItem eventItem = new EventItem(eventId, eventName);
                                                    if (!invitationsList.contains(eventItem)) {
                                                        invitationsList.add(eventItem);
                                                        adapter.notifyDataSetChanged();  // Update the adapter
                                                    }
                                                } else {
                                                    // Remove if no longer on accepted list
                                                    invitationsList.removeIf(event -> event.getEventId().equals(eventId));
                                                    adapter.notifyDataSetChanged();  // Update the adapter
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
