package com.example.babylife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import android.Manifest;
import android.widget.Toolbar;

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
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_NOTIFICATION = 101; // Arbitrary request code
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "baby_notification";
    private Spinner spinnerBabyName;
    private Spinner spinnerDataType;
    private String name, bday;
    private Button  btnAddChild;

    private ImageView btnAddDiaperChange, btnPumpingSession, btnAddSleep, btnAddAFeeding;
    private RecyclerView rvSQLData;
    private SQLDiaperChangeAdapter adapter;
    private FeedingEntryAdapter feedingEntryAdapter;
    private SleepEntryAdapter sleepAdapter;
    private SQLiteDiaperHelper helper;

    private SQLiteAddAFeedingHelper feedingHelper;
    private SQLiteSleepHelper sleepHelper;
    private SQLiteBabyName dbHelper;
    private PumpingSessionHelper pumpingHelper;
    private PumpingSessionAdapter pumpingAdapter;
    private ImageView deleteTable;
    private Context context;
    private MaterialToolbar mTb;
    private ImageButton ibAddChild ,ibNotification;

    private String currentSelectedTable;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if notification is allowed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATION);
                return;
            }
        }



        spinnerDataType = findViewById(R.id.spinnerSelectData);
        btnAddAFeeding = findViewById(R.id.btnBottleFeeding);
        btnAddDiaperChange = findViewById(R.id.btnAddaDiaperChange);
        btnPumpingSession = findViewById(R.id.btnPumpingSession);
        btnAddSleep = findViewById(R.id.btnSleepAdd);
         btnAddChild = findViewById(R.id.btnAddBaby);

        rvSQLData = findViewById(R.id.rv_latest_data);

        deleteTable = findViewById(R.id.ivDeleteTable);
        context = this;
        pumpingAdapter = new PumpingSessionAdapter(this);
        feedingHelper = new SQLiteAddAFeedingHelper(this);
       // sleepHelper = new SQLiteSleepHelper(this);
        dbHelper = new SQLiteBabyName(this);
//Setup Spinner logs
        String[] logSelection = {"Diaper Log", "Feeding Log", "Sleep Log", "Pumping Log"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, logSelection);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDataType.setAdapter(spinnerAdapter);




        mTb = findViewById(R.id.materialToolbar);
        setSupportActionBar(mTb);

        // Set the title for the Toolbar
        getSupportActionBar().setTitle("Baby Life");

        // Create a new LinearLayout to hold the spinner
        LinearLayout rightContainer = new LinearLayout(this);
        rightContainer.setOrientation(LinearLayout.HORIZONTAL);
        Toolbar.LayoutParams layoutParamsContainer = new Toolbar.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // This will wrap the content
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParamsContainer.gravity = Gravity.END; // Align to the right end
        rightContainer.setLayoutParams(layoutParamsContainer);

        // Initialize and configure the spinner
        spinnerBabyName = new Spinner(this);
        dbHelper = new SQLiteBabyName(this);
        updateUIChildData(); // Ensure this method configures the spinner
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.gravity = Gravity.CENTER_VERTICAL;
        rightContainer.addView(spinnerBabyName, spinnerParams);

        // Add the rightContainer to the Toolbar
        mTb.addView(rightContainer);

        // showTestNotification();

        //Setup RV for sqlite data
        rvSQLData.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with context but no cursor yet
        adapter = new SQLDiaperChangeAdapter(this);
        feedingEntryAdapter = new FeedingEntryAdapter(this);

        rvSQLData.setAdapter(feedingEntryAdapter);

        // Initialize the database helper
        helper = new SQLiteDiaperHelper(this);
        //Load the data
        loadFeedingData();


        //Set spinner data logs to change the recyclerview adapter and display different sqlite databases
        spinnerDataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSelectedTable = adapterView.getItemAtPosition(i).toString();
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                if (selectedItem.equals("Feeding Log")) {
                    Log.d("SpinnerDebug", "Setting up Feeding Entry Adapter");
                    feedingEntryAdapter = new FeedingEntryAdapter(context);
                    rvSQLData.setAdapter(feedingEntryAdapter);
                    loadFeedingData();
                    feedingEntryAdapter.notifyDataSetChanged();
                }
                if (selectedItem.equals("Diaper Log")) {
                    // Reset to default adapter for other selections
                    rvSQLData.setAdapter(adapter);
                    loadSQLiteData();
                    adapter.notifyDataSetChanged();
                }
                if(selectedItem.equals("Sleep Log")){
                    sleepHelper = new SQLiteSleepHelper(context);
                    sleepAdapter = new SleepEntryAdapter(context);
                    rvSQLData.setAdapter(sleepAdapter);
                    loadSleepData();
                    sleepAdapter.notifyDataSetChanged();
                }
                if(selectedItem.equals("Pumping Log")){
                    pumpingHelper = new PumpingSessionHelper(context);
                    pumpingAdapter = new PumpingSessionAdapter(context);
                    rvSQLData.setAdapter(pumpingAdapter);
                    loadPumpingData();
                    pumpingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Optional handling if needed
            }
        });


        //Delete the table entries to start over with an empty sqlite table
        deleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentSelectedTable) {
                    case "Feeding Log":
                        // Assuming you have a FeedingLogHelper
                        feedingHelper.deleteAllEntries(FeedingLogContract.FeedingLogEntry.TABLE_NAME);
                        break;
                    case "Diaper Log":
                        helper.deleteAllEntries(DiaperChangeContract.DiaperLogEntry.TABLE_NAME);
                        break;
                    case "Sleep Log":
                        // Assuming you have a SleepLogHelper
                        sleepHelper.deleteAllEntries(SleepSessionContract.SleepLogEntry.TABLE_NAME);
                        break;
                    case "Pumping Log":
                        pumpingHelper.deleteAllEntries(PumpingSessionContract.PumpingLogEntry.TABLE_NAME);
                        break;
                    default:
                        // Handle the default case or show an error
                        Log.e("DeleteAction", "No table selected or unrecognized table.");
                        break;
                }

                // After deletion, reload the appropriate data
                switch (currentSelectedTable) {
                    case "Feeding Log":
                        loadFeedingData();
                        break;
                    case "Diaper Log":
                        loadSQLiteData();
                        break;
                    case "Sleep Log":
                        loadSleepData();
                        break;
                    case "Pumping Log":
                        loadPumpingData();
                        break;
                }

                // Update UI
                updateUIChildData();
            }

        });

        //Setup Button navigation
        btnAddSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddASleepingActivity.class);
                startActivity(i);

            }
        });

        //View charts listener




        //Add a child button
        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddABabyActivity.class);
                startActivity(i);
            }
        });


        //Add pumping session data
        //TODO: ADD LAYOUT AND CONFIGURE TABLES
        btnPumpingSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddAPumpingSessionActivity.class);
                startActivity(i);
            }
        });

        //Add a diaper change entry
        btnAddDiaperChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddADiaperChange.class);
                startActivity(i);
            }
        });

        //Add a diaper change entry
        btnAddAFeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddAFeedingActivity.class);
                startActivity(i);
            }
        });


        //View Charts of data


    }

    private void loadPumpingData() {
        SQLiteDatabase db = pumpingHelper.getReadableDatabase();
        String sortOrder = PumpingSessionContract.PumpingLogEntry.COLUMN_DATE + " DESC";
        Cursor cursor = db.query(PumpingSessionContract.PumpingLogEntry.TABLE_NAME, null, null, null, null, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("MainActivity", "Cursor has " + cursor.getCount() + " entries");
            pumpingAdapter.swapCursor(cursor);
        } else {
            Log.e("MainActivity", "Cursor is empty or null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
            } else {
                // Permission was denied or request was cancelled
            }
        }

        // Handle other permission results
    }

    private void loadSleepData() {
        SQLiteDatabase db = sleepHelper.getReadableDatabase();

        Cursor cursor = db.query(SleepSessionContract.SleepLogEntry.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("MainActivity", "Cursor has " + cursor.getCount() + " entries");
            sleepAdapter.swapCursor(cursor);
        } else {
            Log.e("MainActivity", "Cursor is empty or null");
        }
    }

    private void showTestNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your notification icon
                .setContentTitle("Test Notification")
                .setContentText("This is a test notification.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission check or request code here
                return;
            }
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Method to fetch data for feeding log
    private Cursor fetchDataForFeedingLog() {
// Assuming you have a method in SQLiteAddAFeeding or similar class to fetch feeding data
        SQLiteAddAFeedingHelper dbFeedingHelper = new SQLiteAddAFeedingHelper(context);
        return dbFeedingHelper.fetchFeedingLogs(); // This method should query the database and return a Cursor
    }

    //TO help with the recyclerview adapter and cursors when loading RV.
    private void loadFeedingData() {
        SQLiteDatabase db = feedingHelper.getReadableDatabase();
        Cursor cursor = db.query(FeedingLogContract.FeedingLogEntry.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("MainActivity", "Cursor has " + cursor.getCount() + " entries");
            feedingEntryAdapter.swapCursor(cursor);
        } else {
            Log.e("MainActivity", "Cursor is empty or null");
        }
    }

    //Load the current data from the database
    private void loadSQLiteData() {
        //Loads the sqlite data with a simple query
        //Future this will need to be recreated and changed for more complex queries.
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(DiaperChangeContract.DiaperLogEntry.TABLE_NAME, null, null, null, null, null, null);
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
        loadFeedingData();
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


        btnAddSleep.setVisibility(visibility);

    }

    //on exit
@Override
    protected void onDestroy(){
        super.onDestroy();
        adapter.swapCursor(null);
        helper.close();
}

    // This method will initialize the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    // This method will handle the actions on the toolbar menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MainActivity", "Menu item selected: " + item.getItemId() + ", Name: " + getResources().getResourceEntryName(item.getItemId()));

        Toast.makeText(MainActivity.this, ("Options selected: " + item.getItemId()), Toast.LENGTH_LONG).show();
        Log.d("MainActivity", "Popup menu item clicked: " + item.getTitle());

        // Handle clicks on the popup menu items here
        int id = item.getItemId();
        if(id == R.id.action_bar_notification){
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(i);
        } else if (id == R.id.action_photo_progress) {
            // Handle "Photo Progress" action
            Intent i = new Intent(MainActivity.this, CameraPhotoDisplay.class);
            startActivity(i);
        } else if (id == R.id.action_view_data) {
            Intent i = new Intent(MainActivity.this, ChildDataView.class);
            startActivity(i);
        } else if (id == R.id.action_view_charts) {
            Intent i = new Intent(MainActivity.this, BabyDataChart.class);
            startActivity(i);
        }
        return true;


    }
    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Log to debug the menu item click
                Toast.makeText(MainActivity.this, ("Options selected: " + item.getItemId()), Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Popup menu item clicked: " + item.getTitle());

                // Handle clicks on the popup menu items here
                int id = item.getItemId();
                if(id == R.id.action_bar_notification){
                    Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(i);
                } else if (id == R.id.action_photo_progress) {
                    // Handle "Photo Progress" action
                    Intent i = new Intent(MainActivity.this, CameraPhotoDisplay.class);
                    startActivity(i);
                } else if (id == R.id.action_view_data) {
                    Intent i = new Intent(MainActivity.this, ChildDataView.class);
                    startActivity(i);
                } else if (id == R.id.action_view_charts) {
                    Intent i = new Intent(MainActivity.this, BabyDataChart.class);
                    startActivity(i);
                }
                return true;
            }
        });
        popupMenu.show();
    }
}