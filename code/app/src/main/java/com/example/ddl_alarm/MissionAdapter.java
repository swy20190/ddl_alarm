package com.example.ddl_alarm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {
    private ArrayList<Mission> mMissionList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View missionView;
        CircleImageView missionPic;
        TextView title;
        TextView ddl;
        TextView time2ddl;

        public ViewHolder(View view){
            super(view);
            missionView = view;
            missionPic = (CircleImageView) view.findViewById(R.id.mission_item_pic);
            title = (TextView) view.findViewById(R.id.mission_item_title);
            ddl = (TextView) view.findViewById(R.id.mission_time_ddl);
            time2ddl = (TextView) view.findViewById(R.id.mission_item_time2DDL);
        }
    }

    public MissionAdapter(ArrayList<Mission> missions){
        this.mMissionList = missions;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mission_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.missionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Mission currentMission = mMissionList.get(position);
                Intent intent = new Intent(v.getContext(),DetailActivity.class);
                intent.putExtra("title",currentMission.getTitle());
                intent.putExtra("id",currentMission.getId());
                intent.putExtra("content",currentMission.getContent());
                intent.putExtra("base64",currentMission.getBase64());
                intent.putExtra("ddl",currentMission.getDdl());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
        Mission mission = mMissionList.get(position);
        holder.title.setText(mission.getTitle());
        holder.ddl.setText("ddl: "+mission.getDdl().toString());
        Date now = new Date();
        Date ddl = mission.getDdl();
        long duration = ddl.getTime()-now.getTime();
        CountDownTimer timer = new CountDownTimer(duration, 60000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalMinute = millisUntilFinished/60000;
                long showMinute = totalMinute % 60;
                long totalHour = totalMinute/60;
                long showHour = totalHour % 60;
                long showDay = totalHour/24;
                holder.time2ddl.setText("距ddl还有"+showDay+"天 "+showHour+"时 "+showMinute+"分");
            }

            @Override
            public void onFinish() {
                holder.time2ddl.setText("已超时");
                //TODO update database
            }
        };
        timer.start();
        Bitmap bitmap = null;
        holder.missionPic.setImageResource(R.mipmap.ic_launcher);
        try{
            byte[] bitmapByte = Base64.decode(mission.getBase64(),Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
            holder.missionPic.setImageBitmap(bitmap);
        }catch (Exception e){
            holder.missionPic.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount(){
        return mMissionList.size();
    }

}
