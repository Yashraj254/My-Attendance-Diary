package com.example.AttendanceDiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.AttendanceDiary.Adapters.MyAdapter;
import com.example.AttendanceDiary.Models.Model;
import com.example.AttendanceDiary.Room.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "Main Activity2";
    Model model;
    String date, table_name, detail, newTable;
    ArrayList<Model> userList;
    RecyclerView recyclerView;
    MyAdapter adapter;
    DbHelper db;
    Toolbar toolbar;
    int item, totalCount, totalPresent, totalAbsent, totalPercentage;
    FloatingActionButton fab;
    ImageButton backArrow;
    TextView subjectView;
    public static TextView detailedView;

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
                            adapter.notifyDataSetChanged();
                           // fab.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Selected date already exists.", Toast.LENGTH_LONG).show();
                        }

                    }
                }, year, month, day);
                pickerDialog.show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public static void update_counter(String value) {
        try {
            detailedView.setText(value);
        } catch (Exception ex) {
            Log.d("Exception", "Exception of type" + ex.getMessage());
        }
    }

    public void setAdapter() {
        adapter = new MyAdapter(userList, this, table_name, fab);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        SpacingDecorator decorator = new SpacingDecorator(10);
        recyclerView.addItemDecoration(decorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(userList.size());
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
                    setTitle("Delete").
                    setMessage("Are you sure you want to delete this item.\nYou cannot undo this process.").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                db.deleteItem(table_name, date.trim());
                                userList.remove(item);
                                getData();
                                fab.setVisibility(View.VISIBLE);
                                adapter.notifyItemRemoved(item);
                            } catch (Exception e) {
                            }
                        }
                    }).setNegativeButton("No", null).show();
        }
    };
}