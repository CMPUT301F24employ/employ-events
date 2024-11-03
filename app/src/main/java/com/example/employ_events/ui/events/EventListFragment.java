package com.example.employ_events.ui.events;

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
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.facility.FacilityEventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * EventListFragment displays a list of events for a specific facility.
 */
public class EventListFragment extends Fragment implements FacilityEventsAdapter.FEClickListener {

    private FragmentEventListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    private FacilityEventsAdapter eventsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventsAdapter = new FacilityEventsAdapter(getContext(), eventList, this);

        setupRecyclerView();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);
        if (uniqueID != null) fetchFacilityID(uniqueID);

        binding.addEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_eventListFragment_to_addEventFragment)
        );


        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.eventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

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
