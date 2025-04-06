package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 * Configura la ventana principal y carga la interfaz de usuario desde archivos FXML.
 */
public class App extends Application {

    //--------------------------------------------------
    // VARIABLES ESTÁTICAS
    //--------------------------------------------------
    private static Scene scene;

    //--------------------------------------------------
    // MÉTODOS DE APLICACIÓN JAVAFX
    //--------------------------------------------------
    /**
     * Punto de entrada de la aplicación.
     * Configura la escena principal y muestra la ventana.
     * 
     * @param stage El escenario principal de la aplicación
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Carga la vista inicial desde el archivo FXML
        scene = new Scene(loadFXML("evento"));
        
        // Configura la ventana sin decoraciones (sin barra de título)
        stage.initStyle(StageStyle.UNDECORATED);
        
        // Establece la escena y muestra la ventana
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método principal que inicia la aplicación JavaFX.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        launch();
    }

    //--------------------------------------------------
    // MÉTODOS DE UTILIDAD PARA LA INTERFAZ
    //--------------------------------------------------
    /**
     * Cambia la vista raíz de la escena por otra vista FXML.
     * 
     * @param fxml Nombre del archivo FXML (sin la extensión .fxml)
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga un archivo FXML y devuelve su nodo raíz.
     * 
     * @param fxml Nombre del archivo FXML (sin la extensión .fxml)
     * @return El nodo raíz del FXML cargado
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}