package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.adapters.AccountAdapter;

public class OverviewFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private AccountAdapter accountAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_overview, container, false);

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = (RecyclerView)fragmentView.findViewById(R.id.overview_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerViewLayout.setLayoutManager(llm);
        recyclerViewLayout.setHasFixedSize(true);

        //setup the Floating Action Menu for the overview fragment
        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_overview, R.id.bb_fab_menu_overview);

        //Load all accounts
        dbDispatcher = new DatabaseDispatcher(getContext());
        ArrayList<Account> allAccounts = dbDispatcher.Accounts.getAccounts();

        //set account adapter on the recycle view
        accountAdapter = new AccountAdapter(allAccounts);
        recyclerViewLayout.setAdapter(accountAdapter);

        return fragmentView;
    }
}
