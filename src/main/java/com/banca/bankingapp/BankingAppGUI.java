package com.banca.bankingapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BankingAppGUI extends Application {

    private BankingService bankingService;
    private Stage window; // Fereastra principala

    @Override
    public void init() {
        // Aici inițializăm "creierul" băncii înainte să se deseneze interfața
        bankingService = new BankingService();
    }

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        window.setTitle("Java Bank - Autentificare");

        // Desenăm scena de Login
        Scene loginScene = createLoginScene();

        window.setScene(loginScene);
        window.show();
    }

    // Metodă separată pentru a păstra codul curat
    private Scene createLoginScene() {
        // 1. Actorii
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

        // 2. Logica Butoanelor
        loginBtn.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();
            String role = bankingService.login(user, pass);

            if (role.equals("ADMIN")) {
                messageLabel.setText("Bine ai venit, Admin!");
                messageLabel.setStyle("-fx-text-fill: green;");
                // Aici vom schimba scena spre Dashboard Admin (Pasul urmator)
            } else if (role.equals("USER")) {
                messageLabel.setText("Logare reușită!");
                messageLabel.setStyle("-fx-text-fill: green;");
                // Aici vom schimba scena spre Dashboard User
            } else {
                messageLabel.setText("Date incorecte!");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        registerBtn.setOnAction(e -> {
            // Când apasă înregistrare, schimbăm "decorul" (Scena)
            window.setScene(createRegisterScene());
        });

        // 3. Așezarea în pagină (Vertical Box)
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, userField, passField, loginBtn, registerBtn, messageLabel);

        return new Scene(layout, 400, 400);
    }

    // Metoda care desenează scena de Înregistrare
    private Scene createRegisterScene() {
        Label titleLabel = new Label("Înregistrare Client Nou");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField numeField = new TextField(); numeField.setPromptText("Nume Familie");
        TextField prenumeField = new TextField(); prenumeField.setPromptText("Prenume");
        TextField cnpField = new TextField(); cnpField.setPromptText("CNP");
        TextField userField = new TextField(); userField.setPromptText("Username dorit");
        PasswordField passField = new PasswordField(); passField.setPromptText("Parola");

        Button submitBtn = new Button("Creează Cont");
        Button backBtn = new Button("Înapoi la Login");
        Label messageLabel = new Label();

        // Logica înregistrării
        submitBtn.setOnAction(e -> {
            // Creăm adresa dummy și clientul
            Address addr = new Address("București", "Victoriei", "1");
            Customer newCust = new Customer(numeField.getText(), prenumeField.getText(), cnpField.getText(), addr);
            newCust.setUsername(userField.getText());
            newCust.setPassword(passField.getText());

            bankingService.addCustomer(newCust);

            messageLabel.setText("Cont creat! Acum te poți loga.");
            messageLabel.setStyle("-fx-text-fill: green;");
        });

        // Butonul de înapoi ne întoarce la scena de Login
        backBtn.setOnAction(e -> window.setScene(createLoginScene()));

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setMaxWidth(250); // Micsoram casutele
        layout.getChildren().addAll(titleLabel, numeField, prenumeField, cnpField, userField, passField, submitBtn, backBtn, messageLabel);

        // Folosim un alt VBox exterior pentru a centra totul frumos
        VBox root = new VBox(layout);
        root.setAlignment(Pos.CENTER);

        return new Scene(root, 400, 400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}