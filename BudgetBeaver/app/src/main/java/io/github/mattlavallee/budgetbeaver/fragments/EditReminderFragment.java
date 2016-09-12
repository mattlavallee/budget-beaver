package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;

public class EditReminderFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the account id from the bundle
        int reminderId = getArguments().getInt("reminderId");

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_edit_reminder, container, false);
        if(reminderId == -1) {
            getActivity().setTitle("Add a Reminder");
        } else{
            getActivity().setTitle("Edit Reminder");
        }

        //no FAB on Add/Edit account layout
        RelativeLayout parent = (RelativeLayout)getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        return fragmentView;
    }
}
