package com.myFirstApp1.AttendanceDiary;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

public class NetworkChange extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if(!Common.isConnectedToInternet(context))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.network_change_dialog,null);
            builder.setView(layout_dialog);

            AppCompatButton retry = layout_dialog.findViewById(R.id.retry);


            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReceive(context,intent);
                    dialog.dismiss();
                }
            });

        }
    }
}
