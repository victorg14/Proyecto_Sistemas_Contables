package org.example.proyectosistemascontables;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("AURUM FINANCE");
        try {
            stage.getIcons().add(new Image(
                    HelloApplication.class.getResourceAsStream("/org/example/proyectosistemascontables/img/aurum.png")
            ));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono de la aplicación");
        }

        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}