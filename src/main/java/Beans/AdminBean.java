/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import jakarta.inject.Named;
import java.io.Serializable;
import DAO.FoodtruckDao;
import DAO.MenuDao;
import Models.FoodTruck;
import Models.MenuItem;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Elias
 */
@Named(value = "adminBean")
@ViewScoped
public class AdminBean implements Serializable {

    private static final Logger logger = Logger.getLogger(AdminBean.class.getName());

    @Inject
    private FoodtruckDao foodtruckDao;
    @Inject
    private MenuDao menuDao;

    private List<FoodTruck> foodTrucks;
    private FoodTruck selectedFoodTruck;
    private MenuItem selectedMenuItem;
    private boolean firstLoad = true;

    @PostConstruct
    public void init() {
        loadFoodTrucks();
        // Inicializar objetos para evitar nulos
        selectedFoodTruck = new FoodTruck();
        selectedFoodTruck.setHorarioApertura("08:00");
        selectedFoodTruck.setHorarioCierre("22:00");
        selectedMenuItem = new MenuItem();
    }

    public void loadFoodTrucks() {
        foodTrucks = foodtruckDao.getAllFoodTrucks(0);
        firstLoad = false;
    }

    public List<FoodTruck> getFoodTrucks() {
        return foodTrucks;
    }

    public FoodTruck getSelectedFoodTruck() {
        return selectedFoodTruck;
    }

    public void setSelectedFoodTruck(FoodTruck selectedFoodTruck) {
        this.selectedFoodTruck = selectedFoodTruck;
    }

    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }

    public void saveFoodTruck() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            if (selectedFoodTruck.getId() == 0) {
                int newId = foodtruckDao.createFoodTruck(selectedFoodTruck);
                if (newId > 0) {
                    selectedFoodTruck.setId(newId);
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Food Truck creado"));
                }
            } else {
                boolean success = foodtruckDao.updateFoodTruck(selectedFoodTruck);
                if (success) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Food Truck actualizado"));
                }
            }
            loadFoodTrucks();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar food truck", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteFoodTruck(FoodTruck ft) {
        try {
            boolean success = foodtruckDao.deleteFoodTruck(ft.getId());
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Food Truck eliminado"));

                if (selectedFoodTruck != null && selectedFoodTruck.getId() == ft.getId()) {
                    selectedFoodTruck = new FoodTruck();
                }
                loadFoodTrucks();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar food truck", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void saveMenuItem() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            // Validación manual para evitar errores prematuros
            if (selectedMenuItem.getNombre() == null || selectedMenuItem.getNombre().isEmpty()) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre es requerido"));
                return;
            }

            if (selectedMenuItem.getPrecio() <= 0) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El precio debe ser mayor que 0"));
                return;
            }

            if (selectedMenuItem.getId() == 0) {
                int newId = menuDao.createMenuItem(
                        selectedFoodTruck.getId(),
                        selectedMenuItem.getNombre(),
                        selectedMenuItem.getDescripcion(),
                        selectedMenuItem.getPrecio(),
                        selectedMenuItem.getImagen()
                );
                if (newId > 0) {
                    selectedMenuItem.setId(newId);
                    context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ítem creado"));
                }
            } else {
                boolean success = menuDao.updateMenuItem(
                        selectedMenuItem.getId(),
                        selectedMenuItem.getNombre(),
                        selectedMenuItem.getDescripcion(),
                        selectedMenuItem.getPrecio(),
                        selectedMenuItem.getImagen()
                );
                if (success) {
                    context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ítem actualizado"));
                }
            }
            // Actualizar menú del food truck
            refreshMenu();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar ítem de menú", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    private void refreshMenu() {
        if (selectedFoodTruck != null && selectedFoodTruck.getId() != 0) {
            selectedFoodTruck.setMenu(menuDao.getMenuByFoodTruckId(selectedFoodTruck.getId()));
        }
    }

    public void deleteMenuItem(MenuItem item) {
        try {
            boolean success = menuDao.deleteMenuItem(item.getId());
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ítem eliminado"));

                // Actualizar menú del food truck
                refreshMenu();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar ítem de menú", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void newFoodTruck() {
        selectedFoodTruck = new FoodTruck();
        selectedFoodTruck.setHorarioApertura("08:00");
        selectedFoodTruck.setHorarioCierre("22:00");
        selectedFoodTruck.setLat(0.0);
        selectedFoodTruck.setLng(0.0);
        selectedMenuItem = new MenuItem();
    }

    public void newMenuItem() {
        selectedMenuItem = new MenuItem();
    }

    public void prepareEditFoodTruck(FoodTruck ft) {
        // Cargar datos completos del food truck
        selectedFoodTruck = foodtruckDao.getFoodTruckById(ft.getId());
        refreshMenu();
    }

    public void selectMenuItem(MenuItem item) {
        selectedMenuItem = item;
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    public void onFoodTruckSelect(SelectEvent<FoodTruck> event) {
        FoodTruck selected = event.getObject();
        if (selected != null) {
            // Cargar datos completos y menú
            selectedFoodTruck = foodtruckDao.getFoodTruckById(selected.getId());
            System.out.println("selected: " + selectedFoodTruck.getNombre());
            refreshMenu();
            firstLoad = false;
        }
    }

//    public void prepareEditMenuItem(MenuItem item) {
//        System.out.println("item: " + item);
//        System.out.println("item nom: " + item.getNombre());
//        if (item != null && item.getId() != 0) {
//            selectedMenuItem = menuDao.getMenuItemById(item.getId());
//        } else {
//            selectedMenuItem = new MenuItem();
//        }
//        // Limpia mensajes o estados si es necesario
//        FacesContext.getCurrentInstance().getMessages().remove();
//    }
    public void prepareEditMenuItem() {
        if (selectedMenuItem != null && selectedMenuItem.getId() != 0) {
            selectedMenuItem = menuDao.getMenuItemById(selectedMenuItem.getId());
        } else {
            selectedMenuItem = new MenuItem();
        }
    }
}
