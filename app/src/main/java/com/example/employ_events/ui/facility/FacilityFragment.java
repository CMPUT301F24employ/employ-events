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

public class FacilityFragment extends Fragment {

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
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.equals(document.getBoolean("organizer"), false)) {
                    new CreateFacilityFragment().show(requireActivity().getSupportFragmentManager(), "Create Facility");
                    if (Objects.equals(document.getBoolean("organizer"), false)) {
                        NavHostFragment.findNavController(FacilityFragment.this)
                                .navigate(R.id.action_nav_facility_to_nav_home);
                    }
                }
            } else {
                // Handle the error, e.g., log it
                Log.e("MainActivity", "Error getting documents: ", task.getException());
            }
        });

        binding.createEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_facility_to_addEvent)
        );

        // VIEWING THE EVENTS IN A FACILITY
        // Only need to store the info of the event name and date on the cardview, more info will be accessed elsewhere

        // Setting up the RecyclerView and its adapter
        RecyclerView eventsRecyclerView = binding.eventsRecyclerView;
        // Lines between each event
        DividerItemDecoration d = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        eventsRecyclerView.addItemDecoration(d);
        eventList = new ArrayList<>();
        eventsAdapter = new FacilityEventsAdapter(getContext(), eventList);
        eventsRecyclerView.setAdapter(eventsAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        queryEvents(uniqueID);
        return root;
    }

    private String getFacilityID(String uniqueID) {
        final String[] facilityID = new String[1];
        Query facility = db.collection("facilities").whereEqualTo("owner_id", uniqueID);
        facility.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        facilityID[0] = document.getId();
                    }
                }
            }
        });
        return facilityID[0];
    }

    private void queryEvents(String uniqueID) {
        String facilityID = getFacilityID(uniqueID);
        db.collection("events").whereEqualTo("facilityID", facilityID).get().addOnCompleteListener(task -> {

            // The task is the query: If we received events from the query do this code:
            if (task.isSuccessful()) {
                // task.getResult() is a query snapshot which just holds the documents in the query (the results)
                for (DocumentSnapshot eventDocument: task.getResult()) {
                    Event e = new Event();
                    e.setEventTitle(eventDocument.getString("eventTitle"));
                    Timestamp timestamp = eventDocument.getTimestamp("eventDate");

                    // IF THE EVENT DOESN'T HAVE A DATE
                    if (timestamp == null) {
                        e.setEventDate(null);
                    } else {
                        Date eDate = timestamp.toDate();  // Might cause an issue if timestamp isn't a proper date?
                        e.setEventDate(eDate);
                    }
                    eventList.add(e);
                }
                eventsAdapter.notifyDataSetChanged();
            }
            // We didn't receive any events from the query
            else {
                Toast.makeText(getContext(), "Error loading events or no events found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}