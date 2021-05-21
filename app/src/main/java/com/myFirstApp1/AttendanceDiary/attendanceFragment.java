package com.myFirstApp1.AttendanceDiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myFirstApp1.AttendanceDiary.Adapters.RecyclerAdapter;
import com.myFirstApp1.AttendanceDiary.Models.User;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link attendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class attendanceFragment extends Fragment implements RecyclerAdapter.onClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView;
    String subName;
    int item;
    int count = 0;
    int present = 0;
    int absent = 0;
    int percentage = 0;
    ArrayList<User> usersList;
    RecyclerView recyclerView;
    EditText editText;
    RecyclerAdapter adapter;
    FloatingActionButton fab;
    private static final String TAG = "attendanceFragment";
    String newTable, table_name;
    ImageView addButton;
    InputMethodManager inputMethodManager;
    Toolbar toolbar;
    FragmentTransaction ft;
    FirebaseDatabase database;
    FirebaseUser mAuth;
    String userId, userMail;
    DatabaseReference ref;
    private boolean shouldRefreshOnResume = false;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

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

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();

        userId = mAuth.getUid();


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);

        DrawerLayout drawer = actionBar.findViewById(R.id.draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        usersList = new ArrayList<>();

        clearAll();

        setAdapter();

        getData();
        sharedPref = getContext().getSharedPreferences("myKey", MODE_PRIVATE);
        editor = sharedPref.edit();

        addButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                subName = editText.getText().toString();
                Pattern p = Pattern.compile("[.$\\[\\]#/]");
                Matcher m = p.matcher(subName);
                User user = new User(subName, 0, 0, 0, 0);
                User matched = usersList.stream().filter(user1 -> user1.getSubjectName().equals(subName)).
                        findFirst().orElse(null);
                if (subName.trim().isEmpty())

                    Toast.makeText(getContext(), "Enter something", Toast.LENGTH_SHORT).show();
                else if (m.find()) {

                    Toast.makeText(getContext(), "Do not use . $ [ ] # /", Toast.LENGTH_LONG).show();
                } else if (matched == null) {
                    usersList.add(user);
                    ref.child(userId).child("Subjects").child(subName).setValue(user);
                    getData();
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "Subject with same name already exists.", Toast.LENGTH_LONG).show();
                }

                editText.getText().clear();
                editText.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
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
                inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                editText.requestFocus();
            }
        });
        return view;
    }

    public void getData() {
        Query query = ref.child(userId).child("Subjects");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAll();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = new User();
                    user.setSubjectName(dataSnapshot.child("subjectName").getValue().toString());
                    present = Integer.parseInt(dataSnapshot.child("present").getValue().toString());
                    absent = Integer.parseInt(dataSnapshot.child("absent").getValue().toString());
                    user.setTotal(Integer.parseInt(dataSnapshot.child("total").getValue().toString()));
                    user.setPresent(present);
                    user.setAbsent(absent);
                    user.setPercentage(Integer.parseInt(dataSnapshot.child("percentage").getValue().toString()));
                    usersList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            shouldRefreshOnResume = false;
            getFragmentManager().beginTransaction().replace(R.id.container, new attendanceFragment()).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!shouldRefreshOnResume)
            shouldRefreshOnResume = true;
    }

    public void setAdapter() {
        adapter = new RecyclerAdapter(getActivity(), usersList, this);
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

        Intent intents = new Intent(getContext(), MainActivity2.class);
        intents.putExtra("Send", table_name);
        startActivity(intents);
    }

    private void clearAll() {
        if (usersList != null) {
            usersList.clear();

            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    private ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


            subName = usersList.get(viewHolder.getAdapterPosition()).getSubjectName();
            item = viewHolder.getAdapterPosition();

            new AlertDialog.Builder(getContext()).
                    setIcon(android.R.drawable.ic_delete).
                    setTitle("Delete ").
                    setMessage("Are you sure want to delete this item?\nYou cannot undo this process.").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            User user = new User(subName, 0, 0, 0, 0);
                            ref.child(userId).child("Subjects").child(subName).setValue(user);
                            usersList.remove(item);

                            ref.child(userId).child("Subjects").child(subName).removeValue();
                            adapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("No", null).show();
            adapter.notifyDataSetChanged();
        }
    };
}