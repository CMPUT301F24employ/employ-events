package com.example.employ_events.ui.notifications;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.employ_events.databinding.FragmentSendNotificationScreenBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * A Fragment for sending notifications. It retrieves the event ID (if available) from the
 * arguments to display relevant notification details for the entrants of the event.
 */
public class SendNotificationScreen extends Fragment {

    private FragmentSendNotificationScreenBinding binding;
    private Button sendInvitationButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Inflates the fragment's view and retrieves the event ID from the arguments if available.
     * Sets up an OnClickListener for the "Send Invitation" button to send notifications
     * to entrants of the specified event.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendNotificationScreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sendInvitationButton = binding.sendInvitationButton;

        // Check if the event ID was passed as an argument to the fragment
        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            if (eventId != null) {

                // Set an OnClickListener for the "Send Invitation" button
                sendInvitationButton.setOnClickListener(view -> {
                    // Fetch the list of entrants for the event from Firestore
                    db.collection("events").document(eventId).collection("entrantsList").get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            // Access data from each document in 'entrantsList'
                                            String entrantId = document.getId();
                                            Notification notification = new Notification(eventId, "You suck", false, "organizer_notification_channel");
                                            notification.sendNotification(this.getContext());
                                            addNotification(entrantId, notification);
                                        }
                                    } else {
                                        System.out.println("No entrants found in the subcollection.");
                                    }
                                } else {
                                    System.err.println("Error fetching entrants: " + task.getException());
                                }
                            });
                });
            }
        }

        return root;
    }

    /**
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
}
