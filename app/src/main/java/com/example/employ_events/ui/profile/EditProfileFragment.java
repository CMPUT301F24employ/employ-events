package com.example.employ_events.ui.profile;

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
import com.example.employ_events.databinding.FragmentEditProfileBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A fragment that allows users to edit their profile information.
 * This fragment interacts with Firestore to retrieve and update user profiles.
 */
public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditText editName, editEmail, editPhone;
    private Button confirmButton, uploadButton, removeButton;
    private CollectionReference profilesRef;
    private ImageView userPFP;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri pfpUri;
    private FirebaseFirestore db;
    private boolean isInitialLoad = true;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        initializeViews();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);

        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("userProfiles");

        // Display the profile information.
        DocumentReference docRef = profilesRef.document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    displayProfile(document, editProfileViewModel);
                    isInitialLoad = Boolean.TRUE.equals(document.getBoolean("customPFP"));
                }
            }
        });
        // Check for storage permission
        checkStoragePermission();

        // Initially hide the remove button
        removeButton.setVisibility(View.GONE);

        // Set click listener for the upload banner button
        uploadButton.setOnClickListener(v -> openImageChooser());

        // Set click listener for the remove banner button
        removeButton.setOnClickListener(v -> {
            pfpUri = null; // Clear the banner URI
            userPFP.setImageDrawable(null); // Clear the displayed image
            removeButton.setVisibility(View.GONE); // Hide the remove button
            isInitialLoad = false;
        });

        confirmButton.setOnClickListener(view -> {
            editProfile(uniqueID, () -> {
                NavHostFragment.findNavController(EditProfileFragment.this)
                        .navigate(R.id.action_nav_edit_profile_to_nav_profile);
            });
        });

        return root;
    }

    /**
     * Initializes the views for the fragment.
     */
    private void initializeViews() {
        editName = binding.editTextUserName;
        editEmail = binding.editTextUserEmailAddress;
        editPhone = binding.editTextUserPhone;
        confirmButton = binding.confirmButton;
        uploadButton = binding.uploadPFP;
        removeButton = binding.removePFP;
        userPFP = binding.userPFP;


    }

    /**
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param editProfileViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, EditProfileViewModel editProfileViewModel) {

        // if NULL, it will be blank. Otherwise, display the users info.
        if (document.get("name") != null) {
            editName.setText(Objects.requireNonNull(document.get("name")).toString());
            editProfileViewModel.getText().observe(getViewLifecycleOwner(), editName::setText);
        }
        if (document.get("email") != null) {
            editEmail.setText(Objects.requireNonNull(document.get("email")).toString());
            editProfileViewModel.getText().observe(getViewLifecycleOwner(), editEmail::setText);
        }
        if (document.get("phoneNumber") != null) {
            editPhone.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
            editProfileViewModel.getText().observe(getViewLifecycleOwner(), editPhone::setText);
        }

        boolean isCustomPFP = document.getBoolean("customPFP") != null && document.getBoolean("customPFP");

        if (isCustomPFP && document.get("pfpURI") != null) {
            // Load custom profile picture
            String uri = document.get("pfpURI").toString();
            loadImageFromUrl(uri);
            removeButton.setVisibility(View.VISIBLE);
        } else {
            // Load auto-generated profile picture based on user initial
            String name = document.get("name") != null ? document.get("name").toString() : "-";
            loadDefaultProfileImage(name);
        }

    }

    private void loadDefaultProfileImage(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fallbackRef = storage.getReference().child("autoPFP/Other.png");

        if (name == null || name.trim().isEmpty() || !Character.isLetter(name.charAt(0))) {
            // If name is null, empty, or starts with a non-letter, load fallback image
            fallbackRef.getDownloadUrl().addOnSuccessListener(fallbackUri -> loadImageFromUrl(fallbackUri.toString()));
            return; // Exit the method early
        }

        String initial = name.substring(0, 1).toUpperCase(); // Get the first letter of the name
        String imagePath = "autoPFP/" + initial + ".png"; // Adjust this path to match your Firebase Storage

        // Get the reference to the image in Firebase Storage
        StorageReference storageRef = storage.getReference().child(imagePath);

        // Retrieve the download URL for the image and load it
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> loadImageFromUrl(uri.toString())).addOnFailureListener(e -> {
            // Handle errors or set a fallback image
            fallbackRef.getDownloadUrl().addOnSuccessListener(fallbackUri -> loadImageFromUrl(fallbackUri.toString()));
        });
    }

    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                getActivity().runOnUiThread(() -> userPFP.setImageBitmap(bitmap));
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
    private void editProfile(String uniqueID, Runnable onComplete) {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        Profile profile = new Profile(uniqueID);
        if (name.trim().isEmpty()) {
            editName.setError("Name cannot be empty");
            editName.requestFocus();
        }
        else if (email.trim().isEmpty()) {
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
        }
        else {
            profile.setName(name);
            profile.setEmail(email);
            profile.setPhoneNumber(phone.isEmpty() ? null : phone);
            if (pfpUri != null) { // User uploaded a custom profile picture
                profile.setCustomPFP(true); // Set custom PFP flag to true
                uploadPFPAndSaveProfile(profile, onComplete); // Upload custom PFP
            } else if (!isInitialLoad) { // If not the initial load and no PFP is selected
                // Handle removal of custom PFP
                String imagePath;
                if (!Character.isLetter(name.charAt(0))) {
                    imagePath = "autoPFP/Other.png"; // Use fallback for non-letter initials
                } else {
                    String initial = name.substring(0, 1).toUpperCase();
                    imagePath = "autoPFP/" + initial + ".png"; // Use auto-generated PFP based on initials
                }
                profile.setPfpURI("https://firebasestorage.googleapis.com/v0/b/employ-events.appspot.com/o/" + Uri.encode(imagePath) + "?alt=media");
                profile.setCustomPFP(false); // Set custom PFP flag to false
                saveProfile(profile, uniqueID, onComplete); // Save profile data to Firestore
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
            pfpUri = data.getData();
            userPFP.setImageURI(pfpUri);
            userPFP.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.VISIBLE);
            isInitialLoad = false;
        }
    }

    private void uploadPFPAndSaveProfile(Profile editProfile, Runnable onComplete) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("pfps/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(pfpUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    editProfile.setPfpURI(uri.toString());
                    saveProfile(editProfile, editProfile.getUniqueID(), onComplete);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading pfp!", Toast.LENGTH_SHORT).show());
    }

    private void saveProfile(Profile editProfile, String uniqueID, Runnable onComplete) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", editProfile.getName());
        data.put("email", editProfile.getEmail());
        data.put("phoneNumber", editProfile.getPhoneNumber());
        data.put("pfpURI", editProfile.getPfpURI());
        data.put("customPFP", editProfile.isCustomPFP()); // Save custom PFP flag
        profilesRef.document(uniqueID).set(data, SetOptions.merge())
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}