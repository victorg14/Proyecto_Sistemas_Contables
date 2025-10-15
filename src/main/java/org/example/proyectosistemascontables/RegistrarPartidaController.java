package org.example.proyectosistemascontables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import org.example.proyectosistemascontables.modelos.DetallesPartidaModel;
import org.example.proyectosistemascontables.modelos.DocumentoFuenteModel;
import org.example.proyectosistemascontables.modelos.PartidasModel;
import org.example.proyectosistemascontables.Conexion.ConexionDB;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

public class RegistrarPartidaController {
    @FXML private Button btnAdjuntar;
    @FXML private Button btnAgregar;
    @FXML private Button btnAnterior;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardarTodo;
    @FXML private Button btnSiguiente;
    @FXML private Button btnzoommas;
    @FXML private Button btnzoommenos;
    @FXML private TableColumn<DetallesPartidaModel, Button> clBorrar;
    @FXML private TableColumn<DetallesPartidaModel, Integer> clCodigo;
    @FXML private TableColumn<DetallesPartidaModel, String> clCuenta;
    @FXML private TableColumn<DetallesPartidaModel, Double> clDebe;
    @FXML private TableColumn<DetallesPartidaModel, Button> clEditar;
    @FXML private TableColumn<DetallesPartidaModel, Double> clHaber;
    @FXML private ComboBox<String> cmbCuenta;
    @FXML private Label lblPagina;
    @FXML private Label lblTotalDebe;
    @FXML private Label lblTotalHaber;
    @FXML private StackPane pnDocumento;
    @FXML private TableView<DetallesPartidaModel> tbpartida;
    @FXML private TextField txtDebe;
    @FXML private TextArea txtDocumento;
    @FXML private DatePicker txtFecha;
    @FXML private TextField txtHaber;
    @FXML private TextArea txtConcepto;


    private String TipoDocumento;
    private String DireccionDocumento;
    private String NombreDocumento;
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    private int paginaActual = 1;
    private int totalPaginas = 1;
    private double escala = 1.0;
    private final double ESCALA_MINIMA = 0.2;
    private final double ESCALA_MAXIMA = 3.0;
    private final double FACTOR_ZOOM = 1.25;

    private ObservableList<DetallesPartidaModel> listaDetalles = FXCollections.observableArrayList();
    private Integer idUsuarioActual = 1; // Cambiar según tu sistema de sesión

    public void initialize() {
        configurarTabla();
        configurarEventos();
        cargarCuentas();
        txtFecha.setValue(LocalDate.now());
        actualizarTotales();
    }

    private void configurarTabla() {
        clCodigo.setCellValueFactory(new PropertyValueFactory<>("id_cuenta"));
        clCuenta.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        clDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        clHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));

        // Configurar columna Editar
        clEditar.setCellFactory(e -> new TableCell<>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Image image = new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/pen.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    imageView.setPreserveRatio(true);

                    Button btnEditar = new Button("", imageView);
                    btnEditar.setStyle("-fx-background-color: #2C3539;");
                    btnEditar.setContentDisplay(ContentDisplay.CENTER);

                    btnEditar.setOnAction(ev -> {
                        DetallesPartidaModel detalle = getTableView().getItems().get(getIndex());
                        editarDetalle(detalle);
                    });
                    setGraphic(btnEditar);
                }
            }
        });

        // Configurar columna Borrar
        clBorrar.setCellFactory(e -> new TableCell<>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Image image = new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/remove.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    imageView.setPreserveRatio(true);

                    Button btnEliminar = new Button("", imageView);
                    btnEliminar.setStyle("-fx-background-color: #2C3539;");
                    btnEliminar.setContentDisplay(ContentDisplay.CENTER);

                    btnEliminar.setOnAction(ev -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea eliminar este detalle?");
                        Optional<ButtonType> respuesta = alert.showAndWait();
                        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                            listaDetalles.remove(getIndex());
                            actualizarTotales();
                        }
                    });
                    setGraphic(btnEliminar);
                }
            }
        });

        tbpartida.setItems(listaDetalles);
    }

    private void configurarEventos() {
        btnAdjuntar.setOnAction(e -> {
            try {
                cargarPdf();
            } catch (IOException ex) {
                mostrarError("Error al cargar documento", ex.getMessage());
            }
        });

        btnAgregar.setOnAction(e -> agregarDetalle());
        btnGuardarTodo.setOnAction(e -> guardarPartida());
        btnCancelar.setOnAction(e -> cancelar());

        btnAnterior.setOnAction(e -> mostrarPagina2(paginaActual - 1));
        btnSiguiente.setOnAction(e -> mostrarPagina2(paginaActual + 1));

        btnzoommas.setOnAction(e -> {
            if (escala * FACTOR_ZOOM <= ESCALA_MAXIMA) {
                escala *= FACTOR_ZOOM;
                mostrarPagina2(paginaActual);
            }
        });

        btnzoommenos.setOnAction(e -> {
            if (escala / FACTOR_ZOOM >= ESCALA_MINIMA) {
                escala /= FACTOR_ZOOM;
                mostrarPagina2(paginaActual);
            }
        });

        actualizarBotones();
    }

    private void cargarCuentas() {
        try (Connection conn = ConexionDB.connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id_cuenta, codigo_cuenta, nombre_cuenta " +
                             "FROM tblCuentasContables " +
                             "WHERE estado = TRUE AND nivel = 4 " +
                             "ORDER BY codigo_cuenta")) {

            ObservableList<String> cuentas = FXCollections.observableArrayList();
            while (rs.next()) {
                String item = rs.getInt("id_cuenta") + " - " +
                        rs.getString("codigo_cuenta") + " - " +
                        rs.getString("nombre_cuenta");
                cuentas.add(item);
            }
            cmbCuenta.setItems(cuentas);

        } catch (Exception ex) {
            mostrarError("Error al cargar cuentas", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void agregarDetalle() {
        try {
            // Validaciones
            if (cmbCuenta.getValue() == null || cmbCuenta.getValue().isEmpty()) {
                mostrarAdvertencia("Debe seleccionar una cuenta");
                return;
            }

            String debeStr = txtDebe.getText().trim();
            String haberStr = txtHaber.getText().trim();

            if (debeStr.isEmpty() && haberStr.isEmpty()) {
                mostrarAdvertencia("Debe ingresar un valor en Debe o Haber");
                return;
            }

            Double debe = debeStr.isEmpty() ? 0.0 : Double.parseDouble(debeStr);
            Double haber = haberStr.isEmpty() ? 0.0 : Double.parseDouble(haberStr);

            if (debe > 0 && haber > 0) {
                mostrarAdvertencia("No puede tener valores en Debe y Haber simultáneamente");
                return;
            }

            if (debe < 0 || haber < 0) {
                mostrarAdvertencia("Los valores no pueden ser negativos");
                return;
            }

            // Extraer id_cuenta
            String[] partes = cmbCuenta.getValue().split(" - ");
            Integer idCuenta = Integer.parseInt(partes[0]);
            System.out.println("cuenta: "+ idCuenta);
            String descripcionCuenta = partes[2];

            // Crear detalle - usando el constructor correcto
            DetallesPartidaModel detalle = new DetallesPartidaModel(
                    null,              // id_detalle (null porque es nuevo)
                    null,              // id_partida (se asigna al guardar)
                    idCuenta,          // id_cuenta
                    null,
                    descripcionCuenta, // descripcion
                    debe,              // debe
                    haber              // haber
            );

            listaDetalles.add(detalle);
            limpiarCamposDetalle();
            actualizarTotales();

        } catch (NumberFormatException ex) {
            mostrarError("Error", "Ingrese valores numéricos válidos");
        }
    }

    private void editarDetalle(DetallesPartidaModel detalle) {
        // Cargar datos en los campos
        for (int i = 0; i < cmbCuenta.getItems().size(); i++) {
            if (cmbCuenta.getItems().get(i).startsWith(detalle.getId_cuenta() + " - ")) {
                cmbCuenta.getSelectionModel().select(i);
                break;
            }
        }

        txtDebe.setText(detalle.getDebe() > 0 ? String.valueOf(detalle.getDebe()) : "");
        txtHaber.setText(detalle.getHaber() > 0 ? String.valueOf(detalle.getHaber()) : "");

        // Eliminar el detalle actual (se volverá a agregar al presionar "Agregar")
        listaDetalles.remove(detalle);
        actualizarTotales();
    }

    private void guardarPartida() {
        try {
            // Validaciones
            if (txtFecha.getValue() == null) {
                mostrarAdvertencia("Debe seleccionar una fecha");
                return;
            }

            if (txtConcepto == null || txtConcepto.getText().trim().isEmpty()) {
                mostrarAdvertencia("Debe ingresar un concepto");
                return;
            }

            if (listaDetalles.isEmpty()) {
                mostrarAdvertencia("Debe agregar al menos un detalle");
                return;
            }

            double totalDebe = calcularTotalDebe();
            double totalHaber = calcularTotalHaber();

            if (Math.abs(totalDebe - totalHaber) > 0.01) {
                mostrarAdvertencia("La partida no está cuadrada.\nDebe: $" + String.format("%.2f", totalDebe) +
                        "\nHaber: $" + String.format("%.2f", totalHaber));
                return;
            }

            // Validar que se haya adjuntado un documento
            if ((document == null || totalPaginas == 0) && (txtDocumento == null || txtDocumento.getText().trim().isEmpty())) {
                mostrarAdvertencia("Debe adjuntar un documento de respaldo (PDF o TXT) para registrar la partida.");
                return;
            }

            Connection conn = ConexionDB.connection();
            conn.setAutoCommit(false);

            try {
                // Obtener siguiente número de partida
                Integer numeroPartida = PartidasModel.obtenerSiguienteNumeroPartida(conn);

                // Crear y guardar partida
                PartidasModel partida = new PartidasModel();
                partida.setId_usuario(idUsuarioActual);
                partida.setNumero_partida(numeroPartida);
                partida.setFecha(txtFecha.getValue().toString());
                partida.setConcepto(txtConcepto.getText().trim());

                Integer idPartida = partida.insertar(conn);

                DocumentoFuenteModel documentoFuenteModel = new DocumentoFuenteModel();
                documentoFuenteModel.setNombre_documento(this.NombreDocumento);
                documentoFuenteModel.setDescripcion(this.DireccionDocumento);
                documentoFuenteModel.setTipo_documento(this.TipoDocumento);

                Integer idDocumento = documentoFuenteModel.insertar();

                // Guardar detalles
                for (DetallesPartidaModel detalle : listaDetalles) {
                    detalle.setId_partida(idPartida);
                    detalle.setId_usuario(idUsuarioActual);
                    detalle.setId_documento(idDocumento);
                    detalle.insertar(conn);
                }

                conn.commit();
                mostrarExito("Partida guardada exitosamente\nNúmero de partida: " + numeroPartida);
                limpiarFormulario();

            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception ex) {
            mostrarError("Error al guardar partida", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea cancelar? Se perderán los datos no guardados");
        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtFecha.setValue(LocalDate.now());
        if (txtConcepto != null) {
            txtConcepto.clear();
        }
        listaDetalles.clear();
        limpiarCamposDetalle();
        actualizarTotales();

        // Limpiar documento
        if (pnDocumento != null) {
            pnDocumento.getChildren().clear();
        }
        if (txtDocumento != null) {
            txtDocumento.clear();
        }
    }

    private void limpiarCamposDetalle() {
        cmbCuenta.getSelectionModel().clearSelection();
        txtDebe.clear();
        txtHaber.clear();
    }

    private void actualizarTotales() {
        double totalDebe = calcularTotalDebe();
        double totalHaber = calcularTotalHaber();

        lblTotalDebe.setText(String.format("$%.2f", totalDebe));
        lblTotalHaber.setText(String.format("$%.2f", totalHaber));

        // Cambiar color si no están balanceados
        if (Math.abs(totalDebe - totalHaber) > 0.01) {
            lblTotalDebe.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            lblTotalHaber.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblTotalDebe.setStyle("-fx-text-fill: #00401b; -fx-font-weight: bold;");
            lblTotalHaber.setStyle("-fx-text-fill: #00401b; -fx-font-weight: bold;");
        }
    }

    private double calcularTotalDebe() {
        return listaDetalles.stream()
                .mapToDouble(d -> d.getDebe() != null ? d.getDebe() : 0.0)
                .sum();
    }

    private double calcularTotalHaber() {
        return listaDetalles.stream()
                .mapToDouble(d -> d.getHaber() != null ? d.getHaber() : 0.0)
                .sum();
    }

    private void cargarPdf() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
        );

        Window ventana = btnAdjuntar.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(ventana);
        if (archivo == null) return;

        String nombre = archivo.getName().toLowerCase();

        try {
            // GUARDAR ARCHIVO EN CARPETA archivos/
            String carpetaDestino = "src/main/resources/org/example/proyectosistemascontables/Documentos/";
            File dirDestino = new File(carpetaDestino);
            if (!dirDestino.exists()) {
                dirDestino.mkdirs(); // Crear carpeta si no existe
            }

            // Evitar nombres duplicados agregando timestamp
            String nombreBase = archivo.getName();
            String extension = "";
            int punto = nombreBase.lastIndexOf('.');
            if (punto > 0) {
                extension = nombreBase.substring(punto);
                nombreBase = nombreBase.substring(0, punto);
            }

            String nombreFinal = nombreBase + "_" + System.currentTimeMillis() + extension;
            File destino = new File(dirDestino, nombreFinal);
            this.NombreDocumento = nombreFinal;

            // Copiar archivo al destino
            Files.copy(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Obtener la ruta absoluta donde se guardó el archivo
            String rutaGuardada = destino.getAbsolutePath();
            System.out.println("Archivo guardado en: " + rutaGuardada);
            this.DireccionDocumento = rutaGuardada;
            // =============================================

            pnDocumento.getChildren().clear();
            txtDocumento.clear();

            if (nombre.endsWith(".txt")) {
                this.TipoDocumento = ".txt";
                String contenido = Files.readString(destino.toPath()); // Leer desde copia
                txtDocumento.setText(contenido);
                txtDocumento.setVisible(true);
                pnDocumento.setVisible(false);

            } else if (nombre.endsWith(".pdf")) {
                this.TipoDocumento = ".PDF";
                pnDocumento.setVisible(true);
                txtDocumento.setVisible(false);

                if (document != null) {
                    document.close();
                }

                document = PDDocument.load(destino); // Cargar PDF desde copia
                totalPaginas = document.getNumberOfPages();
                pdfRenderer = new PDFRenderer(document);
                paginaActual = 0;
                escala = 1.0; // Reiniciar escala
                mostrarPagina2(paginaActual);
            }

        } catch (Exception e) {
            mostrarError("Error al cargar archivo", e.getMessage());
            e.printStackTrace();
        }
    }


//    private void cargarPdf() throws IOException {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Seleccionar archivo");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"),
//                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
////                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
//        );
//
//        Window ventana = btnAdjuntar.getScene().getWindow();
//        File archivo = fileChooser.showOpenDialog(ventana);
//        if (archivo == null) return;
//
//        String nombre = archivo.getName().toLowerCase();
//
//        try {
//            pnDocumento.getChildren().clear();
//            txtDocumento.clear();
//
//            if (nombre.endsWith(".txt")) {
//                this.TipoDocumento = ".txt";
//                String contenido = Files.readString(archivo.toPath());
//                txtDocumento.setText(contenido);
//                txtDocumento.setVisible(true);
//                pnDocumento.setVisible(false);
//
//            } else if (nombre.endsWith(".pdf")) {
//                this.TipoDocumento = ".PDF";
//                pnDocumento.setVisible(true);
//                txtDocumento.setVisible(false);
//
//                if (document != null) {
//                    document.close();
//                }
//
//                document = PDDocument.load(archivo);
//                totalPaginas = document.getNumberOfPages();
//                pdfRenderer = new PDFRenderer(document);
//                paginaActual = 0;
//                escala = 1.0; // Reiniciar escala
//                mostrarPagina2(paginaActual);
//            }
//        } catch (Exception e) {
//            mostrarError("Error al cargar archivo", e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void mostrarPagina2(int numero) {
        if (document == null) return;
        if (numero < 0 || numero >= totalPaginas) return;

        try {
            paginaActual = numero;

            BufferedImage buffered = pdfRenderer.renderImage(paginaActual, (float) escala);
            WritableImage fxImage = SwingFXUtils.toFXImage(buffered, null);

            ImageView imageView = new ImageView(fxImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(fxImage.getWidth());
            imageView.setFitHeight(fxImage.getHeight());

            pnDocumento.getChildren().clear();
            pnDocumento.getChildren().add(imageView);

            lblPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);
            actualizarBotones();
            btnzoommas.setDisable(escala >= ESCALA_MAXIMA);
            btnzoommenos.setDisable(escala <= ESCALA_MINIMA);

        } catch (IOException ex) {
            mostrarError("Error al mostrar página", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarBotones() {
        btnAnterior.setDisable(paginaActual <= 0);
        btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para establecer el usuario actual
    public void setIdUsuarioActual(Integer idUsuario) {
        this.idUsuarioActual = idUsuario;
    }

    // Método para limpiar recursos al cerrar
    public void cerrarRecursos() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}