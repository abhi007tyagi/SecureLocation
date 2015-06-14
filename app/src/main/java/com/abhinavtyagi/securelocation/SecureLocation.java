package com.abhinavtyagi.securelocation;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by abhinavtyagi on 13/06/15.
 */
public class SecureLocation extends Application {

    BluetoothAdapter mBluetoothAdapter;
    DevicePolicyManager devicePolicyManager;
    ComponentName deviceAdmin;
    Intent service;

    // uncaught exception handler variable
    private Thread.UncaughtExceptionHandler exceptionHandler;

    private final static String TAG = "SecureLocation";

    public SecureLocation() {
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(unCaughtExceptionHandler);
    }

    //Handle uncaught exception..  handler listener
    private Thread.UncaughtExceptionHandler unCaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {

                    PendingIntent myActivity = PendingIntent.getActivity(getBaseContext(),
                            30006, new Intent(getBaseContext(), ExceptionActivity.class),
                            PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager alarmManager;
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            7000, myActivity);
                    System.exit(2);

                    // re-throw critical exception further to the os (important)
                    exceptionHandler.uncaughtException(thread, ex);
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();

        // Use this check to determine whether BLE is supported on the device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        //Initialize Preference Helper
        PreferenceHelper.init(getApplicationContext());

        //Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //Initialize Device Policy Manager and Receiver
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdmin = new ComponentName(this, AdminReceiver.class);

        //Initialize background service intent
        service = new Intent(this, ScanService.class);
    }
}
