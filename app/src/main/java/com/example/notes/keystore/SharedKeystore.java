package com.example.notes.keystore;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.notes.R;

public class SharedKeystore implements Keystore {

    private static final String PIN_SHARED_PREF_NAME = "PIN_Shared_Pref";
    private static final String PIN_KEY = "pinKey";

    private Context context;

    public SharedKeystore(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPref() {
        return context.getSharedPreferences(PIN_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    // Проверяем соответсвтвие введёноого текста паролю, сохранённому в памяти
    @Override
    public boolean checkPin(String enteredPin) {
        String pinCode = getSharedPref().getString(PIN_KEY, "");
        return enteredPin.equals(pinCode);
    }

    @Override
    public void saveNewPin(String pin) {
        if (pin.length() == 4) {
            getSharedPref().edit().putString(PIN_KEY, pin).apply();
            Toast.makeText(context, context.getResources().getString(R.string.password_save), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.password_no_save), Toast.LENGTH_LONG).show();
        }
    }

}