package com.example.employ_events.ui.events;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.AddEventBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
This class handles organizers creating an event, validating the information provided and
storing it into firebase. It generates a QR code when all fields are valid and the event is being
created.

Minor issue is when pressing the add/confirm button to create the event, it may load long enough to double click
the button which crashes the app and adds 2 copies of the event.

US 02.01.01	As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
US 02.01.02	As an organizer I want to store hash data of the generated QR code in my database
US 02.03.01	As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
US 02.04.01	As an organizer I want to upload an event poster to provide visual information to entrants
 */

/**
 * AddEventFragment is a Fragment that allows organizers to create a new event
 * by providing details such as title, description, dates, time, fee, capacity, and location.
 */
public class AddEventFragment extends Fragment {

    private AddEventBinding binding;
    private EditText eventTitleInput, descriptionInput, limitInput, feeInput, eventCapacityInput;
    private CheckBox geoLocation;
    private Button uploadBannerButton, removeBannerButton, saveButton,  registrationStartDeadlineButton, registrationDeadlineButton, eventDateButton;
    private ImageView bannerImageView;
    private Date eventDate, registrationDeadline, registrationStartDeadline;
    private FirebaseFirestore db;
    private String facilityID;
    private Uri bannerUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final String PERMISSION_READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firestore database instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Unique ID retrieval
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        // Initialize the UI components for the fragment
        initializeViews();

        // Initialize the ActivityResultLauncher for requesting permission
        // https://developer.android.com/training/permissions/requesting
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
                    }
                }
        );

        // Fetch the facility ID
        fetchFacilityID(uniqueID);

        registrationStartDeadlineButton.setEnabled(false); // Disable start date button
        registrationDeadlineButton.setEnabled(false); // Disable registration deadline button
        // Set up date picker dialogs
        eventDateButton.setOnClickListener(view -> showDateTimePicker(eventDateButton, "eventDate"));
        registrationDeadlineButton.setOnClickListener(view -> showDateTimePicker(registrationDeadlineButton, "registrationDeadline"));
        registrationStartDeadlineButton.setOnClickListener(view -> showDateTimePicker(registrationStartDeadlineButton, "startDate"));

        // Initially hide the remove button
        removeBannerButton.setVisibility(View.GONE);

        // Set click listener for the upload banner button
        uploadBannerButton.setOnClickListener(v -> pickImage());

        // Set click listener for the remove banner button
        removeBannerButton.setOnClickListener(v -> {
            bannerUri = null; // Clear the banner URI
            bannerImageView.setImageDrawable(null); // Clear the displayed image
            removeBannerButton.setVisibility(View.GONE); // Hide the remove button
        });

        // Save event button click listener
        saveButton.setOnClickListener(view -> {
            if (facilityID == null) {
                Toast.makeText(getContext(), "Facility ID not fetched yet.", Toast.LENGTH_SHORT).show();
                return; // Prevent saving if facility ID is not available
            }

            try {
                String eventTitle = eventTitleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String limitString = limitInput.getText().toString();
                String feeString = feeInput.getText().toString();
                String eventCapacity = eventCapacityInput.getText().toString();

                // Validate inputs
                if (eventTitle.trim().isEmpty()) {
                    eventTitleInput.setError("Event title required");
                    eventTitleInput.requestFocus();
                }
                else if (description.trim().isEmpty()) {
                    descriptionInput.setError("Event description required");
                    descriptionInput.requestFocus();
                }
                else if (eventDate == null) {
                    eventDateButton.setError("Event date required");
                    eventDateButton.requestFocus();
                }
                else if (registrationStartDeadline == null) {
                    registrationStartDeadlineButton.setError("Registration opening date required");
                    registrationStartDeadlineButton.requestFocus();
                }
                else if (registrationDeadline == null) {
                    registrationDeadlineButton.setError("Registration deadline date required");
                    registrationDeadlineButton.requestFocus();
                }
                else if (eventCapacity.trim().isEmpty()) {
                    eventCapacityInput.setError("Event capacity required");
                    eventCapacityInput.requestFocus();
                }
                else if (eventCapacity.trim().equals("0")) {
                    eventCapacityInput.setError("Event capacity cannot be 0");
                    eventCapacityInput.requestFocus();
                }
                else if (!limitString.trim().isEmpty() && limitString.trim().equals("0")) {
                    limitInput.setError("Waiting list capacity cannot be 0");
                    limitInput.requestFocus();
                }
                else {
                    // Generate a unique ID for the event
                    //String id = db.collection("events").document().getId(); // Generate a new ID
                    Event newEvent = new Event();

                    // Set event details
                    //newEvent.setId(id);
                    newEvent.setFacilityID(facilityID);
                    newEvent.setEventTitle(eventTitle);
                    newEvent.setDescription(description);
                    newEvent.setEventDate(eventDate);
                    newEvent.setRegistrationStartDate(registrationStartDeadline);
                    newEvent.setRegistrationDateDeadline(registrationDeadline);
                    newEvent.setEventCapacity(Integer.valueOf(eventCapacity));
                    newEvent.setLimited(limitString.isEmpty() ? null : Integer.valueOf(limitString));
                    newEvent.setFee(feeString.isEmpty() ? null : Integer.valueOf(feeString));
                    newEvent.setGeoLocation(geoLocation.isChecked());
                    // Upload banner and/or save event
                    if (bannerUri != null) {
                        uploadBannerAndSaveEvent(newEvent, view);
                    } else {
                        saveEvent(newEvent, view);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error creating event!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /**
     * Initializes the views for the fragment.
     */
    private void initializeViews() {
        eventTitleInput = binding.eventTitle;
        descriptionInput = binding.description;
        limitInput = binding.limit;
        feeInput = binding.feeText;
        eventCapacityInput = binding.eventCapacity;
        eventDateButton = binding.eventDate;
        registrationDeadlineButton = binding.registrationDateDeadline;
        registrationStartDeadlineButton = binding.registrationStartDeadline;
        saveButton = binding.saveEventButton;
        geoLocation = binding.geolocationStatus;
        uploadBannerButton = binding.uploadBannerButton;
        bannerImageView = binding.bannerImage;
        removeBannerButton = binding.removeBannerButton;
    }

    /**
     * Displays a DatePicker and TimePicker for selecting event dates and times.
     * @param button The button that triggers the date and time picker.
     * @param filter The type of date being set (event date, registration start date, or registration deadline).
     */
    private void showDateTimePicker(Button button, String filter) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Step 1: Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // After the date is selected, show TimePickerDialog
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(),
                            (timeView, selectedHour, selectedMinute) -> {
                                // Set the hour and minute after selecting the time
                                selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                                selectedDate.set(Calendar.MINUTE, selectedMinute);

                                // Format the combined date and time
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                String formattedDateTime = sdf.format(selectedDate.getTime());
                                button.setText(formattedDateTime);

                                // Save the combined date and time into respective variables
                                if (filter.equals("eventDate")) {
                                    eventDate = selectedDate.getTime();
                                    registrationStartDeadlineButton.setEnabled(true);  // Enable Registration Start button
                                } else if (filter.equals("startDate")) {
                                    registrationStartDeadline = selectedDate.getTime();
                                    registrationDeadlineButton.setEnabled(true);  // Enable Registration Deadline button
                                } else {
                                    registrationDeadline = selectedDate.getTime();
                                }
                            },
                            hour, minute, true // Use 24-hour format
                    );
                    timePickerDialog.show();
                },
                year, month, day
        );

        // Set the minimum and maximum dates based on filter type
        if (filter.equals("eventDate")) {
            // Ensure the current date is being used for event date
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        } else if (filter.equals("startDate")) {
            // Ensure registration start date is no earlier than today
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            if (eventDate != null) {
                // Ensure registration start date is no later than event date
                datePickerDialog.getDatePicker().setMaxDate(eventDate.getTime());
            }
        } else if (filter.equals("registrationDeadline")) {
            // Ensure registration deadline is between registration start date and event date
            if (registrationStartDeadline != null && eventDate != null) {
                datePickerDialog.getDatePicker().setMinDate(registrationStartDeadline.getTime());
                datePickerDialog.getDatePicker().setMaxDate(eventDate.getTime());
            } else {
                Toast.makeText(getContext(), "Please set the registration start date and event date first.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Show the date picker dialog
        datePickerDialog.show();
    }

    /**
     * Retrieves the facility ID associated with the given unique ID.
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

    /**
     * Fetches the facility ID for the current user and stores it in the facilityID variable.
     * @param uniqueID The unique ID of the user.
     */
    public void fetchFacilityID(String uniqueID) {
        getFacilityID(uniqueID, id -> {
            facilityID = id; // Store the facility ID
        });
    }

    /**
     * Callback interface for fetching facility ID.
     */
    public interface OnFacilityIDFetchedListener {
        void onFacilityIDFetched(String facilityID);
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
     * Uploads the event banner to Firebase Storage and saves the event details in Firestore.
     * @param newEvent The Event object containing event details.
     * @param view The view from which the operation is triggered.
     */
    private void uploadBannerAndSaveEvent(Event newEvent, View view) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("banners/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(bannerUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    newEvent.setBannerUri(uri.toString());
                    saveEvent(newEvent, view);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading banner!", Toast.LENGTH_SHORT).show());

    }

    /**
     * Saves the event details to Firestore.
     * @param newEvent The Event object to be saved.
     * @param view The view from which the operation is triggered.
     */
    private void saveEvent(Event newEvent, View view) {
        db.collection("events").add(newEvent)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Event Created Successfully", Toast.LENGTH_SHORT).show();

                    String theID = documentReference.getId();
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", theID);
                    db.collection("events").document(theID).set(data, SetOptions.merge());
                    createEntrantListSubcollection(documentReference);
                    try {
                        Bitmap qrCodeBitmap = makeQRBitmap(theID);
                        uploadAndSaveQR(qrCodeBitmap, theID);

                    } catch (WriterException e) {
                        throw new RuntimeException(e);
                    }
                    NavController navController = Navigation.findNavController(view);
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.manageEventFragment, false) // Clears stack up to ManageEventFragment
                            .build();
                    navController.navigate(R.id.action_addEventFragment_to_eventListFragment, null, navOptions);


                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving event!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Prepares an entrantList subcollection under the event document.
     * (No documents are added to it initially.)
     *
     * @param eventDocumentReference The reference to the newly created event document.
     */
    private void createEntrantListSubcollection(DocumentReference eventDocumentReference) {
        // No initial data or document is added here; the subcollection is simply available for future use.

    }

    /*
    QR CODE GENERATION:
    - making a bitmap (pixel image map) to store the qr code image
    - saving the image in the firebase storage
    - storing a url link in the event document in the database so the storage image can be accessed
     */
    /**
     * Generates a QR code bitmap for the given event document ID.
     * @param eventDocumentID The unique identifier for the event.
     * @return A Bitmap object representing the QR code.
     * @throws WriterException If there is an error in generating the QR code.
     */
    private Bitmap makeQRBitmap(String eventDocumentID) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        int size = 300;
        Bitmap bMap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
        BitMatrix bMatrix = writer.encode(eventDocumentID, BarcodeFormat.QR_CODE, size, size);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bMap.setPixel(x, y, bMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bMap;
    }

    /**
     * Uploads the QR code bitmap to Firebase Storage and saves the URL in Firestore.
     * @param bMap The Bitmap object representing the QR code.
     * @param eventDocumentID The unique identifier for the event associated with the QR code.
     */
    private void uploadAndSaveQR(Bitmap bMap, String eventDocumentID) {
        StorageReference storageReference = storage.getReference().child("QRCodes/" + eventDocumentID + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compress the bitmap to PNG format
        boolean compressed = bMap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (!compressed) {
            Toast.makeText(getContext(), "Failed to compress QR code. Couldn't save image!", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] data = baos.toByteArray();
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();

        // Start uploading the QR code image to Firebase Storage
        UploadTask uploadTask = storageReference.putBytes(data, metadata);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadurl = uri.toString();

            // Creating the field the event will have to find the image in the firebase storage
            Map<String, Object> qrData = new HashMap<>();
            qrData.put("QRCodeUrl", downloadurl);
            qrData.put("id", eventDocumentID);

            // Save the QR code URL to the corresponding event document
            db.collection("events").document(eventDocumentID).set(qrData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Your QR Code was succesfully generated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: Couldn't set event data to store qr code url!", Toast.LENGTH_SHORT).show());
            })
        ).addOnFailureListener(e -> Toast.makeText(getContext(), "Error retrieving event qr url!", Toast.LENGTH_SHORT).show());
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
