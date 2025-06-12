/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import DAO.FavoritoDao;
import jakarta.inject.Named;
//import jakarta.enterprise.context.RequestScoped;

import Models.FoodTruck;
import DAO.FoodtruckDao;
import DAO.ReviewDao;
import Models.Review;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elias
 */
@Named(value = "indexBean")
@ViewScoped
public class IndexBean implements Serializable {

    @Inject
    private FoodtruckDao foodTruckDAO;

    @Inject
    private ReviewDao reviewDAO;
    @Inject
    private FavoritoDao favoritoDao;

    @Inject
    private LoginBean loginBean;

    private List<FoodTruck> foodTrucks;
    private String reviews; // Para almacenar las reseñas como texto

    @PostConstruct
    public void init() {
        // Obtener lista de food trucks desde DAO
//        foodTrucks = foodTruckDAO.getAllFoodTrucks();
        int usuarioId = loginBean.getUsuario().getId(); // Obtén el id del usuario actual
        foodTrucks = foodTruckDAO.getAllFoodTrucks(usuarioId);

        // Para cada food truck, setear calificación promedio y cantidad de reseñas
        for (FoodTruck ft : foodTrucks) {
            double avgRating = reviewDAO.obtenerCalificacionPromedio(ft.getId());
            int reviewCount = reviewDAO.contarReseñasPorFoodTruck(ft.getId());
            ft.setAvg_rating(avgRating);
            ft.setReview_count(reviewCount);
        }
    }

    public List<FoodTruck> getFoodTrucks() {
        return foodTrucks;
    }

    // Método para mostrar reseñas
    public void showReviews(int foodTruckId) {
        List<Review> reviewList = reviewDAO.getReviewsByFoodTruckId(foodTruckId);
        StringBuilder sb = new StringBuilder();
        for (Review review : reviewList) {
            sb.append("<p><strong>").append(review.getUserName()).append(": </strong>")
                    .append(review.getComentario()).append(" (").append(review.getRating()).append(" estrellas)</p>");
        }
        reviews = sb.toString();
    }

    public String getReviews() {
        return reviews;
    }

    // Método para toggle favorito
    public void toggleFavorito() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String idStr = params.get("foodtruckId");
        if (idStr != null) {
            int foodtruckId = Integer.parseInt(idStr);
            int usuarioId = loginBean.getUsuario().getId();

            if (favoritoDao.isFavorito(usuarioId, foodtruckId)) {
                favoritoDao.deleteFavorito(usuarioId, foodtruckId);
            } else {
                favoritoDao.addFavorito(usuarioId, foodtruckId);
            }

            // Recarga la lista para actualizar el estado de favoritos
            foodTrucks = foodTruckDAO.getAllFoodTrucks(usuarioId);

            // Opcional: recalcula ratings si lo deseas
            for (FoodTruck ft : foodTrucks) {
                double avgRating = reviewDAO.obtenerCalificacionPromedio(ft.getId());
                int reviewCount = reviewDAO.contarReseñasPorFoodTruck(ft.getId());
                ft.setAvg_rating(avgRating);
                ft.setReview_count(reviewCount);
            }
        }
    }


}
