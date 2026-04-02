package com.banca.bankingapp.services;

import com.banca.bankingapp.models.Account;
import com.banca.bankingapp.models.Customer;
import com.banca.bankingapp.models.SavingsAccount;
import com.banca.bankingapp.models.Transaction;

import java.util.*;

public class BankingService {
    private Set<Customer> customers;
    private Map<String, Account> accounts;
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";

    public BankingService() {
        this.customers = new TreeSet<>();
        this.accounts = new HashMap<>();
    }

    public String login(String username, String password) {
        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            AuditService.getInstance().logAction("Logare ADMIN");
            return "ADMIN";
        }
        for (Customer c : customers) {
            if (username.equals(c.getUsername()) && password.equals(c.getPassword())) {
                AuditService.getInstance().logAction("Logare USER | Username: " + c.getUsername() + " | CNP: " + c.getCnp());
                return "USER";
            }
        }
        return "INVALID";
    }

    public void addCustomer(Customer customer){
        customers.add(customer);
    }

    public Set<Customer> getCustomers(){
        return customers;
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
            String ownerName = a.getOwner().getLastName() + " " + a.getOwner().getFirstName();
            String ownerCnp = a.getOwner().getCnp();

            AuditService.getInstance().logAction("Depunere: " + amount + " RON | Cont: " + iban + " | Client: " + ownerName + " (CNP: " + ownerCnp + ")");
        }
    }

    public void withdraw(String iban,double amount){
        Account a = accounts.get(iban);
        if(a!=null){
            a.withdraw(amount);
            String ownerName = a.getOwner().getLastName() + " " + a.getOwner().getFirstName();
            String ownerCnp = a.getOwner().getCnp();

            AuditService.getInstance().logAction("Retragere: " + amount + " RON | Cont: " + iban + " | Client: " + ownerName + " (CNP: " + ownerCnp + ")");
        }
    }

    public void transfer(String sourceIban, String destIban, double amount){
        Account s = accounts.get(sourceIban);
        Account d = accounts.get(destIban);

        if(s!=null&&d!=null){
            double value = s.getBalance();

            String senderName = s.getOwner().getLastName();
            String senderCnp = s.getOwner().getCnp();

            if(value < amount){
                System.out.println("Transferul nu poate fi efectuat! Fonduri insuficiente");
                AuditService.getInstance().logAction("Transfer ESUAT (Fonduri insuficiente) | Cont Sursa: " + sourceIban + " | Suma: " + amount);
            }
            else{
                s.withdraw(amount);
                d.deposit(amount);

                AuditService.getInstance().logAction("Transfer: " + amount + " RON | De la: " + sourceIban + " | Catre: " + destIban + " | Initiat de: " + senderName + " (CNP: " + senderCnp + ")");
            }
        }
    }

    public double getAccountBalance(String iban){
        Account a = accounts.get(iban);
        if(a!=null){
            return a.getBalance();
        }
       return 0.0;
    }

    public Account getAccount(String iban) {
        return accounts.get(iban);
    }

    public Set<Customer> displayAllCustomers(){
        return customers;
    }

    public List<Transaction> getAccountTransactions(String iban){
        Account a = accounts.get(iban);
        if(a!=null){
            return a.getTransactions();
        }
        return new ArrayList<>();
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
