package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView pic;
    private TextView content;
    private TextView ddl;
    private FloatingActionButton toEdit;
    private Button finish;

    private int id;
    private String title;
    private String contentString;
    private Date ddlDate;
    private String base64;
    private int status;

    private MyDatabaseHelper dbHelper;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new MyDatabaseHelper(this,"MissionStore.db",null,1);

        final Intent intent = getIntent();
        toolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        pic = (ImageView)findViewById(R.id.detail_pic);
        content = (TextView)findViewById(R.id.detail_content);
        ddl = (TextView)findViewById(R.id.detail_ddl);
        toEdit = (FloatingActionButton) findViewById(R.id.detail_toEdit);
        finish = (Button)findViewById(R.id.detail_finish);

        id = intent.getIntExtra("id",-1);
        Toast.makeText(this,"id: "+id,Toast.LENGTH_SHORT).show();
        title = intent.getStringExtra("title");
        contentString = intent.getStringExtra("content");
        // base64 = intent.getStringExtra("base64");
        status = intent.getIntExtra("status",0);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Missions",null,"id = ?",new String[]{id+""},null,null,null);
        if(cursor.moveToFirst()){
            do{
                base64 = cursor.getString(cursor.getColumnIndex("base64"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        ddlDate = (Date)intent.getSerializableExtra("ddl");


        toolbar.setTitle(intent.getStringExtra("title"));

        Bitmap bitmap = null;
        try{
            byte[] bitmapByte = Base64.decode(base64,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        pic.setImageBitmap(bitmap);

        content.setText(intent.getStringExtra("content"));

        ddl.setText(intent.getSerializableExtra("ddl").toString());

        if(status!=0){
            toEdit.setVisibility(View.GONE);
        }
        toEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNew = new Intent(DetailActivity.this, EditActivity.class);
                intentNew.putExtra("id",id);
                intentNew.putExtra("title",title);
                intentNew.putExtra("content",contentString);
                intentNew.putExtra("ddl",ddlDate);
                startActivity(intentNew);
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("status",2);
                db.update("Missions",values,"id = ?",new String[]{id+""});
                Intent intent = new Intent(DetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
