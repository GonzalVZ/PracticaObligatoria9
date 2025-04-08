package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

public class ParticipanteController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    @FXML
    private HBox barraTitulo;
    private double xOffset = 0;
    private double yOffset = 0;

    // TableView y columnas para mostrar la información del join
    @FXML
    private TableView<Participante> tableView;

    @FXML
    private TableView<Participante> tableView1;

    @FXML
    private TableColumn<Participante, String> nombre;      // persona.nombre
    @FXML
    private TableColumn<Participante, String> apellido1;      // persona.apellido1
    @FXML
    private TableColumn<Participante, String> apellido2;      // persona.apellido2
    @FXML
    private TableColumn<Participante, String> email;          // participante.email
    @FXML
    private TableColumn<Participante, String> fecha; 
    
    
    // participa.fecha
    @FXML
    private TableColumn<Participante, String> colEvNombre;       // evento.nombre
    @FXML
    private TableColumn<Participante, String> colEvDescripcion;  // evento.descripcion
    @FXML
    private TableColumn<Participante, String> colEvLugar;        // evento.lugar
    @FXML
    private TableColumn<Participante, String> colEvFechaInicio;  // evento.fecha_inicio
    @FXML
    private TableColumn<Participante, String> colEvFechaFin;     // evento.fecha_fin
    @FXML
    private TableColumn<Participante, Void> colAcciones;

    // Lista de participantes obtenida de la consulta JOIN
    private ObservableList<Participante> listaParticipantes = FXCollections.observableArrayList();
    
    // Mapa para relacionar nombres de eventos con objetos Evento
    private Map<String, Evento> eventosMap = new HashMap<>();
    
    // Lista de eventos para el ComboBox
    private ObservableList<String> nombresEventos = FXCollections.observableArrayList();

    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    public void initialize() {
        cargarEventos();
        configurarColumnas();
        configurarMovimientoVentana();
        configurarSeleccionYEdicion();
        tableView.setItems(listaParticipantes);
        tableView1.setItems(listaParticipantes);
        loadData();
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE COLUMNAS
    //--------------------------------------------------
    private void configurarColumnas() {
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        apellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        fecha.setCellValueFactory(new PropertyValueFactory<>("participaFecha"));
        colEvNombre.setCellValueFactory(new PropertyValueFactory<>("evNombre"));
        colEvDescripcion.setCellValueFactory(new PropertyValueFactory<>("evDescripcion"));
        colEvLugar.setCellValueFactory(new PropertyValueFactory<>("evLugar"));
        colEvFechaInicio.setCellValueFactory(new PropertyValueFactory<>("evFechaInicio"));
        colEvFechaFin.setCellValueFactory(new PropertyValueFactory<>("evFechaFin"));

        // Hacer editables las columnas de los datos de persona, email y fecha
        nombre.setCellFactory(TextFieldTableCell.forTableColumn());
        apellido1.setCellFactory(TextFieldTableCell.forTableColumn());
        apellido2.setCellFactory(TextFieldTableCell.forTableColumn());
        email.setCellFactory(TextFieldTableCell.forTableColumn());
        fecha.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Configurar la columna de eventos como ComboBox con interfaz personalizada 
        // Solución unificada para evitar duplicidad de configuración
        colEvNombre.setCellFactory(column -> {
            ComboBoxTableCell<Participante, String> cell = new ComboBoxTableCell<Participante, String>(nombresEventos) {
                {
                    // Añadir tooltip a la celda
                    setTooltip(new Tooltip("Haga clic para seleccionar un evento"));
                }
                
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Crear contenedor para texto e indicador
                        HBox content = new HBox(5);
                        content.setAlignment(Pos.CENTER_LEFT);
                        
                        // Texto del evento
                        Label texto = new Label(item);
                        
                        // Indicador de desplegable
                        Label indicador = new Label("▼");
                        indicador.setStyle("-fx-text-fill: #666666; -fx-font-size: 8pt;");
                        
                        content.getChildren().addAll(texto, indicador);
                        
                        // Aplicar estilo para que parezca un combo box
                        setStyle("-fx-background-color: #3c3c3c; -fx-border-color: #555555; -fx-border-radius: 3;");
                        
                        // Establecer el contenido personalizado
                        setText(null);
                        setGraphic(content);
                    }
                }
                
                // Mejora: Activar automáticamente el editor al hacer clic
                @Override
                public void startEdit() {
                    super.startEdit();
                }
            };
            
            return cell;
        });
        
       
        // Configurar columna de acciones (guardar/eliminar)
        colAcciones.setCellFactory(new Callback<TableColumn<Participante, Void>, TableCell<Participante, Void>>() {
            @Override
            public TableCell<Participante, Void> call(final TableColumn<Participante, Void> param) {
                return new TableCell<Participante, Void>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnEliminar = new Button("✖");

                    {
                        btnGuardar.setOnAction(event -> {
                            Participante p = getTableView().getItems().get(getIndex());
                            saveRow(p);
                        });
                        btnEliminar.setOnAction(event -> {
                            deleteRow();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getSelectionModel().getSelectedIndex() != getIndex()) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(5, btnGuardar, btnEliminar);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE EVENTOS
    //--------------------------------------------------
    private void cargarEventos() {
        ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
        Evento.getAll(listaEventos);
        
        nombresEventos.clear();
        eventosMap.clear();
        
        for (Evento evento : listaEventos) {
            String nombre = evento.getNombre();
            nombresEventos.add(nombre);
            eventosMap.put(nombre, evento);
        }
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE MOVIMIENTO Y EDICIÓN
    //--------------------------------------------------
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
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSel, newSel) -> {
            tableView.refresh();
        });
        tableView.setEditable(true);
    }

    //--------------------------------------------------
    // MANEJO DE DATOS
    //--------------------------------------------------
    private void loadData() {
        listaParticipantes.clear();
        Participante.getAll(listaParticipantes);
    }

    @FXML
    public void addRow() throws IOException {
        // Crear un nuevo Participante (sin datos de join, se rellenarán al guardar)
        Participante nuevo = new Participante(0, "", "", "", "");
        listaParticipantes.add(nuevo);
        tableView.getSelectionModel().select(nuevo);
        tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
    }

    public void saveRow(Participante p) {
        int result = p.save();
        if (result > 0) {
            showAlert(AlertType.INFORMATION, "Guardado", "Participante guardado correctamente.");
            loadData();
        } else {
            showAlert(AlertType.ERROR, "Error", "No se pudo guardar el participante.");
        }
    }

    @FXML
    public void deleteRow() {
        Participante p = tableView.getSelectionModel().getSelectedItem();
        if (p == null) {
            showAlert(AlertType.WARNING, "Eliminar", "Seleccione un participante a eliminar.");
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿Está seguro de eliminar el participante?");
        alert.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int rows = p.delete(p.getId());
            if (rows > 0) {
                listaParticipantes.remove(p);
                showAlert(AlertType.INFORMATION, "Eliminado", "Participante eliminado correctamente.");
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el participante.");
            }
        }
    }

    //--------------------------------------------------
    // CONTROLES DE VENTANA
    //--------------------------------------------------
    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //--------------------------------------------------
    // MÉTODO AUXILIAR PARA MOSTRAR ALERTAS
    //--------------------------------------------------
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}