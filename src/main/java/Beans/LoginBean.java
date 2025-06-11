/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import DAO.UsuarioDao;
import Models.Usuario;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elias
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginBean.class);

    private String email;
    private String password;
    private Usuario usuario;
    private boolean admin;

    public String login() {
        LOGGER.info("Intentando login para: {}", email);

        UsuarioDao dao = new UsuarioDao();
        usuario = dao.validarUsuario(email, password);

        if (usuario != null) {
            LOGGER.info("Login exitoso para: {}", email);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("username", usuario.getNombre());

            // Determinar si es administrador
            admin = usuario.isAdmin();
            context.getExternalContext().getSessionMap().put("admin", admin);

            return "/views/index?faces-redirect=true";
        } else {
            LOGGER.info("Login fallido para: {}", email);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales inválidas"));
            return null;
        }
    }

    public void logout() {
        // Invalidar sesión
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // Redirigir a login
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(FacesContext.getCurrentInstance().getExternalContext()
                            .getRequestContextPath() + "/views/login.xhtml");
        } catch (IOException e) {
            LOGGER.info("Error al redirigir después de logout", e);
        }
//        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
//        return "/views/login?faces-redirect=true";
    }

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String navigateTo(String page) {
        return page + "?faces-redirect=true";
    }

}
