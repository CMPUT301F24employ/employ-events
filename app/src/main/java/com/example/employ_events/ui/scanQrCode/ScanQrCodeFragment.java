package com.example.employ_events.ui.scanQrCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/*
Purpose is to handle the scanning of an event qr code that brings the entrant
to event details page.
US 01.06.02	As an entrant I want to be able to be sign up for an event by scanning the QR code
US 01.06.01	As an entrant I want to view event details within the app by scanning the promotional QR code
 */
/**
 * Fragment to let entrants scan a qr code to view event details.
 */
public class ScanQrCodeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CollectionReference eventsRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Launcher to handle QR code scan results and process the scanned data
     */
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), qrCodeResult -> {
        if (qrCodeResult.getData() != null && qrCodeResult.getResultCode() == getActivity().RESULT_OK) {
            // qrCodeResult: ActivityResult
            // qrCodeResult.getData(): Intent

            // Turning the activity result into an intent result
            IntentResult result = IntentIntegrator.parseActivityResult(qrCodeResult.getResultCode(), qrCodeResult.getData());

            // Getting the contents of the Intent to use
            String eventID;
            if (result != null && result.getContents() != null) {
                // If QR code retrieved data
                eventID = result.getContents();

                db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // If the event exists in the database, go to event details fragment and fill page with event details from firebase
                        navigateToEventDetailsFrag(eventID);
                    } else {
                        // Else make a toast saying there was an error
                        Toast.makeText(getContext(), "Error: Event not found in database!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // No data from QR code
                Toast.makeText(getContext(), "Error: No data retrieved from QR code!", Toast.LENGTH_SHORT).show();
            }
        }
    });

    /**
     * Inflates the fragment layout and sets up event listeners for UI components.
     *
     * @param inflater           the LayoutInflater object to inflate views
     * @param container          the parent view that contains the fragment's UI
     * @param savedInstanceState the saved instance state for restoring fragment state
     * @return the root view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanQrCodeViewModel scanQrCodeViewModelViewModel =
                new ViewModelProvider(this).get(ScanQrCodeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scanInfo();

        return root;
    }

    /*
    Got code from https://zxing.github.io/zxing/apidocs/com/google/zxing/integration/android/IntentIntegrator.html
    chatgpt: I want to create a scanqrcode fragment in android studio using zxming. In
    the fragment there is a button and I click on it to start scanning
    */

    /**
     * Loads an event's QR code image into the specified ImageView using the event's ID.
     *
     * @param eventID the ID of the event to load
     * @param view    the ImageView where the QR code image will be displayed
     */
    private void loadEvent(String eventID, ImageView view) {
        db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Toast.makeText(getContext(), eventID, Toast.LENGTH_SHORT).show();
                String url = documentSnapshot.getString("QRCodeUrl");
                if (url != null) {
                    Glide.with(this).load(url).into(view);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Didn't work", Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigates to the event details fragment, passing the event ID as an argument.
     *
     * @param eventID the ID of the event to be displayed in the details fragment
     */
    private void navigateToEventDetailsFrag(String eventID) {
        Bundle args = new Bundle();
        args.putString("EVENT_ID", eventID);
        NavHostFragment.findNavController(ScanQrCodeFragment.this).navigate(
                R.id.action_scan_qr_code_to_eventDetailsFragment,
                args,
                new NavOptions.Builder()
                        .setPopUpTo(R.id.scan_qr_code, true) // Remove ScanQrCodeFragment from the back stack
                        .build()
        );
    }

    /**
     * Initiates the QR code scanning process using the IntentIntegrator
     */
    private void scanInfo() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ScanQrCodeFragment.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("Find a code to scan");
        Intent data = integrator.createScanIntent();
        launcher.launch(data);
    }

    /**
     * Cleans up the binding when the fragment's view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}