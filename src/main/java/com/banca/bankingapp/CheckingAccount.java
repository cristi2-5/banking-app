package com.banca.bankingapp;

public class CheckingAccount extends Account{
    private final double maintenanceFee;

    public CheckingAccount(String iban,double balance,Customer owner,double maintenanceFee){
        super(iban,balance,owner);
        this.maintenanceFee = maintenanceFee;
    }

    @Override
    public String getAccountType(){
        return "Cont Curent";
    }

    public double getMaintenanceFee(){
        return  maintenanceFee;
    }

    public void applyMaintenanceFee(){
        withdraw(maintenanceFee);
    }

}
