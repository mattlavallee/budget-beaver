package io.github.mattlavallee.budgetbeaver.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import io.github.mattlavallee.budgetbeaver.models.enums.SortDirection;
import io.github.mattlavallee.budgetbeaver.models.enums.SortType;

public class Transaction {
    private int id;
    private int accountId;
    private String location;
    private String description;
    private double amount;
    private Date dateModified;
    private boolean active;

    public Transaction(){
        id = -1;
        accountId = -1;
        location = "";
        description = "";
        amount = 0;
        active = false;
        dateModified = new Date(0);
    }

    public Transaction(int transactionId, int transAccountId, String transactionLocation,
                       String transactionDescription, double transactionAmount,
                       Date transactionDate, boolean isActive){
        id = transactionId;
        accountId = transAccountId;
        location = transactionLocation;
        description = transactionDescription;
        amount = transactionAmount;
        active = isActive;
        dateModified = transactionDate;
    }

    public int getId(){ return id; }
    public int getAccountId(){ return accountId; }
    public String getLocation(){ return location; }
    public String getDescription(){ return description; }
    public double getAmount(){ return amount; }
    public String getFormattedAmount(){
        return getLocalizedCurrencyString(amount);
    }
    public boolean isActive(){ return active; }
    public Date getTransactionDate(){ return dateModified; }

    public static double getTotal(ArrayList<Transaction> transactions){
        double total = 0;
        for(int i = 0; i < transactions.size(); i++){
            if(transactions.get(i).isActive()) {
                total += transactions.get(i).getAmount();
            }
        }
        return total;
    }
    public static String getFormattedTotal(ArrayList<Transaction> transactions){
        double total = Transaction.getTotal(transactions);
        return getLocalizedCurrencyString(total);
    }

    private static String getLocalizedCurrencyString(double amount){
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance(Locale.getDefault()));
        return format.format(amount);
    }

    public static void sortTransactions(ArrayList<Transaction> transactions,
                                        final SortType sortTarget, final SortDirection sortDirection ){
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction transaction, Transaction t1) {
                int sortEvaluation;
                switch(sortTarget){
                    case Amount:
                        sortEvaluation = 0;
                        if(transaction.getAmount() < t1.getAmount()){
                            sortEvaluation = -1;
                        } else if( transaction.getAmount() > t1.getAmount()){
                            sortEvaluation = 1;
                        }
                        //secondary sort
                        if(sortEvaluation == 0){
                            sortEvaluation = transaction.getTransactionDate().compareTo(t1.getTransactionDate());
                        }
                        break;
                    case Location:
                        sortEvaluation = transaction.getLocation().compareTo(t1.getLocation());
                        //secondary sort
                        if(sortEvaluation == 0){
                            sortEvaluation = transaction.getTransactionDate().compareTo(t1.getTransactionDate());
                        }
                        break;
                    case Description:
                        sortEvaluation = transaction.getDescription().compareTo(t1.getDescription());
                        //secondary sort
                        if(sortEvaluation == 0){
                            sortEvaluation = transaction.getTransactionDate().compareTo(t1.getTransactionDate());
                        }
                        break;
                    case Tag:
                        //TODO: implement tag sorting
                    default:
                        sortEvaluation = transaction.getTransactionDate().compareTo(t1.getTransactionDate());
                        //secondary sort
                        if(sortEvaluation == 0){
                            sortEvaluation = transaction.getLocation().compareTo(t1.getLocation());
                        }
                        break;
                }

                return sortDirection == SortDirection.Descending ? (sortEvaluation * -1) : sortEvaluation;
            }
        });
    }

    public void setIsActive( boolean isActive ){
        active = isActive;
    }
}
