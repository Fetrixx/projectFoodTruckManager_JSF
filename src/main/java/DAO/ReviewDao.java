/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Review;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
@Named
@ApplicationScoped
public class ReviewDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioDao.class);

    /**
     * Crea una reseña nueva.
     */
    public boolean createReview(int usuarioId, int foodtruckId, int rating, String comentario) {
        String sql = "INSERT INTO reviews (usuario_id, foodtruck_id, rating, comentario) VALUES (?, ?, ?, ?)";

        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, foodtruckId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comentario);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error("Error creando review", e);
            return false;
        }
    }

    /**
     * Obtiene todas las reseñas, incluyendo el nombre del usuario y nombre del
     * food truck.
     */
    public List<Review> getAllReviews() {
        String sql = "SELECT r.*, u.nombre AS user_name, f.nombre AS foodtruck_nombre "
                + "FROM reviews r "
                + "JOIN usuarios u ON r.usuario_id = u.id "
                + "JOIN foodtrucks f ON r.foodtruck_id = f.id "
                + "ORDER BY r.fecha DESC";

        List<Review> reviews = new ArrayList<>();

        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return reviews;
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo todas las reviews", e);
        }

        return reviews;
    }

    /**
     * Obtiene todas las reseñas para un food truck específico, incluyendo el
     * nombre del usuario y nombre del food truck.
     */
    public List<Review> getReviewsByFoodTruckId(int foodtruckId) {
        String sql = "SELECT r.*, u.nombre AS user_name, f.nombre AS foodtruck_nombre "
                + "FROM reviews r "
                + "JOIN usuarios u ON r.usuario_id = u.id "
                + "JOIN foodtrucks f ON r.foodtruck_id = f.id "
                + "WHERE r.foodtruck_id = ? "
                + "ORDER BY r.fecha DESC";

        List<Review> reviews = new ArrayList<>();

        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return reviews;
            }

            pstmt.setInt(1, foodtruckId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo reviews por foodtruckId: " + foodtruckId, e);
        }
        return reviews;
    }

    /**
     * Obtiene reseñas para varios food trucks (lista de IDs), incluyendo nombre
     * usuario y nombre food truck.
     */
    public List<Review> getReviewsByFoodtruckIds(List<Integer> foodtruckIds) {
        List<Review> reviews = new ArrayList<>();
        if (foodtruckIds == null || foodtruckIds.isEmpty()) {
            return reviews;
        }

        String placeholders = String.join(",", foodtruckIds.stream().map(id -> "?").toArray(String[]::new));
        String sql = "SELECT r.*, u.nombre AS user_name, f.nombre AS foodtruck_nombre "
                + "FROM reviews r "
                + "JOIN usuarios u ON r.usuario_id = u.id "
                + "JOIN foodtrucks f ON r.foodtruck_id = f.id "
                + "WHERE r.foodtruck_id IN (" + placeholders + ") "
                + "ORDER BY r.fecha DESC";
        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return reviews;
            }

            for (int i = 0; i < foodtruckIds.size(); i++) {
                pstmt.setInt(i + 1, foodtruckIds.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo reviews por foodtruckIds", e);
        }
        return reviews;
    }

    /**
     * Actualiza una reseña existente (solo si pertenece al usuario).
     */
    public boolean updateReview(int reviewId, int usuarioId, int rating, String comentario) {
        String sql = "UPDATE reviews SET rating = ?, comentario = ? WHERE id = ? AND usuario_id = ?";
        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return false;
            }

            pstmt.setInt(1, rating);
            pstmt.setString(2, comentario);
            pstmt.setInt(3, reviewId);
            pstmt.setInt(4, usuarioId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.error("Error actualizando review id: " + reviewId, e);
            return false;
        }
    }

    /**
     * Elimina una reseña (solo si pertenece al usuario).
     */
    public boolean deleteReview(int reviewId, int usuarioId) {
        String sql = "DELETE FROM reviews WHERE id = ? AND usuario_id = ?";
        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return false;
            }

            pstmt.setInt(1, reviewId);
            pstmt.setInt(2, usuarioId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.error("Error eliminando review id: " + reviewId, e);
            return false;
        }
    }

    /**
     * Obtiene una reseña por su ID (para edición).
     */
    public Review getReviewById(int reviewId, int usuarioId) {
        String sql = "SELECT * FROM reviews WHERE id = ? AND usuario_id = ?";
        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return null;
            }

            pstmt.setInt(1, reviewId);
            pstmt.setInt(2, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo review id: " + reviewId, e);
        }
        return null;
    }

    /**
     * Obtiene todas las reseñas de un usuario.
     */
    public List<Review> getReviewsByUserId(int usuarioId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE usuario_id = ?";
        LOGGER.info("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener la conexión a la base de datos");
                return reviews;
            }

            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapResultSetToReview(rs);
                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo reviews por usuarioId: " + usuarioId, e);
        }
        return reviews;
    }

    /**
     * Mapea un ResultSet a un objeto Review.
     */
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setUsuarioId(rs.getInt("usuario_id"));
        review.setFoodtruckId(rs.getInt("foodtruck_id"));
        review.setRating(rs.getInt("rating"));
        review.setComentario(rs.getString("comentario"));
        review.setFecha(rs.getTimestamp("fecha"));

        if (columnExists(rs, "user_name")) {
            review.setUserName(rs.getString("user_name"));
        }

        if (columnExists(rs, "foodtruck_nombre")) {
            review.setFoodtruckNombre(rs.getString("foodtruck_nombre"));
        }

        return review;
    }

    private boolean columnExists(ResultSet rs, String columnLabel) {
        try {
            rs.findColumn(columnLabel);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtiene la calificación promedio para un food truck dado. Retorna 0 si no
     * hay reseñas.
     */
    public double obtenerCalificacionPromedio(int foodtruckId) {
        String sql = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE foodtruck_id = ?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener conexión a BD");
                return 0.0;
            }

            pstmt.setInt(1, foodtruckId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error obteniendo calificación promedio para foodtruckId: " + foodtruckId, e);
        }
        return 0.0;
    }

    /**
     * Cuenta el número de reseñas para un food truck dado.
     */
    public int contarReseñasPorFoodTruck(int foodtruckId) {
        String sql = "SELECT COUNT(*) AS review_count FROM reviews WHERE foodtruck_id = ?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                LOGGER.error("No se pudo obtener conexión a BD");
                return 0;
            }

            pstmt.setInt(1, foodtruckId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("review_count");
                }
            }

        } catch (SQLException e) {
            LOGGER.error("Error contando reseñas para foodtruckId: " + foodtruckId, e);
        }
        return 0;
    }

}
