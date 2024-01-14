package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerBabyName;

    private String name, bday;
    private Button btnAddAFeeding, btnAddDiaperChange, btnPumpingSession, btnViewData, btnAddChild,btnViewCharts,
            btnPhotoProgress, btnLogin, btnProfile, btnAddSleep;

    private RecyclerView rvSQLData;
    private SQLChildAdapter adapter;
    private SQLiteHelper helper;
    private SQLiteBabyName dbHelper;
    private ImageView deleteTable;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerBabyName = findViewById(R.id.spinnerBabyName);
        btnAddAFeeding = findViewById(R.id.btnAddAFeeding);
        btnAddDiaperChange = findViewById(R.id.btnAddaDiaperChange);
        btnPumpingSession = findViewById(R.id.btnPumpingSession);
        btnViewData = findViewById(R.id.btnViewData);
        btnViewCharts = findViewById(R.id.btnChart);
        btnAddChild = findViewById(R.id.btnAddBaby);
        btnPhotoProgress = findViewById(R.id.btnPhotoGallery);
        rvSQLData = findViewById(R.id.rv_latest_data);
        btnAddSleep = findViewById(R.id.btnSleepAdd);
        deleteTable = findViewById(R.id.ivDeleteTable);

        //Load names for Children from db
        dbHelper = new SQLiteBabyName(this);
        updateUIChildData();

        //Setup RV for sqlite data
        rvSQLData.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with context but no cursor yet
        adapter = new SQLChildAdapter(this);
        rvSQLData.setAdapter(adapter);

        // Initialize the database helper
        helper = new SQLiteHelper(this);
     //   helper.deleteAllEntries(ChildLogContract.DiaperLogEntry.TABLE_NAME);
        // Populate the data
       // populateSQLiteData();

        //Load the data
        loadSQLiteData();



        deleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.deleteAllEntries(ChildLogContract.DiaperLogEntry.TABLE_NAME);
                loadSQLiteData();
                updateUIChildData();
            }
        });
        //Setup Button navigation
        btnAddSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "More coming soon!", Toast.LENGTH_LONG).show();


            }
        });
        btnViewCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BabyDataChart.class);
            }
        });

        btnPhotoProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CameraPhotoDisplay.class);
                startActivity(i);
            }
        });

        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddABabyActivity.class);
                startActivity(i);
            }
        });
        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ChildDataView.class);
                startActivity(i);
            }
        });
        btnPumpingSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnAddDiaperChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddADiaperChange.class);
                startActivity(i);
            }
        });
        btnAddAFeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "More coming soon!", Toast.LENGTH_LONG).show();
            }
        });
        btnPumpingSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "More coming soon!", Toast.LENGTH_LONG).show();
            }
        });
        btnViewCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BabyDataChart.class);
                startActivity(i);
            }
        });
    }

    //Load the current data from the database
    private void loadSQLiteData() {
        //Loads the sqlite data with a simple query
        //Future this will need to be recreated and changed for more complex queries.
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(ChildLogContract.DiaperLogEntry.TABLE_NAME, null, null, null, null, null, null);
        if (adapter != null) {
            adapter.swapCursor(cursor);
        } else {
            Log.e("MainActivity", "Adapter is null");
        }
    }

    //Removed dummy data
    /* private void populateSQLiteData() {
        //Sets Dummy Data into the SQLITE database
        //initialize
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Add a new row
        ContentValues values = new ContentValues();
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_LOG_TYPE, "Diaper Change");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NAME, "Samuel M");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_DATE, "2023-11-19");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TIME, "10:00");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TYPE, "Wet");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NOTES, "N/A");
        long newRowId = db.insert(ChildLogContract.DiaperLogEntry.TABLE_NAME, null, values);

        //Add additional rows
        ContentValues values1 = new ContentValues();
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_LOG_TYPE, "Diaper Change");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NAME, "Samuel M");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_DATE, "2023-11-19");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TIME, "13:00");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TYPE, "Both");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NOTES, "LOTS");
        long newRowId1 = db.insert(ChildLogContract.DiaperLogEntry.TABLE_NAME, null, values);

        ContentValues values2 = new ContentValues();
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_LOG_TYPE, "Diaper Change");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NAME, "Samuel M");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_DATE, "2023-11-19");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TIME, "18:00");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TYPE, "Poop");
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NOTES, "Normal");
        long newRowId2 = db.insert(ChildLogContract.DiaperLogEntry.TABLE_NAME, null, values);
    }*/

    //When resuming the activity. This updates the information to it's current state includes additions and deletions.
    @Override
    protected void onResume(){
        super.onResume();
        loadSQLiteData();
        updateUIChildData();
    }
    //Retrieves the names from the childnames database and populates the spinner with them
    private void updateUIChildData(){
        List<String> childNames = dbHelper.getAllChildNames(); // Fetch names from the database
        Log.d("MainActivity", "Child list size: " + childNames.size());

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childNames);
            spinnerBabyName.setAdapter(adapter);
            setButtonsVisibility(View.VISIBLE);
        } else {
            setButtonsVisibility(View.INVISIBLE);
        }
    }
    //Updates visibility
    private void setButtonsVisibility(int visibility) {
        btnAddAFeeding.setVisibility(visibility);
        btnAddDiaperChange.setVisibility(visibility);
        btnPumpingSession.setVisibility(visibility);
        btnViewData.setVisibility(visibility);
        btnPhotoProgress.setVisibility(visibility);
        btnAddSleep.setVisibility(visibility);
        btnViewCharts.setVisibility(visibility);
    }

    //on exit
@Override
    protected void onDestroy(){
        super.onDestroy();
        adapter.swapCursor(null);
        helper.close();
}

}