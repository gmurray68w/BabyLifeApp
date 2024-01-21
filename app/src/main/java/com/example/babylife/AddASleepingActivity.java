package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.babylife.helpers.SQLiteSleepHelper;
import com.example.babylife.sqlitefiles.SQLiteBabyName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddASleepingActivity extends AppCompatActivity {

    public static String TAG = "SLEEPACTIVITY";
    Context context = this;
    Button btnSelectDate, btnSelectStart, btnSelectEnd, btnCancel, btnSave;

    Spinner spinChildNames;

    EditText etSleepNotes;

    TextView tvSelectedDate, tvSelectedStartTime, tvSelectedEndTime, tvSleepInfo;
    private SQLiteBabyName dbNameHelper;
    String durationStr;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asleeping);

        btnSelectDate = findViewById(R.id.btnSleepSelectDate);
        btnSelectStart = findViewById(R.id.btnSleepSelectStartTime);
        btnSelectEnd = findViewById(R.id.btnSleepSelectEnd);
        btnCancel = findViewById(R.id.btnSleepCancel);
        btnSave = findViewById(R.id.btnSleepSave);

        spinChildNames = findViewById(R.id.spinSleepChildName);

        etSleepNotes = findViewById(R.id.etSleepNotes);

        tvSelectedDate =findViewById(R.id.tvSleepDateSelected);
        tvSelectedStartTime = findViewById(R.id.tvSleepStartTimeText);
        tvSelectedEndTime = findViewById(R.id.tvSleepSelectedEndTIme);
        tvSleepInfo = findViewById(R.id.tvSleepNotesWhole);
        
        //Set ChildName Spiner
        dbNameHelper= new SQLiteBabyName(this);
        loadBabyNames();
        
        
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateForSleep();
            }
        });
        btnSelectStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartTime();
            }
        });
        
        btnSelectEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEndTime();
            }
        });
        etSleepNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateSleepInfo();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDb();
            }
        });
    }

    private void saveToDb() {
        String childName = spinChildNames.getSelectedItem().toString();
        String selectedDate = tvSelectedDate.getText().toString();
        String selectedStartTime=  tvSelectedStartTime.getText().toString();
        String selectedEndTime = tvSelectedEndTime.getText().toString();
        String dur = durationStr.toString();
        String notes = etSleepNotes.getText().toString();
        Log.d(TAG, "SLEEP ENTRY PRIOR");
        Toast.makeText(context, "Prior to add", Toast.LENGTH_LONG).show();
        SQLiteSleepHelper sleep = new SQLiteSleepHelper(this);
        sleep.insertSleepingLog("Sleeping", childName, selectedDate,selectedStartTime,selectedEndTime,dur,notes);
        Log.d(TAG, "SLEEP ENTRY POST");
        Toast.makeText(context, "ADDED", Toast.LENGTH_LONG).show();
        clearFields();
        finish();
    }

    private void clearFields() {
        tvSelectedDate.setText("Date");
        tvSelectedStartTime.setText("Time");
        tvSelectedEndTime.setText("Time");
        tvSleepInfo.setText("");
        etSleepNotes.setText("");
    }

    private void updateSleepInfo() {
        String name = spinChildNames.getSelectedItem().toString();
        String date = tvSelectedDate.getText().toString();
        String startTime = tvSelectedStartTime.getText().toString();
        String endTime = tvSelectedEndTime.getText().toString();
        String notes = etSleepNotes.getText().toString();

        // Calculate duration (ensure positive value)
        // Assuming startTime and endTime format is "HH:mm"
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date start = format.parse(startTime);
            Date end = format.parse(endTime);
            long duration = end.getTime() - start.getTime();
            if (duration < 0) {
                // Handles next day scenario by adding 24 hours to the duration
                duration += 24 * 60 * 60 * 1000;
            }
            long diffHours = duration / (60 * 60 * 1000) % 24;
            long diffMinutes = duration / (60 * 1000) % 60;
            durationStr = String.format(Locale.getDefault(), "%02d:%02d", diffHours, diffMinutes);

            String sleepInfo = "Name = " + name + "\nDate= " + date + "\nStartTime: " + startTime + ",\nEndTime: " + endTime + "\nDuration: " + durationStr + "\nNotes=" + notes;
            tvSleepInfo.setText(sleepInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void selectEndTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        tvSelectedEndTime.setText(selectedTime);
                        updateSleepInfo();
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectStartTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        tvSelectedStartTime.setText(selectedTime);
                        updateSleepInfo();
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void setDateForSleep() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Month is zero-based, add 1 for display
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        tvSelectedDate.setText(selectedDate);
                        updateSleepInfo();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadBabyNames() {
        List<String> childNames = dbNameHelper.getAllChildNames();// Fetch names from the database
        Log.d("Add Child Name", "Child list size: " + childNames.size());

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childNames);
            spinChildNames.setAdapter(adapter);

        } else {
        }
    }
}