package com.example.employ_events.ui.profile;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employ_events.databinding.FragmentEditProfileBinding;
import com.example.employ_events.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private TextView name, email, phone_number;
    private SwitchCompat organizer_notifications, admin_notifications;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        String android_id = Secure.getString(requireContext().getContentResolver(), Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}