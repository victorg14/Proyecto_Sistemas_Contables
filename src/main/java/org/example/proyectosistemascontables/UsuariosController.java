package org.example.proyectosistemascontables;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.example.proyectosistemascontables.modelos.UsuarioModel;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UsuariosController {


    @FXML private TableView<UsuarioModel> tvUsuarios;
    @FXML private TableColumn<UsuarioModel, Integer> tcID;
    @FXML private TableColumn<UsuarioModel, String> tcUsuario;
    @FXML private TableColumn<UsuarioModel, String> tcEmail;
    @FXML private TableColumn<UsuarioModel, String> tcPassword;
    @FXML private TableColumn<UsuarioModel, String> tcRol;
    @FXML private TableColumn<UsuarioModel, Boolean> tcActivo;
    @FXML private TableColumn<UsuarioModel, Button> tcEditar;
    @FXML private TableColumn<UsuarioModel, Button> tcEliminar;


    @FXML private AnchorPane anchorform;
    @FXML private AnchorPane anchorUsuarios;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private RadioButton rbTrue;
    @FXML private RadioButton rbFalse;
    @FXML private ToggleGroup activo;

    @FXML private Button btnActualizar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCambiarPassword;
    @FXML private Button btnregistrar;
    @FXML private Button btnbuscar;
    @FXML private TextField txtBuscar;

    //  Variables de instancia
    private UsuarioModel usuarioSeleccionado;

    //  Inicialización
    @FXML
    public void initialize() {
        configurarTabla();
        configurarBusqueda();
        configurarRadioButtons();
        configurarFormulario();
        cargarDatos();

        // Ocultar formulario al inicio
        if (anchorform != null) {
            anchorform.setVisible(false);
        }

        cmbRol.setItems(FXCollections.observableArrayList("Administrador", "Contador", "Auditor"));
    }

    private void configurarTabla() {
        // Configurar columnas
        tcID.setCellValueFactory(new PropertyValueFactory<>("id_usuario"));
        tcUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        tcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tcPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        tcRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        tcActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Configurar columna de activo con estilo visual
        tcActivo.setCellFactory(column -> new TableCell<UsuarioModel, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(activo ? "✓ Activo" : "✗ Inactivo");
                    setStyle(activo ?
                            "-fx-text-fill: green; -fx-font-weight: bold;" :
                            "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });

        // Configurar botones de editar y eliminar
        configurarBotonEditar();
        configurarBotonEliminar();

        // Ocultar columna de contraseña por seguridad
        tcPassword.setVisible(false);
    }

    private void configurarBotonEditar() {
        tcEditar.setCellFactory(column -> new TableCell<UsuarioModel, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    // Crear botón con icono
                    Image image = new Image(getClass().getResourceAsStream(
                            "/org/example/proyectosistemascontables/img/editar.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    imageView.setPreserveRatio(true);

                    Button btnEditar = new Button("", imageView);
                    btnEditar.setStyle("-fx-background-color: transparent;");
                    btnEditar.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btnEditar.setAlignment(Pos.CENTER);

                    btnEditar.setOnAction(e -> {
                        usuarioSeleccionado = getTableView().getItems().get(getIndex());
                        cargarDatosEnFormulario(usuarioSeleccionado);
                        mostrarFormulario();
                    });

                    setGraphic(btnEditar);
                }
            }
        });
    }

    private void configurarBotonEliminar() {
        tcEliminar.setCellFactory(column -> new TableCell<UsuarioModel, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    // Crear botón con icono
                    Image image = new Image(getClass().getResourceAsStream(
                            "/org/example/proyectosistemascontables/img/borrar.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    imageView.setPreserveRatio(true);

                    Button btnEliminar = new Button("", imageView);
                    btnEliminar.setStyle("-fx-background-color: transparent;");
                    btnEliminar.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btnEliminar.setAlignment(Pos.CENTER);

                    btnEliminar.setOnAction(e -> {
                        UsuarioModel usuario = getTableView().getItems().get(getIndex());
                        confirmarYEliminarUsuario(usuario);
                    });

                    setGraphic(btnEliminar);
                }
            }
        });
    }

    private void configurarBusqueda() {
        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                cargarDatos();
            } else {
                tvUsuarios.setItems(UsuarioModel.buscarUsuarios(newValue.trim()));
            }
        });
    }

    private void configurarRadioButtons() {
        ToggleGroup group = new ToggleGroup();
        rbTrue.setToggleGroup(group);
        rbFalse.setToggleGroup(group);
        rbTrue.setSelected(true); // Por defecto activo
    }

    private void configurarFormulario() {
        if (btnActualizar != null) {
            btnActualizar.setOnAction(e -> actualizarUsuario());
        }

        if (btnCancelar != null) {
            btnCancelar.setOnAction(e -> ocultarFormulario());
        }

        if (btnCambiarPassword != null) {
            btnCambiarPassword.setOnAction(e -> mostrarDialogoCambiarPassword());
        }
    }

    //  Operaciones CRUD
    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarError("Error", "No hay usuario seleccionado",
                    "Debe seleccionar un usuario de la tabla primero.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            // Actualizar datos del modelo (sin contraseña)
            usuarioSeleccionado.setNombre(txtNombre.getText().trim());
            usuarioSeleccionado.setApellido(txtApellido.getText().trim());
            usuarioSeleccionado.setEmail(txtEmail.getText().trim());
            usuarioSeleccionado.setUsuario(txtUsuario.getText().trim());
            usuarioSeleccionado.setRol(cmbRol.getValue().trim());
            usuarioSeleccionado.setActivo(rbTrue.isSelected());

            // NO se actualiza la contraseña aquí, solo con el botón específico

            // Actualizar en la base de datos
            usuarioSeleccionado.actualizarUsuario();

            mostrarExito("Usuario actualizado", "El usuario se actualizó correctamente");
            cargarDatos();
            ocultarFormulario();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al actualizar", "No se pudo actualizar el usuario",
                    e.getMessage());
        }
    }

    private void mostrarDialogoCambiarPassword() {
        if (usuarioSeleccionado == null) {
            return;
        }

        // Crear diálogo personalizado
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Cambiar contraseña de: " + usuarioSeleccionado.getUsuario());

        // Botones
        ButtonType btnCambiar = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelarDialog = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCambiar, btnCancelarDialog);

        // Campos de contraseña
        PasswordField txtNuevaPassword = new PasswordField();
        txtNuevaPassword.setPromptText("Nueva contraseña");
        PasswordField txtConfirmarPassword = new PasswordField();
        txtConfirmarPassword.setPromptText("Confirmar contraseña");

        // Layout
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Label("Nueva contraseña:"),
                txtNuevaPassword,
                new Label("Confirmar contraseña:"),
                txtConfirmarPassword
        );
        dialog.getDialogPane().setContent(vbox);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCambiar) {
                return txtNuevaPassword.getText();
            }
            return null;
        });

        // Mostrar y procesar
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nuevaPassword -> {
            if (nuevaPassword.isEmpty()) {
                mostrarError("Error", "Contraseña vacía", "Debe ingresar una contraseña");
                return;
            }

            if (!nuevaPassword.equals(txtConfirmarPassword.getText())) {
                mostrarError("Error", "Las contraseñas no coinciden",
                        "Ambas contraseñas deben ser iguales");
                return;
            }

            if (nuevaPassword.length() < 6) {
                mostrarError("Error", "Contraseña muy corta",
                        "La contraseña debe tener al menos 6 caracteres");
                return;
            }

            try {
                // Encriptar y actualizar
                usuarioSeleccionado.setPassword(encriptarContraseña(nuevaPassword));
                usuarioSeleccionado.actualizarUsuario();
                mostrarExito("Contraseña actualizada",
                        "La contraseña se cambió correctamente");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error", "No se pudo cambiar la contraseña", e.getMessage());
            }
        });
    }

    private void confirmarYEliminarUsuario(UsuarioModel usuario) {
        Alert alert = crearAlerta(
                Alert.AlertType.CONFIRMATION,
                "Confirmar eliminación",
                "¿Está seguro de eliminar este usuario?",
                String.format("Usuario: %s %s\nNombre de usuario: %s\n\nEsta acción no se puede deshacer.",
                        usuario.getNombre(), usuario.getApellido(), usuario.getUsuario())
        );

        // Personalizar botones
        ButtonType btnEliminar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnEliminar, btnCancelar);

        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnEliminar) {
            try {
                usuario.eliminarUsuario();
                mostrarExito("Usuario eliminado", "El usuario se eliminó correctamente");
                cargarDatos();

                // Si el usuario eliminado estaba en el formulario, ocultarlo
                if (usuarioSeleccionado != null &&
                        usuarioSeleccionado.getId_usuario().equals(usuario.getId_usuario())) {
                    ocultarFormulario();
                }

            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al eliminar", "No se pudo eliminar el usuario",
                        e.getMessage());
            }
        }
    }

    // Gestión del formulario
    private void cargarDatosEnFormulario(UsuarioModel usuario) {
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtEmail.setText(usuario.getEmail());
        txtUsuario.setText(usuario.getUsuario());
        txtPassword.setPromptText("••••••••"); // Campo deshabilitado
        cmbRol.setValue(usuario.getRol());

        if (usuario.getActivo()) {
            rbTrue.setSelected(true);
        } else {
            rbFalse.setSelected(true);
        }
    }

    private void mostrarFormulario() {
        if (anchorform != null) {
            anchorform.setVisible(true);
        }
    }

    private void ocultarFormulario() {
        if (anchorform != null) {
            anchorform.setVisible(false);
            limpiarFormulario();
            usuarioSeleccionado = null;
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtUsuario.clear();
        txtPassword.clear();
        txtPassword.setPromptText("");
        //cmbRol.clear();
        rbTrue.setSelected(true);
    }

    // ==================== Validaciones ====================
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("• El nombre es obligatorio\n");
        }
        if (txtApellido.getText().trim().isEmpty()) {
            errores.append("• El apellido es obligatorio\n");
        }
        if (txtEmail.getText().trim().isEmpty()) {
            errores.append("• El email es obligatorio\n");
        }
        if (txtUsuario.getText().trim().isEmpty()) {
            errores.append("• El nombre de usuario es obligatorio\n");
        }
        if (cmbRol.getValue().trim().isEmpty()) {
            errores.append("• El rol es obligatorio\n");
        }

        if (errores.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText("Por favor complete los siguientes campos:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    //  Utilidades
    public void cargarDatos() {
        tvUsuarios.setItems(UsuarioModel.getUsuarios());
    }

    private String encriptarContraseña(String contraseña) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(contraseña, salt);
    }

    //  Alertas
    private Alert crearAlerta(Alert.AlertType tipo, String titulo, String header, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #f0f8ff;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-font-size: 14px;"
        );

        return alert;
    }

    private void mostrarError(String titulo, String header, String contenido) {
        Alert alert = crearAlerta(Alert.AlertType.ERROR, titulo, header, contenido);
        alert.showAndWait();
    }

    private void mostrarExito(String titulo, String contenido) {
        Alert alert = crearAlerta(Alert.AlertType.INFORMATION, titulo, null, contenido);
        alert.showAndWait();
    }
}