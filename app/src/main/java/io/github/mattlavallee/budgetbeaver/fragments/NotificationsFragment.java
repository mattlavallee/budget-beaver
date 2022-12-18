package io.github.mattlavallee.budgetbeaver.fragments;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
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
    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.recycler_view, container, false);
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

        TextView emptyMessage = (TextView)fragmentView.findViewById(R.id.empty_recycler_message);
        emptyMessage.setText("There are no active notifications");
        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                R.id.empty_recycler_message, allNotifications.size());

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
                    //notificationAdapter.updateData(notificationIntegrity);
                    BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                            R.id.empty_recycler_message, notificationIntegrity.size());
                }
            }
            //about the length of Snackbar.LENGTH_LONG
        }, 3000);
    }

    public void dismissNotification(int reminderId){
        final Reminder notification = dbDispatcher.Reminders.getReminderById(reminderId);
        notification.setIsNotificationActive(false);
        dbDispatcher.Reminders.updateReminder(notification);

        final ArrayList<Reminder> notifications = dbDispatcher.Reminders.getActiveNotifications();
        notificationAdapter.updateData(notifications);
        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                R.id.empty_recycler_message, notifications.size());
        Snackbar snack = Snackbar
                .make(getView(), "Notification Dismissed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        notification.setIsNotificationActive(true);
                        dbDispatcher.Reminders.updateReminder(notification);

                        ArrayList<Reminder> notificationState = dbDispatcher.Reminders.getActiveNotifications();
                        notificationAdapter.updateData(notificationState);
                        BudgetBeaverRecyclerHandler.updateViewVisibility(fragmentView, R.id.recycler_container,
                                R.id.empty_recycler_message, notificationState.size());
                    }
                });
        TextView snackText = (TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        TextView actionText = (TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_action);
        actionText.setTextColor(Color.CYAN);
        snack.show();
    }
}
