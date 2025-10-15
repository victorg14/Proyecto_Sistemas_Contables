package org.example.proyectosistemascontables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ContadorController {
    @FXML
    private AnchorPane AnchorMostrar;

    @FXML
    private AnchorPane anchorAdministrar;

    @FXML
    private AnchorPane anchorRegistrar;

    @FXML
    private AnchorPane anchorRegistrarPartida;

    @FXML
    private AnchorPane anchords;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnLibroMayor;

    @FXML
    private Button btnPrincipal;

    @FXML
    private Button btnRegistrarPartida;

    @FXML
    private Button btnlibrodiario;

    @FXML
    private Label lblID;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblUser;

    private Timestamp inicio = null;
    private Timestamp fin = null;
    private ObservableList<String> cuentas = FXCollections.observableArrayList();



    public void recibirID(int id){
        this.lblID.setText(String.valueOf(id));
    }
    public void recibirRol(String rol){this.lblRol.setText(rol);}
    public void recibirUser(String user){this.lblUser.setText(user);}

    private int boton = 1; // Valor inicial (ningún botón activo)
    private final Map<Integer, Pair<Button, AnchorPane>> botones = new HashMap<>();


    public void initialize(){

        botones.put(1, new Pair<>(btnPrincipal, anchords));
        botones.put(2, new Pair<>(btnRegistrarPartida, anchorRegistrarPartida));
        botones.put(3, new Pair<>(btnlibrodiario, anchorAdministrar));
        botones.put(4, new Pair<>(btnLibroMayor, anchorRegistrar));

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
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

            } catch (Exception er) {
                er.printStackTrace();
            }
        });

        this.btnRegistrarPartida.setOnAction(e->{
            cambiarColor(this.boton, 2);
            this.boton = 2;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Contador/RegistrarPartida.fxml"));
                Parent vista = loader.load();

                // Pasar datos al controlador del nuevo FXML
//                UsuariosController controlador = loader.getController();
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

        this.btnlibrodiario.setOnAction(e -> {
            cambiarColor(this.boton, 3);
            this.boton = 3;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Contador/libroDiario.fxml"));
                Parent vista = loader.load();

//                 Pasar datos al controlador del nuevo FXML
                LibrodiarioController controlador = loader.getController();
                controlador.setContadorController(this);
                if (this.cuentas != null){
                    controlador.traerDatos(this.cuentas, this.inicio, this.fin);
                }
                controlador.CargarDatosFiltrados();

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

        this.btnLibroMayor.setOnAction(e->{
            cambiarColor(this.boton, 4);
            this.boton = 4;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectosistemascontables/Contador/libroMayor.fxml"));
                Parent vista = loader.load();

                // Pasar datos al controlador del nuevo FXML
                LibroMayorController controlador = loader.getController();

                if (this.cuentas != null){
                    controlador.setList(this.cuentas, this.inicio, this.fin);
                }


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

        this.btnPrincipal.setOnAction(e->{
//            System.out.println("Hola desde btonds");
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
        // Desactivar el botón anterior
        if (botones.containsKey(btnAntes)) {
            Button btnAnterior = botones.get(btnAntes).getKey();
            AnchorPane anchorAnterior = botones.get(btnAntes).getValue();

            anchorAnterior.setStyle("-fx-background-color: #FEFAE0;");
            btnAnterior.setStyle("-fx-background-color: transparent; -fx-text-fill: #00401B;");

            Node principalW = anchorAnterior.lookup("#principal_b");
            Node principalG = anchorAnterior.lookup("#principal_g");
            Node registrarW = anchorAnterior.lookup("#registrar_b");
            Node registrarG = anchorAnterior.lookup("#registrar_g");
            Node libroDW = anchorAnterior.lookup("#libro_diario_b");
            Node libroDG = anchorAnterior.lookup("#libro_diario_g");
            Node libroMB = anchorAnterior.lookup("#libro_mayor_b");
            Node libroMG = anchorAnterior.lookup("#libro_mayor_g");

            if (principalW != null) principalW.setVisible(false);
            if (principalG != null) principalG.setVisible(true);
            if (registrarW != null) registrarW.setVisible(false);
            if (registrarG != null) registrarG.setVisible(true);
            if (libroDW != null) libroDW.setVisible(false);
            if (libroDG != null) libroDG.setVisible(true);
            if (libroMB != null) libroMB.setVisible(false);
            if (libroMG != null) libroMG.setVisible(true);


        }

        // Activar el nuevo botón
        if (botones.containsKey(btnDespues)) {
            Button btnNuevo = botones.get(btnDespues).getKey();
            AnchorPane anchorNuevo = botones.get(btnDespues).getValue();

            anchorNuevo.setStyle("-fx-background-color: #00401b;");
            btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: #fefae0;");

            Node principalW = anchorNuevo.lookup("#principal_b");
            Node principalG = anchorNuevo.lookup("#principal_g");
            Node registrarW = anchorNuevo.lookup("#registrar_b");
            Node registrarG = anchorNuevo.lookup("#registrar_g");
            Node libroDW = anchorNuevo.lookup("#libro_diario_b");
            Node libroDG = anchorNuevo.lookup("#libro_diario_g");
            Node libroMB = anchorNuevo.lookup("#libro_mayor_b");
            Node libroMG = anchorNuevo.lookup("#libro_mayor_g");

            if (principalW != null) principalW.setVisible(true);
            if (principalG != null) principalG.setVisible(false);
            if (registrarW != null) registrarW.setVisible(true);
            if (registrarG != null) registrarG.setVisible(false);
            if (libroDW != null) libroDW.setVisible(true);
            if (libroDG != null) libroDG.setVisible(false);
            if (libroMB != null) libroMB.setVisible(true);
            if (libroMG != null) libroMG.setVisible(false);
        }
    }

    public void activarBotonLMayor(Integer boton){
        cambiarColor(this.boton, boton);
        this.boton= boton;
    }

    public void TraerFiltros(ObservableList<String> cuentas, Timestamp inicio, Timestamp fin){
        this.cuentas = cuentas;
        this.inicio = inicio;
        this.fin = fin;
    }

}
