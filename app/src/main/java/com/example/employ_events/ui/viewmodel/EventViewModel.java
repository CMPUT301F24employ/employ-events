package com.example.employ_events.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for managing and providing data related to an event.
 * It holds a MutableLiveData object that contains text data for the event.
 */
public class EventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    /**
     * Initializes the EventViewModel by creating an instance of MutableLiveData.
     */
    public EventViewModel() {
        mText = new MutableLiveData<>();

    }

    /**
     * Returns the LiveData instance containing text data for the event.
     *
     * @return LiveData<String> containing event-related text data
     */
    public LiveData<String> getText() {
        return mText;
    }

}

