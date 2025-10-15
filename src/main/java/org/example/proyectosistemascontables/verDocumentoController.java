package org.example.proyectosistemascontables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.proyectosistemascontables.modelos.DocumentoFuenteModel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class verDocumentoController {
    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnSiguiente;

    @FXML
    private Button btnzoommas;

    @FXML
    private Button btnzoommenos;

    @FXML
    private Label lblPagina;

    @FXML
    private StackPane pnDocumento;

    @FXML
    private TextArea txtDocumento;

    @FXML
    private Label lblIDdocumento;
    @FXML
    private Button btnRegresar;

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

    public void setIdDocumento(int id) {
        // Mostrar el dato recibido
        DocumentoFuenteModel modelo = new DocumentoFuenteModel();
        ObservableList<DocumentoFuenteModel> datos = modelo.getDocumento(id);
        this.DireccionDocumento = datos.get(0).getDescripcion();
        File ruta = new File(this.DireccionDocumento);
        mostrarDocumento(ruta);
    }

    public void initialize() {

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

        this.btnRegresar.setOnAction(e ->{
            // Obtener el Stage actual desde el botón
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.close();
        });


    }

    public void cargarDatos(){

    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarDocumento(File archivo) {
        if (archivo == null || !archivo.exists()) {
            mostrarError("Archivo no encontrado", "La ruta proporcionada no existe.");
            return;
        }

        String nombre = archivo.getName().toLowerCase();

        try {
            pnDocumento.getChildren().clear();
            txtDocumento.clear();

            if (nombre.endsWith(".txt")) {
                this.TipoDocumento = ".txt";
                String contenido = Files.readString(archivo.toPath());
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

                document = PDDocument.load(archivo);
                totalPaginas = document.getNumberOfPages();
                pdfRenderer = new PDFRenderer(document);
                paginaActual = 0;
                escala = 1.0;
                mostrarPagina2(paginaActual);
            } else {
                mostrarError("Tipo de archivo no compatible", "Solo se pueden abrir archivos .txt o .pdf");
            }

        } catch (IOException e) {
            mostrarError("Error al abrir el archivo", e.getMessage());
            e.printStackTrace();
        }
    }


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
}
