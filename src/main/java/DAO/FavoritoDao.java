/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.FoodTruck;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elias
 */
public class FavoritoDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(FavoritoDao.class);

    public boolean addFavorito(int usuarioId, int foodtruckId) {
        String sql = "INSERT INTO favoritos (usuario_id, foodtruck_id) VALUES (?, ?)";
        LOGGER.debug("Agregando favorito: usuario_id={}, foodtruck_id={}", usuarioId, foodtruckId);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, foodtruckId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            LOGGER.error("Error al agregar favorito", ex);
            return false;
        }
    }

    public List<Integer> getFavoritosByUsuarioId(int usuarioId) {
        List<Integer> favoritos = new ArrayList<>();
        String sql = "SELECT foodtruck_id FROM favoritos WHERE usuario_id = ?";
        LOGGER.debug("Obteniendo favoritos para usuario_id={}", usuarioId);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    favoritos.add(rs.getInt("foodtruck_id"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener favoritos", ex);
        }
        return favoritos;
    }

    public boolean deleteFavorito(int usuarioId, int foodtruckId) {
        String sql = "DELETE FROM favoritos WHERE usuario_id = ? AND foodtruck_id = ?";
        LOGGER.debug("Eliminando favorito: usuario_id={}, foodtruck_id={}", usuarioId, foodtruckId);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, foodtruckId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error("Error al eliminar favorito", ex);
            return false;
        }
    }

    public boolean isFavorito(int usuarioId, int foodtruckId) {
        String sql = "SELECT 1 FROM favoritos WHERE usuario_id = ? AND foodtruck_id = ? LIMIT 1";
        LOGGER.debug("Verificando si es favorito: usuario_id={}, foodtruck_id={}", usuarioId, foodtruckId);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, foodtruckId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al verificar favorito", ex);
            return false;
        }
    }

    public List<FoodTruck> getFavoritosByUsuarioIdWithDetails(int usuarioId) {
        List<FoodTruck> favoritos = new ArrayList<>();
        String sql = "SELECT ft.* FROM favoritos f JOIN foodtrucks ft ON f.foodtruck_id = ft.id WHERE f.usuario_id = ?";

        LOGGER.info("Obteniendo favoritos completos para usuario_id={}", usuarioId);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
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
                    favoritos.add(ft);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener favoritos completos", ex);
        }
        return favoritos;
    }

}
