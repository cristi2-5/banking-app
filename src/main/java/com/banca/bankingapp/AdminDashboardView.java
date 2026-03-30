package com.banca.bankingapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AdminDashboardView {

    public static Scene getScene(BankingAppGUI app, BankingService bankingService) {
        // Titlu și subtitlu pentru a diferenția clar fereastra
        Label title = new Label("Panou Control Administrator");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");

        Label subtitle = new Label("Acces restricționat. Doar operațiuni administrative.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Butoanele de acțiune
        Button btnShowCustomers = new Button("Afișează Toți Clienții");
        Button btnApplyInterest = new Button("Aplică Dobândă (Conturi Economii)");
        Button btnLogout = new Button("Deconectare");

        // Stilizare pentru a arăta profesional (mai lat, culori specifice)
        String adminBtnStyle = "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 250px;";
        btnShowCustomers.setStyle(adminBtnStyle);
        btnApplyInterest.setStyle(adminBtnStyle);
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 250px;");

        // --- LOGICA BUTOANELOR ---

        // 1. Afișare Clienți
        // În AdminDashboardView.java, înlocuiește logica butonului btnShowCustomers:

        btnShowCustomers.setOnAction(e -> {
            // 1. Creăm un Dialog nou
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Baza de Date Clienți");
            dialog.setHeaderText("Toți clienții înregistrați în sistem:");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // 2. Creăm lista vizuală
            ListView<String> customerListView = new ListView<>();
            customerListView.setPrefSize(400, 300);

            // 3. Preluăm clienții de la serviciu și îi adăugăm în listă
            var allCustomers = bankingService.displayAllCustomers();

            if (allCustomers.isEmpty()) {
                customerListView.getItems().add("Nu există clienți înregistrați.");
            } else {
                for (Customer c : allCustomers) {
                    // Afișăm Nume, Prenume și CNP
                    String info = c.getLastName() + " " + c.getFirstName() + " (CNP: " + c.getCnp() + ")";
                    customerListView.getItems().add(info);
                }
            }

            dialog.getDialogPane().setContent(customerListView);
            dialog.showAndWait();
        });

        // 2. Aplicare Dobândă
        btnApplyInterest.setOnAction(e -> {
            // Apelăm metoda ta din BankingService
            bankingService.applyInterestToAllSavings();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Operațiune Reușită");
            alert.setHeaderText(null);
            alert.setContentText("Dobânda a fost aplicată cu succes tuturor conturilor de tip Economii (Savings)!");
            alert.showAndWait();
        });

        // 3. Deconectare
        btnLogout.setOnAction(e -> app.logout());

        // --- ASAMBLARE LAYOUT ---
        VBox layout = new VBox(20, title, subtitle, btnShowCustomers, btnApplyInterest, btnLogout);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 500, 400);
    }
}