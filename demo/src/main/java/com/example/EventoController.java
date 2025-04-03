package com.example;

import java.io.IOException;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node; // Importación correcta
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;

public class EventoController {

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
     private TableColumn<Evento, String> nombre_categoria;

     @FXML
    private TableColumn<Evento, Void> acciones;
     



     private ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
 
     // En initialize() se inicializa el TableView y se asocia a la lista de usuarios.
     // ¡Esto solo hay que hacerlo una vez!
     public void initialize() {

        
       

    
        acciones.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Evento, Void> call(final TableColumn<Evento, Void> param) {
                return new TableCell<>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnCancelar = new Button("✖");
    
                    {
                        btnGuardar.setOnAction(event -> {
                            Evento evento = getTableView().getItems().get(getIndex());
                            saveRow(evento); // Lógica para guardar los cambios
                        });
    
                        btnCancelar.setOnAction(event -> {
                            Evento evento = getTableView().getItems().get(getIndex());
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
         lugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
         fecha_inicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
         fecha_fin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
         nombre_categoria.setCellValueFactory(new PropertyValueFactory<>("nombre_categoria"));

         tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

 
         nombre.setCellFactory(TextFieldTableCell.forTableColumn());
         descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
         lugar.setCellFactory(TextFieldTableCell.forTableColumn());
         fecha_inicio.setCellFactory(TextFieldTableCell.forTableColumn());
         fecha_fin.setCellFactory(TextFieldTableCell.forTableColumn());

// Cargar los nombres de todas las categorías para el ComboBox
    ObservableList<String> nombresCategorias = FXCollections.observableArrayList();
    Categoria.getNombres(nombresCategorias); // Llama al método para cargar todas las categorías

    if (nombresCategorias == null || nombresCategorias.isEmpty()) {
        System.out.println("No hay nombres de categorías disponibles.");
    } else {
        // Configurar la columna nombre_categoria con un ComboBoxTableCell
        nombre_categoria.setCellFactory(ComboBoxTableCell.forTableColumn(nombresCategorias));

        // Manejar el evento onEditCommit
        nombre_categoria.setOnEditCommit(event -> {
            Evento evento = event.getRowValue();
            String nuevoNombreCategoria = event.getNewValue();

            // Buscar el ID de la categoría correspondiente al nombre seleccionado
            int nuevoIdCategoria = Categoria.getAll().stream()
                .filter(categoria -> categoria.getNombre().equals(nuevoNombreCategoria))
                .findFirst()
                .map(Categoria::getId)
                .orElse(-1);

            if (nuevoIdCategoria == -1) {
                System.out.println("Categoría no encontrada: " + nuevoNombreCategoria);
            } else {
                evento.setId_categoria(nuevoIdCategoria); // Actualizar el ID de la categoría en el objeto Evento
            }
        });
    }

    // Inicializar la columna nombre_categoria con los nombres de las categorías asociadas a los eventos
    ObservableList<String> nombresCategoriasEvento = FXCollections.observableArrayList();
    Categoria.getNombres1(nombresCategoriasEvento); // Llama al método para cargar las categorías de los eventos

    if (nombresCategoriasEvento == null || nombresCategoriasEvento.isEmpty()) {
        System.out.println("No hay categorías asociadas a los eventos.");
    } else {
        // Configurar la columna para mostrar los nombres iniciales de las categorías asociadas a los eventos
        nombre_categoria.setCellValueFactory(cellData -> {
            Evento evento = cellData.getValue();
            String nombreCategoria = nombresCategoriasEvento.stream()
                .filter(nombre -> nombre.equals(evento.getNombreCategoria()))
                .findFirst()
                .orElse("Sin categoría");
            return new SimpleStringProperty(nombreCategoria);
        });
    }


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
         tableView.setItems(listaEventos);
         loadData();    
     }
    private void loadData() {
         Evento.getAll(listaEventos);   // Llamamos al modelo Usuario
         // Como tenemos el TableView asociado a la ObservableList usuarios, el TableView se actualizará automáticamente
         // al cambiar la lista de usuarios sin necesidad de hacer tableView.setItems(usuarios).
         // Si usuarios fuera un ArrayList convencional, habría que actualizar el tableView cada vez que cambiasen los datos de la lista.
     }
     private void loadDataNombres() {
        Categoria.getNombres1(listaEventos);   // Llamamos al modelo Usuario
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
