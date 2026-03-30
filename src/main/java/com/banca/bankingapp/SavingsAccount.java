package com.banca.bankingapp;

public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String iban, double balance, Customer owner, double interestRate) {
        super(iban, balance, owner);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
    }

    @Override
    public String getAccountType() {
        return "Cont de Economii";
    }
}