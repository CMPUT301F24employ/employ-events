package com.example.employ_events.ui.profile;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentProfileBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/*
Authors: Tina

This fragment is for viewing the users profile.
Allows user to press edit profile to send them to that fragment.
 */

/**
 * A fragment that displays the user's profile information.
 * It retrieves the profile data from Firestore based on a unique identifier and
 * populates the UI elements with the fetched data.
 */
public class ProfileFragment extends Fragment{

    private FragmentProfileBinding binding;
    private TextView name, email, phone_number;
    private Button editProfileButton, deleteProfileButton;
    private ImageView pfp;
    private boolean isAdmin = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        // Initialize Firestore database instance and set reference to "userProfiles" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference profilesRef = db.collection("userProfiles");

        if (getArguments() != null) {
            uniqueID = getArguments().getString("uniqueID");
            isAdmin = getArguments().getBoolean("IS_ADMIN", false);
        }

        // Initialize the UI components for the fragment
        initializeViews();

        // Display the profile information if uniqueID is available
        assert uniqueID != null;
        DocumentReference docRef = profilesRef.document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Populate UI with profile information
                    displayProfile(document, profileViewModel);
                }
            }
        });


        // Navigate to the edit profile screen when the button is clicked
        editProfileButton.setOnClickListener(v->
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_nav_profile_to_nav_edit_profile));

        String finalUniqueID = uniqueID;
        deleteProfileButton.setOnClickListener(v -> {
            if (finalUniqueID != null) {
                //deleteProfile(finalUniqueID, db);
                deleteProfileAndFacility(finalUniqueID, db);
            }
        });

        return root;
    }



    /**
     * Initializes the views for the fragment by binding UI elements to variables.
     */
    private void initializeViews() {
        name = binding.profileName;
        phone_number = binding.profilePhoneNumber;
        email = binding.profileEmail;
        editProfileButton = binding.editProfileButton;
        pfp = binding.userPFP;

        deleteProfileButton = binding.deleteProfileButton;
        // Hiding edit button if the user is an admin and only showing delete event button
        if (isAdmin) {
            editProfileButton.setVisibility(View.GONE);
        } else {
            deleteProfileButton.setVisibility(View.GONE);
        }

    }

    /**
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param profileViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, ProfileViewModel profileViewModel) {
        // Set views for each field if available.
        if (document.getString("name") != null) {
            name.setText(Objects.requireNonNull(document.get("name")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        }
        if (document.getString("email") != null) {
            email.setText(Objects.requireNonNull(document.get("email")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
        }

        if (document.getString("phoneNumber") != null && !Objects.requireNonNull(document.get("phoneNumber")).toString().equals("0")) {
            phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);
        }
        if (document.getString("pfpURI") != null) {
            String uri = document.getString("pfpURI");
            loadImageFromUrl(uri);
        }

    }

    /**
     * Loads an image from a URL and displays it in the ImageView.
     * @param url The URL of the image to be loaded.
     */
    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                requireActivity().runOnUiThread(() -> pfp.setImageBitmap(bitmap));
            } catch (IOException e) {
                Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }


    private void deleteProfileAndFacility(String uniqueID, FirebaseFirestore db) {

        WriteBatch batch = db.batch();
        DocumentReference profileRef = db.collection("userProfiles").document(uniqueID);

        db.collection("facilities")
                .whereEqualTo("organizer_id", uniqueID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean hasFacility = !task.getResult().isEmpty();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            batch.delete(document.getReference());
                        }

                        batch.delete(profileRef);
                        batch.commit()
                                .addOnSuccessListener(unused -> {
                                    if (hasFacility) {
                                        Toast.makeText(getContext(), "Profile and associated facility have been successfully deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                       Toast.makeText(getContext(), "No associated facility found. Profile successfully deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    NavHostFragment.findNavController(ProfileFragment.this).popBackStack();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error deleting profile and associated facility.", Toast.LENGTH_SHORT).show();
                                });
                    }
                    else {
                        Toast.makeText(getContext(), "Error fetching associated facility.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching associated facility.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}