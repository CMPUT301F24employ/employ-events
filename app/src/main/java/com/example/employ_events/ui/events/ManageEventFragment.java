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

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentManageEventBinding;
import com.example.employ_events.ui.profile.EditProfileViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class ManageEventFragment extends Fragment {
    private FragmentManageEventBinding binding;
    private FirebaseFirestore db;
    private String bannerUri;
    private Button editEventButton, viewEntrantsButton, qrCodeButton;
    private TextView titleTV, descriptionTV, eventDateTV, registrationPeriodTV,
            eventCapacityTV, waitingListCapacityTV, eventFeeTV, geolocationRequiredTV;
    private ImageView bannerImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ManageEventViewModel manageEventViewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);
        // Inflate the layout for this fragment
        binding = FragmentManageEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        initializeViews();

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
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
        }

        editEventButton.setOnClickListener(view -> {
            if (getView() != null) {
                Bundle bundle = new Bundle();
                if (getArguments() != null) {
                    String eventId = getArguments().getString("EVENT_ID");
                    if (eventId != null) {
                        bundle.putString("EVENT_ID", eventId); // Pass event ID
                        Navigation.findNavController(getView()).navigate(R.id.action_manageEventFragment_to_editEventFragment, bundle);
                    }
                }
            }
        });

        // Manage Entrants
        viewEntrantsButton.setOnClickListener(view -> {
            if (getView() != null) {
                Bundle bundle = new Bundle();
                if (getArguments() != null) {
                    String eventId = getArguments().getString("EVENT_ID");
                    if (eventId != null) {
                        bundle.putString("EVENT_ID", eventId); // Pass event ID
                        Navigation.findNavController(getView()).navigate(R.id.action_manageEventFragment_to_manageEventEntrantsFragment, bundle);
                    }
                }
            }
        });


        return root;
    }

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
    }


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
            String waitingList = "Waiting List Capacity: " + Objects.requireNonNull(document.get("limited")).toString();
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
                requireActivity().runOnUiThread(() -> bannerImage.setImageBitmap(bitmap));
            } catch (IOException e) {
                Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }

}
