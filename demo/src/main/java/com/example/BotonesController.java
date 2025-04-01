package com.example;

import java.io.IOException;
import javafx.fxml.FXML;

public class BotonesController {

     
     @FXML
     private void evento() throws IOException {
        App.setRoot("evento");
    }
    @FXML
     private void categoria() throws IOException {
    
        App.setRoot("categoria");
    }
    @FXML
     private void participante() throws IOException {
        App.setRoot("participante");
    }
    @FXML
     private void artista() throws IOException {
        App.setRoot("artista");
    }  
}
