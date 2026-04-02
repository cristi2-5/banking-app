package com.banca.bankingapp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

abstract public class Account {
    private final String iban;
    private double balance;
    private final Customer owner;
    private final List<Transaction> transactions;
    private List<Card> cards = new ArrayList<>();

    public Account(String iban,double balance,Customer owner){
        this.iban = iban;
        this.balance = balance;
        this.owner = owner;
        this.transactions = new ArrayList<Transaction>();
    }

    public String getIban() {
        return iban;
    }

    public double getBalance(){
        return balance;
    }

    public Customer getOwner(){
        return owner;
    }

    public List<Card> getCards() {
        return cards;
    }


    public void addCard(Card card) {
        this.cards.add(card);
    }

    public List<Transaction> getTransactions(){
        return transactions;
    }

    public void deposit(double amount){
        balance += amount;
        Transaction t = new Transaction(
                UUID.randomUUID().toString(),
                "DEPOSIT",
                amount,
                "Depunere numerar",
                System.currentTimeMillis()
        );
        this.transactions.add(t);
    }

    public boolean withdraw(double amount){
        if(amount<=balance&&amount>0)
        {
            balance-=amount;
            Transaction t = new Transaction(
                    UUID.randomUUID().toString(),
                    "RETRAGERE",
                    amount,
                    "Retragere numerar",
                    System.currentTimeMillis()
            );
            this.transactions.add(t);
            return true;
        }
        else if(amount>balance)
        {
            System.out.println("Fonduri insuficiente!");
            return false;
        }
        else{
            System.out.println("Poti retrage doar sume pozitive!");
            return false;
        }
    }

    protected void setBalance(double amount){
        balance = amount;
    }

    public abstract String getAccountType();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return iban.equals(account.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }
}
