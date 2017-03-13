package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.handlers.FragmentManagementHandler;
import io.github.mattlavallee.budgetbeaver.handlers.SnackBarHandler;
import io.github.mattlavallee.budgetbeaver.models.Account;

public class EditAccountFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the account id from the bundle
        final int accountId = getArguments().getInt("accountId");

        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_account, container, false);
        dbDispatcher = new DatabaseDispatcher(getContext());

        if (accountId == -1) {
            getActivity().setTitle("Add an Account");
        } else {
            getActivity().setTitle("Edit Account");
            Account currentAccount = dbDispatcher.Accounts.getAccountById( accountId );
            EditText accountNameContainer = (EditText)fragmentView.findViewById(R.id.edit_account_name);
            accountNameContainer.setText(currentAccount.getName());
        }

        //no FAB on Add/Edit account layout
        RelativeLayout parent = (RelativeLayout) getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        Button cancelButton = (Button) fragmentView.findViewById(R.id.edit_account_cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeEditAccountFragment();
            }
        });

        Button saveButton = (Button) fragmentView.findViewById(R.id.edit_account_save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccount(accountId, fragmentView);
            }
        });

        return fragmentView;
    }

    private void closeEditAccountFragment() {
        FragmentManagementHandler.closeFragment(getActivity());
    }

    private void saveAccount(int accountId, View view) {
        EditText accountNameContainer = (EditText) view.findViewById(R.id.edit_account_name);
        String accountName = accountNameContainer.getText().toString();

        if (new String(accountName).equals("") || accountName.length() < 3) {
            SnackBarHandler.generateSnackBar(view, "Account name must be at least 3 letters long").show();
            return;
        }

        Account currentAccount = new Account(accountId, accountName, 1);
        long result;
        if (accountId == -1) {
            result = dbDispatcher.Accounts.insertAccount(currentAccount);
        } else {
            result = dbDispatcher.Accounts.updateAccount(currentAccount);
        }

        if (result == -1) {
            SnackBarHandler.generateSnackBar(view, "There was an error saving the account").show();
            return;
        }
        closeEditAccountFragment();
    }
}
