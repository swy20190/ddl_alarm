package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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

import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView pic;
    private TextView content;
    private TextView ddl;
    private Button toEdit;

    private long id;
    private String title;
    private String contentString;
    private Date ddlDate;
    private String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        toolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        pic = (ImageView)findViewById(R.id.detail_pic);
        content = (TextView)findViewById(R.id.detail_content);
        ddl = (TextView)findViewById(R.id.detail_ddl);
        toEdit = (Button)findViewById(R.id.detail_toEdit);
        id = intent.getIntExtra("id",-1);
        Toast.makeText(this,"id: "+id,Toast.LENGTH_SHORT).show();
        title = intent.getStringExtra("title");
        contentString = intent.getStringExtra("content");
        base64 = intent.getStringExtra("base64");
        ddlDate = (Date)intent.getSerializableExtra("ddl");


        toolbar.setTitle(intent.getStringExtra("title"));

        Bitmap bitmap = null;
        try{
            byte[] bitmapByte = Base64.decode(intent.getStringExtra("base64"),Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        pic.setImageBitmap(bitmap);

        content.setText(intent.getStringExtra("content"));

        ddl.setText(intent.getSerializableExtra("ddl").toString());

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

    }
}
