package com.example.employ_events.ui.entrants;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.employ_events.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class WaitinglistFragment extends Fragment {
    private RecyclerView recyclerView;
    private EntrantsAdapter entrantsAdapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waitinglist, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_waitlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entrantsAdapter = new EntrantsAdapter(new ArrayList<>());
        recyclerView.setAdapter(entrantsAdapter);

        db = FirebaseFirestore.getInstance();
        fetchWaitlistedEntrants();

        return view;
    }

    private void fetchWaitlistedEntrants() {
        db.collection("entrants") // Replace with the path to your Firestore collection
                .whereEqualTo("onWaitingList", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Entrant> entrants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Entrant entrant = document.toObject(Entrant.class);
                            entrants.add(entrant);
                        }
                        entrantsAdapter.updateEntrants(entrants);
                    }
                });
    }
}