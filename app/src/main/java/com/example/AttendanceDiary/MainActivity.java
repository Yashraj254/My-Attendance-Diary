package com.example.AttendanceDiary;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle bar;
    DrawerLayout layout;
    Toolbar toolbar;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        nav = findViewById(R.id.nav_menu);
        layout = findViewById(R.id.draw_layout);
        header = nav.getHeaderView(0);
        setSupportActionBar(toolbar);
        bar = new ActionBarDrawerToggle(this,layout,toolbar,R.string.open,R.string.close);
        layout.addDrawerListener(bar);
        bar.syncState();
        bar.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white_translucent));
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new homeFragment()).commit();
        nav.setCheckedItem(R.id.nav_home);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment temp;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        temp = new homeFragment();
                        break;
                    case R.id.nav_attendance:
                        temp = new attendanceFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container,temp).commit();
                layout.closeDrawer(GravityCompat.START);
                return true;}
        });
    }
}
