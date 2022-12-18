package io.github.mattlavallee.budgetbeaver.handlers;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
        closeKeyboardIfOpen(activity);
        activity.getSupportFragmentManager().popBackStackImmediate();
    }

    private static void closeKeyboardIfOpen(FragmentActivity activity){
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}
