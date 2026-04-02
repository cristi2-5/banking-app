package com.banca.bankingapp.services;

import com.banca.bankingapp.models.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements Repository<Transaction> {
    private static TransactionRepository instance;
    private final Connection connection;

    private TransactionRepository() {
        this.connection = DatabaseConfiguration.getConnection();
    }

    public static synchronized TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    @Override
    public void save(Transaction t) {
        String sql = "INSERT INTO transactions (transaction_id, type, amount, description, timestamp, account_iban) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, t.getTransactionId());
            pstmt.setString(2, t.getType());
            pstmt.setDouble(3, t.getAmount());
            pstmt.setString(4, t.getDescription());
            pstmt.setLong(5, t.getTimestamp());
            pstmt.setString(6, t.getAccountIban());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Transaction findById(String id) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Reconstruim obiectul folosind constructorul tău exact
                return new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getLong("timestamp"),
                        rs.getString("account_iban")
                );
            }
        } catch (SQLException e) {
            System.err.println("Eroare la căutarea tranzacției: " + e.getMessage());
        }

        return null; // Returnăm null dacă nu a fost găsită nicio tranzacție cu acel ID
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getLong("timestamp"),
                        rs.getString("account_iban") // Constructorul tău actualizat
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Transaction entity) {
    }

    @Override
    public void delete(String id) {
    }
}