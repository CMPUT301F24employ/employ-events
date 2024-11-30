package com.example.employ_events.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.employ_events.databinding.FragmentSendNotificationScreenBinding;
import com.example.employ_events.ui.notifications.Notification;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SendNotificationScreen extends Fragment {

    private FragmentSendNotificationScreenBinding binding;
    private Button sendInvitationButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendNotificationScreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button confirmButton = binding.confirmButton;
        EditText messageInput = binding.messageInput;
        TabLayout tabLayout = binding.tabLayout;


        // Keep track of the selected tab
        int[] selectedTabPosition = {0}; // Default to the "All" tab

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition[0] = tab.getPosition();
                //System.out.println(selectedTabPosition[0]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            if (eventId != null) {
                // Send notification using custom message entered by the user
                confirmButton.setOnClickListener(view -> {
                    String message = messageInput.getText().toString().trim();

                    if (message.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendNotifications(eventId, message, selectedTabPosition[0]);
                });
            }
        }

        return root;
    }

    private void sendNotifications(String eventId, String message, int tabPosition) {
        String ORGANIZER_CHANNEL_ID = "organizer_notification_channel";
        db.collection("events").document(eventId).collection("entrantsList").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Boolean onWaitingList = document.getBoolean("onWaitingList");
                                Boolean onAcceptedList = document.getBoolean("onAcceptedList");
                                Boolean onCancelledList = document.getBoolean("onCancelledList");
                                Boolean onRegisteredList = document.getBoolean("onRegisteredList");
                                boolean shouldNotify = false;
                                switch (tabPosition) {
                                    case 0:
                                        shouldNotify = true;
                                        break;
                                    case 1: // Waitlisted
                                        shouldNotify = Boolean.TRUE.equals(onWaitingList);
                                        System.out.println("We are fetching the waitList");
                                        break;
                                    case 2: // Selected
                                        shouldNotify = Boolean.TRUE.equals(onAcceptedList);
                                        System.out.println("We are fetching the acceptedList");
                                        break;
                                    case 3: // Cancelled
                                        shouldNotify = Boolean.TRUE.equals(onCancelledList);
                                        System.out.println("We are fetching the cancelledList");
                                        break;
                                    case 4: // Registered
                                        shouldNotify = Boolean.TRUE.equals(onRegisteredList);
                                        System.out.println("We are fetching the registeredList");
                                        break;
                                }

                                if (shouldNotify) {
                                    String entrantId = document.getId();
                                    Notification notification = new Notification(eventId, message, false, ORGANIZER_CHANNEL_ID);
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

    private void addNotification(String userID, Notification notification) {
        db.collection("userProfiles")
                .document(userID)
                .collection("Notifications")
                .add(new HashMap<String, Object>() {{
                    put("Notification", notification);
                }})
                .addOnSuccessListener(aVoid ->
                        System.out.println("Notification successfully added!")
                )
                .addOnFailureListener(e ->
                        System.err.println("Error adding notification: " + e.getMessage())
                );
    }
}
