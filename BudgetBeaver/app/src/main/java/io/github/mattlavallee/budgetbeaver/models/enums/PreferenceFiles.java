package io.github.mattlavallee.budgetbeaver.models.enums;

public class PreferenceFiles {
    private static String defaultPreferences = "budget_beaver_app_settings";
    private static String accountPreferencesPrefix = "budget_beaver_account_";

    public static String DefaultPreferences(){
        return defaultPreferences;
    }

    public static String AccountPreferences(int accountId){
        return accountPreferencesPrefix + accountId + "_settings";
    }
}
