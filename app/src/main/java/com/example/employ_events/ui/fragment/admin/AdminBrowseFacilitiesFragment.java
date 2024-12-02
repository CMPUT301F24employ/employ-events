package com.example.employ_events.ui.fragment.admin;

import android.os.Bundle;
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
import com.example.employ_events.model.Facility;
import com.example.employ_events.ui.adapter.FacilityBrowseAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
/*
Author: Jasleen

The purpose of this fragment is to allow admins to view/scroll through a list of facilities.
Additionally, they have the option of deleting a facility, and by doing so, all associated events, events from entrants lists, and
QR hashed data is deleted as well.

US 03.07.01 As an administrator I want to remove facilities that violate app policy
 */

/**
 * @author Jasleen
 * Fragment that displays a list of facilities for an admin user.
 * This fragment fetches facilities from Firestore and shows them in a RecyclerView.
 * Admins can click on a facility to view more details.
 */
public class AdminBrowseFacilitiesFragment extends Fragment implements FacilityBrowseAdapter.FacilityClickListener {

    private FragmentAdminFacilityListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Facility> facilityList;
    private FacilityBrowseAdapter facilityAdapter;

    /**
     * @author Jasleen
     * Inflates the fragment layout and initializes the Firestore instance,
     * the list of facilities, and the RecyclerView adapter.
     */
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
     * @author Jasleen
     * Sets up the RecyclerView for displaying the facilities.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.allFacilitiesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(facilityAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * @author Jasleen and Tina
     * Loads facilities from Firestore and updates the RecyclerView.
     */
    private void loadFacilities() {
        db.collection("facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot facilityDocument : task.getResult()) {
                            String organizer_id = facilityDocument.getString("organizer_id");
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

    /**
     * @author Jasleen
     * Callback method for when a facility item in the RecyclerView is clicked.
     * This method navigates to a detail screen for the selected facility.
     *
     * @param facility The Facility object that was clicked.
     */
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
