/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.Timestamp;

/**
 *
 * @author Elias
 */
public class Review {

    private int id;
    private int usuarioId;
    private int foodtruckId;
    private int rating;
    private String comentario;
    private Timestamp fecha;

    private String userName;
    private String foodtruckNombre;

// getters y setters para estos campos
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFoodtruckNombre() {
        return foodtruckNombre;
    }

    public void setFoodtruckNombre(String foodtruckNombre) {
        this.foodtruckNombre = foodtruckNombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getFoodtruckId() {
        return foodtruckId;
    }

    public void setFoodtruckId(int foodtruckId) {
        this.foodtruckId = foodtruckId;
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

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
