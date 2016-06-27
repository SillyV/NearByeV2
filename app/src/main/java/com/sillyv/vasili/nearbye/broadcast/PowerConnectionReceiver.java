package com.sillyv.vasili.nearbye.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by vasili on 27-Jun-16.
 */
public class PowerConnectionReceiver extends BroadcastReceiver
{
    private static final String TAG = "SillyV.PCR";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String msg = "Power was Disconnected";
        if (intent.getAction() == Intent.ACTION_POWER_CONNECTED)
        {
            msg = "Power was connected.";
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
