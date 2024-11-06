package com.example.employ_events.ui.events;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentManageEventEntrantsBinding;
import com.example.employ_events.ui.entrants.WaitinglistFragment;
import com.example.employ_events.ui.events.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.Task;
//import com.google.firebase.firestore.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;

public class ManageEventEntrants extends Fragment {

    private FragmentManageEventEntrantsBinding binding;
    private FirebaseFirestore db;
    private Button sendNotification, sampleEntrants, removeEntrant, viewEntrantMap;
    private String eventId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageEventEntrantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
            if (eventId != null) {
                // Button functionality
                sendNotification.setOnClickListener(view -> {
                    Bundle args = new Bundle();
                    args.putString("EVENT_ID", eventId);
                    NavHostFragment.findNavController(ManageEventEntrants.this)
                            .navigate(R.id.action_manageEventEntrantsFragment_to_sendNotificationsScreen, args);
                });
            }
        }

        sampleEntrants.setOnClickListener(v -> {
            if (eventId != null) {
                fetchEventAndGenerateSample(eventId);
            } else {
                Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void initializeViews() {
        sendNotification = binding.sendNotification;
        sampleEntrants = binding.sampleEntrants;
        removeEntrant = binding.removeEntrant;
        viewEntrantMap = binding.viewEntrantMap;
    }

    private void fetchEventAndGenerateSample(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    event.generateSample();
                    Boolean isSampleSuccessful = Boolean.FALSE;
                    if (event.getEntrantsList().size() == (Math.min(event.getEventCapacity(), event.getEntrantsList().size()))){
                        isSampleSuccessful = Boolean.TRUE;
                    }
                    if (isSampleSuccessful) {
                        Toast.makeText(getContext(), "Sampling successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Sampling unsuccessful :(", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
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
