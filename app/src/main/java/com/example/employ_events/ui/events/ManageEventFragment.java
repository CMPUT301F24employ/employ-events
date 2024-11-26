package com.example.employ_events.ui.events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentManageEventBinding;
import com.example.employ_events.ui.profile.EditProfileViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Objects;

/*
Authors: Tina, Sahara, Aasvi, Jasleen, Connor

The purpose of this fragment is to manage an event such as update the event banner, a button that
brings you to the download event qr page, a button to bring you to manage entrants page for the event,
and a button to delete the event if the user is an admin.
 */

/**
 * Fragment to manage event details including editing, viewing entrants, and obtaining QR codes.
 */
public class ManageEventFragment extends Fragment {
    private FragmentManageEventBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String bannerUri, eventId;
    private Button editEventButton, viewEntrantsButton, qrCodeButton, deleteEventButton;
    private TextView titleTV, descriptionTV, eventDateTV, registrationPeriodTV,
            eventCapacityTV, waitingListCapacityTV, eventFeeTV, geolocationRequiredTV;
    private ImageView bannerImage;
    private boolean isAdmin = false;

    /**
     * Inflates the layout, initializes views, fetches event details from Firestore,
     * and sets up click listeners for various buttons.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ManageEventViewModel manageEventViewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);
        // Inflate the layout for this fragment
        binding = FragmentManageEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Retrieve the event ID passed in the bundle arguments also checking for admin
        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
            isAdmin = getArguments().getBoolean("IS_ADMIN", false);
        }

        // Initialize views after it is determined whether user is admin or not
        initializeViews();

        // Fetch event details if eventId is available
        if (eventId != null) {
            DocumentReference eventRef = db.collection("events").document(eventId);
            eventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        displayDetails(document, manageEventViewModel);
                    }
                }
            });
        }

        editEventButton.setOnClickListener(view -> {
            if (getView() != null) {
                Bundle bundle = new Bundle();
                if (eventId != null) {
                    bundle.putString("EVENT_ID", eventId); // Pass event ID
                    Navigation.findNavController(getView()).navigate(R.id.action_manageEventFragment_to_editEventFragment, bundle);
                }
            }
        });

        qrCodeButton.setOnClickListener(view -> {
            if (getView() != null) {
                Bundle bundle = new Bundle();
                if (eventId != null) {
                    bundle.putString("EVENT_ID", eventId); // Pass event ID
                    Navigation.findNavController(getView()).navigate(R.id.action_manageEventFragment_to_download_qr_code, bundle);
                }
            }
        });

        // Manage Entrants
        viewEntrantsButton.setOnClickListener(view -> {
            if (getView() != null) {
                Bundle bundle = new Bundle();
                if (eventId != null) {
                    bundle.putString("EVENT_ID", eventId); // Pass event ID
                    Navigation.findNavController(getView()).navigate(R.id.action_manageEventFragment_to_manageEventEntrantsFragment, bundle);
                }
            }
        });

        // Delete Event
        deleteEventButton.setOnClickListener(view -> {
            db.collection("events").document(eventId).delete().addOnSuccessListener(unused -> {
                StorageReference storageReference = storage.getReference().child("QRCodes/" + eventId + ".png");

                storageReference.delete().addOnSuccessListener(unused1 -> {
                    Toast.makeText(getContext(), "Event successfully deleted!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Event successfully deleted!", Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error deleting event qr code from storage!", Toast.LENGTH_SHORT).show();
            });
        });
        return root;
    }

    /**
     * Initializes all view components (Buttons, TextViews, ImageViews).
     */
    private void initializeViews() {
        bannerImage = binding.bannerImage;
        titleTV = binding.eventTitle;
        descriptionTV = binding.description;
        eventDateTV = binding.eventDate;
        registrationPeriodTV = binding.registrationPeriod;
        eventCapacityTV = binding.eventCapacity;
        waitingListCapacityTV = binding.waitingListCapacity;
        eventFeeTV = binding.feeText;
        geolocationRequiredTV = binding.geolocationStatus;
        editEventButton = binding.editEventButton;
        qrCodeButton = binding.qrCodeButton;
        viewEntrantsButton = binding.viewEntrantsButton;
        deleteEventButton = binding.deleteEventButton;

        // Hiding these buttons if the user is an admin and only showing delete event button
        if (isAdmin) {
            editEventButton.setVisibility(View.GONE);
            qrCodeButton.setVisibility(View.GONE);
            viewEntrantsButton.setVisibility(View.GONE);
        } else {
            deleteEventButton.setVisibility(View.GONE);
        }

    }

    /**
     * Displays the event details in the UI based on the retrieved document from Firestore.
     * Handles both required and optional fields (e.g., waiting list capacity, event fee, etc.).
     *
     * @param document The Firestore document containing event data.
     * @param galleryViewModel The ViewModel for managing event data.
     */
    private void displayDetails(DocumentSnapshot document, ManageEventViewModel galleryViewModel) {
        // Set optional fields to invisible until it is determined to be non-null.
        waitingListCapacityTV.setVisibility(View.GONE);
        eventFeeTV.setVisibility(View.GONE);
        bannerImage.setVisibility(View.GONE);

        // Set fields to corresponding views.

        titleTV.setText(Objects.requireNonNull(document.get("eventTitle")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), titleTV::setText);

        // To format dates in user friendly format using month abbreviations and 12 hour clock with am/pm.
        Format formatter = new SimpleDateFormat("MMM-dd-yyyy hh:mm aa");
        String eventDate = formatter.format(document.getDate("eventDate"));
        String regOpenDate = formatter.format(document.getDate("registrationStartDate"));
        String regCloseDate = formatter.format(document.getDate("registrationDateDeadline"));

        eventDateTV.setText(Objects.requireNonNull(eventDate));
        galleryViewModel.getText().observe(getViewLifecycleOwner(), eventDateTV::setText);

        descriptionTV.setText(Objects.requireNonNull(document.get("description")).toString());
        galleryViewModel.getText().observe(getViewLifecycleOwner(), descriptionTV::setText);

        String registrationPeriod = "Registration Period: " + regOpenDate + " - " + regCloseDate;
        registrationPeriodTV.setText(registrationPeriod);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), registrationPeriodTV::setText);

        String eventCapacity = "Event Capacity: " + Objects.requireNonNull(document.get("eventCapacity"));
        eventCapacityTV.setText(eventCapacity);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), eventCapacityTV::setText);

        String geoLocation = "Geolocation required: " +
                Objects.requireNonNull(document.getBoolean("geoLocation"));
        geolocationRequiredTV.setText(geoLocation);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), geolocationRequiredTV::setText);

        // Check the non required fields and set visible if it is not null.

        if (document.get("limited") != null) {
            String waitingList = "Waiting List Capacity: " + Objects.requireNonNull(document.get("limited"));
            waitingListCapacityTV.setVisibility(View.VISIBLE);
            waitingListCapacityTV.setText(waitingList);
            galleryViewModel.getText().observe(getViewLifecycleOwner(), waitingListCapacityTV::setText);
        }

        if (document.get("fee") != null) {
            String fee = "Fee: " + Objects.requireNonNull(document.get("fee"));
            eventFeeTV.setVisibility(View.VISIBLE);
            eventFeeTV.setText(fee);
            galleryViewModel.getText().observe(getViewLifecycleOwner(), eventFeeTV::setText);
        }

        if (document.get("bannerUri") != null) {
            bannerImage.setVisibility(View.VISIBLE);
            bannerUri = Objects.requireNonNull(document.get("bannerUri")).toString();
            loadImageFromUrl(bannerUri);
        }
    }

    /**
     * Loads an image from the specified URL and sets it in the banner ImageView.
     * This method runs in a background thread to avoid blocking the main UI thread.
     *
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
                requireActivity().runOnUiThread(() -> bannerImage.setImageBitmap(bitmap));
            } catch (IOException e) {
                Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }

}
