package com.banca.bankingapp;

public class CreditCard extends Card {
    private double creditLimit;

    public CreditCard(String cardNumber, String pin, String expiryDate, boolean status, Account account, double creditLimit) {
        super(cardNumber, pin, expiryDate, status, account);
        this.creditLimit = creditLimit;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public void displayCardType() {
        System.out.println("Acesta este un Card de Credit cu limita de: " + creditLimit);
    }
}