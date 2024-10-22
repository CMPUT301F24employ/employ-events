package com.example.employ_events.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

//Fetches and displays the list of people who have joined the waiting list for an event
//Fetches data from Firebase, sets up RecyclerView, and passes data to EntrantsAdapter class to display

public class EntrantsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String eventId;
    private RecyclerView entrantsRecyclerView;
    private EntrantsAdapter entrantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");

        entrantsRecyclerView = findViewById(R.id.entrantsRecyclerView);
        entrantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantsAdapter = new EntrantsAdapter(new ArrayList<>());
        entrantsRecyclerView.setAdapter(entrantsAdapter);

        fetchWaitingListEntrants();
    }

    private void fetchWaitingListEntrants() {
        if (eventId != null) {
            db.collection("events").document(eventId)
                    .collection("waitingList")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Entrant> entrants = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Entrant entrant = document.toObject(Entrant.class);
                                entrants.add(entrant);
                            }
                            entrantsAdapter.updateEntrants(entrants);
                        } else {
                            Toast.makeText(this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
