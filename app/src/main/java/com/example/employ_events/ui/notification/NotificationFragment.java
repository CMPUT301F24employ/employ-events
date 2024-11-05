package com.example.employ_events.ui.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentFacilityBinding;
import com.example.employ_events.databinding.FragmentNotificationBinding;
import com.example.employ_events.databinding.FragmentProfileBinding;
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.facility.CreateFacilityFragment;
import com.example.employ_events.ui.facility.FacilityEventsAdapter;
import com.example.employ_events.ui.facility.FacilityFragment;
import com.example.employ_events.ui.facility.FacilityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationFragment extends Fragment{
    private FragmentNotificationBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button clearAll = binding.clearAllButton;
        binding.settingButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_nav_notifications_to_nav_edit_notifications)
        );
        return root;
    }
}
