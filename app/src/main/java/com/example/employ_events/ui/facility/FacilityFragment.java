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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        // Fetch the facility ID and display profile.
        getFacilityID(uniqueID, facilityID -> {
            if (facilityID != null) {
                DocumentReference facilityRef = db.collection("facilities").document(facilityID);
                facilityRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        displayProfile(document, facilityViewModel);
                    }
                });
            } else {
                Toast.makeText(getContext(), "Facility ID not found!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /**
     * Retrieves the facility ID associated with the given unique ID.
     *
     * @param uniqueID The unique ID of the user.
     * @param listener Callback to return the facility ID.
     */
    private void getFacilityID(String uniqueID, OnFacilityIDFetchedListener listener) {
        Query facility = db.collection("facilities").whereEqualTo("organizer_id", uniqueID);
        facility.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    listener.onFacilityIDFetched(document.getId());
                    return; // Stop after finding the first match
                }
            }
            listener.onFacilityIDFetched(null); // No match found
        });
    }
    private void displayProfile(DocumentSnapshot document, FacilityViewModel facilityViewModel) {
        name.setText(Objects.requireNonNull(document.get("name")).toString());
        facilityViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        email.setText(Objects.requireNonNull(document.get("email")).toString());
        facilityViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
        address.setText(Objects.requireNonNull(document.get("address")).toString());
        facilityViewModel.getText().observe(getViewLifecycleOwner(), address::setText);
        if (document.get("phoneNumber") != null && !document.get("phoneNumber").toString().equals("0")) {
            phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
            facilityViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);
        }
    }
    /**
     * Callback interface for fetching facility ID.
     */
    public interface OnFacilityIDFetchedListener {
        void onFacilityIDFetched(String facilityID);
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
