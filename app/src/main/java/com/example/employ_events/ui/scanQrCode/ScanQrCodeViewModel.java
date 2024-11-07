package com.example.employ_events.ui.scanQrCode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the ScanQrCodeFragment, providing data to be displayed in the home UI.
 */
public class ScanQrCodeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Initializes the ViewModel with default text for the ScanQrCode fragment.
     */
    public ScanQrCodeViewModel() {
        mText = new MutableLiveData<>();
    }

    /**
     * Provides a LiveData object containing the text to be displayed in the ScanQrCode fragment.
     *
     * @return LiveData object containing the ScanQrCode fragment text
     */
    public LiveData<String> getText() {
        return mText;
    }
}