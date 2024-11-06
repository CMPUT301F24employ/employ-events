package com.example.employ_events.ui.entrants;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEntrantsListBinding;
import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.entrants.EntrantsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
public class EntrantsListFragment extends Fragment{
    private FragmentEntrantsListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Entrant> entrantList;
    private EntrantsAdapter entrantsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEntrantsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        entrantList = new ArrayList<>();
        entrantsAdapter = new EntrantsAdapter(getContext(), entrantList);

        setupRecyclerView();

        // Retrieve the event ID from the arguments passed to the fragment
        Bundle args = getArguments();
        if (args != null) {
            String eventId = args.getString("EVENT_ID");
            if (eventId != null) {
                loadEntrants(eventId);
            } else {
                Toast.makeText(getContext(), "Event ID not found!", Toast.LENGTH_SHORT).show();
            }
        }

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.entrantsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(entrantsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadEntrants(String eventId) {
        db.collection("events").document(eventId).collection("entrantsList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                entrantList.clear(); // Clear the list to avoid duplicates
                for (QueryDocumentSnapshot entrantDocument : task.getResult()) {
                    String name = entrantDocument.getString("name");
                    if (name != null) {
                        Entrant entrant = new Entrant(name);
                        entrantList.add(entrant);
                        Log.d("Entrant", "Name: " + name);
                    }
                }
                entrantsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading entrants!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
