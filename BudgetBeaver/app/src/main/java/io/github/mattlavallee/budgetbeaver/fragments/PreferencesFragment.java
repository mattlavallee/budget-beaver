package io.github.mattlavallee.budgetbeaver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.models.enums.PreferenceFiles;

public class PreferencesFragment extends PreferenceFragment {
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
