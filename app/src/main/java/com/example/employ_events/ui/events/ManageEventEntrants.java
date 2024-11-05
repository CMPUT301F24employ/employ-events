package com.example.employ_events.ui.events;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentManageEventEntrantsBinding;
import com.example.employ_events.ui.entrants.WaitinglistFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManageEventEntrants extends Fragment {

    private FragmentManageEventEntrantsBinding binding;
    private FirebaseFirestore db;
    private Button sendNotification, sampleEntrants, removeEntrant, viewEntrantMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageEventEntrantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            if (eventId != null) {
//                db.collection("events").document(eventId).get()
//                        .addOnSuccessListener()
            }
        }
        sampleEntrants.setOnClickListener(v -> loadFragment(new WaitinglistFragment()));

        return root;
    }

    private void initializeViews() {
        sendNotification = binding.sendNotification;
        sampleEntrants = binding.sampleEntrants;
        removeEntrant = binding.removeEntrant;
        viewEntrantMap = binding.viewEntrantMap;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}