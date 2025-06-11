/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import DAO.FavoritoDao;
import Models.FoodTruck;
import jakarta.inject.Named;
import java.io.Serializable;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import java.util.List;

/**
 *
 * @author Elias
 */
@Named(value = "favoritesBean")
@RequestScoped
public class FavoritesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<FoodTruck> favoritos;
    private int foodtruckIdToDelete;
    private LoginBean loginBean;

    @Inject
    private FavoritoDao favoritoDao; // Asegúrate de tener esto configurado

    @PostConstruct
    public void init() {
        // Cargar los favoritos del usuario
        int userId = getCurrentUserId();
        favoritos = favoritoDao.getFavoritosByUsuarioIdWithDetails(userId);
    }

    public List<FoodTruck> getFavoritos() {
        return favoritos;
    }

    public void deleteFavorito(int foodtruckId) {
        this.foodtruckIdToDelete = foodtruckId;
    }

    public void confirmDelete() {
        // Eliminar favorito
        favoritoDao.deleteFavorito(getCurrentUserId(), foodtruckIdToDelete);
        // Recargar favoritos
        init();
    }

    private int getCurrentUserId() {
        int usuarioId = loginBean.getUsuario().getId();
        // Implementa la lógica para obtener el ID del usuario actual
        return usuarioId; // Placeholder
    }

    public void redirectToBooking(int foodtruckId) {
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler nav = context.getApplication().getNavigationHandler();
        nav.handleNavigation(context, null, "booking?foodtruck=" + foodtruckId);
    }

}
