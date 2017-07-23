package io.github.mattlavallee.budgetbeaver;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.fragments.AccountFragment;
import io.github.mattlavallee.budgetbeaver.fragments.FeedbackFragment;
import io.github.mattlavallee.budgetbeaver.fragments.NotificationsFragment;
import io.github.mattlavallee.budgetbeaver.fragments.OverviewFragment;
import io.github.mattlavallee.budgetbeaver.fragments.RemindersFragment;
import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverStatusBarNotifier;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;


public class BudgetBeaverActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout _drawerLayout;
    ActionBarDrawerToggle _drawerToggle;
    private int currentSelectedMenuId = R.id.action_account_overview;

    private void loadNavigationDrawerMenu(){
        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        Menu drawerMenu = drawerMenuNav.getMenu();

        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(getApplicationContext());
        ArrayList<Account> accounts = dbDispatcher.Accounts.getAccounts();

        //clear the set of all accounts previously loaded in the nav menu because it could have changed
        //and clearing a small set and re-adding is significantly less complicated than doing a diff to
        //figure out what needs to be added and where
        int index = 0;
        while(index < drawerMenu.size()){
            MenuItem currItem = drawerMenu.getItem(index);
            if(currItem.getGroupId() == R.id.menu_middle && !currItem.getTitle().toString().equals("Accounts")){
                drawerMenu.removeItem(currItem.getItemId());
            } else {
                index++;
            }
        }

        if(accounts.size() == 0){
            int noAccountId = View.generateViewId();
            drawerMenu.add(R.id.menu_middle, noAccountId, 103, "No Accounts");
            drawerMenu.findItem(noAccountId).setEnabled(false);
        }

        for(int i = 0; i < accounts.size(); i++){
            Account currAccount = accounts.get(i);
            drawerMenu.add(R.id.menu_middle, currAccount.getId(), 103 + i, currAccount.getName());
            drawerMenu.findItem(currAccount.getId()).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            drawerMenu.findItem(currAccount.getId()).setCheckable(true);
        }

        drawerMenuNav.setNavigationItemSelectedListener(this);
    }

    private void invalidateOldAndActivateNewNotifications(){
        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(getApplicationContext());
        ArrayList<Reminder> allReminders = dbDispatcher.Reminders.getReminders();

        ArrayList<Reminder> expiredReminders = Reminder.invalidateExpiredNotifications( allReminders );
        ArrayList<Reminder> newActiveReminders = Reminder.activateNewNotifications( allReminders );

        for(Reminder expReminder : expiredReminders ){
            dbDispatcher.Reminders.updateReminder(expReminder);
        }
        for(Reminder actReminder : newActiveReminders ){
            dbDispatcher.Reminders.updateReminder(actReminder);
        }

        BudgetBeaverStatusBarNotifier.generate(this, newActiveReminders);
    }

    private void checkForActiveNotifications(){
        invalidateOldAndActivateNewNotifications();

        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(getApplicationContext());
        ArrayList<Reminder> activeNotifications = dbDispatcher.Reminders.getActiveNotifications();

        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        Menu drawerMenu = drawerMenuNav.getMenu();

        MenuItem notificationsEntry = drawerMenu.getItem(0);
        if( activeNotifications.size() > 0 ){
            notificationsEntry.setEnabled(true);
        } else{
            notificationsEntry.setEnabled(false);
        }
    }

    private void loadDefaultFragment(boolean shouldStartOnNotificationFragment){
        Fragment startingFragment = shouldStartOnNotificationFragment ? new NotificationsFragment() : new OverviewFragment();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.budget_beaver_activity_content, startingFragment)
            .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_beaver);
        boolean startOnNotification = false;
        if(savedInstanceState == null){
            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                startOnNotification = bundle.getBoolean("startOnNotifications");
            }
        }

        _drawerLayout = (DrawerLayout)findViewById(R.id.budget_beaver_drawer_layout);

        //set the toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.budget_beaver_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Budget Beaver");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _drawerToggle = new ActionBarDrawerToggle( this, _drawerLayout, toolbar,
                R.string.app_drawerPlaceholder, R.string.app_drawerPlaceholder ) {
            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                loadNavigationDrawerMenu();
                checkForActiveNotifications();
                supportInvalidateOptionsMenu();

                NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
                drawerMenuNav.getMenu().findItem(currentSelectedMenuId).setChecked(true);
            }
        };
        _drawerToggle.setDrawerIndicatorEnabled(true);
        _drawerLayout.addDrawerListener(_drawerToggle);
        _drawerToggle.syncState();

        //populate the menu with account information and set default menu item
        loadNavigationDrawerMenu();
        loadDefaultFragment(startOnNotification);
        invalidateOldAndActivateNewNotifications();
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidateOldAndActivateNewNotifications();
    }

    private void setSelectedNavigationViewItem(MenuItem item){
        item.setChecked(true);
        currentSelectedMenuId = item.getItemId();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setSelectedNavigationViewItem(item);

        int id = item.getItemId();
        Fragment activeViewFragment;

        if (id == R.id.action_account_overview) {
            activeViewFragment = new OverviewFragment();
        } else if( id == R.id.action_notifications){
            activeViewFragment = new NotificationsFragment();
        } else if (id == R.id.action_reminders) {
            activeViewFragment = new RemindersFragment();
        } else if(id == R.id.action_about) {
            activeViewFragment = new FeedbackFragment();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClassName(this, "io.github.mattlavallee.budgetbeaver.BudgetBeaverSettings");
            startActivity(intent);
            return true;
        } else {
            //when loading an account, we need to provide the account id so that the fragment
            //knows which account it should load
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("accountId", id);

            activeViewFragment = new AccountFragment();
            activeViewFragment.setArguments(dataBundle);
        }

        //Load the new fragment
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.budget_beaver_activity_content, activeViewFragment)
            .addToBackStack(null)
            .commit();

        //close the app drawer
        _drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }
}
