package com.example.newrecylce.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newrecylce.Models.Model;
import com.example.newrecylce.R;
import com.example.newrecylce.Room.DbHelper;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Model> userList;
    private onClickListener clickListener;
    private onLongClickListener longClickListener;
    String table_name;
    DbHelper db;
    Context context;
    Model model;
    int status, totalCount,totalPresent,totalAbsent,totalPercentage;

    public MyAdapter(ArrayList<Model> userList, onClickListener clickListener, onLongClickListener longClickListener, Context context, String table_name) {

        this.userList = userList;
        this.context = context;
        this.table_name = table_name;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;

        db = new DbHelper(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView nameText;
        RadioGroup radioGroup;
        RadioButton present, absent;
        LinearLayout mainLayout;
        private MyAdapter.onClickListener clickListener;
        private MyAdapter.onLongClickListener longClickListener;

        public MyViewHolder(@NonNull final View view, onClickListener clickListener, onLongClickListener longClickListener) {
            super(view);
            this.clickListener = clickListener;
            this.longClickListener = longClickListener;

            nameText = view.findViewById(R.id.textView1);
            radioGroup = view.findViewById(R.id.radioStatus);
            present = view.findViewById(R.id.present);
            absent = view.findViewById(R.id.absent);
            mainLayout = view.findViewById(R.id.mainLayout);
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
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items2, parent, false);
        return new MyViewHolder(itemView, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
        final String date = userList.get(position).getDate();
        final int state = userList.get(position).getStatus();
        holder.nameText.setText(date);

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.present:
                        status = 1;
                        db.addDateData(date, status);
                        getData();
                        holder.absent.setEnabled(false);
                        break;
                    case R.id.absent:
                        status = 0;
                        db.addDateData(date, status);
                        getData();
                        holder.present.setEnabled(false);
                        break;
                }
            }
        });
    }
    public void getData()
    {
        totalCount = db.totalCount(table_name);
        totalAbsent = db.getAbsentCount(table_name);
        totalPresent = db.getPresentCount(table_name);
        try{
        totalPercentage = totalPresent * 100/totalCount;
        db.updateData(totalCount, table_name,totalPresent,totalAbsent,totalPercentage);}
        catch (Exception e)
        {
            db.updateData(totalCount, table_name,totalPresent,totalAbsent,totalPercentage);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface onClickListener {
        void onClick(int position);
    }

    public interface onLongClickListener {
        boolean onLongClick(int position);
    }
}