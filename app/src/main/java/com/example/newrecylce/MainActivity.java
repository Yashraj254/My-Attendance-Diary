package com.example.newrecylce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newrecylce.Adapters.RecyclerAdapter;
import com.example.newrecylce.Models.User;
import com.example.newrecylce.Room.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onClickListener, RecyclerAdapter.onLongClickListener {

     String subName,id;
     int total,item;
    ArrayList<User> usersList;
    RecyclerView recyclerView;
    EditText editText;
    RecyclerAdapter adapter;
    FloatingActionButton fab;
    private static final String TAG = "Main Activity";
    Intent intent;
    DbHelper db;
    String newTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_list);

        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        subName = editText.getText().toString();
        fab = findViewById(R.id.floatingActionButton);
        db = new DbHelper(this);
        usersList = new ArrayList<>();

        show();
        setAdapter();

        adapter.notifyDataSetChanged();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subName = editText.getText().toString();
                int count = 0;
                int present = 0;
                int absent = 0;
                int percentage = 0;
                User user = new User(subName,count,present,absent,percentage);
                if (subName.trim().isEmpty())
                    Toast.makeText(MainActivity.this, "Enter something", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        db.insertData(subName.trim(),count);
                        usersList.add(user);
                        adapter.notifyDataSetChanged();


                    }catch (Exception e){
                        Log.d(TAG, "onClick: Value not found "+total);
                    }
                }
                Log.d(TAG, "onClick: " + subName + " has been added to " + db.TABLE_NAME1);

                editText.getText().clear();
            }
        });
    }  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    public void setAdapter() {
        adapter = new RecyclerAdapter(MainActivity.this,usersList, this, this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    public void onClick(int position) {

        String newTable = usersList.get(position).getSubjectName();

        db.createTable(newTable);

        adapter.notifyDataSetChanged();
        adapter.notifyItemChanged(position);

        Log.d(TAG, "onClick: " + newTable + "  table created");

        intent = new Intent();
        intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("Send", newTable);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(int position) {
        newTable = usersList.get(position).getSubjectName();

        subName =  usersList.get(position).getSubjectName();
        item = position;
        Log.d(TAG, "onLongClick: position = "+item+" and "+subName);


        new AlertDialog.Builder(MainActivity.this).
                setIcon(android.R.drawable.ic_delete).
                setTitle("Are your sure ").
                setMessage("You want to delete this item").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.deleteData(newTable,subName.trim());
                        usersList.remove(item);
                        adapter.notifyDataSetChanged();

                        Log.d(TAG, "Item is deleted and data has been saved");
                    }
                }).setNegativeButton("No", null).show();

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void show() {
        Cursor cursor = db.showData();
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while ((cursor.moveToNext())) {
                User user = new User(cursor.getString(1),cursor.getInt(2),cursor.getInt(3),
                        cursor.getInt(4),cursor.getInt(5));
                usersList.add(user);
            }
        }
    }


}
