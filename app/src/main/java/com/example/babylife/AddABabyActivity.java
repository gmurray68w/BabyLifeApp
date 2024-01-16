package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babylife.sqlitefiles.SQLiteBabyName;

public class AddABabyActivity extends AppCompatActivity {
//Declare variables
    private EditText etChildName;
    private CalendarView cvBirthday;
    private TextView tvChildInfo;
    private Button btnSave;
    private Button btnCancel;

    private String dateChosen;

    private SQLiteBabyName db;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ababy);
        //Locate Variables
        etChildName = findViewById(R.id.etChildName);
        cvBirthday = findViewById(R.id.calendarViewBirthday);
        tvChildInfo = findViewById(R.id.tvChildInfo);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        tvChildInfo.setVisibility(View.INVISIBLE);

        db = new SQLiteBabyName(this);
        etChildName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String childName= etChildName.getText().toString();

            }
        });
        cvBirthday.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                dateChosen = dayOfMonth + "/" + (month + 1) + "/" + year; // Remove 'String' to refer to the member variable
                tvChildInfo.setText("Name: " + etChildName.getText() + "\nBirthday: " + dateChosen);
                tvChildInfo.setVisibility(View.VISIBLE);
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
                String childName = etChildName.getText().toString();
                if (childName != null && !childName.isEmpty() && dateChosen != null && !dateChosen.isEmpty()) {
                    // Save the data to the SQLite database
                    db.addChild(childName, dateChosen);

                    // Optional: Display a message or close the activity
                    Toast.makeText(AddABabyActivity.this, "Child saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddABabyActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}