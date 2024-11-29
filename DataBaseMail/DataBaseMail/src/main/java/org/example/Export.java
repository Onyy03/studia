package org.example;

import connection.Connect;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.sql.*;
import java.util.List;

public class Export {

    public void exportSelectedRowsToCSV(String schema, String tableName, List<ObservableList<Object>> selectedRows, List<String> selectedColumns, ObservableList<TableColumn<ObservableList<Object>, ?>> allColumns) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();

        if (connection != null) {
            try {
                // Natywne okno dialogowe do wyboru lokalizacji pliku
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
                    // Zapis nagłówków kolumn (bez "Zaznacz")
                    for (int i = 0; i < selectedColumns.size(); i++) {
                        if (!"Zaznacz".equals(selectedColumns.get(i))) { // Pomijamy kolumnę "Zaznacz"
                            writer.write(selectedColumns.get(i));
                            if (i < selectedColumns.size() - 1) {
                                writer.write(",");
                            }
                        }
                    }
                    writer.newLine();

                    // Zapis danych (bez "Zaznacz")
                    for (ObservableList<Object> row : selectedRows) {
                        for (int i = 0; i < selectedColumns.size(); i++) {
                            String columnName = selectedColumns.get(i);

                            if (!"Zaznacz".equals(columnName)) { // Pomijamy kolumnę "Zaznacz"
                                // Znajdź indeks kolumny na podstawie jej nazwy
                                int columnIndex = -1;
                                for (int j = 0; j < allColumns.size(); j++) {
                                    if (allColumns.get(j).getText().equals(columnName)) {
                                        columnIndex = j;
                                        break;
                                    }
                                }

                                if (columnIndex >= 0 && columnIndex < row.size()) {
                                    Object cellValue = row.get(columnIndex);
                                    writer.write(cellValue != null ? cellValue.toString() : "");
                                }

                                if (i < selectedColumns.size() - 1) {
                                    writer.write(",");
                                }
                            }
                        }
                        writer.newLine();
                    }

                    System.out.println("Eksport zakończony pomyślnie: " + fileToSave.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Błąd zapisu pliku.");
                }
            } finally {
                connect.close();
            }
        } else {
            System.err.println("Nie udało się połączyć z bazą danych.");
        }
    }
}
