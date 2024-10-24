package com.example.employ_events.ui.profile;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private SwitchCompat organizer_notifications, admin_notifications;
    private Button confirmButton, uploadButton, removeButton;
    private CollectionReference profilesRef;
    ImageView userPFP;

    /**
     * Creates the view hierarchy associated with this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment's layout.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews();

        String android_id = Secure.getString(requireContext().getContentResolver(), Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("userProfiles");

        // If profile exists, display the profile information.
        DocumentReference docRef = profilesRef.document(android_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        displayProfile(document, editProfileViewModel);
                    }
                }
            }
        });

        uploadButton.setOnClickListener(v->
                NavHostFragment.findNavController(EditProfileFragment.this)
                        .navigate(R.id.action_nav_edit_profile_to_nav_upload_image));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile(profilesRef, android_id);
            }
        });

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
        organizer_notifications = binding.profileOrganizerNotificationStatus;
        admin_notifications = binding.profileAdminNotificationStatus;
    }

    /**
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param editProfileViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, EditProfileViewModel editProfileViewModel) {
        organizer_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("organizerNotifications")));
        admin_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("adminNotifications")));
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
     * @param android_id The unique identifier for the user's device.
     */
    private void editProfile(CollectionReference profilesRef, String android_id) {
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
            data.put("phoneNumber", editPhone.getText().toString());
            data.put("organizerNotifications", organizer_notifications.isChecked());
            data.put("adminNotifications", admin_notifications.isChecked());

            profilesRef.document(android_id).set(data, SetOptions.merge());

            NavHostFragment.findNavController(EditProfileFragment.this)
                    .navigate(R.id.action_nav_edit_profile_pop);
        }
    }

    /**
     * Cleans up resources when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}