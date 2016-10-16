package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;

public class EditTransactionFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the transaction id from the bundle
        int transactionId = getArguments().getInt("transactionId");
        int accountId = getArguments().getInt("accountId");

        dbDispatcher = new DatabaseDispatcher(getContext());

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_edit_transaction, container, false);
        if(transactionId == -1) {
            getActivity().setTitle("Add a Transaction");
        } else{
            getActivity().setTitle("Edit Transaction");
        }

        //no FAB on Add/Edit transaction layout
        RelativeLayout parent = (RelativeLayout)getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        initializeButtons( fragmentView );

        return fragmentView;
    }

    private void closeEditTransactionFragment() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void initializeButtons(View view ){
        Button cancel = (Button)view.findViewById(R.id.edit_transaction_cancel_btn);
        Button save = (Button)view.findViewById(R.id.edit_transaction_save_btn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeEditTransactionFragment();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: actually save something
                closeEditTransactionFragment();
            }
        });
    }
}
