package com.example.ddl_alarm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TerminatedAdapter extends RecyclerView.Adapter<TerminatedAdapter.ViewHolder> {

    private ArrayList<Mission> missions;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View missionView;
        CircleImageView missionPic;
        TextView title;
        TextView ddl;

        public ViewHolder(View view){
            super(view);
            missionView = view;
            missionPic = (CircleImageView)view.findViewById(R.id.terminated_item_pic);
            title = (TextView)view.findViewById(R.id.terminated_item_title);
            ddl = (TextView)view.findViewById(R.id.terminated_time_ddl);
        }

    }
    public TerminatedAdapter(ArrayList<Mission> missions){
        this.missions = missions;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.terminated_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Mission currentMission = missions.get(position);
        holder.title.setText(currentMission.getTitle());
        holder.ddl.setText("ddl: "+currentMission.getDdl().toString());

        Bitmap bitmap = null;
        holder.missionPic.setImageResource(R.mipmap.ic_launcher);
        try{
            byte[] bitmapByte = Base64.decode(currentMission.getBase64(),Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
            holder.missionPic.setImageBitmap(bitmap);
        }catch (Exception e){
            holder.missionPic.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount(){
        return missions.size();
    }
}
