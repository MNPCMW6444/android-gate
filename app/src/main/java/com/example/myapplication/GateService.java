package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GateService extends Service {

    private static final String GATE_NUMBER = "0524412316"; // Replace with your gate's phone number


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pollServer();
            handler.postDelayed(this, 6000); // Poll every minute
        }
    };

    private static final int REQUEST_CODE = 0;

    private PendingIntent createPendingIntent() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + GATE_NUMBER));
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(this, REQUEST_CODE, callIntent, flags);
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        }

        Notification notification = new NotificationCompat.Builder(this, "com.example.myapplication.GateServiceChannel")
                .setContentTitle("Gate Service")
                .setContentText("Running...")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        handler.postDelayed(runnable, 0);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void pollServer() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://caphub.onrender.com/michael")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    String yoad = jsonResponse.getString("yoad");

                    if (yoad.equals("open")) {
                        PendingIntent pendingIntent = createPendingIntent();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(GateService.this, "com.example.myapplication.GateServiceChannel")
                                .setSmallIcon(com.google.android.material.R.drawable.abc_ic_star_black_16dp)
                                .setContentTitle("Incoming Call")
                                .setContentText("Gate Call")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_CALL)
                                .setFullScreenIntent(pendingIntent, true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GateService.this);

                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(23423432, builder.build());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
