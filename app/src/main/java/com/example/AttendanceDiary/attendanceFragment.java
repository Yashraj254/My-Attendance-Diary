package com.example.AttendanceDiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AttendanceDiary.Adapters.RecyclerAdapter;
import com.example.AttendanceDiary.Models.User;
import com.example.AttendanceDiary.Room.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link attendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class attendanceFragment extends Fragment implements RecyclerAdapter.onClickListener, RecyclerAdapter.onLongClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView;
    String subName;
    int  item;
    ArrayList<User> usersList;
    RecyclerView recyclerView;
    EditText editText;
    RecyclerAdapter adapter;
    FloatingActionButton fab;
    private static final String TAG = "attendanceFragment";
    Intent intent;
    DbHelper db;
    String newTable,table_name;
    ImageView addButton;
    InputMethodManager inputMethodManager;
    Toolbar toolbar;
    FragmentTransaction ft;
    private boolean shouldRefreshOnResume = false;
    public attendanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment attendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static attendanceFragment newInstance(String param1, String param2) {
        attendanceFragment fragment = new attendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        editText = view.findViewById(R.id.editText);
        subName = editText.getText().toString();
        fab = view.findViewById(R.id.floatingActionButton);
        addButton = view.findViewById(R.id.addButton);
        textView = view.findViewById(R.id.myAttendance);
        toolbar = view.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);

        Fragment attendanceFragment = new attendanceFragment();
     /*   getFragmentManager().beginTransaction()
                .replace(R.id.container,attendanceFragment,"ATTENDANCE")
                .addToBackStack("YOUR_SOURCE_FRAGMENT_TAG").commit();*/

        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        db = new DbHelper(getContext());
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
                    Toast.makeText(getContext(), "Enter something", Toast.LENGTH_SHORT).show();
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
                fab.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                inputMethodManager =  (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
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
                inputMethodManager =  (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                editText.requestFocus();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(shouldRefreshOnResume){
            shouldRefreshOnResume = false;
            getFragmentManager().beginTransaction().replace(R.id.container,new attendanceFragment()).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!shouldRefreshOnResume)
        shouldRefreshOnResume = true;
    }
    public void setAdapter() {
        adapter = new RecyclerAdapter(getActivity(), usersList, this, this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
        intent = new Intent(getContext(), MainActivity2.class);
        intent.putExtra("Send", table_name);
        startActivity(intent);
    }
    @Override
    public boolean onLongClick(int position) {
        return true;
    }
    public void show() {
        Cursor cursor = db.showData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
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

            new AlertDialog.Builder(getContext()).
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