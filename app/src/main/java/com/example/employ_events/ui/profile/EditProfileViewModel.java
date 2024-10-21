package com.example.employ_events.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EditProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is an edit profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}