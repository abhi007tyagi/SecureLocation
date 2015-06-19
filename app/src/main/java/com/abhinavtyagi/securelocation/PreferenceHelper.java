package com.abhinavtyagi.securelocation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abhinavtyagi on 13/06/15.
 */
public class PreferenceHelper {

    static final int ALT_BEACON = 709;
    static final int I_BEACON = 907;
    private static final String APP = "BLE_SECURE_LOCATION";
    private static final String IS_SERVICE_RUNNING = "isServiceRunning";
    private static final String UUID = "uuid";
    private static final String SCAN_TIME = "scan_time";
    private static final String SCAN_INTERVAL = "scan_interval";
    private static final String IN_REGION = "is_inside_secure_region";
    private static final String BEACON_TYPE = "beaconType";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private static Context context;

    /**
     * Initializes the Preference Helper class
     * @param ctx
     */
    public synchronized static void init(Context ctx) {
        context = ctx;
        prefs = context.getSharedPreferences(APP, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Set service flag to check if background service is running or not
     * @param flag
     */
    public synchronized static void setServiceFlag(boolean flag){
        editor.putBoolean(IS_SERVICE_RUNNING, flag);
        editor.commit();
    }

    /**
     * Check if background service is running or not
     * @return
     */
    public synchronized static boolean isServiceRunning(){
        return prefs.getBoolean(IS_SERVICE_RUNNING, false);
    }

    /**
     * Save UUID, SCAN TIME and SCAN INTERVAL values
     * @param uuid
     * @param scanTime
     * @param scanInterval
     */
    public synchronized static void setData(String uuid, int scanTime, int scanInterval){
        editor.putString(UUID,uuid);
        editor.putInt(SCAN_TIME, scanTime);
        editor.putInt(SCAN_INTERVAL, scanInterval);
        editor.commit();
    }

    /**
     * Fetch saved UUID
     * @return
     */
    public synchronized static String getUUID(){
        return prefs.getString(UUID, context.getResources().getString(R.string.default_uuid));
    }

    /**
     * Fetch saved SCAN TIME
     * @return
     */
    public synchronized static int getScanTime(){
        return prefs.getInt(SCAN_TIME, 5);
    }

    /**
     * Fetch saved SCAN INTERVAL
     * @return
     */
    public synchronized static int getScanInterval(){
        return prefs.getInt(SCAN_INTERVAL, 1);
    }

    /**
     * Check if inside secure location or not
     * @return
     */
    public synchronized static boolean isInsideSecureLocation() {
        return prefs.getBoolean(IN_REGION, false);
    }

    /**
     * Set flag if inside secure location or not
     * @param flag
     */
    public synchronized static void setInsideSecureLocation(boolean flag){
        editor.putBoolean(IN_REGION, flag);
        editor.commit();
    }

    /**
     * Get the Beacon type used for transmitting signals
     * @return
     */
    public synchronized static int getBeaconType() {
        return prefs.getInt(BEACON_TYPE, ALT_BEACON);
    }

    /**
     * Sets Beacon Type
     *
     * @param type
     */
    public synchronized static void setBeaconType(int type) {
        editor.putInt(BEACON_TYPE, type);
        editor.commit();
    }


}
