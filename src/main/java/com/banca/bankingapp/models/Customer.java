package com.banca.bankingapp.models;

import java.util.*;

public class Customer implements Comparable<Customer>{
    private String cnp;
    private String firstName;
    private String lastName;
    private Address address;
    private final List<Account> accounts;
    private String username;
    private String password;

    public Customer(String cnp, String firstName, String lastName, Address address) {
        this.cnp = cnp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.accounts = new ArrayList<Account>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCnp() {
        return cnp;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public void addAccount(Account account){
        this.accounts.add(account);
    }

    @Override
    public int compareTo(Customer other) {
        int lastNameComparison = this.lastName.compareTo(other.lastName);
        if (lastNameComparison != 0) {
            return lastNameComparison;
        }
        return this.firstName.compareTo(other.firstName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(cnp, customer.cnp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnp);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (CNP: " + cnp + ") - Adresa: " + address.getCity();
    }
}
