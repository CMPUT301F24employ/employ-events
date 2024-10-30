package com.example.employ_events.ui.facility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.employ_events.ui.events.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import java.util.Date;

/**
 * FacilityFragment is a Fragment that displays events associated with a facility.
 * It allows users to create events and shows a list of existing events for a facility.
 */
public class FacilityFragment extends Fragment implements FacilityEventsAdapter.FEClickListener {

    private FragmentFacilityBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    private FacilityEventsAdapter eventsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FacilityViewModel facilityViewModel =
                new ViewModelProvider(this).get(FacilityViewModel.class);

        binding = FragmentFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.equals(document.getBoolean("organizer"), false)) {
                    new CreateFacilityFragment().show(requireActivity().getSupportFragmentManager(), "Create Facility");
                    NavHostFragment.findNavController(FacilityFragment.this)
                            .navigate(R.id.action_nav_facility_to_nav_home);
                }
            } else {
                Log.e("MainActivity", "Error getting documents: ", task.getException());
            }
        });

        binding.createEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_facility_to_addEvent)
        );

        RecyclerView eventsRecyclerView = binding.eventsRecyclerView;
        DividerItemDecoration d = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        eventsRecyclerView.addItemDecoration(d);
        eventList = new ArrayList<>();
        eventsAdapter = new FacilityEventsAdapter(getContext(), eventList, this);
        eventsRecyclerView.setAdapter(eventsAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch the facility ID and then query events
        getFacilityID(uniqueID, facilityID -> {
            if (facilityID != null) {
                queryEvents(facilityID);
            } else {
                Toast.makeText(getContext(), "Facility ID not found!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /**
     * Retrieves the facility ID associated with the given unique ID.
     *
     * @param uniqueID The unique ID of the user.
     * @param listener Callback to return the facility ID.
     */
    private void getFacilityID(String uniqueID, OnFacilityIDFetchedListener listener) {
        Query facility = db.collection("facilities").whereEqualTo("organizer_id", uniqueID);
        facility.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    listener.onFacilityIDFetched(document.getId());
                    return; // Stop after finding the first match
                }
            }
            listener.onFacilityIDFetched(null); // No match found
        });
    }

    /**
     * Queries the events associated with the given facility ID.
     *
     * @param facilityID The ID of the facility.
     */
    private void queryEvents(String facilityID) {
        db.collection("events").whereEqualTo("facilityID", facilityID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot eventDocument : task.getResult()) {
                    Event e = new Event();
                    e.setEventTitle(eventDocument.getString("eventTitle"));
                    Timestamp timestamp = eventDocument.getTimestamp("eventDate");

                    if (timestamp == null) {
                        e.setEventDate(null);
                    } else {
                        Date eDate = timestamp.toDate();
                        e.setEventDate(eDate);
                    }
                    eventList.add(e);
                }
                eventsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading events or no events found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles click events on items in the event list.
     *
     * @param event The event that was clicked.
     */
    @Override
    public void onItemClick(Event event) {
        Toast.makeText(getContext(), "Event Clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback interface for fetching facility ID.
     */
    public interface OnFacilityIDFetchedListener {
        void onFacilityIDFetched(String facilityID);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}