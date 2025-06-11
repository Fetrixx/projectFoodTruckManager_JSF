package DAO;

//import java.util.logging.Logger;
import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;  
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.stream.Collectors;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Elias
 */
public class Conexion {
  private static final Logger LOGGER = LoggerFactory.getLogger(Conexion.class);
    private static final String DB_DIR = "C:/FoodTruckManagerDB";
    private static final String DB_PATH = DB_DIR + "/foodtruck_manager.db";

    public static String getDB_PATH() {
        return DB_PATH;
    }
    
    // Usamos ThreadLocal para conexiones seguras en multi-hilo
    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            try {
                createDatabaseDirectory();
                copyDatabaseFile();
                executeSQLScript();
                LOGGER.info("Base de datos inicializada y script SQL ejecutado");
            } catch (IOException | SQLException e) {
                LOGGER.error("Error crítico al inicializar la base de datos", e);
                throw new RuntimeException("Error en inicialización de BD", e);
            }
        }
    }

    private static void createDatabaseDirectory() throws IOException {
        Path dirPath = Paths.get(DB_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            LOGGER.info("Directorio creado: {}", DB_DIR);
        }
    }

    private static void copyDatabaseFile() throws IOException {
        try (InputStream is = getResourceAsStream("database/foodtruck_manager.db")) {
            if (is == null) {
                throw new FileNotFoundException("Archivo de base de datos no encontrado en recursos");
            }
            Files.copy(is, Paths.get(DB_PATH), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Base de datos copiada a: {}", DB_PATH);
        }
    }

    private static void executeSQLScript() throws IOException, SQLException {
        try (InputStream is = getResourceAsStream("database/create_script.sql");
             Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             Statement stmt = conn.createStatement()) {
            
            if (is == null) {
                throw new FileNotFoundException("Script SQL no encontrado en recursos");
            }
            
            String script = new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining("\n"));
            
            for (String sql : script.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.executeUpdate(sql);
                }
            }
            LOGGER.info("Script SQL ejecutado exitosamente");
        }
    }

    private static InputStream getResourceAsStream(String resourcePath) {
        return Conexion.class.getClassLoader().getResourceAsStream(resourcePath);
    }

    public static Connection getConexion() {
        Connection conn = threadLocalConnection.get();
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
                threadLocalConnection.set(conn);
                LOGGER.debug("Nueva conexión creada para el hilo actual");
            }
            return conn;
        } catch (SQLException e) {
            LOGGER.error("Error al obtener conexión", e);
            throw new RuntimeException("Error de conexión a BD", e);
        }
    }

    public static void cerrarConexion() {
        Connection conn = threadLocalConnection.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    LOGGER.debug("Conexión cerrada para el hilo actual");
                }
            } catch (SQLException e) {
                LOGGER.error("Error al cerrar conexión", e);
            } finally {
                threadLocalConnection.remove();
            }
        }
    }
}
