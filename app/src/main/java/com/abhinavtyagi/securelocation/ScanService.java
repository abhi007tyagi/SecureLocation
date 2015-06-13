package com.abhinavtyagi.securelocation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by abhinavtyagi on 13/06/15.
 */
public class ScanService extends Service {

    private static final String TAG = "Scan Service";

    private String UUID;
    private long SCAN_TIME;
    private long SCAN_INTERVAL;

    private int totalFound = 0;
    private int beaconFound = 0;

    private Handler handler = new Handler();
    private Timer timer = null;
    private BluetoothAdapter mBluetoothAdapter;
    //    private boolean mScanning;
    private Handler mHandler = new Handler();
    StringBuffer sb = new StringBuffer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        UUID = PreferenceHelper.getUUID();
        SCAN_TIME = PreferenceHelper.getScanTime() * 1000; //default 5sec
        SCAN_INTERVAL = PreferenceHelper.getScanInterval() * 60 * 1000; //default 1min

        // cancel if already existed
        if (timer != null) {
            timer.cancel();
        } else {
            // recreate new
            timer = new Timer();
        }
        // schedule task
        timer.scheduleAtFixedRate(new RefreshScanTask(), 0, SCAN_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
        handler.removeCallbacks(null);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    /**
     * Scan for BLE devices
     */
    private void scanLeDevice() {
        mBluetoothAdapter = ((SecureLocation) getApplication()).mBluetoothAdapter;

        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                    mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                if ((beaconFound < 1)) {
                    if (PreferenceHelper.isInsideSecureLocation()) {
                        //call exited region
                        exitedRegion();
                    }
                }
                Log.d(TAG, "MAC-->" + sb.toString());
                Log.d(TAG, "TotalFound-->" + totalFound);
                Log.d(TAG, "Beacon-->" + beaconFound);
                Log.d(TAG, "*****************************************************");
                sb = new StringBuffer();
                totalFound = 0;
                beaconFound = 0;
            }
        }, SCAN_TIME);

//            mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

//            Log.d("IN",""+sharedpreferences.getBoolean(IN_REGION,false));
//            Log.d("OUT",""+sharedpreferences.getBoolean(OUT_REGION,false));

            String macAdd = device.getAddress();
            if (sb.toString().contains(macAdd)) {
                // skip device
            } else {
                // add new found device
                sb.append(macAdd + ";;");
                totalFound++;

                String hexData = bytesToHex(scanRecord);
                if (hexData.contains(UUID) && hexData.toUpperCase().startsWith(getResources().getString(R.string.beacon_code))) {
                    beaconFound++;
                    if (!PreferenceHelper.isInsideSecureLocation()) {
                        enteredRegion();
                    }
                }
            }
        }
    };


    private void enteredRegion() {
        // Inside Region
        if (((SecureLocation) getApplication()).devicePolicyManager.isAdminActive(((SecureLocation) getApplication()).deviceAdmin)) {
            ((SecureLocation) getApplication()).devicePolicyManager.setCameraDisabled(((SecureLocation) getApplication()).deviceAdmin, true);
            showNotification(getResources().getString(R.string.camera_disabled), getResources().getString(R.string.enter_secure_region), false);
            Log.d(TAG, "Camera Disabled");
            PreferenceHelper.setInsideSecureLocation(true);
        }
    }

    private void exitedRegion() {
        // Outside region
        if (((SecureLocation) getApplication()).devicePolicyManager.isAdminActive(((SecureLocation) getApplication()).deviceAdmin)) {
            ((SecureLocation) getApplication()).devicePolicyManager.setCameraDisabled(((SecureLocation) getApplication()).deviceAdmin, false);
            showNotification(getResources().getString(R.string.camera_enabled), getResources().getString(R.string.exit_secure_region), true);
            Log.d(TAG, "Camera Enabled");
            PreferenceHelper.setInsideSecureLocation(false);
        }
    }

    private void showNotification(String title, String message, boolean isOn) {
        long[] pattern = {200, 300, 200, 300, 200, 300};

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notify;

        if (isOn) {
            notify = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLights(Color.GREEN, 200, 300)
                    .setVibrate(pattern)
                    .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.speech_on))
                    .setSmallIcon(R.mipmap.icon_open).build();
        } else {
            notify = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLights(Color.RED, 300, 200)
                    .setVibrate(pattern)
                    .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.speech_off))
                    .setSmallIcon(R.mipmap.icon_locked).build();
        }

        notificationManager.notify(0, notify);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    /**
     * Timer Task class
     */
    class RefreshScanTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            Log.d(TAG, "##################################################");
            scanLeDevice();
        }
    }
}