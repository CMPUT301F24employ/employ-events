package com.example.employ_events.ui.profile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employ_events.Profile;
import com.example.employ_events.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.provider.Settings.Secure;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private SwitchCompat organizer_notifications, admin_notifications;
    private Button editProfile;
    private CollectionReference profilesRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Grab device_id and initialize the firebase.
        String android_id = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("userProfiles");

        // Binding the profile elements to the corresponding views.
        organizer_notifications = binding.profileOrganizerNotificationStatus;
        admin_notifications = binding.profileAdminNotificationStatus;
        final TextView name = binding.profileName;
        final TextView phone_number = binding.profilePhoneNumber;
        final TextView email = binding.profileEmail;
        editProfile = binding.editProfileButton;

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
                            name.setText(Objects.requireNonNull(document.get("name")).toString());
                            profileViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
                        }
                        if (document.get("email") != null) {
                            email.setText(Objects.requireNonNull(document.get("email")).toString());
                            profileViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
                        }

                        if (document.get("phoneNumber") != null && !document.get("phoneNumber").toString().equals("0")) {
                            phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
                            profileViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);
                        }
                    }
                }
            }
        });

        //editProfile.setOnClickListener();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}