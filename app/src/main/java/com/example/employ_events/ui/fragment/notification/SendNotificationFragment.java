package com.example.employ_events.ui.fragment.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.employ_events.databinding.FragmentSendNotificationScreenBinding;
import com.example.employ_events.model.Notification;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * @author: Sahara
 * A fragment that allows the user to send notifications to event entrants.
 * The user can enter a custom message, select a category of recipients,
 * and send notifications to entrants in Firestore.
 */
public class SendNotificationFragment extends Fragment {

    private FragmentSendNotificationScreenBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Called to inflate the fragment's layout and initialize the UI components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI will attach to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return The root view of the inflated layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendNotificationScreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button confirmButton = binding.confirmButton;
        EditText messageInput = binding.messageInput;
        TabLayout tabLayout = binding.tabLayout;
        Button clearButton = binding.clearButton;
        //sendInvitationButton = binding.sendInvitationButton;


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

                // Clear current input.
                clearButton.setOnClickListener(view -> messageInput.setText(""));

            }
        }

        return root;
    }

    /**
     * Sends notifications to the event's entrants based on the selected category.
     *
     * @param eventId   The ID of the event whose entrants will receive notifications.
     * @param message   The custom message to include in the notification.
     * @param tabPosition The selected tab position to determine the category of recipients:
     *                    <ul>
     *                    <li>0 - All entrants</li>
     *                    <li>1 - Waitlisted entrants</li>
     *                    <li>2 - Accepted entrants</li>
     *                    <li>3 - Cancelled entrants</li>
     *                    <li>4 - Registered entrants (accepted but not cancelled)</li>
     *                    </ul>
     */
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
                                boolean shouldNotify = false;

                                switch (tabPosition) {
                                    case 0:
                                        shouldNotify = true; // All entrants
                                        break;
                                    case 1: // Waitlisted
                                        shouldNotify = Boolean.TRUE.equals(onWaitingList);
                                        break;
                                    case 2: // Selected
                                        shouldNotify = Boolean.TRUE.equals(onAcceptedList);
                                        break;
                                    case 3: // Cancelled
                                        shouldNotify = Boolean.TRUE.equals(onCancelledList);
                                        break;
                                    case 4: // Registered
                                        shouldNotify = Boolean.TRUE.equals(onAcceptedList) && !Boolean.TRUE.equals(onCancelledList);
                                        break;
                                }

                                if (shouldNotify) {
                                    String entrantId = document.getId();
                                    Notification notification = new Notification(eventId, message, false, ORGANIZER_CHANNEL_ID);
                                    addNotification(entrantId, notification);
                                    notification.sendNotification(getContext()); // Trigger system notification
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

    /**
     * Adds a notification to the specified user's Firestore profile.
     *
     * @param userID       The ID of the user to whom the notification will be added.
     * @param notification The Notification object containing the event ID, message, read status, and channel ID.
     */
    private void addNotification(String userID, Notification notification) {
        db.collection("userProfiles")
                .document(userID)
                .collection("Notifications")
                .add(new HashMap<String, Object>() {{
                    put("eventID", notification.getEventID());
                    put("message", notification.getMessage());
                    put("read", notification.isRead());
                    put("CHANNEL_ID", notification.getCHANNEL_ID());
                }})
                .addOnSuccessListener(aVoid ->
                        System.out.println("Notification successfully added!")
                )
                .addOnFailureListener(e ->
                        System.err.println("Error adding notification: " + e.getMessage())
                );
    }
}
