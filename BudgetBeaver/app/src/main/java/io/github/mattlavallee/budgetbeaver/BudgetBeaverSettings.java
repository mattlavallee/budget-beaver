package io.github.mattlavallee.budgetbeaver;

import android.preference.PreferenceActivity;
import java.util.List;
import io.github.mattlavallee.budgetbeaver.fragments.PreferencesFragment;

public class BudgetBeaverSettings extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return PreferencesFragment.class.getName().equals(fragmentName);
    }
}
