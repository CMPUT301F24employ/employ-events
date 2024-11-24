package com.example.employ_events.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentAdminEventListBinding;
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.facility.FacilityEventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminEventListFragment extends Fragment implements FacilityEventsAdapter.FEClickListener {

    // for US 3.04.01 - browsing events
    //

    private FragmentAdminEventListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    private FacilityEventsAdapter eventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminEventListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventsAdapter = new FacilityEventsAdapter(getContext(), eventList, this);

        setupRecyclerView();
        loadEvents();

        return root;

    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.allEventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadEvents() {
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot eventDocument : task.getResult()) {
                    Event event = new Event();
                    event.setId(eventDocument.getId());
                    event.setEventTitle(eventDocument.getString("eventTitle"));
                    event.setEventDate(eventDocument.getDate("eventDate"));
                    eventList.add(event);
                }
                eventsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading/retrieving events!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Event event) {
        // When you click on an event maybe handle other admin actions like deleting the event
    }
}