package io.github.mattlavallee.budgetbeaver.models;


public class Account {
    private int id;
    private String name;
    private boolean isActive;

    public Account(){
        id = -1;
        name = "";
        isActive = false;
    }

    public Account(int accountId, String accountName, int active){
        id = accountId;
        name = accountName;
        isActive = (active == 0) ? false : true;
    }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public boolean isActive(){ return isActive; }

    public void setIsActive( boolean active ){
        isActive = active;
    }

    public void setName(String newName ){
        name = newName;
    }
}
