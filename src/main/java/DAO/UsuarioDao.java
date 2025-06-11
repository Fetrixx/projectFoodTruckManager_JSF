/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Usuario;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elias
 */
public class UsuarioDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioDao.class);

    public Usuario validarUsuario(String email, String password) {
        String sql = "SELECT id, nombre, email, admin FROM usuarios WHERE email = ? AND password = ?";
        LOGGER.debug("Validando usuario: email={}", email);
        LOGGER.debug("SQL: {}", sql);

        // try (Connection conn = Conexion.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        Connection conn = Conexion.getConexion();
        if (conn == null) {
            LOGGER.error("Conexión es null, no se puede ejecutar la consulta");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setAdmin(rs.getBoolean("admin"));

                    LOGGER.info("Usuario autenticado: {}", usuario.getEmail());
                    return usuario;
                } else {
                    LOGGER.warn("Credenciales inválidas para: {}", email);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error en la consulta SQL", ex);
        }
        return null;
    }

    // Método para obtener usuario por nombre y email (similar a getUserByUsernameAndEmail)
    public Usuario getUserByNombreAndEmail(String nombre, String email) {
        Connection conn = Conexion.getConexion();
        if (conn == null) {
            LOGGER.error("No se pudo obtener conexión a la base de datos");
            return null;
        }

        String sql = "SELECT id, nombre, email, admin FROM usuarios WHERE nombre = ? AND email = ? LIMIT 1";
        LOGGER.debug("SQL: {}", sql);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setAdmin(rs.getBoolean("admin"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error en la consulta SQL", e);
        }
        return null;
    }

}
