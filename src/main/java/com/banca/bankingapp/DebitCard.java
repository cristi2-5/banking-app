package com.banca.bankingapp;

public class DebitCard extends Card {

    public DebitCard(String cardNumber, String pin, String expiryDate, boolean status, Account account) {
        super(cardNumber, pin, expiryDate, status, account);
    }

    @Override
    public void displayCardType() {
        System.out.println("Acesta este un Card de Debit asociat contului: " + getAccount().getIban());
    }
}