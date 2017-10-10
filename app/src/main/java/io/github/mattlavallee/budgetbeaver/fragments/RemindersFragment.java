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

import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverRecyclerHandler;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;
import io.github.mattlavallee.budgetbeaver.models.adapters.ReminderAdapter;

public class RemindersFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private ReminderAdapter reminderAdapter;
    private ArrayList<Reminder> allReminders;
    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.recycler_view, container, false);
        getActivity().setTitle("Reminders");


        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_reminder, R.id.bb_fab_menu_reminder);

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = BudgetBeaverRecyclerHandler
                .createFragmentRecyclerView(R.id.recycler_container, fragmentView, getContext());

        //load all reminders
        dbDispatcher = new DatabaseDispatcher(getContext());
        allReminders = dbDispatcher.Reminders.getReminders();

        reminderAdapter = new ReminderAdapter(allReminders, this, dbDispatcher);
        recyclerViewLayout.setAdapter(reminderAdapter);

        registerFabClickEvents(getActivity());

        TextView emptyMessage = (TextView)fragmentView.findViewById(R.id.empty_recycler_message);
        emptyMessage.setText("Use the button below to add a reminder");
        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                R.id.empty_recycler_message, allReminders.size());

        return fragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //TODO: there must be a better way!
        new android.os.Handler().postDelayed( new Runnable() {
            public void run() {
                ArrayList<Reminder> reminderIntegrity = dbDispatcher.Reminders.getReminders();
                if(reminderIntegrity.size() != allReminders.size()){
                    reminderAdapter.updateData(reminderIntegrity);
                    BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                            R.id.empty_recycler_message, reminderIntegrity.size());
                }
            }
            //about the length of Snackbar.LENGTH_LONG
        }, 3000);
    }

    public void editReminder(int reminderId){
        addEditReminder(reminderId);
    }

    public void deleteReminders(){
        final ArrayList<Reminder> remindersToDelete = dbDispatcher.Reminders.getReminders();
        for(int i = 0; i < remindersToDelete.size(); i++){
            remindersToDelete.get(i).setIsActive(false);
            dbDispatcher.Reminders.updateReminder(remindersToDelete.get(i));
        }
        reminderAdapter.updateData(new ArrayList<Reminder>());
        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                R.id.empty_recycler_message, 0);

        Snackbar snack = Snackbar
                .make(getView(), "All reminders deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        for(int i = 0; i < remindersToDelete.size(); i++){
                            remindersToDelete.get(i).setIsActive(true);
                            dbDispatcher.Reminders.updateReminder(remindersToDelete.get(i));
                        }
                        reminderAdapter.updateData(remindersToDelete);
                        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                                R.id.empty_recycler_message, remindersToDelete.size());
                    }
                });
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        TextView actionText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(Color.CYAN);
        snack.show();
    }

    public void deleteReminder(int reminderId){
        final Reminder reminderToDelete = dbDispatcher.Reminders.getReminderById(reminderId);
        reminderToDelete.setIsActive(false);
        dbDispatcher.Reminders.updateReminder(reminderToDelete);

        Account accountForReminder = dbDispatcher.Accounts.getAccountById(reminderToDelete.getAccountId());
        ArrayList<Reminder> reminders = dbDispatcher.Reminders.getReminders();
        reminderAdapter.updateData(reminders);
        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                R.id.empty_recycler_message, reminders.size());

        Snackbar snack = Snackbar
                .make(getView(), accountForReminder.getName() + " reminder deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        reminderToDelete.setIsActive(true);
                        dbDispatcher.Reminders.updateReminder(reminderToDelete);
                        ArrayList<Reminder> reminderState = dbDispatcher.Reminders.getReminders();
                        reminderAdapter.updateData(reminderState);
                        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                                R.id.empty_recycler_message, reminderState.size());
                    }
                });
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        TextView actionText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(Color.CYAN);
        snack.show();
    }

    private void registerFabClickEvents(FragmentActivity view) {
        FloatingActionButton reminderBtn = (FloatingActionButton) view.findViewById(R.id.fab_reminder_add_reminder);
        FloatingActionButton deleteBtn = (FloatingActionButton) view.findViewById(R.id.fab_reminder_delete_reminders);

        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditReminder(-1);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deleteReminders();
            }
        });
    }

    private void addEditReminder(int reminderId){
        Fragment editReminderFragment = new EditReminderFragment();
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("reminderId", reminderId);
        editReminderFragment.setArguments(dataBundle);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, editReminderFragment)
                .addToBackStack(null)
                .commit();
    }
}
