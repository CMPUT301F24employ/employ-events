package com.example.employ_events.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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

/**
 * @author Connor
 * AdminEventListFragment allows users who are admins (according to their firebase profile) to browse all the events
 * stored in firebase. Admins are also able to click on an event to be able to delete the event or its qr code.
 * US 03.04.01 As an administrator, I want to be able to browse events.
 */
public class AdminEventListFragment extends Fragment implements FacilityEventsAdapter.FEClickListener {

    private FragmentAdminEventListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    private FacilityEventsAdapter eventsAdapter;

    /**
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return the root view of the fragment
     *
     * Sets up the recycler view and queries & adds the events to the view.
     */
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

    /**
     * Initializes the recycler view, sets the adapter to view the events, and adds dividers to make it look neat.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.allEventsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Queries the events from firebase, adds them to the arraylist, and updates the recycler view
     */
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

    /**
     * @param event The event whose details will be displayed in the next fragment
     * This method brings the admin to the manage event fragment where they will be able to delete the qr code or the entire event.
     * @see com.example.employ_events.ui.events.ManageEventFragment
     */
    @Override
    public void onItemClick(Event event) {
        // When you click on an event maybe handle other admin actions like deleting the event
        if (getView() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("EVENT_ID", event.getId()); // Pass event ID
            bundle.putBoolean("IS_ADMIN", true);  // If you were on this page then you would be an admin
            Navigation.findNavController(getView()).navigate(R.id.action_adminEventListFragment_to_manageEventFragment, bundle);
        }

    }
}