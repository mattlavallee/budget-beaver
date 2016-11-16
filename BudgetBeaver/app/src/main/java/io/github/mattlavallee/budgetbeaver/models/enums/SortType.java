package io.github.mattlavallee.budgetbeaver.models.enums;

public enum SortType {
    Location(0), Tag(1), Amount(2), Description(3), Date(4);

    private int _value;

    SortType( int value ){
        _value = value;
    }

    public int getValue(){
        return _value;
    }

    public static SortType fromInt(int i){
        for(SortType type : SortType.values()){
            if(type.getValue() == i){
                return type;
            }
        }
        return null;
    }
}