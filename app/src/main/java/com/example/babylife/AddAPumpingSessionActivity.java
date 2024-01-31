package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.babylife.helpers.PumpingSessionHelper;
import com.example.babylife.sqlitefiles.SQLiteBabyName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAPumpingSessionActivity extends AppCompatActivity {

    private Spinner spinBabyName;

    private EditText etDurationAmtPumping, etAmountPumping;
    private Button btnDate, btnSavePumping, btnCancelPumping;
    private RadioButton rbNowTime;

    private String selectedDateTime, selectedTime, selectedDate;
    private SQLiteBabyName dbNameHelper;
    private TextView tvInfoTextPumping;
    private int saveInt, amount;
    private boolean saveBool;
    private  String date;
    private CheckBox cbSave, cbLeft, cbRight;
    private PumpingSessionHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_apumping_session);
        etDurationAmtPumping = findViewById(R.id.etDurationAmtPumping);
        etAmountPumping = findViewById(R.id.etAmountPumping);
        rbNowTime = findViewById(R.id.rbNowTimePumping);
        cbLeft = findViewById(R.id.rbLeft);
        cbRight = findViewById(R.id.rbRight);
        cbSave = findViewById(R.id.switchSave);
        btnSavePumping = findViewById(R.id.btnSavePumping);
        btnCancelPumping = findViewById(R.id.btnCancelPumping);
        tvInfoTextPumping = findViewById(R.id.tvInfoTextPumping);
        btnDate = findViewById(R.id.btnChooseDatePumping);
        spinBabyName = findViewById(R.id.spinner_baby_name_pumping);


        dbNameHelper = new SQLiteBabyName(this);
        dbHelper = new PumpingSessionHelper(this);
        loadBabyNames();
        setTimeNow();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDateAndTimePicker();
            }
        });

        cbSave.setChecked(true);
        cbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfoDisplay();
            }
        });

        etAmountPumping.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
updateInfoDisplay();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etDurationAmtPumping.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
updateInfoDisplay();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSavePumping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePumpingSession();
            }
        });

        btnCancelPumping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle cancel
                finish();
            }
        });
    updateInfoDisplay();

    }

    private void loadBabyNames() {
        List<String> childNames = dbNameHelper.getAllChildNames();// Fetch names from the database
        Log.d("Add Child Name", "Child list size: " + childNames.size());

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childNames);
            spinBabyName.setAdapter(adapter);

        } else {
        }
    }

    private void ShowDateAndTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        // Date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = dateFormat.format(calendar.getTime());

                    // Time picker dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                selectedTime = timeFormat.format(calendar.getTime());
                                updateInfoDisplay();
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    private void savePumpingSession() {
        String name = spinBabyName.getSelectedItem().toString();
        String duration = etDurationAmtPumping.getText().toString();

        // Check if the amount field is not empty
        String amountStr = etAmountPumping.getText().toString();
        if (!amountStr.isEmpty()) {
            amount = Integer.parseInt(amountStr);
        } else {
            amount = 0; // Or set to a default value or handle this case as you see fit
        }

        String side = getSelectedSide();
        saveInt = cbSave.isChecked() ? 1 : 0; // For database
        saveBool = cbSave.isChecked(); // For application logic

        String date;
        if (rbNowTime.isChecked()) {
            // Get current date and time
            setTimeNow();
            date = selectedDateTime;
        } else {
            // Use selected date and time
            date = selectedDate;
        }

        // Insert log entry with total amount calculation
        Log.d("PumpingSession", "Inserting: Name=" + name + ", Date=" + date + ", Duration=" + duration + ", Amount=" + amount + ", Side=" + side + ", Save=" + saveInt);
        dbHelper.insertPumpingLogWithTotal("pumping_log", name, date, amount, duration, side, saveBool);

        // Optionally finish the activity or show a confirmation message
        finish();
    }

    private int getLastEntryId() {
        Pair<Integer, Integer> lastEntryDetails = dbHelper.getLastEntryDetails();
        return lastEntryDetails.first; // Return the ID part of the pair

    }


    private String getSelectedSide() {
        if (cbLeft.isChecked() && cbRight.isChecked()) {
            return "Both";
        } else if (cbLeft.isChecked()) {
            return "Left";
        } else if (cbRight.isChecked()) {
            return "Right";
        } else {
            return "Not Specified";
        }
    }

    private void setTimeNow(){
        final Calendar calendarNow = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        selectedDateTime = dateFormat.format(calendarNow.getTime());
        SimpleDateFormat dateNowFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeNowFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectedDate = dateNowFormat.format(calendarNow.getTime());
        selectedTime = timeNowFormat.format(calendarNow.getTime());

    }

    private void updateInfoDisplay() {
        String name = spinBabyName.getSelectedItem().toString();
        String duration = etDurationAmtPumping.getText().toString();
        String amountStr = etAmountPumping.getText().toString();
        String side = getSelectedSide();
        saveInt = cbSave.isChecked() ? 1 : 0; // For database
        saveBool = cbSave.isChecked(); // For application logic
        amount = 0;
        if (!amountStr.isEmpty()) {
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                Log.e("AddAPumpingSessionActivity", "Invalid number format: " + e.getMessage());
                // Optionally handle the error (e.g., show an error message)
            }
        }
        if (rbNowTime.isChecked()) {
            // Get current date and time
            setTimeNow();
        } else {
            // Use selected date and time
            date = selectedDate;
        }

        // Construct the summary text
        String summary =  "Name" + name + "\n"+
                "Date: " + selectedDate + "\n"+
                "Time: " + selectedTime + "\n"+
                "Duration: " + duration + "mins\n" +
                "Amount: " + amount + " oz\n" +
                "Side: " + side + "\n" +
                "Save: " + (saveBool ? "Yes" : "No");
        tvInfoTextPumping.setText(summary);

    }
}