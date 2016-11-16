package io.github.mattlavallee.budgetbeaver.models;


import io.github.mattlavallee.budgetbeaver.models.enums.SortDirection;
import io.github.mattlavallee.budgetbeaver.models.enums.SortType;

public class Settings {
    private SortType defaultSortType;
    private SortDirection defaultSortDirection;
    private String positiveTransactionColor;
    private String negativeTransactionColor;
    private String positiveAccountColor;
    private String negativeAccountColor;
    private int defaultCurrencyId;

    public Settings(){
        defaultSortType = SortType.Date;
        defaultSortDirection = SortDirection.Ascending;
        positiveTransactionColor = "";
        negativeTransactionColor = "";
        positiveAccountColor = "";
        negativeAccountColor = "";
        defaultCurrencyId = -1;
    }

    public SortType getDefaultSortType(){ return defaultSortType; }
    public SortDirection getDefaultSortDirection(){ return defaultSortDirection; }
    public String getPositiveTransactionColor(){ return positiveTransactionColor; }
    public String getNegativeTransactionColor(){ return negativeTransactionColor; }
    public String getPositiveAccountColor(){ return positiveAccountColor; }
    public String getNegativeAccountColor(){ return negativeAccountColor; }
    public int getDefaultCurrencyId(){ return defaultCurrencyId; }

    public void setDefaultSortType(SortType type){
        defaultSortType = type;
    }
    public void setDefaultSortDirection(SortDirection direction){
        defaultSortDirection = direction;
    }
    public void setPositiveTransactionColor(String color){
        positiveTransactionColor = color;
    }
    public void setNegativeTransactionColor(String color){
        negativeTransactionColor = color;
    }
    public void setPositiveAccountColor(String color){
        positiveAccountColor = color;
    }
    public void setNegativeAccountColor(String color){
        negativeAccountColor = color;
    }
    public void setDefaultCurrencyId(int currencyId){
        defaultCurrencyId = currencyId;
    }
}
