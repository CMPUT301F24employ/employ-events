package com.example.employ_events.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

//import com.example.employ_events.databinding.FragmentImageBinding;
import com.example.employ_events.databinding.FragmentImageListBinding;
import com.example.employ_events.ui.facility.FacilityViewModel;
import com.google.firebase.firestore.FirebaseFirestore;


public class ImageListFragment extends Fragment {
    private FirebaseFirestore db;
    private FragmentImageListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;

    }

}