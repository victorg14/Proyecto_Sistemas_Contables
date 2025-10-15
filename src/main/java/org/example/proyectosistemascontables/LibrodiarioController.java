package org.example.proyectosistemascontables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.proyectosistemascontables.modelos.DetallesPartidaModel;
import org.example.proyectosistemascontables.modelos.UsuarioModel;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;

public class LibrodiarioController {

    private ContadorController contadorController;

    public void setContadorController(ContadorController controller) {
        this.contadorController = controller;
    }

    @FXML
    private AnchorPane AnchorMostrar;
    @FXML
    private Button btnBuscar;

    @FXML
    private TableColumn<DetallesPartidaModel, Button> btnDocumento;

    @FXML
    private Button btnExportar;

    @FXML
    private Button btnLibroMayor;

    @FXML
    private Button btnReiniciarFiltro;

    @FXML
    private TableColumn<DetallesPartidaModel, String> clCuenta;

    @FXML
    private TableColumn<DetallesPartidaModel, Double> clDebe;

    @FXML
    private TableColumn<DetallesPartidaModel, String> clFecha;

    @FXML
    private TableColumn<DetallesPartidaModel, Double> clHaber;

    @FXML
    private DatePicker txtFechaFinal;

    @FXML
    private DatePicker txtFechaInicio;
    @FXML
    private TableView<DetallesPartidaModel> tbLibroDiario;

    private Timestamp inicio = null;
    private Timestamp fin = null;
    private ObservableList<String> cuentas = FXCollections.observableArrayList();

    public void initialize() {

        this.clFecha.setCellValueFactory(new PropertyValueFactory<>("fecha_creacion"));
        this.clCuenta.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        this.clDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        this.clHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));

        this.btnDocumento.setCellFactory(column -> new TableCell<DetallesPartidaModel, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    // Crear botón con icono
                    Image image = new Image(getClass().getResourceAsStream(
                            "/org/example/proyectosistemascontables/img/pdf.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    imageView.setPreserveRatio(true);

                    Button btndocumento = new Button("", imageView);
                    btndocumento.setStyle("-fx-background-color: transparent;");
                    btndocumento.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btndocumento.setAlignment(Pos.CENTER);

                    btndocumento.setOnAction(e-> {
                        try {
                            // Cargar el FXML de la ventana emergente
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectosistemascontables/contador/verDocumento.fxml"));
                            Parent root = loader.load();

                            // Obtener el controlador de la nueva ventana
                            verDocumentoController controller = loader.getController();

                            // Extraer datos de la ventana actual y pasarlos
                            DetallesPartidaModel item = getTableView().getItems().get(getIndex());
                            int id = item.getId_documento();
                            controller.setIdDocumento(id);

                            // Crear la nueva ventana (Stage)
                            Stage stage = new Stage();
                            stage.setTitle("Documento Adjunto");
                            stage.setScene(new Scene(root));

                            // bloquea la ventana principal mientras la nueva esté abierta
                            stage.initModality(Modality.APPLICATION_MODAL);

                            // Establecer la ventana actual como padre de la nueva
                            stage.initOwner(btndocumento.getScene().getWindow());

                            // Mostrar la ventana y esperar hasta que se cierre
                            stage.showAndWait();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });

                    setGraphic(btndocumento);
                }
            }
        });

        this.btnReiniciarFiltro.setOnAction(e ->{
            // Limpiar los campos de fecha
            this.txtFechaInicio.setValue(null);
            this.txtFechaFinal.setValue(null);

            // Limpiar los filtros
            contadorController.TraerFiltros(null, null, null);
            this.cuentas = FXCollections.observableArrayList();
            this.inicio = null;
            this.fin = null;

            // Cargar todos los datos sin filtro
            cargarDatos();

            // Gestión de botones después de reiniciar filtro
            btnBuscar.setDisable(false);           // Habilitar búsqueda
            btnReiniciarFiltro.setDisable(true);   // Deshabilitar reiniciar filtro
            btnLibroMayor.setDisable(true);        // Deshabilitar libro mayor
            btnExportar.setDisable(true);          // Deshabilitar exportar
        });

        this.btnBuscar.setOnAction(e ->{
            // Validar que ambas fechas estén seleccionadas
            if (!validarFechas()) {
                return; // Si la validación falla, no continúa
            }

            this.inicio = Timestamp.valueOf(txtFechaInicio.getValue().atStartOfDay());
            this.fin = Timestamp.valueOf(txtFechaFinal.getValue().atTime(LocalTime.MAX));
            tbLibroDiario.setItems(DetallesPartidaModel.getdetallesFiltro(inicio, fin));

            // Limpiar y llenar la lista de cuentas
            this.cuentas.clear();
            for (DetallesPartidaModel model : tbLibroDiario.getItems()) {
                String desc = model.getDescripcion();
                if (!this.cuentas.contains(desc)) {
                    this.cuentas.add(desc);
                }
            }

            for (String item: this.cuentas){
                System.out.println(item);
            }

            contadorController.TraerFiltros(this.cuentas, this.inicio, this.fin);

            // Habilitar los botones de libro mayor y exportar si hay datos
            boolean hayDatos = !tbLibroDiario.getItems().isEmpty();

            // Gestión de botones después de aplicar filtro
            btnLibroMayor.setDisable(!hayDatos);      // Habilitar/deshabilitar según haya datos
            btnExportar.setDisable(!hayDatos);        // Habilitar/deshabilitar según haya datos
            btnReiniciarFiltro.setDisable(!hayDatos); // Habilitar si hay datos filtrados
            btnBuscar.setDisable(hayDatos);           // Deshabilitar búsqueda si hay datos
        });

        this.btnLibroMayor.setOnAction(e ->{
            try {
                // Cargar el archivo FXML de Scene2 y pasar el dato
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Contador/libroMayor.fxml"));
                Parent vista = loader.load();

                // Obtener el controlador de la segunda escena
                LibroMayorController controladorScene2 = loader.getController();

                if (this.cuentas != null){
                    controladorScene2.setList(this.cuentas, this.inicio, this.fin);
                }
                contadorController.activarBotonLMayor(4);

                // Reemplazar el contenido del AnchorPane
                AnchorMostrar.getChildren().setAll(vista);

                // Opcional: Anclar la vista cargada a todos los bordes
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

            } catch (Exception er) {
                er.printStackTrace();
            }
        });

        // Configurar el evento del botón de exportar
        configurarExportacion();

        // Estado inicial de los botones al cargar la vista
        btnBuscar.setDisable(false);           // Habilitar búsqueda al inicio
        btnReiniciarFiltro.setDisable(true);   // Deshabilitar reiniciar filtro al inicio
        btnLibroMayor.setDisable(true);        // Deshabilitar libro mayor al inicio
        btnExportar.setDisable(true);          // Deshabilitar exportar al inicio
    }

    /**
     * Valida que ambas fechas estén seleccionadas en los DatePicker
     * retornara un true si ambas fechas están seleccionadas, false en caso contrario
     */
    private boolean validarFechas() {
        if (txtFechaInicio.getValue() == null || txtFechaFinal.getValue() == null) {
            mostrarAlerta("Validación requerida",
                    "No se puede realizar un filtro sin seleccionar ambas fechas.\nPor favor, selecciona la fecha de inicio y la fecha final.",
                    Alert.AlertType.WARNING);
            return false;
        }

        // Validación adicional: verificar que la fecha inicial no sea mayor que la final
        if (txtFechaInicio.getValue().isAfter(txtFechaFinal.getValue())) {
            mostrarAlerta("Fechas inválidas",
                    "La fecha de inicio no puede ser posterior a la fecha final.\nPor favor, verifica los valores ingresados.",
                    Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void cargarDatos(){
        tbLibroDiario.setItems(DetallesPartidaModel.getdetalles());
    }

    public void traerDatos(ObservableList<String> cuentas, Timestamp inicio, Timestamp fin){
        this.cuentas = cuentas;
        this.inicio = inicio;
        this.fin = fin;
    }

    public void CargarDatosFiltrados(){

        if (this.inicio == null){
            cargarDatos();
            // Estado de botones sin filtro
            btnBuscar.setDisable(false);
            btnReiniciarFiltro.setDisable(true);
            btnLibroMayor.setDisable(true);
            btnExportar.setDisable(true);
        }else {
            LocalDate localDate = this.inicio.toLocalDateTime().toLocalDate();
            this.txtFechaInicio.setValue(localDate);
            LocalDate localDatefin = this.fin.toLocalDateTime().toLocalDate();
            this.txtFechaFinal.setValue(localDatefin);

            this.inicio = Timestamp.valueOf(txtFechaInicio.getValue().atStartOfDay());
            this.fin = Timestamp.valueOf(txtFechaFinal.getValue().atTime(LocalTime.MAX));
            tbLibroDiario.setItems(DetallesPartidaModel.getdetallesFiltro(inicio, fin));

            // Estado de botones con filtro aplicado
            boolean hayDatos = !tbLibroDiario.getItems().isEmpty();
            btnBuscar.setDisable(hayDatos);
            btnReiniciarFiltro.setDisable(!hayDatos);
            btnLibroMayor.setDisable(!hayDatos);
            btnExportar.setDisable(!hayDatos);
        }

    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);

        // Seleccionar el icono según el tipo de alerta
        String rutaIcono = "";
        switch (tipo) {
            case INFORMATION:
                rutaIcono = "/org/example/proyectosistemascontables/img/comprobado.png";
                break;
            case WARNING:
                rutaIcono = "/org/example/proyectosistemascontables/img/error.png";
                break;
            case ERROR:
                rutaIcono = "/org/example/proyectosistemascontables/img/error.png";
                break;
            case CONFIRMATION:
                rutaIcono = "/org/example/proyectosistemascontables/img/comprobado.png";
                break;
        }

        // Configurar el icono de la alerta
        if (!rutaIcono.isEmpty()) {
            try {
                ImageView icono = new ImageView(
                        new Image(getClass().getResourceAsStream(rutaIcono))
                );
                icono.setFitWidth(25);
                icono.setFitHeight(25);
                icono.setPreserveRatio(true);
                alerta.setGraphic(icono);
            } catch (Exception e) {
                System.err.println("Error al cargar el icono: " + e.getMessage());
            }
        }

        // Configurar el icono de la ventana
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        try {
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/org/example/proyectosistemascontables/img/aurum.png"))
            );
        } catch (Exception e) {
            System.err.println("Error al cargar el icono de la ventana: " + e.getMessage());
        }

        alerta.showAndWait();
    }

    // funciones para exportar a pdf
    private void configurarExportacion() {
        btnExportar.setOnAction(event -> exportarAPDF());
    }

    private void exportarAPDF() {
        // Crear un FileChooser para que el usuario seleccione dónde guardar el PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Libro Diario");
        fileChooser.setInitialFileName("LibroDiario_" + LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf")
        );

        // Mostrar el diálogo y obtener el archivo seleccionado
        File archivo = fileChooser.showSaveDialog(btnExportar.getScene().getWindow());

        if (archivo != null) {
            try {
                generarPDF(archivo);
                mostrarAlerta("Éxito", "El archivo PDF se ha generado correctamente en:\n" + archivo.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo generar el PDF: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    //funcion para generar pdf
    private void generarPDF(File archivo) throws Exception {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivo));

        documento.open();

        // Título del documento
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("LIBRO DIARIO", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        documento.add(titulo);

        // Agregar información de fechas si hay filtro aplicado
        LocalDate fechaInicio = txtFechaInicio.getValue();
        LocalDate fechaFinal = txtFechaFinal.getValue();

        if (fechaInicio != null || fechaFinal != null) {
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
            String rangoFechas = "Período: ";

            if (fechaInicio != null && fechaFinal != null) {
                rangoFechas += fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " - " + fechaFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (fechaInicio != null) {
                rangoFechas += "Desde " + fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else {
                rangoFechas += "Hasta " + fechaFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            Paragraph subtitulo = new Paragraph(rangoFechas, fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);
        } else {
            Paragraph espacio = new Paragraph(" ");
            espacio.setSpacingAfter(10);
            documento.add(espacio);
        }

        // Crear tabla con 4 columnas
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10);
        tabla.setSpacingAfter(10);

        // Definir anchos de columnas
        float[] columnWidths = {1.5f, 4f, 2f, 2f};
        tabla.setWidths(columnWidths);

        // Fuentes para la tabla
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font fuenteContenido = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

        // Crear encabezados de la tabla
        String[] encabezados = {"Fecha", "Cuenta", "Debe ($)", "Haber ($)"};

        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
            celda.setBackgroundColor(new BaseColor(0, 64, 27)); // Color #00401b
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(8);
            tabla.addCell(celda);
        }

        // Variables para calcular totales
        double totalDebe = 0.0;
        double totalHaber = 0.0;

        // Agregar los datos de la tabla (solo los visibles/filtrados)
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (DetallesPartidaModel detalle : tbLibroDiario.getItems()) {
            // Fecha
            String fecha = "";
            if (detalle.getFecha_creacion() != null) {
                fecha = detalle.getFecha_creacion().toLocalDateTime().toLocalDate()
                        .format(formatoFecha);
            }
            PdfPCell celdaFecha = new PdfPCell(new Phrase(fecha, fuenteContenido));
            celdaFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaFecha.setPadding(5);
            tabla.addCell(celdaFecha);

            // Cuenta
            PdfPCell celdaCuenta = new PdfPCell(new Phrase(detalle.getDescripcion(), fuenteContenido));
            celdaCuenta.setPadding(5);
            tabla.addCell(celdaCuenta);

            // Debe
            String debe = detalle.getDebe() != null && detalle.getDebe() > 0
                    ? String.format("$%.2f", detalle.getDebe()) : "-";
            PdfPCell celdaDebe = new PdfPCell(new Phrase(debe, fuenteContenido));
            celdaDebe.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaDebe.setPadding(5);
            tabla.addCell(celdaDebe);

            if (detalle.getDebe() != null) {
                totalDebe += detalle.getDebe();
            }

            // Haber
            String haber = detalle.getHaber() != null && detalle.getHaber() > 0
                    ? String.format("$%.2f", detalle.getHaber()) : "-";
            PdfPCell celdaHaber = new PdfPCell(new Phrase(haber, fuenteContenido));
            celdaHaber.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaHaber.setPadding(5);
            tabla.addCell(celdaHaber);

            if (detalle.getHaber() != null) {
                totalHaber += detalle.getHaber();
            }
        }

        // Agregar fila de totales
        Font fuenteTotales = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

        PdfPCell celdaTotalLabel = new PdfPCell(new Phrase("TOTALES:", fuenteTotales));
        celdaTotalLabel.setColspan(2);
        celdaTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaTotalLabel.setPadding(8);
        celdaTotalLabel.setBackgroundColor(new BaseColor(254, 250, 224)); // Color #FEFAE0
        tabla.addCell(celdaTotalLabel);

        PdfPCell celdaTotalDebe = new PdfPCell(new Phrase(String.format("$%.2f", totalDebe), fuenteTotales));
        celdaTotalDebe.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaTotalDebe.setPadding(8);
        celdaTotalDebe.setBackgroundColor(new BaseColor(254, 250, 224));
        tabla.addCell(celdaTotalDebe);

        PdfPCell celdaTotalHaber = new PdfPCell(new Phrase(String.format("$%.2f", totalHaber), fuenteTotales));
        celdaTotalHaber.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaTotalHaber.setPadding(8);
        celdaTotalHaber.setBackgroundColor(new BaseColor(254, 250, 224));
        tabla.addCell(celdaTotalHaber);

        documento.add(tabla);

        // Pie de página
        Paragraph piePagina = new Paragraph("\nGenerado el: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY));
        piePagina.setAlignment(Element.ALIGN_RIGHT);
        documento.add(piePagina);

        documento.close();
    }

}