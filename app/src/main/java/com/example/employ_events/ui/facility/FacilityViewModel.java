package com.example.employ_events.ui.facility;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FacilityViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FacilityViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is facility fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}