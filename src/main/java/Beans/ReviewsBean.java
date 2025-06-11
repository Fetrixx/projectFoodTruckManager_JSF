/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import Models.FoodTruck;
import DAO.FoodtruckDao;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import DAO.ReviewDao;
import Models.Review;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Elias
 */
@Named(value = "reviewsBean")
@ViewScoped
public class ReviewsBean implements Serializable{

    @Inject
    private ReviewDao reviewDAO;

    @Inject
    private FoodtruckDao foodtruckDAO;
    
    @Inject
    private LoginBean loginBean;
    
    private List<FoodTruck> foodTrucks;

    private List<Review> latestReviews;
    private int selectedFoodTruck;
    private int rating;
    private String comentario;
    private int reviewId;
    private String action = "create";

    public ReviewsBean() {
        // Constructor vacío, no usar para inicialización que dependa de inyección
    }

    public List<FoodTruck> getFoodTrucks() {
        return foodTrucks;
    }

    public void setFoodTrucks(List<FoodTruck> foodTrucks) {
        this.foodTrucks = foodTrucks;
    }

    @PostConstruct
    public void init() {
        foodTrucks = foodtruckDAO.getAllFoodTrucks();

        loadLatestReviews();
    }

    public void loadLatestReviews() {
        this.latestReviews = reviewDAO.getAllReviews();
    }

    public void submitReview() {
        int usuarioId = loginBean.getUsuario().getId();
        if ("edit".equals(action) && reviewId > 0) {
            reviewDAO.updateReview(reviewId, usuarioId, rating, comentario); // Ajusta usuarioId según contexto
        } else {
            reviewDAO.createReview(usuarioId, selectedFoodTruck, rating, comentario);
        }
        resetFields();
        loadLatestReviews();
        action = "create";
    }

    public void editReview(Review review) {
        this.reviewId = review.getId();
        this.selectedFoodTruck = review.getFoodtruckId();
        this.rating = review.getRating();
        this.comentario = review.getComentario();
        this.action = "edit";
    }

    public void deleteReview(int reviewId) {
        int usuarioId = loginBean.getUsuario().getId();
        reviewDAO.deleteReview(reviewId, usuarioId); // Ajusta usuarioId según contexto
        loadLatestReviews();
    }

    private void resetFields() {
        this.reviewId = 0;
        this.selectedFoodTruck = 0;
        this.rating = 0;
        this.comentario = "";
    }

    // Getters y setters
    public List<Review> getLatestReviews() {
        return latestReviews;
    }

    public int getSelectedFoodTruck() {
        return selectedFoodTruck;
    }

    public void setSelectedFoodTruck(int selectedFoodTruck) {
        this.selectedFoodTruck = selectedFoodTruck;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public void cancelEdit() {
        resetFields();
        action="create";
    }

}
