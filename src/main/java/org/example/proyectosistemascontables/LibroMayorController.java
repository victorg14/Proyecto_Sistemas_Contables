package org.example.proyectosistemascontables;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.proyectosistemascontables.modelos.DetallesPartidaModel;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibroMayorController {
    @FXML
    private ScrollPane ScrollPane;

    @FXML
    private VBox vbtablas;

    @FXML
    private Button btnExportar;

    private ObservableList<String> cuentas;
    private Timestamp inicio;
    private Timestamp fin;

    public void initialize() {
        ScrollPane.setFitToWidth(true);
        ScrollPane.setFitToHeight(false);
        configurarExportacion();
    }

    public void setList(ObservableList<String> cuentas, Timestamp inicio, Timestamp fin){
        this.cuentas = cuentas;
        this.inicio = inicio;
        this.fin = fin;

        for (String nombre : cuentas) {
            double totalDebe = 0;
            double totalHaber = 0;
            double total = 0;

            TableView<DetallesPartidaModel> tableView = crearTabla(nombre);
            tableView.getStylesheets().add(
                    getClass().getResource("/org/example/proyectosistemascontables/style/style.css").toExternalForm()
            );
            tableView.setId("tb_"+nombre.toLowerCase());
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            ObservableList<DetallesPartidaModel> data = DetallesPartidaModel.getdetallesFiltroMayor(inicio, fin, nombre);
            tableView.setItems(data);

            for (DetallesPartidaModel item: data){
                totalDebe += item.getDebe();
                totalHaber += item.getHaber();
            }

            total = totalDebe - totalHaber;

            Label TipoTabla = new Label();
            TipoTabla.setStyle("-fx-font-family: 'System'; -fx-font-size: 14px; -fx-font-weight: bold;");

            Label totalLabel = new Label("Total");
            totalLabel.setStyle("-fx-font-family: 'System'; -fx-font-size: 14px; -fx-font-weight: bold;");
            Label valorLabel = new Label();
            if (total < 0){
                total = total*-1;
                valorLabel.setText("($"+total+")");
                TipoTabla.setText("Acreedor");
            }else {
                valorLabel.setText("$"+total);
                TipoTabla.setText("Deudor");
            }
            valorLabel.setStyle("-fx-font-family: 'System'; -fx-font-size: 14px; -fx-font-weight: bold;");
            valorLabel.setId("total_"+nombre.toLowerCase());

            Label nombreTabla = new Label(nombre);
            nombreTabla.setStyle("-fx-font-family: 'System'; -fx-font-size: 16px; -fx-font-weight: bold;");
            HBox nameBox = new HBox();
            nameBox.setPadding(new Insets(5, 0, 5, 0));
            nameBox.setSpacing(20);
            nameBox.setStyle("-fx-background-color: #FEFAE0; -fx-padding: 5; -fx-border-color: #d99201;");
            HBox.setHgrow(nombreTabla, Priority.ALWAYS);
            HBox.setHgrow(TipoTabla, Priority.ALWAYS);
            nombreTabla.setMaxWidth(Double.MAX_VALUE);
            TipoTabla.setMaxWidth(Double.MAX_VALUE);
            nombreTabla.setAlignment(Pos.CENTER_LEFT);
            TipoTabla.setAlignment(Pos.CENTER_RIGHT);
            nameBox.getChildren().addAll(nombreTabla, TipoTabla);

            HBox totalBox = new HBox();
            totalBox.setPadding(new Insets(5, 0, 5, 0));
            totalBox.setSpacing(20);
            totalBox.setStyle("-fx-background-color: #FEFAE0; -fx-padding: 5; -fx-border-color: #d99201;");
            HBox.setHgrow(totalLabel, Priority.ALWAYS);
            HBox.setHgrow(valorLabel, Priority.ALWAYS);
            totalLabel.setMaxWidth(Double.MAX_VALUE);
            valorLabel.setMaxWidth(Double.MAX_VALUE);
            totalLabel.setAlignment(Pos.CENTER_LEFT);
            valorLabel.setAlignment(Pos.CENTER_RIGHT);

            totalBox.getChildren().addAll(totalLabel, valorLabel);

            VBox.setMargin(totalBox, new Insets(0, 0, 20, 0));

            this.vbtablas.getChildren().addAll(nameBox, tableView, totalBox);
        }
    }

    private TableView<DetallesPartidaModel> crearTabla(String nombre) {
        TableView<DetallesPartidaModel> table = new TableView<>();
        table.setPrefHeight(300);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<DetallesPartidaModel, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha_creacion"));

        TableColumn<DetallesPartidaModel, String> colCuenta = new TableColumn<>("Cuenta");
        colCuenta.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<DetallesPartidaModel, Double> colDebe = new TableColumn<>("Debe");
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));

        TableColumn<DetallesPartidaModel, Double> colHaber = new TableColumn<>("Haber");
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));

        table.getColumns().addAll(colFecha, colCuenta, colDebe, colHaber);

        return table;
    }

    //  FUNCIONES DE EXPORTACIÓN A PDF

    private void configurarExportacion() {
        btnExportar.setOnAction(event -> exportarAPDF());
    }

    private void exportarAPDF() {
        if (cuentas == null || cuentas.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay datos para exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Libro Mayor");
        fileChooser.setInitialFileName("LibroMayor_" + LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf")
        );

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

    private void generarPDF(File archivo) throws Exception {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivo));

        documento.open();

        // Título del documento
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("LIBRO MAYOR", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        documento.add(titulo);

        // Información de fechas
        if (inicio != null && fin != null) {
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
            LocalDate fechaInicio = inicio.toLocalDateTime().toLocalDate();
            LocalDate fechaFinal = fin.toLocalDateTime().toLocalDate();
            String rangoFechas = "Período: " +
                    fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " - " + fechaFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Paragraph subtitulo = new Paragraph(rangoFechas, fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);
        }

        // Generar una tabla para cada cuenta
        for (String nombre : cuentas) {
            agregarTablaAlDocumento(documento, nombre);
        }

        // Pie de página
        Paragraph piePagina = new Paragraph("\nGenerado el: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY));
        piePagina.setAlignment(Element.ALIGN_RIGHT);
        documento.add(piePagina);

        documento.close();
    }

    private void agregarTablaAlDocumento(Document documento, String nombreCuenta) throws DocumentException {
        // Obtener datos para calcular el saldo
        ObservableList<DetallesPartidaModel> data = DetallesPartidaModel.getdetallesFiltroMayor(inicio, fin, nombreCuenta);
        double totalDebe = 0.0;
        double totalHaber = 0.0;

        for (DetallesPartidaModel detalle : data) {
            totalDebe += detalle.getDebe();
            totalHaber += detalle.getHaber();
        }

        double saldo = totalDebe - totalHaber;
        String tipoSaldo = saldo < 0 ? "Acreedor" : "Deudor";

        // Título de la cuenta con saldo
        Font fuenteNombreCuenta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Paragraph nombreCuentaPar = new Paragraph(nombreCuenta + " (" + tipoSaldo + ")", fuenteNombreCuenta);
        nombreCuentaPar.setSpacingBefore(15);
        nombreCuentaPar.setSpacingAfter(10);
        documento.add(nombreCuentaPar);

        // Crear tabla con 4 columnas
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5);
        tabla.setSpacingAfter(10);

        float[] columnWidths = {1.5f, 4f, 2f, 2f};
        tabla.setWidths(columnWidths);

        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font fuenteContenido = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

        String[] encabezados = {"Fecha", "Cuenta", "Debe ($)", "Haber ($)"};

        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
            celda.setBackgroundColor(new BaseColor(0, 64, 27));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(8);
            tabla.addCell(celda);
        }

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (DetallesPartidaModel detalle : data) {
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
            String debe = detalle.getDebe() > 0
                    ? String.format("$%.2f", detalle.getDebe()) : "-";
            PdfPCell celdaDebe = new PdfPCell(new Phrase(debe, fuenteContenido));
            celdaDebe.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaDebe.setPadding(5);
            tabla.addCell(celdaDebe);

            // Haber
            String haber = detalle.getHaber() > 0
                    ? String.format("$%.2f", detalle.getHaber()) : "-";
            PdfPCell celdaHaber = new PdfPCell(new Phrase(haber, fuenteContenido));
            celdaHaber.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaHaber.setPadding(5);
            tabla.addCell(celdaHaber);
        }

        // Fila de totales
        Font fuenteTotales = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

        PdfPCell celdaTotalLabel = new PdfPCell(new Phrase("TOTALES:", fuenteTotales));
        celdaTotalLabel.setColspan(2);
        celdaTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaTotalLabel.setPadding(8);
        celdaTotalLabel.setBackgroundColor(new BaseColor(254, 250, 224));
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
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}