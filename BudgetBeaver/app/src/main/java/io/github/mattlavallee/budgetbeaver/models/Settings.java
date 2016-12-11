package io.github.mattlavallee.budgetbeaver.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import io.github.mattlavallee.budgetbeaver.models.enums.SortDirection;
import io.github.mattlavallee.budgetbeaver.models.enums.SortType;

public class Settings {
    private SharedPreferences _preferences;
    private int defaultPositiveColor = Color.parseColor("#333333");
    private int defaultNegativeColor = Color.parseColor("#cc0000");
    public Settings(Context context){
        _preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SortType getTransactionSortType(){
        String sortTypeAsString = _preferences.getString("setting_transaction_sort_type", "4");
        return SortType.fromInt(Integer.parseInt(sortTypeAsString));
    }
    public SortDirection getTransactionSortDirection(){
        boolean reverseSort = _preferences.getBoolean("setting_transaction_sort_direction", false);
        return SortDirection.fromInt( reverseSort ? 1 : 0 );
    }

    public int getPositiveTransactionColor(){
        return _preferences.getInt("setting_transaction_positive_color", defaultPositiveColor);
    }

    public int getNegativeTransactionColor(){
        return _preferences.getInt("setting_transaction_negative_color", defaultNegativeColor);
    }

    public int getPositiveAccountColor(){
        return _preferences.getInt("setting_account_positive_color", defaultPositiveColor);
    }

    public int getNegativeAccountColor(){
        return _preferences.getInt("setting_account_negative_color", defaultNegativeColor);
    }
}
