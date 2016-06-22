package com.sillyv.vasili.nearbye.activities.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.sillyv.vasili.nearbye.R;

/**
 * Created by vasil on 6/22/2016.
 */

public class PrefsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);
    }
}
