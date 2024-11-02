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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentEditFacilityBinding;

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

/**
 * A fragment that allows users to edit their profile information.
 * This fragment interacts with Firestore to retrieve and update user profiles.
 */
public class EditFacilityFragment extends Fragment {
    private FragmentEditFacilityBinding binding;
    private EditText editName, editEmail, editPhone, editAddress;
    private Button confirmButton, uploadButton, removeButton;
    private ImageView facilityPFP;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri facilityPfpUri;
    private FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditFacilityViewModel editFacilityViewModel =
                new ViewModelProvider(this).get(EditFacilityViewModel.class);

        binding = FragmentEditFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();

        initializeViews();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);



        // Fetch the facility ID and display profile.
        getFacilityID(uniqueID, facilityID -> {
            if (facilityID != null) {
                DocumentReference facilityRef = db.collection("facilities").document(facilityID);
                facilityRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        displayProfile(document, editFacilityViewModel);

                        // Check for storage permission
                        checkStoragePermission();

                        // Initially hide the remove button
                        removeButton.setVisibility(View.GONE);

                        // Set click listener for the upload banner button
                        uploadButton.setOnClickListener(v -> openImageChooser());

                        // Set click listener for the remove banner button
                        removeButton.setOnClickListener(v -> {
                            facilityPfpUri = null; // Clear the banner URI
                            facilityPFP.setImageDrawable(null); // Clear the displayed image
                            removeButton.setVisibility(View.GONE); // Hide the remove button
                        });

                        confirmButton.setOnClickListener(view -> editProfile(uniqueID, facilityID, () -> NavHostFragment.findNavController(EditFacilityFragment.this)
                                .navigate(R.id.action_nav_edit_facility_to_nav_facility)));
                    }
                });
            } else {
                Toast.makeText(getContext(), "Facility ID not found!", Toast.LENGTH_SHORT).show();
            }
        });



        return root;
    }

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
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param editFacilityViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, EditFacilityViewModel editFacilityViewModel) {

        // if NULL, it will be blank. Otherwise, display the users info.
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

        if (document.get("facilityPfpUri") != null) {
            String uri = Objects.requireNonNull(document.get("facilityPfpUri")).toString();
            loadImageFromUrl(uri);
            removeButton.setVisibility(View.VISIBLE);
        }

    }


    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                getActivity().runOnUiThread(() -> facilityPFP.setImageBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Edits the user's profile based on the input fields and updates the Firestore database.
     *
     *
     * @param uniqueID The unique identifier for the user's device.
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
            if (facilityPFP != null) { // User uploaded a custom profile picture
                uploadPFPAndSaveProfile(profile, facilityID, onComplete); // Upload custom PFP
            } else {
                saveProfile(profile, facilityID, onComplete); // Save profile data to Firestore
            }

        }

    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PFP"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            facilityPfpUri = data.getData();
            facilityPFP.setImageURI(facilityPfpUri);
            facilityPFP.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.VISIBLE);
        }
    }

    private void uploadPFPAndSaveProfile(Facility editProfile, String facilityID, Runnable onComplete) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("pfps/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(facilityPfpUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    editProfile.setFacilityPfpUri(uri.toString());
                    saveProfile(editProfile, facilityID, onComplete);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading pfp!", Toast.LENGTH_SHORT).show());
    }

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
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Toast.makeText(getContext(), "Error updating profile!", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkStoragePermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
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