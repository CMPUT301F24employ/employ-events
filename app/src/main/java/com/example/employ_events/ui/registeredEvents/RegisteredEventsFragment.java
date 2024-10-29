package com.example.employ_events.ui.registeredEvents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentRegisteredEventsBinding;
import com.example.employ_events.ui.events.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class RegisteredEventsFragment extends Fragment {

    private FragmentRegisteredEventsBinding binding;
    private ListView registeredList;
    private ArrayList<Event> eventDataList;
    private RegisteredArrayAdapter registeredArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private Button joinButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventViewModel galleryViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentRegisteredEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("waitinglist");

        //registeredList = binding.getRoot().findViewById(R.id.listView);
        eventDataList = new ArrayList<>();

        registeredArrayAdapter = new RegisteredArrayAdapter(getContext(), eventDataList);
        registeredList.setAdapter(registeredArrayAdapter);

        joinButton = binding.getRoot().findViewById(R.id.joinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Event newEvent = new Event("");
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

