package com.example.employ_events.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the HomeFragment, providing data to be displayed in the home UI.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Initializes the ViewModel with default text for the home fragment.
     */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /**
     * Provides a LiveData object containing the text to be displayed in the home fragment.
     *
     * @return LiveData object containing the home fragment text
     */
    public LiveData<String> getText() {
        return mText;
    }
}