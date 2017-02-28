package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.tokenautocomplete.TokenCompleteTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.handlers.FragmentManagementHandler;
import io.github.mattlavallee.budgetbeaver.handlers.SnackBarHandler;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Tag;
import io.github.mattlavallee.budgetbeaver.models.Transaction;
import io.github.mattlavallee.budgetbeaver.models.adapters.TagCompletionView;

public class EditTransactionFragment extends Fragment implements TokenCompleteTextView.TokenListener{
    private DatabaseDispatcher dbDispatcher;
    private ArrayList<Account> allAccounts;
    private ArrayList<Tag> addedTags;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the transaction id from the bundle
        int transactionId = getArguments().getInt("transactionId");
        int accountId = getArguments().getInt("accountId");
        addedTags = new ArrayList<>();

        dbDispatcher = new DatabaseDispatcher(getContext());

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_edit_transaction, container, false);

        //set the max date for the date picker dynamically...no future transactions!
        DatePicker date = (DatePicker)fragmentView.findViewById(R.id.edit_transaction_date);
        date.setMaxDate(new Date().getTime());

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
        initializeTagsTypeahead( fragmentView);
        populateAccountSpinner( fragmentView, accountId );

        return fragmentView;
    }

    private void closeEditTransactionFragment() {
        FragmentManagementHandler.closeFragment(getActivity());
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

    private void initializeTagsTypeahead(View view){
        //get typeahead data
        ArrayList<Tag> typeaheadData = dbDispatcher.Tags.getUniqueTags();
        TagCompletionView tagTypeahead = (TagCompletionView)view.findViewById(R.id.edit_transaction_tags);
        //set tag adapater
        ArrayAdapter<Tag> tagTypeaheadAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, typeaheadData);
        tagTypeahead.setAdapter(tagTypeaheadAdapter);
        //begin typeahead results at 1 character
        tagTypeahead.setThreshold(1);
        //user can't select duplicates!
        tagTypeahead.allowDuplicates(false);
        //set add/remove listeners
        tagTypeahead.setTokenListener(this);
    }

    private void initializeFields(final View view, final int transactionId ){
        Transaction transToEdit = dbDispatcher.Transactions.getTransactionById(transactionId);

        EditText location = (EditText)view.findViewById(R.id.edit_transaction_location);
        EditText amount = (EditText)view.findViewById(R.id.edit_transaction_amount);
        EditText description = (EditText)view.findViewById(R.id.edit_transaction_description);
        CheckBox applyAsDeduction = (CheckBox)view.findViewById(R.id.edit_transaction_deduction);
        DatePicker date = (DatePicker)view.findViewById(R.id.edit_transaction_date);

        location.setText(transToEdit.getLocation());
        description.setText(transToEdit.getDescription());
        double transAmount = transToEdit.getAmount();
        if(transAmount < 0){
            transAmount *= -1;
            applyAsDeduction.setChecked(true);
        }
        amount.setText(new DecimalFormat("0.00").format( transAmount ));

        Calendar cal = Calendar.getInstance();
        cal.setTime(transToEdit.getTransactionDate());
        date.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        addedTags = dbDispatcher.Tags.getTagsForTransaction(transactionId);
        //restore tags for the transaction
        TagCompletionView tagTypeahead = (TagCompletionView)view.findViewById(R.id.edit_transaction_tags);
        for(Tag tag : addedTags){
            tagTypeahead.addObject(tag);
        }
    }

    private void saveTransaction( View view, int transactionId, int accountId ){
        EditText location = (EditText)view.findViewById(R.id.edit_transaction_location);
        EditText amount = (EditText)view.findViewById(R.id.edit_transaction_amount);
        EditText description = (EditText)view.findViewById(R.id.edit_transaction_description);
        CheckBox applyDeduction = (CheckBox)view.findViewById(R.id.edit_transaction_deduction);
        DatePicker date = (DatePicker)view.findViewById(R.id.edit_transaction_date);
        //accountId is -1 if we're adding a transaction from the Overview screen
        if(accountId == -1){
            //try to get the user selected account
            Spinner accountSelect = (Spinner)view.findViewById(R.id.edit_transaction_account);
            int selectedAccountPosition = accountSelect.getSelectedItemPosition();
            try {
                accountId = allAccounts.get(selectedAccountPosition).getId();
            } catch(Exception except){
                SnackBarHandler.generateSnackBar(view, "Could not get account information").show();
                return;
            }
        }
        String transLocation = location.getText().toString();
        String transDescription = description.getText().toString();
        double transAmount = 0.0;
        if(!amount.getText().toString().isEmpty()){
            transAmount = Double.parseDouble(amount.getText().toString());
        }
        if(applyDeduction.isChecked()){
            transAmount *= -1;
        }
        Date transDate = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth()).getTime();

        if(transLocation.length() < 3){
            SnackBarHandler.generateSnackBar(view, "Location must be at least 3 letters").show();
            return;
        } else if( transAmount == 0.0){
            SnackBarHandler.generateSnackBar(view, "Transaction must hav ea non-zero value").show();
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
            SnackBarHandler.generateSnackBar(view, "There was an error saving the transaction").show();
            return;
        }

        int currentTransactionId = transactionId == -1 ? (int)result : transactionId;
        //Handle tags for the transaction!
        ArrayList<Tag> tagsOnTransaction = dbDispatcher.Tags.getTagsForTransaction(transactionId);
        //get all new tags
        long addResult = dbDispatcher.Tags.insertTags(findTagsToAdd(currentTransactionId, tagsOnTransaction));
        //remove tags
        long removalResult = dbDispatcher.Tags.deleteTags(findTagsToRemove(tagsOnTransaction));
        if(addResult < 0 || removalResult < 0){
            SnackBarHandler.generateSnackBar(view, "There was an error updating tags on the transaction").show();
            return;
        }

        closeEditTransactionFragment();
    }

    private ArrayList<Tag> findTagsToAdd(int transactionId, ArrayList<Tag> existingTags){
        ArrayList<Tag> newTags = new ArrayList<>();
        for(Tag currTag : addedTags ){
            boolean found = false;
            for(Tag existingTag : existingTags ){
                if (existingTag.getTagName() != null && existingTag.getTagName().toLowerCase().equals(currTag.getTagName().toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if(!found){
                newTags.add(new Tag(-1, transactionId, currTag.getTagName()));
            }
        }
        return newTags;
    }

    private ArrayList<Tag> findTagsToRemove(ArrayList<Tag> existingTags){
        ArrayList<Tag> deletedTags = new ArrayList<>();
        for(Tag existingTag : existingTags){
            boolean found = false;
            for(Tag currTag : addedTags){
                if (currTag.getTagName() != null && currTag.getTagName().toLowerCase().equals(existingTag.getTagName().toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if(!found){
                deletedTags.add(existingTag);
            }
        }
        return deletedTags;
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

    @Override
    public void onTokenAdded(Object token) {
        boolean found = false;
        for(Tag tag : addedTags ){
            if(tag.getTagName() != null && tag.getTagName().toLowerCase().equals(token.toString().toLowerCase())){
                found = true;
                break;
            }
        }
        if(!found){
            addedTags.add(new Tag(-1, -1, token.toString()));
        }
    }

    @Override
    public void onTokenRemoved(Object token) {
        int tagIndex = -1;
        for(int i = 0; i < addedTags.size(); i++ ){
            Tag tag = addedTags.get(i);
            if(tag.getTagName() != null && tag.getTagName().toLowerCase().equals(token.toString().toLowerCase())){
                tagIndex = i;
                break;
            }
        }
        if(tagIndex >= 0) {
            addedTags.remove(tagIndex);
        }
    }
}
