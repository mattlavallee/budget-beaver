package io.github.mattlavallee.budgetbeaver.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.handlers.FragmentManagementHandler;
import io.github.mattlavallee.budgetbeaver.handlers.SnackBarHandler;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;

public class EditReminderFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private ArrayList<Account> allAccounts;

    private EditText message;
    private EditText dayOfMonth;
    private EditText daysUntilExpiration;
    private TextView preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //retrieve the account id from the bundle
        int reminderId = getArguments().getInt("reminderId");
        dbDispatcher = new DatabaseDispatcher(getContext());

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_edit_reminder, container, false);

        message = (EditText)fragmentView.findViewById(R.id.edit_reminder_message);
        dayOfMonth = (EditText)fragmentView.findViewById(R.id.edit_reminder_dayOfMonth);
        daysUntilExpiration = (EditText)fragmentView.findViewById(R.id.edit_reminder_daysUntilExpiration);
        preview = (TextView)fragmentView.findViewById(R.id.edit_reminder_preview);

        Reminder currentReminder = new Reminder();
        if(reminderId == -1) {
            getActivity().setTitle("Add a Reminder");
        } else{
            currentReminder = dbDispatcher.Reminders.getReminderById(reminderId);
            getActivity().setTitle("Edit Reminder");
            initializeFields(currentReminder);
        }

        //no FAB on Add/Edit account layout
        RelativeLayout parent = (RelativeLayout)getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        initializeButtons(fragmentView, reminderId, currentReminder.getAccountId());
        populateAccountSpinner(fragmentView, currentReminder.getAccountId());
        registerEditTextListeners();

        final EditText firstEntry = (EditText)fragmentView.findViewById(R.id.edit_reminder_message);
        firstEntry.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(firstEntry, 0);
            }
        }, 100);

        return fragmentView;
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
        FragmentManagementHandler.closeFragment(getActivity());
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
        EditText messageField = (EditText)view.findViewById(R.id.edit_reminder_message);
        EditText dayOfMonthField = (EditText)view.findViewById(R.id.edit_reminder_dayOfMonth);
        EditText daysUntilExpirationField = (EditText)view.findViewById(R.id.edit_reminder_daysUntilExpiration);
        if(accountId == -1){
            Spinner accountSelect = (Spinner)view.findViewById(R.id.edit_reminder_account);
            int selectedAccountPosition = accountSelect.getSelectedItemPosition();
            try{
                accountId = allAccounts.get(selectedAccountPosition).getId();
            } catch(Exception except){
                SnackBarHandler.generateSnackBar(view, "Could not get account information").show();
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
            SnackBarHandler.generateSnackBar(view, "You must include a message for the reminder").show();
            return;
        }
        if(dayOfMonth < 1 || dayOfMonth > 31){
            SnackBarHandler.generateSnackBar(view, "Day of month must be between 1 and 31").show();
            return;
        }
        if(daysUntilExpiration > 31){
            String daysUntilMsg ="Reminders must expire within a month. If you prefer to have " +
                    "an indefinite reminder, leave it blank!";
            SnackBarHandler.generateSnackBar(view, daysUntilMsg).show();
            return;
        }

        Reminder reminderToSave = new Reminder(reminderId, accountId, message, dayOfMonth,
                daysUntilExpiration, new Date(0), false, true );
        long result;
        if(reminderId == -1){
            result = dbDispatcher.Reminders.insertReminder(reminderToSave);
        } else{
            Reminder existingReminder = dbDispatcher.Reminders.getReminderById(reminderId);
            // if nothing was edited but the reminder was saved again,
            // don't clear out active state and last date activated fields
            if(existingReminder.getMessage().equals(reminderToSave.getMessage()) &&
               existingReminder.getDayOfMonth() == reminderToSave.getDayOfMonth() &&
               existingReminder.getDaysUntilExpiration() == reminderToSave.getDaysUntilExpiration()){
                reminderToSave.setLastDateActivated(existingReminder.getLastDateActivated());
                reminderToSave.setIsNotificationActive(existingReminder.isNotificationActive());
            }

            result = dbDispatcher.Reminders.updateReminder(reminderToSave);
        }

        if(result == -1){
            SnackBarHandler.generateSnackBar(view, "There was an error saving the reminder").show();
            return;
        }

        closeEditReminderFragment();
    }

    private void initializeFields(Reminder reminder){
        preview.setText(generatePreviewText(reminder));
        //Note: account spinner is handled in populateAccountSpinner

        message.setText(reminder.getMessage());
        dayOfMonth.setText(String.valueOf(reminder.getDayOfMonth()));
        if(reminder.getDaysUntilExpiration() >= 0){
            daysUntilExpiration.setText(String.valueOf(reminder.getDaysUntilExpiration()));
        }
    }

    private int getPreviewInteger(String val, int defaultVal){
        return val.length() == 0 ? defaultVal : Integer.parseInt(val);
    }
    private void registerEditTextListeners(){
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String message = editable.toString();
                int day = getPreviewInteger(dayOfMonth.getText().toString(), 1);
                int expire = getPreviewInteger(daysUntilExpiration.getText().toString(), -1);
                preview.setText(generatePreviewText(new Reminder(-1, -1, message, day, expire, new Date(), false, true)));
            }
        });

        dayOfMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String msg = message.getText().toString();
                int day = getPreviewInteger(editable.toString(), 1);
                int expire = getPreviewInteger(daysUntilExpiration.getText().toString(), -1);
                preview.setText(generatePreviewText(new Reminder(-1, -1, msg, day, expire, new Date(), false, true)));
            }
        });

        daysUntilExpiration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String msg = message.getText().toString();
                int day = getPreviewInteger(dayOfMonth.getText().toString(), 1);
                int expire = getPreviewInteger(editable.toString(), -1);
                preview.setText(generatePreviewText(new Reminder(-1, -1, msg, day, expire, new Date(), false, true)));
            }
        });
    }

    private String generatePreviewText(Reminder reminder){
        ArrayList<Integer> suffixSt = new ArrayList<>(Arrays.asList(1, 21, 31));
        ArrayList<Integer> suffixNd = new ArrayList<>(Arrays.asList(2, 22));
        ArrayList<Integer> suffixRd = new ArrayList<>(Arrays.asList(3, 23));

        int monthDay = reminder.getDayOfMonth();
        int expireDays = reminder.getDaysUntilExpiration();

        String suffix = suffixSt.contains(monthDay) ? "st" :
                suffixNd.contains(monthDay) ? "nd" :
                        suffixRd.contains(monthDay) ? "rd" : "th";
        String dayOrDays = expireDays <= 1 ? "day" : "days";

        return reminder.getMessage() + " \n\nwill notify on the " + monthDay + suffix +
                " day of the month" + (expireDays > 0 ? " and will expire after " + expireDays + " " + dayOrDays : "");
    }
}
