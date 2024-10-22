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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employ_events.MainActivity;
import com.example.employ_events.databinding.FragmentHomeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView qrResult;

    // Interprets the data obtained from scanning the QR code
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), qrCodeResult -> {
        if (qrCodeResult.getData() != null && qrCodeResult.getResultCode() == getActivity().RESULT_OK) {
            // qrCodeResult: ActivityResult
            // qrCodeResult.getData(): Intent

            // Turning the activity result into an intent result
            IntentResult result = IntentIntegrator.parseActivityResult(qrCodeResult.getResultCode(), qrCodeResult.getData());

            // Getting the contents of the Intent to use
            if (result != null && result.getContents() != null) {
                qrResult.setText(result.getContents());
            } else {
                qrResult.setText("No Content Found");
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

        Button scanQRCode = binding.scanQrCodeButton;
        scanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanInfo();
            }
        });

        return root;
    }

    private void scanInfo() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
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