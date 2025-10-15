package org.example.proyectosistemascontables;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.proyectosistemascontables.modelos.UsuarioModel;
import org.mindrot.jbcrypt.BCrypt;

public class RegistrarUsuarioController {
    @FXML
    private Button btnRegistrar;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPassword;

    @FXML
    private ComboBox<String> txtRol;

    @FXML
    private TextField txtUsuario;

    public void initialize(){
        this.txtRol.getItems().addAll("Admin", "Contador", "Auditor");
        this.btnRegistrar.setOnAction(e->{
            registrarUsuario();
        });
    }

    public void registrarUsuario(){
        // Validar que los campos no estén vacíos
        if (this.txtNombre.getText().trim().isEmpty() ||
                this.txtApellido.getText().trim().isEmpty() ||
                this.txtEmail.getText().trim().isEmpty() ||
                this.txtUsuario.getText().trim().isEmpty() ||
                this.txtPassword.getText().trim().isEmpty() ||
                this.txtRol.getValue() == null) {

            mostrarAlertaError("No pueden haber campos vacíos");
            return;
        }

        // Validar si el email ya existe
        if (emailYaExiste(this.txtEmail.getText().trim())) {
            mostrarAlertaError("El correo electrónico ya está registrado en otro usuario");
            return;
        }

        // Validar si el usuario ya existe
        if (usuarioYaExiste(this.txtUsuario.getText().trim())) {
            mostrarAlertaError("El nombre de usuario ya está registrado");
            return;
        }

        UsuarioModel usuarioModel = new UsuarioModel();
        try {
            usuarioModel.setNombre(this.txtNombre.getText());
            usuarioModel.setApellido(this.txtApellido.getText());
            usuarioModel.setEmail(this.txtEmail.getText());
            usuarioModel.setUsuario(this.txtUsuario.getText());
            usuarioModel.setPassword(encriptarContraseña(pass()));
            usuarioModel.setRol(this.txtRol.getValue());
            usuarioModel.InsertarUsuario();

            // Mostrar alerta de éxito
            mostrarAlertaExito();

            // Limpiar todos los campos
            limpiarCampos();

        }catch (Exception e){
            System.out.println(e);
            // Mostrar alerta de error genérica
            mostrarAlertaError("Ocurrió un error al registrar el usuario");
        }
    }

    private boolean emailYaExiste(String email) {
        try {
            UsuarioModel usuarioModel = new UsuarioModel();
            return usuarioModel.verificarEmailExistente(email);
        } catch (Exception e) {
            System.out.println("Error al verificar email: " + e);
            return false;
        }
    }

    private boolean usuarioYaExiste(String usuario) {
        try {
            UsuarioModel usuarioModel = new UsuarioModel();
            return usuarioModel.verificarUsuarioExistente(usuario);
        } catch (Exception e) {
            System.out.println("Error al verificar usuario: " + e);
            return false;
        }
    }

    private void mostrarAlertaExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "El usuario ha sido registrado exitosamente en el sistema.", ButtonType.OK);
        alert.setTitle("Registro Exitoso");
        alert.setHeaderText("Usuario registrado con éxito");

        // Icono de la alerta
        ImageView icono = new ImageView(
                new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/comprobado.png"))
        );
        icono.setFitWidth(25);
        icono.setFitHeight(25);
        icono.setPreserveRatio(true);
        alert.setGraphic(icono);

        // Icono de la ventana
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/aurum.png"))
        );

        alert.showAndWait();
    }

    private void mostrarAlertaError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Ha ocurrido un error al registrar el usuario: \n" + mensaje, ButtonType.OK);
        alert.setTitle("Error de Registro");
        alert.setHeaderText("Error al registrar usuario");

        // Icono de la alerta
        ImageView icono = new ImageView(
                new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/error.png"))
        );
        icono.setFitWidth(25);
        icono.setFitHeight(25);
        icono.setPreserveRatio(true);
        alert.setGraphic(icono);

        // Icono de la ventana
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/aurum.png"))
        );

        alert.showAndWait();
    }

    private void limpiarCampos() {
        this.txtNombre.clear();
        this.txtApellido.clear();
        this.txtEmail.clear();
        this.txtUsuario.clear();
        this.txtPassword.clear();
        this.txtRol.setValue(null);
    }

    public String encriptarContraseña(String contraseña) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(contraseña, salt);
        return hashedPassword;
    }

    public String pass(){
        if (this.txtPassword.isVisible()){
            return txtPassword.getText();
        }else{
            return txtPassword.getText();
        }
    }
}
