package com.myFirstApp1.AttendanceDiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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

import com.myFirstApp1.AttendanceDiary.Adapters.MyAdapter;
import com.myFirstApp1.AttendanceDiary.Models.Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "Main Activity2";
    Model model;
    String date, table_name, detail, newTable, status;
    ArrayList<Model> userList;
    RecyclerView recyclerView;
    MyAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser mAuth;

    String userId,id;
    DividerItemDecoration itemDecoration;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    Toolbar toolbar;
    int item, totalCount, totalPresent, totalAbsent, totalPercentage, present, absent;
    FloatingActionButton fab;
    ImageButton backArrow;
    TextView subjectView;
    public static TextView detailedView;
    NetworkChange networkChange = new NetworkChange();
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

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        userId = mAuth.getUid();
        userList = new ArrayList<>();


        table_name = getIntent().getStringExtra("Send");
        newTable = table_name;


        sharedPref = this.getSharedPreferences("myKey", MODE_PRIVATE);
        editor = sharedPref.edit();


        sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        clearAll();

        getDates();

        subjectView.setText(newTable);

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
                        date = dayOfMonth + "/" + month + 1 + "/" + year;
                        Model matched = userList.stream().filter(model -> model.getDate().equals(date))
                                .findFirst().orElse(null); // first occurrence

                        if (matched == null) {
                            model = new Model(date);
                            userList.add(model);
                            Log.d(TAG, "onClick: " + date + " has been added to " + table_name);
                            adapter.notifyDataSetChanged();
                            //  fab.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Selected date already exists.", Toast.LENGTH_LONG).show();
                            fab.setVisibility(View.VISIBLE);
                        }

                    }
                }, year, month, day);
                pickerDialog.setCancelable(false);
                pickerDialog.show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getDates() {
        Query query = ref.child(userId).child("Subjects").child(newTable).child("Date");
        Query quer = ref.child(userId).child("Subjects").orderByChild("subjectName").equalTo(newTable);
        quer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clearAll();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    present = Integer.parseInt(dataSnapshot.child("present").getValue().toString());
                    absent = Integer.parseInt(dataSnapshot.child("absent").getValue().toString());
                }


                Log.d(TAG, "onDataChange: Present: "+present+" Absent: "+absent);
                editor.putInt("present",present);
                editor.putInt("absent",absent);
                editor.apply();
                recyclerView.removeItemDecoration(itemDecoration);
                setAdapter();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAll();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Model model = new Model();
                    model.setDate(dataSnapshot.child("date").getValue().toString());
                    model.setStatus(dataSnapshot.child("status").getValue().toString());
                    model.setId(dataSnapshot.child("id").getValue().toString());
                    userList.add(model);
                    int size = (int) snapshot.getChildrenCount();

                    ref.child(userId).child("Subjects").child(newTable).child("total").setValue(size);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        present = sharedPreferences.getInt("present", 0);
        absent = sharedPreferences.getInt("absent", 0);
        Log.d(TAG, "SharedPref: Present: "+present+" Absent: "+absent);
        getData();
    }

    private void clearAll() {
        if (userList != null) {
            userList.clear();

            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
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

        Log.d(TAG, "Adapter: Present: "+present+" Absent: "+absent);
        adapter = new MyAdapter(userList, this, table_name, fab, database, ref, newTable, present, absent, userId);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
       // recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);


        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }


    public void getData() {
        Log.d(TAG, "getData: Present "+present+" Absent "+absent);
        totalCount = present + absent;
        totalAbsent = absent;
        totalPresent = present;
        totalPercentage = 0;

        try {
            totalPercentage = totalPresent * 100 / totalCount;
            ref.child(userId).child("Subjects").child(newTable).child("percentage").setValue(totalPercentage);
            double requiredAttendance = ((0.75 * totalCount) - totalPresent) / 0.25;
            int attendance = (int) requiredAttendance;
            if (totalPercentage < 75)
                detail = "You have to attend " + attendance + " more classes to maintain 75% attendance.";
            else
                detail = "Your attendance is above 75%, Keep up!!!";
            detailedView.setText(detail);
        } catch (Exception e) {

        }
        if (totalCount == 0)
        {
            ref.child(userId).child("Subjects").child(newTable).child("total").setValue(0);
            ref.child(userId).child("Subjects").child(newTable).child("percentage").setValue(0);
            detailedView.setText("");}
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
            id = userList.get(item).getId();
            Log.d(TAG, "onSwiped: id "+id);
            Log.d(TAG, "onSwiped: " + date + " at position: " + item);
            status = userList.get(item).getStatus();

            new AlertDialog.Builder(MainActivity2.this).
                    setCancelable(false).
                    setIcon(android.R.drawable.ic_delete).
                    setTitle("Delete").
                    setMessage("Are you sure you want to delete this item.\nYou cannot undo this process.").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                userList.remove(item);
                                adapter.notifyDataSetChanged();
                                ref.child(userId).child("Subjects").child(newTable).child("Date").child(id).removeValue();


                                if (status.equals("Present")) {
                                    present--;
                                    getData();
                                    ref.child(userId).child("Subjects").child(newTable).child("present").setValue(present);

                                }
                                if (status.equals("Absent")) {
                                    absent--;
                                    getData();
                                    ref.child(userId).child("Subjects").child(newTable).child("absent").setValue(absent);

                                }


                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                            }
                        }
                    }).setNegativeButton("No", null).show();
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onStop() {
        unregisterReceiver(networkChange);
        super.onStop();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChange,filter);
        super.onStart();
    }
}