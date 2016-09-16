package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.adapters.AccountAdapter;

public class OverviewFragment extends Fragment implements View.OnClickListener {
    private DatabaseDispatcher dbDispatcher;
    private AccountAdapter accountAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_overview, container, false);

        getActivity().setTitle("Budget Beaver - Overview");
        //setup the Floating Action Menu for the overview fragment
        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_overview, R.id.bb_fab_menu_overview);

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = (RecyclerView) fragmentView.findViewById(R.id.overview_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerViewLayout.setLayoutManager(llm);
        recyclerViewLayout.setHasFixedSize(true);

        //Load all accounts
        dbDispatcher = new DatabaseDispatcher(getContext());
        ArrayList<Account> allAccounts = dbDispatcher.Accounts.getAccounts();

        //set account adapter on the recycle view
        accountAdapter = new AccountAdapter(allAccounts, this);
        recyclerViewLayout.setAdapter(accountAdapter);

        registerFabClickEvents(getActivity());

        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_overflow:
                Log.i("TEST", "overflow pressed");
                break;
        }
    }

    private void registerFabClickEvents(FragmentActivity view) {
        FloatingActionButton reminderBtn = (FloatingActionButton) view.findViewById(R.id.fab_overview_add_reminder);
        FloatingActionButton transactionBtn = (FloatingActionButton) view.findViewById(R.id.fab_overview_add_transaction);
        FloatingActionButton accountBtn = (FloatingActionButton) view.findViewById(R.id.fab_overview_add_account);

        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
            }
        });
        transactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
            }
        });
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAccount(-1);
            }
        });
    }

    public void editAccount(int accountId) {
        Fragment editAccountFragment = new EditAccountFragment();
        editAccountFragment.setArguments(generateIdBundle("accountId", accountId));

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, editAccountFragment)
                .addToBackStack(null)
                .commit();
    }

    public void addTransaction() {
        Fragment editTransactionFragment = new EditTransactionFragment();
        editTransactionFragment.setArguments(generateIdBundle("transactionId", -1));

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, editTransactionFragment)
                .addToBackStack(null)
                .commit();
    }

    public void addReminder() {
        Fragment editReminderFragment = new EditReminderFragment();
        editReminderFragment.setArguments(generateIdBundle("reminderId", -1));

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, editReminderFragment)
                .addToBackStack(null)
                .commit();
    }

    private Bundle generateIdBundle(String name, int id) {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt(name, id);
        return dataBundle;
    }

    public void deleteAccount(int accountId) {
        Account accountToDelete = dbDispatcher.Accounts.getAccountById(accountId);
        accountToDelete.setIsActive(false);
        dbDispatcher.Accounts.updateAccount(accountToDelete);

        ArrayList<Account> allAccounts = dbDispatcher.Accounts.getAccounts();
        accountAdapter.updateData( allAccounts );
    }
}
