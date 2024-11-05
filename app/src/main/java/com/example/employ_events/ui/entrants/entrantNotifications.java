package com.example.employ_events.ui.entrants;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEntrantNotificationsBinding;
import com.google.firebase.firestore.FirebaseFirestore;


public class entrantNotifications extends Fragment {

    private FragmentEntrantNotificationsBinding binding;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEntrantNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        initializeViews();

        // Recycler view
        /*
        maybe make a notification class?
        copy code from event list fragment
        make it so we click on event and get details of it
        THESE R NOTIFICATIONS
        send event document id and the boolean for invited over as a bundle when you click on the notification
        item in the recycler view
        on click listener (for clicking on the notifcaiton) -> bundle args whatever
         */

        return root;
    }

    private void initializeViews() {
        // set up the xml for the notifcation page
        // called past notifcations from old figma blue page
    }
}