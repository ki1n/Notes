package com.example.notes.keystore;

public interface Keystore {

    void saveNewPin(String pin);

    boolean checkPin(String pin) throws Exception;
}
