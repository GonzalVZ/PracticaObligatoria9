package com.example;

import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class EventoController {

    @FXML
     TableView tableView;  

     @FXML
     private TableColumn<Evento, Integer>id;    
 
     @FXML
     private TableColumn<Evento, String> nombre;   
 
     @FXML
     private TableColumn<Evento, String> descripcion;

     @FXML
     private TableColumn<Evento, String>lugar;    
 
     @FXML
     private TableColumn<Evento, String> fecha_inicio;   
 
     @FXML
     private TableColumn<Evento, String> fecha_fin;

     @FXML
     private TableColumn<Evento, Integer> id_categoria;

     
     



     private ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
 
     // En initialize() se inicializa el TableView y se asocia a la lista de usuarios.
     // ¡Esto solo hay que hacerlo una vez!
     public void initialize() {
        tableView.setEditable(true);



         // Inicializamos las columnas de la tabla.
         id.setCellValueFactory(new PropertyValueFactory<>("id"));
         nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
         descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
         lugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
         fecha_inicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
         fecha_fin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
         id_categoria.setCellValueFactory(new PropertyValueFactory<>("id_categoria"));

        
 
         // Hacemos que las columnas nick y email sean editables (no el id)
         nombre.setCellFactory(TextFieldTableCell.forTableColumn());
         descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
         lugar.setCellFactory(TextFieldTableCell.forTableColumn());
         fecha_inicio.setCellFactory(TextFieldTableCell.forTableColumn());
         fecha_fin.setCellFactory(TextFieldTableCell.forTableColumn());

            ObservableList<Integer> opcionesCategoria = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        id_categoria.setCellFactory(ComboBoxTableCell.forTableColumn(opcionesCategoria));

          id_categoria.setOnEditCommit(event -> {
          Evento evento = event.getRowValue();
          evento.setId_categoria(event.getNewValue());
});

 
         
         // Asignamos la ObservableList de usuarios al TableView. Así, cada vez que cambie la lista,
         // la vista se actualizará automáticamente.
         tableView.setItems(listaEventos);
         loadData();    
     }
    private void loadData() {
         Evento.getAll(listaEventos);   // Llamamos al modelo Usuario
         // Como tenemos el TableView asociado a la ObservableList usuarios, el TableView se actualizará automáticamente
         // al cambiar la lista de usuarios sin necesidad de hacer tableView.setItems(usuarios).
         // Si usuarios fuera un ArrayList convencional, habría que actualizar el tableView cada vez que cambiasen los datos de la lista.
     }
 
     @FXML
     public void addRow() throws IOException {
         // Creamos un usuario vacío
         Evento filaVacia = new Evento(Evento.getLastId()+1, "", "","","","",3);
 
         // Añadimos la fila vacía al ObservableList (esto lo añadirá también al TableView)
         listaEventos.add(filaVacia);
 
         // Seleccionamos la fila recién añadida y hacemos que sea editable
         tableView.getSelectionModel().select(filaVacia);
         tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
     }
 
 
     // Guarda un usuario en la base de datos
     public void saveRow(Evento evento) {
        evento.save();   // Llamamos al modelo Usuario
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
             Evento evento = (Evento) tableView.getSelectionModel().getSelectedItem();
             evento.delete();  // Lo borramos de la base de datos
             listaEventos.remove(evento);  // Lo borramos del ObservableList y del TableView
         }
     }
}
