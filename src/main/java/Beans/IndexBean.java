/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import jakarta.inject.Named;
//import jakarta.enterprise.context.RequestScoped;

import Models.FoodTruck;
import DAO.FoodtruckDao;
import DAO.ReviewDao;
import Models.Review;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
import java.util.List;

/**
 *
 * @author Elias
 */
@Named(value = "indexBean")
@ViewScoped
public class IndexBean implements Serializable{

    @Inject
    private FoodtruckDao foodTruckDAO;

    @Inject
    private ReviewDao reviewDAO;

    private List<FoodTruck> foodTrucks;
    private String reviews; // Para almacenar las reseñas como texto

    @PostConstruct
    public void init() {
        // Obtener lista de food trucks desde DAO
        foodTrucks = foodTruckDAO.getAllFoodTrucks();

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
        List<Review> reviewList = reviewDAO.getReviewsByFoodTruckId(foodTruckId); // Asumiendo que tienes este método
        StringBuilder sb = new StringBuilder();
        for (Review review : reviewList) {
            sb.append(review).append("<br/>"); // Formato simple para las reseñas
        }
        reviews = sb.toString();
    }

    public String getReviews() {
        return reviews;
    }
}
