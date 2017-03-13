package io.github.mattlavallee.budgetbeaver.handlers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.github.mattlavallee.budgetbeaver.R;

public class FragmentManagementHandler {
    public static void launchNewFragment(FragmentActivity activity, Fragment newFragment){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, newFragment)
                .addToBackStack(null)
                .commit();
    }

    public static void closeFragment(FragmentActivity activity){
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
