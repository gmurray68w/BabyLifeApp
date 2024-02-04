package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.babylife.contracts.FeedingLogContract;
import com.example.babylife.contracts.PumpingSessionContract;
import com.example.babylife.contracts.SleepSessionContract;
import com.example.babylife.helpers.PumpingSessionHelper;
import com.example.babylife.helpers.SQLiteAddAFeedingHelper;
import com.example.babylife.helpers.SQLiteSleepHelper;
import com.example.babylife.sqlitefiles.SQLiteBabyName;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class BabyDataChart extends AppCompatActivity {
    private SQLiteAddAFeedingHelper feedingHelper;
    private PumpingSessionHelper pumpingHelper;
    private SQLiteSleepHelper sleepingHelper;
    private SQLiteBabyName dbNameHelper;
    private ArrayList<Float> data;
    Spinner spinnerChildName, spinnerType, spinnerTypeFocused;
    int materialPurpleColor;

    ArrayAdapter<CharSequence> typeAdapter;
    BarChart barChart;
    private Button btnReturn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_data_chart);
        spinnerChildName=findViewById(R.id.spinnerChartChildName);
        spinnerType=findViewById(R.id.spinnerChartType);
        btnReturn=findViewById(R.id.btnExitCharts);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.log_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        materialPurpleColor = Color.parseColor("#9C27B0"); // Material Design Purple 500

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dbNameHelper= new SQLiteBabyName(this);
        feedingHelper = new SQLiteAddAFeedingHelper(this);
        pumpingHelper = new PumpingSessionHelper(this);
        sleepingHelper = new SQLiteSleepHelper(this);
        //Load child names
        loadBabyNames();

        //Setup BarChart.
        barChart = findViewById(R.id.chart);


         typeAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_item, getResources().getTextArray(R.array.log_types)) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == spinnerType.getSelectedItemPosition()) {
                    // Set the background color and text color for the selected item
                    tv.setBackgroundColor(Color.DKGRAY);
                    tv.setTextColor(Color.WHITE);
                } else {
                    // Set the background color and text color for other items
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Set bar data dependent on child name selected
        ArrayList<Float> feedingData = getFeedingDataFromDB(spinnerChildName.getSelectedItem().toString());
        BarData data = new BarData(getDataSet(feedingData));
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues(feedingData.size())));
        barChart.setData(data);
        barChart.animateXY(2000, 2000);
        barChart.invalidate();



    }

    //Will change chart data when different child name is selected
    private void updateChartData() {
        String selectedChild = spinnerChildName.getSelectedItem().toString();
        String selectedLogType = spinnerType.getSelectedItem().toString();


        if("Feeding Amount".equals(selectedLogType)){
            data = getFeedingDataFromDB(selectedChild);
        }else if ("Sleeping Amount".equals(selectedLogType)){
            data = getSleepingDataFromDB(selectedChild);

        }else if("Pumping Amount".equals(selectedLogType)){
            data = getPumpingDataFromDB(selectedChild);
        }else if("Freezer Stash".equals(selectedLogType)){
            data = getPumpingTotalAmountDataFromDB(selectedChild);
        }
        BarData barData = new BarData(getDataSet(data));
        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getXAxis().setGridColor(Color.WHITE);
        barChart.getAxisLeft().setGridColor(Color.WHITE);
        barChart.getAxisRight().setGridColor(Color.WHITE);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues(data.size())));
        barChart.setData(barData);
        barChart.notifyDataSetChanged(); // Notify the chart of data changes
        barChart.invalidate(); // Refresh the chart
    }

    //Loads baby names from SQLITEBABYNAMES and sets up the listener for the first spinner.
    private void loadBabyNames() {
        List<String> childNames = dbNameHelper.getAllChildNames(); // Fetch names from the database

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, childNames) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    // Change background and text color
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
            spinnerChildName.setAdapter(adapter);

            spinnerChildName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    updateChartData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Do something here
                }
            });
        } else {
            // Handle case where no child names are available
        }
        Log.d("Add Child Name", "Child list size: " + childNames.size());
    }


    //Sets up the bargraph data dependent on the getFeed

    private ArrayList<IBarDataSet> getDataSet(ArrayList<Float> dataSet) {
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        //Loop for amount in feeding data
        for (int i = 0; i < dataSet.size(); i++) {
            // The first parameter should be the index, second parameter should be the value
            valueSet1.add(new BarEntry(i, dataSet.get(i)));
            Log.d("ChartData", "Amount: " + dataSet.get(i));
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, spinnerChildName.getSelectedItem().toString());
        barDataSet1.setColor(materialPurpleColor);

        dataSets.add(barDataSet1);
        return dataSets;
    }

    //Returns an arraylist of the data for the amount that is in the feeding data database
    //QUERIES database for "amount," and returns that in descending order.
    private ArrayList<Float> getFeedingDataFromDB(String childName) {

        ArrayList<Float> feedingData = new ArrayList<>();
        SQLiteDatabase db = feedingHelper.getReadableDatabase();

        String selection = FeedingLogContract.FeedingLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { childName };
        String sortOrder = FeedingLogContract.FeedingLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = db.query(
                FeedingLogContract.FeedingLogEntry.TABLE_NAME,
                new String[] {FeedingLogContract.FeedingLogEntry.COLUMN_AMOUNT},
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        try {
            // Iterate over the cursor to get amounts
            while (cursor.moveToNext()) {
                String amountStr = cursor.getString(cursor.getColumnIndexOrThrow(FeedingLogContract.FeedingLogEntry.COLUMN_AMOUNT));
                try {
                    // Convert the amount string to float and add to the list
                    float amount = Float.parseFloat(amountStr);
                    feedingData.add(amount);
                    Log.d("FeedingData", "Amount parsed: " + amount); // Add this line for debugging

                } catch (NumberFormatException e) {
                    // Handle parse error
                    Log.e("getFeedingDataFromDB", "Error parsing amount to float: " + amountStr, e);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        Log.e("BABYDATA", feedingData.toString());
        return feedingData;
    }

    private ArrayList<Float> getPumpingDataFromDB(String childName) {

        ArrayList<Float> pumpingData = new ArrayList<>();
        SQLiteDatabase dbP = pumpingHelper.getReadableDatabase();

        String selection = PumpingSessionContract.PumpingLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { childName };
        String sortOrder = PumpingSessionContract.PumpingLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = dbP.query(
                PumpingSessionContract.PumpingLogEntry.TABLE_NAME,
                new String[] {PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT},
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        try {
            // Iterate over the cursor to get amounts
            while (cursor.moveToNext()) {
                String amountStr = cursor.getString(cursor.getColumnIndexOrThrow(PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT));
                try {
                    // Convert the amount string to float and add to the list
                    float amount = Float.parseFloat(amountStr);
                    pumpingData.add(amount);
                    Log.d("PumpingData", "Amount parsed: " + amount); // Add this line for debugging

                } catch (NumberFormatException e) {
                    // Handle parse error
                    Log.e("getPumpingDataFromDB", "Error parsing amount to float: " + amountStr, e);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        dbP.close();
        Log.e("BABYPUMPINGDATA", pumpingData.toString());
        return pumpingData;
    }

    private ArrayList<Float> getPumpingTotalAmountDataFromDB(String childName) {

        ArrayList<Float> pumpingDataTotal = new ArrayList<>();
        SQLiteDatabase dbP = pumpingHelper.getReadableDatabase();

        String selection = PumpingSessionContract.PumpingLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { childName };
        String sortOrder = PumpingSessionContract.PumpingLogEntry.COLUMN_DATE + " ASC";

        Cursor cursor = dbP.query(
                PumpingSessionContract.PumpingLogEntry.TABLE_NAME,
                new String[] {PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT},
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        try {
            // Iterate over the cursor to get amounts
            while (cursor.moveToNext()) {
                String amountStr = cursor.getString(cursor.getColumnIndexOrThrow(PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT));
                try {
                    // Convert the amount string to float and add to the list
                    float amount = Float.parseFloat(amountStr);
                    pumpingDataTotal.add(amount);
                    Log.d("PumpingData", "Amount parsed: " + amount); // Add this line for debugging

                } catch (NumberFormatException e) {
                    // Handle parse error
                    Log.e("getPumpingDataFromDB", "Error parsing amount to float: " + amountStr, e);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        dbP.close();
        Log.e("BABYPUMPINGDATA", pumpingDataTotal.toString());
        return pumpingDataTotal;
    }
    private ArrayList<Float> getSleepingDataFromDB(String childName) {

        ArrayList<Float> sleepingData = new ArrayList<>();
        SQLiteDatabase dbS = sleepingHelper.getReadableDatabase();

        String selection = SleepSessionContract.SleepLogEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { childName };
        String sortOrder = SleepSessionContract.SleepLogEntry.COLUMN_DATE + " DESC";

        Cursor cursor = dbS.query(
                SleepSessionContract.SleepLogEntry.TABLE_NAME,
                new String[] {SleepSessionContract.SleepLogEntry.COLUMN_DURATION},
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        try {
            // Iterate over the cursor to get amounts
            while (cursor.moveToNext()) {
                String amountStr = cursor.getString(cursor.getColumnIndexOrThrow(SleepSessionContract.SleepLogEntry.COLUMN_DURATION));
                try {
                    // Convert the amount string to float and add to the list
                    float amount = Float.parseFloat(amountStr);
                    sleepingData.add(amount);
                    Log.d("PumpingData", "Amount parsed: " + amount); // Add this line for debugging

                } catch (NumberFormatException e) {
                    // Handle parse error
                    Log.e("getPumpingDataFromDB", "Error parsing amount to float: " + amountStr, e);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        dbS.close();
        Log.e("BABYPUMPINGDATA", sleepingData.toString());
        return sleepingData;
    }
    //Sets the xAxis values based on each entry that is in current database.
    private ArrayList<String> getXAxisValues(int numberOfEntries) {
        ArrayList<String> xAxis = new ArrayList();
       for(int i = 0; i <numberOfEntries +1; i++){
           xAxis.add("Entry "+ (i +1));
       }
        return xAxis;
    }
}