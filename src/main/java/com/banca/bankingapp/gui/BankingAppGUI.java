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

        Address addr1 = new Address("București", "Calea Victoriei 12", "010061");
        Customer c1 = new Customer("1234567890123", "Ion", "Ionescu", addr1);
        c1.setUsername("ion");
        c1.setPassword("ion123");
        bankingService.addCustomer(c1);

        // Adăugăm un cont de tip CheckingAccount (taxă 5 RON)
        CheckingAccount acc1 = new CheckingAccount("RO01BANC123", 1500.0, c1, 5.0);
        bankingService.addAccountToCustomer(c1.getCnp(), acc1);


        // --- USER 2: Popescu Maria (Cont Economii) ---
        Address addr2 = new Address("Cluj-Napoca", "Str. Eroilor 5", "400129");
        Customer c2 = new Customer("2345678901234", "Maria", "Popescu", addr2);
        c2.setUsername("maria");
        c2.setPassword("maria123");
        bankingService.addCustomer(c2);

        // Adăugăm un cont de tip SavingsAccount (dobândă 5%)
        SavingsAccount acc2 = new SavingsAccount("RO02BANC456", 5000.0, c2, 0.05);
        bankingService.addAccountToCustomer(c2.getCnp(), acc2);


        // --- USER 3: Vasilescu Dan (Două conturi) ---
        Address addr3 = new Address("Iași", "Str. Palat 1", "700032");
        Customer c3 = new Customer("2345078901234", "Dan", "Vasilescu", addr3);
        c3.setUsername("dan");
        c3.setPassword("dan123");
        bankingService.addCustomer(c3);

        // Dan are și cont curent și cont de economii pentru a testa lista multiplă
        bankingService.addAccountToCustomer(c3.getCnp(), new CheckingAccount("RO03BANC789", 200.0, c3, 5.0));
        bankingService.addAccountToCustomer(c3.getCnp(), new SavingsAccount("RO04BANC000", 10000.0, c3, 0.07));
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