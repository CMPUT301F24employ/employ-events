package com.example.employ_events.ui.events;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.EditEventBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A Fragment for editing event details, including uploading and displaying a banner image.
 * This fragment allows users to select an image from their device, upload it to Firebase Storage,
 * and save the image URI to Firestore.
 */
public class EditEventFragment extends Fragment {

    private EditEventBinding binding;
    private FirebaseFirestore db;
    private Uri bannerUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final String PERMISSION_READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;
    private Button saveButton, uploadBannerButton, removeBannerButton;
    private ImageView bannerImageView;
    private boolean isInitialized = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = EditEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
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
                        bannerUri = result.getData().getData();
                        bannerImageView.setImageURI(bannerUri);
                        bannerImageView.setVisibility(View.VISIBLE);
                        removeBannerButton.setVisibility(View.VISIBLE);
                        isInitialized = false;
                    }
                }
        );

        // Initially hide the remove button
        removeBannerButton.setVisibility(View.GONE);

        // Set click listener for the upload banner button
        uploadBannerButton.setOnClickListener(v -> pickImage());

        // Set click listener for the remove banner button
        removeBannerButton.setOnClickListener(v -> {
            bannerUri = null; // Clear the banner URI
            bannerImageView.setImageDrawable(null); // Clear the displayed image
            removeBannerButton.setVisibility(View.GONE); // Hide the remove button
            isInitialized = false;
        });

        // Get the event ID from the arguments
        String eventId = requireArguments().getString("EVENT_ID");

        // Load the event's current banner from Firestore
        DocumentReference eventRef = db.collection("events").document(Objects.requireNonNull(eventId));
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    if (document.get("bannerUri") != null) {
                        bannerUri = Uri.parse(Objects.requireNonNull(document.get("bannerUri")).toString());
                        loadImageFromUrl(String.valueOf(bannerUri));
                        removeBannerButton.setVisibility(View.VISIBLE);
                        bannerImageView.setVisibility(View.VISIBLE);
                    }
                    else {
                        // Mark as uninitialized if no URI found
                        isInitialized = false;
                    }
                }
            }
        });

        // Save button click listener to save only the banner
        saveButton.setOnClickListener(view -> {
            if (bannerUri != null) {
                if (!isInitialized) {
                    uploadBannerAndSaveOnly(eventId, () -> NavHostFragment.findNavController(EditEventFragment.this)
                            .popBackStack(R.id.manageEventFragment, false)); // Returns to ManageEventFragment without creating a new instance
                }
                else {
                    NavHostFragment.findNavController(EditEventFragment.this)
                        .popBackStack(R.id.manageEventFragment, false);
                }
            } else {
                saveBannerOnly(eventId, null, () -> NavHostFragment.findNavController(EditEventFragment.this)
                        .popBackStack(R.id.manageEventFragment, false)); // Returns to ManageEventFragment
            }
        });

        return root;
    }

    /**
     * Launches the image picker intent to select a banner.
     */
    private void pickImage() {
        // Check for storage permission
        checkAndRequestStoragePermission();

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Uploads the banner image to Firebase Storage and saves the URI to Firestore.
     * @param eventId The ID of the event to which the banner belongs.
     * @param onComplete A Runnable to be executed after the upload completes.
     */
    private void uploadBannerAndSaveOnly(String eventId, Runnable onComplete) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("banners/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(bannerUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save only the banner URI to Firestore
                    saveBannerOnly(eventId, uri.toString(), onComplete);
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error uploading banner!", Toast.LENGTH_SHORT).show()
                );
    }

    /**
     * Saves only the banner URI to Firestore.
     * @param eventId The ID of the event to update.
     * @param bannerUrl The URL of the uploaded banner image, or null if no new banner was uploaded.
     * @param onComplete A Runnable to be executed after the update completes.
     */
    private void saveBannerOnly(String eventId, String bannerUrl, Runnable onComplete) {
        Map<String, Object> data = new HashMap<>();
        data.put("bannerUri", bannerUrl);

        db.collection("events").document(eventId)
                .set(data, SetOptions.merge()) // Merges the bannerUri field without affecting other fields
                .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Banner updated successfully!", Toast.LENGTH_SHORT).show();
                            if (onComplete != null) {
                                onComplete.run(); // Call the callback after successful update
                            }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error updating banner!", Toast.LENGTH_SHORT).show()
                );
    }

    /**
     * Initializes UI components by getting references from the binding object.
     */
    private void initializeViews() {
        saveButton = binding.saveEventButton;
        uploadBannerButton = binding.uploadBannerButton;
        bannerImageView = binding.bannerImage;
        removeBannerButton = binding.removeBannerButton;
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
     * Loads an image from a URL and displays it in the ImageView.
     * @param url The URL of the image to be loaded.
     */
    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                requireActivity().runOnUiThread(() -> bannerImageView.setImageBitmap(bitmap));
            } catch (IOException e) {
                Log.e("EditProfileFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
