package com.example.employ_events.ui.facility;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for managing UI-related data in the Facility Fragment.
 * This class is responsible for providing data to the UI and managing its lifecycle.
 */
public class EditFacilityViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EditFacilityViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}