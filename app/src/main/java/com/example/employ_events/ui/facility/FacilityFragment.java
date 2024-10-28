package com.example.employ_events.ui.facility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentFacilityBinding;
import com.example.employ_events.ui.profile.ProfileFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FacilityFragment extends Fragment {

    private FragmentFacilityBinding binding;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FacilityViewModel facilityViewModel =
                new ViewModelProvider(this).get(FacilityViewModel.class);

        binding = FragmentFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.equals(document.getBoolean("organizer"), false)) {
                    new CreateFacilityFragment().show(requireActivity().getSupportFragmentManager(), "Create Facility");
                    if (Objects.equals(document.getBoolean("organizer"), false)) {
                        NavHostFragment.findNavController(FacilityFragment.this)
                                .navigate(R.id.action_nav_facility_to_nav_home);

                    }
                }

            } else {
                // Handle the error, e.g., log it
                Log.e("MainActivity", "Error getting documents: ", task.getException());
            }


        });


        // Set the button click listener to navigate to AddEventFragment
        binding.createEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_facility_to_addEvent)
        );



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
