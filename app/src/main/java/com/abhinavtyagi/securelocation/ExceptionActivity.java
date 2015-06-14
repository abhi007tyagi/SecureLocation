package com.abhinavtyagi.securelocation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class ExceptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);
        Log.d("ExceptionActivity", "Unknown Error");
    }

    public void exit(View view){
        finish();
        Log.d("ExceptionActivity", "finish");
    }
}
