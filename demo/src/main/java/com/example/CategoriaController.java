
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

public class CategoriaController {

    @FXML
     TableView tableView;  

     @FXML
     private TableColumn<Categoria, Integer>id;    
 
     @FXML
     private TableColumn<Categoria, String> nombre;   
 
     @FXML
     private TableColumn<Categoria, String> descripcion;

    


     private ObservableList<Categoria> listaCategoria = FXCollections.observableArrayList();
 
     // En initialize() se inicializa el TableView y se asocia a la lista de usuarios.
     // ¡Esto solo hay que hacerlo una vez!
     public void initialize() {
         // Inicializamos las columnas de la tabla.
         id.setCellValueFactory(new PropertyValueFactory<>("id"));
         nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
         descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
         
 
         // Hacemos que las columnas nick y email sean editables (no el id)
         nombre.setCellFactory(TextFieldTableCell.forTableColumn());
         descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
         

 
         // Asignamos un manejador de Categorias para cuando se editen las celdas de la tabla
         nombre.setOnEditCommit(event -> {
             // Cuando se edita el campo "nick", se guarda automáticamente
             Categoria Categoria = event.getRowValue();
             Categoria.setNombre(event.getNewValue()); // Actualiza el valor de la propiedad 'nick'
             saveRow(Categoria);                  // Actualiza la fila en la base de datos
         });        
         descripcion.setOnEditCommit(event -> {
            // Cuando se edita el campo "nick", se guarda automáticamente
            Categoria Categoria = event.getRowValue();
            Categoria.setDescripcion(event.getNewValue()); // Actualiza el valor de la propiedad 'nick'
            saveRow(Categoria);                  // Actualiza la fila en la base de datos
        });   
          
         // Asignamos la ObservableList de usuarios al TableView. Así, cada vez que cambie la lista,
         // la vista se actualizará automáticamente.
         tableView.setItems(listaCategoria);
         loadData();    
     }
    private void loadData() {
         Categoria.getAll(listaCategoria);   // Llamamos al modelo Usuario
         // Como tenemos el TableView asociado a la ObservableList usuarios, el TableView se actualizará automáticamente
         // al cambiar la lista de usuarios sin necesidad de hacer tableView.setItems(usuarios).
         // Si usuarios fuera un ArrayList convencional, habría que actualizar el tableView cada vez que cambiasen los datos de la lista.
     }
 
     @FXML
     public void addRow() throws IOException {
         // Creamos un usuario vacío
         Categoria filaVacia = new Categoria(Categoria.getLastId()+1, "", "");
 
         // Añadimos la fila vacía al ObservableList (esto lo añadirá también al TableView)
         listaCategoria.add(filaVacia);
 
         // Seleccionamos la fila recién añadida y hacemos que sea editable
         tableView.getSelectionModel().select(filaVacia);
         tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
     }
 
 
     // Guarda un usuario en la base de datos
     public void saveRow(Categoria Categoria) {
        Categoria.save();   // Llamamos al modelo Usuario
     }    
 
     // Elimina un usuario de la base de datos y del TableView
     @FXML
     public void deleteRow() {
         // Pedimos confirmación con un Alert antes de continuar
         Alert a = new Alert(AlertType.CONFIRMATION);
         a.setTitle("Confirmación");
         a.setHeaderText("¿Estás seguro de que quieres borrar este usuario?");
         Optional<ButtonType> result = a.showAndWait();
         if (result.get() == ButtonType.OK) {
             // Obtenemos el usuario seleccionado
             Categoria Categoria = (Categoria) tableView.getSelectionModel().getSelectedItem();
             Categoria.delete();  // Lo borramos de la base de datos
             listaCategoria.remove(Categoria);  // Lo borramos del ObservableList y del TableView
         }
     }
}
