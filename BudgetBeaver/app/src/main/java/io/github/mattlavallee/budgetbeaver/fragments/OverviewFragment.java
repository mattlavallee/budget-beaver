package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.BudgetBeaverRecyclerHandler;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.handlers.SnackBarHandler;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;
import io.github.mattlavallee.budgetbeaver.models.Settings;
import io.github.mattlavallee.budgetbeaver.models.Transaction;
import io.github.mattlavallee.budgetbeaver.models.adapters.AccountAdapter;

public class OverviewFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private AccountAdapter accountAdapter;
    private ArrayList<Account> allAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.recycler_view, container, false);

        getActivity().setTitle("Account Overview");
        //setup the Floating Action Menu for the overview fragment
        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_overview, R.id.bb_fab_menu_overview);

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = BudgetBeaverRecyclerHandler
                .createFragmentRecyclerView(R.id.recycler_container, fragmentView, getContext());

        //Load all accounts
        dbDispatcher = new DatabaseDispatcher(getContext());
        allAccounts = dbDispatcher.Accounts.getAccounts();
        Settings appSettings = new Settings(getContext());

        //set account adapter on the recycle view
        accountAdapter = new AccountAdapter(allAccounts, appSettings, this);
        recyclerViewLayout.setAdapter(accountAdapter);

        registerFabClickEvents(getActivity());

        return fragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //TODO: there must be a better way!
        new android.os.Handler().postDelayed( new Runnable() {
            public void run() {
                ArrayList<Account> accountIntegrity = dbDispatcher.Accounts.getAccounts();
                if(accountIntegrity.size() != allAccounts.size()){
                    accountAdapter.updateData(accountIntegrity);
                }
            }
        //about the length of Snackbar.LENGTH_LONG
        }, 3000);
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
        launchNewFragment(editAccountFragment);
    }

    public void addTransaction() {
        Fragment editTransactionFragment = new EditTransactionFragment();
        Bundle bundle = generateIdBundle("transactionId", -1);
        bundle.putInt("accountId", -1);
        editTransactionFragment.setArguments(bundle);
        launchNewFragment(editTransactionFragment);
    }

    public void addReminder() {
        Fragment editReminderFragment = new EditReminderFragment();
        editReminderFragment.setArguments(generateIdBundle("reminderId", -1));
        launchNewFragment(editReminderFragment);
    }

    public void launchAccountFragment(int accountId) {
        Fragment accountFragment = new AccountFragment();
        accountFragment.setArguments(generateIdBundle("accountId", accountId));
        launchNewFragment(accountFragment);
    }

    private Bundle generateIdBundle(String name, int id) {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt(name, id);
        return dataBundle;
    }

    private void launchNewFragment(Fragment newFragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, newFragment)
                .addToBackStack(null)
                .commit();
    }

    public void deleteAccount(int accountId) {
        final Account accountToDelete = dbDispatcher.Accounts.getAccountById(accountId);
        final ArrayList<Transaction> accountTransactions = dbDispatcher.Transactions.getTransactionsForAccount(accountId);
        final ArrayList<Reminder> accountReminders = dbDispatcher.Reminders.getRemindersForAccount(accountId);

        accountToDelete.setIsActive(false);
        dbDispatcher.Accounts.updateAccount(accountToDelete);

        for(int i = 0; i < accountTransactions.size(); i++){
            accountTransactions.get(i).setIsActive(false);
            dbDispatcher.Transactions.updateTransaction(accountTransactions.get(i));
        }
        for(int r = 0; r < accountReminders.size(); r++){
            accountReminders.get(r).setIsActive(false);
            dbDispatcher.Reminders.updateReminder(accountReminders.get(r));
        }

        ArrayList<Account> allAccounts = dbDispatcher.Accounts.getAccounts();
        accountAdapter.updateData(allAccounts);

        Snackbar snack = SnackBarHandler.generateActionableSnackBar(getView(), accountToDelete.getName() + " deleted" );
        snack.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < accountTransactions.size(); i++) {
                    accountTransactions.get(i).setIsActive(true);
                    dbDispatcher.Transactions.updateTransaction(accountTransactions.get(i));
                }
                for (int r = 0; r < accountReminders.size(); r++) {
                    accountReminders.get(r).setIsActive(true);
                    dbDispatcher.Reminders.updateReminder(accountReminders.get(r));
                }

                accountToDelete.setIsActive(true);
                dbDispatcher.Accounts.updateAccount(accountToDelete);

                accountAdapter.updateData(dbDispatcher.Accounts.getAccounts());
            }
        } );
        snack.show();
    }
}
