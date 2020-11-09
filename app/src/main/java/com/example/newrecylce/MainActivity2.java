package com.example.newrecylce;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newrecylce.Adapters.MyAdapter;
import com.example.newrecylce.Models.Model;
import com.example.newrecylce.Room.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class MainActivity2 extends AppCompatActivity implements MyAdapter.onClickListener, MyAdapter.onLongClickListener {

    Context context;
    private static final String TAG = "Main Activity2";
    Model model;
    String date, table_name;
    private RadioGroup radioGroup;
    private RadioButton present, absent;
    Intent intent;
    ArrayList<Model> userList;
    RecyclerView recyclerView;
    MyAdapter adapter;
    DbHelper db;
    Toolbar toolbar;
    int item,totalCount,totalPresent,totalAbsent,totalPercentage;
    FloatingActionButton fab;
    ImageButton backArrow;

    public void onCreate(Bundle savedInstancedState) {
        super.onCreate(savedInstancedState);
        setContentView(R.layout.date_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.toolbar_back_arrow);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.floatingActionButton);
        radioGroup = findViewById(R.id.radioStatus);
        present = findViewById(R.id.present);
        absent = findViewById(R.id.absent);

        db = new DbHelper(this);
        userList = new ArrayList<>();
        show();

        intent = new Intent();
        table_name = getIntent().getStringExtra("Send");
        setAdapter();

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
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date = dayOfMonth + "/" + month + "/" + year;
                        model = new Model(date);
                        userList.add(model);
                        Log.d(TAG, "onClick: " + date + " has been added to " + table_name);

                        adapter.notifyDataSetChanged();

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
        adapter = new MyAdapter(userList, this, this, this, table_name);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void show() {
        Cursor cursor = db.display();
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity2.this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while ((cursor.moveToNext())) {
                Model model = new Model(cursor.getString(1),cursor.getInt(2));
                userList.add(model);
            }
        }
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public boolean onLongClick(final int position) {
        item = position;
        date = userList.get(position).getDate();
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
                            getData();
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
//                            Log.d(TAG, "onClick: Some error " + table_name + " position " + item + " date is " + model.getDate());
                        }
                    }
                }).setNegativeButton("No", null).show();
        return true;
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
}