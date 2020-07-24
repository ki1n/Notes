package com.example.notes.activities.pincode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notes.App;
import com.example.notes.R;
import com.example.notes.activities.ListNotesActivity;
import com.example.notes.activities.settings.SettingsActivity;

public class PinCodeActivity extends AppCompatActivity {
    // класс для накопления числа StringBuffer() - резервирует место под 16 символов без перераспределения памяти
    private StringBuffer inputPin = new StringBuffer();
    private boolean firstPin = true;

    private Button dell;
    private ImageView first;
    private ImageView second;
    private ImageView third;
    private ImageView fourth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);
        // запретим поворот
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        init();
        deleteButton();

        try {
            checkPin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        dell = findViewById(R.id.button_delete);
        first = findViewById(R.id.pinCircleFirst);
        second = findViewById(R.id.pinCircleSecond);
        third = findViewById(R.id.pinCircleThird);
        fourth = findViewById(R.id.pinCircleFourth);
    }

    public void pinClickButtonNumber(View view) throws Exception {
        Button btn = (Button) view;
        if (inputPin.length() <= 3) {
            inputPin.append(btn.getText());
            animationPin(inputPin);

            if (inputPin.length() == 4) {
                if (App.getInstance().getKeystore().checkPin(inputPin.toString())) {
                    Intent intent = new Intent(PinCodeActivity.this, ListNotesActivity.class);
                    startActivity(intent);
                    PinCodeActivity.this.finish();
                } else {
                    Toast.makeText(PinCodeActivity.this, getResources().getString(R.string.invalid_pin_code), Toast.LENGTH_LONG).show();
                    inputPin.setLength(0);
                    animationPin(inputPin);
                }
            }
        }
    }


    private void checkPin() throws Exception {
        if (App.getInstance().getKeystore().checkPin("")) {
            if (firstPin) {
                firstPin = false;
                Intent intent = new Intent(PinCodeActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else {
                finish();
            }
        }
    }

    private void deleteButton() {
        dell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputPin.length() > 0) {
                    inputPin.deleteCharAt(inputPin.length() - 1);
                    animationPin(inputPin);
                }
            }
        });
    }

    // метод изменяет отображение кружков при вводе пароля
    public void animationPin(StringBuffer builder) {
        switch (builder.length()) {
            case 4:
                fourth.setImageResource(R.drawable.circle_full);
                break;
            case 3:
                fourth.setImageResource(R.drawable.circle_empty);
                third.setImageResource(R.drawable.circle_full);
                break;
            case 2:
                second.setImageResource(R.drawable.circle_full);
                third.setImageResource(R.drawable.circle_empty);
                break;
            case 1:
                second.setImageResource(R.drawable.circle_empty);
                first.setImageResource(R.drawable.circle_full);
                break;
            default:
                first.setImageResource(R.drawable.circle_empty);
                second.setImageResource(R.drawable.circle_empty);
                third.setImageResource(R.drawable.circle_empty);
                fourth.setImageResource(R.drawable.circle_empty);
        }
    }
}