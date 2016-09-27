package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle(activeAccount.getName() + " ($0.00)");

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = BudgetBeaverRecyclerHandler
                .createFragmentRecyclerView(R.id.account_recycler, fragmentView, getContext());

        //load all transactions
        ArrayList<Transaction> allTransactions = new ArrayList();
        allTransactions.add(new Transaction(1, accountId, "Amazon", "Gift", 13.7, true));
        allTransactions.add(new Transaction(2, accountId, "Big Y", "Groceries for the week", 65.17, true));
        allTransactions.add(new Transaction(3, accountId, "Starbucks", "Gotta get that caffeine", 3.46, true));
        allTransactions.add(new Transaction(4, accountId, "Home Depot", "Getting some tools to keep the house from falling apart", 40, true));
        allTransactions.add(new Transaction(5, accountId, "Spa", "Gosh I'm exhausted", 80.70, true));

        transAdapter = new TransactionAdapter(allTransactions, this);
        recyclerViewLayout.setAdapter(transAdapter);

        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_account, R.id.bb_fab_menu_account);

        return fragmentView;
    }
}
