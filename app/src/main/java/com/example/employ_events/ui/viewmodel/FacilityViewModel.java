package com.example.employ_events.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for managing UI-related data in the Facility Fragment.
 * This class is responsible for providing data to the UI and managing its lifecycle.
 */
public class FacilityViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FacilityViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}