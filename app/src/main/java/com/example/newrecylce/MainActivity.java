package com.example.newrecylce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newrecylce.Adapters.RecyclerAdapter;
import com.example.newrecylce.Models.User;
import com.example.newrecylce.Room.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onClickListener, RecyclerAdapter.onLongClickListener {

    TextView textView;
    String subName;
    int  item;
    ArrayList<User> usersList;
    RecyclerView recyclerView;
    EditText editText;
    RecyclerAdapter adapter;
    FloatingActionButton fab;
    private static final String TAG = "Main Activity";
    Intent intent;
    DbHelper db;
    String newTable,table_name;
    ImageView addButton,cancelButton;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_list);

        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        subName = editText.getText().toString();
        fab = findViewById(R.id.floatingActionButton);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);
        textView = findViewById(R.id.myAttendance);

        db = new DbHelper(this);
        usersList = new ArrayList<>();

        show();
        setAdapter();

        adapter.notifyDataSetChanged();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subName = editText.getText().toString();
                int count = 0;
                int present = 0;
                int absent = 0;
                int percentage = 0;
                User user = new User(subName, count, present, absent, percentage);
                if (subName.trim().isEmpty())
                    Toast.makeText(MainActivity.this, "Enter something", Toast.LENGTH_SHORT).show();
                else {
                  //  subName = subName.replaceAll(" ","");
                    db.insertData(subName.trim(), count, present, absent, percentage);
                    usersList.add(user);
                    adapter.notifyDataSetChanged();
                }
                Log.d(TAG, "onClick: " + subName + " has been added to " + db.TABLE_NAME1);

                editText.getText().clear();
                editText.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                editText.requestFocus();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                editText.requestFocus();
            }
        });  cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.getText().clear();
                editText.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(cancelButton.getWindowToken(), 0);

                fab.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    public void setAdapter() {
        adapter = new RecyclerAdapter(MainActivity.this, usersList, this, this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
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
        table_name = newTable;
        newTable =  newTable.replaceAll("[^a-zA-Z0-9]","");
        db.createTable(newTable);
        adapter.notifyDataSetChanged();
        adapter.notifyItemChanged(position);
        intent = new Intent();
        intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("Send", table_name);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(int position) {
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
                User user = new User(cursor.getString(1), cursor.getInt(2), cursor.getInt(3),
                        cursor.getInt(4), cursor.getInt(5));
                usersList.add(user);
            }
        }
    }
    private ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            newTable = usersList.get(viewHolder.getAdapterPosition()).getSubjectName();
            newTable =  newTable.replaceAll("[^a-zA-Z0-9]","");
            subName = usersList.get(viewHolder.getAdapterPosition()).getSubjectName();
            item = viewHolder.getAdapterPosition();

            new AlertDialog.Builder(MainActivity.this).
                    setIcon(android.R.drawable.ic_delete).
                    setTitle("Delete ").
                    setMessage("Are you want to delete this item?\nYou cannot undo this process.").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteData(newTable, subName.trim());
                            usersList.remove(item);
                            adapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("No", null).show();
            adapter.notifyDataSetChanged();
        }
    };

}
