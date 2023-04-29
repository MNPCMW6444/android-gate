package com.example.myapplication;

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
        callIntent.setData(Uri.parse("tel:" + GATE_NUMBER));

        if (PermissionUtils.hasCallPhonePermission(context)) {
            context.startActivity(callIntent);
        } else {
            Toast.makeText(context, "Please grant CALL_PHONE permission to use this feature", Toast.LENGTH_SHORT).show();
        }
    }
}
