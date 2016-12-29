package io.github.mattlavallee.budgetbeaver.models.adapters;

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

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.fragments.RemindersFragment;
import io.github.mattlavallee.budgetbeaver.models.Reminder;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private ArrayList<Reminder> reminders;
    private RemindersFragment adapterContainer;

    public ReminderAdapter(ArrayList<Reminder> allReminders, RemindersFragment container){
        reminders = allReminders;
        adapterContainer = container;
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder{
        RemindersFragment _container;
        CardView cardView;
        TextView reminderMessage;
        View overflow;

        ReminderViewHolder(View itemView, RemindersFragment container){
            super(itemView);
            _container = container;
            cardView = (CardView) itemView.findViewById(R.id.reminder_card_view);
            reminderMessage = (TextView)itemView.findViewById(R.id.reminder_message);
            overflow = itemView.findViewById(R.id.reminder_overflow);
            overflow.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View view){
                    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    final Menu menu = popupMenu.getMenu();
                    final int reminderId = (Integer) view.getTag();

                    popupMenu.getMenuInflater().inflate(R.menu.reminder_popup_menu, menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item){
                            int itemId = item.getItemId();
                            if(itemId == R.id.action_reminder_popup_edit){
                                _container.editReminder(reminderId);
                            } else if(itemId == R.id.action_reminder_popup_delete){
                                _container.deleteReminder(reminderId);
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
        ReminderViewHolder viewHolder = new ReminderViewHolder(view, adapterContainer);
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
        viewHolder.reminderMessage.setText(reminder.getMessage());
        viewHolder.overflow.setTag(reminder.getId());
    }

    @Override
    public int getItemCount(){
        return reminders.size();
    }
}