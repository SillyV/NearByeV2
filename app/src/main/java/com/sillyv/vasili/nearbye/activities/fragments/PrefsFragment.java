package com.sillyv.vasili.nearbye.activities.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.sillyv.vasili.nearbye.R;
import com.sillyv.vasili.nearbye.helpers.database.LocationTableHandler;

/**
 * Created by Vasili.Fedotov on 6/22/2016.
 */

public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
{
    //Constructors / onCreate methods
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);

        Preference units = findPreference("unit_preference");
        units.setOnPreferenceChangeListener(this);

        Preference button = findPreference("remove_favorite_preference");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                LocationTableHandler db = new LocationTableHandler(getActivity());
                db.removeAll();
                Toast.makeText(getActivity(), "Favorites Removed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    //Override Methods
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        preference.setTitle(getResources().getStringArray(R.array.entries_list_preference)[Integer.valueOf((String)newValue)]);
        ((ListPreference)preference).setValueIndex(Integer.valueOf((String) newValue));
        return true;
    }
}
