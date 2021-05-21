package com.myFirstApp1.AttendanceDiary.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myFirstApp1.AttendanceDiary.MainActivity2;
import com.myFirstApp1.AttendanceDiary.Models.Model;
import com.myFirstApp1.AttendanceDiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class    MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Model> userList;
    String table_name, newTable;
    private static final String TAG = "MyAdapter";
    Context context;
    Model model;
    int totalCount, totalPresent, totalAbsent, totalPercentage;
    String status, date;
    FloatingActionButton fab;
    String detail, userId,id;
    FirebaseDatabase database;
    DatabaseReference ref;
    String subName, key;
    int presents , absents;

    public MyAdapter(ArrayList<Model> userList,
                     Context context, String table_name, FloatingActionButton fab, FirebaseDatabase database, DatabaseReference ref, String subName,
                     int presents, int absents, String userId) {
        this.userList = userList;
        this.context = context;
        this.table_name = table_name;
        this.fab = fab;
        this.database = database;
        this.ref = ref;
        this.subName = subName;
        this.presents = presents;
        this.absents = absents;
        this.userId = userId;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        RadioGroup radioGroup;
        RadioButton present, absent;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull final View view) {
            super(view);
            nameText = view.findViewById(R.id.textView1);
            radioGroup = view.findViewById(R.id.radioStatus);
            present = view.findViewById(R.id.present);
            absent = view.findViewById(R.id.absent);
            mainLayout = view.findViewById(R.id.mainLayout);
        }
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_items, parent, false);
            return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
        date = userList.get(position).getDate();
        final String state = userList.get(position).getStatus();
        id = userList.get(position).getId();

        holder.setIsRecyclable(false);
        holder.nameText.setText(date);
        holder.setIsRecyclable(false);
        holder.radioGroup.setOnCheckedChangeListener(null);
        if (state != null) {
            if (state.equals("Present")) {
                holder.present.setChecked(true);
                holder.absent.setEnabled(false);
            } else if (state.equals("Absent")) {
                holder.absent.setChecked(true);
                holder.present.setEnabled(false);
            }
        }

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.present:
                        status = "Present";
                        presents = presents +   1 ;

                        userList.get(position).setStatus(status);

                        ref.child(userId).child("Subjects").child(subName).child("present").setValue(presents);
                        id =  ref.child(userId).child("Subjects").child(subName).child("Date").push().getKey();
                        model = new Model(date,status,id);
                        ref.child(userId).child("Subjects").child(subName).child("Date").child(id).setValue(model);

                        getData();
                        holder.absent.setEnabled(false);
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.absent:
                        status = "Absent";
                        absents = absents + 1;
                        userList.get(position).setStatus(status);
                        ref.child(userId).child("Subjects").child(subName).child("absent").setValue(absents);
                        id =  ref.child(userId).child("Subjects").child(subName).child("Date").push().getKey();
                        model = new Model(date,status,id);
                        ref.child(userId).child("Subjects").child(subName).child("Date").child(id).setValue(model);
                        getData();
                        holder.present.setEnabled(false);
                        fab.setVisibility(View.VISIBLE);
                        break;
                }
              //  notifyDataSetChanged();
            }
        });
    }

    public void getData() {

        totalCount = presents + absents;
        totalAbsent = absents;
        totalPresent = presents;
        totalPercentage = 0;

        try {
            totalPercentage = totalPresent * 100 / totalCount;
            double requiredAttendance = ((0.75 * totalCount) - totalPresent) / 0.25;
            int attendance = (int) requiredAttendance;
            ref.child(userId).child("Subjects").child(subName).child("percentage").setValue(totalPercentage);


            if (totalPercentage < 75)
                detail = "You have to attend " + attendance + " more classes to maintain 75% attendance";
            else
                detail = "Your attendance is above 75%, Keep up!!!";

            MainActivity2.update_counter(detail);

          //  notifyDataSetChanged();
            //Log.d(TAG, "Total Count: " + totalCount + " Total Present: " + totalPresent + " Total Absent: " + totalAbsent + " Total Attendance:" + attendance);


        } catch (Exception e) {
            Log.d(TAG, "Error generated Table name: " + table_name + " Total Count: " + totalCount + " Total Present: " + totalPresent + " Total Absent: " + totalAbsent);

        }
    }



    @Override
    public int getItemCount() {
        return userList.size();
    }

}
