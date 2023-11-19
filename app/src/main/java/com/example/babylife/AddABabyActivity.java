package com.example.babylife;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

public class AddABabyActivity extends AppCompatActivity {
//Declare variables
    private EditText etChildName;
    private CalendarView cvBirthday;
    private TextView tvChildInfo;
    private Button btnSave;
    private Button btnCancel;

    private String dateChosen;
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
                    ChildInfo childInfo = new ChildInfo(childName, dateChosen);
                    ChildDataListManager.getInstance().addChildInfo(childInfo);
                    // Optionally, you can add a finish() here if you want to close the activity after saving
                    finish();
                }
            }
        });

    }
}