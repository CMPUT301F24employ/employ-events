package com.example.employ_events.ui.fragment.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.employ_events.databinding.FragmentEditProfileBinding;
import com.example.employ_events.model.Profile;
import com.example.employ_events.ui.viewmodel.EditProfileViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
Authors: Tina

The purpose of this fragment is to allow a user to edit their profile fields.
Name and email cannot be empty, and will prompt the user if it is and not accept changes until it is not.
This is where a user can upload their profile picture and remove it.
This is where the automated profile picture is assigned based on name if a user has yet to set one.
No issues at the moment.

US 01.03.03	As an entrant I want my profile picture to be deterministically generated from my profile name
if I haven't uploaded a profile image yet.
US 01.03.02	As an entrant I want remove profile picture if need be
US 01.03.01	As an entrant I want to upload a profile picture for a more personalized experience
US 01.02.02	As an entrant I want to update information such as name, email and contact information on my profile
US 01.02.01	As an entrant, I want to provide my personal information such as name, email and optional phone number in the app

 */

/**
 * A fragment that allows users to edit their profile information.
 * This fragment interacts with Firestore to retrieve and update user profiles.
 * This fragment auto generates a profile picture if no custom one is provided.
 */
public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditText editName, editEmail, editPhone;
    private Button confirmButton, uploadButton, removeButton;
    private CollectionReference profilesRef;
    private ImageView userPFP;
    private Uri pfpUri;
    private boolean pfp = false, custom = false;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final String PERMISSION_READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the UI components for the fragment
        initializeViews();

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isAdded()) { // Check if the fragment is attached before showing the Toast
                        if (isGranted) {
                            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Permission denied to read your external storage", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if (isAdded()) { // Ensure fragment is still attached
                            pfpUri = result.getData().getData();
                            userPFP.setImageURI(pfpUri);
                            userPFP.setVisibility(View.VISIBLE);
                            removeButton.setVisibility(View.VISIBLE);
                            pfp = true;
                            custom = true;
                        }
                    }
                }
        );


        // Retrieve uniqueID from SharedPreferences for Firestore lookup
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        // Initialize Firestore database instance  and set reference to "userProfiles" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("userProfiles");

        // Display the profile information if uniqueID is available
        assert uniqueID != null;
        DocumentReference docRef = profilesRef.document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    displayProfile(document, editProfileViewModel);
                    custom = Boolean.TRUE.equals(document.getBoolean("customPFP"));
                }
            }
        });

        // Initially hide the remove button
        removeButton.setVisibility(View.GONE);

        // Set click listener for the upload pfp button
        uploadButton.setOnClickListener(v -> pickImage());

        // Set click listener for the remove pfp button
        removeButton.setOnClickListener(v -> {
            pfpUri = null; // Clear the pfp URI
            userPFP.setImageDrawable(null); // Clear the displayed image
            removeButton.setVisibility(View.GONE); // Hide the remove button
            pfp = false;
            custom = false;
        });

        // Navigate to the profile screen when the changes are confirmed.
        confirmButton.setOnClickListener(view -> editProfile(uniqueID, () -> NavHostFragment.findNavController(this).popBackStack()));

        return root;
    }

    /**
     * Initializes the views for the fragment by binding UI elements to variables.
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

        if (document.contains("pfpURI") && document.get("pfpURI") != null) {
            // Load custom profile picture
            String uri = Objects.requireNonNull(document.get("pfpURI")).toString();
            loadImageFromUrl(uri);
            removeButton.setVisibility(View.VISIBLE);
            pfp = true;
        }
        else {
            removeButton.setVisibility(View.GONE);
            pfp = false;
            pfpUri = null;
            // Load auto-generated profile picture based on user initial
            String name = document.get("name") != null ? Objects.requireNonNull(document.get("name")).toString() : "-";
            loadDefaultProfileImage(name);
        }
    }

    /**
     * Loads a default profile picture based on the initial of the user's name.
     * If the initial is not a letter, or if an image is not found for that initial, a fallback image is used.
     * The image is retrieved from Firebase Storage and displayed in the UI.
     * @param name The name of the user, used to determine the initial for the profile picture.
     */
    private void loadDefaultProfileImage(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fallbackRef = storage.getReference().child("autoPFP/Other.png");

        if (name == null || name.trim().isEmpty() || !Character.isLetter(name.charAt(0))) {
            // If name is null, empty, or starts with a non-letter, load fallback image
            fallbackRef.getDownloadUrl().addOnSuccessListener(fallbackUri -> loadImageFromUrl(fallbackUri.toString()));
            return;
        }

        String initial = name.substring(0, 1).toUpperCase();
        String imagePath = "autoPFP/" + initial + ".png";

        // Get the reference to the image in Firebase Storage
        StorageReference storageRef = storage.getReference().child(imagePath);

        // Retrieve the download URL for the image and load it
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> loadImageFromUrl(uri.toString())).addOnFailureListener(e -> {
            // Set a fallback image
            fallbackRef.getDownloadUrl().addOnSuccessListener(fallbackUri -> loadImageFromUrl(fallbackUri.toString()));
        });
    }

    /**
     * @author Tina
     * Loads an image from a URL and displays it in the bannerImage.
     * @param imageUrl The URL of the image to be loaded.
     */
    private void loadImageFromUrl(String imageUrl) {
        if (isAdded()) {
            // Proceed with image loading
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(userPFP);
            userPFP.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Edits the user's profile based on the input fields and updates the Firestore database.
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
            if (pfp && pfpUri != null) { // User uploaded a custom profile picture
                profile.setCustomPFP(true);
                uploadPFPAndSaveProfile(profile, onComplete); // Upload custom PFP
            }
            else if (pfp && custom) {
                profile.setCustomPFP(true);
                saveProfile(profile, uniqueID, onComplete); // Save profile data to Firestore
            }
            else {
                profile.setCustomPFP(false);
                String imagePath;
                if (!Character.isLetter(name.charAt(0))) {
                    imagePath = "autoPFP/Other.png"; // Use fallback for non-letter initials
                } else {
                    String initial = name.substring(0, 1).toUpperCase();
                    imagePath = "autoPFP/" + initial + ".png"; // Use auto-generated PFP based on initials
                }
                profile.setPfpURI("https://firebasestorage.googleapis.com/v0/b/employ-events.appspot.com/o/" + Uri.encode(imagePath) + "?alt=media");
                saveProfile(profile, uniqueID, onComplete); // Save profile data to Firestore
            }
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
     * Uploads a custom profile picture to Firebase Storage and updates the user's profile with the image URI.
     * After successfully uploading, the `saveProfile` method is called to update the profile data in Firestore.
     * @param editProfile The Profile object representing the user's profile.
     * @param onComplete  A callback to execute after successfully updating the profile in Firestore.
     */
    private void uploadPFPAndSaveProfile(Profile editProfile, Runnable onComplete) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("pfps/" + System.currentTimeMillis() + ".png");
        storageRef.putFile(pfpUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    editProfile.setPfpURI(uri.toString());
                    saveProfile(editProfile, editProfile.getUniqueID(), onComplete);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading pfp!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Saves the user's profile information in Firestore.
     * The data is saved under the document ID `uniqueID`, with fields for name, email, phone number, profile picture URI, and custom profile picture flag.
     * @param editProfile The Profile object representing the user's profile.
     * @param uniqueID    The unique identifier for the user's document in Firestore.
     * @param onComplete  A callback to execute after successfully saving the profile data in Firestore.
     */
    private void saveProfile(Profile editProfile, String uniqueID, Runnable onComplete) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", editProfile.getName());
        data.put("email", editProfile.getEmail());
        data.put("phoneNumber", editProfile.getPhoneNumber());
        data.put("customPFP", editProfile.isCustomPFP());
        if (pfp && pfpUri == null && custom) {

        }
        else {
            data.put("pfpURI", editProfile.getPfpURI());
        }

        profilesRef.document(uniqueID).set(data, SetOptions.merge())
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}