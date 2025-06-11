/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Reserva;
import Models.ReservaItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elias
 */
public class ReservaDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservaDao.class);

    public int createReserva(int usuarioId, int foodtruckId, String fecha, String hora, double total) {
        String sql = "INSERT INTO reservas (usuario_id, foodtruck_id, fecha, hora, total) VALUES (?, ?, ?, ?, ?)";
        int reservaId = -1;

        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, foodtruckId);
            pstmt.setString(3, fecha);
            pstmt.setString(4, hora);
            pstmt.setDouble(5, total);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reservaId = rs.getInt(1);
                        LOGGER.info("Reserva creada con ID: {}", reservaId);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al crear reserva", ex);
        }
        return reservaId;
    }

    public List<Reserva> getReservasByUsuarioId(int usuarioId, boolean showAll) {
        List<Reserva> reservas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.nombre AS usuario_nombre "
                + "FROM reservas r "
                + "JOIN usuarios u ON r.usuario_id = u.id "
        );
        if (!showAll) {
            sql.append("WHERE r.usuario_id = ? ");
        }
        sql.append("ORDER BY r.fecha_creacion DESC");

        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            if (!showAll) {
                pstmt.setInt(1, usuarioId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    reserva.setUsuarioId(rs.getInt("usuario_id"));
                    reserva.setFoodtruckId(rs.getInt("foodtruck_id"));
                    reserva.setFecha(rs.getString("fecha"));
                    reserva.setHora(rs.getString("hora"));
                    reserva.setTotal(rs.getDouble("total"));
                    reserva.setEstado(rs.getString("estado"));
                    reserva.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                    reserva.setUsuarioNombre(rs.getString("usuario_nombre"));

                    reservas.add(reserva);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener reservas por usuario", ex);
        }
        return reservas;
    }

    public List<ReservaItem> getItemsByReservaId(int reservaId) {
        List<ReservaItem> items = new ArrayList<>();
        String sql = "SELECT ri.*, m.nombre AS menu_nombre "
                + "FROM reserva_items ri "
                + "JOIN menus m ON ri.menu_id = m.id "
                + "WHERE ri.reserva_id = ?";

        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReservaItem item = new ReservaItem();
                    item.setId(rs.getInt("id"));
                    item.setReservaId(rs.getInt("reserva_id"));
                    item.setMenuId(rs.getInt("menu_id"));
                    item.setCantidad(rs.getInt("cantidad"));
                    item.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    item.setMenuNombre(rs.getString("menu_nombre"));

                    items.add(item);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener items de reserva", ex);
        }
        return items;
    }

    public boolean createReservaItem(int reservaId, int menuId, int cantidad, double precio) {
        String sql = "INSERT INTO reserva_items (reserva_id, menu_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservaId);
            pstmt.setInt(2, menuId);
            pstmt.setInt(3, cantidad);
            pstmt.setDouble(4, precio);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.error("Error al crear item de reserva", ex);
            return false;
        }
    }

    public Reserva getReservaById(int reservaId) {
        String sql = "SELECT * FROM reservas WHERE id = ?";
        Reserva reserva = null;

        LOGGER.debug("SQL: {}", sql);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    reserva.setUsuarioId(rs.getInt("usuario_id"));
                    reserva.setFoodtruckId(rs.getInt("foodtruck_id"));
                    reserva.setFecha(rs.getString("fecha"));
                    reserva.setHora(rs.getString("hora"));
                    reserva.setTotal(rs.getDouble("total"));
                    reserva.setEstado(rs.getString("estado"));
                    reserva.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener reserva por ID", ex);
        }
        return reserva;
    }

    public boolean deleteReserva(int reservaId) {
        String sql = "DELETE FROM reservas WHERE id = ?";

        LOGGER.debug("SQL: {}", sql);

        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservaId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.error("Error al eliminar reserva", ex);
            return false;
        }
    }

    public boolean updateReservaEstado(int reservaId, String estado) {
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";

        LOGGER.debug("SQL: {}", sql);
        try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            pstmt.setInt(2, reservaId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.error("Error al actualizar estado de reserva", ex);
            return false;
        }
    }
}
