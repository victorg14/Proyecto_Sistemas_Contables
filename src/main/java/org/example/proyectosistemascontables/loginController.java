package org.example.proyectosistemascontables;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.proyectosistemascontables.modelos.UsuarioModel;

public class loginController {

    @FXML
    private Button btnIngresar;

    @FXML
    private Button btnOjo;

    @FXML
    private Button btnRegistrar;

    @FXML
    private TextField txtPass;

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtpass1;

    @FXML
    private Label lblID;

    @FXML
    private AnchorPane AnchorLogin;

    @FXML
    private void MenuApp(ActionEvent event) {
        // Lógica para manejar el evento
    }

    public void initialize(){

        this.btnIngresar.setOnAction(e->{
            int Id=0;
            Boolean existe;
            String rol = "";
            UsuarioModel usuarioModel = new UsuarioModel();
            existe = usuarioModel.Ingresar(this.txtUser.getText(), pass());
            if (existe == true){


                //mostramos una alerta antes de ingresar al sistema
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Has iniciado sesión correctamente.", ButtonType.OK);
                alert.setTitle("Bienvenido");
                alert.setHeaderText("Inicio de sesión exitoso");
                ImageView icono = new ImageView(
                        new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/comprobado.png"))
                );
                icono.setFitWidth(25);
                icono.setFitHeight(25);
                icono.setPreserveRatio(true);
                alert.setGraphic(icono);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(
                        new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/aurum.png"))
                );
                alert.showAndWait();





                Id = usuarioModel.recogerID(this.txtUser.getText());
                rol = usuarioModel.recogerRol(Id);
                cambiarEscena(rol, Id, this.txtUser.getText());

            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Credenciales Incorreptas");
                alert.setTitle("ERROR al ingresar");
                alert.show();
                this.txtUser.setText(null);
                this.txtPass.setText(null);
                this.txtpass1.setText(null);
            }

        });



        iniciarOjo();
        this.btnOjo.setOnAction(e->{
            Image ojoAbierto = new Image(getClass().getResourceAsStream("/org/example/proyectoSistemasContables/img/ojoabierto.png"));
            Image ojoCerrado = new Image(getClass().getResourceAsStream("/org/example/proyectoSistemasContables/img/ojo.png"));
            if(txtPass.isVisible()){
                txtpass1.setVisible(true);
                txtpass1.setText(txtPass.getText());
                txtPass.setVisible(false);
                this.btnOjo.setGraphic(new ImageView(ojoAbierto));
            }else{
                txtPass.setVisible(true);
                txtPass.setText(txtpass1.getText());
                txtpass1.setVisible(false);
                this.btnOjo.setGraphic(new ImageView(ojoCerrado));
            }
        });

    }

    public void iniciarOjo(){
        Image ojoCerrado = new Image(getClass().getResourceAsStream("/org/example/proyectoSistemasContables/img/ojoabierto.png"));
        this.btnOjo.setGraphic(new ImageView(ojoCerrado));
        this.txtpass1.setVisible(true);
        this.txtPass.setVisible(false);
    }

    public String pass(){
        if (this.txtpass1.isVisible()){
            return txtpass1.getText();
        }else{
            return txtPass.getText();
        }
    }

    public void cambiarEscena(String rol, int Id, String user){
        try {
            if (rol.equals("Admin")) {
                System.out.println("Hola desde admin");
                // Cargar el archivo FXML de Scene2 y pasar el dato
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDahsboard.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la segunda escena
                AdminController controladorScene2 = loader.getController();
                controladorScene2.recibirID(Id);
                controladorScene2.recibirRol(rol);
                controladorScene2.recibirUser(user);// Pasamos el nombre al controlador de Scene2
                // Cambiar la escena
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                stage.hide();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();
            } else if (rol.equals("Contador")) {
                // Cargar el archivo FXML de Scene2 y pasar el dato
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Contadordashboard.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la segunda escena
                ContadorController controladorScene2 = loader.getController();
                controladorScene2.recibirID(Id);
                controladorScene2.recibirRol(rol);
                controladorScene2.recibirUser(user);// Pasamos el nombre al controlador de Scene2
                // Cambiar la escena
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                stage.hide();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();
            } else {
                // Cargar el archivo FXML de Scene2 y pasar el dato
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Auditordashboard.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la segunda escena
                AuditorController controladorScene2 = loader.getController();
                controladorScene2.recibirID(Id);
                controladorScene2.recibirRol(rol);
                controladorScene2.recibirUser(user);// Pasamos el nombre al controlador de Scene2
                // Cambiar la escena
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                stage.hide();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();
            }

        } catch (Exception er) {
            er.printStackTrace();
        }
    }
}
