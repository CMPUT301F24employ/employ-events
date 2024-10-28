package com.example.employ_events.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for managing the data related to editing a user's profile.
 * This class holds the profile text and allows the UI to observe changes.
 */
public class EditProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Initializes the EditProfileViewModel and its LiveData.
     */
    public EditProfileViewModel() {
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