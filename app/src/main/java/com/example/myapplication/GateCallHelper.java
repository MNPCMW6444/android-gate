package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;



public class GateCallHelper {

    private static final String GATE_NUMBER = "0524412316"; // Replace with your gate's phone number

    public static void initiateCall(Context context) {
        if (context == null) {
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + GATE_NUMBER));



        if (PermissionUtils.hasCallPhonePermission(context)) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(callIntent);
                    }
                });
            } else {
                context.startActivity(callIntent);
            }
        } else {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Please grant CALL_PHONE permission to use this feature", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
