package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeoutActivity extends AppCompatActivity {

    private ArrayList<Mission> missions = new ArrayList<>();

    private MyDatabaseHelper dbHelper;

    private TerminatedAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);

        dbHelper = new MyDatabaseHelper(this,"MissionStore.db",null,1);
        Toolbar toolbar = (Toolbar)findViewById(R.id.timeout_toolbar);
        toolbar.setTitle("超时未完成的任务");
        initMissions();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.timeout_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TerminatedAdapter(missions);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.timeout_swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.backGroundColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMissions();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int position;
        position = adapter.getPosition();
        switch (item.getItemId()){
            case 0:
                int idDelete = missions.get(position).getId();
                SQLiteDatabase dbDelete = dbHelper.getWritableDatabase();
                dbDelete.delete("Missions","id = ?", new String[]{idDelete+""});
                missions.remove(position);
                refreshMissions();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void initMissions(){
        missions.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Missions",null,"status = ?",new String[]{1+""},null,null,"ddl");
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

    private void refreshMissions(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMissions();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}
