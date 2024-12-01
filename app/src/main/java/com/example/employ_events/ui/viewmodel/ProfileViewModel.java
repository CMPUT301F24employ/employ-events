package com.example.employ_events.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for managing the profile data.
 * This class holds the data related to the user's profile and provides it to the UI.
 */
public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> mText;


    /**
     * Initializes the ProfileViewModel and its LiveData.
     */
    public ProfileViewModel() {
        mText = new MutableLiveData<>();
    }

    /**
     * Returns the LiveData object containing the profile text.
     * @return a LiveData object that can be observed for changes
     */
    public LiveData<String> getText() {
        return mText;
    }

}