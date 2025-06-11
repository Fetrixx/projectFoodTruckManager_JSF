/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import DAO.FoodtruckDao;
import DAO.ReservaDao;
import Models.Reserva;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;

import java.util.List;



/**
 *
 * @author Elias
 */
@Named(value = "reservaBean")
@RequestScoped
public class ReservaBean {

    
     private List<Reserva> reservas;
    private int reservaIdToDelete;

    @Inject
    private ReservaDao reservaDao;

    @Inject
    private FoodtruckDao foodTruckDao;
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        // Cargar reservas del usuario
        int userId = getCurrentUserId();
        reservas = reservaDao.getReservasByUsuarioId(userId, false);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void deleteReserva(int reservaId) {
        reservaDao.deleteReserva(reservaId);
        init(); // Recargar reservas
    }

    public void cancelReserva(int reservaId) {
        reservaDao.updateReservaEstado(reservaId, "cancelada");
        init(); // Recargar reservas
    }

    public void completeReserva(int reservaId) {
        reservaDao.updateReservaEstado(reservaId, "confirmada");
        init(); // Recargar reservas
    }

    private int getCurrentUserId() {
        // Lógica para obtener el ID del usuario actual
        int usuarioId = loginBean.getUsuario().getId();
        return usuarioId; // Placeholder
    }
}
