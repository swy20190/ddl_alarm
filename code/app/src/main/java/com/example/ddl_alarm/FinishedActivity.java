package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FinishedActivity extends AppCompatActivity {

    private ArrayList<Mission> missions = new ArrayList<>();

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);

        dbHelper = new MyDatabaseHelper(this,"MissionStore.db",null,1);
        initMissions();

        Toolbar toolbar = (Toolbar)findViewById(R.id.finished_toolbar);
        toolbar.setTitle("已完成的任务");

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.finished_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TerminatedAdapter adapter = new TerminatedAdapter(missions);
        recyclerView.setAdapter(adapter);

    }

    private void initMissions(){
        missions.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Missions",null,"status = ?",new String[]{2+""},null,null,"ddl");
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String base64 = cursor.getString(cursor.getColumnIndex("base64"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String ddl = cursor.getString(cursor.getColumnIndex("ddl"));
                String[] splitDDL = ddl.split("\\.");


                Calendar ddlCalendar = new GregorianCalendar(Integer.parseInt(splitDDL[0]),Integer.parseInt(splitDDL[1])-1,
                        Integer.parseInt(splitDDL[2]),Integer.parseInt(splitDDL[3]),Integer.parseInt(splitDDL[4]));
                Date ddlDate = ddlCalendar.getTime();

                Mission currentMission = new Mission(title,base64,content,ddlDate,id);
                missions.add(currentMission);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
}
