package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;

public class Seller {
    private DatabaseContext onlineShop;
    private Connection connection;
    private Menu menu;

    public Seller(DatabaseContext onlineShop, Connection connection) throws SQLException, ClassNotFoundException {
        this.onlineShop = onlineShop;
        this.connection = connection;
        this.menu = new Menu();
    }

    public void showAndEditProducts(int id) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        onlineShop.printResultSet(onlineShop.getSellerProducts(id), "\nLista twoich produktów:");

        String action = menu.getInput("Co chcesz zrobić? \n1 - edytować produkt \n2 - wrócić");
        while (!"1".equals(action) && !"2".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("\nPodaj numer");
        }
        switch (action) {
            case "1":
                sellerEditProduct();
                menu.startMenu();
                break;
            case "2":
                menu.startMenu();
                break;
        }
    }

    public void sellerEditProduct() throws SQLException {
        String productName = menu.getInput("Który produkt chcesz edytować?");
        String productId = onlineShop.getProductId(productName);
        while (productId == null) {
            System.out.println("Niepoprawna nazwa produktu. Spróbuj ponownie.");
            productName = menu.getInput("Wybierz produkt");
            productId = onlineShop.getProductId(productName);
        }

        String[] productInfo = onlineShop.getResultAsTable(onlineShop.getProductInfo(productId));

        System.out.println("\nWprowadź dane, które chcesz edytować:");
        String[] column = {"nazwie produktu", "producencie", "opisie", "cenie (w zł)", "sprzedającym", "dostępności (w sztukach)"};
        String[] input = new String[productInfo.length];

        for (int i = 0; i < productInfo.length; i++) {
            if (i == 4) {
                continue;
            }
            System.out.println("Aktualne informacje o " + column[i] + ": " + productInfo[i]);
            input[i] = menu.getInput("Nowe informacje:");
            if ("".equals(input[i])) {
                input[i] = productInfo[i];
            }
        }
        onlineShop.editProduct(productId, input);
    }

    public void sellerAddProducts(int userId) throws SQLException, ClassNotFoundException {
        InProductStats inProductStats = new InProductStats(connection);
        String id = menu.getInput("Podaj id produktu");
        String category = menu.getInput("Podaj id kategorii");
        String name = menu.getInput("Podaj nazwę produktu");
        String producer = menu.getInput("Podaj producenta");
        String description = menu.getInput("Podaj opis produktu");
        double price = -1;
        while (price < 0) {
            try {
                price = Double.valueOf(menu.getInput("Podaj cenę (w zł)"));
            } catch (IllegalArgumentException e) {
                System.out.println("Błąd. Podaj liczbę.");
            }
        }
        int availability = -1;
        while (availability < 0) {
            try {
                availability = Integer.valueOf(menu.getInput("Podaj dostępną liczbę sztuk"));
            } catch (IllegalArgumentException e) {
                System.out.println("Błąd. Podaj liczbę.");
            }
        }
        onlineShop.addProduct(id, category, name, producer, description, price, userId, availability);

        inProductStats.addToProductStats(id);
    }
}
