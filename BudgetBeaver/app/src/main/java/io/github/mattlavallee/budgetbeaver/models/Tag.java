package io.github.mattlavallee.budgetbeaver.models;

import java.io.Serializable;

public class Tag implements Serializable {
    private int id;
    private int tagTransactionId;
    private String tagName;
    private boolean active;

    public Tag(){
        id = -1;
        tagTransactionId = -1;
        tagName = "";
        active = false;
    }

    public Tag( int tagId, int transactionId, String name, boolean isActive){
        id = tagId;
        tagTransactionId = transactionId;
        tagName = name;
        active = isActive;
    }

    public int getId(){ return id; }
    public int getTransactionId(){ return tagTransactionId; }
    public String getTagName(){ return tagName; }
    public boolean isActive(){ return active; }

    public void setTransactionId( int transId ){
        tagTransactionId = transId;
    }

    public void setTagName( String name ){
        tagName = name;
    }

    public void setIsActive( boolean isActive ){
        active = isActive;
    }

    @Override
    public String toString(){
        return tagName;
    }
}
