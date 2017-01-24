package io.github.mattlavallee.budgetbeaver.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Reminder {
    private int _id;
    private int _accountId;
    private String _message;
    private int _dayOfMonth;
    private int _daysUntilExpiration;
    private Date _lastDateActivated;
    private boolean _isActiveNotification;
    private boolean _active;

    public Reminder(){
        _id = -1;
        _accountId = -1;
        _message = "";
        _dayOfMonth = 1;
        _daysUntilExpiration = 0;
        _lastDateActivated = new Date(0);
        _isActiveNotification = false;
        _active = false;
    }

    public Reminder(int id, int accountId, String message, int dayOfMonth, int daysUntilExpires,
                    Date lastDateActivated, boolean isNotificationActive, boolean isActive){
        _id = id;
        _accountId = accountId;
        _message = message;
        _dayOfMonth = dayOfMonth;
        _daysUntilExpiration = daysUntilExpires;
        _lastDateActivated = lastDateActivated;
        _isActiveNotification = isNotificationActive;
        _active = isActive;
    }

    public int getId(){ return _id; }
    public int getAccountId(){ return _accountId; }
    public String getMessage(){ return _message; }
    public int getDayOfMonth(){ return _dayOfMonth; }
    public int getDaysUntilExpiration(){ return _daysUntilExpiration; }
    public Date getLastDateActivated(){ return _lastDateActivated; }
    public boolean isNotificationActive(){ return _isActiveNotification; }
    public boolean isActive(){ return _active; }

    public void setIsActive(boolean activeState){ _active = activeState; }
    public void setIsNotificationActive(boolean activeState){ _isActiveNotification = activeState; }
    public void setLastDateActivated(Date date){ _lastDateActivated = date; }

    public static ArrayList<Reminder> invalidateExpiredNotifications( ArrayList<Reminder> reminders ){
        ArrayList<Reminder> expiredNotifications = new ArrayList<>();
        for(Reminder currReminder : reminders){
            //we only are expiring already active notifications
            if(currReminder.isNotificationActive() == false){
                continue;
            }

            //initialize a calendar to the current year and month to get the maximum date for the month
            Calendar cal = Calendar.getInstance();
            cal.set( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            //get current date for comparison
            Date currentDate = new Date();
            //get the min notification date
            int reminderDay = currReminder.getDayOfMonth();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), reminderDay > maxDay ? maxDay : reminderDay );
            Date minNotificationDate = cal.getTime();
            //get the max notification date
            Date maxNotificationDate = new Date(Long.MAX_VALUE);
            if( currReminder.getDaysUntilExpiration() != -1) {
                cal.add(Calendar.DATE, currReminder.getDaysUntilExpiration());
                maxNotificationDate = cal.getTime();
            }

            //if the current date is before the minNotificationDate or after the maxNotificationDate it's expired
            if(currentDate.before(minNotificationDate) || currentDate.after(maxNotificationDate)){
                currReminder.setIsNotificationActive(false);
                expiredNotifications.add(currReminder);
            }
        }

        return expiredNotifications;
    }
    public static ArrayList<Reminder> activateNewNotifications( ArrayList<Reminder> reminders ){
        ArrayList<Reminder> newNotifications = new ArrayList<>();

        for(Reminder currReminder : reminders){
            //we only are expiring already active notifications
            if(currReminder.isNotificationActive() == true){
                continue;
            }

            //initialize a calendar to the current year and month to get the maximum date for the month
            Calendar cal = Calendar.getInstance();
            cal.set( cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            //get current date for comparison
            Calendar rightNow = Calendar.getInstance();
            rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            Date currentDate = rightNow.getTime();
            //get the min notification date
            int reminderDay = currReminder.getDayOfMonth();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), reminderDay > maxDay ? maxDay : reminderDay, 0, 0, 0 );
            Date minNotificationDate = cal.getTime();
            //get the max notification date
            Date maxNotificationDate = new Date(Long.MAX_VALUE);
            if( currReminder.getDaysUntilExpiration() != -1) {
                cal.add(Calendar.DATE, currReminder.getDaysUntilExpiration());
                maxNotificationDate = cal.getTime();
            }

            //is valid active date iif currDate >= minDate && currDate <= maxDate
            boolean currentDateIsValid = currentDate.compareTo(minNotificationDate) > -1 &&
                    currentDate.compareTo(maxNotificationDate) < 1;
            //check if it's been activated for the current month by ensuring that the notification
            //was last active at a date before the current minNotificationDate.
            //if it was already activated for this month, lastDateActive >= minNotificationDate
            boolean notActivatedForThisMonth = currReminder.getLastDateActivated().compareTo(minNotificationDate) < 0;
            if(currentDateIsValid && notActivatedForThisMonth){
                currReminder.setIsNotificationActive(true);
                currReminder.setLastDateActivated(new Date());
                newNotifications.add(currReminder);
            }
        }

        return newNotifications;
    }
}
