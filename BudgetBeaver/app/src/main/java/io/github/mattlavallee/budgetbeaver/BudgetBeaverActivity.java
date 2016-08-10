package io.github.mattlavallee.budgetbeaver;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.fragments.AccountFragment;
import io.github.mattlavallee.budgetbeaver.fragments.OverviewFragment;
import io.github.mattlavallee.budgetbeaver.fragments.RemindersFragment;
import io.github.mattlavallee.budgetbeaver.fragments.SettingsFragment;


public class BudgetBeaverActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout _drawerLayout;

    private void loadNavigationDrawerMenu(){
        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        Menu drawerMenu = drawerMenuNav.getMenu();

        SubMenu accountsSubMenu = drawerMenu.getItem(2).getSubMenu();

        //TODO: this should load accounts from the database when we get to that point
        ArrayList<String> accounts = new ArrayList<>();
        accounts.add("Pirate Booty");
        accounts.add("Under the Mattress");
        accounts.add("Mason Jars");

        if(accounts.size() > 0){
            accountsSubMenu.clear();
        }

        for(int i = 0; i < accounts.size(); i++){
            int accountId = i;
            accountsSubMenu.add(2, accountId, i, accounts.get(i));
        }

        drawerMenuNav.setNavigationItemSelectedListener(this);
        drawerMenu.getItem(0).setChecked(true);
    }

    private void loadDefaultFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.budget_beaver_activity_content, new OverviewFragment());
        ft.commit();
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

        //populate the menu with account information and set default menu item
        loadNavigationDrawerMenu();
        loadDefaultFragment();
    }

    private void setSelectedNavigationViewItem(MenuItem item){
        int order = item.getOrder() - 100;
        //less than zero we're in the accounts submenu; set checked item to accounts title
        if(order < 0){
            order = 2;
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
        } else if (id == R.id.action_reminders) {
            activeViewFragment = new RemindersFragment();
        } else if (id == R.id.action_settings) {
            activeViewFragment = new SettingsFragment();
        } else {
            //when loading an account, we need to provide the account id so that the fragment
            //knows which account it should load
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("accountId", id);
            dataBundle.putString("test", "hi");

            activeViewFragment = new AccountFragment();
            activeViewFragment.setArguments(dataBundle);
        }

        //Load the new fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.budget_beaver_activity_content, activeViewFragment);
        ft.commit();

        //close the app drawer
        _drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
