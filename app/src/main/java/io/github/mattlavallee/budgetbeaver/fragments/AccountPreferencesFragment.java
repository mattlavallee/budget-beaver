package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.models.enums.PreferenceFiles;

public class AccountPreferencesFragment extends PreferenceFragment{
    private int accountId;
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        accountId = getArguments().getInt("accountId");
        String accountName = getArguments().getString("accountName");

        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(PreferenceFiles.AccountPreferences(accountId));
        addPreferencesFromResource(R.xml.budget_beaver_account_settings);

        //Configure the settings page for the current account
        PreferenceCategory header = (PreferenceCategory)findPreference("setting_title_act_transaction_settings");
        header.setTitle(accountName + " Settings");
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
