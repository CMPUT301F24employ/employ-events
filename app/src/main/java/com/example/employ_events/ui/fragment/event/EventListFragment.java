package com.example.employ_events.ui.fragment.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEventListBinding;
import com.example.employ_events.model.Event;
import com.example.employ_events.ui.adapter.FacilityEventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/*
Authors: Aasvi, Connor

This fragment is used for displaying the list of events an organizer owns, and
on click of an event, sends them to the specific event page.
 */

/**
 * EventListFragment displays a list of events for a specific facility.
 * It fetches events from Firestore based on the facility ID and allows
 * the user to view event details or add a new event.
 */
public class EventListFragment extends Fragment implements FacilityEventsAdapter.FEClickListener {

    private FragmentEventListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    private FacilityEventsAdapter eventsAdapter;

    /**
     * Called when the fragment's view is created. It initializes views,
     * sets up the RecyclerView, fetches the facility ID, and sets a listener
     * to add a new event.
     *
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventsAdapter = new FacilityEventsAdapter(getContext(), eventList, this);

        // Set up the RecyclerView to display the list of events
        setupRecyclerView();

        // Retrieve the unique ID from SharedPreferences and fetch facility ID
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);
        if (uniqueID != null) fetchFacilityID(uniqueID);

        // Set up listener to add new event
        binding.addEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_eventListFragment_to_addEventFragment)
        );

        return root;
    }

    /**
     * Sets up the RecyclerView to display the events list with a vertical divider.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Fetches the facility ID associated with the given unique ID from Firestore.
     * If the facility ID is found, it loads the events for that facility.
     *
     * @param uniqueID The unique ID of the current user (organizer).
     */
    private void fetchFacilityID(String uniqueID) {
        Query facilityQuery = db.collection("facilities").whereEqualTo("organizer_id", uniqueID);
        facilityQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String facilityID = document.getId();
                    loadEvents(facilityID);
                    break;
                }
            } else {
                Toast.makeText(getContext(), "Facility ID not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads the events associated with a given facility ID from Firestore.
     * Updates the events list and notifies the adapter.
     *
     * @param facilityID The ID of the facility whose events are to be loaded.
     */
    private void loadEvents(String facilityID) {
        db.collection("events").whereEqualTo("facilityID", facilityID).get().addOnCompleteListener(task -> {
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
                Toast.makeText(getContext(), "Error loading events!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles item clicks in the event list. It navigates to the ManageEventFragment
     * with the selected event ID passed as an argument.
     *
     * @param event The clicked event object.
     */
    @Override
    public void onItemClick(Event event) {
        // Pass the event ID to the EventDetailsFragment
        if (getView() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("EVENT_ID", event.getId()); // Pass event ID
            Navigation.findNavController(getView()).navigate(R.id.action_eventListFragment_to_manageEventFragment, bundle);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
