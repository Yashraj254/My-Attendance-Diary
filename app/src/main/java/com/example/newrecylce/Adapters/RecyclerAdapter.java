package com.example.newrecylce.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrecylce.Models.User;
import com.example.newrecylce.R;
import com.example.newrecylce.Room.DbHelper;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<User> userList;
    private onClickListener clickListener;
    private onLongClickListener longClickListener;
    Activity activity;
    private static final String TAG = "RecyclerAdapter";
    DbHelper db;
    Context context;

    public RecyclerAdapter(Activity activity, ArrayList<User> userList, onClickListener clickListener, onLongClickListener longClickListener) {
        db = new DbHelper(context);
        this.activity = activity;
        this.userList = userList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView subject, total, present, absent,percentage;
        private onClickListener clickListener;
        private onLongClickListener longClickListener;
        Context context;

        public MyViewHolder(@NonNull final View view, onClickListener clickListener, onLongClickListener longClickListener) {
            super(view);

            this.clickListener = clickListener;
            this.longClickListener = longClickListener;
            subject = view.findViewById(R.id.subject);
            total = view.findViewById(R.id.total);
            present = view.findViewById(R.id.present);
            absent = view.findViewById(R.id.absent);
            percentage = view.findViewById(R.id.percentage);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onLongClick(getAdapterPosition());
            return true;
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView, clickListener, longClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {

        String subject = userList.get(position).getSubjectName();
        int total = userList.get(position).getTotal();
        int present = userList.get(position).getPresent();
        int absent = userList.get(position).getAbsent();
        int percentage = userList.get(position).getPercentage();

        holder.subject.setText(subject);
        holder.total.setText("Total: " + total);
        holder.present.setText("Present: " + present);
        holder.absent.setText("Absent: " + absent);
        holder.percentage.setText(""+percentage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface onClickListener {
        void onClick(int position);
        //   void onUpdate(int position);
    }

    public interface onLongClickListener {
        boolean onLongClick(int position);
    }

}
