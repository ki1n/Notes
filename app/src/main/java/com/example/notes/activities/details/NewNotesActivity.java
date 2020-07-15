package com.example.notes.activities.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.notes.App;
import com.example.notes.R;
import com.example.notes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class NewNotesActivity extends AppCompatActivity {
    private static final String EXTRA_NOTE = "NewNotesActivity.EXTRA_NOTE";

    Calendar calendarDateTime = Calendar.getInstance();
    Note note;

    EditText headingNote;
    EditText textNote;
    ImageButton imageButtonCalendar;
    EditText dateTimeTextNote;
    CheckBox timeLimit;

    public static void start(Activity activity, Note note) {
        Intent intent = new Intent(activity, NewNotesActivity.class);
        if (note != null) {
            intent.putExtra(EXTRA_NOTE, note);
        }
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notes);

        Toolbar toolbar = findViewById(R.id.my_toolbar_new_notes);
        setSupportActionBar(toolbar);

        // устанавливаем кнопку назад в appBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(R.string.new_note);
        init();
        checkBoxInit();

        if (getIntent().hasExtra(EXTRA_NOTE)) {
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            headingNote.setText(note.heading);
            textNote.setText(note.text);
            dateTimeTextNote.setText(note.date);
        } else {
            note = new Note();
        }
    }

    private void init() {
        headingNote = findViewById(R.id.et_heading_note);
        textNote = findViewById(R.id.et_text_note);
        imageButtonCalendar = findViewById(R.id.ImB_calendar);
        dateTimeTextNote = findViewById(R.id.et_data_note);
        timeLimit = findViewById(R.id.cb_time);
    }

    // отображаем диалоговое окно для выбора даты и времени
    private void initCalendarButton() {
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(NewNotesActivity.this, time
                        , calendarDateTime.get(Calendar.HOUR_OF_DAY)
                        , calendarDateTime.get(Calendar.MINUTE), true).show();

                new DatePickerDialog(NewNotesActivity.this, date
                        , calendarDateTime.get(Calendar.YEAR)
                        , calendarDateTime.get(Calendar.MONTH),
                        calendarDateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    // если нажат чекбокс то откроем календарь
    private void checkBoxInit() {
        imageButtonCalendar.setEnabled(false);
        dateTimeTextNote.setEnabled(false);
        timeLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imageButtonCalendar.setEnabled(true);
                    dateTimeTextNote.setEnabled(true);
                    setInitialDateTime();
                    initCalendarButton();
                } else {
                    imageButtonCalendar.setEnabled(false);
                    dateTimeTextNote.setEnabled(false);
                    dateTimeTextNote.setText("");
                }
            }
        });
    }

    // установка даты и времени
    private void setInitialDateTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-y, HH:mm");
        dateTimeTextNote.setText(dateFormat.format(calendarDateTime.getTimeInMillis()));
    }

    // установка выбора даты
    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendarDateTime.set(Calendar.YEAR, year);
            calendarDateTime.set(Calendar.MONTH, month);
            calendarDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    // установка выбора времени
    private TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendarDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarDateTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                if (headingNote.getText().length() > 0 | textNote.getText().length() > 0) {
                    note.heading = headingNote.getText().toString();
                    note.text = textNote.getText().toString();
                    note.date = dateTimeTextNote.getText().toString();
                    if (getIntent().hasExtra(EXTRA_NOTE)) {
                        App.getInstance().getNoteDao().update(note);
                    } else {
                        App.getInstance().getNoteDao().insert(note);
                    }
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
