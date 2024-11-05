package com.example.employ_events.ui.registeredEvents;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.databinding.FragmentRegisteredEventsBinding;
import com.example.employ_events.ui.events.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class RegisteredEventsFragment extends Fragment {

    private FragmentRegisteredEventsBinding binding;
    private ListView registeredList;
    private ArrayList<Event> eventDataList;
    private RegisteredArrayAdapter registeredArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventViewModel galleryViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentRegisteredEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("waitinglist");

        eventDataList = new ArrayList<>();
        registeredArrayAdapter = new RegisteredArrayAdapter(getContext(), eventDataList);
        RecyclerView recyclerView = binding.eventListView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(registeredArrayAdapter);

        loadRegisteredEvents();

        return root;
    }

    private void loadRegisteredEvents() {
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventDataList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    eventDataList.add(event);
                }
                registeredArrayAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

