package com.example.employ_events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NewProfileFragment extends DialogFragment {
    private String android_id;
    private NewProfileDialogListener listener;

    interface NewProfileDialogListener {
        void addProfile(Profile profile);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NewProfileDialogListener) {
            listener = (NewProfileDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement NewProfileDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        android_id = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        View view = getLayoutInflater().inflate(R.layout.provide_info, null);
        EditText edit_name = view.findViewById(R.id.editName);
        EditText edit_email = view.findViewById(R.id.editEmailAddress);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setView(view)
                .setTitle("Name and Email Required")
                .setPositiveButton("Confirm", null); // Initially set to null

        // Create the dialog
        AlertDialog dialog = builder.create();

        // User must enter name and email.
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Set up the positive button after the dialog is shown
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String name = edit_name.getText().toString().trim();
                String email = edit_email.getText().toString().trim();

                if (name.isEmpty()) {
                    edit_name.setError("Name cannot be empty");
                    edit_name.requestFocus();
                } else if (email.isEmpty()) {
                    edit_email.setError("Email cannot be empty");
                    edit_email.requestFocus();
                } else {
                    listener.addProfile(new Profile(android_id, name, email));
                    dialog.dismiss(); // Dismiss the dialog if input is valid
                }
            });
        });

        return dialog;
    }

}


