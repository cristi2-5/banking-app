package com.banca.bankingapp.services;

import com.banca.bankingapp.models.Account;
import com.banca.bankingapp.models.CheckingAccount;
import com.banca.bankingapp.models.Customer;
import com.banca.bankingapp.models.SavingsAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository implements Repository<Account> {
    private static AccountRepository instance;
    private final Connection connection;

    private AccountRepository() {
        this.connection = DatabaseConfiguration.getConnection();
    }

    public static synchronized AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    @Override
    public void save(Account acc) {
        String sql = "INSERT INTO accounts (iban, balance, type, extra_param, customer_cnp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, acc.getIban());
            pstmt.setDouble(2, acc.getBalance());

            if (acc instanceof SavingsAccount) {
                pstmt.setString(3, "SAVINGS");
                pstmt.setDouble(4, ((SavingsAccount) acc).getInterestRate());
            } else if (acc instanceof CheckingAccount) {
                pstmt.setString(3, "CHECKING");
                pstmt.setDouble(4, ((CheckingAccount) acc).getMaintenanceFee());
            }

            pstmt.setString(5, acc.getOwner().getCnp());
            pstmt.executeUpdate();
            System.out.println("Cont salvat in DB: " + acc.getIban());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Account findById(String iban) {
        String sql = "SELECT * FROM accounts WHERE iban = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, iban);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                double balance = rs.getDouble("balance");
                double extraParam = rs.getDouble("extra_param");
                String cnp = rs.getString("customer_cnp");

                // Găsim clientul pentru a-l atașa contului
                Customer owner = CustomerRepository.getInstance().findById(cnp);

                if ("SAVINGS".equals(type)) {
                    return new SavingsAccount(iban, balance, owner, extraParam);
                } else {
                    return new CheckingAccount(iban, balance, owner, extraParam);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String iban = rs.getString("iban");
                String type = rs.getString("type");
                double balance = rs.getDouble("balance");
                double extraParam = rs.getDouble("extra_param");
                String cnp = rs.getString("customer_cnp");

                Customer owner = CustomerRepository.getInstance().findById(cnp);
                if (owner != null) {
                    if ("SAVINGS".equals(type)) {
                        list.add(new SavingsAccount(iban, balance, owner, extraParam));
                    } else {
                        list.add(new CheckingAccount(iban, balance, owner, extraParam));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Account acc) {
        String sql = "UPDATE accounts SET balance = ? WHERE iban = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, acc.getBalance());
            pstmt.setString(2, acc.getIban());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String iban) {
        String sql = "DELETE FROM accounts WHERE iban = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, iban);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}