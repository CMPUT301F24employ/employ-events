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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.provider.Settings.Secure;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView name, email, phone_number;
    private SwitchCompat organizer_notifications, admin_notifications;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        String android_id = Secure.getString(requireContext().getContentResolver(), Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        organizer_notifications = binding.profileOrganizerNotificationStatus;
        admin_notifications = binding.profileAdminNotificationStatus;
        name = binding.profileName;
        phone_number = binding.profilePhoneNumber;
        email = binding.profileEmail;


        DocumentReference profileRef = db.collection("userProfiles").document(android_id);
        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        organizer_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("organizerNotifications")));
                        admin_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("adminNotifications")));
                        name.setText(Objects.requireNonNull(document.get("name")).toString());
                        email.setText(Objects.requireNonNull(document.get("email")).toString());
                        phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
                    }
                }
            }
        });
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