package com.example.employ_events.ui.fragment.event;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentRegisteredEventsBinding;
import com.example.employ_events.model.Event;
import com.example.employ_events.ui.adapter.RegisteredArrayAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Date;

/**
 * Fragment to display a list of registered events. Fetches data from Firestore and displays it in a RecyclerView.
 * @author Tina
 * @author Jasleen
 * @author Aasvi
 */
public class RegisteredEventsFragment extends Fragment {

    private FragmentRegisteredEventsBinding binding;
    private ArrayList<Event> eventDataList;
    private RegisteredArrayAdapter registeredArrayAdapter;
    private String uniqueID;
    private FirebaseFirestore db;

    /**
     * Called to create and initialize the view for this fragment.
     *
     * @param inflater           the LayoutInflater object used to inflate views
     * @param container          the parent view that the fragment’s UI is attached to
     * @param savedInstanceState previously saved instance data
     * @return the root view of the inflated fragment layout
     *
     * @author Jasleen
     * @author Aasvi
     * @author Tina
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisteredEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve uniqueID from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uniqueID = sharedPreferences.getString("uniqueID", null);

        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView and adapter
        eventDataList = new ArrayList<>();
        registeredArrayAdapter = new RegisteredArrayAdapter(getContext(), eventDataList, eventId -> {
            // Navigate to EventDetailsFragment with the eventId as an argument
            Bundle args = new Bundle();
            args.putString("EVENT_ID", eventId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_nav_registered_events_to_eventDetailsFragment, args);
        });

        RecyclerView recyclerView = binding.eventListView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(registeredArrayAdapter);

        // Load registered events and update the adapter
        loadRegisteredEvents(eventDataList, registeredArrayAdapter);

        return root;
    }

    /**
     * Fetches registered events from the Firestore 'waitinglist' collection and updates the adapter with the data.
     * @author Tina
     */
    private void loadRegisteredEvents(ArrayList<Event> eventDataList, RegisteredArrayAdapter adapter) {
        if (uniqueID == null) {
            Log.e("loadRegisteredEvents", "uniqueID is null. Cannot query Firestore.");
            return; // Exit early if uniqueID is null
        }
        Date currentDate = new Date();
        db.collection("events")
                .whereGreaterThanOrEqualTo("eventDate", currentDate)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventDataList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventId = document.getId();

                    db.collection("events")
                            .document(eventId)
                            .collection("entrantsList")
                            .document(uniqueID)
                            .get()
                            .addOnCompleteListener(entrantTask -> {
                                if (entrantTask.isSuccessful() && entrantTask.getResult().exists()) {
                                    Boolean onCancelledList = entrantTask.getResult().getBoolean("onCancelledList");
                                    if (onCancelledList != null && !onCancelledList) {
                                        Event event = document.toObject(Event.class);
                                        event.setId(eventId); // Ensure eventId is set
                                        eventDataList.add(event);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if (!entrantTask.getResult().exists()) {
                                    Log.w("loadRegisteredEvents", "No entry found for uniqueID: " + uniqueID);
                                }
                            }).addOnFailureListener(e -> Log.e("loadRegisteredEvents", "Failed to fetch entrants for eventId: " + eventId, e));
                }
            } else {
                Log.e("loadRegisteredEvents", "Failed to fetch events collection.", task.getException());
            }
        });
    }

    /**
     * Called when the view hierarchy associated with the fragment is being removed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


