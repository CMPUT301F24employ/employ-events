package com.example.employ_events.ui.facility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.employ_events.R;

public class CreateFacilityFragment extends DialogFragment {
    private CreateFacilityDialogListener listener;

    public interface CreateFacilityDialogListener {
        void createFacility(Facility facility, String uniqueID);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateFacilityDialogListener) {
            listener = (CreateFacilityDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateFacilityDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        View view = getLayoutInflater().inflate(R.layout.create_facility, null);
        EditText editFacilityName = view.findViewById(R.id.editFacilityName);
        EditText editFacilityAddress = view.findViewById(R.id.editFacilityAddress);
        EditText editFacilityEmail = view.findViewById(R.id.editFacilityEmail);
        EditText editFacilityPhone = view.findViewById(R.id.editFacilityPhone);

        editFacilityAddress.setClickable(false);
        editFacilityAddress.setFocusable(false);
        editFacilityAddress.setFocusableInTouchMode(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Create Facility")
                .setPositiveButton("Create", null)
                .setNegativeButton("Not Now", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                if (validateInputs(editFacilityName, editFacilityEmail, editFacilityAddress)) {
                    String name = editFacilityName.getText().toString().trim();
                    String address = editFacilityAddress.getText().toString().trim();
                    String email = editFacilityEmail.getText().toString().trim();
                    int phone;
                    if (editFacilityPhone.getText().toString().trim().isEmpty()) {
                        phone = 0;
                    }
                    else {
                        phone = Integer.parseInt(editFacilityPhone.getText().toString().trim());
                    }
                    Facility facility;
                    facility = new Facility(name, email, address, uniqueID, phone);
                    listener.createFacility(facility, uniqueID);
                    Toast.makeText(getActivity(), "Facility Created!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            Button noButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            noButton.setOnClickListener(v -> {
                onCancel(dialog);
                dialog.dismiss();
            });
        });

        return dialog;
    }

    private boolean validateInputs(EditText editFacilityName, EditText editFacilityEmail, EditText editFacilityAddress) {
        if (editFacilityName.getText().toString().trim().isEmpty()) {
            editFacilityName.setError("Facility name cannot be empty");
            editFacilityName.requestFocus();
            return false;
        }
        if (editFacilityEmail.getText().toString().trim().isEmpty()) {
            editFacilityEmail.setError("Facility email cannot be empty");
            editFacilityEmail.requestFocus();
            return false;
        }
        if (editFacilityAddress.getText().toString().trim().isEmpty()) {
            editFacilityAddress.setError("Facility address cannot be empty");
            editFacilityAddress.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        Toast.makeText(getActivity(), "Facility creation canceled. You can start again anytime!", Toast.LENGTH_SHORT).show();
    }
}
