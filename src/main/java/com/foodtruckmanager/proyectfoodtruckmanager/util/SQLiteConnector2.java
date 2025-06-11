package com.foodtruckmanager.proyectfoodtruckmanager.util;

import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class SQLiteConnector2 {

    public static Connection getConnection() throws Exception {
        // Leer el archivo .db como recurso desde el classpath
        InputStream input = SQLiteConnector2.class.getClassLoader().getResourceAsStream("database/foodtruck_manager.db");

        if (input == null) {
            throw new FileNotFoundException("No se encontró el archivo: database/foodtruck_manager.db");
        }

        // Crear archivo temporal
        File tempDbFile = File.createTempFile("foodtruck_manager", ".db");
        tempDbFile.deleteOnExit();

        // Copiar contenido del recurso al archivo temporal
        Files.copy(input, tempDbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Crear conexión SQLite a ese archivo
        String url = "jdbc:sqlite:" + tempDbFile.getAbsolutePath();
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    // Método de prueba para consola
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Conexión exitosa ✅");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            while (rs.next()) {
                System.out.println("Tabla: " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
