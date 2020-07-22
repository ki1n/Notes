package com.example.notes.activities.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.notes.App;
import com.example.notes.R;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private boolean pinVisible = true;

    private ImageButton imageButtonVisibleInvisible;
    private Button button_save;
    private EditText editText_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.my_toolbar_settings);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // устанавливаем кнопку назад в appBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(getString(R.string.settings));
        init();
        initButtonVisible();
        initButtonSavePin();
    }

    private void initButtonSavePin() {
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = editText_pin.getText().toString(); // считаем пароль
                App.getInstance().getKeystore().saveNewPin(pin); // сохраним пинкод
            }
        });
    }

    // https://fooobar.com/questions/61961/how-to-switch-between-hide-and-view-password
    private void initButtonVisible() {
        imageButtonVisibleInvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinVisible) {
                    imageButtonVisibleInvisible.setImageResource(R.drawable.ic_visibility_on);
                    editText_pin.setTransformationMethod(null);
                } else {
                    imageButtonVisibleInvisible.setImageResource(R.drawable.ic_visibility_off);
                    editText_pin.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

    }

    private void init() {
        imageButtonVisibleInvisible = findViewById(R.id.Ibt_visibility_invisible);
        button_save = findViewById(R.id.bt_save_pin);
        editText_pin = findViewById(R.id.et_new_pin);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}