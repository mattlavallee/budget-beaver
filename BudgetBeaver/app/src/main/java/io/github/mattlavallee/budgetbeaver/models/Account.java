package io.github.mattlavallee.budgetbeaver.models;


import io.github.mattlavallee.budgetbeaver.models.enums.SortDirection;
import io.github.mattlavallee.budgetbeaver.models.enums.SortType;

public class Account {
    private int id;
    private String name;
    private int sortType;
    private int sortDirection;
    private boolean isActive;

    public Account(){
        id = -1;
        name = "";
        sortType = -1;
        sortDirection = -1;
        isActive = false;
    }

    public Account(int accountId, String accountName, int accountSortType, int accountSortDirection, int active){
        id = accountId;
        name = accountName;
        sortType = accountSortType;
        sortDirection = accountSortDirection;
        isActive = (active == 0) ? false : true;
    }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public SortType getSortType(){
        return SortType.fromInt( sortType );
    }
    public SortType getSortType( SortType appSortType ){
        SortType accountSortType = getSortType();
        return accountSortType == null ? appSortType : accountSortType;
    }
    public SortDirection getSortDirection(){
        return SortDirection.fromInt( sortDirection );
    }
    public SortDirection getSortDirection( SortDirection appSortDirection ){
        SortDirection accountSortDirection = getSortDirection();
        return accountSortDirection == null ? appSortDirection : accountSortDirection;
    }
    public boolean isActive(){ return isActive; }

    public void setIsActive( boolean active ){
        isActive = active;
    }
    public void setName(String newName ){
        name = newName;
    }
    public void setSortType( SortType type ){
        sortType = type == null ? -1 : type.getValue();
    }
    public void setSortDirection( SortDirection direction ){
        sortDirection = direction == null ? -1 : direction.getValue();
    }
}
