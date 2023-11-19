package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class AddADiaperChange extends AppCompatActivity {
    private Spinner spinnerChildName, spinnerDateTime;
    private RadioGroup radioGroup;
    private TextView tvInfo;
    private Button btnSave, btnCancel;
    private String selectedDateTime;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adiaper_change);

        spinnerChildName = findViewById(R.id.spinnerNameDiaper);
        spinnerDateTime = findViewById(R.id.spinnerDateDiaper);
        radioGroup = findViewById(R.id.radioGroup);


        tvInfo = findViewById(R.id.tvInfoDiaper);
        btnSave = findViewById(R.id.btnSaveDiaper);
        btnCancel = findViewById(R.id.btnCancelDiaper);



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}