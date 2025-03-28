package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EventoController {

    @FXML
     TableView tableView;  

     @FXML
     private TableColumn<Evento, Integer>nombre;    
 
     @FXML
     private TableColumn<Evento, String> colNick;   
 
     @FXML
     private TableColumn<Evento, String> colEmail;
    
}
