package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddAFeedingActivity extends AppCompatActivity {

    private Spinner childNameSpinner;
    private  RadioButton rbBreastFeed;
    private RadioButton rbBottleFeed;

    private  RadioButton rbTimeNow;

    private  Button btnSelectFeedDate;

    private  EditText etDurationFeed;
    private EditText etAmountFeed;
    private EditText etNotesFeed;
    private TextView tvInfoFeed;
    private Button btnCancelFeed, btnSaveFeed;

    private SQLiteBabyName dbNameHelper;
   private String selectedDateTime;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_afeeding);

        childNameSpinner = findViewById(R.id.spinnerFeedChildname);
        rbBreastFeed = findViewById(R.id.rbBreastFeed);
        rbBottleFeed = findViewById(R.id.rbBottleFeed);
        rbTimeNow = findViewById(R.id.rbTimeNowFeed);
        btnSelectFeedDate = findViewById(R.id.btnSelectFeedDate);
        etDurationFeed = findViewById(R.id.etDurationFeed);
        etAmountFeed = findViewById(R.id.etAmountFeed);
        etNotesFeed = findViewById(R.id.etNotesFeed);
        tvInfoFeed = findViewById(R.id.tvInfoFeeding);
        btnCancelFeed = findViewById(R.id.btnCancelFeeding);
        btnSaveFeed = findViewById(R.id.btnSaveFeeding);

        //Set ChildNameSpinner
        //Initialize baby Name
        dbNameHelper = new SQLiteBabyName(this);
        loadBabyNames();
        //TODO:Set Radiobutton/group for type
        //TODO:Set Radiobutton/group for time

        //TODO:Set Radiobutton/group for date
        btnSelectFeedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDateAndTimePicker();
            }
        });

        // TODO:Set Radiobutton/group for duration
        // TODO:Set Radiobutton/group for amount
        //TODO: Radiobutton/group for notes


        //Set Button for save
        btnSaveFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save data
                saveFeedingToDb();
            }
        });
        //Set Button for cancel
        btnCancelFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel
                finish();
            }
        });

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

                    // Time picker dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                selectedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.getTime());
                                updateInfoDisplay();
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void updateInfoDisplay() {
    }



    private void loadBabyNames() {
        List<String> childNames = dbNameHelper.getAllChildNames();// Fetch names from the database
        Log.d("Add Child Name", "Child list size: " + childNames.size());

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childNames);
            childNameSpinner.setAdapter(adapter);

        } else {
        }
    }

    private void saveFeedingToDb() {
        String childName = childNameSpinner.getSelectedItem().toString();
    }

}