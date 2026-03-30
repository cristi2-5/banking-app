module com.banca.bankingapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.banca.bankingapp to javafx.fxml;
    exports com.banca.bankingapp;
}