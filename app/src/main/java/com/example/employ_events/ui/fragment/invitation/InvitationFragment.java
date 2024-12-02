package com.example.employ_events.ui.fragment.invitation;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
Authors: Tina.
Purpose is to allow users to accept or decline an invitation.

US 01.05.02 As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event
US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
 */

/**
 * A fragment that displays some details for a specific event that the user is invited to.
 * It retrieves the event details from Firestore using the event ID passed from the previous fragment.
 * Allows the user to accept or decline the invitation.
 * @author Tina
 */
public class InvitationFragment extends Fragment {

    private TextView eventNameTextView, eventDateTextView;
    private String eventId, uniqueID;
    private FirebaseFirestore db;
    private Button acceptButton, declineButton;

    /**
     * Called to create the view for this fragment. It initializes the UI elements and fetches event details
     * from Firestore based on the event ID passed via the Bundle.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_invitation, container, false);

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the event ID from the Bundle
        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
        }

        // Initialize UI elements
        eventNameTextView = rootView.findViewById(R.id.eventName);
        eventDateTextView = rootView.findViewById(R.id.eventDate);
        acceptButton = rootView.findViewById(R.id.accept_invitation_button);
        declineButton = rootView.findViewById(R.id.decline_invitation_button);

        // Fetch event details from Firestore
        if (eventId != null) {
            fetchEventDetails(eventId);
        } else {
            Log.e("InvitationFragment", "Event ID is null");
        }

        acceptButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Confirm your acceptance to join the event!")
                .setNegativeButton("Return", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Accept", (dialog, which) -> {
                    acceptInvitation(eventId);
                    navigateBackToInvitationsList();

                })
                .show());

        declineButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to decline this invitation?")
                .setNegativeButton("Return", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Decline", (dialog, which) -> {
                    declineInvitation(eventId);
                    navigateBackToInvitationsList();
                })
                .show());


        return rootView;
    }

    /**
     * Fetches the event details from Firestore using the provided event ID.
     * The details fetched include the event name and date.
     * @param eventId The ID of the event to fetch details for.
     */
    private void fetchEventDetails(String eventId) {
        // Fetch the event details from Firestore using the event ID
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String eventName = document.getString("eventTitle");
                            Date eventDate = document.getDate("eventDate"); // Assuming you have event details field

                            // Set the event details on the UI
                            eventNameTextView.setText(eventName);
                            eventDateTextView.setText(eventDate.toString());
                        } else {
                            Log.e("InvitationFragment", "No such document found!");
                        }
                    } else {
                        Log.e("InvitationFragment", "Error getting event details", task.getException());
                    }
                });
    }

    private void acceptInvitation(String eventId) {
        // Reference to the specific entrant document within the 'entrantsList' collection
        db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .document(uniqueID)
                .get()
                .addOnCompleteListener(entrantTask -> {
                    // Check if the task was successful and the document exists
                    if (entrantTask.isSuccessful() && entrantTask.getResult().exists()) {

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("onAcceptedList", false);
                        updates.put("onRegisteredList", true);

                        // Perform the update on Firestore
                        db.collection("events")
                                .document(eventId)
                                .collection("entrantsList")
                                .document(uniqueID)
                                .update(updates)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d("InvitationFragment", "Entrant fields updated successfully.");
                                    } else {
                                        Log.e("InvitationFragment", "Error updating entrant fields", updateTask.getException());
                                    }
                                });
                    } else {
                        // Log if the entrant document does not exist
                        Log.e("InvitationFragment", "Entrant document not found or error retrieving the document.");
                    }
                });

    }

    private void declineInvitation(String eventId) {
        // Reference to the specific entrant document within the 'entrantsList' collection
        db.collection("events")
                .document(eventId)
                .collection("entrantsList")
                .document(uniqueID)
                .get()
                .addOnCompleteListener(entrantTask -> {
                    // Check if the task was successful and the document exists
                    if (entrantTask.isSuccessful() && entrantTask.getResult().exists()) {

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("onAcceptedList", false);
                        updates.put("onCancelledList", true);

                        // Perform the update on Firestore
                        db.collection("events")
                                .document(eventId)
                                .collection("entrantsList")
                                .document(uniqueID)
                                .update(updates)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d("InvitationFragment", "Entrant fields updated successfully.");
                                    } else {
                                        Log.e("InvitationFragment", "Error updating entrant fields", updateTask.getException());
                                    }
                                });
                    } else {
                        // Log if the entrant document does not exist
                        Log.e("InvitationFragment", "Entrant document not found or error retrieving the document.");
                    }
                });

    }

    /**
     * Navigates back to the Invitations List fragment and clears the back stack.
     */
    private void navigateBackToInvitationsList() {
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_invitation_fragment_to_nav_invitations, null,
                            new NavOptions.Builder()
                                    .setPopUpTo(R.id.nav_invitations, true)  // Clear stack
                                    .build());
        }
    }

}
