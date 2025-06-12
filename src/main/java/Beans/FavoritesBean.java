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
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 *
 * @author Elias
 */
@Named(value = "favoritesBean")
@ViewScoped
public class FavoritesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FavoritoDao favoritoDao;

    @Inject
    private LoginBean loginBean;

    private List<FoodTruck> favoritos;

    private int foodtruckIdToDelete = -1;

    @PostConstruct
    public void init() {
        loadFavoritos();
    }

    public void loadFavoritos() {
        int userId = loginBean.getUsuario().getId();
        favoritos = favoritoDao.getFavoritosByUsuarioIdWithDetails(userId);
    }

    public List<FoodTruck> getFavoritos() {
        return favoritos;
    }

    public void prepareDelete(int foodtruckId) {
        this.foodtruckIdToDelete = foodtruckId;
    }

    public void confirmDelete() {
        if (foodtruckIdToDelete != -1) {
            int userId = loginBean.getUsuario().getId();
            boolean deleted = favoritoDao.deleteFavorito(userId, foodtruckIdToDelete);
            FacesContext context = FacesContext.getCurrentInstance();
            if (deleted) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Food truck eliminado de favoritos."));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el favorito."));
            }
            loadFavoritos();
            foodtruckIdToDelete = -1;
        }
    }
}
