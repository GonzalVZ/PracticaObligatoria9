package com.example;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Controlador para los botones de navegación principal de la aplicación.
 * Gestiona la transición entre las distintas vistas de la aplicación mediante
 * botones incluidos en un menú de navegación común.
 * 
 * <p>Este controlador es utilizado como parte de una vista modular incluida mediante
 * fx:include en todas las pantallas principales, proporcionando una navegación
 * consistente a lo largo de la aplicación.</p>
 * 
 * @author Jesús
 * @version 1.0
 */
public class BotonesController {

    /**
     * Navega a la vista de gestión de eventos.
     * Esta vista permite crear, editar, eliminar y visualizar eventos.
     * 
     * @throws IOException Si hay un problema al cargar la vista
     */
    @FXML
    private void evento() throws IOException {
        App.setRoot("evento");
    }
    
    /**
     * Navega a la vista de gestión de categorías.
     * Esta vista permite crear, editar, eliminar y visualizar categorías
     * a las que pueden pertenecer los eventos.
     * 
     * @throws IOException Si hay un problema al cargar la vista
     */
    @FXML
    private void categoria() throws IOException {
        App.setRoot("categoria");
    }
    
    /**
     * Navega a la vista de gestión de participantes.
     * Esta vista permite crear, editar, eliminar y visualizar participantes,
     * así como gestionar su inscripción en eventos.
     * 
     * @throws IOException Si hay un problema al cargar la vista
     */
    @FXML
    private void participante() throws IOException {
        App.setRoot("participante");
    }
    
    /**
     * Navega a la vista de gestión de artistas.
     * Esta vista permite crear, editar, eliminar y visualizar artistas,
     * así como gestionar su asignación a eventos.
     * 
     * @throws IOException Si hay un problema al cargar la vista
     */
    @FXML
    private void artista() throws IOException {
        App.setRoot("artista");
    }  
}