package com.example;

import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class PersonaController {

    @FXML
     TableView tableView;  

     @FXML
     private TableColumn<Persona, Integer>id;    
 
     @FXML
     private TableColumn<Persona, String> nombre;   
 
     @FXML
     private TableColumn<Persona, String> apellido1;

     @FXML
     private TableColumn<Persona, String>apellido2;    
 
     
     
     @FXML
     private void persona() throws IOException {
        App.setRoot("persona");
    }


     private ObservableList<Persona> listaPersona = FXCollections.observableArrayList();
 
     // En initialize() se inicializa el TableView y se asocia a la lista de usuarios.
     // ¡Esto solo hay que hacerlo una vez!
     public void initialize() {
         // Inicializamos las columnas de la tabla.
         id.setCellValueFactory(new PropertyValueFactory<>("id"));
         nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
         apellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
         apellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        
 
         
         tableView.setItems(listaPersona);
         loadData();    
     }
    private void loadData() {
         Persona.getAll(listaPersona);   // Llamamos al modelo Usuario
         // Como tenemos el TableView asociado a la ObservableList usuarios, el TableView se actualizará automáticamente
         // al cambiar la lista de usuarios sin necesidad de hacer tableView.setItems(usuarios).
         // Si usuarios fuera un ArrayList convencional, habría que actualizar el tableView cada vez que cambiasen los datos de la lista.
     }
 
     
}
