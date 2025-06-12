/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import jakarta.inject.Named;
import DAO.FoodtruckDao;
import DAO.MenuDao;
import DAO.ReservaDao;
import Models.FoodTruck;
import Models.MenuItem;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.AjaxBehaviorEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Elias
 */
@Named(value = "bookingBean")
@SessionScoped
public class BookingBean implements Serializable {

    private int currentStep = 1;
    private List<FoodTruck> foodTrucks;
    private FoodTruck selectedFoodTruck;
    private List<MenuItem> menuItems;
//    private Map<Integer, Integer> quantities = new HashMap<>();
    private Map<Integer, Object> quantities = new HashMap<>();
    private double total = 0.0;
    private String qrCodeBase64;
    private int reservationId;

    @Inject
    private FoodtruckDao foodTruckDao;

    @Inject
    private MenuDao menuDao;

    @Inject
    private ReservaDao reservaDao;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        // Cargar foodtrucks solo si no están cargados
        if (foodTrucks == null || foodTrucks.isEmpty()) {
            int usuarioId = loginBean.getUsuario().getId(); // Obtén el id del usuario actual
            foodTrucks = foodTruckDao.getAllFoodTrucks(usuarioId);
        }
    }

    public void selectFoodTruckById() {
        FacesContext context = FacesContext.getCurrentInstance();
        String idParam = context.getExternalContext().getRequestParameterMap().get("foodTruckId");

        if (idParam != null && !idParam.isEmpty()) {
            int id = Integer.parseInt(idParam);
            for (FoodTruck ft : foodTrucks) {
                if (id == ft.getId()) {
                    this.selectedFoodTruck = ft;
                    break;
                }
            }
            if (selectedFoodTruck != null) {
                loadMenuItems();
                currentStep = 2;
            }
        }
    }

    public void loadMenuItems() {
        if (selectedFoodTruck != null) {
            menuItems = menuDao.getMenuByFoodTruckId(selectedFoodTruck.getId());

            Map<Integer, Object> newQuantities = new HashMap<>();
            for (MenuItem item : menuItems) {
                // Usar convertToInt para obtener el valor numérico
                int quantity = convertToInt(quantities.get(item.getId()));
                newQuantities.put(item.getId(), quantity);
            }
            quantities = newQuantities;
            updateCart(null);
        }
    }

    public void updateCart(AjaxBehaviorEvent event) {
        total = 0;
        if (menuItems != null) {
            for (MenuItem item : menuItems) {
                Object value = quantities.get(item.getId());
                int quantity = convertToInt(value);

                // Actualizar el mapa con el valor convertido
                quantities.put(item.getId(), quantity);

                if (quantity > 0) {
                    total += item.getPrecio() * quantity;
                }
            }
        }
        PrimeFaces.current().ajax().update(
                "bookingForm:cart-items-container",
                "bookingForm:cart-total",
                "bookingForm:confirm-button-container"
        );
    }

    public void confirmReservation() {
        FacesContext context = FacesContext.getCurrentInstance();
        System.out.println("confirmReservation");

        // Validar que haya al menos un ítem seleccionado
        boolean hasItems = false;
        for (Object value : quantities.values()) {
            int qty = convertToInt(value);
            if (qty > 0) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Debes seleccionar al menos un ítem."));
            return;
        }

        try {
            int usuarioId = loginBean.getUsuario().getId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String fecha = sdf.format(new Date());
            String hora = timeFormat.format(new Date());

            // Crear la reserva principal
            reservationId = reservaDao.createReserva(usuarioId, selectedFoodTruck.getId(), fecha, hora, total);
            System.out.println("reservationId: " + reservationId);
            if (reservationId > 0) {
                // Crear los ítems de la reserva
                for (MenuItem item : menuItems) {
                    Object value = quantities.get(item.getId());
                    int quantity = convertToInt(value);
                    if (quantity > 0) {
                        reservaDao.createReservaItem(reservationId, item.getId(), quantity, item.getPrecio());
                    }
                }

                // Generar QR
                generateQRCode();

                currentStep = 3;
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo crear la reserva."));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Error al crear la reserva: " + e.getMessage()));
        }
    }

    private void generateQRCode() {
        String text = "RESERVA-" + reservationId;
        int width = 200;
        int height = 200;

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            qrCodeBase64 = Base64.getEncoder().encodeToString(pngData);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    public void newReservation() {
        currentStep = 1;
        selectedFoodTruck = null;
        menuItems = null;
        quantities.clear();
        total = 0.0;
        qrCodeBase64 = null;
    }

    public void close() {
        try {
            newReservation();
            FacesContext.getCurrentInstance().getExternalContext().redirect("reservas.xhtml");
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al redirigir"));
        }
    }

    public void goToStep(int step) {
        this.currentStep = step;

        if (step == 1) {
            // Reset completo solo al volver al paso 1
            this.quantities.clear();
            this.total = 0;
            this.selectedFoodTruck = null;
            this.menuItems = null;
        }
    }

    // Getters y Setters
    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public List<FoodTruck> getFoodTrucks() {
        return foodTrucks;
    }

    public FoodTruck getSelectedFoodTruck() {
        return selectedFoodTruck;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public double getTotal() {
        return total;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public boolean isTotalGreaterThanZero() {
        return total > 0;
    }

    public Map<Integer, Object> getQuantities() {
        return quantities;
    }

    public void setQuantities(Map<Integer, Object> quantities) {
        this.quantities = quantities;
    }

    private int convertToInt_2(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return 0;
        }
    }

    public int getQuantityForItem(int itemId) {
        return convertToInt(quantities.get(itemId));
    }

    public int convertToInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return 0;
        }
    }
}
