package com.myFirstApp1.AttendanceDiary;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    NetworkChange networkChange = new NetworkChange();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  checkConnection();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        nav = findViewById(R.id.nav_menu);
        layout = findViewById(R.id.draw_layout);
        header = nav.getHeaderView(0);


        setSupportActionBar(toolbar);
        bar = new ActionBarDrawerToggle(this, layout, toolbar, R.string.open, R.string.close);
        layout.addDrawerListener(bar);
        bar.syncState();
        bar.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white_translucent));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new homeFragment()).commit();
        nav.setCheckedItem(R.id.nav_home);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment temp;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        temp = new homeFragment();
                        break;
                    case R.id.nav_attendance:
                        temp = new attendanceFragment();
                        break;
                    case R.id.nav_logout:
                        temp = new Logout();
                        break;
                    case R.id.nav_delete:
                        temp = new DeleteFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, temp).commit();
                layout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    public void checkConnection()
    {
        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork!=null)
        {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                Toast.makeText(this,"Wifi Enabled.",Toast.LENGTH_SHORT).show();
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                Toast.makeText(this,"Data Network Enabled.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"No Internet Connection.",Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChange,filter);

        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChange);
        super.onStop();
    }

}
