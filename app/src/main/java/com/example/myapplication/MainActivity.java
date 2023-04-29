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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button callGateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


