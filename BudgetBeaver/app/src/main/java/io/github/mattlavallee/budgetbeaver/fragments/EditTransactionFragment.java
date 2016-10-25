package io.github.mattlavallee.budgetbeaver.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Transaction;

public class EditTransactionFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private ArrayList<Account> allAccounts;
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
            initializeFields(fragmentView, transactionId);
        }

        //no FAB on Add/Edit transaction layout
        RelativeLayout parent = (RelativeLayout)getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        initializeButtons( fragmentView, transactionId, accountId );
        populateAccountSpinner( fragmentView, accountId );

        return fragmentView;
    }

    private void closeEditTransactionFragment() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void initializeButtons(final View view, final int transactionId, final int accountId ){
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
            public void onClick(View v) {
                saveTransaction( view, transactionId, accountId );
            }
        });
    }

    private void initializeFields(final View view, final int transactionId ){
        Transaction transToEdit = dbDispatcher.Transactions.getTransactionById(transactionId);

        EditText location = (EditText)view.findViewById(R.id.edit_transaction_location);
        EditText amount = (EditText)view.findViewById(R.id.edit_transaction_amount);
        EditText description = (EditText)view.findViewById(R.id.edit_transaction_description);
        DatePicker date = (DatePicker)view.findViewById(R.id.edit_transaction_date);

        location.setText(transToEdit.getLocation());
        description.setText(transToEdit.getDescription());
        amount.setText(Double.toString(transToEdit.getAmount()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(transToEdit.getTransactionDate());
        date.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private void saveTransaction( View view, int transactionId, int accountId ){
        EditText location = (EditText)view.findViewById(R.id.edit_transaction_location);
        EditText amount = (EditText)view.findViewById(R.id.edit_transaction_amount);
        EditText description = (EditText)view.findViewById(R.id.edit_transaction_description);
        DatePicker date = (DatePicker)view.findViewById(R.id.edit_transaction_date);
        //accountId is -1 if we're adding a transaction from the Overview screen
        if(accountId == -1){
            //try to get the user selected account
            Spinner accountSelect = (Spinner)view.findViewById(R.id.edit_transaction_account);
            int selectedAccountPosition = accountSelect.getSelectedItemPosition();
            try {
                accountId = allAccounts.get(selectedAccountPosition).getId();
            } catch(NullPointerException except){
                displaySnack("Could not get account information", view);
                return;
            }
        }
        String transLocation = location.getText().toString();
        String transDescription = description.getText().toString();
        double transAmount = 0.0;
        if(!amount.getText().toString().isEmpty()){
            transAmount = Double.parseDouble(amount.getText().toString());
        }
        Date transDate = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth()).getTime();

        if(transLocation.length() < 3){
            displaySnack("Location must be at least 3 letters", view);
            return;
        } else if( transAmount == 0.0){
            displaySnack("Transaction must have a non-zero value", view);
            return;
        }

        Transaction transToSave = new Transaction(transactionId, accountId, transLocation,
                transDescription, transAmount, transDate, true);
        long result;
        if(transactionId == -1){
            result = dbDispatcher.Transactions.insertTransaction(transToSave);
        } else{
            result = dbDispatcher.Transactions.updateTransaction(transToSave);
        }

        if(result == -1){
            displaySnack("There was an error saving the transaction", view);
            return;
        }
        closeEditTransactionFragment();
    }

    private void populateAccountSpinner(View view, int accountId){
        //get all account names and get the active account's position in the dropdown
        allAccounts = dbDispatcher.Accounts.getAccounts();
        Collections.sort(allAccounts, new Comparator<Account>() {
            @Override
            public int compare(Account account, Account t1) {
                return account.getName().compareTo(t1.getName());
            }
        });
        ArrayList<String> adapterValues = new ArrayList<>();
        int activePosition = -1;
        for(int i = 0; i < allAccounts.size(); i++){
            adapterValues.add(allAccounts.get(i).getName());

            if(allAccounts.get(i).getId() == accountId){
                activePosition = i;
            }
        }

        Spinner accountDropdown = (Spinner)view.findViewById(R.id.edit_transaction_account);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.support_simple_spinner_dropdown_item,  adapterValues);
        accountDropdown.setAdapter(adapter);
        accountDropdown.setSelection(activePosition);

        //if we're in a specific account, don't let the user change it
        if(accountId != -1){
            accountDropdown.setEnabled(false);
        }
    }

    private void displaySnack(String message, View view) {
        Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.CYAN);
        snack.show();
    }
}
