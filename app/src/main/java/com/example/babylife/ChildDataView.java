package com.example.babylife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ChildDataView extends AppCompatActivity {

    List<DiaperChangeLogList> diaperLogs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_data_view);

        RecyclerView recyclerView = findViewById(R.id.rv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        setData();

        DiaperChangeAdapter  adapter = new DiaperChangeAdapter(diaperLogs);
        recyclerView.setAdapter(adapter);
    }

    private void setData() {
        diaperLogs.add(new DiaperChangeLogList("Samuel M", "2023-11-19", "08:00 AM", "Wet", "N/A"));
        diaperLogs.add(new DiaperChangeLogList("Samuel M", "2023-11-19", "11:00 AM", "Both", "N/A"));
        diaperLogs.add(new DiaperChangeLogList("Samuel M", "2023-11-19", "03:05 PM", "Poop", "N/A"));

    }
}