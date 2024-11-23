package com.example.employ_events.ui.notifications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentSendNotificationScreenBinding;

/**
 * A Fragment for sending notifications, which retrieves an event ID (if available)
 * from the arguments to display relevant notification details.
 */
public class SendNotificationScreen extends Fragment {

    private FragmentSendNotificationScreenBinding binding;

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

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            if (eventId != null) {
                // use the event ID here

            }
        }

        return root;
    }
}