package io.github.mattlavallee.budgetbeaver;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.ArrayList;
import java.util.List;

import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.fragments.AccountPreferencesFragment;
import io.github.mattlavallee.budgetbeaver.fragments.PreferencesFragment;
import io.github.mattlavallee.budgetbeaver.models.Account;

public class BudgetBeaverSettings extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);

        DatabaseDispatcher dbHelper = new DatabaseDispatcher(getApplicationContext());
        ArrayList<Account> accounts = dbHelper.Accounts.getAccounts();
        for(Account account : accounts ){
            Header header = new Header();
            header.title = account.getName() + " Settings";
            header.summary = "Account settings for " + account.getName();
            header.fragment = AccountPreferencesFragment.class.getName();

            Bundle preferenceBundle = new Bundle();

            preferenceBundle.putInt("accountId", account.getId());
            preferenceBundle.putString("accountName", account.getName());
            header.fragmentArguments = preferenceBundle;
            target.add(header);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return PreferencesFragment.class.getName().equals(fragmentName) ||
                AccountPreferencesFragment.class.getName().equals(fragmentName);
    }
}
