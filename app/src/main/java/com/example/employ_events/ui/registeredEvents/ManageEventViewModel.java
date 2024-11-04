package com.example.employ_events.ui.registeredEvents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ManageEventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ManageEventViewModel() {
        mText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }

}
