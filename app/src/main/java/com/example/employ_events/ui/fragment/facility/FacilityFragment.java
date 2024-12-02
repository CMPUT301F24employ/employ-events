package com.example.employ_events.ui.fragment.facility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentFacilityBinding;
import com.example.employ_events.model.Facility;
import com.example.employ_events.ui.viewmodel.FacilityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
Authors: Tina, Sahara, and Jasleen

The purpose of this fragment is offer options through buttons
to edit their facility profile or view their events.

US 02.01.03 As an organizer, I want to create and manage my facility profile.
 */

/**
 * FacilityFragment displays facility profile information.
 * @author Tina
 * @author Jasleen
 * @author Sahara
 */
public class FacilityFragment extends Fragment implements CreateFacilityFragment.CreateFacilityDialogListener {

    private FragmentFacilityBinding binding;
    private FirebaseFirestore db;
    private TextView name, email, phone_number, address;
    private ImageView facilityPFP;
    private FacilityViewModel facilityViewModel;
    private String uniqueID;
    private Button deleteFacilityButton;
    private boolean isAdmin = false;

    /**
     * Called when a new facility is created. This method handles the addition of the facility to
     * Firestore and updates the user's profile.
     * @param facility  The created Facility object.
     * @param uniqueID  A unique identifier for the facility.
     * @author Tina
     */
    @Override
    public void createFacility(Facility facility, String uniqueID) {
        // Add the facility to Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("organizer", true);
        db.collection("facilities").add(facility);
        db.collection("userProfiles").document(uniqueID).set(data, SetOptions.merge());
    }

    /**
     * Called when a facility has been successfully created. This method refreshes the facility
     * data displayed in the UI.
     */
    @Override
    public void onFacilityCreated() {
        // Refresh the facility data when a facility is created
        refreshFacilityData();
    }

    /**
     * Displays the dialog for creating a facility. This method initializes the dialog fragment
     * and sets the listener for facility creation events.
     * @author Tina
     */
    private void showCreateFacilityDialog() {
        CreateFacilityFragment dialogFragment = new CreateFacilityFragment();
        dialogFragment.setListener(this); // Ensure listener is set before showing the dialog
        dialogFragment.show(getChildFragmentManager(), "Create Facility");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        facilityViewModel = new ViewModelProvider(this).get(FacilityViewModel.class);

        binding = FragmentFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore database instance
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) { // Coming from admin browse.
            uniqueID = getArguments().getString("organizer_id");
            isAdmin = getArguments().getBoolean("IS_ADMIN");
        }
        else { // Regular user.
            // Retrieve uniqueID from SharedPreferences for Firestore lookup
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            uniqueID = sharedPreferences.getString("uniqueID", null);

            isAdmin = false;

            // Prompt user to create facility if they are not yet an organizer.
            setupUserProfile(uniqueID);
        }

        // Initialize the UI components for the fragment
        initializeViews();

        // Fetch the facility ID and display facility profile.
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
                Toast.makeText(getContext(), "Create a facility to get started!", Toast.LENGTH_SHORT).show();
            }
        });

        // Button to navigate to EventListFragment to view events
        binding.viewEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_facilityFragment_to_eventListFragment)
        );

        // Button to edit facility profile.
        binding.editFacilityButton.setOnClickListener(view ->
                        Navigation.findNavController(view).navigate(R.id.action_nav_facility_to_nav_edit_facility)
                );

        // Remove facility and everything associated with it
        deleteFacilityButton.setOnClickListener(v -> {
            if (uniqueID != null) {
                deleteFacility(uniqueID);
            }
        });

        return root;
    }

    /**
     * Retrieves the facility ID associated with the given unique ID.
     * @param uniqueID The unique ID of the user.
     * @param listener Callback to return the facility ID.
     * @author Tina
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

    /**
     * Displays the facility's profile information in the UI.
     * @param document The Firestore document containing the facility's profile data.
     * @param facilityViewModel The ViewModel associated with this fragment.
     * @author Tina
     */
    private void displayProfile(DocumentSnapshot document, FacilityViewModel facilityViewModel) {
        name.setText(Objects.requireNonNull(document.get("name")).toString());
        facilityViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        email.setText(Objects.requireNonNull(document.get("email")).toString());
        facilityViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
        address.setText(Objects.requireNonNull(document.get("address")).toString());
        facilityViewModel.getText().observe(getViewLifecycleOwner(), address::setText);
        if (document.get("phone_number") != null) {
            phone_number.setText(Objects.requireNonNull(document.get("phone_number")).toString());
            facilityViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);
        }
        if (document.get("facilityPfpUri") != null) {
            String uri = document.getString("facilityPfpUri");
            loadImageFromUrl(uri);
        }
        else {
            facilityPFP.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Loads an image from a URL and displays it in the bannerImage.
     * @param imageUrl The URL of the image to be loaded.
     * @author Tina
     */
    private void loadImageFromUrl(String imageUrl) {
        if (isAdded()) {
            // Proceed with image loading
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(facilityPFP);
            facilityPFP.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Callback interface for fetching facility ID.
     * @author Tina
     */
    public interface OnFacilityIDFetchedListener {
        void onFacilityIDFetched(String facilityID);
    }

    /**
     * Checks if the user is an organizer and prompts non-organizers to create a facility. This
     * method fetches user profile data from Firestore to determine the user's status.
     * @param uniqueID The unique ID of the user.
     * @author Sahara
     */
    private void setupUserProfile(String uniqueID) {
        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && Boolean.FALSE.equals(document.getBoolean("organizer"))) {
                    showCreateFacilityDialog();
                }
            } else {
                Log.e("FacilityFragment", "Error fetching user profile: ", task.getException());
            }
        });
    }

    /**
     * Initializes the views for the fragment.
     */
    private void initializeViews() {
        name = binding.facilityNameTV;
        email = binding.facilityEmailTV;
        phone_number = binding.facilityPhoneTV;
        address = binding.facilityAddressTV;
        facilityPFP = binding.facilityPFP;
        deleteFacilityButton = binding.deleteFacilityButton;

        // Hiding edit button if the user is an admin and only showing delete facility button
        if (isAdmin) {
            binding.viewEventButton.setVisibility(View.GONE);
            binding.editFacilityButton.setVisibility(View.GONE);
        } else {
            deleteFacilityButton.setVisibility(View.GONE);
        }
    }

    /**
     * Called when the fragment becomes visible to the user. This method refreshes the facility data
     * each time the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Refresh the facility data
        refreshFacilityData();
    }

    /**
     * Refreshes the facility data displayed in the UI. This method re-fetches the facility ID and
     * updates the profile display.
     */
    public void refreshFacilityData() {
        // Logic to refresh facility data
        getFacilityID(uniqueID, facilityID -> {
            if (facilityID != null) {
                DocumentReference facilityRef = db.collection("facilities").document(facilityID);
                facilityRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        displayProfile(document, facilityViewModel); // Call your display method
                    }
                });
            }
        });
    }

    /**
     * Deletes the facility and all associated events and data from Firebase.
     * @param uniqueID The unique ID of the user, used to identify the facility.
     * @author Jasleen
     */
    private void deleteFacility(String uniqueID) {
        // Fetch the facility ID
        getFacilityID(uniqueID, facilityID -> {
            if (facilityID != null) {
                DocumentReference facilityRef = db.collection("facilities").document(facilityID);
                facilityRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot facilityDocument = task.getResult();
                        if (facilityDocument != null) {
                            // Fetch events
                            db.collection("events").whereEqualTo("facilityID", facilityID).get().addOnCompleteListener(eventTask -> {
                                if (eventTask.isSuccessful()) {
                                    // Check if the facility has any associated events, if not, then go ahead and delete facility
                                    if (eventTask.getResult().isEmpty()) {
                                        facilityRef.delete().addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Facility successfully deleted!", Toast.LENGTH_SHORT).show();
                                                NavHostFragment.findNavController(FacilityFragment.this).popBackStack();
                                            } else {
                                                Toast.makeText(getContext(), "Error deleting facility", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    // If yes, then delete events, events from entrants lists, and then facility
                                    else{
                                        for (QueryDocumentSnapshot eventDoc : eventTask.getResult()) {
                                            String eventID = eventDoc.getId();
                                            db.collection("entrantsList").whereEqualTo("id", eventID).get().addOnCompleteListener(entrantsTask -> {
                                                if (entrantsTask.isSuccessful()) {
                                                    for (QueryDocumentSnapshot entrantDoc : entrantsTask.getResult()) {
                                                        // Remove the event from any entrants lists
                                                        db.collection("entrantsList").document(entrantDoc.getId()).delete();
                                                    }
                                                }
                                            });

                                            //Delete the event and QR code
                                            db.collection("events").document(eventID).delete();
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("QRCodes/" + eventDoc.getId() + ".png");
                                            storageReference.delete();
                                            }

                                        // Delete the facility
                                        facilityRef.delete().addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Facility and associated events have been successfully deleted !", Toast.LENGTH_SHORT).show();
                                                NavHostFragment.findNavController(FacilityFragment.this).popBackStack();
                                            } else {
                                                Toast.makeText(getContext(), "Error deleting facility and associated events.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
