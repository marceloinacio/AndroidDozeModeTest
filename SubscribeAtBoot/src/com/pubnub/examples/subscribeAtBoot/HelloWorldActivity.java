package com.pubnub.examples.subscribeAtBoot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;


public class HelloWorldActivity extends Activity {
    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
        	Intent intent = new Intent();
        	String packageName = getPackageName();
        	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        	if (!pm.isIgnoringBatteryOptimizations(packageName)) {
        	    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + packageName));
				startActivity(intent);
        	}
        }

        // In new versions of Android, the service may not be activated unless an
        // associated activity is run at least once. This empty activity serves
        // that purpose

        Intent serviceIntent = new Intent(this, PubnubService.class);
        startService(serviceIntent);

        Log.i("HelloWorldActivity", "PubNub Activity Started!");

    }
}