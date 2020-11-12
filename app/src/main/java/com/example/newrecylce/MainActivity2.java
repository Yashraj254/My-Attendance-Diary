package com.example.newrecylce;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrecylce.Adapters.MyAdapter;
import com.example.newrecylce.Models.Model;
import com.example.newrecylce.Room.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity2 extends AppCompatActivity implements MyAdapter.onClickListener, MyAdapter.onLongClickListener {

    private static final String TAG = "Main Activity2";
    Model model;
    String date, table_name, detail, newTable;
    private RadioGroup radioGroup;
    private RadioButton present, absent;
    Intent intent;
    ArrayList<Model> userList;
    RecyclerView recyclerView;
    MyAdapter adapter;
    DbHelper db;
    Toolbar toolbar;
    int item, totalCount, totalPresent, totalAbsent, totalPercentage;
    FloatingActionButton fab;
    ImageButton backArrow;
    TextView subjectView, detailedView;

    public void onCreate(Bundle savedInstancedState) {
        super.onCreate(savedInstancedState);
        setContentView(R.layout.date_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.toolbar_back_arrow);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.floatingActionButton);
        subjectView = findViewById(R.id.subjectView);
        detailedView = findViewById(R.id.detailView);

        db = new DbHelper(this);
        userList = new ArrayList<>();
        show();

        table_name = getIntent().getStringExtra("Send");
        newTable = table_name;
        table_name = newTable.replaceAll("[^a-zA-Z0-9]", "");

        setAdapter();

        subjectView.setText(newTable);
        getData();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity2.this, MainActivity.class));
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog pickerDialog = new DatePickerDialog(MainActivity2.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date = dayOfMonth + "/" + month + "/" + year;
                        Model matched = userList.stream().filter(model -> model.getDate().equals(date))
                                .findFirst().orElse(null); // first occurrence
                        Log.d(TAG, "User list : " + userList.stream());
                        if (matched == null) {
                            model = new Model(date);
                            userList.add(model);
                            Log.d(TAG, "onClick: " + date + " has been added to " + table_name);
                            getData();
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't be added", Toast.LENGTH_LONG).show();
                        }
                    }
                }, year, month, day);
                pickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity2.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

    public void setAdapter() {
        adapter = new MyAdapter(userList, this, this, this, newTable);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        SpacingDecorator decorator = new SpacingDecorator(10);
        recyclerView.addItemDecoration(decorator);
        recyclerView.setAdapter(adapter);
    }

    public void show() {
        Cursor cursor = db.display();
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity2.this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while ((cursor.moveToNext())) {
                Model model = new Model(cursor.getString(1), cursor.getInt(2));
                userList.add(model);
            }
        }
    }

    @Override
    public void onClick(int position) {
    }

    @Override
    public boolean onLongClick(final int position) {

        return true;
    }

    public void getData() {
        totalCount = db.totalCount(table_name);
        totalAbsent = db.getAbsentCount(table_name);
        totalPresent = db.getPresentCount(table_name);
        totalPercentage = 0;
        try {
            totalPercentage = totalPresent * 100 / totalCount;
            double requiredAttendance = ((0.75 * totalCount) - totalPresent) / 0.25;
            int attendance = (int) requiredAttendance;
            if (totalPercentage < 75)
                detail = "You have to attend " + attendance + " more classes to maintain 75% attendance";
            else
                detail = "Your attendance is above 75%, Keep up!!!";

            detailedView.setText(detail);

            db.updateData(totalCount, table_name, totalPresent, totalAbsent, totalPercentage);
        } catch (Exception e) {
            db.updateData(totalCount, table_name, totalPresent, totalAbsent, totalPercentage);
        }
    }

    private ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            item = viewHolder.getAdapterPosition();
            date = userList.get(item).getDate();
            new AlertDialog.Builder(MainActivity2.this).
                    setIcon(android.R.drawable.ic_delete).
                    setTitle("Are your sure ").
                    setMessage("You want to delete this item").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                db.deleteItem(table_name, date.trim());
                                userList.remove(item);
                                adapter.notifyItemRemoved(item);
                               // getData();
                            } catch (Exception e) {
                            }
                        }
                    }).setNegativeButton("No", null).show();
            adapter.notifyDataSetChanged();
        }
    };

}