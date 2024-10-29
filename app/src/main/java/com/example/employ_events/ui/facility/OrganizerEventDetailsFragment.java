package com.example.employ_events.ui.facility;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentOrganizerEventDetailsBinding;

public class OrganizerEventDetailsFragment extends Fragment {

    private FragmentOrganizerEventDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizerEventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
}