package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class MainActivity extends AppCompatActivity {

    private Button callGateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "com.example.myapplication.GateServiceChannel",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        Intent serviceIntent = new Intent(this, GateService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            MainActivity.this.startForegroundService(serviceIntent);
        } else{
            startService(serviceIntent);
        }




        callGateButton = findViewById(R.id.call_gate_button);
        callGateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.hasCallPhonePermission(MainActivity.this)) {
                    GateCallHelper.initiateCall(MainActivity.this);
                } else {
                    PermissionUtils.requestCallPhonePermission(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GateCallHelper.initiateCall(MainActivity.this);
            } else {
                Toast.makeText(MainActivity.this, "Please grant CALL_PHONE permission to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, GateService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, GateService.class));
    }



    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 6000); // Poll every minute
        }


    };

}


