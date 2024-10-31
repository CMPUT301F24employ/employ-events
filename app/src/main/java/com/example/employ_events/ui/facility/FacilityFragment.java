package com.example.employ_events.ui.facility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentFacilityBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * FacilityFragment displays facility profile information without listing events.
 */
public class FacilityFragment extends Fragment {

    private FragmentFacilityBinding binding;
    private FirebaseFirestore db;
    private TextView name, email, phone_number, address;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacilityViewModel facilityViewModel = new ViewModelProvider(this).get(FacilityViewModel.class);

        binding = FragmentFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);
        db = FirebaseFirestore.getInstance();

        setupUserProfile(uniqueID, facilityViewModel);

        name = binding.facilityNameTV;
        email = binding.facilityEmailTV;
        phone_number = binding.facilityPhoneTV;
        address = binding.facilityAddressTV;

        // Button to navigate to EventListFragment to view events
        binding.viewEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_facilityFragment_to_eventListFragment)
        );

        return root;
    }

    private void setupUserProfile(String uniqueID, FacilityViewModel facilityViewModel) {
        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && !document.getBoolean("organizer")) {
                    new CreateFacilityFragment().show(requireActivity().getSupportFragmentManager(), "Create Facility");
                    NavHostFragment.findNavController(FacilityFragment.this)
                            .navigate(R.id.action_nav_facility_to_nav_home);
                }
            } else {
                Log.e("FacilityFragment", "Error fetching user profile: ", task.getException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
