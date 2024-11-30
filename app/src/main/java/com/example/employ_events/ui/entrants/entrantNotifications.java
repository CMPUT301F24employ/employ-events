package com.example.employ_events.ui.entrants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEntrantNotificationsBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
 * List of previous notifications still in progress, to be completed when notifications are working.
 *
 * US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
 */

/**
 * @author Connor, Aasvi, Jasleen, Sahara, Tina
 * Displays a list of previous notifications.
 * Allows users to manage their notification settings.
 * The settings are stored in Firebase Firestore under the "userProfiles" collection.
 * Users can toggle switches for receiving admin and organizer notifications.
 * Changes are saved when the user clicks the "Save" button on the dialog.
 */
public class entrantNotifications extends Fragment {

    private FragmentEntrantNotificationsBinding binding;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private String uniqueID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEntrantNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // Show notification settings dialog when button is clicked.
        root.findViewById(R.id.button_notifications_settings).setOnClickListener(v -> showNotificationSettingsDialog());

        setupRecyclerView();

        // Recycler view
        /*
        maybe make a notification class?
        copy code from event list fragment
        make it so we click on event and get details of it
        THESE R NOTIFICATIONS
        send event document id and the boolean for invited over as a bundle when you click on the notification
        item in the recycler view
        on click listener (for clicking on the notification) -> bundle args whatever
         */

        return root;
    }

    private void setupRecyclerView() {
        recyclerView = binding.notificationRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

//    private void initializeViews() {
//        recyclerView = binding.notificationRecyclerView;
//
//        // set up the xml for the notifcation page
//        // called past notifcations from old figma blue page
//    }

    /**
     * @author Tina
     * Displays a dialog where the user can toggle notification settings for admin and organizer notifications.
     * Fetches current settings from Firebase Firestore and updates the switches accordingly.
     * When the user clicks "Save", the settings are saved back to Firestore.
     */
    private void showNotificationSettingsDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.notifications_setting, null);

        // Get references to the dialog's views
        SwitchMaterial switchAdminNotifications = dialogView.findViewById(R.id.switch_admin_notifications);
        SwitchMaterial switchOrganizerNotifications = dialogView.findViewById(R.id.switch_organizer_notifications);

        // Fetch user data from Firestore
        DocumentReference userDocRef = db.collection("userProfiles").document(uniqueID);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Set the switches based on Firestore data
                    Boolean adminNotifications = document.getBoolean("adminNotifications");
                    Boolean organizerNotifications = document.getBoolean("organizerNotifications");

                    // If the fields are not null, set the switches
                    if (adminNotifications != null) {
                        switchAdminNotifications.setChecked(adminNotifications);
                    }
                    if (organizerNotifications != null) {
                        switchOrganizerNotifications.setChecked(organizerNotifications);
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load settings.", Toast.LENGTH_SHORT).show();
            }
        });

        // Build the dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Notification Settings")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // When user clicks "Save", update Firestore with the new preferences
                    updateNotificationPreference("adminNotifications", switchAdminNotifications.isChecked());
                    updateNotificationPreference("organizerNotifications", switchOrganizerNotifications.isChecked());
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // If the user clicks "Cancel", just dismiss the dialog without saving
                    Toast.makeText(requireContext(), "Changes canceled", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    /**
     * @author Tina
     * Updates the notification preference for the user in Firestore.
     *
     * @param preferenceType The type of preference to update (either "adminNotifications" or "organizerNotifications").
     * @param isEnabled Whether the notification preference should be enabled (true) or disabled (false).
     */
    private void updateNotificationPreference(String preferenceType, boolean isEnabled) {
        // Update Firestore with the new preference
        DocumentReference userDocRef = db.collection("userProfiles").document(uniqueID);
        userDocRef.update(preferenceType, isEnabled)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to update settings", Toast.LENGTH_SHORT).show());
    }

}