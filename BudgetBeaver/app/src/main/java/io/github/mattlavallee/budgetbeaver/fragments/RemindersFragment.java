package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
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
import io.github.mattlavallee.budgetbeaver.models.Reminder;
import io.github.mattlavallee.budgetbeaver.models.adapters.ReminderAdapter;

public class RemindersFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private ReminderAdapter reminderAdapter;
    private ArrayList<Reminder> allReminders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_reminders, container, false);
        getActivity().setTitle("Budget Beaver - Reminders");

        BudgetBeaverFabSetup.addFabToView(getActivity(), inflater, R.id.budget_beaver_fragment_wrapper,
                R.layout.fab_reminder, R.id.bb_fab_menu_reminder);

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = BudgetBeaverRecyclerHandler
                .createFragmentRecyclerView(R.id.reminders_recycler, fragmentView, getContext());

        //load all reminders
        dbDispatcher = new DatabaseDispatcher(getContext());
        allReminders = dbDispatcher.Reminders.getReminders();

        reminderAdapter = new ReminderAdapter(allReminders, this);
        recyclerViewLayout.setAdapter(reminderAdapter);

        registerFabClickEvents(getActivity());

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
                }
            }
            //about the length of Snackbar.LENGTH_LONG
        }, 3000);
    }

    public void editReminder(int reminderId){

    }

    public void deleteReminder(int reminderId){

    }

    private void registerFabClickEvents(FragmentActivity view) {
        FloatingActionButton reminderBtn = (FloatingActionButton) view.findViewById(R.id.fab_reminder_add_reminder);

        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
            }
        });
    }

    private void addReminder(){
        Fragment editReminderFragment = new EditReminderFragment();
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("reminderId", -1);
        editReminderFragment.setArguments(dataBundle);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.budget_beaver_activity_content, editReminderFragment)
                .addToBackStack(null)
                .commit();
    }
}
