package com.example.employ_events.ui.notifications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentSendNotificationScreenBinding;


public class SendNotificationScreen extends Fragment {

    private FragmentSendNotificationScreenBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendNotificationScreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }
}