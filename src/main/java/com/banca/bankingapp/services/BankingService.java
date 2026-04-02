package com.banca.bankingapp.services;

import com.banca.bankingapp.models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BankingService {
    private Set<Customer> customers;
    private Map<String, Account> accounts;
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";

    public BankingService() {
        this.customers = new TreeSet<>();
        this.accounts = new HashMap<>();
        loadDataFromDatabase();
    }

    public void loadDataFromDatabase() {
        List<Customer> dbCustomers = CustomerRepository.getInstance().findAll();
        for (Customer c : dbCustomers) {
            this.customers.add(c);
        }

        List<Account> dbAccounts = AccountRepository.getInstance().findAll();
        for (Account acc : dbAccounts) {
            this.accounts.put(acc.getIban(), acc);

            Customer owner = findCustomerByCnp(acc.getOwner().getCnp());
            if (owner != null) {
                owner.addAccount(acc);
            }
        }

        List<Card> dbCards = CardRepository.getInstance().findAll();
        for (Card card : dbCards) {
            Account acc = accounts.get(card.getAccount().getIban());
            if (acc != null) {
                acc.addCard(card);
            }
        }

        List<Transaction> dbTransactions = TransactionRepository.getInstance().findAll();
        for (Transaction t : dbTransactions) {
            Account acc = accounts.get(t.getAccountIban());
            if (acc != null) {
                acc.addTransaction(t);
            }
        }

        System.out.println("✅ Date încărcate: " + customers.size() + " clienți și " + accounts.size() + " conturi.");
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
        CustomerRepository.getInstance().save(customer);
        AuditService.getInstance().logAction(customer.getUsername()+ " s-a inregistrat cu succes!");
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
            AccountRepository.getInstance().save(account);
            accounts.put(account.getIban(),account);
        }
    }

    public void deposit(String iban,double amount){
        Account a = accounts.get(iban);
        if(a!=null)
        {
            a.deposit(amount);
            AccountRepository.getInstance().update(a);

            String ownerName = a.getOwner().getLastName() + " " + a.getOwner().getFirstName();
            String ownerCnp = a.getOwner().getCnp();

            AuditService.getInstance().logAction("Depunere: " + amount + " RON | Cont: " + iban + " | Client: " + ownerName + " (CNP: " + ownerCnp + ")");
        }
    }

    public void withdraw(String iban,double amount){
        Account a = accounts.get(iban);
        if(a!=null){
            a.withdraw(amount);
            AccountRepository.getInstance().update(a);

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
                AccountRepository.getInstance().update(s);
                AccountRepository.getInstance().update(d);

                long time = System.currentTimeMillis();
                Transaction tSource = new Transaction(UUID.randomUUID().toString(), "TRANSFER_OUT", amount, "Transfer către " + destIban, time, sourceIban);
                TransactionRepository.getInstance().save(tSource);

                Transaction tDest = new Transaction(UUID.randomUUID().toString(), "TRANSFER_IN", amount, "Transfer de la " + sourceIban, time, destIban);
                TransactionRepository.getInstance().save(tDest);
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

    public void issueCard(String iban, String pin, String type, double creditLimitIfApply) {
        Account acc = accounts.get(iban);

        if (acc != null) {
            String cardNumber = "4111" + (100000000000L + new Random().nextLong(900000000000L));
            String expiryDate = LocalDate.now().plusYears(4).format(DateTimeFormatter.ofPattern("MM/yy"));

            boolean status = true;

            Card newCard;
            if ("CREDIT".equalsIgnoreCase(type)) {
                newCard = new CreditCard(cardNumber, pin, expiryDate, status, acc, creditLimitIfApply);
            } else {
                newCard = new DebitCard(cardNumber, pin, expiryDate, status, acc);
            }
            acc.addCard(newCard);

            String ownerName = acc.getOwner().getLastName();
            String logInfo = String.format("Emitere Card %s | Nr: %s | Exp: %s | Cont: %s | Client: %s",
                    type, cardNumber, expiryDate, iban, ownerName);
            CardRepository.getInstance().save(newCard);
            AuditService.getInstance().logAction(logInfo);
        } else {
            System.out.println("Eroare: IBAN-ul nu există!");
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
