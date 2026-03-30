package com.banca.bankingapp;

public final class Transaction {
    private final String transactionId;
    private final String type;
    private final  double amount;
    private final String description;
    private final long timestamp;

    public Transaction(String transactionId,String type,double amount,String description,long timestamp){
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
