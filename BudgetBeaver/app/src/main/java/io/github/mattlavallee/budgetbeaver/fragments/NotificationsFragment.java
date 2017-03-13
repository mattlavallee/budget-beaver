package io.github.mattlavallee.budgetbeaver.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverFabSetup;
import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverRecyclerHandler;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Reminder;
import io.github.mattlavallee.budgetbeaver.models.adapters.ReminderAdapter;

public class NotificationsFragment extends Fragment {
    private DatabaseDispatcher dbDispatcher;
    private ReminderAdapter notificationAdapter;
    private ArrayList<Reminder> allNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.recycler_view, container, false);
        getActivity().setTitle("Notifications");

        RelativeLayout parent = (RelativeLayout) getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        //initialize the recycler view for the fragment
        RecyclerView recyclerViewLayout = BudgetBeaverRecyclerHandler
                .createFragmentRecyclerView(R.id.recycler_container, fragmentView, getContext());

        //load all notifications
        dbDispatcher = new DatabaseDispatcher(getContext());
        allNotifications = dbDispatcher.Reminders.getActiveNotifications();

        notificationAdapter = new ReminderAdapter(allNotifications, this, dbDispatcher);
        recyclerViewLayout.setAdapter(notificationAdapter);

        return fragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //TODO: there must be a better way!
        new android.os.Handler().postDelayed( new Runnable() {
            public void run() {
                ArrayList<Reminder> notificationIntegrity = dbDispatcher.Reminders.getActiveNotifications();
                if(notificationIntegrity.size() != allNotifications.size()){
                    notificationAdapter.updateData(notificationIntegrity);
                }
            }
            //about the length of Snackbar.LENGTH_LONG
        }, 3000);
    }

    public void dismissNotification(int reminderId){
        final Reminder notification = dbDispatcher.Reminders.getReminderById(reminderId);
        notification.setIsNotificationActive(false);
        dbDispatcher.Reminders.updateReminder(notification);

        notificationAdapter.updateData(dbDispatcher.Reminders.getActiveNotifications());
        Snackbar snack = Snackbar
                .make(getView(), "Notification Dismissed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        notification.setIsNotificationActive(true);
                        dbDispatcher.Reminders.updateReminder(notification);
                        notificationAdapter.updateData(dbDispatcher.Reminders.getActiveNotifications());
                    }
                });
        TextView snackText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        TextView actionText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_action);
        actionText.setTextColor(Color.CYAN);
        snack.show();
    }
}
