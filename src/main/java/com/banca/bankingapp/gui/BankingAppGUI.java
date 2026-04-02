package com.banca.bankingapp.gui;

import com.banca.bankingapp.models.Address;
import com.banca.bankingapp.models.CheckingAccount;
import com.banca.bankingapp.models.Customer;
import com.banca.bankingapp.models.SavingsAccount;
import com.banca.bankingapp.services.BankingService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankingAppGUI extends Application {

    private BankingService bankingService;
    private Stage window;
    private Customer loggedInCustomer;

    @Override
    public void init() {
        bankingService = new BankingService();
    }

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        window.setTitle("Java Bank");

        // Pornim direct cu ecranul de Login
        showLoginScene();
        window.show();
    }

    // --- METODE DE NAVIGARE --- //

    public void showLoginScene() {
        // Cerem Scene-ul de la clasa LoginView și îi dăm acces la "app" (this) și la serviciu
        Scene scene = LoginView.getScene(this, bankingService);
        window.setScene(scene);
    }

    public void showRegisterScene() {
        Scene scene = RegisterView.getScene(this, bankingService);
        window.setScene(scene);
    }

    public void showUserDashboard(Customer customer) {
        this.loggedInCustomer = customer; // Salvăm cine s-a logat
        Scene scene = UserDashboardView.getScene(this, bankingService, loggedInCustomer);
        window.setScene(scene);
    }

    public void showAdminDashboard() {
        Scene scene = AdminDashboardView.getScene(this, bankingService);
        window.setScene(scene);
    }

    public void logout() {
        this.loggedInCustomer = null;
        showLoginScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}