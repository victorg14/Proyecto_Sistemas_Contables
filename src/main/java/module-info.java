module org.example.proyectosistemascontables {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires javafx.media;
    requires org.apache.pdfbox;
    requires OpenViewerFX;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;

    // extrae la libreria itex7
    requires itextpdf;


    opens org.example.proyectosistemascontables.modelos to javafx.base;
    opens org.example.proyectosistemascontables to javafx.fxml;
    exports org.example.proyectosistemascontables;
}