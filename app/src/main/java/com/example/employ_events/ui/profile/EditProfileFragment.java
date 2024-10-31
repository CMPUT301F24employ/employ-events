package com.example.employ_events.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEditProfileBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A fragment that allows users to edit their profile information.
 * This fragment interacts with Firestore to retrieve and update user profiles.
 */
public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditText editName, editEmail, editPhone;
    //private SwitchCompat organizer_notifications, admin_notifications;
    private Button confirmButton, uploadButton, removeButton;
    private CollectionReference profilesRef;
    ImageView userPFP;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        initializeViews();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("userProfiles");

        // Display the profile information.
        DocumentReference docRef = profilesRef.document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    displayProfile(document, editProfileViewModel);
                }
            }
        });

        uploadButton.setOnClickListener(v->
                NavHostFragment.findNavController(EditProfileFragment.this)
                        .navigate(R.id.action_nav_edit_profile_to_nav_upload_image));

        confirmButton.setOnClickListener(view -> editProfile(profilesRef, uniqueID));

        return root;
    }

    /**
     * Initializes the views for the fragment.
     */
    private void initializeViews() {
        editName = binding.editTextUserName;
        editEmail = binding.editTextUserEmailAddress;
        editPhone = binding.editTextUserPhone;
        confirmButton = binding.confirmButton;
        uploadButton = binding.uploadProfileImage;
        removeButton = binding.removeProfileImage;
        userPFP = binding.userPFP;
        //organizer_notifications = binding.profileOrganizerNotificationStatus;
        //admin_notifications = binding.profileAdminNotificationStatus;
    }

    /**
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param editProfileViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, EditProfileViewModel editProfileViewModel) {
        //organizer_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("organizerNotifications")));
        //admin_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("adminNotifications")));
        // if NULL, it will be blank. Otherwise, display the users info.
        if (document.get("name") != null) {
            editName.setText(Objects.requireNonNull(document.get("name")).toString());
            editProfileViewModel.getText().observe(getViewLifecycleOwner(), editName::setText);
        }
        if (document.get("email") != null) {
            editEmail.setText(Objects.requireNonNull(document.get("email")).toString());
            editProfileViewModel.getText().observe(getViewLifecycleOwner(), editEmail::setText);
        }
        if (document.get("phoneNumber") != null && !document.get("phoneNumber").toString().equals("0")) {
            editPhone.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
            editProfileViewModel.getText().observe(getViewLifecycleOwner(), editPhone::setText);
        }
    }

    /**
     * Edits the user's profile based on the input fields and updates the Firestore database.
     *
     * @param profilesRef The Firestore collection reference for user profiles.
     * @param uniqueID The unique identifier for the user's device.
     */
    private void editProfile(CollectionReference profilesRef, String uniqueID) {
        Map<String, Object> data = new HashMap<>();
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        if (name.isEmpty()) {
            editName.setError("Name cannot be empty");
            editName.requestFocus();
        }
        else if (email.isEmpty()) {
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
        }
        else {
            data.put("name", editName.getText().toString());
            data.put("email", editEmail.getText().toString());
            int phone;
            if (editPhone.getText().toString().trim().isEmpty()) {
                phone = 0;
            }
            else {
                phone = Integer.parseInt(editPhone.getText().toString().trim());
            }
            data.put("phoneNumber", phone);

            profilesRef.document(uniqueID).set(data, SetOptions.merge());
            Toast.makeText(getActivity(), "Profile Updated!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(EditProfileFragment.this)
                    .navigate(R.id.action_nav_edit_profile_pop);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}