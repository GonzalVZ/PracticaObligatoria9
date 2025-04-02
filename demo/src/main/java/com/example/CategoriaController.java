
package com.example;

import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CategoriaController {

    @FXML
    private HBox barraTitulo;

    private double xOffset = 0;
    private double yOffset = 0;

     @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true); // Minimiza la ventana
    }

    @FXML
    private void maximizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false); // Restaura el tamaño original
        } else {
            stage.setMaximized(true); // Maximiza la ventana
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); // Cierra la ventana
    }

    @FXML
     TableView tableView;  

     @FXML
     private TableColumn<Categoria, Integer>id;    
 
     @FXML
     private TableColumn<Categoria, String> nombre;   
 
     @FXML
     private TableColumn<Categoria, String> descripcion;

     @FXML
     private TableColumn<Categoria, Void> acciones;
    


     private ObservableList<Categoria> listaCategoria = FXCollections.observableArrayList();
 
     // En initialize() se inicializa el TableView y se asocia a la lista de usuarios.
     // ¡Esto solo hay que hacerlo una vez!
     public void initialize() {

         acciones.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Categoria, Void> call(final TableColumn<Categoria, Void> param) {
                return new TableCell<>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnCancelar = new Button("✖");
    
                    {
                        btnGuardar.setOnAction(event -> {
                            Categoria categoria = getTableView().getItems().get(getIndex());
                            saveRow(categoria); // Lógica para guardar los cambios
                        });
    
                        btnCancelar.setOnAction(event -> {
                            Categoria categoria = getTableView().getItems().get(getIndex());
                            deleteRow(); // Lógica para cancelar los cambios
                        });
                    }
    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getSelectionModel().getSelectedIndex() != getIndex()) {
                            setGraphic(null); // Oculta los botones si la fila no está seleccionada
                        } else {
                            HBox hBox = new HBox(5, btnGuardar, btnCancelar);
                            setGraphic(hBox); // Muestra los botones si la fila está seleccionada
                        }
                    }
                };
            }
        });
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            tableView.refresh(); // Refresca el TableView para actualizar las celdas
        });
        tableView.setEditable(true);

         // Inicializamos las columnas de la tabla.
         id.setCellValueFactory(new PropertyValueFactory<>("id"));
         nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
         descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

         tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

 
         // Hacemos que las columnas nick y email sean editables (no el id)
         nombre.setCellFactory(TextFieldTableCell.forTableColumn());
         descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
         

 
         
        barraTitulo.setOnMousePressed(event -> {
            Stage stage = (Stage) barraTitulo.getScene().getWindow();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        barraTitulo.setOnMouseDragged(event -> {
            Stage stage = (Stage) barraTitulo.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
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
     public void saveRow(Categoria categoria) {
        categoria.save();   // Llamamos al modelo Usuario
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
             Categoria categoria = (Categoria) tableView.getSelectionModel().getSelectedItem();
             categoria.delete();  // Lo borramos de la base de datos
             listaCategoria.remove(categoria);  // Lo borramos del ObservableList y del TableView
         }
     }
}
