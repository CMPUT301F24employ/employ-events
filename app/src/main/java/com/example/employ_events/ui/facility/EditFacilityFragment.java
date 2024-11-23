package com.example.employ_events.ui.facility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEditFacilityBinding;

import com.example.employ_events.ui.profile.EditProfileFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/*
The purpose of this fragment is to allow the facility to edit their contact info or upload/remove their pfp.

There is a bug where you have previously uploaded a pfp, and go to edit the profile again and only update non pfp fields.
The pfp clears after confirming and is set to null.
US 02.01.03 As an organizer, I want to create and manage my facility profile

 */

/**
 * A fragment that allows users to edit their facility's profile information.
 * This fragment interacts with Firestore to retrieve and update facility profiles.
 */
public class EditFacilityFragment extends Fragment {
    private FragmentEditFacilityBinding binding;
    private EditText editName, editEmail, editPhone, editAddress;
    private Button confirmButton, uploadButton, removeButton;
    private ImageView facilityPFP;
    private Uri facilityPfpUri;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final String PERMISSION_READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditFacilityViewModel editFacilityViewModel =
                new ViewModelProvider(this).get(EditFacilityViewModel.class);

        binding = FragmentEditFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // Initialize Firestore database instance
        db = FirebaseFirestore.getInstance();

        // Initialize the UI components for the fragment
        initializeViews();

        // Initialize the ActivityResultLauncher for requesting permission
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission was granted
                        Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        // Permission was denied
                        Toast.makeText(getContext(), "Permission denied to read your external storage", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Initialize the ActivityResultLauncher for picking an image.
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // Handle the image selection
                        facilityPfpUri = result.getData().getData();
                        facilityPFP.setImageURI(facilityPfpUri);
                        facilityPFP.setVisibility(View.VISIBLE);
                        removeButton.setVisibility(View.VISIBLE);
                    }
                }
        );


        // Fetch the facility ID and display profile.
        getFacilityID(uniqueID, facilityID -> {
            if (facilityID != null) {
                DocumentReference facilityRef = db.collection("facilities").document(facilityID);
                facilityRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        displayProfile(document, editFacilityViewModel);

                        // Set click listener for the upload banner button
                        uploadButton.setOnClickListener(v -> pickImage());

                        // Set click listener for the remove banner button
                        removeButton.setOnClickListener(v -> {
                            // Clear the image in the UI but do not delete the URI from Firestore
                            facilityPfpUri = null;
                            facilityPFP.setImageDrawable(null); // Clear the displayed image
                            removeButton.setVisibility(View.GONE); // Hide the remove button
                        });

                        confirmButton.setOnClickListener(view -> editProfile(uniqueID, facilityID, () -> NavHostFragment.findNavController(EditFacilityFragment.this)
                                .popBackStack(R.id.nav_facility, false)));
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
     * @param uniqueID The unique ID of the user.
     * @param listener Callback to return the facility ID.
     */
    private void getFacilityID(String uniqueID, FacilityFragment.OnFacilityIDFetchedListener listener) {
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
     * Initializes the views for the fragment.
     */
    private void initializeViews() {
        editName = binding.editTextFacilityName;
        editEmail = binding.editTextFacilityEmailAddress;
        editPhone = binding.editTextFacilityPhone;
        editAddress = binding.editTextFacilityAddress;
        confirmButton = binding.confirmButton;
        uploadButton = binding.uploadPFP;
        removeButton = binding.removePFP;
        facilityPFP = binding.facilityPFP;
    }

    /**
     * Displays the facility's profile information in the UI.
     * @param document            The Firestore document containing the facility's profile data.
     * @param editFacilityViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, EditFacilityViewModel editFacilityViewModel) {
        // if NULL, it will be blank. Otherwise, display the facility's info.
        if (document.get("name") != null) {
            editName.setText(Objects.requireNonNull(document.get("name")).toString());
            editFacilityViewModel.getText().observe(getViewLifecycleOwner(), editName::setText);
        }
        if (document.get("email") != null) {
            editEmail.setText(Objects.requireNonNull(document.get("email")).toString());
            editFacilityViewModel.getText().observe(getViewLifecycleOwner(), editEmail::setText);
        }
        if (document.get("phone_number") != null) {
            editPhone.setText(Objects.requireNonNull(document.get("phone_number")).toString());
            editFacilityViewModel.getText().observe(getViewLifecycleOwner(), editPhone::setText);
        }
        if (document.get("address") != null) {
            editAddress.setText(Objects.requireNonNull(document.get("address")).toString());
            editFacilityViewModel.getText().observe(getViewLifecycleOwner(), editAddress::setText);
        }
        // Check if there's a profile picture URI available in Firestore
        if (document.get("facilityPfpUri") != null) {
            String uri = Objects.requireNonNull(document.get("facilityPfpUri")).toString();
            loadImageFromUrl(uri);
            // Show the remove button if there's a profile picture
            removeButton.setVisibility(View.VISIBLE);
        } else {
            // If no profile picture, hide the remove button
            removeButton.setVisibility(View.GONE);
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
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                requireActivity().runOnUiThread(() -> facilityPFP.setImageBitmap(bitmap));
            } catch (IOException e) {
                Log.e("EditFacilityFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Edits the facility's profile based on the input fields and updates the Firestore database.
     * @param uniqueID The unique identifier for the user's device.
     * @param facilityID The unique identifier for the facility.
     * @param onComplete A callback to execute after successfully updating the profile in Firestore.
     */
    private void editProfile(String uniqueID, String facilityID, Runnable onComplete) {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        Facility profile = new Facility(name, email, address, uniqueID);
        if (name.trim().isEmpty()) {
            editName.setError("Name cannot be empty");
            editName.requestFocus();
        }
        else if (email.trim().isEmpty()) {
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
        }
        else if (address.trim().isEmpty()) {
            editAddress.setError("Address cannot be empty");
            editAddress.requestFocus();
        }
        else {
            profile.setName(name);
            profile.setEmail(email);
            profile.setPhone_number(phone.isEmpty() ? null : phone);
            profile.setAddress(address);
            // Handle profile picture logic
            if (facilityPfpUri != null) {
                // If the user has uploaded a new profile picture, upload it.
                uploadPFPAndSaveProfile(profile, facilityID, onComplete);
            } else {
                profile.setFacilityPfpUri(null);
                // If no new profile picture, save the profile data.
                saveProfile(profile, facilityID, onComplete);
            }
        }
    }

    /**
     * Uploads a custom profile picture to Firebase Storage and updates the facility's profile with the image URI.
     * After successfully uploading, the `saveProfile` method is called to update the profile data in Firestore.
     *
     * @param editProfile The Facility object representing the facility's profile.
     * @param facilityID The unique identifier for the facility.
     * @param onComplete  A callback to execute after successfully updating the profile in Firestore.
     */
    private void uploadPFPAndSaveProfile(Facility editProfile, String facilityID, Runnable onComplete) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("pfps/" + System.currentTimeMillis() + ".png");
        storageRef.putFile(facilityPfpUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    editProfile.setFacilityPfpUri(uri.toString());
                    saveProfile(editProfile, facilityID, onComplete);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading pfp!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Saves the facility's profile information in Firestore.
     * The data is saved under the document ID `facilityID`, with fields for name, email, phone number, profile picture URI, and address.
     * @param editProfile The Facility object representing the facility's profile.
     * @param facilityID    The unique identifier for the facility's document in Firestore.
     * @param onComplete  A callback to execute after successfully saving the profile data in Firestore.
     */
    private void saveProfile(Facility editProfile, String facilityID, Runnable onComplete) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", editProfile.getName());
        data.put("email", editProfile.getEmail());
        data.put("phone_number", editProfile.getPhone_number());
        data.put("address", editProfile.getAddress());
        data.put("facilityPfpUri", editProfile.getFacilityPfpUri());

        db.collection("facilities").document(facilityID).set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (onComplete != null) {
                        onComplete.run(); // Call the callback after successful update
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating profile!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Checks if storage permission is granted and requests it if necessary.
     */
    private void checkAndRequestStoragePermission() {
        String permission = PERMISSION_READ_MEDIA_IMAGES;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; request permission
            requestPermissionLauncher.launch(permission);
        }
    }

    /**
     * Launches the image picker intent to select a profile picture.
     */
    private void pickImage() {
        // Check for storage permission
        checkAndRequestStoragePermission();

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Callback interface for fetching facility ID.
     */
    public interface OnFacilityIDFetchedListener {
        void onFacilityIDFetched(String facilityID);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}