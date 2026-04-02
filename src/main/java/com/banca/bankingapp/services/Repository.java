package com.banca.bankingapp.services;

import java.util.List;

public interface Repository<T> {
    void save(T entity);         // CREATE
    T findById(String id);       // READ (unul singur)
    List<T> findAll();           // READ (toate)
    void update(T entity);       // UPDATE
    void delete(String id);      // DELETE
}