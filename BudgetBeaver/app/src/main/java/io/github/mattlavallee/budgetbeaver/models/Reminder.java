package io.github.mattlavallee.budgetbeaver.models;

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
        _lastDateActivated = new Date(Long.MIN_VALUE);
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
}
