package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.babylife.contracts.FeedingLogContract;
import com.example.babylife.helpers.SQLiteAddAFeedingHelper;
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
    private SQLiteBabyName dbNameHelper;
    Spinner spinnerChildName, spinnerType, spinnerTypeFocused;

    BarChart barChart;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_data_chart);
        spinnerChildName=findViewById(R.id.spinnerChartChildName);
        spinnerType=findViewById(R.id.spinnerChartType);
        spinnerTypeFocused =findViewById(R.id.spinnerChartTypeFocused);

        dbNameHelper= new SQLiteBabyName(this);
        feedingHelper = new SQLiteAddAFeedingHelper(this);

        //Load child names
        loadBabyNames();

        //Setup BarChart.
        barChart = findViewById(R.id.chart);


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
        ArrayList<Float> feedingData = getFeedingDataFromDB(selectedChild);
        BarData data = new BarData(getDataSet(feedingData));
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues(feedingData.size())));
        barChart.setData(data);
        barChart.notifyDataSetChanged(); // Notify the chart of data changes
        barChart.invalidate(); // Refresh the chart
    }

    //Loads baby names from SQLITEBABYNAMES and sets up the listener for the first spinner.
    private void loadBabyNames() {
        List<String> childNames = dbNameHelper.getAllChildNames();// Fetch names from the database

        if (!childNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childNames);
            spinnerChildName.setAdapter(adapter);

            spinnerChildName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    updateChartData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
        }
        Log.d("Add Child Name", "Child list size: " + childNames.size());
    }


    //Sets up the bargraph data dependent on the getFeed
    //TODO: Add 2 other types (SLEEPDATA, DIAPERDATA) and make that return correct fields.
    private ArrayList<IBarDataSet> getDataSet(ArrayList<Float> feedingData) {
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        //Loop for amount in feeding data
        for (int i = 0; i < feedingData.size(); i++) {
            // Use the feedingData that's passed as a parameter
            Log.d("ChartData", "Amount: "+ feedingData.get(i));
            valueSet1.add(new BarEntry(feedingData.get(i), feedingData.get(i)));
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, spinnerChildName.getSelectedItem().toString()); // Replace with actual child's name
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        dataSets.add(barDataSet1);
        return dataSets;
    }

    //Returns an arraylist of the data for the amount that is in the feeding data database
    //QUERIES database for "amount," and returns that in descending order.
    private ArrayList<Float> getFeedingDataFromDB(String childName) {
        //TODO FIX
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


    //Sets the xAxis values based on each entry that is in current database.
    private ArrayList<String> getXAxisValues(int numberOfEntries) {
        ArrayList<String> xAxis = new ArrayList();
       for(int i = 0; i <numberOfEntries +1; i++){
           xAxis.add("Entry "+ (numberOfEntries -i));
       }
        return xAxis;
    }
}