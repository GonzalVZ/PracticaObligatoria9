package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class EventoController {

    // ------------------- VARIABLES Y CAMPOS FXML -------------------
    
    // Control de la ventana
    @FXML
    private HBox barraTitulo;
    private double xOffset = 0;
    private double yOffset = 0;

    // TableView y sus columnas
    @FXML
    TableView<Evento> tableView;  

    @FXML
    private TableColumn<Evento, Integer> id;    
 
    @FXML
    private TableColumn<Evento, String> nombre;   
 
    @FXML
    private TableColumn<Evento, String> descripcion;

    @FXML
    private TableColumn<Evento, String> lugar;    
 
    @FXML
    private TableColumn<Evento, String> fecha_inicio;   
 
    @FXML
    private TableColumn<Evento, String> fecha_fin;

    @FXML
    private TableColumn<Evento, String> nombre_categoria;

    @FXML
    private TableColumn<Evento, Void> acciones;
     
    // Listas de datos
    private ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
    private Map<Integer, String> mapaCategorias = new HashMap<>(); // Relación id_categoria -> nombre_categoria
    
    // ------------------- MÉTODOS DE CONTROL DE VENTANA -------------------
    
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
    
    // ------------------- INICIALIZACIÓN -------------------
    
    public void initialize() {
        // Precargar mapa de categorías
        precargaCategorias();
        
        // Configurar columnas del TableView
        configurarColumnas();
        
        // Configurar columna nombre_categoria
        configurarColumnaNombreCategoria();
        
        // Configurar columna acciones
        configurarColumnaAcciones();
        
        // Configurar movimiento de ventana
        configurarMovimientoVentana();
        
        // Configurar selección y edición
        configurarSeleccionYEdicion();
        
        // Cargar datos iniciales
        tableView.setItems(listaEventos);
        loadData();    
    }
    
    private void precargaCategorias() {
        // Precargar el mapa de categorías
        ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
        Categoria.getAll(listaCategorias);
        
        // Crear el mapa id_categoria -> nombre_categoria
        for (Categoria categoria : listaCategorias) {
            mapaCategorias.put(categoria.getId(), categoria.getNombre());
        }
    }
    
    private void configurarColumnas() {
        // Configurar cell value factories
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        lugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
        fecha_inicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
        fecha_fin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
        
        // Configurar cell factories para edición
        nombre.setCellFactory(TextFieldTableCell.forTableColumn());
        descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        lugar.setCellFactory(TextFieldTableCell.forTableColumn());
        fecha_inicio.setCellFactory(TextFieldTableCell.forTableColumn());
        fecha_fin.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Configurar política de redimensionamiento
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void configurarColumnaNombreCategoria() {
        // Cargar los nombres de todas las categorías para el ComboBox
        ObservableList<String> nombresCategorias = FXCollections.observableArrayList();
        Categoria.getNombres(nombresCategorias);
        
        // Configurar la columna para mostrar el nombre correcto de la categoría
        nombre_categoria.setCellValueFactory(cellData -> {
            Evento evento = cellData.getValue();
            String nombreCategoria = mapaCategorias.getOrDefault(evento.getId_categoria(), "Sin categoría");
            return new SimpleStringProperty(nombreCategoria);
        });
        
        // Configurar ComboBox para edición
        nombre_categoria.setCellFactory(ComboBoxTableCell.forTableColumn(nombresCategorias));
        
        // Configurar acción al editar
        nombre_categoria.setOnEditCommit(event -> {
            Evento evento = event.getRowValue();
            String nuevoNombreCategoria = event.getNewValue();
            
            // Buscar el ID de la categoría correspondiente al nombre seleccionado
            for (Map.Entry<Integer, String> entry : mapaCategorias.entrySet()) {
                if (entry.getValue().equals(nuevoNombreCategoria)) {
                    evento.setId_categoria(entry.getKey());
                    evento.save();
                    break;
                }
            }
        });
    }
    
    private void configurarColumnaAcciones() {
        acciones.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Evento, Void> call(final TableColumn<Evento, Void> param) {
                return new TableCell<>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnCancelar = new Button("✖");
    
                    {
                        btnGuardar.setOnAction(event -> {
                            Evento evento = getTableView().getItems().get(getIndex());
                            saveRow(evento);
                        });
    
                        btnCancelar.setOnAction(event -> {
                            Evento evento = getTableView().getItems().get(getIndex());
                            deleteRow();
                        });
                    }
    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getSelectionModel().getSelectedIndex() != getIndex()) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(5, btnGuardar, btnCancelar);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }
    
    private void configurarMovimientoVentana() {
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
    }
    
    private void configurarSeleccionYEdicion() {
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            tableView.refresh();
        });
        tableView.setEditable(true);
    }
    
    // ------------------- MÉTODOS DE GESTIÓN DE DATOS -------------------
    
    private void loadData() {
        listaEventos.clear();
        Evento.getAll(listaEventos);
    }
 
    @FXML
    public void addRow() throws IOException {
        // Creamos un evento vacío
        Evento filaVacia = new Evento(Evento.getLastId()+1, "", "","","","",0);
 
        // Añadimos la fila vacía al ObservableList
        listaEventos.add(filaVacia);
 
        // Seleccionamos la fila recién añadida y hacemos que sea editable
        tableView.getSelectionModel().select(filaVacia);
        tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
    }
     
    // Guarda un evento en la base de datos
    public void saveRow(Evento evento) {
        evento.save();
    }    
 
    // Elimina un evento de la base de datos y del TableView
    @FXML
    public void deleteRow() {
        // Pedimos confirmación con un Alert antes de continuar
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Confirmación");
        a.setHeaderText("¿Estás seguro de que quieres borrar este evento?");
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            // Obtenemos el evento seleccionado
            Evento evento = (Evento) tableView.getSelectionModel().getSelectedItem();
            evento.delete();  // Lo borramos de la base de datos
            listaEventos.remove(evento);  // Lo borramos del ObservableList y del TableView
        }
    }
}
