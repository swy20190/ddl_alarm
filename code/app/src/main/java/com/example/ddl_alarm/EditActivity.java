package com.example.ddl_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private Uri picUri;

    private EditText title;
    private ImageView pic;
    private EditText content;
    private TextView ddlDate;
    private TextView ddlTime;
    private Button save;

    private String titleString;
    private String base64;
    private String contentString;
    private Date ddl;
    private Calendar mCalendar;
    private int id;

    private int mDate;
    private int mMonth;
    private int mYear;

    private int mHour;
    private int mMinute;

    private MyDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new MyDatabaseHelper(this,"MissionStore.db",null,1);


        mCalendar = Calendar.getInstance();
        mDate = mCalendar.get(Calendar.DATE);
        mMonth = mCalendar.get(Calendar.MONTH)+1;
        mYear = mCalendar.get(Calendar.YEAR);
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

        title = (EditText)findViewById(R.id.edit_title);
        pic = (ImageView)findViewById(R.id.edit_photo);
        content = (EditText)findViewById(R.id.edit_content);
        ddlDate = (TextView)findViewById(R.id.edit_ddl_date);
        ddlTime = (TextView)findViewById(R.id.edit_ddl_time);
        save = (Button)findViewById(R.id.edit_save);

        Intent intent = getIntent();
        titleString = intent.getStringExtra("title");
        base64 = intent.getStringExtra("base64");
        contentString = intent.getStringExtra("content");
        ddl = (Date)intent.getSerializableExtra("ddl");
        id = intent.getIntExtra("id",-1);



        ddlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                ddlDate.setText(year+"/"+month+"/"+dayOfMonth);
                            }
                        },mYear,mMonth,mDate);
                datePickerDialog.show();
            }
        });

        ddlTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                ddlTime.setText(hourOfDay+":"+minute);
                            }
                        },mHour,mMinute,true);
                timePickerDialog.show();
            }
        });

        if(id == -1){
            // new mission
        }
        else{
            title.setText(titleString);
            Bitmap bitmap = null;
            try{
                byte[] bitmapByte = Base64.decode(base64,Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
            }catch(Exception e){
                e.printStackTrace();
            }
            pic.setImageBitmap(bitmap);

            content.setText(contentString);

            ddlDate.setText(ddl.getYear()+1900+"/"+(ddl.getMonth()+1)+"/"+ddl.getDate());
            ddlTime.setText(ddl.getHours()+" : "+ddl.getMinutes());

        }
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id == -1){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String insertTitle = title.getText().toString();
                    Bitmap insertBitmap = ((BitmapDrawable)((ImageView) pic).getDrawable()).getBitmap();
                    String insertBase64 = bitmapToBase64(insertBitmap);
                    if(insertBase64 == null){
                        insertBase64 = "";
                    }
                    String insertDDL = ddlDate.getText().toString().replace('/','.')
                            + "."
                            + ddlTime.getText().toString().replace(':','.');
                    String insertContent = content.getText().toString();
                    ContentValues values = new ContentValues();
                    values.put("title",insertTitle);
                    values.put("base64",insertBase64);
                    values.put("content",insertContent);
                    values.put("ddl",insertDDL);

                    int insertStatus = 0;
                    if(compareTime(insertDDL)){
                        insertStatus = 0;
                    }else{
                        insertStatus = 1;
                    }
                    values.put("status",insertStatus);
                    db.insert("Missions",null,values);
                    values.clear();



                }
                else{
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String updateTitle = title.getText().toString();
                    Bitmap updateBitmap = ((BitmapDrawable)((ImageView) pic).getDrawable()).getBitmap();
                    String updateBase64 = bitmapToBase64(updateBitmap);
                    if(updateBase64 == null){
                        updateBase64 = "";
                    }
                    String updateDDL = ddlDate.getText().toString().replace('/','.')
                            + "."
                            + ddlTime.getText().toString().replace(':','.');
                    String updateContent = content.getText().toString();
                    ContentValues values = new ContentValues();
                    values.put("title",updateTitle);
                    values.put("base64",updateBase64);
                    values.put("content",updateContent);
                    values.put("ddl",updateDDL);

                    int updateStatus = 0;
                    if(compareTime(updateDDL)){
                        updateStatus = 0;
                    }else{
                        updateStatus = 1;
                    }
                    values.put("status",updateStatus);
                    db.update("Missions",values,"id = ?",new String[]{id+""});
                    values.clear();

                }
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap){
        String result = null;
        ByteArrayOutputStream baos = null;
        try{
            if(bitmap!=null){
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(baos!=null){
                    baos.flush();
                    baos.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean compareTime(String ddl){
        Calendar ddlCalendar = Calendar.getInstance();
        String[] splitDDL = ddl.split("\\.");
        ddlCalendar = new GregorianCalendar(Integer.parseInt(splitDDL[0]),Integer.parseInt(splitDDL[1])-1,
                Integer.parseInt(splitDDL[2]),Integer.parseInt(splitDDL[3]),Integer.parseInt(splitDDL[4]));
        Calendar now = Calendar.getInstance();
        return now.getTimeInMillis() < ddlCalendar.getTimeInMillis();

    }



    private void setUpDialog(){
        final String[] items = {"拍照","相册"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                    try{
                        if(outputImage.exists()){
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(Build.VERSION.SDK_INT>=24){
                        picUri = FileProvider.getUriForFile(EditActivity.this,
                                "com.example.ddl_alarm.fileprovider",outputImage);
                    }else{
                        picUri = Uri.fromFile(outputImage);
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,picUri);
                    startActivityForResult(intent, TAKE_PHOTO);
                }else if(which == 1){
                    if(ContextCompat.checkSelfPermission(EditActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(EditActivity.this, new
                                String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        openAlbum();
                    }
                }
            }
        });
        listDialog.show();
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(picUri)
                        );
                        pic.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //document 类型 uri,使用 document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap = getBitmapFromUri(this, getImageContentUri(this, imagePath));
            pic.setImageBitmap(bitmap);
        }
    }

    public static Uri getImageContentUri(Context context, String path){
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},MediaStore.Images.Media.DATA+"=?",
                new String[]{path},null);
        if(cursor!=null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri,""+id);
        }else{
            if(new File(path).exists()){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }else{
                return null;
            }
        }
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri){
        try{
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri,"r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
