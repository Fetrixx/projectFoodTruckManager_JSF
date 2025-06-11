package com.foodtruckmanager.proyectfoodtruckmanager.util;

import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class SQLiteConnector {

    private static final String DB_RESOURCE_PATH = "/database/foodtruck_manager.db"; // ruta en resources
    private static final String DB_OUTPUT_PATH = "data/foodtruck_manager.db"; // carpeta donde querés guardar la base

    public static Connection getConnection() throws Exception {
        Path dbPath = Paths.get(DB_OUTPUT_PATH);

        // Si el archivo no existe, copiar desde recurso
        if (!Files.exists(dbPath)) {
            System.out.println("Archivo DB no encontrado, copiando desde recursos...");
            try (InputStream is = SQLiteConnector.class.getResourceAsStream(DB_RESOURCE_PATH)) {
                if (is == null) {
                    throw new FileNotFoundException("Recurso no encontrado: " + DB_RESOURCE_PATH);
                }
                Files.createDirectories(dbPath.getParent());
                Files.copy(is, dbPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        String url = "jdbc:sqlite:" + dbPath.toAbsolutePath().toString();
        return DriverManager.getConnection(url);
    }
}
