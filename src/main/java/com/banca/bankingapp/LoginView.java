package com.banca.bankingapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginView {

    // Primește ca parametri "Dirijorul" (app) pentru a putea schimba scenele și "Creierul" (service)
    public static Scene getScene(BankingAppGUI app, BankingService bankingService) {
        Label titleLabel = new Label("Autentificare Java Bank");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Nume de utilizator");
        userField.setMaxWidth(200);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Parola");
        passField.setMaxWidth(200);

        Button loginBtn = new Button("Intră în cont");
        Button registerBtn = new Button("Nu ai cont? Înregistrează-te");
        Label messageLabel = new Label();

        // Logica Butoanelor (folosind metodele din app pentru a naviga)
        loginBtn.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();
            String role = bankingService.login(user, pass);

            if (role.equals("ADMIN")) {
                messageLabel.setText("Admin temporar indisponibil în module!");
                messageLabel.setStyle("-fx-text-fill: red;");
                app.showAdminDashboard();
            } else if (role.equals("USER")) {
                Customer customer = bankingService.getCustomers().stream()
                        .filter(c -> c.getUsername().equals(user))
                        .findFirst().orElse(null);

                app.showUserDashboard(customer); // Aici schimbăm scena!
            } else {
                messageLabel.setText("Date incorecte!");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        registerBtn.setOnAction(e -> app.showRegisterScene()); // Aici schimbăm scena!

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, userField, passField, loginBtn, registerBtn, messageLabel);

        return new Scene(layout, 400, 400);
    }
}