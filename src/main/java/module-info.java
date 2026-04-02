module com.banca.bankingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.banca.bankingapp to javafx.fxml;
    exports com.banca.bankingapp;
    exports com.banca.bankingapp.models;
    opens com.banca.bankingapp.models to javafx.fxml;
    exports com.banca.bankingapp.services;
    opens com.banca.bankingapp.services to javafx.fxml;
    exports com.banca.bankingapp.gui;
    opens com.banca.bankingapp.gui to javafx.fxml;
}