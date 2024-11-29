package org.example;

import connection.Connect;
import javafx.collections.ObservableList;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.sql.*;
import java.util.List;

public class Export {

    public void exportSelectedRowsToCSV(String schema, String tableName, List<ObservableList<Object>> selectedRows) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();

        if (connection != null) {
            try {
                String query = "SELECT * FROM \"" + schema + "\".\"" + tableName + "\"";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData metaData = rs.getMetaData();

                // Użycie natywnego okna dialogowego
                FileDialog fileDialog = new FileDialog((Frame) null, "Wybierz miejsce do zapisania pliku", FileDialog.SAVE);
                fileDialog.setFile(tableName + ".csv");
                fileDialog.setVisible(true);

                String filePath = fileDialog.getDirectory() + fileDialog.getFile();

                if (fileDialog.getFile() == null) {
                    System.out.println("Anulowano zapis pliku.");
                    return; // Użytkownik anulował zapis
                }

                File fileToSave = new File(filePath);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                    int columnCount = metaData.getColumnCount();

                    // Zapisz nagłówki kolumn
                    for (int i = 1; i <= columnCount; i++) {
                        writer.write(metaData.getColumnName(i));
                        if (i < columnCount) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();

                    // Zapisz tylko zaznaczone wiersze
                    for (ObservableList<Object> row : selectedRows) {
                        for (int i = 0; i < columnCount; i++) {
                            Object cellValue = row.get(i);
                            writer.write(cellValue != null ? cellValue.toString() : "");
                            if (i < columnCount - 1) {
                                writer.write(",");
                            }
                        }
                        writer.newLine();
                    }

                    System.out.println("Eksport zakończony pomyślnie: " + fileToSave.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Błąd zapisu pliku.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Błąd zapytania do bazy danych.");
            } finally {
                connect.close();
            }
        } else {
            System.err.println("Nie udało się połączyć z bazą danych.");
        }
    }
}
