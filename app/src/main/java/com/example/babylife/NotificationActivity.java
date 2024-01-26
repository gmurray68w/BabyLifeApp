package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    // Unique identifier for your broadcast receiver action
    private static final String ACTION_NOTIFY = "com.example.babylife.ACTION_NOTIFY";
    private static final String CHANNEL_ID = "baby_notification";
    private static final int NOTIFICATION_ID = 1;
    TextClock tc;
    Button btnSaveNotfication, btnSetTime;

    TextView tvTimeSet, tvNotificationSet, tvDateSet;
    String[] notificationTypes;
    Spinner spinType, spinFrequency;
    //TODO:ADD RECYCLERVIEW AND SETUP THE DELETION OF ENTRIES WHEN SELECTED.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        //Set Variables
        tvTimeSet = findViewById(R.id.tvTimeSet);
        tvNotificationSet = findViewById(R.id.tvNotificationInfo);
        tvDateSet = findViewById(R.id.tvDateSetNotification);
        tc = findViewById(R.id.textClock);
        spinFrequency = findViewById(R.id.spinnerFrequencyNotification);
        spinType = findViewById(R.id.spinnerNotificationType);
        btnSaveNotfication = findViewById(R.id.btnSaveNotification);
        btnSetTime = findViewById(R.id.btnSetNotificationTime);
        String[] notificationTypes = {"Diaper Change", "Sleep", "Feeding", "Pumping"};
        String[] notificationFrequencies = {"2 hours", "6 hours", "12 hours", "24 hours", "weekly"};



//Setup Time Clock
        //TODO FIX if needed
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        final Handler timeHandler = new Handler(Looper.getMainLooper());
        final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                tc.setText(timeFormat.format(new Date())); // set the current time
                timeHandler.postDelayed(this, 1000); // update every second
            }
        };
        timeHandler.post(timeRunnable);

        //Spinner for Notification TYpe
        // Here we are using the default layout directly when initializing the ArrayAdapter
        ArrayAdapter<String> adapterNotificationType = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, notificationTypes);
        spinType.setAdapter(adapterNotificationType);


        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, notificationFrequencies);
        spinFrequency.setAdapter(adapterFrequency);

        // Listener for the spinner item selection
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNotificationInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Can leave this empty
            }
        };
        // Set the listener to both spinners
        spinType.setOnItemSelectedListener(spinnerListener);
        spinFrequency.setOnItemSelectedListener(spinnerListener);

        // Set Time button click listener
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TimePickerDialog code here
                showTimePicker();
            }
        });
        // Save Notification button click listener
        btnSaveNotfication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected items from spinners and time picker
                // Set the notification info into tvNotificationSet TextView
                // Code for setting up the actual notification will go here
                // Get the selected items from spinners and time picker
                String selectedType = spinType.getSelectedItem().toString();
                String selectedFrequency = spinFrequency.getSelectedItem().toString();
                // Show notification immediately
                showNotification(NotificationActivity.this, selectedType, selectedFrequency);
                // Schedule future notifications

                scheduleNotification();
            }
        });


    }
    // Inside NotificationActivity class
    private static void showNotification(Context context, String type, String frequency) {
        // Create an explicit intent for an Activity in your app
        Intent notificationIntent;
        switch (type) {
            case "Feeding":
                notificationIntent = new Intent(context, AddAFeedingActivity.class);
                break;
            // Add cases for other types if needed
            case "Diaper Change":
                notificationIntent = new Intent(context,AddADiaperChange.class);
                break;
            case "Seeping":
                notificationIntent = new Intent(context, AddASleepingActivity.class);
                break;
            //TODO ADD CASES FOR FEEDING AND PUMPING WHEN COMPLETE.
            default:
                notificationIntent = new Intent(context, MainActivity.class);
                break;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        // Set up the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // replace with your notification icon
                .setContentTitle("Time for " + type + "!")
                .setContentText("Frequency: " + frequency )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Handle permission check or request here
                return;
            }
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    private void scheduleNotification() {

        String selectedDate = tvDateSet.getText().toString();
        String selectedTime = tvTimeSet.getText().toString();
        String selectedType = spinType.getSelectedItem().toString();
        String selectedFrequency = spinFrequency.getSelectedItem().toString();

        Intent intent = new Intent(ACTION_NOTIFY);
        intent.putExtra("NOTIFICATION_TYPE", selectedType);
        intent.putExtra("NOTIFICATION_FREQUENCY", selectedFrequency);
        // Parse the date and time to set the calendar object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateFormat.parse(selectedDate + " " + selectedTime);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the error according to your needs
            return;
        }

        // Convert frequency to milliseconds
        long repeatInterval = getRepeatInterval(selectedFrequency);

        // Schedule the alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (repeatInterval > 0) {
                // For repeating alarms
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, pendingIntent);
            } else {
                // For one-time alarm
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private long getRepeatInterval(String frequency) {
        long oneHourInMillis = 3600000; // 1 hour in milliseconds
        switch (frequency) {
            case "2 hours":
                return 2 * oneHourInMillis;
            case "6 hours":
                return 6 * oneHourInMillis;
            case "12 hours":
                return 12 * oneHourInMillis;
            case "24 hours":
                return 24 * oneHourInMillis;
            case "weekly":
                return 7 * 24 * oneHourInMillis;
            default:
                return 0; // No repeat
        }
    }

    private void updateNotificationInfo() {
        String selectedDate = tvDateSet.getText().toString();
        String selectedTime = tvTimeSet.getText().toString();
        String selectedType = spinType.getSelectedItem().toString();
        String selectedFrequency = spinFrequency.getSelectedItem().toString();

        String notificationInfo = "Date: " + selectedDate + "\n" +
                "Time: " + selectedTime + "\n" +
                "Type: " + selectedType + "\n" +
                "Frequency: " + selectedFrequency;

        tvNotificationSet.setText(notificationInfo);
    }


    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        // Time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (timeView, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    // Format and save the time in a separate variable
                    String selectedTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

                    // Update the time display
                    updateTimeDisplay(selectedTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }

    // Update display methods for date and time
    private void updateDateDisplay(String date) {
        // Replace with your actual TextView ID
        tvDateSet.setText(date);
    }

    private void updateTimeDisplay(String time) {
        // Replace with your actual TextView ID
        tvTimeSet.setText(time);
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract the notification type from the intent
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Log.d("BootReceiver", "Device rebooted - rescheduling alarms");
            }
                String type = intent.getStringExtra("NOTIFICATION_TYPE");
            String frequency= intent.getStringExtra("NOTIFICATION_FREQUENCY");
            // Call the static method to show the notification
            NotificationActivity.showNotification(context,type,frequency);
        }
    }

}
