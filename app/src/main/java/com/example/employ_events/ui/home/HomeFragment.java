package com.example.employ_events.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView qrResult;
    private TextView name;
    private CollectionReference eventsRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Interprets the data obtained from scanning the QR code
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), qrCodeResult -> {
        if (qrCodeResult.getData() != null && qrCodeResult.getResultCode() == getActivity().RESULT_OK) {
            // qrCodeResult: ActivityResult
            // qrCodeResult.getData(): Intent

            // Turning the activity result into an intent result
            IntentResult result = IntentIntegrator.parseActivityResult(qrCodeResult.getResultCode(), qrCodeResult.getData());

            // Getting the contents of the Intent to use
            String eventID;
            if (result != null && result.getContents() != null) {
                eventID = result.getContents();

                db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        navigateToEventDetailsFrag(eventID);
                        // The event exists
//                        NavController controller = Navigation.findNavController(getView());
//                        controller.navigate();

                        name.setText(documentSnapshot.getString("name"));
                    } else {
                        name.setText("Didn't work!");
                    }
                });
//                qrResult.setText(result.getContents());
            } else {
                qrResult.setText("No Content Found: Please try again!");
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        qrResult = binding.qrResult;
        name = binding.eventName;

        // BUTTON
        Button scanQRCode = binding.scanQrCodeButton;
        scanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanInfo();
            }
        });

        return root;
    }

    /*
    Got code from https://zxing.github.io/zxing/apidocs/com/google/zxing/integration/android/IntentIntegrator.html
    chatgpt: I want to create a scanqrcode fragment in android studio using zxming. In
    the fragment there is a button and I click on it to start scanning
    */

    private void navigateToEventDetailsFrag(String eventID) {
        Bundle args = new Bundle();
        args.putString("eventData", eventID);
        NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_nav_home_to_entrantEventDetails, args);
    }


    private void scanInfo() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("Find a code to scan");
        Intent data = integrator.createScanIntent();
        launcher.launch(data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}