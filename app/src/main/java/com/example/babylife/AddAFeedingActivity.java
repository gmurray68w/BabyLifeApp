package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babylife.helpers.SQLiteAddAFeedingHelper;
import com.example.babylife.sqlitefiles.SQLiteBabyName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddAFeedingActivity extends AppCompatActivity {

    private Spinner childNameSpinner;
    private  RadioButton rbBreastFeed;
    private RadioButton rbBottleFeed;

    private  Button btnTimeNow;

    private  Button btnSelectFeedDate;

    private  EditText etDurationFeed;
    private EditText etAmountFeed;
    private EditText etNotesFeed;
    private TextView tvInfoFeed;
    private Button btnCancelFeed, btnSaveFeed;
    String selectedDateAndTime;
    RadioGroup rgFeedingType;

    private SQLiteBabyName dbNameHelper;
   private String selectedDateTime;
    private String selectedDate;
    private String selectedTime;
    private TextView tvNameF, tvDateF,tvTimeF,tvTypeF,tvNotesF,tvDurationF,tvAmountF;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_afeeding);

        childNameSpinner = findViewById(R.id.spinnerFeedChildname);
        rbBreastFeed = findViewById(R.id.rbBreastFeed);
        rbBottleFeed = findViewById(R.id.rbBottleFeed);
        btnTimeNow = findViewById(R.id.rbTimeNowFeed);
        btnSelectFeedDate = findViewById(R.id.btnSelectFeedDate);
        etDurationFeed = findViewById(R.id.etDurationFeed);
        etAmountFeed = findViewById(R.id.etAmountFeed);
        etNotesFeed = findViewById(R.id.etNotesFeed);
        tvInfoFeed = findViewById(R.id.tvInfoFeeding);
        btnCancelFeed = findViewById(R.id.btnCancelFeeding);
        btnSaveFeed = findViewById(R.id.btnSaveFeeding);
        rgFeedingType = findViewById(R.id.rgFeedingType);

        //Card code
        tvNameF=findViewById(R.id.tv_feeding_entry_name_F);
        tvDateF=findViewById(R.id.tv_feeding_date_entry_F);
        tvTimeF=findViewById(R.id.tv_feeding_time_entry_F);
        tvTypeF = findViewById(R.id.tv_feeding_type_entry_F);
        tvNotesF = findViewById(R.id.tv_feeding_additional_notes_F);
        tvDurationF = findViewById(R.id.tv_feeding_duration_entry_F);
        tvAmountF = findViewById(R.id.tv_feeding_amount_entry_F);
        
        
        //Set ChildNameSpinner
        //Initialize baby Name
        dbNameHelper = new SQLiteBabyName(this);
        loadBabyNames();
        //Set Radiobutton/group for type
        rgFeedingType.setOnCheckedChangeListener((group, checkedId) ->updateInfoDisplay());
        //Set Radiobutton/group for time
        btnTimeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeToNow();
            }
        });

        //Set Radiobutton/group for date
        btnSelectFeedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDateAndTimePicker();
            }
        });

        // Set Radiobutton/group for duration
        etDurationFeed.addTextChangedListener(new TextWatcher() {
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
        // Set Radiobutton/group for amount
        etAmountFeed.addTextChangedListener(new TextWatcher() {
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
        //Radiobutton/group for notes
        etNotesFeed.addTextChangedListener(new TextWatcher() {
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

    private void setTimeToNow() {
        final Calendar calendarNow = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        selectedDateTime = dateFormat.format(calendarNow.getTime());
        SimpleDateFormat dateNowFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeNowFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectedDate = dateNowFormat.format(calendarNow.getTime());
        selectedTime = timeNowFormat.format(calendarNow.getTime());
        updateInfoDisplay();
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

    private void updateInfoDisplay() {

        String childName = childNameSpinner.getSelectedItem().toString();

        int selectedFeedingType = rgFeedingType.getCheckedRadioButtonId();
        String selectedFeedingTypeString = (selectedFeedingType == R.id.rbBreastFeed) ? "Breast" : "Bottle";

        String userTime = selectedDateTime;
        String duration = etDurationFeed.getText().toString();
        String amount = etAmountFeed.getText().toString();
        String notes = etNotesFeed.getText().toString();

        if (childName.isEmpty() || userTime == null || duration.isEmpty() || amount.isEmpty() || notes.isEmpty()) {
            tvInfoFeed.setText("Please enter all information");
        } else {
            String infoText = "Child: " + childName +
                    "\nFeedingType: " + selectedFeedingTypeString +
                    "\nDate: " + selectedDate +
                    "\nTime: " + selectedTime +
                    "\nDuration: " + duration +
                    "\nAmount: " + amount +
                    "\nNotes: " + notes;
            tvInfoFeed.setText(infoText);
        }

        tvNameF.setText(childName);
        tvTypeF.setText(selectedFeedingTypeString);
        tvDateF.setText(selectedDate);
        tvTimeF.setText(selectedTime);
        tvDurationF.setText(duration);
        tvAmountF.setText(amount);
        tvNotesF.setText(notes);
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
        int selectedFeedingType = rgFeedingType.getCheckedRadioButtonId();
        String selectedFeedingTypeString = (selectedFeedingType == R.id.rbBreastFeed) ? "Breast" : "Bottle";

        String duration = etDurationFeed.getText().toString();
        String amount = etAmountFeed.getText().toString();
        String notes = etNotesFeed.getText().toString();

        // Check if any field is empty or null
        if (childName.isEmpty() || selectedDateTime == null || duration.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please enter all information", Toast.LENGTH_SHORT).show();
        } else {
            // Assuming selectedDateTime is formatted as "yyyy-MM-dd HH:mm"
            String[] dateTimeParts = selectedDateTime.split(" ");
            String date = dateTimeParts[0];
            String time = dateTimeParts[1];

            SQLiteAddAFeedingHelper feeding = new SQLiteAddAFeedingHelper(this);
            feeding.insertFeedingLog("Feeding" ,childName, date, time, selectedFeedingTypeString, duration, amount, notes);
            feeding.logFirstEntry();
            // Clear fields if necessary and return to the previous activity
            clearFields();
            finish(); // Closes the current activity and returns to the previous one
        }


    }

    private void clearFields() {
        // Reset all fields here, e.g.:
        rgFeedingType.clearCheck();
        selectedDateAndTime = "";
        selectedTime = "";
        selectedDate = "";
        etDurationFeed.setText("");
        etAmountFeed.setText("");
        etNotesFeed.setText("");
    }


}