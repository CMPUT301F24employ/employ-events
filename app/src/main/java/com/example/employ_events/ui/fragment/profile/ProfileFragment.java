package com.example.employ_events.ui.fragment.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentProfileBinding;
import com.example.employ_events.ui.viewmodel.ProfileViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;

/*
Authors: Tina and Jasleen

This fragment is for viewing the users profile.
Allows user to press edit profile to send them to that fragment.
If a user is an admin, it allows them to remove profiles.
 */

/**
 * A fragment that displays the user's profile information.
 * It retrieves the profile data from Firestore based on a unique identifier and
 * populates the UI elements with the fetched data.
 * @author Tina
 * @author Jasleen
 */
public class ProfileFragment extends Fragment{

    private FragmentProfileBinding binding;
    private TextView name, email, phone_number;
    private Button editProfileButton, deleteProfileButton, notificationSettingsButton;
    private ImageView pfp;
    private String uniqueID;
    private boolean isAdmin = false;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // Initialize Firestore database instance and set reference to "userProfiles" collection
        db = FirebaseFirestore.getInstance();
        CollectionReference profilesRef = db.collection("userProfiles");

        if (getArguments() != null) {
            uniqueID = getArguments().getString("uniqueID");
            isAdmin = getArguments().getBoolean("IS_ADMIN", false);
        }

        // Initialize the UI components for the fragment
        initializeViews();

        // Display the profile information if uniqueID is available
        if (uniqueID != null) {
            DocumentReference docRef = profilesRef.document(uniqueID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Populate UI with profile information
                        displayProfile(document, profileViewModel);
                    }
                }
            });

            // Navigate to the edit profile screen when the button is clicked
            editProfileButton.setOnClickListener(v->
                    NavHostFragment.findNavController(ProfileFragment.this)
                            .navigate(R.id.action_nav_profile_to_nav_edit_profile));

            // Delete user profile and everything associated with it
            deleteProfileButton.setOnClickListener(v -> deleteProfileAndFacility());

            // Show notification settings dialog when button is clicked.
            notificationSettingsButton.setOnClickListener(v -> showNotificationSettingsDialog());
        }

        return root;
    }



    /**
     * Initializes the views for the fragment by binding UI elements to variables.
     */
    private void initializeViews() {
        name = binding.profileName;
        phone_number = binding.profilePhoneNumber;
        email = binding.profileEmail;
        editProfileButton = binding.editProfileButton;
        pfp = binding.userPFP;
        notificationSettingsButton = binding.buttonNotificationsSettings;
        deleteProfileButton = binding.deleteProfileButton;

        // Hiding edit button if the user is an admin and only showing delete profile button
        if (isAdmin) {
            editProfileButton.setVisibility(View.GONE);
            notificationSettingsButton.setVisibility(View.GONE);
        } else {
            deleteProfileButton.setVisibility(View.GONE);
        }

    }

    /**
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param profileViewModel The ViewModel associated with this fragment.
     * @author Tina
     */
    private void displayProfile(DocumentSnapshot document, ProfileViewModel profileViewModel) {
        // Set views for each field if available.
        if (document.getString("name") != null) {
            name.setText(Objects.requireNonNull(document.get("name")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        }
        if (document.getString("email") != null) {
            email.setText(Objects.requireNonNull(document.get("email")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
        }

        if (document.getString("phoneNumber") != null && !Objects.requireNonNull(document.get("phoneNumber")).toString().equals("0")) {
            phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);
        }
        if (document.getString("pfpURI") != null) {
            String uri = document.getString("pfpURI");
            loadImageFromUrl(uri);
        }

    }

    /**
     * Loads an image from a URL and displays it in the bannerImage.
     * @param imageUrl The URL of the image to be loaded.
     * @author Tina
     */
    private void loadImageFromUrl(String imageUrl) {
        if (isAdded()) {
            // Proceed with image loading
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(pfp);
        }
    }

    /**
     * Deletes a users profile and affiliated facility and events, along with removing them from event entrantLists.
     * @author Jasleen
     */
    private void deleteProfileAndFacility() {
        DocumentReference profileRef = db.collection("userProfiles").document(uniqueID);
        profileRef.get().addOnCompleteListener(profileTask -> {
            if (profileTask.isSuccessful() && profileTask.getResult() != null) {
                DocumentSnapshot profileDoc = profileTask.getResult();

                // Check if the profile has the "admin" field set to true
                // If yes, then the profile cannot be deleted, otherwise everything proceeds as normal
                isAdmin = Boolean.TRUE.equals(profileDoc.getBoolean("admin"));
                if (Boolean.TRUE.equals(isAdmin)) {
                    Toast.makeText(getContext(), "Sorry, you cannot delete admin profiles.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            WriteBatch batch = db.batch();

            // Fetching facilities associated with the user profile
            db.collection("facilities")
                    .whereEqualTo("organizer_id", uniqueID)
                    .get()
                    .addOnCompleteListener(facilityTask -> {
                        if (facilityTask.isSuccessful() && facilityTask.getResult() != null) {

                            // Check if the user has an associated facility
                            if (!facilityTask.getResult().isEmpty()) {
                                DocumentSnapshot facilityDoc = facilityTask.getResult().getDocuments().get(0);
                                // Delete the facility
                                batch.delete(facilityDoc.getReference());

                                // Fetching events whose facilityID match the facility's ID
                                db.collection("events")
                                        .whereEqualTo("facilityID", facilityDoc.getId())
                                        .get()
                                        .addOnCompleteListener(eventTask -> {
                                            if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                                                // Delete all events and QR codes associated with the facility
                                                for (DocumentSnapshot eventDoc : eventTask.getResult().getDocuments()) {
                                                    batch.delete(eventDoc.getReference());
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("QRCodes/" + eventDoc.getId() + ".png");
                                                    storageReference.delete();
                                                }
                                            }

                                            // Removing a user from the entrantsList in all events they have joined
                                            db.collection("events")
                                                    .get()
                                                    .addOnCompleteListener(allEventsTask -> {
                                                        if (allEventsTask.isSuccessful() && allEventsTask.getResult() != null) {
                                                            // Iterate through all events to find the ones where the user is an entrant
                                                            for (DocumentSnapshot eventDoc : allEventsTask.getResult().getDocuments()) {
                                                                db.collection("events")
                                                                        .document(eventDoc.getId())
                                                                        .collection("entrantsList")
                                                                        .document(uniqueID)
                                                                        .delete();
                                                                        }
                                                            }
                                                        });

                                            // Delete the user profile
                                            batch.delete(profileRef);
                                            batch.commit()
                                                    .addOnSuccessListener(unused -> {
                                                        Toast.makeText(getContext(), "Profile and associated facility have been successfully deleted!", Toast.LENGTH_SHORT).show();
                                                        NavHostFragment.findNavController(ProfileFragment.this).popBackStack();
                                                    })
                                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting profile and associated facility.", Toast.LENGTH_SHORT).show());
                                        });
                            }

                            // If no associated facility is found, only profile is deleted
                            else {

                                // Removing a user from the entrantsList in all events they have joined
                                db.collection("events")
                                        .get()
                                        .addOnCompleteListener(allEventsTask -> {
                                            if (allEventsTask.isSuccessful() && allEventsTask.getResult() != null) {
                                                for (DocumentSnapshot eventDoc : allEventsTask.getResult().getDocuments()) {
                                                    db.collection("events")
                                                            .document(eventDoc.getId())
                                                            .collection("entrantsList")
                                                            .document(uniqueID)
                                                            .delete();
                                                }
                                            }
                                        });

                                // Delete user profile
                                batch.delete(profileRef);
                                batch.commit()
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(getContext(), "No associated facility found. Profile deleted successfully!", Toast.LENGTH_SHORT).show();
                                            NavHostFragment.findNavController(ProfileFragment.this).popBackStack();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting profile.", Toast.LENGTH_SHORT).show());
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "Error fetching associated facilities.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error retrieving associated facilities.", Toast.LENGTH_SHORT).show());
        });
    }


    /**
     * Displays a dialog where the user can toggle notification settings for admin and organizer notifications.
     * Fetches current settings from Firebase Firestore and updates the switches accordingly.
     * When the user clicks "Save", the settings are saved back to Firestore.
     * @author Tina
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
     * Updates the notification preference for the user in Firestore.
     *
     * @param preferenceType The type of preference to update (either "adminNotifications" or "organizerNotifications").
     * @param isEnabled Whether the notification preference should be enabled (true) or disabled (false).
     * @author Tina
     */
    private void updateNotificationPreference(String preferenceType, boolean isEnabled) {
        // Update Firestore with the new preference
        DocumentReference userDocRef = db.collection("userProfiles").document(uniqueID);
        userDocRef.update(preferenceType, isEnabled)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to update settings", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}