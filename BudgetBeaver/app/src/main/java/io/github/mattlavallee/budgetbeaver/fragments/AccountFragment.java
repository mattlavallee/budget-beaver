package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;

public class AccountFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the account id from the bundle
        int accountId = getArguments().getInt("accountId");

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle("Account Name ($0.00)");

        TextView temp = (TextView)fragmentView.findViewById(R.id.account_test);
        temp.setText("Account page: " + Integer.toString(accountId) + "!");

        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_account, R.id.bb_fab_menu_account);

        return fragmentView;
    }
}
