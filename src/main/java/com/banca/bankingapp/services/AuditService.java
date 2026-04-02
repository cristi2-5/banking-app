package com.banca.bankingapp.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static AuditService instance;
    private static final String FILE_PATH = "logs/audit.csv";

    private AuditService() {
        // Constructor privat pentru Singleton
    }

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void logAction(String actionName) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Format: nume_actiune, timestamp
            pw.println(actionName + "," + timestamp);

        } catch (IOException e) {
            System.err.println("Eroare la scrierea în audit: " + e.getMessage());
        }
    }
}
