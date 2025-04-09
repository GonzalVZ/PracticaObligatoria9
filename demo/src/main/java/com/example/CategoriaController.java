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
 * Permite crear, editar, eliminar y visualizar las categorías
 * utilizadas para clasificar los eventos en el sistema.
 * 
 * <p>Este controlador proporciona la funcionalidad para el CRUD
 * (Crear, Leer, Actualizar, Eliminar) de categorías mediante una
 * interfaz TableView editable con botones de acción integrados.</p>
 * 
 * @author Jesús
 * @version 1.0
 */
public class CategoriaController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    /**
     * Barra de título personalizada para mover la ventana.
     */
    @FXML
    private HBox barraTitulo;

    /**
     * TableView que muestra la lista de categorías.
     */
    @FXML
    private TableView<Categoria> tableView;

    /**
     * Columna para el nombre de la categoría.
     */
    @FXML
    private TableColumn<Categoria, String> nombre;   
 
    /**
     * Columna para la descripción de la categoría.
     */
    @FXML
    private TableColumn<Categoria, String> descripcion;

    /**
     * Columna para los botones de acción (guardar, eliminar).
     */
    @FXML
    private TableColumn<Categoria, Void> acciones;
    
    //--------------------------------------------------
    // VARIABLES
    //--------------------------------------------------
    /**
     * Coordenada X inicial para el movimiento de la ventana.
     */
    private double xOffset = 0;
    
    /**
     * Coordenada Y inicial para el movimiento de la ventana.
     */
    private double yOffset = 0;
    
    /**
     * Lista observable que contiene las categorías mostradas en la tabla.
     */
    private ObservableList<Categoria> listaCategoria = FXCollections.observableArrayList();
 
    //--------------------------------------------------
    // MÉTODOS DE CONTROL DE VENTANA
    //--------------------------------------------------
    /**
     * Minimiza la ventana actual.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Maximiza o restaura la ventana actual.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void maximizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    /**
     * Cierra la ventana actual.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    /**
     * Método de inicialización que se ejecuta al cargar la vista.
     * Configura las columnas, los eventos y carga los datos iniciales.
     */
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
    
    /**
     * Configura las columnas de la tabla para mostrar los datos de las categorías.
     * Define las fábricas de valores y celdas para las columnas.
     */
    private void configurarColumnas() {
        // Configurar la fábrica de valores para asociar propiedades del modelo
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        
        // Hacer que las columnas sean editables usando TextFieldTableCell
        nombre.setCellFactory(TextFieldTableCell.forTableColumn());
        descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Asegurar que las columnas se redimensionan correctamente
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    /**
     * Configura la columna de acciones con botones para guardar y eliminar categorías.
     * Los botones solo se muestran en la fila seleccionada.
     */
    private void configurarColumnaAcciones() {
        acciones.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Categoria, Void> call(final TableColumn<Categoria, Void> param) {
                return new TableCell<>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnCancelar = new Button("✖");
    
                    {
                        // Configurar tooltip para los botones
                        btnGuardar.setTooltip(new javafx.scene.control.Tooltip("Guardar cambios"));
                        btnCancelar.setTooltip(new javafx.scene.control.Tooltip("Eliminar categoría"));
                        
                        // Configurar acción de guardado
                        btnGuardar.setOnAction(event -> {
                            Categoria categoria = getTableView().getItems().get(getIndex());
                            saveRow(categoria);
                        });
    
                        // Configurar acción de eliminación
                        btnCancelar.setOnAction(event -> {
                            tableView.getSelectionModel().select(getIndex());
                            deleteRow();
                        });
                        
                        // Añadir clases CSS para estilizar los botones
                        btnGuardar.getStyleClass().add("action-button");
                        btnCancelar.getStyleClass().add("action-button");
                    }
    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getSelectionModel().getSelectedIndex() != getIndex()) {
                            setGraphic(null); // Oculta los botones si la fila no está seleccionada
                        } else {
                            HBox hBox = new HBox(5, btnGuardar, btnCancelar);
                            hBox.setAlignment(javafx.geometry.Pos.CENTER);
                            setGraphic(hBox); // Muestra los botones si la fila está seleccionada
                        }
                    }
                };
            }
        });
    }
    
    /**
     * Configura la selección y edición de filas en la tabla.
     * Añade listeners para actualizar la visualización cuando cambia la selección.
     */
    private void configurarSeleccionYEdicion() {
        // Actualizar cuando cambie la selección
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            tableView.refresh(); // Refresca el TableView para actualizar las celdas
        });
        
        // Permitir que la tabla sea editable
        tableView.setEditable(true);
    }
    
    /**
     * Configura el comportamiento para mover la ventana arrastrando la barra de título.
     */
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
    /**
     * Carga las categorías desde la base de datos y actualiza la tabla.
     */
    private void loadData() {
        try {
            listaCategoria.clear(); // Limpiar lista antes de cargar
            Categoria.getAll(listaCategoria);
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error de carga", 
                    "No se pudieron cargar las categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    /**
     * Añade una nueva fila vacía a la tabla para crear una nueva categoría.
     * 
     * @throws IOException Si hay un problema de entrada/salida
     */
    @FXML
    public void addRow() throws IOException {
        try {
            // Crear categoría con ID 0 para que la base de datos lo genere
            Categoria filaVacia = new Categoria(0, "", "");
     
            // Añadir la fila vacía al ObservableList
            listaCategoria.add(filaVacia);
     
            // Seleccionar la fila recién añadida y hacerla editable
            tableView.getSelectionModel().select(filaVacia);
            tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error", 
                    "No se pudo añadir una nueva categoría: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    /**
     * Guarda los cambios realizados en una categoría en la base de datos.
     * 
     * @param categoria La categoría a guardar
     */
    public void saveRow(Categoria categoria) {
        try {
            // Validar datos antes de guardar
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
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
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error al guardar", 
                    "Se produjo un error al guardar la categoría: " + e.getMessage());
            e.printStackTrace();
        }
    }    
 
    /**
     * Elimina la categoría seleccionada de la base de datos,
     * previa confirmación del usuario.
     */
    @FXML
    public void deleteRow() {
        try {
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
                int filasAfectadas = categoria.delete();
                
                if (filasAfectadas > 0) {
                    listaCategoria.remove(categoria);
                    mostrarAlerta(AlertType.INFORMATION, "Eliminación exitosa", 
                            "La categoría ha sido eliminada correctamente.");
                } else {
                    mostrarAlerta(AlertType.ERROR, "Error al eliminar", 
                            "No se pudo eliminar la categoría. Podría estar siendo utilizada por eventos.");
                }
            }
        } catch (Exception e) {
            String mensajeError = e.getMessage();
            if (mensajeError != null && mensajeError.contains("foreign key constraint")) {
                mostrarAlerta(AlertType.ERROR, "Error al eliminar", 
                        "Esta categoría está siendo utilizada por uno o más eventos. Elimine primero los eventos asociados.");
            } else {
                mostrarAlerta(AlertType.ERROR, "Error al eliminar", 
                        "Error en la base de datos: " + mensajeError);
                e.printStackTrace();
            }
        }
    }
    
    //--------------------------------------------------
    // MÉTODOS AUXILIARES
    //--------------------------------------------------
    /**
     * Muestra un cuadro de diálogo de alerta con el tipo, título y mensaje especificados.
     * 
     * @param tipo El tipo de alerta (ERROR, WARNING, INFORMATION, etc.)
     * @param titulo El título del cuadro de diálogo
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        try {
            javafx.application.Platform.runLater(() -> {
                Alert alerta = new Alert(tipo);
                alerta.setTitle(titulo);
                alerta.setHeaderText(null);
                alerta.setContentText(mensaje);
                alerta.showAndWait();
            });
        } catch (Exception e) {
            // Si hay un problema mostrando la alerta, imprimir en consola
            System.out.println("[ALERTA " + tipo + "] " + titulo + ": " + mensaje);
            e.printStackTrace();
        }
    }
}