package com.example.employ_events.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.employ_events.databinding.FragmentNotificationBinding;
import com.example.employ_events.ui.events.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationSettingFragment extends Fragment{
    private FragmentNotificationBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
    }

