package com.example.ddl_alarm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TerminatedAdapter extends RecyclerView.Adapter<TerminatedAdapter.ViewHolder> {

    private ArrayList<Mission> missions;

    private int position;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
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
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
            contextMenu.add("删除");
        }

    }
    public TerminatedAdapter(ArrayList<Mission> missions){
        this.missions = missions;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.terminated_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.missionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Mission currentMission = missions.get(position);
                Intent intent = new Intent(v.getContext(),DetailActivity.class);
                intent.putExtra("title",currentMission.getTitle());
                intent.putExtra("id",currentMission.getId());
                intent.putExtra("content",currentMission.getContent());
                // intent.putExtra("base64",currentMission.getBase64());
                intent.putExtra("ddl",currentMission.getDdl());
                intent.putExtra("status",2);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
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

        holder.missionView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getLayoutPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount(){
        return missions.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder){
        holder.missionView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }
}
