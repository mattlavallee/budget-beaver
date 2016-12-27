package io.github.mattlavallee.budgetbeaver.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.models.enums.PreferenceFiles;

public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(PreferenceFiles.DefaultPreferences());
        addPreferencesFromResource(R.xml.budget_beaver_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getApplicationContext()
            .getSharedPreferences(PreferenceFiles.DefaultPreferences(), Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getApplicationContext()
            .getSharedPreferences(PreferenceFiles.DefaultPreferences(), Context.MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals("setting_title_font_color"))
//        {
//            // get preference by key
//            Preference pref = findPreference(key);
//            // do your stuff here
//        }
    }
}
