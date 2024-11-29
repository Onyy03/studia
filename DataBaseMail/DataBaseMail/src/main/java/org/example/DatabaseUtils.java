package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    private String driver;
    private String url;
    private String user;
    private String password;

    public DatabaseUtils(String configFilePath) {
        loadConfigFromFile(configFilePath);
    }

    private void loadConfigFromFile(String configFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split("=");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                switch (key) {
                    case "driver":
                        driver = value;
                        break;
                    case "url":
                        url = value;
                        break;
                    case "user":
                        user = value;
                        break;
                    case "password":
                        password = value;
                        break;
                    default:
                        System.err.println("Nieznana opcja w pliku konfiguracyjnym: " + key);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku konfiguracyjnego: " + e.getMessage());
        }
    }

    private Connection getConnection() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Błąd podczas nawiązywania połączenia: " + e.getMessage());
            return null;
        }
    }

    public String[] getAllTables() {
        List<String> tables = new ArrayList<>();
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tables.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania tabel: " + e.getMessage());
        }
        return tables.toArray(new String[0]);
    }

    public String[][] getTableData(String tableName) {
        List<String[]> rows = new ArrayList<>();
        String query = "SELECT * FROM \"" + tableName + "\"";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            while (rs.next()) {
                String[] row = new String[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; i++) {
                    row[i - 1] = rs.getString(i);
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania danych z tabeli: " + e.getMessage());
        }

        return rows.toArray(new String[0][0]);
    }
}
