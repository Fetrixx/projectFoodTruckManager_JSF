/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.MenuItem;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class MenuDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuDao.class);

    public List<MenuItem> getMenuByFoodTruckId(int foodtruckId) {
        List<MenuItem> menus = new ArrayList<>();
        String sql = "SELECT * FROM menus WHERE foodtruck_id = ?";
        LOGGER.debug("Obteniendo menú para foodtruck_id={}", foodtruckId);

        LOGGER.debug("SQL: {}", sql);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodtruckId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem menuItem = new MenuItem();
                    menuItem.setId(rs.getInt("id"));
                    menuItem.setFoodtruckId(rs.getInt("foodtruck_id"));
                    menuItem.setNombre(rs.getString("nombre"));
                    menuItem.setDescripcion(rs.getString("descripcion"));
                    menuItem.setPrecio(rs.getDouble("precio"));
                    menuItem.setImagen(rs.getString("imagen"));
                    menus.add(menuItem);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener el menú", ex);
        }
        return menus;
    }

    public int createMenuItem(int foodtruckId, String nombre, String descripcion, double precio, String imagen) {
        String sql = "INSERT INTO menus (foodtruck_id, nombre, descripcion, precio, imagen) VALUES (?, ?, ?, ?, ?)";
        LOGGER.debug("Creando nuevo elemento de menú para foodtruck_id={}", foodtruckId);
        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, foodtruckId);
            pstmt.setString(2, nombre);
            pstmt.setString(3, descripcion);
            pstmt.setDouble(4, precio);
            pstmt.setString(5, imagen);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al crear el elemento de menú", ex);
        }
        return -1;
    }

    public boolean updateMenuItem(int id, String nombre, String descripcion, double precio, String imagen) {
        String sql = "UPDATE menus SET nombre = ?, descripcion = ?, precio = ?, imagen = ? WHERE id = ?";
        LOGGER.debug("Actualizando elemento de menú id={}", id);
        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, descripcion);
            pstmt.setDouble(3, precio);
            pstmt.setString(4, imagen);
            pstmt.setInt(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error("Error al actualizar el elemento de menú", ex);
        }
        return false;
    }

    public boolean deleteMenuItem(int id) {
        String sql = "DELETE FROM menus WHERE id = ?";
        LOGGER.debug("Eliminando elemento de menú id={}", id);
        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error("Error al eliminar el elemento de menú", ex);
        }
        return false;
    }

    public MenuItem getMenuItemById(int id) {
        String sql = "SELECT * FROM menus WHERE id = ? LIMIT 1";
        LOGGER.debug("Obteniendo elemento de menú id={}", id);
        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MenuItem menuItem = new MenuItem();
                    menuItem.setId(rs.getInt("id"));
                    menuItem.setFoodtruckId(rs.getInt("foodtruck_id"));
                    menuItem.setNombre(rs.getString("nombre"));
                    menuItem.setDescripcion(rs.getString("descripcion"));
                    menuItem.setPrecio(rs.getDouble("precio"));
                    menuItem.setImagen(rs.getString("imagen"));
                    return menuItem;
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener el elemento de menú", ex);
        }
        return null;
    }
}
