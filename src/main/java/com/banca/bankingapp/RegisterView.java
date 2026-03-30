package com.banca.bankingapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class RegisterView {

    public static Scene getScene(BankingAppGUI app, BankingService bankingService) {
        // Titlu
        Label titleLabel = new Label("Înregistrare Client Nou");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Folosim un GridPane pentru a alinia etichetele cu câmpurile de text
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Secțiunea: Date Personale
        TextField lastNameField = new TextField(); lastNameField.setPromptText("Nume");
        TextField firstNameField = new TextField(); firstNameField.setPromptText("Prenume");
        TextField cnpField = new TextField(); cnpField.setPromptText("CNP");

        grid.add(new Label("Nume:"), 0, 0); grid.add(lastNameField, 1, 0);
        grid.add(new Label("Prenume:"), 0, 1); grid.add(firstNameField, 1, 1);
        grid.add(new Label("CNP:"), 0, 2); grid.add(cnpField, 1, 2);

        // Secțiunea: Adresă (Aici am pus Zip Code conform cererii tale)
        TextField cityField = new TextField(); cityField.setPromptText("Ex: București");
        TextField streetField = new TextField(); streetField.setPromptText("Ex: Calea Victoriei");
        TextField zipField = new TextField(); zipField.setPromptText("Ex: 010061");

        grid.add(new Label("Oraș:"), 0, 3); grid.add(cityField, 1, 3);
        grid.add(new Label("Stradă:"), 0, 4); grid.add(streetField, 1, 4);
        grid.add(new Label("Zip Code:"), 0, 5); grid.add(zipField, 1, 5);

        // Secțiunea: Cont
        TextField userField = new TextField(); userField.setPromptText("Username dorit");
        PasswordField passField = new PasswordField(); passField.setPromptText("Parola");

        grid.add(new Label("Username:"), 0, 6); grid.add(userField, 1, 6);
        grid.add(new Label("Parolă:"), 0, 7); grid.add(passField, 1, 7);

        // Butoane și Mesaje
        Button submitBtn = new Button("Creează Cont");
        submitBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Button backBtn = new Button("Înapoi la Login");
        Label messageLabel = new Label();

        // Logica înregistrării
        submitBtn.setOnAction(e -> {
            // Validare simplă: verificăm dacă câmpurile esențiale sunt goale
            if (lastNameField.getText().isEmpty() || cnpField.getText().isEmpty() ||
                    userField.getText().isEmpty() || zipField.getText().isEmpty()) {
                messageLabel.setText("Eroare: Toate câmpurile sunt obligatorii!");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // 1. Creăm obiectul Address cu datele REALE (inclusiv Zip Code)
            Address addr = new Address(cityField.getText(), streetField.getText(), zipField.getText());

            // 2. Creăm obiectul Customer
            Customer newCust = new Customer(cnpField.getText(),firstNameField.getText(), lastNameField.getText(), addr);
            newCust.setUsername(userField.getText());
            newCust.setPassword(passField.getText());

            // 3. Salvăm în serviciu
            bankingService.addCustomer(newCust);

            messageLabel.setText("Cont creat cu succes! Te poți loga.");
            messageLabel.setTextFill(Color.GREEN);

            // Opțional: Curățăm câmpurile după succes
            clearFields(lastNameField, firstNameField, cnpField, cityField, streetField, zipField, userField, passField);
        });

        backBtn.setOnAction(e -> app.showLoginScene());

        // Layout-ul Final
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, grid, submitBtn, backBtn, messageLabel);

        return new Scene(layout, 450, 550);
    }

    private static void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }
}