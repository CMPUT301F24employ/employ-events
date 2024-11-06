package com.example.employ_events.ui.registeredEvents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GeolocationRequiredDialog extends DialogFragment {
    private GeolocationRequiredDialogListener listener;

    public interface GeolocationRequiredDialogListener {
        void onFinishGeolocationRequiredDialog(Boolean proceed);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Warning! Geolocation Required")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    listener.onFinishGeolocationRequiredDialog(false);
                    this.dismiss();
                } )
                .setPositiveButton("Proceed", (dialog, which) -> {
                    listener.onFinishGeolocationRequiredDialog(true);
                    this.dismiss();
                } )
                .create();
    }

    public static String TAG = "JoinEventConfirmationDialog";
}
