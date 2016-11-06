package io.github.mattlavallee.budgetbeaver.models;

import java.io.Serializable;

public class Tag implements Serializable {
    private int id;
    private int tagTransactionId;
    private String tagName;

    public Tag(){
        id = -1;
        tagTransactionId = -1;
        tagName = "";
    }

    public Tag( int tagId, int transactionId, String name ){
        id = tagId;
        tagTransactionId = transactionId;
        tagName = name;
    }

    public int getId(){ return id; }
    public int getTransactionId(){ return tagTransactionId; }
    public String getTagName(){ return tagName; }

    public void setTransactionId( int transId ){
        tagTransactionId = transId;
    }

    public void setTagName( String name ){
        tagName = name;
    }

    @Override
    public String toString(){
        return tagName;
    }
}
