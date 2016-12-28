package io.github.mattlavallee.budgetbeaver.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;

public class EditReminderFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private ArrayList<Account> allAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the account id from the bundle
        int reminderId = getArguments().getInt("reminderId");
        dbDispatcher = new DatabaseDispatcher(getContext());

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_edit_reminder, container, false);
        Reminder currentReminder = new Reminder();
        if(reminderId == -1) {
            getActivity().setTitle("Add a Reminder");
        } else{
            currentReminder = dbDispatcher.Reminders.getReminderById(reminderId);
            getActivity().setTitle("Edit Reminder");
            initializeFields(fragmentView, currentReminder);
        }

        //no FAB on Add/Edit account layout
        RelativeLayout parent = (RelativeLayout)getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        initializeButtons(fragmentView, reminderId, currentReminder.getAccountId());
        populateAccountSpinner(fragmentView, currentReminder.getAccountId());
        return fragmentView;
    }

    private void displaySnack(String message, View view) {
        Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.CYAN);
        snack.show();
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

        Spinner accountDropdown = (Spinner)view.findViewById(R.id.edit_reminder_account);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.support_simple_spinner_dropdown_item,  adapterValues);
        accountDropdown.setAdapter(adapter);
        accountDropdown.setSelection(activePosition);

        //if we're in a specific account, don't let the user change it
        if(accountId != -1){
            accountDropdown.setEnabled(false);
        }
    }

    private void closeEditReminderFragment() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void initializeButtons(final View view, final int reminderId, final int accountId){
        Button cancel = (Button)view.findViewById(R.id.edit_reminder_cancel_btn);
        Button save = (Button)view.findViewById(R.id.edit_reminder_save_btn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeEditReminderFragment();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder( view, reminderId, accountId );
            }
        });
    }

    private void saveReminder( View view, int reminderId, int accountId ){
        EditText messageField = (EditText)view.findViewById(R.id.edit_reminder_messsage);
        EditText dayOfMonthField = (EditText)view.findViewById(R.id.edit_reminder_dayOfMonth);
        EditText daysUntilExpirationField = (EditText)view.findViewById(R.id.edit_reminder_daysUntilExpiration);
        if(accountId == -1){
            Spinner accountSelect = (Spinner)view.findViewById(R.id.edit_reminder_account);
            int selectedAccountPosition = accountSelect.getSelectedItemPosition();
            try{
                accountId = allAccounts.get(selectedAccountPosition).getId();
            } catch(NullPointerException except){
                displaySnack("Could not get account information", view);
                return;
            }
        }

        String message = messageField.getText().toString();
        int dayOfMonth = -1;
        int daysUntilExpiration = -1;
        if(!dayOfMonthField.getText().toString().isEmpty()){
            dayOfMonth = Integer.parseInt(dayOfMonthField.getText().toString());
        }
        if(!daysUntilExpirationField.getText().toString().isEmpty()){
            daysUntilExpiration = Integer.parseInt(daysUntilExpirationField.getText().toString());
        }

        if(message.length() < 3){
            displaySnack("You must include a message for the reminder", view);
            return;
        }
        if(dayOfMonth < 1 || dayOfMonth > 31){
            displaySnack("Day of month must be between 1 and 31", view);
            return;
        }
        if(daysUntilExpiration > 31){
            displaySnack("Reminders must expire within a month.  If you prefer to have an indefinite " +
            "reminder, leave it blank!", view);
            return;
        }

        Reminder reminderToSave = new Reminder(reminderId, accountId, message, dayOfMonth,
                daysUntilExpiration, new Date(Long.MAX_VALUE), false, true );
        long result;
        if(reminderId == -1){
            result = dbDispatcher.Reminders.insertReminder(reminderToSave);
        } else{
            result = dbDispatcher.Reminders.updateReminder(reminderToSave);
        }

        if(result == -1){
            displaySnack("There was an error saving the reminder", view);
            return;
        }

        closeEditReminderFragment();
    }

    private void initializeFields(View view, Reminder reminder){
        EditText message = (EditText)view.findViewById(R.id.edit_reminder_messsage);
        EditText dayOfMonth = (EditText)view.findViewById(R.id.edit_reminder_dayOfMonth);
        EditText daysUntilExpiration = (EditText)view.findViewById(R.id.edit_reminder_daysUntilExpiration);
        //Note: account spinner is handled in populateAccountSpinner

        message.setText(reminder.getMessage());
        dayOfMonth.setText(reminder.getDayOfMonth());
        if(reminder.getDaysUntilExpiration() >= 0){
            daysUntilExpiration.setText(reminder.getDaysUntilExpiration());
        }
    }
}
