package com.example.notes.keystore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import com.example.notes.R;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SaltKeystore implements Keystore {
    private static final String PIN_SHARED_PREF_NAME = "PIN_Shared_Pref";
    private static final String PIN_KEY = "pinKey";

    private static final int iterations = 20 * 100; // число итераций
    private static final int keyLength = 256;
    private static final int saltLen = 32;

    private Context context;

    public SaltKeystore(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPref() {
        return context.getSharedPreferences(PIN_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveNewPin(String pin) {
        try {
            if (pin.length() == 4) {
                String hashPin = getSaltedHash(pin);
                getSharedPref().edit().putString(PIN_KEY, hashPin).apply();
                Toast.makeText(context, context.getResources().getString(R.string.password_save), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.password_no_save), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkPin(String pin) {
        String salt = getSharedPref().getString(PIN_KEY, "");
        try {
            return check(pin, salt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salt.isEmpty();
    }

    // https://stackoverflow.com/questions/27622625/securerandom-with-nativeprng-vs-sha1prng
    // https://fooobar.com/questions/812405/securerandomgetinstancesha1prng-sun-always-blocking-while-new-securerandom-is-not
    private String getSaltedHash(String pin) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(salt) + "$" + hash(pin, salt);
        }
        return "";
    }

    // Проверим соответствует ли данный обычный текстовый пароль сохраненному в соленый хеш.
    private boolean check(String password, String storedPassword) throws Exception {
        String[] saltAndHash = storedPassword.split("\\$"); // соль и хеш
        if (saltAndHash.length != 2) {
            throw new IllegalAccessException("Сохраненный пароль должен иметь форму 'salt + hash'");
        }
        String hashInput = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hashInput = hash(password, Base64.getDecoder().decode(saltAndHash[0]));
        }
        if (hashInput != null) {
            return hashInput.equals(saltAndHash[1]);
        }
        return false;
    }

    // 1. используя секретный ключ PBKDF2WithHmacSHA1 factory, предоставленный солнцем
    // https://fooobar.com/questions/146750/pbkdf2-with-bouncycastle-in-java
    // https://fooobar.com/questions/15555263/string-to-public-key-using-base64getencoder
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ->
    // https://fooobar.com/questions/14732409/what-is-the-purpose-of-the-condition-if-buildversionsdkint-buildversioncodeso-in-the-firebase-cloud-messaging-sample-project
    // https://www.codota.com/code/java/methods/java.util.Base64$Encoder/encodeToString
    private String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalAccessException("Пустые пароли не поддерживаются");

        SecretKeyFactory sKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = sKeyFactory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(key.getEncoded()); // String для открытого ключа с использованием Base64.getEncoder()
        }
        return "";
    }
}
