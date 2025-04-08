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

/**
 * Controlador para la vista de gestión de categorías.
 * Maneja la interfaz de usuario para crear, editar y eliminar categorías.
 */
public class CategoriaController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    @FXML
    private HBox barraTitulo;

    @FXML
    private TableView<Categoria> tableView;  // Corregido: se agrega el tipo genérico

      
 
    @FXML
    private TableColumn<Categoria, String> nombre;   
 
    @FXML
    private TableColumn<Categoria, String> descripcion;

    @FXML
    private TableColumn<Categoria, Void> acciones;
    
    //--------------------------------------------------
    // VARIABLES
    //--------------------------------------------------
    private double xOffset = 0;
    private double yOffset = 0;
    private ObservableList<Categoria> listaCategoria = FXCollections.observableArrayList();
 
    //--------------------------------------------------
    // MÉTODOS DE CONTROL DE VENTANA
    //--------------------------------------------------
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

    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    public void initialize() {
        // Configurar las columnas
        configurarColumnas();
        
        // Configurar la columna de acciones
        configurarColumnaAcciones();
        
        // Configurar selección y edición
        configurarSeleccionYEdicion();
        
        // Configurar movimiento de ventana
        configurarMovimientoVentana();
        
        // Asignar datos y cargar
        tableView.setItems(listaCategoria);
        loadData();    
    }
    
    private void configurarColumnas() {
        // Inicializamos las columnas de la tabla
        
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        
        // Hacer que las columnas sean editables
        nombre.setCellFactory(TextFieldTableCell.forTableColumn());
        descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Configurar política de redimensionamiento
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void configurarColumnaAcciones() {
        acciones.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Categoria, Void> call(final TableColumn<Categoria, Void> param) {
                return new TableCell<>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnCancelar = new Button("✖");
    
                    {
                        btnGuardar.setOnAction(event -> {
                            Categoria categoria = getTableView().getItems().get(getIndex());
                            saveRow(categoria);
                        });
    
                        btnCancelar.setOnAction(event -> {
                            deleteRow();
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
    }
    
    private void configurarSeleccionYEdicion() {
        // Actualizar cuando cambie la selección
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            tableView.refresh(); // Refresca el TableView para actualizar las celdas
        });
        
        // Permitir edición
        tableView.setEditable(true);
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

    //--------------------------------------------------
    // MANEJO DE DATOS
    //--------------------------------------------------
    private void loadData() {
        listaCategoria.clear(); // Limpiar lista antes de cargar
        Categoria.getAll(listaCategoria);
    }
 
    @FXML
    public void addRow() throws IOException {
        // Crear categoría con ID 0 para que la base de datos lo genere
        Categoria filaVacia = new Categoria(0, "", "");
 
        // Añadir la fila vacía al ObservableList
        listaCategoria.add(filaVacia);
 
        // Seleccionar la fila recién añadida y hacerla editable
        tableView.getSelectionModel().select(filaVacia);
        tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
    }
 
    public void saveRow(Categoria categoria) {
        // Validar datos antes de guardar
        if (categoria.getNombre().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Datos incompletos", 
                    "El nombre de la categoría no puede estar vacío.");
            return;
        }
        
        int resultado = categoria.save();
        
        if (resultado > 0) {
            mostrarAlerta(AlertType.INFORMATION, "Guardado exitoso", 
                    "La categoría ha sido guardada correctamente.");
            
            // Recargar datos para actualizar IDs y reflejar cambios de la BD
            loadData();
        } else {
            mostrarAlerta(AlertType.ERROR, "Error al guardar", 
                    "No se pudo guardar la categoría. Verifica la conexión a la base de datos.");
        }
    }    
 
    @FXML
    public void deleteRow() {
        Categoria categoria = tableView.getSelectionModel().getSelectedItem();
        
        if (categoria == null) {
            mostrarAlerta(AlertType.WARNING, "Selección vacía", 
                    "Debes seleccionar una categoría para eliminar.");
            return;
        }

        // Pedir confirmación antes de eliminar
        Alert alerta = new Alert(AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText("¿Estás seguro de que quieres borrar esta categoría?");
        alerta.setContentText("Esta acción no se puede deshacer.");
        
        Optional<ButtonType> resultado = alerta.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                int filasAfectadas = categoria.delete();
                
                if (filasAfectadas > 0) {
                    listaCategoria.remove(categoria);
                    mostrarAlerta(AlertType.INFORMATION, "Eliminación exitosa", 
                            "La categoría ha sido eliminada correctamente.");
                } else {
                    mostrarAlerta(AlertType.ERROR, "Error al eliminar", 
                            "No se pudo eliminar la categoría. Podría estar siendo utilizada por eventos.");
                }
            } catch (Exception e) {
                mostrarAlerta(AlertType.ERROR, "Error al eliminar", 
                        "Error en la base de datos: " + e.getMessage());
            }
        }
    }
    
    //--------------------------------------------------
    // MÉTODOS AUXILIARES
    //--------------------------------------------------
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}