package com.banca.bankingapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class UserDashboardView {

    public static Scene getScene(BankingAppGUI app, BankingService bankingService, Customer loggedInCustomer) {
        Label welcomeLabel = new Label("Salutare, " + loggedInCustomer.getFirstName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

        Label totalBalanceLabel = new Label();
        totalBalanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        ListView<String> accountsList = new ListView<>();
        actualizeazaConturi(accountsList, loggedInCustomer);
        actualizeazaSold(accountsList, totalBalanceLabel, loggedInCustomer, bankingService);

        // Butoane Acțiuni Existente
        Button btnDeposit = new Button("Depunere");
        Button btnWithdraw = new Button("Retragere");
        Button btnTransfer = new Button("Transfer");
        Button btnStatement = new Button("Extras de Cont");
        Button btnHistory = new Button("Istoric Tranzacții");
        Button btnCards = new Button("Carduri");

        // Buton NOU: Deschidere Cont
        Button btnOpenAccount = new Button(" + Deschide Cont Nou");
        btnOpenAccount.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnLogout = new Button("Ieșire");

        // Layout butoane
        HBox topActions = new HBox(15, btnOpenAccount);
        topActions.setAlignment(Pos.CENTER);

        HBox bottomActions = new HBox(10, btnDeposit, btnWithdraw, btnTransfer,btnStatement, btnHistory, btnCards);
        bottomActions.setAlignment(Pos.CENTER);

        // --- LOGICA: DESCHIDERE CONT NOU ---
        btnOpenAccount.setOnAction(e -> {
            // 1. Întrebăm tipul de cont
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Cont Curent", "Cont Curent", "Cont Economii");
            dialog.setTitle("Cont Nou");
            dialog.setHeaderText("Ce tip de cont dorești să deschizi?");
            dialog.setContentText("Alege tipul:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(type -> {
                String iban = generaIBAN();
                Account contNou;

                if (type.equals("Cont Economii")) {
                    // Presupunem o dobândă de 5% (0.05) implicită
                    contNou = new SavingsAccount(iban, 0.0, loggedInCustomer, 0.05);
                } else {
                    double taxa = 5.0;
                    contNou = new CheckingAccount(iban, 0.0, loggedInCustomer, taxa);
                }

                // 2. Adăugăm contul la client și în sistem
                bankingService.addAccountToCustomer(loggedInCustomer.getCnp(), contNou);

                // 3. Refresh vizual
                actualizeazaConturi(accountsList, loggedInCustomer);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Felicitări! Ai deschis un " + type + " cu IBAN-ul: " + iban);
                alert.show();
            });
        });

        // --- LOGICA: DEPUNERE (Existentă) ---
        btnDeposit.setOnAction(e -> {
            String selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String iban = selected.split(" - ")[0];
                TextInputDialog input = new TextInputDialog("0");
                input.setHeaderText("Depunere în " + iban);
                input.showAndWait().ifPresent(suma -> {
                    try {
                        bankingService.deposit(iban, Double.parseDouble(suma));
                        actualizeazaConturi(accountsList, loggedInCustomer);
                        actualizeazaSold(accountsList, totalBalanceLabel, loggedInCustomer, bankingService);
                    } catch (Exception ex) { afiseazaEroare("Sumă invalidă!"); }
                });
            }
        });

        // --- LOGICA: RETRAGERE ---
        btnWithdraw.setOnAction(e -> {
            String selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String iban = selected.split(" - ")[0]; // Extragem IBAN-ul

                TextInputDialog input = new TextInputDialog("0");
                input.setTitle("Retragere Numerar");
                input.setHeaderText("Retragere din: " + iban);
                input.setContentText("Introdu suma dorită:");

                input.showAndWait().ifPresent(suma -> {
                    try {
                        double amount = Double.parseDouble(suma);
                        bankingService.withdraw(iban, amount);
                        actualizeazaConturi(accountsList, loggedInCustomer); // Refresh vizual
                        actualizeazaSold(accountsList, totalBalanceLabel, loggedInCustomer, bankingService);
                    } catch (NumberFormatException ex) {
                        afiseazaEroare("Te rog să introduci un număr valid!");
                    }
                });
            } else {
                afiseazaEroare("Selectează un cont din listă mai întâi!");
            }
        });

        // --- LOGICA: TRANSFER ---
        btnTransfer.setOnAction(e -> {
            String selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String sourceIban = selected.split(" - ")[0]; // Contul din care pleacă banii

                // Creăm un dialog personalizat
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Transfer Bancar");
                dialog.setHeaderText("Transfer din contul:\n" + sourceIban);

                // Câmpurile pe care le va completa utilizatorul
                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField destIbanField = new TextField();
                destIbanField.setPromptText("Ex: RO02BANC456");
                TextField amountField = new TextField();
                amountField.setPromptText("0.00");

                grid.add(new Label("IBAN Destinație:"), 0, 0);
                grid.add(destIbanField, 1, 0);
                grid.add(new Label("Sumă (RON):"), 0, 1);
                grid.add(amountField, 1, 1);

                dialog.getDialogPane().setContent(grid);
                // Adăugăm butoanele standard OK și Cancel
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                // Ce se întâmplă când dă click pe OK
                dialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            double amount = Double.parseDouble(amountField.getText());
                            String destIban = destIbanField.getText();

                            // Apelăm metoda ta din BankingService
                            bankingService.transfer(sourceIban, destIban, amount);
                            actualizeazaConturi(accountsList, loggedInCustomer); // Refresh la ecran
                            actualizeazaSold(accountsList, totalBalanceLabel, loggedInCustomer, bankingService);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Transfer procesat! Verifică soldul.");
                            alert.show();
                        } catch (NumberFormatException ex) {
                            afiseazaEroare("Suma introdusă este invalidă!");
                        }
                    }
                });
            } else {
                afiseazaEroare("Selectează contul sursă din listă!");
            }
        });

        // --- LOGICA: ISTORIC TRANZACȚII ---
        btnHistory.setOnAction(e -> {
            String selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String iban = selected.split(" - ")[0];

                // Preluăm tranzacțiile de la serviciu
                List<Transaction> tranzactii = bankingService.getAccountTransactions(iban);

                // Creăm o fereastră nouă de tip Dialog
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Extras de Cont");
                dialog.setHeaderText("Istoric pentru: " + iban);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                ListView<String> historyList = new ListView<>();
                historyList.setPrefWidth(400);
                historyList.setPrefHeight(300);

                if (tranzactii.isEmpty()) {
                    historyList.getItems().add("Nu există tranzacții pentru acest cont.");
                } else {
                    // Presupunem că Transaction are o metodă toString() bine definită
                    for (Transaction t : tranzactii) {
                        historyList.getItems().add(t.toString());
                    }
                }

                dialog.getDialogPane().setContent(historyList);
                dialog.showAndWait();
            } else {
                afiseazaEroare("Selectează un cont pentru a vedea istoricul!");
            }
        });


        btnStatement.setOnAction(e -> {
            String selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String iban = selected.split(" - ")[0];
                List<Transaction> tranzactii = bankingService.getAccountTransactions(iban);

                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Extras de Cont");
                dialog.setHeaderText("Extras generat pentru IBAN:\n" + iban);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                ListView<String> historyList = new ListView<>();
                historyList.setPrefSize(350, 250);
                if (tranzactii.isEmpty()) historyList.getItems().add("Nicio tranzacție găsită.");
                else tranzactii.forEach(t -> historyList.getItems().add(t.toString()));

                dialog.getDialogPane().setContent(historyList);
                dialog.showAndWait();
            } else {
                afiseazaEroare("Selectează un cont pentru extras!");
            }
        });

        // --- GESTIUNE CARDURI (Emitere + Schimbare PIN) ---
        btnCards.setOnAction(e -> {
            String selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String iban = selected.split(" - ")[0];
                Account account = bankingService.getAccount(iban);

                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Gestiune Carduri");
                dialog.setHeaderText("Carduri pentru contul: " + iban);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                VBox vBox = new VBox(10);
                vBox.setPadding(new Insets(15));

                // Folosim direct obiecte Card pentru a putea lua PIN-ul/Numărul mai târziu
                ListView<Card> listaCarduri = new ListView<>();
                if(account.getCards() != null) {
                    listaCarduri.getItems().addAll(account.getCards());
                }

                Button btnEmit = new Button("+ Emite Card Nou");
                Button btnChangePin = new Button("Schimbă PIN");

                // 1. LOGICA PENTRU EMITERE CARD NOU
                btnEmit.setOnAction(ev -> {
                    ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("DebitCard", "DebitCard", "CreditCard");
                    choiceDialog.setTitle("Tip Card");
                    choiceDialog.setHeaderText("Alege tipul cardului:");
                    choiceDialog.setContentText("Tip:");

                    choiceDialog.showAndWait().ifPresent(tipCard -> {
                        String cardNum = (tipCard.equals("CreditCard") ? "5" : "4") + "000" + (1000 + new Random().nextInt(9000)) + "20003000";
                        String pin = "1234";
                        String expiry = LocalDate.now().plusYears(4).toString();

                        Card newCard = null;

                        if (tipCard.equals("CreditCard")) {
                            newCard = new CreditCard(cardNum, pin, expiry, true, account, 5000.0);
                        } else {
                            newCard = new DebitCard(cardNum, pin, expiry, true, account);
                        }

                        // Salvăm în cont și afișăm pe ecran
                        account.addCard(newCard);
                        listaCarduri.getItems().add(newCard);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Card emis cu succes!\nTip: " + tipCard + "\nNr: " + cardNum + "\nPIN initial: " + pin);
                        alert.show();
                    });
                });

                // 2. LOGICA PENTRU SCHIMBARE PIN
                btnChangePin.setOnAction(ev -> {
                    Card selectedCard = listaCarduri.getSelectionModel().getSelectedItem();
                    if(selectedCard != null) {
                        TextInputDialog pinDialog = new TextInputDialog();
                        pinDialog.setTitle("Schimbare PIN");
                        pinDialog.setHeaderText("Schimbare PIN pentru cardul:\n" + selectedCard.getCardNumber());
                        pinDialog.setContentText("Introdu noul PIN (4 cifre):");

                        pinDialog.showAndWait().ifPresent(newPin -> {
                            if(newPin.matches("\\d{4}")) {
                                selectedCard.setPin(newPin); // Asigură-te că ai setPin() în clasa Card
                                Alert a = new Alert(Alert.AlertType.INFORMATION, "PIN schimbat cu succes!");
                                a.show();
                                listaCarduri.refresh(); // Actualizăm vizual
                            } else {
                                afiseazaEroare("PIN-ul trebuie să conțină exact 4 cifre!");
                            }
                        });
                    } else {
                        afiseazaEroare("Selectează un card din listă mai întâi!");
                    }
                });

                HBox cardBtns = new HBox(10, btnEmit, btnChangePin);
                vBox.getChildren().addAll(new Label("Cardurile tale:"), listaCarduri, cardBtns);
                dialog.getDialogPane().setContent(vBox);
                dialog.showAndWait();
            } else {
                afiseazaEroare("Selectează un cont din listă!");
            }
        });


        btnLogout.setOnAction(e -> app.logout());

        VBox layout = new VBox(20, welcomeLabel,totalBalanceLabel, topActions, new Label("Conturile tale:"), accountsList, bottomActions, btnLogout);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 550, 600);
    }




    private static void actualizeazaConturi(ListView<String> listView, Customer customer) {
        listView.getItems().clear();
        for (Account acc : customer.getAccounts()) {
            String tip = (acc instanceof SavingsAccount) ? "[Economii]" : "[Curent]";
            listView.getItems().add(acc.getIban() + " - " + tip + " - Sold: " + acc.getBalance() + " RON");
        }
    }

    private static void actualizeazaSold(ListView<String> listView, Label totalLabel, Customer customer, BankingService service) {
        listView.getItems().clear();
        double soldTotal = 0;

        for (Account acc : customer.getAccounts()) {
            listView.getItems().add(acc.getIban() + " - Sold: " + acc.getBalance() + " RON");
            // Folosim funcția ta cerută
            soldTotal += service.getAccountBalance(acc.getIban());
        }

        totalLabel.setText("Sold Total (Toate conturile): " + String.format("%.2f", soldTotal) + " RON");
    }

    // Metodă utilitară pentru a genera un IBAN fictiv
    private static String generaIBAN() {
        Random r = new Random();
        int cifre = 100000 + r.nextInt(900000); // 6 cifre aleatorii
        return "RO" + cifre + "BANC" + (10 + r.nextInt(89));
    }

    private static void afiseazaEroare(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.show();
    }
}