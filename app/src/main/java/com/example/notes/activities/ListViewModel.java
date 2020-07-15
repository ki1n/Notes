package com.example.notes.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.notes.App;
import com.example.notes.model.Note;

import java.util.List;

public class ListViewModel extends ViewModel {
    private LiveData<List<Note>> noteLiveData = App.getInstance().getNoteDao().getAllLiveDate();

    public LiveData<List<Note>> getNoteLiveData() {
        return noteLiveData;
    }
}
