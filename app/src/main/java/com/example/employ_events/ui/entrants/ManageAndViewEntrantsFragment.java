package com.example.employ_events.ui.entrants;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.employ_events.R;
import com.google.android.material.tabs.TabLayout;
import android.widget.Button;
import android.widget.Toast;

public class ManageAndViewEntrantsFragment extends Fragment {

    private RecyclerView entrantsRecyclerView;
    private TabLayout tabLayout;
    private Button sampleEntrantsButton, removeEntrantButton, sendNotificationButton, viewEntrantMapButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage_entrants, container, false);

        // Initialize views
        entrantsRecyclerView = view.findViewById(R.id.entrantsRecyclerView);
        tabLayout = view.findViewById(R.id.tabLayout);
        sampleEntrantsButton = view.findViewById(R.id.sampleEntrantsButton);
        removeEntrantButton = view.findViewById(R.id.removeEntrantButton);
        sendNotificationButton = view.findViewById(R.id.sendNotificationButton);
        viewEntrantMapButton = view.findViewById(R.id.viewEntrantMapButton);

        // Setup tabs
        setupTabs();

        // Set up button click listeners
        sampleEntrantsButton.setOnClickListener(v -> showSampleEntrants());
        removeEntrantButton.setOnClickListener(v -> removeSelectedEntrant());
        sendNotificationButton.setOnClickListener(v -> sendNotification());
        viewEntrantMapButton.setOnClickListener(v -> viewEntrantMap());

        return view;
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection to filter entrants based on status
                String status = tab.getText().toString();
                filterEntrantsByStatus(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void filterEntrantsByStatus(String status) {
        // Implement filtering logic based on the status (e.g., All, Waitlisted, Selected, etc.)
        Toast.makeText(getContext(), "Filtering by " + status, Toast.LENGTH_SHORT).show();
    }

    private void showSampleEntrants() {
        // Implement logic to show sample entrants
        Toast.makeText(getContext(), "Sample Entrants", Toast.LENGTH_SHORT).show();
    }

    private void removeSelectedEntrant() {
        // Implement logic to remove selected entrant
        Toast.makeText(getContext(), "Remove Entrant", Toast.LENGTH_SHORT).show();
    }

    private void sendNotification() {
        // Implement logic to send notification to entrants
        Toast.makeText(getContext(), "Send Notification", Toast.LENGTH_SHORT).show();
    }

    private void viewEntrantMap() {
        // Implement logic to view entrant locations on a map
        Toast.makeText(getContext(), "View Entrant Map", Toast.LENGTH_SHORT).show();
    }
}

