package com.example.employ_events.ui.notifications;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentSendNotificationScreenBinding;
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.profile.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * A Fragment for sending notifications, which retrieves an event ID (if available)
 * from the arguments to display relevant notification details.
 */
public class SendNotificationScreen extends Fragment {

    private FragmentSendNotificationScreenBinding binding;
    private Button sendInvitationButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Inflates the fragment's view and retrieves the event ID from the arguments if available.
     *
     * @param inflater           LayoutInflater to inflate views in the fragment
     * @param container          ViewGroup containing the fragment's UI
     * @param savedInstanceState Bundle with saved instance data
     * @return the root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendNotificationScreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sendInvitationButton = binding.sendInvitationButton;

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            if (eventId != null) {

                // use the event ID here
                sendInvitationButton.setOnClickListener(view -> {
//                    Event event
                    db.collection("events").document(eventId).collection("entrantsList").get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            // Access data from each document in 'entrantsList'
                                            String entrantId = document.getId();
                                            Notification notification = new Notification(eventId, "You suck", false);
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
                        System.err.println("Error writing document: ")
                );
    }
}