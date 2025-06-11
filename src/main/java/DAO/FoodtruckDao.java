/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.FoodTruck;
import Models.MenuItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elias
 */
@Named
@ApplicationScoped
public class FoodtruckDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioDao.class);

    // Obtener todos los food trucks con promedio y cantidad de reviews
    public List<FoodTruck> getAllFoodTrucks() {
        String sql = "SELECT ft.*, IFNULL(AVG(r.rating), 0) AS avg_rating, COUNT(r.id) AS review_count "
                + "FROM foodtrucks ft "
                + "LEFT JOIN reviews r ON r.foodtruck_id = ft.id "
                + "GROUP BY ft.id "
                + "ORDER BY ft.nombre ASC";

        LOGGER.debug("SQL: {}", sql);

        List<FoodTruck> foodTrucks = new ArrayList<>();
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                FoodTruck ft = new FoodTruck();
                ft.setId(rs.getInt("id"));
                ft.setNombre(rs.getString("nombre"));
                ft.setDescripcion(rs.getString("descripcion"));
                ft.setUbicacion(rs.getString("ubicacion"));
                ft.setLat(rs.getDouble("lat"));
                ft.setLng(rs.getDouble("lng"));
                ft.setHorarioApertura(rs.getString("horario_apertura"));
                ft.setHorarioCierre(rs.getString("horario_cierre"));
                ft.setImagen(rs.getString("imagen"));
                ft.setAvg_rating(rs.getDouble("avg_rating"));
                ft.setReview_count(rs.getInt("review_count"));

                foodTrucks.add(ft);
            }
        } catch (SQLException e) {
            LOGGER.error("Error obteniendo todos los food trucks", e);
        }
        return foodTrucks;
    }

    // Obtener un food truck por ID con su menú
    public FoodTruck getFoodTruckById(int id) {
        String sqlFoodTruck = "SELECT * FROM foodtrucks WHERE id = ? LIMIT 1";
        String sqlMenu = "SELECT * FROM menus WHERE foodtruck_id = ? ORDER BY nombre ASC";

        LOGGER.debug("SQL: {}", sqlFoodTruck);
        LOGGER.debug("SQL: {}", sqlMenu);

        try (
                Connection conn = Conexion.getConexion(); PreparedStatement pstmtFoodTruck = conn.prepareStatement(sqlFoodTruck); PreparedStatement pstmtMenu = conn.prepareStatement(sqlMenu)) {

            pstmtFoodTruck.setInt(1, id);
            try (ResultSet rsFt = pstmtFoodTruck.executeQuery()) {
                if (!rsFt.next()) {
                    return null;
                }

                FoodTruck ft = new FoodTruck();
                ft.setId(rsFt.getInt("id"));
                ft.setNombre(rsFt.getString("nombre"));
                ft.setDescripcion(rsFt.getString("descripcion"));
                ft.setUbicacion(rsFt.getString("ubicacion"));
                ft.setLat(rsFt.getDouble("lat"));
                ft.setLng(rsFt.getDouble("lng"));
                ft.setHorarioApertura(rsFt.getString("horario_apertura"));
                ft.setHorarioCierre(rsFt.getString("horario_cierre"));
                ft.setImagen(rsFt.getString("imagen"));

                // Obtener menú
                pstmtMenu.setInt(1, id);
                try (ResultSet rsMenu = pstmtMenu.executeQuery()) {
                    List<MenuItem> menuList = new ArrayList<>();
                    while (rsMenu.next()) {
                        MenuItem menu = new MenuItem();
                        menu.setId(rsMenu.getInt("id"));
                        menu.setFoodtruckId(rsMenu.getInt("foodtruck_id"));
                        menu.setNombre(rsMenu.getString("nombre"));
                        menu.setDescripcion(rsMenu.getString("descripcion"));
                        menu.setPrecio(rsMenu.getDouble("precio"));
                        menu.setImagen(rsMenu.getString("imagen"));
                        menuList.add(menu);
                    }
                    ft.setMenu(menuList);
                }
                return ft;
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo food truck por ID: " + id, e);
            return null;
        }
    }

    // Crear food truck
    public int createFoodTruck(FoodTruck ft) {
        String sql = "INSERT INTO foodtrucks (nombre, descripcion, ubicacion, lat, lng, horario_apertura, horario_cierre, imagen) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        LOGGER.debug("SQL: {}", sql);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ft.getNombre());
            pstmt.setString(2, ft.getDescripcion());
            pstmt.setString(3, ft.getUbicacion());
            pstmt.setDouble(4, ft.getLat());
            pstmt.setDouble(5, ft.getLng());
            pstmt.setString(6, ft.getHorarioApertura());
            pstmt.setString(7, ft.getHorarioCierre());
            pstmt.setString(8, ft.getImagen());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error creando food truck", e);
            return -1;
        }
    }

    // Actualizar food truck
    public boolean updateFoodTruck(FoodTruck ft) {
        String sql = "UPDATE foodtrucks SET nombre = ?, descripcion = ?, ubicacion = ?, lat = ?, lng = ?, "
                + "horario_apertura = ?, horario_cierre = ?, imagen = ? WHERE id = ?";
        LOGGER.debug("SQL: {}", sql);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ft.getNombre());
            pstmt.setString(2, ft.getDescripcion());
            pstmt.setString(3, ft.getUbicacion());
            pstmt.setDouble(4, ft.getLat());
            pstmt.setDouble(5, ft.getLng());
            pstmt.setString(6, ft.getHorarioApertura());
            pstmt.setString(7, ft.getHorarioCierre());
            pstmt.setString(8, ft.getImagen());
            pstmt.setInt(9, ft.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error("Error actualizando food truck con id: " + ft.getId(), e);
            return false;
        }
    }

    // Eliminar food truck
    public boolean deleteFoodTruck(int id) {
        String sql = "DELETE FROM foodtrucks WHERE id = ?";
        LOGGER.debug("SQL: {}", sql);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error("Error eliminando food truck con id: " + id, e);
            return false;
        }
    }

}
