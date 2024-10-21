package com.example.employ_events.ui.profile;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employ_events.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.provider.Settings.Secure;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private SwitchCompat organizer_notifications, admin_notifications;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String android_id = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        organizer_notifications = binding.profileOrganizerNotificationStatus;
        admin_notifications = binding.profileAdminNotificationStatus;
        final TextView name = binding.profileName;
        final TextView phone_number = binding.profilePhoneNumber;
        final TextView email = binding.profileEmail;

        db.collection("userProfiles").whereEqualTo("userID", android_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        organizer_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("organizerNotifications")));
                        admin_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("adminNotifications")));
                        name.setText(Objects.requireNonNull(document.get("name")).toString());
                        email.setText(Objects.requireNonNull(document.get("email")).toString());
                        phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
                    }
                }
            }
        });

        profileViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        profileViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
        profileViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);

        //final TextView textView = binding.textSlideshow;
        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}