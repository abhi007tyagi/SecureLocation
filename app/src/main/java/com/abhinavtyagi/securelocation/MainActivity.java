package com.abhinavtyagi.securelocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by abhinavtyagi on 13/06/15.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int ADMIN = 1006;
    private static final int BL = 2005;

    EditText uuid, scanTime, scanInterval;
    Switch beaconType, serviceSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ask user for allowing Admin access to implement Device Manager Policies
        if (!((SecureLocation) getApplication()).devicePolicyManager.isAdminActive(((SecureLocation) getApplication()).deviceAdmin)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ((SecureLocation) getApplication()).deviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Application needs control as per organization policy!");
            startActivityForResult(intent, ADMIN);
        }
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        else if (((SecureLocation) getApplication()).mBluetoothAdapter == null || !((SecureLocation) getApplication()).mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BL);
        }
        // Do regular task and show UI screen
        else {
            doTask();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == ADMIN) { //Device Manager Access
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (((SecureLocation) getApplication()).mBluetoothAdapter == null || !((SecureLocation) getApplication()).mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, BL);
                } else {
                    doTask();
                }
            } else {
                showMessage(requestCode);
            }
        } else if (requestCode == BL) { //Bluetooth is enabled
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                doTask();
            } else {
                showMessage(requestCode);
            }
        }
    }

    /**
     * Populate UI and set listeners
     */
    private void doTask() {

        uuid = (EditText) findViewById(R.id.uuid);
        scanTime = (EditText) findViewById(R.id.scanTime);
        scanInterval = (EditText) findViewById(R.id.scanInterval);
        beaconType = (Switch) findViewById(R.id.beaconType);
        serviceSwitch = (Switch) findViewById(R.id.service);

        resetUI();

        scanTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (!isDataValid()) {
                        scanTime.setText("");
                    }
                }
            }
        });

        scanInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (!isDataValid()) {
                        scanInterval.setText("");
                    }
                }
            }
        });

        beaconType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    beaconType.setText(getResources().getText(R.string.i_beacon));
                    PreferenceHelper.setBeaconType(PreferenceHelper.I_BEACON);
                    Log.d(TAG, "iBeacon selected");
                } else {
                    beaconType.setText(getResources().getText(R.string.alt_beacon));
                    PreferenceHelper.setBeaconType(PreferenceHelper.ALT_BEACON);
                    Log.d(TAG, "AltBeacon selected");
                }
            }
        });

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isDataValid()) {
                    if (isChecked) {
                        setData();
                        startService(((SecureLocation) getApplication()).service);
                        serviceSwitch.setText(getResources().getText(R.string.stop_service));
                        PreferenceHelper.setServiceFlag(true);
                        Log.d(TAG, "Service Started");
                    } else {
                        stopService(((SecureLocation) getApplication()).service);
                        serviceSwitch.setText(getResources().getText(R.string.start_service));
                        PreferenceHelper.setServiceFlag(false);
                        Log.d(TAG, "Service Stopped");
                    }
                } else {
                    if (isChecked) {
                        serviceSwitch.setChecked(false);
                    }
                }
            }
        });
    }

    /**
     * Check if entered fields are valid or not
     *
     * @return
     */
    private boolean isDataValid() {
        int scan_time = Integer.parseInt(scanTime.getText().toString());
        int scan_interval = Integer.parseInt(scanInterval.getText().toString());

        if (uuid.getText().toString().length() != 32) {
            Toast.makeText(this, "UUID should be 32 characters long", Toast.LENGTH_SHORT).show();
            return false;
        } else if (scan_time < 2 || scan_time > 9) {
            Toast.makeText(this, "For better results, Scan Time should be between 2 to 9 sec", Toast.LENGTH_LONG).show();
            return false;
        } else if (scan_interval < 1 || scan_interval > 10) {
            Toast.makeText(this, "For better results, Scan Interval should be between 1 to 10 min", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }


    }

    /**
     * Save data in shared preferences
     */
    private void setData() {

        int scan_time = Integer.parseInt(scanTime.getText().toString());
        int scan_interval = Integer.parseInt(scanInterval.getText().toString());
        PreferenceHelper.setData(uuid.getText().toString(), scan_time, scan_interval);
    }

    /**
     * Check and update Beacon Type UI and Service Switch UI
     */
    private void resetUI() {
        switch (PreferenceHelper.getBeaconType()) {
            case PreferenceHelper.I_BEACON:
                beaconType.setText(getResources().getText(R.string.i_beacon));
                beaconType.setChecked(true);
                break;
            case PreferenceHelper.ALT_BEACON:
            default:
                beaconType.setText(getResources().getText(R.string.alt_beacon));
                beaconType.setChecked(false);
                break;
        }

        if (PreferenceHelper.isServiceRunning()) {
            serviceSwitch.setChecked(true);
            serviceSwitch.setText(getResources().getText(R.string.stop_service));
        } else {
            serviceSwitch.setChecked(false);
            serviceSwitch.setText(getResources().getText(R.string.start_service));
        }
    }

    /**
     * Show alert message based on requestCode
     *
     * @param requestCode
     */
    private void showMessage(int requestCode) {
        if (requestCode == ADMIN) {
            new AlertDialog.Builder(this)
                    .setMessage("Please select \"Activate\" to proceed!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue to ask for Admin access
                            //Ask user for allowing Admin access to implement Device Manager Policies
                            if (!((SecureLocation) getApplication()).devicePolicyManager.isAdminActive(((SecureLocation) getApplication()).deviceAdmin)) {
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ((SecureLocation) getApplication()).deviceAdmin);
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Application needs control as per organization policy!");
                                startActivityForResult(intent, ADMIN);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (requestCode == BL) {
            new AlertDialog.Builder(this)
                    .setMessage("Please enable Bluetooth to proceed!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with Bluetooth enabling
                            if (((SecureLocation) getApplication()).mBluetoothAdapter == null || !((SecureLocation) getApplication()).mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, BL);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


}
