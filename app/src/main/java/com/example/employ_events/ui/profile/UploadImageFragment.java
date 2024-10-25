package com.example.employ_events.ui.profile;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.employ_events.databinding.UploadImageBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class UploadImageFragment extends Fragment {
    private UploadImageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = UploadImageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String android_id = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
