package io.github.mattlavallee.budgetbeaver.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.BudgetBeaverRecyclerHandler;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Transaction;
import io.github.mattlavallee.budgetbeaver.models.adapters.TransactionAdapter;

public class AccountFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private TransactionAdapter transAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the account id from the bundle
        int accountId = getArguments().getInt("accountId");

        dbDispatcher = new DatabaseDispatcher(getContext());
        Account activeAccount = dbDispatcher.Accounts.getAccountById(accountId);

        //load all transactions
        ArrayList<Transaction> allTransactions = dbDispatcher.Transactions.getTransactionsForAccount(accountId);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_account, container, false);
        String transactionTotal = Transaction.getFormattedTotal(allTransactions);
        getActivity().setTitle(activeAccount.getName() + " (" + transactionTotal + ")");

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = BudgetBeaverRecyclerHandler
                .createFragmentRecyclerView(R.id.account_recycler, fragmentView, getContext());

        transAdapter = new TransactionAdapter(allTransactions, this);
        recyclerViewLayout.setAdapter(transAdapter);

        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_account, R.id.bb_fab_menu_account);

        registerFabClickEvents( getActivity(), accountId );
        return fragmentView;
    }

    private void registerFabClickEvents(FragmentActivity view, final int accountId) {
        FloatingActionButton addTransactionBtn = (FloatingActionButton) view.findViewById(R.id.fab_account_add_transaction);
        FloatingActionButton deleteAccountBtn = (FloatingActionButton) view.findViewById(R.id.fab_account_delete_account);
        FloatingActionButton deleteTransactionsBtn = (FloatingActionButton) view.findViewById(R.id.fab_account_delete_all_transactions);
        FloatingActionButton sortTransactionsBtn = (FloatingActionButton) view.findViewById(R.id.fab_account_sort);

        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrEditTransaction(-1, accountId);
            }
        });
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(accountId);
            }
        });
        deleteTransactionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { deleteTransactions();
            }
        });
        sortTransactionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTransactions();
            }
        });
    }

    public void addOrEditTransaction(int transactionId, int accountId){
        Fragment addTransactionFragment = new EditTransactionFragment();
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("transactionId", transactionId);
        dataBundle.putInt("accountId", accountId);
        addTransactionFragment.setArguments(dataBundle);

        launchNewFragment(addTransactionFragment);
    }

    //TODO: set active=false on all transactions associated with account as well
    public void deleteAccount(int accountId){
        final Account accountToDelete = dbDispatcher.Accounts.getAccountById(accountId);

        accountToDelete.setIsActive(false);
        dbDispatcher.Accounts.updateAccount(accountToDelete);

        Snackbar snack = Snackbar.make(getView(), accountToDelete.getName() + " deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accountToDelete.setIsActive(true);
                        dbDispatcher.Accounts.updateAccount(accountToDelete);
                    }
                });
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        TextView actionText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(Color.CYAN);
        snack.show();

        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void deleteTransactions(){
        Snackbar.make(getView(), "TODO: delete all transactions", Snackbar.LENGTH_SHORT).show();
    }

    public void sortTransactions(){
        Snackbar.make(getView(), "TODO: sort transactions", Snackbar.LENGTH_SHORT).show();
    }

    public void editTransaction( int transactionId ){
        Transaction trans = dbDispatcher.Transactions.getTransactionById(transactionId);
        addOrEditTransaction( transactionId, trans.getAccountId() );
    }

    public void deleteTransaction( int transactionId ){
        final Transaction activeTransaction = dbDispatcher.Transactions.getTransactionById(transactionId);
        activeTransaction.setIsActive(false);
        dbDispatcher.Transactions.updateTransaction( activeTransaction );

        ArrayList<Transaction> allTransactions = dbDispatcher.Transactions.getTransactionsForAccount(activeTransaction.getAccountId());
        transAdapter.updateData(allTransactions);

        Snackbar snack = Snackbar.make(getView(), activeTransaction.getLocation() + " deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activeTransaction.setIsActive(true);
                        dbDispatcher.Transactions.updateTransaction(activeTransaction);

                        transAdapter.updateData(dbDispatcher.Transactions.getTransactionsForAccount(activeTransaction.getAccountId()));
                    }
                });
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        TextView actionText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(Color.CYAN);
        snack.show();
    }

    private void launchNewFragment(Fragment newFragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, newFragment)
                .addToBackStack(null)
                .commit();
    }
}
