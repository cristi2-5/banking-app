package com.banca.bankingapp.services;

import com.banca.bankingapp.models.Account;
import com.banca.bankingapp.models.Card;
import com.banca.bankingapp.models.CreditCard;
import com.banca.bankingapp.models.DebitCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository implements Repository<Card> {
    private static CardRepository instance;
    private final Connection connection;

    private CardRepository() {
        this.connection = DatabaseConfiguration.getConnection();
    }

    public static synchronized CardRepository getInstance() {
        if (instance == null) {
            instance = new CardRepository();
        }
        return instance;
    }

    @Override
    public void save(Card card) {
        String sql = "INSERT INTO cards (card_number, pin, expiry_date, status, type, credit_limit, account_iban) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, card.getCardNumber());
            pstmt.setString(2, card.getPin());
            pstmt.setString(3, card.getExpiryDate());
            pstmt.setBoolean(4, card.isActive());

            if (card instanceof CreditCard) {
                pstmt.setString(5, "CREDIT");
                pstmt.setDouble(6, ((CreditCard) card).getCreditLimit());
            } else {
                pstmt.setString(5, "DEBIT");
                pstmt.setDouble(6, 0.0);
            }

            pstmt.setString(7, card.getAccount().getIban());
            pstmt.executeUpdate();
            System.out.println("Card salvat in DB: " + card.getCardNumber());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Card findById(String cardNumber) {
        String sql = "SELECT * FROM cards WHERE card_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String pin = rs.getString("pin");
                String expiry = rs.getString("expiry_date");
                boolean status = rs.getBoolean("status");
                String type = rs.getString("type");
                double limit = rs.getDouble("credit_limit");
                String iban = rs.getString("account_iban");

                Account acc = AccountRepository.getInstance().findById(iban);

                if ("CREDIT".equals(type)) {
                    return new CreditCard(cardNumber, pin, expiry, status, acc, limit);
                } else {
                    return new DebitCard(cardNumber, pin, expiry, status, acc); // sau return new Card(...) in functie de constructorul tau
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Card> findAll() {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT * FROM cards";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String cardNumber = rs.getString("card_number");
                String pin = rs.getString("pin");
                String expiry = rs.getString("expiry_date");
                boolean status = rs.getBoolean("status");
                String type = rs.getString("type");
                double limit = rs.getDouble("credit_limit");
                String iban = rs.getString("account_iban");

                // FOARTE IMPORTANT: Luăm contul din AccountRepository pentru a-l asocia cardului
                Account acc = AccountRepository.getInstance().findById(iban);

                Card card;
                if ("CREDIT".equals(type)) {
                    card = new CreditCard(cardNumber, pin, expiry, status, acc, limit);
                } else {
                    card = new DebitCard(cardNumber, pin, expiry, status, acc);
                }
                list.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Card card) {
        String sql = "UPDATE cards SET pin = ?, status = ? WHERE card_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, card.getPin());
            pstmt.setBoolean(2, card.isActive());
            pstmt.setString(3, card.getCardNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String cardNumber) {
        String sql = "DELETE FROM cards WHERE card_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}