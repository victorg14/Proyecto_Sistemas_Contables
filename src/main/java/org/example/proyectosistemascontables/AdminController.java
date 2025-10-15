package org.example.proyectosistemascontables;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AdminController {

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
    private Button btnRegister;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnds;

    @FXML
    private Label lblID;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblUser;

    @FXML
    private ImageView user_green;

    @FXML
    private ImageView user_white;





    public void recibirID(int id){
        this.lblID.setText(String.valueOf(id));
    }
    public void recibirRol(String rol){this.lblRol.setText(rol);}
    public void recibirUser(String user){this.lblUser.setText(user);}

    private int boton = 0; // Valor inicial (ningún botón activo)
    private final Map<Integer, Pair<Button, AnchorPane>> botones = new HashMap<>();


    public void initialize(){

        botones.put(1, new Pair<>(btnds, anchords));
        botones.put(2, new Pair<>(btnAdministrar, anchorAdministrar));
        botones.put(3, new Pair<>(btnRegister, anchorRegistrar));
        // Agrega más botones aquí cuando tengas más
        // botones.put(3, new Pair<>(btnUsuarios, anchorUsuarios)); etc.

        this.btnCerrarSesion.setOnAction(e->{
            try {
                    // Cargar el archivo FXML de Scene2 y pasar el dato
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                    Parent root = loader.load();

                    // Obtener el controlador de la segunda escena
//                    AdminController controladorScene2 = loader.getController();
//                    controladorScene2.recibirID(Id);
//                    controladorScene2.recibirRol(rol);
//                    controladorScene2.recibirUser(user);// Pasamos el nombre al controlador de Scene2
                    // Cambiar la escena
                    Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                    stage.hide();
                    stage.setMaximized(true);
                    stage.setScene(new Scene(root));
                    stage.show();

            } catch (Exception er) {
                er.printStackTrace();
            }
        });

        this.btnAdministrar.setOnAction(e -> {
            cambiarColor(this.boton, 2);
            this.boton = 2;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin/mostrarUsuarios.fxml"));
                Parent vista = loader.load();

                // Pasar datos al controlador del nuevo FXML
                UsuariosController controlador = loader.getController();
//                controlador.recibirID(Id);

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Opcional: Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

        });

        this.btnRegister.setOnAction(e->{
            cambiarColor(this.boton, 3);
            this.boton = 3;


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin/RegistrarUsuarios.fxml"));
                Parent vista = loader.load();

                // Pasar datos al controlador del nuevo FXML
                RegistrarUsuarioController controlador = loader.getController();
//                controlador.recibirID(Id);

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Opcional: Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        this.btnds.setOnAction(e->{
            System.out.println("Hola desde btonds");
            cambiarColor(this.boton, 1);
            this.boton = 1;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent vista = loader.load();

                HelloController helloController = loader.getController();
                helloController.recibirRol(lblRol.getText());
                helloController.recibirUser(lblUser.getText());

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Opcional: Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

    }



    private void cambiarColor(int btnAntes, int btnDespues) {
        //  Desactivar botón anterior
        if (botones.containsKey(btnAntes)) {
            Button btnAnterior = botones.get(btnAntes).getKey();
            AnchorPane anchorAnterior = botones.get(btnAntes).getValue();

            anchorAnterior.setStyle("-fx-background-color: #FEFAE0;");
            btnAnterior.setStyle("-fx-background-color: transparent; -fx-text-fill: #00401B;");

            // Buscar los íconos dentro del AnchorPane y cambiar visibilidad
            Node userWhite = anchorAnterior.lookup("#user_white");
            Node userGreen = anchorAnterior.lookup("#user_green");
            Node adminWhite = anchorAnterior.lookup("#admin_white");
            Node adminGreen = anchorAnterior.lookup("#admin_green");
            Node dashWhite = anchorAnterior.lookup("#dash_white");
            Node dashGreen = anchorAnterior.lookup("#dash_green");

            if (userWhite != null) userWhite.setVisible(false);
            if (userGreen != null) userGreen.setVisible(true);
            if (adminWhite != null) adminWhite.setVisible(false);
            if (adminGreen != null) adminGreen.setVisible(true);
            if (dashWhite != null) dashWhite.setVisible(false);
            if (dashGreen != null) dashGreen.setVisible(true);
        }

        // Activar nuevo botón
        if (botones.containsKey(btnDespues)) {
            Button btnNuevo = botones.get(btnDespues).getKey();
            AnchorPane anchorNuevo = botones.get(btnDespues).getValue();

            anchorNuevo.setStyle("-fx-background-color: #00401B;");
            btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: #FEFAE0;");

            Node userWhite = anchorNuevo.lookup("#user_white");
            Node userGreen = anchorNuevo.lookup("#user_green");
            Node adminWhite = anchorNuevo.lookup("#admin_white");
            Node adminGreen = anchorNuevo.lookup("#admin_green");
            Node dashWhite = anchorNuevo.lookup("#dash_white");
            Node dashGreen = anchorNuevo.lookup("#dash_green");

            if (userWhite != null) userWhite.setVisible(true);
            if (userGreen != null) userGreen.setVisible(false);
            if (adminWhite != null) adminWhite.setVisible(true);
            if (adminGreen != null) adminGreen.setVisible(false);
            if (dashWhite != null) dashWhite.setVisible(true);
            if (dashGreen != null) dashGreen.setVisible(false);
        }
    }



}
