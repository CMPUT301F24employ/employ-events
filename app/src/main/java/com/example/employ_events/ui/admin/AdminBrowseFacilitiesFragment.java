package com.example.employ_events.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentAdminFacilityListBinding;
import com.example.employ_events.ui.facility.Facility;
import com.example.employ_events.ui.facility.FacilityBrowseAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminBrowseFacilitiesFragment extends Fragment implements FacilityBrowseAdapter.FacilityClickListener {

    private FragmentAdminFacilityListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Facility> facilityList;
    private FacilityBrowseAdapter facilityAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminFacilityListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityBrowseAdapter(requireContext(), facilityList, this);

        setupRecyclerView();
        loadFacilities();

        return root;
    }

    /**
     * Sets up the RecyclerView for displaying the facilities.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.allFacilitiesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(facilityAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Loads facilities from Firestore and updates the RecyclerView.
     */
    private void loadFacilities() {
        db.collection("facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot facilityDocument : task.getResult()) {
                            String organizer_id = facilityDocument.getId();
                            String name = facilityDocument.getString("name");
                            String email = facilityDocument.getString("email");
                            String address = facilityDocument.getString("address");
                            String phone_number = facilityDocument.getString("phone_number");
                            Facility facility = new Facility(name, email, address, organizer_id, phone_number);

                            facilityList.add(facility);
                        }
                        facilityAdapter.updateFacilityList(facilityList);
                    } else {
                        Toast.makeText(getContext(), "Error loading facilities!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(Facility facility) {
        if (getView() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("organizer_id", facility.getOrganizer_id());
            bundle.putBoolean("IS_ADMIN", true);

            Navigation.findNavController(getView()).navigate(R.id.action_adminBrowseFacilitiesFragment_to_facilityFragment, bundle);
        }
    }
}
