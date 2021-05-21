package com.myFirstApp1.AttendanceDiary.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myFirstApp1.AttendanceDiary.Models.User;
import com.myFirstApp1.AttendanceDiary.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<User> userList;
    private onClickListener clickListener;
    Activity activity;
    private static final String TAG = "RecyclerAdapter";

    public RecyclerAdapter(Activity activity, ArrayList<User> userList, onClickListener clickListener) {
        this.activity = activity;
        this.userList = userList;
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subject, total, present, absent,percentage;
        ProgressBar percentage_bar;
        private onClickListener clickListener;

        public MyViewHolder(@NonNull final View view, onClickListener clickListener) {
            super(view);

            this.clickListener = clickListener;

            subject = view.findViewById(R.id.subject);
            total = view.findViewById(R.id.total);
            present = view.findViewById(R.id.present);
            absent = view.findViewById(R.id.absent);
            percentage = view.findViewById(R.id.percentage);
            percentage_bar = view.findViewById(R.id.percentage_bar);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_items, parent, false);

        return new MyViewHolder(itemView, clickListener);
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
        holder.percentage.setText(""+percentage+"%");
        holder.percentage_bar.setProgress(percentage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface onClickListener {
        void onClick(int position);
    }

}
