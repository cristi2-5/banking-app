package com.banca.bankingapp;

import java.util.*;

public class BankingService {
    private Set<Customer> customers;
    private Map<String,Account> accounts;
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";

    public BankingService() {
        this.customers = new TreeSet<>();
        this.accounts = new HashMap<>();
    }

    public String login(String username, String password) {
        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            return "ADMIN";
        }
        for (Customer c : customers) {
            if (username.equals(c.getUsername()) && password.equals(c.getPassword())) {
                return "USER";
            }
        }
        return "INVALID";
    }

    public void addCustomer(Customer customer){
        customers.add(customer);
    }

    public Customer findCustomerByCnp(String cnp) {
        return customers.stream()
                .filter(c -> c.getCnp().equals(cnp))
                .findFirst()
                .orElse(null);
    }

    public void addAccountToCustomer(String customerCnp,Account account){
        Customer c = findCustomerByCnp(customerCnp);
        if(c!=null){
            c.addAccount(account);
            accounts.put(account.getIban(),account);
        }
    }

    public void deposit(String iban,double amount){
        Account a = accounts.get(iban);
        if(a!=null)
        {
            a.deposit(amount);
        }
    }

    public void withdraw(String iban,double amount){
        Account a = accounts.get(iban);
        if(a!=null){
            a.withdraw(amount);
        }
    }

    public void transfer(String sourceIban, String destIban, double amount){
        Account s = accounts.get(sourceIban);
        Account d = accounts.get(destIban);

        if(s!=null&&d!=null){
            double value = s.getBalance();
            if(value<amount){
                System.out.println("Transferul nu poate fi efectuat! Fonduri insuficiente");
            }
            else{
                s.withdraw(amount);
                d.deposit(amount);
            }
        }
    }

    public void getAccountBalance(String iban){
        Account a = accounts.get(iban);
        if(a!=null){
            System.out.println(a.getBalance());
        }
    }

    public void displayAllCustomers(){
        for(Customer c : customers){
            System.out.println(c);
            System.out.println();
        }
    }

    public void getAccountTransactions(String iban){
        Account a = accounts.get(iban);
        if(a!=null){
            List<Transaction> transactions = a.getTransactions();
            for(Transaction t : transactions){
                System.out.println(t);
                System.out.println();
            }
        }
    }

    public void applyInterestToAllSavings() {
        for (Account acc : accounts.values()) {
            if (acc instanceof SavingsAccount) {
                SavingsAccount savingsAcc = (SavingsAccount) acc;
                savingsAcc.applyInterest();
            }
        }
    }


    public void deleteCustomer(String cnp) {
        Customer customerToDelete = findCustomerByCnp(cnp);

        if (customerToDelete != null) {
            for (Account acc : customerToDelete.getAccounts()) {
                accounts.remove(acc.getIban());
            }
            customers.remove(customerToDelete);
            System.out.println("Clientul cu CNP " + cnp + " a fost șters cu succes.");
        } else {
            System.out.println("Eroare: Clientul nu a fost găsit.");
        }
    }


}
