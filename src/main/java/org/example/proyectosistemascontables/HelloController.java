package org.example.proyectosistemascontables;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HelloController {

    @FXML
    private Label lblUser;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblBienvenida;

    @FXML
    private Label lblUserBienvenida;

    @FXML
    private Label lblRolBienvenida;

    @FXML
    private Label lblUltimoAcceso;

    /**
     * Recibe el rol del usuario y lo muestra en la interfaz
     */
    public void recibirRol(String rol) {
        if (rol != null && !rol.isEmpty()) {
            this.lblRol.setText(rol);
            this.lblRolBienvenida.setText(rol);
        }
    }

    /**
     * Recibe el nombre de usuario y lo muestra en la interfaz
     */
    public void recibirUser(String user) {
        if (user != null && !user.isEmpty()) {
            this.lblUser.setText(user);
            this.lblUserBienvenida.setText(user);

            // Personalizar mensaje de bienvenida
            personalizarSaludo();
        }
    }

    /**
     * Inicializa la vista con la fecha y hora actual
     */
    @FXML
    public void initialize() {
        // Establecer la fecha y hora actual
        actualizarFechaAcceso();
    }

    /**
     * Personaliza el mensaje de bienvenida según la hora del día
     */
    private void personalizarSaludo() {
        LocalDateTime ahora = LocalDateTime.now();
        int hora = ahora.getHour();
        String saludo;

        if (hora >= 5 && hora < 12) {
            saludo = "Buenos días";
        } else if (hora >= 12 && hora < 19) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }

        this.lblBienvenida.setText(saludo);
    }

    /**
     * Actualiza la etiqueta de último acceso con la fecha actual
     */
    private void actualizarFechaAcceso() {
        LocalDateTime ahora = LocalDateTime.now();

        // Formato: "14 de Octubre, 2025 - 15:30"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy - HH:mm", new Locale("es", "ES"));
        String fechaFormateada = ahora.format(formatter);

        this.lblUltimoAcceso.setText("Último acceso: " + fechaFormateada);
    }
}