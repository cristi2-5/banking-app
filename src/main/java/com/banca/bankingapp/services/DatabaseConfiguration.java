package com.banca.bankingapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfiguration {
    private static final String DB_URL = "jdbc:sqlite:banking_system.db";
    private static Connection connection;

    // Singleton pentru conexiune
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                createTables(); // Creăm tabelele imediat ce ne conectăm
                System.out.println("Conexiune la baza de date stabilită cu succes!");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la conectarea la DB: " + e.getMessage());
        }
        return connection;
    }

    private static void createTables() {
        // Aici definim SQL-ul pentru toate cele 4 entități
        String createCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
                "cnp TEXT PRIMARY KEY, " +
                "first_name TEXT, last_name TEXT, " +
                "username TEXT, password TEXT, " +
                "city TEXT, street TEXT, zip TEXT);";

        String createAccounts = "CREATE TABLE IF NOT EXISTS accounts (" +
                "iban TEXT PRIMARY KEY, " +
                "balance REAL, " +
                "type TEXT, " +          // SAVINGS sau CHECKING
                "extra_param REAL, " +   // Aici punem dobânda (0.05) sau taxa de mentenanță
                "customer_cnp TEXT, " +
                "FOREIGN KEY(customer_cnp) REFERENCES customers(cnp));";

        String createCards = "CREATE TABLE IF NOT EXISTS cards (" +
                "card_number TEXT PRIMARY KEY, " +
                "pin TEXT, " +
                "expiry_date TEXT, " +
                "status BOOLEAN, " +
                "type TEXT, " +          // DEBIT sau CREDIT
                "credit_limit REAL, " +  // Doar pentru credit
                "account_iban TEXT, " +
                "FOREIGN KEY(account_iban) REFERENCES accounts(iban));";

        String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id TEXT PRIMARY KEY, " +
                "type TEXT, " +
                "amount REAL, " +
                "description TEXT, " +
                "timestamp INTEGER, " +
                "account_iban TEXT, " +
                "FOREIGN KEY(account_iban) REFERENCES accounts(iban));";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCustomers);
            stmt.execute(createAccounts);
            stmt.execute(createCards);
            stmt.execute(createTransactions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}