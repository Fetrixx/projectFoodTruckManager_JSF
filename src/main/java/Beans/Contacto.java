/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package Beans;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
@Named(value = "contacto")
@RequestScoped
public class Contacto {

    private List<String> imagenes;
    private List<Integrante> integrantes;

    @PostConstruct
    public void init() {
        // Inicializar lista de imágenes
        imagenes = new ArrayList<>();
        imagenes.add("/projectFoodTruckManager/public/assets/img/foodtruck_img.jpg");
        imagenes.add("https://thehawaiivacationguide.com/wp-content/uploads/2020/01/best-maui-food-truck-parks.jpg");
        imagenes.add("https://thehawaiivacationguide.com/wp-content/uploads/2020/01/hana-maui-food-truck-park-family-1024x768.jpg");
        imagenes.add("https://thehawaiivacationguide.com/wp-content/uploads/2022/12/maui-food-trucks-family-dining-1024x768.jpg");
        imagenes.add("https://thehawaiivacationguide.com/wp-content/uploads/2020/01/maui-food-truck-park-kaanapali-1024x546.jpg");
        imagenes.add("https://thehawaiivacationguide.com/wp-content/uploads/2022/12/maui-kaanapali-food-truck-park-1024x576.jpg");

        // Inicializar lista de integrantes
        integrantes = new ArrayList<>();
        integrantes.add(new Integrante("Elias Medina", "emedina@email.com", "user.svg"));
        integrantes.add(new Integrante("German Lares", "glares@email.com", "user.svg"));
        integrantes.add(new Integrante("Hugo Silguero", "hsilguero@email.com", "user.svg"));
        integrantes.add(new Integrante("Delcy Mendoza", "dmendoza@email.com", "user.svg"));
        integrantes.add(new Integrante("Noelia Apodaca", "napodaca@email.com", "user.svg"));
    }

    // Getters
    public List<String> getImagenes() {
        return imagenes;
    }

    public List<Integrante> getIntegrantes() {
        return integrantes;
    }

    // Clase interna para representar un integrante
    public static class Integrante {

        private String nombre;
        private String contacto;
        private String foto;

        public Integrante(String nombre, String contacto, String foto) {
            this.nombre = nombre;
            this.contacto = contacto;
            this.foto = foto;
        }

        // Getters
        public String getNombre() {
            return nombre;
        }

        public String getContacto() {
            return contacto;
        }

        public String getFoto() {
            return foto;
        }
    }

}
