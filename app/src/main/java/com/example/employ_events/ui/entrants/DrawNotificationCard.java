package com.example.employ_events.ui.entrants;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentDrawNotificationCardBinding;
import com.example.employ_events.databinding.FragmentManageEventBinding;
import com.example.employ_events.ui.events.Event;
import com.google.firebase.firestore.FirebaseFirestore;

/*

Handles invites for when they're accepted to an eevent and when they didn't get accepted

*/
public class DrawNotificationCard extends Fragment {

    private FragmentDrawNotificationCardBinding binding;
    private FirebaseFirestore db;
    private Button acceptButton, declineButton;
    private TextView notificationStatus, notificationMessage, eventTitle, eventDate, confirmOrDecline;
    private boolean invited;

    // We will receive as an argument the event the notification is referring to and a boolean for if it's an invitation or a rejection

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDrawNotificationCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            invited = getArguments().getBoolean("INVITED");
            String eventId = getArguments().getString("EVENT_ID");
//            db.collection("event").document(eventId).get()
            if (eventId != null) {

                // Handling whether if this notification is for an invitation or rejection
                if (invited) {
                    notificationStatus.setText("Invitation");
                    notificationMessage.setText("Congratulations! You have been selected to register for the following event:");
                    confirmOrDecline.setText("Please confirm or decline your invitation below.");

                } else {
                    notificationStatus.setText("Sorry :(");
                    // Entrant doesn't have a choice, they will be notified if someone declines
                    notificationMessage.setText("Unfortunately, you were not randomly selected for this event. If a selected participant declines their invitation you MAY be notified again.");
                    // If this is a rejection, hide the event details and buttons
                    eventTitle.setVisibility(View.INVISIBLE);
                    eventDate.setVisibility(View.INVISIBLE);
                    acceptButton.setVisibility(View.INVISIBLE);
                    declineButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        return root;
    }

    private void initializeViews() {
        acceptButton = binding.acceptButton;
        declineButton = binding.declineButton;
        notificationStatus = binding.notificationStatus;
        notificationMessage = binding.notificationMessage;
        eventTitle = binding.eventTitle;
        eventDate = binding.eventDate;
        confirmOrDecline = binding.confirmOrDecline;
    }
}