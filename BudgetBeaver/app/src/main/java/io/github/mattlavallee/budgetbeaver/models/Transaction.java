package io.github.mattlavallee.budgetbeaver.models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class Transaction {
    private int id;
    private int accountId;
    private String location;
    private String description;
    private double amount;
    private Date dateModified;
    private boolean active;
    //TODO: add Currency Support
    //TODO: add tag support

    public Transaction(){
        id = -1;
        accountId = -1;
        location = "";
        description = "";
        amount = 0;
        active = false;
        dateModified = new Date(Long.MIN_VALUE);
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
        String formattedAmount = new DecimalFormat("0.00").format( amount );
        return "$" + formattedAmount;
    }
    public boolean isActive(){ return active; }
    public Date getTransactionDate(){ return dateModified; }

    public static String getFormattedTotal(ArrayList<Transaction> transactions){
        double total = 0;
        for(int i = 0; i < transactions.size(); i++){
            if(transactions.get(i).isActive()) {
                total += transactions.get(i).getAmount();
            }
        }

        String formattedAmount = new DecimalFormat("0.00").format( total );
        return "$" + formattedAmount;
    }
}
