package com.abhinavtyagi.securelocation;

import android.app.Application;
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
