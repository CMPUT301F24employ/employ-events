package com.example.employ_events.ui.entrants;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.google.android.material.tabs.TabLayout;
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
    private List<Entrant> allEntrants = new ArrayList<>();

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

        //fetchWaitingListEntrants();
        fetchAllEntrants();

        // Set up TabLayout and listener
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "All":
                        entrantsAdapter.updateEntrants(allEntrants);
                        break;
                    case "Waitlisted":
                        List<Entrant> waitlistedEntrants = new ArrayList<>();
                        for (Entrant entrant : allEntrants) {
                            if (entrant.getOnWaitingList() != null && entrant.getOnWaitingList()) {
                                waitlistedEntrants.add(entrant);
                            }
                        }
                        entrantsAdapter.updateEntrants(waitlistedEntrants);
                        break;
                    // Add cases for other tabs as needed (Selected, Cancelled, Registered)
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchAllEntrants() {
        if (eventId != null) {
            db.collection("com/example/employ_events/ui/events").document(eventId)
                    .collection("entrants") // Assuming 'entrants' is the collection name
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            allEntrants.clear(); // Clear previous data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Entrant entrant = document.toObject(Entrant.class);
                                allEntrants.add(entrant);
                            }
                            entrantsAdapter.updateEntrants(allEntrants); // Show all entrants by default
                        } else {
                            Toast.makeText(this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

//    private void fetchWaitingListEntrants() {
//        if (eventId != null) {
//            db.collection("com/example/employ_events/ui/events").document(eventId)
//                    .collection("waitingList")
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            List<Entrant> entrants = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Entrant entrant = document.toObject(Entrant.class);
//                                entrants.add(entrant);
//                            }
//                            entrantsAdapter.updateEntrants(entrants);
//                        } else {
//                            Toast.makeText(this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
}
