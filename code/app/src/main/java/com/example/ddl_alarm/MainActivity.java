package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Mission> missions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("未完成的任务");
        initMissions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MissionAdapter adapter = new MissionAdapter(missions);
        recyclerView.setAdapter(adapter);
    }

    private void initMissions(){
        missions.add(new Mission("mission 1","","",new Date(2020-1900,12-1,14),1));
        missions.add(new Mission("mission 2","","",new Date(2020-1900,11-1,11),2));
    }
}
