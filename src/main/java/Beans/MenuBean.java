/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import DAO.FoodtruckDao;
import Models.FoodTruck;
import Models.MenuItem;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Elias
 */
@Named(value = "menuBean")
@RequestScoped
public class MenuBean implements Serializable{

    @Inject
    private FoodtruckDao foodtruckDao;

    private FoodTruck foodtruck;
    private List<MenuItem> menu;

    private Integer foodtruckId;

    @PostConstruct
    public void init() {
        // Obtener parámetro foodtruck_id de la URL
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idParam = params.get("foodtruck_id");

        if (idParam == null) {
            redirectToMain();
            return;
        }

        try {
            foodtruckId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            redirectToMain();
            return;
        }

        // Obtener foodtruck con menú
        foodtruck = foodtruckDao.getFoodTruckById(foodtruckId);
        if (foodtruck == null) {
            redirectToMain();
            return;
        }

        menu = foodtruck.getMenu();
    }

    private void redirectToMain() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("main.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public FoodTruck getFoodtruck() {
        return foodtruck;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

}
