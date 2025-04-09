package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controlador para la vista de gestión de eventos.
 * Permite crear, ver, editar y eliminar eventos del sistema,
 * incluyendo la asignación de categorías mediante selección visual.
 * 
 * <p>Este controlador implementa un patrón MVVM, utilizando propiedades
 * observables para la vinculación bidireccional con la interfaz de usuario.</p>
 * 
 * @author Jesús
 * @version 1.0
 */
public class EventoController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    /**
     * Barra de título personalizada para mover la ventana.
     */
    @FXML
    private HBox barraTitulo;
    
    /**
     * Coordenada X inicial para el movimiento de la ventana.
     */
    private double xOffset = 0;
    
    /**
     * Coordenada Y inicial para el movimiento de la ventana.
     */
    private double yOffset = 0;

    /**
     * TableView que muestra los eventos del sistema.
     */
    @FXML
    private TableView<Evento> tableView;

    /**
     * Columna para el nombre del evento.
     */
    @FXML
    private TableColumn<Evento, String> nombre;
    
    /**
     * Columna para la descripción del evento.
     */
    @FXML
    private TableColumn<Evento, String> descripcion;

    /**
     * Columna para el lugar donde se celebra el evento.
     */
    @FXML
    private TableColumn<Evento, String> lugar;
    
    /**
     * Columna para la fecha de inicio del evento.
     */
    @FXML
    private TableColumn<Evento, String> fecha_inicio;
    
    /**
     * Columna para la fecha de finalización del evento.
     */
    @FXML
    private TableColumn<Evento, String> fecha_fin;

    /**
     * Columna para el nombre de la categoría del evento.
     */
    @FXML
    private TableColumn<Evento, String> nombre_categoria;

    /**
     * Columna para los botones de acción (guardar, eliminar).
     */
    @FXML
    private TableColumn<Evento, Void> acciones;
    
    //--------------------------------------------------
    // COLECCIONES DE DATOS
    //--------------------------------------------------
    /**
     * Lista observable que contiene todos los eventos mostrados en la tabla.
     */
    private ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
    
    /**
     * Mapa que relaciona IDs de categorías con sus nombres para mostrarlos en la interfaz.
     */
    private Map<Integer, String> mapaCategorias = new HashMap<>();

    //--------------------------------------------------
    // CONTROL DE VENTANA
    //--------------------------------------------------
    /**
     * Minimiza la ventana actual.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void minimizarVentana(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setIconified(true);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudo minimizar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maximiza o restaura la ventana actual.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void maximizarVentana(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudo maximizar/restaurar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana actual.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudo cerrar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    /**
     * Método de inicialización que se ejecuta al cargar la vista.
     * Configura las columnas, los eventos y carga los datos iniciales.
     */
    public void initialize() {
        try {
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
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de inicialización", 
                "No se pudo inicializar correctamente el controlador: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Precarga el mapa de categorías para mostrar los nombres en lugar de IDs.
     * Este mapa relaciona cada ID de categoría con su nombre correspondiente.
     */
    private void precargaCategorias() {
        try {
            ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
            Categoria.getAll(listaCategorias);
            
            // Limpiar mapa existente
            mapaCategorias.clear();
            
            // Crear el mapa id_categoria -> nombre_categoria
            for (Categoria categoria : listaCategorias) {
                mapaCategorias.put(categoria.getId(), categoria.getNombre());
            }
            
            // Asegurar que siempre haya una entrada para el ID 0
            if (!mapaCategorias.containsKey(0)) {
                mapaCategorias.put(0, "Sin categoría");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudieron cargar las categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura las columnas básicas del TableView.
     * Establece las fábricas de valores y celdas para columnas editables.
     */
    private void configurarColumnas() {
        try {
            // Configurar cell value factories para vincular columnas con propiedades
            nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            lugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
            fecha_inicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
            fecha_fin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
            
            // Configurar cell factories para edición con TextFieldTableCell
            nombre.setCellFactory(TextFieldTableCell.forTableColumn());
            descripcion.setCellFactory(TextFieldTableCell.forTableColumn());
            lugar.setCellFactory(TextFieldTableCell.forTableColumn());
            fecha_inicio.setCellFactory(TextFieldTableCell.forTableColumn());
            fecha_fin.setCellFactory(TextFieldTableCell.forTableColumn());
            
            // Configurar política de redimensionamiento para mejor visualización
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudieron configurar las columnas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura la columna especial para nombres de categorías.
     * Implementa un ComboBox personalizado para seleccionar categorías.
     */
    private void configurarColumnaNombreCategoria() {
        try {
            // Cargar los nombres de todas las categorías para el ComboBox
            ObservableList<String> nombresCategorias = FXCollections.observableArrayList();
            Categoria.getNombres(nombresCategorias);
            
            // Añadir "Sin categoría" si no está ya incluido
            if (!nombresCategorias.contains("Sin categoría")) {
                nombresCategorias.add("Sin categoría");
            }
            
            // Configurar la fábrica de valores para mostrar el nombre de la categoría
            nombre_categoria.setCellValueFactory(cellData -> {
                Evento evento = cellData.getValue();
                String nombreCategoria = mapaCategorias.getOrDefault(evento.getId_categoria(), "Sin categoría");
                return new SimpleStringProperty(nombreCategoria);
            });
            
            // Configurar ComboBox con interfaz personalizada
            nombre_categoria.setCellFactory(column -> {
                return new ComboBoxTableCell<Evento, String>(nombresCategorias) {
                    {
                        // Añadir tooltip para guiar al usuario
                        setTooltip(new Tooltip("Haga clic para seleccionar una categoría"));
                    }
                    
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Crear contenedor personalizado con indicador visual
                            HBox content = new HBox(5);
                            content.setAlignment(Pos.CENTER_LEFT);
                            
                            // Texto e indicador visual
                            Label texto = new Label(item);
                            Label indicador = new Label("▼");
                            indicador.setStyle("-fx-text-fill: #666666; -fx-font-size: 8pt;");
                            
                            content.getChildren().addAll(texto, indicador);
                            
                            // Estilo visual para parecer un ComboBox
                            setStyle("-fx-background-color: #3c3c3c; -fx-border-color: #555555; -fx-border-radius: 3;");
                            
                            setText(null);
                            setGraphic(content);
                        }
                    }
                    
                    // Activar edición automáticamente al hacer clic
                    @Override
                    public void startEdit() {
                        super.startEdit();
                    }
                };
            });
            
            // Configurar acción al confirmar la edición
            nombre_categoria.setOnEditCommit(event -> {
                try {
                    Evento evento = event.getRowValue();
                    String nuevoNombreCategoria = event.getNewValue();
                    
                    // Buscar el ID correspondiente al nombre de categoría seleccionado
                    Integer categoriaId = null;
                    for (Map.Entry<Integer, String> entry : mapaCategorias.entrySet()) {
                        if (entry.getValue().equals(nuevoNombreCategoria)) {
                            categoriaId = entry.getKey();
                            break;
                        }
                    }
                    
                    // Si encontramos la categoría, actualizar el evento
                    if (categoriaId != null) {
                        evento.setId_categoria(categoriaId);
                        
                        // Guardar el cambio en la base de datos
                        int result = evento.save();
                        if (result <= 0) {
                            showAlert(AlertType.WARNING, "Advertencia", 
                                "No se pudo guardar la categoría. Verifique los datos.");
                            
                            // Recargar datos para deshacer cambios visuales
                            loadData();
                        }
                    } else if ("Sin categoría".equals(nuevoNombreCategoria)) {
                        // Establecer sin categoría (ID 0)
                        evento.setId_categoria(0);
                        evento.save();
                    }
                } catch (Exception e) {
                    showAlert(AlertType.ERROR, "Error", 
                        "Error al cambiar la categoría: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Recargar datos para mostrar el estado correcto
                    loadData();
                }
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo configurar la columna de categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura la columna de acciones con botones para guardar y eliminar eventos.
     * Los botones solo se muestran en la fila seleccionada.
     */
    private void configurarColumnaAcciones() {
        try {
            acciones.setCellFactory(new Callback<>() {
                @Override
                public TableCell<Evento, Void> call(final TableColumn<Evento, Void> param) {
                    return new TableCell<>() {
                        private final Button btnGuardar = new Button("✔");
                        private final Button btnEliminar = new Button("✖");
        
                        {
                            // Configurar tooltips para los botones
                            btnGuardar.setTooltip(new Tooltip("Guardar cambios"));
                            btnEliminar.setTooltip(new Tooltip("Eliminar evento"));
                            
                            // Aplicar estilos visuales
                            btnGuardar.getStyleClass().add("action-button");
                            btnEliminar.getStyleClass().add("action-button");
                            
                            // Configurar acciones de los botones
                            btnGuardar.setOnAction(event -> {
                                Evento evento = getTableView().getItems().get(getIndex());
                                saveRow(evento);
                            });
        
                            btnEliminar.setOnAction(event -> {
                                tableView.getSelectionModel().select(getIndex());
                                deleteRow();
                            });
                        }
        
                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || getTableView().getSelectionModel().getSelectedIndex() != getIndex()) {
                                setGraphic(null); // No mostrar botones si la celda está vacía o no seleccionada
                            } else {
                                HBox hBox = new HBox(5, btnGuardar, btnEliminar);
                                hBox.setAlignment(Pos.CENTER);
                                setGraphic(hBox); // Mostrar botones en la fila seleccionada
                            }
                        }
                    };
                }
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo configurar la columna de acciones: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura la funcionalidad para mover la ventana arrastrando la barra de título.
     */
    private void configurarMovimientoVentana() {
        try {
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
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo configurar el movimiento de la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura la selección y edición de filas en la tabla.
     * Añade listeners para actualizar la visualización cuando cambia la selección.
     */
    private void configurarSeleccionYEdicion() {
        try {
            // Actualizar visualización cuando cambia la selección
            tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
                tableView.refresh();
            });
            
            // Habilitar edición en la tabla
            tableView.setEditable(true);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo configurar la selección y edición: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //--------------------------------------------------
    // MANEJO DE DATOS
    //--------------------------------------------------
    /**
     * Carga todos los eventos desde la base de datos.
     * Actualiza la lista observable con los eventos recuperados.
     */
    private void loadData() {
        try {
            // Limpiar la lista actual
            listaEventos.clear();
            
            // Cargar eventos desde la base de datos
            Evento.getAll(listaEventos);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudieron cargar los eventos: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    /**
     * Añade una nueva fila vacía a la tabla para crear un nuevo evento.
     * 
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @FXML
    public void addRow() throws IOException {
        try {
            // Crear un evento vacío con un ID preliminar
            int nuevoId = Evento.getLastId() + 1;
            Evento nuevoEvento = new Evento(nuevoId, "", "", "", "", "", 0);
     
            // Añadir el nuevo evento a la lista
            listaEventos.add(nuevoEvento);
     
            // Seleccionar y hacer editable la nueva fila
            tableView.getSelectionModel().select(nuevoEvento);
            tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo añadir un nuevo evento: " + e.getMessage());
            e.printStackTrace();
        }
    }
     
    /**
     * Guarda los cambios de un evento en la base de datos.
     * 
     * @param evento El evento a guardar
     */
    public void saveRow(Evento evento) {
        try {
            // Validar datos básicos
            if (evento.getNombre() == null || evento.getNombre().trim().isEmpty()) {
                showAlert(AlertType.WARNING, "Datos incompletos", 
                    "El nombre del evento no puede estar vacío");
                return;
            }
            
            // Intentar guardar el evento
            int result = evento.save();
            
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Guardado exitoso", 
                    "Evento guardado correctamente");
                    
                // Recargar datos para actualizar IDs y reflejar cambios de la BD
                loadData();
            } else {
                showAlert(AlertType.ERROR, "Error al guardar", 
                    "No se pudo guardar el evento. Verifique los datos ingresados.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error al guardar", 
                "Se produjo un error al guardar el evento: " + e.getMessage());
            e.printStackTrace();
        }
    }    
 
    /**
     * Elimina el evento seleccionado de la base de datos,
     * previa confirmación del usuario.
     */
    @FXML
    public void deleteRow() {
        try {
            // Verificar que haya un evento seleccionado
            Evento evento = tableView.getSelectionModel().getSelectedItem();
            if (evento == null) {
                showAlert(AlertType.WARNING, "Selección vacía", 
                    "Debe seleccionar un evento para eliminar");
                return;
            }
            
            // Pedir confirmación antes de eliminar
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("¿Está seguro de que desea eliminar este evento?");
            alert.setContentText("Esta acción no se puede deshacer.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Intentar eliminar el evento
                int success = evento.delete();
                
                if (success > 0) {
                    // Eliminar de la lista y mostrar confirmación
                    listaEventos.remove(evento);
                    showAlert(AlertType.INFORMATION, "Eliminación exitosa", 
                        "El evento ha sido eliminado correctamente");
                } else {
                    showAlert(AlertType.ERROR, "Error al eliminar", 
                        "No se pudo eliminar el evento. Puede que tenga participantes o artistas asignados.");
                }
            }
        } catch (Exception e) {
            String mensajeError = e.getMessage();
            if (mensajeError != null && mensajeError.contains("foreign key constraint")) {
                showAlert(AlertType.ERROR, "Error al eliminar", 
                    "Este evento tiene participantes o artistas asociados. Elimine primero esas asignaciones.");
            } else {
                showAlert(AlertType.ERROR, "Error al eliminar", 
                    "Error en la base de datos: " + mensajeError);
            }
            e.printStackTrace();
        }
    }
    
    //--------------------------------------------------
    // MÉTODOS AUXILIARES
    //--------------------------------------------------
    /**
     * Muestra un cuadro de diálogo de alerta con el tipo, título y mensaje especificados.
     * 
     * @param type El tipo de alerta (ERROR, WARNING, INFORMATION, etc.)
     * @param title El título del cuadro de diálogo
     * @param message El mensaje a mostrar
     */
    private void showAlert(AlertType type, String title, String message) {
        try {
            Platform.runLater(() -> {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        } catch (Exception e) {
            // Si hay un problema mostrando la alerta, imprimir en consola
            System.out.println("[ALERTA " + type + "] " + title + ": " + message);
            e.printStackTrace();
        }
    }
}