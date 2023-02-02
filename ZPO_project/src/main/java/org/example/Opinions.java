package org.example;

import java.sql.*;

public class Opinions {
    /**
     * Klasa reprezentująca opinie.
     */
    private Connection connection;

    public Opinions() throws SQLException, ClassNotFoundException {
        this.connection = Connect.makeConnection();
    }

    /**
     * Metoda dodająca opinię o produkcie.
     *
     * @param clientId  - ID klienta
     * @param productId - nazwa produktu
     * @param rating    - ocena
     * @param text      - treść opinii
     */
    public void addOpinion(int clientId, String productId, String text, double rating) throws ClassNotFoundException, SQLException {
        Statement stmt = connection.createStatement();
        InProductStats stats = new InProductStats();

        stats.addToProductStats(productId);

        PreparedStatement selectAllSt1 = connection.prepareStatement("select id, rating from product_stats where product_id='" + productId + "';");
        ResultSet rs1 = selectAllSt1.executeQuery();
        if (rs1.next()) {
            int productStatsId = rs1.getInt(1);
            double productStatsRating = rs1.getInt(2);

            String sql1 = "INSERT INTO product_opinions(product_stat_id, client_id, description) values('" + productStatsId + "','" + clientId + "','" + text + "');";
            stmt.executeUpdate(sql1);

            if (productStatsRating == 0) {
                double finalRating = rating;
                String sql9 = "update product_stats set rating=" + finalRating + " where id='" + productStatsId + "';";
                stmt.executeUpdate(sql9);
            } else if (productStatsRating != 0) {
                double finalRating = (productStatsRating + rating) / 2;
                String sql9 = "update product_stats set rating=" + finalRating + " where id='" + productStatsId + "';";
                stmt.executeUpdate(sql9);
            }
        }
        System.out.println("\nPomyślnie dodano opinię.");
    }

    /**
     * Metoda drukująca opinie o produkcie.
     *
     * @param productId - nazwa produktu
     */
    public void showOpinions(String productId) throws SQLException {
        PreparedStatement selectAllSt1 = connection.prepareStatement("select rating, id from product_stats where product_id='" + productId + "' limit 1;");
        ResultSet rs1 = selectAllSt1.executeQuery();
        if (!rs1.next()) {
            System.out.println("\nBrak opinii o danym produkcie.");
        } else {
            double rating = rs1.getDouble(1);
            String productStatID = rs1.getString(2);
            System.out.println("\nOcena (skala 1 - 5): " + rating);

            System.out.println("\nOpinie o produkcie:");
            PreparedStatement selectAllSt = connection.prepareStatement("select description from product_opinions where product_stat_id='" + productStatID + "';");
            ResultSet rs = selectAllSt.executeQuery();
            printResultSetEnumerate(rs);
        }
    }

    /**
     * Metoda drukująca ponumerowane dane wynikowe z bazy danych.
     *
     * @param resultSet - zapytanie do bazy danych
     */
    public static void printResultSetEnumerate(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        int counter = 1;
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                System.out.print("Opinia " + counter + ": " + columnValue);
                counter += 1;
            }
            System.out.println("");
        }
    }
}