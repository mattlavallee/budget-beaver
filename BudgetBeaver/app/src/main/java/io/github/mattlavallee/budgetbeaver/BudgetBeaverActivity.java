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
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.fragments.AccountFragment;
import io.github.mattlavallee.budgetbeaver.fragments.NotificationsFragment;
import io.github.mattlavallee.budgetbeaver.fragments.OverviewFragment;
import io.github.mattlavallee.budgetbeaver.fragments.RemindersFragment;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;


public class BudgetBeaverActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout _drawerLayout;
    ActionBarDrawerToggle _drawerToggle;

    private void loadNavigationDrawerMenu(){
        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        Menu drawerMenu = drawerMenuNav.getMenu();

        SubMenu accountsSubMenu = drawerMenu.getItem(3).getSubMenu();

        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(getApplicationContext());
        ArrayList<Account> accounts = dbDispatcher.Accounts.getAccounts();

        accountsSubMenu.clear();
        if(accounts.size() == 0){
            accountsSubMenu.add(2, -1, 0, "No Accounts");
            accountsSubMenu.getItem(0).setEnabled(false);
        }

        for(int i = 0; i < accounts.size(); i++){
            Account currAccount = accounts.get(i);
            accountsSubMenu.add(2, currAccount.getId(), i, currAccount.getName());
        }

        drawerMenuNav.setNavigationItemSelectedListener(this);
        drawerMenu.getItem(1).setChecked(true);
    }

    private void checkForActiveNotifications(){
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

        ArrayList<Reminder> activeNotifications = dbDispatcher.Reminders.getActiveNotifications();

        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        Menu drawerMenu = drawerMenuNav.getMenu();

        MenuItem notificationsEntry = drawerMenu.getItem(0);
        if( activeNotifications.size() > 0 ){
            notificationsEntry.setVisible(true);
        } else{
            notificationsEntry.setVisible(false);
        }
    }

    private void loadDefaultFragment(){
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.budget_beaver_activity_content, new OverviewFragment())
            .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_beaver);

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
            }
        };
        _drawerToggle.setDrawerIndicatorEnabled(true);
        _drawerLayout.addDrawerListener(_drawerToggle);
        _drawerToggle.syncState();

        //populate the menu with account information and set default menu item
        loadNavigationDrawerMenu();
        loadDefaultFragment();
    }

    private void setSelectedNavigationViewItem(MenuItem item){
        int order = item.getOrder() - 100;
        //less than zero we're in the accounts submenu; set checked item to accounts title
        if(order < 0){
            order = 3;
        }
        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        drawerMenuNav.getMenu().getItem(order).setChecked(true);
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
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }
}
