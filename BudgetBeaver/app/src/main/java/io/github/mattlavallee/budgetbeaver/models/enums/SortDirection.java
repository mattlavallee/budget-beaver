package io.github.mattlavallee.budgetbeaver.models.enums;

public enum SortDirection {
    Ascending(0), Descending(1);

    private int _value;
    SortDirection( int value ){
        _value = value;
    }

    public int getValue(){
        return _value;
    }

    public static SortDirection fromInt(int i){
        for(SortDirection direction : SortDirection.values()){
            if(direction.getValue() == i){
                return direction;
            }
        }
        return null;
    }
}


