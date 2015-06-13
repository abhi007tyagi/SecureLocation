package com.abhinavtyagi.securelocation;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by abhinavtyagi on 13/06/15.
 */
public class AdminReceiver extends DeviceAdminReceiver {
    static final String TAG = "AdminReceiver";

    /** Called when this application is approved to be a device administrator. */
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, R.string.admin_receiver_status_enabled,Toast.LENGTH_LONG).show();
        Log.d(TAG, "onEnabled");
    }

    /** Called when this application is no longer the device administrator. */
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, R.string.admin_receiver_status_disabled,
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDisabled");
    }
}
