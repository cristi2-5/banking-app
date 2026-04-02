package com.banca.bankingapp.models;

public final class Transaction {
    private final String transactionId;
    private final String type;
    private final  double amount;
    private final String description;
    private final String accountIban;
    private final long timestamp;

    public Transaction(String transactionId,String type,double amount,String description,long timestamp,String accountIban){
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.accountIban = accountIban;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public String getAccountIban() {
        return accountIban;
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

    @Override
    public String toString() {
        return this.type + ": " + this.amount + " RON";
    }
}
