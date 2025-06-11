/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodtruckmanager.proyectfoodtruckmanager.util;

import DAO.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;



/**
 *
 * @author Elias
 */
public class SQLiteConnector_testing {
      public static void printAllTablesData() {
                System.out.println("Archivo de base de datos utilizado: " + Conexion.getDB_PATH()); // <-- Aquí mostramos la ruta
        try (Connection conn = Conexion.getConexion();
             Statement stmt = conn.createStatement()) {

            // Obtener todas las tablas del usuario (excluyendo tablas internas sqlite_)
            ResultSet rsTables = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'");

            while (rsTables.next()) {
                String tableName = rsTables.getString("name");
                printTableData(conn, tableName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.cerrarConexion();
        }
    }

    private static void printTableData(Connection conn, String tableName) throws SQLException {
        System.out.println("\n--- Datos de la tabla: " + tableName + " ---");

        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Imprimir nombres de columnas
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            // Imprimir filas
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    System.out.print(value + "\t");
                }
                System.out.println();
            }

            if (!hasRows) {
                System.out.println("[No hay registros]");
            }
        }
    }

    // Método main para prueba rápida
    public static void main(String[] args) {
        printAllTablesData();
    }
}
