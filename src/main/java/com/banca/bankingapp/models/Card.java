package com.banca.bankingapp.models;

public abstract class Card {
    final private String cardNumber;
    private String pin;
    private final String expiryDate;
    private boolean isActive;
    final private Account account;

    public Card(String cardNumber, String pin, String expiryDate, boolean status, Account account) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.expiryDate = expiryDate;
        this.isActive = status;
        this.account = account;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getExpiryDate() {
        return expiryDate;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Account getAccount() {
        return account;
    }

    public abstract void displayCardType();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " - " + this.cardNumber + " (Exp: " + this.expiryDate + ")";
    }
}
