package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babylife.helpers.SQLiteDiaperHelper;
import com.example.babylife.sqlitefiles.SQLiteBabyName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddADiaperChange extends AppCompatActivity {
    private Spinner spinnerChildName, spinnerDateTime;
    private RadioGroup radioGroup;
    private Button btnSelectDate;
    private TextView tvInfo;
    private Button btnSave, btnCancel;
    private String selectedDateTime;
    private EditText babyChangeNotes;
    private SQLiteBabyName dbHelper;
    private SQLiteDiaperHelper diaperChangeDb;
    private TextView tvTimeD, tvDateD, tvNotesD,tvTypeD;
    private String date, time;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adiaper_change);

        spinnerChildName = findViewById(R.id.spinnerNameDiaper);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        radioGroup = findViewById(R.id.radioGroup);
        babyChangeNotes = findViewById(R.id.etBabyChangeNotes);

        tvTimeD = findViewById(R.id.tv_time_entry_diaper);
        tvDateD= findViewById(R.id.tv_date_entry_diaper);
        tvNotesD = findViewById(R.id.tv_additional_notes_diaper);
        tvTypeD = findViewById(R.id.tv_type_entry_diaper);

        tvInfo = findViewById(R.id.tvInfoDiaper);
        btnSave = findViewById(R.id.btnSaveDiaper);
        btnCancel = findViewById(R.id.btnCancelDiaper);
        diaperChangeDb = new SQLiteDiaperHelper(this);
        dbHelper = new SQLiteBabyName(this);
        loadBabyName();

        //Gather user user input information:
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateAndTimePicker();
            }
        });
        //will update with multiple children if needed
        spinnerChildName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update tvInfo when a new child is selected
                updateInfoDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Update tvInfo when diaper type changes
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> updateInfoDisplay());

        babyChangeNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update tvInfo when notes change
                updateInfoDisplay();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Saves info to db
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            saveDiaperChangeToDb();
            }
        });
        //Returns to main activity
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //Saves Name, Type of change, notes, date and time to database
    private void saveDiaperChangeToDb() {
        String childName = spinnerChildName.getSelectedItem().toString();

        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = selectedId != -1 ? findViewById(selectedId) : null;
        String diaperType = radioButton != null ? radioButton.getText().toString() : "";
        String notes = babyChangeNotes.getText().toString();
        if (selectedDateTime != null && !selectedDateTime.isEmpty()) {
            String[] dateTimeParts = selectedDateTime.split(" ");
            date = dateTimeParts[0];
            time = dateTimeParts.length > 1 ? dateTimeParts[1] : "";
            String logType = "Diaper Change";

            diaperChangeDb.insertDiaperChange(logType, childName, date, time, diaperType, notes);

            Toast.makeText(this, "Diaper change saved", Toast.LENGTH_SHORT).show();

            // Optionally, clear the form or navigate away after saving
            clearForm();
            finish();
        } else {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
        }
    }

    //Clears all inputs after user has saved information.
    private void clearForm() {
        radioGroup.clearCheck();
        babyChangeNotes.setText("");
        selectedDateTime = "";
        tvInfo.setText("");
    }

    //This is update the tvInfo at the bottom so you are able to verify data before you submit / save
    private void updateInfoDisplay() {
        String childName = spinnerChildName.getSelectedItem().toString();

        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = selectedId != -1 ? findViewById(selectedId) : null;
        String diaperType = radioButton != null ? radioButton.getText().toString() :

                "Diaper Type Not Selected";
        String notes = babyChangeNotes.getText().toString();

        //String infoText = "Child: " + childName + "\n" +
        //        "Diaper Type: " + diaperType + "\n" +
        //        "Date and Time: " + (selectedDateTime != null ? selectedDateTime : "Not Selected") + "\n" +
        //        "Notes: " + notes;
        //tvInfo.setText(infoText);

        tvDateD.setText(date);
        tvTimeD.setText(time);
        tvTypeD.setText(diaperType);
        tvNotesD.setText(notes);

    }


    //Initially loads baby names from database previously setup
    private void loadBabyName() {
        List<String> childNames = dbHelper.getAllChildNames(); // Fetch names from the database
        Log.d("Add Child Name", "Child list size: " + childNames.size());

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, childNames) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    tv.setTextColor(Color.WHITE); // Set the text color of selected item to white
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    // Change background and text color for drop down items
                    if (position == spinnerChildName.getSelectedItemPosition()) {
                        tv.setBackgroundColor(Color.DKGRAY);
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setBackgroundColor(Color.WHITE);
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            // Customize the spinner layout
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerChildName.setAdapter(adapter);
        }
    }
    //Select date and time of diaper change.
    private void showDateAndTimePicker(){
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


}