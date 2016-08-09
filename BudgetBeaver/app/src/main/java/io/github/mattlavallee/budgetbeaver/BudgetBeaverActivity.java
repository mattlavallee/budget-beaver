package io.github.mattlavallee.budgetbeaver;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.FrameLayout;

import java.util.ArrayList;


public class BudgetBeaverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_beaver);

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.budget_beaver_drawer_layout);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.budget_beaver_activity_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.budget_beaver_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Budget Beaver");

        NavigationView drawerMenuNav = (NavigationView) findViewById(R.id.budget_beaver_navigation_view);
        Menu drawerMenu = drawerMenuNav.getMenu();

        SubMenu accountsSubMenu = drawerMenu.getItem(2).getSubMenu();
        accountsSubMenu.clear();
        ArrayList<String> accounts = new ArrayList<String>();
        accounts.add("Pirate Booty");
        accounts.add("Under the Mattress");
        accounts.add("Mason Jars");

        for(int i = 0; i < accounts.size(); i++){
            accountsSubMenu.add(accounts.get(i));
        }
    }
}
