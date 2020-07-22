package com.example.notes;

import android.app.Application;

import androidx.room.Room;

import com.example.notes.data.AppDatabase;
import com.example.notes.data.NoteDao;
import com.example.notes.keystore.Keystore;
import com.example.notes.keystore.SaltKeystore;

public class App extends Application {

    private AppDatabase database;
    private NoteDao noteDao;
    private Keystore keystore;

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

        // keystore
        keystore = new SaltKeystore(this);
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public Keystore getKeystore() { return keystore; }
}
