package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Mission> missions = new ArrayList<>();

    private MyDatabaseHelper dbHelper;

    private SwipeRefreshLayout swipeRefreshLayout;

    private MissionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this,"MissionStore.db",null,1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("未完成的任务");
        setSupportActionBar(toolbar);
        initMissions();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MissionAdapter(missions);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("id",-1);
                startActivity(intent);
            }
        });



        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.main_swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.backGroundColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMissions();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.toolbar_toFinished:
                Intent intent2Finished = new Intent(MainActivity.this, FinishedActivity.class);
                startActivity(intent2Finished);
                break;
            case R.id.toolbar_toTimeout:
                Intent intent2Timeout = new Intent(MainActivity.this,TimeoutActivity.class);
                startActivity(intent2Timeout);
                break;
            case R.id.toolbar_toAbout:
                Intent intent2About = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent2About);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int position = adapter.getPosition();
        switch (item.getItemId()){
            case 0:
                // Toast.makeText(this,position+"",Toast.LENGTH_SHORT).show();
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
        Cursor cursor = db.query("Missions",null,"status = ?",new String[]{0+""},null,null,"ddl");
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
