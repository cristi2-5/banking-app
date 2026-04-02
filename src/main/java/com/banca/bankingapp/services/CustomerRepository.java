package com.banca.bankingapp.services;

import com.banca.bankingapp.models.Address;
import com.banca.bankingapp.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements Repository<Customer> {
    // Implementare Singleton
    private static CustomerRepository instance;
    private final Connection connection;

    private CustomerRepository() {
        this.connection = DatabaseConfiguration.getConnection();
    }

    public static synchronized CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }

    @Override
    public void save(Customer c) {
        String sql = "INSERT INTO customers (cnp, first_name, last_name, username, password, city, street, zip) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, c.getCnp());
            pstmt.setString(2, c.getFirstName());
            pstmt.setString(3, c.getLastName());
            pstmt.setString(4, c.getUsername());
            pstmt.setString(5, c.getPassword());
            pstmt.setString(6, c.getAddress().getCity());
            pstmt.setString(7, c.getAddress().getStreet());
            pstmt.setString(8, c.getAddress().getZipCode());
            pstmt.executeUpdate();
            System.out.println("Client salvat in DB: " + c.getCnp());
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea clientului: " + e.getMessage());
        }
    }

    @Override
    public Customer findById(String cnp) {
        String sql = "SELECT * FROM customers WHERE cnp = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cnp);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Address addr = new Address(rs.getString("city"), rs.getString("street"), rs.getString("zip"));
                Customer c = new Customer(rs.getString("cnp"), rs.getString("first_name"), rs.getString("last_name"), addr);
                c.setUsername(rs.getString("username"));
                c.setPassword(rs.getString("password"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Nu a fost găsit
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Address addr = new Address(rs.getString("city"), rs.getString("street"), rs.getString("zip"));
                Customer c = new Customer(rs.getString("cnp"), rs.getString("first_name"), rs.getString("last_name"), addr);
                c.setUsername(rs.getString("username"));
                c.setPassword(rs.getString("password"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Customer c) {
        String sql = "UPDATE customers SET first_name=?, last_name=?, username=?, password=?, city=?, street=?, zip=? WHERE cnp=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, c.getFirstName());
            pstmt.setString(2, c.getLastName());
            pstmt.setString(3, c.getUsername());
            pstmt.setString(4, c.getPassword());
            pstmt.setString(5, c.getAddress().getCity());
            pstmt.setString(6, c.getAddress().getStreet());
            pstmt.setString(7, c.getAddress().getZipCode());
            pstmt.setString(8, c.getCnp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String cnp) {
        String sql = "DELETE FROM customers WHERE cnp = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cnp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}