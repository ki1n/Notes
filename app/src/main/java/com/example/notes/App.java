package com.example.notes;

import android.app.Application;

import androidx.room.Room;

import com.example.notes.data.AppDatabase;
import com.example.notes.data.NoteDao;

public class App extends Application {

    private AppDatabase database;
    private NoteDao noteDao;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // создание базы данных
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db-Notes")
                .allowMainThreadQueries()
                .build();

        // получаем обект
        noteDao = database.noteDao();
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }
}
