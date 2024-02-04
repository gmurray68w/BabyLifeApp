package com.example.babylife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babylife.adapters.DiaperChangeAdapter;
import com.example.babylife.adapters.FeedingEntryAdapter;
import com.example.babylife.adapters.PumpingSessionAdapter;
import com.example.babylife.adapters.SQLDiaperChangeAdapter;
import com.example.babylife.adapters.SleepEntryAdapter;
import com.example.babylife.contracts.DiaperChangeContract;
import com.example.babylife.contracts.FeedingLogContract;
import com.example.babylife.contracts.PumpingSessionContract;
import com.example.babylife.contracts.SleepSessionContract;
import com.example.babylife.helpers.PumpingSessionHelper;
import com.example.babylife.helpers.SQLiteAddAFeedingHelper;
import com.example.babylife.helpers.SQLiteDiaperHelper;
import com.example.babylife.helpers.SQLiteSleepHelper;
import com.example.babylife.sqlitefiles.SQLiteBabyName;

import java.util.ArrayList;
import java.util.List;

public class ChildDataView extends AppCompatActivity {

    private Spinner spinnerBabyName, spinnerDataType;
    private RecyclerView recyclerView;
    private ArrayAdapter<String> spinnerAdapter;
    private String currentSelectedTable;
    private SQLDiaperChangeAdapter diaperAdapter;
    private FeedingEntryAdapter feedingEntryAdapter;
    private SleepEntryAdapter sleepAdapter;
    private PumpingSessionAdapter pumpingAdapter;
    private SQLiteBabyName dbHelper;
    private SQLiteDiaperHelper helper;
    private SQLiteSleepHelper sleepHelper;
    private SQLiteAddAFeedingHelper feedingHelper;
    private PumpingSessionHelper pumpingHelper;

    private String selectedChildName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_data_view);



        recyclerView = findViewById(R.id.rv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new SQLiteBabyName(this);
        helper = new SQLiteDiaperHelper(this);
        sleepHelper = new SQLiteSleepHelper(this);
        feedingHelper = new SQLiteAddAFeedingHelper(this);
        pumpingHelper = new PumpingSessionHelper(this);
        diaperAdapter = new SQLDiaperChangeAdapter(this);

        spinnerBabyName = findViewById(R.id.spinner_name);
        spinnerDataType = findViewById(R.id.spinner_type);
          updateChildNames();

        setupSpinners();
        spinnerBabyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChildName = adapterView.getItemAtPosition(i).toString();
                // Now update the RecyclerView based on the selected child name and data type
                updateRecyclerView(spinnerDataType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerDataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedType = adapterView.getItemAtPosition(i).toString();
                updateRecyclerView(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateChildNames() {
        List<String> childNames = dbHelper.getAllChildNames(); // Fetch names from the database
        Log.d("ChildDataView", "Child list size: " + childNames.size());

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, childNames) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.WHITE); // Set the text color
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the dropdown background color and text color
                    tv.setBackgroundColor(Color.BLACK); // Set your desired background color
                    tv.setTextColor(Color.WHITE); // Set your desired text color
                    return view;
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBabyName.setAdapter(adapter);
        } else {
            // Handle case where no child names are available
        }
    }

    private void setupSpinners() {
        // Populate baby name spinner
        // Populate baby name spinner with customized ArrayAdapter
        List<String> childNames = dbHelper.getAllChildNames();
        ArrayAdapter<String> babyNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, childNames) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE); // Set the text color of spinner item to white
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change background and text color for drop down items
                if (position == spinnerBabyName.getSelectedItemPosition()) {
                    tv.setBackgroundColor(Color.DKGRAY);
                    tv.setTextColor(Color.WHITE);
                } else {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        babyNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBabyName.setAdapter(babyNameAdapter);

        // Populate data type spinner with customized ArrayAdapter
        String[] dataTypes = {"Diaper Log", "Feeding Log", "Sleep Log", "Pumping Log"};
        ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataTypes) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE); // Set the text color of spinner item to white
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Change background and text color for drop down items
                if (position == spinnerDataType.getSelectedItemPosition()) {
                    tv.setBackgroundColor(Color.DKGRAY);
                    tv.setTextColor(Color.WHITE);
                } else {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        dataTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDataType.setAdapter(dataTypeAdapter);
    }

    private void updateRecyclerView(String selectedType) {
        switch (selectedType) {
            case "Diaper Log":
                recyclerView.setAdapter(diaperAdapter);
                selectedChildName = spinnerBabyName.getSelectedItem().toString();

                loadDiaperData(selectedChildName);

                break;
            case "Feeding Log":
                feedingEntryAdapter = new FeedingEntryAdapter(this);
                recyclerView.setAdapter(feedingEntryAdapter);
                loadFeedingData();
                break;
            case "Sleep Log":
                sleepAdapter = new SleepEntryAdapter(this);
                recyclerView.setAdapter(sleepAdapter);
                loadSleepData();
                break;
            case "Pumping Log":
                pumpingAdapter = new PumpingSessionAdapter(this);
                recyclerView.setAdapter(pumpingAdapter);
                loadPumpingData();
                break;
            default:
                break;
        }
    }

    private void loadPumpingData() {
        SQLiteDatabase db = pumpingHelper.getReadableDatabase();

        String selectedChildName = spinnerBabyName.getSelectedItem().toString();


        String selection = PumpingSessionContract.PumpingLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { selectedChildName };
        String sortOrder = PumpingSessionContract.PumpingLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = db.query(
                PumpingSessionContract.PumpingLogEntry.TABLE_NAME,
                null, // You can specify columns here, null will return all columns
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("ChildDataView", "Cursor has " + cursor.getCount() + " entries for " + selectedChildName);
            pumpingAdapter.swapCursor(cursor);
        } else {
            Log.e("ChildDataView", "Cursor is empty or null for " + selectedChildName);
        }
    }

    private void loadSleepData() {
        SQLiteDatabase db = sleepHelper.getReadableDatabase();

        String selectedChildName = spinnerBabyName.getSelectedItem().toString();


        String selection = SleepSessionContract.SleepLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { selectedChildName };
        String sortOrder = SleepSessionContract.SleepLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = db.query(
                SleepSessionContract.SleepLogEntry.TABLE_NAME,
                null, // You can specify columns here, null will return all columns
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        if (cursor != null && cursor.getCount() > 0) {
            Log.d("MainActivity", "Cursor has " + cursor.getCount() + " entries");
            sleepAdapter.swapCursor(cursor);
        } else {
            Log.e("MainActivity", "Cursor is empty or null");
        }
    }

    private void loadFeedingData() {
        SQLiteDatabase db = feedingHelper.getReadableDatabase();
        String selectedChildName = spinnerBabyName.getSelectedItem().toString();


        String selection = FeedingLogContract.FeedingLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { selectedChildName };
        String sortOrder = FeedingLogContract.FeedingLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = db.query(
                FeedingLogContract.FeedingLogEntry.TABLE_NAME,
                null, // You can specify columns here, null will return all columns
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        if (cursor != null && cursor.getCount() > 0) {
            Log.d("MainActivity", "Cursor has " + cursor.getCount() + " entries");
            feedingEntryAdapter.swapCursor(cursor);
        } else {
            Log.e("MainActivity", "Cursor is empty or null");
        }
    }

    private void loadDiaperData(String childName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        selectedChildName = childName;
        Log.d("CHILDDATAVIEW", "Child Name in diaperData: " + childName);

        String selection = DiaperChangeContract.DiaperLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { selectedChildName };
        String sortOrder = DiaperChangeContract.DiaperLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = db.query(
                DiaperChangeContract.DiaperLogEntry.TABLE_NAME,
                null, // You can specify columns here, null will return all columns
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("ChildDataView", "Cursor has " + cursor.getCount() + " entries for " + selectedChildName);
            diaperAdapter.swapCursor(cursor);
        } else {
            Log.e("ChildDataView", "Cursor is empty or null for " + selectedChildName);
        }
    }


}