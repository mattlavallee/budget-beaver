package io.github.mattlavallee.budgetbeaver.models.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.fragments.NotificationsFragment;
import io.github.mattlavallee.budgetbeaver.fragments.RemindersFragment;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private ArrayList<Reminder> reminders;
    private Fragment adapterContainer;
    private DatabaseDispatcher dbHelper;
    private boolean isNotificationView = false;

    public ReminderAdapter(ArrayList<Reminder> allReminders, RemindersFragment container, DatabaseDispatcher helper){
        reminders = allReminders;
        adapterContainer = container;
        dbHelper = helper;
        isNotificationView = false;
    }

    public ReminderAdapter(ArrayList<Reminder> allReminders, NotificationsFragment container, DatabaseDispatcher helper){
        reminders = allReminders;
        adapterContainer = container;
        dbHelper = helper;
        isNotificationView = true;
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder{
        Fragment _container;
        CardView cardView;
        TextView reminderMessage;
        TextView reminderAccount;
        TextView reminderTimeframe;
        View overflow;

        ReminderViewHolder(View itemView, Fragment container, final boolean isNotificationView){
            super(itemView);
            _container = container;
            cardView = (CardView) itemView.findViewById(R.id.reminder_card_view);
            reminderMessage = (TextView)itemView.findViewById(R.id.reminder_message);
            reminderAccount = (TextView)itemView.findViewById(R.id.reminder_account_name);
            reminderTimeframe = (TextView) itemView.findViewById(R.id.reminder_timeFrame);
            overflow = itemView.findViewById(R.id.reminder_overflow);
            overflow.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View view){
                    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    final Menu menu = popupMenu.getMenu();
                    final int reminderId = (Integer) view.getTag();

                    if(isNotificationView){
                        popupMenu.getMenuInflater().inflate(R.menu.notification_popup_menu, menu);
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.reminder_popup_menu, menu);
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item){
                            int itemId = item.getItemId();
                            if(itemId == R.id.action_reminder_popup_edit){
                                ((RemindersFragment)_container).editReminder(reminderId);
                            } else if(itemId == R.id.action_reminder_popup_delete){
                                ((RemindersFragment)_container).deleteReminder(reminderId);
                            } else if(itemId == R.id.action_notification_popup_dismiss){
                                ((NotificationsFragment)_container).dismissNotification(reminderId);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    @Override
    public ReminderAdapter.ReminderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reminder_row, viewGroup, false);
        ReminderViewHolder viewHolder = new ReminderViewHolder(view, adapterContainer, isNotificationView);
        return viewHolder;
    }

    public void updateData(ArrayList<Reminder> data){
        reminders.clear();
        reminders = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder viewHolder, int position){
        Reminder reminder = reminders.get(position);
        Account accountForReminder = dbHelper.Accounts.getAccountById(reminder.getAccountId());
        viewHolder.reminderAccount.setText(accountForReminder.getName());
        viewHolder.reminderMessage.setText(reminder.getMessage());

        int dayOfMonth = reminder.getDayOfMonth();
        String suffix = "th";
        if(dayOfMonth == 1 || dayOfMonth == 21 || dayOfMonth == 31){
            suffix = "st";
        } else if( dayOfMonth == 2 || dayOfMonth == 22){
            suffix = "nd";
        } else if( dayOfMonth == 3 || dayOfMonth == 23){
            suffix = "rd";
        }
        if(!isNotificationView) {
            String activationMsg = "Notification activates on the " + dayOfMonth + suffix + " of the month";

            int expiration = reminder.getDaysUntilExpiration();
            String expirationMsg = "When activated, the notification will not expire until dismissed";
            if (expiration > 0) {
                expirationMsg = "When activated, the notification will expire after " + expiration +
                        (expiration <= 1 ? " day" : " days");
            }
            viewHolder.reminderTimeframe.setText(activationMsg + "\n" + expirationMsg);
        } else{
            String msg = "This notification will remain active until dismissed";
            if( reminder.getDaysUntilExpiration() > 0){
                Calendar cal = Calendar.getInstance();
                cal.set( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                Date currentDate = new Date();
                int reminderDay = reminder.getDayOfMonth();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), reminderDay > maxDay ? maxDay : reminderDay );
                cal.add(Calendar.DATE, reminder.getDaysUntilExpiration());
                int daysRemaining = 0;
                while( cal.getTime().after(currentDate)){
                    cal.add(Calendar.DATE, -1);
                    daysRemaining++;
                }
                msg = "This notification will expire after " + daysRemaining +
                        (daysRemaining == 1 ? " day" : " days");
            }
            viewHolder.reminderTimeframe.setText(msg);
        }
        viewHolder.overflow.setTag(reminder.getId());
    }

    @Override
    public int getItemCount(){
        return reminders.size();
    }
}