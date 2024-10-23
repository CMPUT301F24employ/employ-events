package com.example.employ_events.ui.profile;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEditProfileBinding;
import com.example.employ_events.databinding.FragmentProfileBinding;
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

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditText editName, editEmail, editPhone;
    private SwitchCompat organizer_notifications, admin_notifications;
    private Button confirm_button, upload_button, delete_button;
    private CollectionReference profilesRef;
    private ImageView pfp;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editName = binding.editTextUserName;
        editEmail = binding.editTextUserEmailAddress;
        editPhone = binding.editTextUserPhone;
        confirm_button = binding.confirmButton;
        upload_button = binding.uploadProfileImage;
        delete_button = binding.removeProfileImage;
        pfp = binding.userPFP;
        organizer_notifications = binding.profileOrganizerNotificationStatus;
        admin_notifications = binding.profileAdminNotificationStatus;

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
                }
            }
        });

        upload_button.setOnClickListener(v->
                NavHostFragment.findNavController(EditProfileFragment.this)
                        .navigate(R.id.action_nav_edit_profile_to_nav_upload_image));


        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", editName.getText().toString());
                data.put("email", editEmail.getText().toString());
                data.put("phoneNumber", editPhone.getText().toString());
                data.put("organizerNotifications", organizer_notifications.isChecked());
                data.put("adminNotifications", admin_notifications.isChecked());

                profilesRef.document(android_id).set(data, SetOptions.merge());

                NavHostFragment.findNavController(EditProfileFragment.this)
                        .navigate(R.id.action_nav_edit_profile_pop);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}