package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;

public class RemindersFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_reminders, container, false);
        getActivity().setTitle("Budget Beaver - Reminders");

        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_reminder, R.id.bb_fab_menu_reminder);

        return fragmentView;
    }
}
