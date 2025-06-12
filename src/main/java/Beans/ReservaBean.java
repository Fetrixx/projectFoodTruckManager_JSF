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
import Models.ReservaItem;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.PrimeFaces;
/**
 *
 * @author Elias
 */
@Named(value = "reservaBean")
@RequestScoped
public class ReservaBean {

    private List<Reserva> reservas;
    private Map<Integer, List<ReservaItem>> reservaItems = new HashMap<>();
    private Map<Integer, String> foodtruckNames = new HashMap<>();

    @Inject
    private ReservaDao reservaDao;

    @Inject
    private FoodtruckDao foodTruckDao;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        int userId = getCurrentUserId();
        reservas = reservaDao.getReservasByUsuarioId(userId, false);

        // Cargar items y nombres de foodtrucks
        for (Reserva reserva : reservas) {
            List<ReservaItem> items = reservaDao.getItemsByReservaId(reserva.getId());
            reservaItems.put(reserva.getId(), items);

            String foodtruckName = foodTruckDao.getFoodTruckById(reserva.getFoodtruckId()).getNombre();
            foodtruckNames.put(reserva.getId(), foodtruckName);
        }
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public List<ReservaItem> getItemsForReserva(int reservaId) {
        return reservaItems.get(reservaId);
    }

    public String getFoodtruckName(int reservaId) {
        return foodtruckNames.get(reservaId);
    }

    public void deleteReserva(int reservaId) {
        reservaDao.deleteReserva(reservaId);
        init(); // Recargar reservas
        PrimeFaces.current().executeScript("PF('successDialog').show();");
    }

    public void cancelReserva(int reservaId) {
        reservaDao.updateReservaEstado(reservaId, "cancelada");
        init(); // Recargar reservas
        PrimeFaces.current().executeScript("PF('successDialog').show();");
    }

    public void completeReserva(int reservaId) {
        reservaDao.updateReservaEstado(reservaId, "confirmada");
        init(); // Recargar reservas
        PrimeFaces.current().executeScript("PF('successDialog').show();");
    }

    private int getCurrentUserId() {
        return loginBean.getUsuario().getId();
    }

    public void redirectToBooking(int foodtruckId) {
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler nav = context.getApplication().getNavigationHandler();
        nav.handleNavigation(context, null, "booking?foodtruck=" + foodtruckId);
    }
}
