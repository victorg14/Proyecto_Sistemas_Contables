package org.example.proyectosistemascontables;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuditorController {

    @FXML
    private AnchorPane AnchorMostrar;

    @FXML
    private AnchorPane anchorAdministrar;

    @FXML
    private AnchorPane anchorRegistrar;

    @FXML
    private AnchorPane anchords;

    @FXML
    private Button btnAdministrar;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnds;

    @FXML
    private Label lblID;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblUser;

    private int boton = 1; // Valor inicial
    private final Map<Integer, Pair<Button, AnchorPane>> botones = new HashMap<>();

    public void recibirID(int id){
        this.lblID.setText(String.valueOf(id));
    }

    public void recibirRol(String rol){
        this.lblRol.setText(rol);
    }

    public void recibirUser(String user){
        this.lblUser.setText(user);
    }

    public void initialize(){
        // Mapeo de botones
        botones.put(1, new Pair<>(btnds, anchords));
        botones.put(2, new Pair<>(btnAdministrar, anchorAdministrar));
        botones.put(3, new Pair<>(btnRegister, anchorRegistrar));

        // Botón Cerrar Sesión
        this.btnCerrarSesion.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                stage.hide();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

            } catch (Exception er) {
                er.printStackTrace();
            }
        });

        // Botón Principal
        this.btnds.setOnAction(e -> {
            cambiarColor(this.boton, 1);
            this.boton = 1;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectosistemascontables/hello-view.fxml"));
                Parent vista = loader.load();

                HelloController helloController = loader.getController();
                helloController.recibirRol(lblRol.getText());
                helloController.recibirUser(lblUser.getText());

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        // Botón Administrar Usuarios - Vista para gestionar usuarios del sistema
        this.btnAdministrar.setOnAction(e -> {
            cambiarColor(this.boton, 2);
            this.boton = 2;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Auditor/Usuarios.fxml"));
                Parent vista = loader.load();

                // Pasar datos al controlador del nuevo FXML
                // UsuariosController controlador = loader.getController();
                // controlador.recibirID(Integer.parseInt(lblID.getText()));

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (IOException exception) {
                exception.printStackTrace();
                System.out.println("Error al cargar vista de Administrar Usuarios");
            }
        });

        // Botón Registrar Usuarios - Vista para crear nuevos usuarios
        this.btnRegister.setOnAction(e -> {
            cambiarColor(this.boton, 3);
            this.boton = 3;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Auditor/RegistrarUsuario.fxml"));
                Parent vista = loader.load();

                // Pasar datos al controlador del nuevo FXML
                // RegistrarUsuarioController controlador = loader.getController();
                // controlador.recibirID(Integer.parseInt(lblID.getText()));

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (IOException exception) {
                exception.printStackTrace();
                System.out.println("Error al cargar vista de Registrar Usuario");
            }
        });
    }


    private void cambiarColor(int btnAntes, int btnDespues) {
        // Desactivar el botón anterior
        if (botones.containsKey(btnAntes)) {
            Button btnAnterior = botones.get(btnAntes).getKey();
            AnchorPane anchorAnterior = botones.get(btnAntes).getValue();

            // Estilo inactivo
            anchorAnterior.setStyle("-fx-background-color: #FEFAE0;");
            btnAnterior.setStyle("-fx-background-color: transparent; -fx-text-fill: #00401B;");
        }

        // Activar el nuevo botón
        if (botones.containsKey(btnDespues)) {
            Button btnNuevo = botones.get(btnDespues).getKey();
            AnchorPane anchorNuevo = botones.get(btnDespues).getValue();

            // Estilo activo (verde oscuro)
            anchorNuevo.setStyle("-fx-background-color: #00401b;");
            btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: #fefae0;");
        }
    }
}