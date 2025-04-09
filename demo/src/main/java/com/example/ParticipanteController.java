package com.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controlador para la vista de gestión de participantes.
 * Permite crear, ver, editar y eliminar participantes, así como gestionar
 * su relación con los eventos en los que participan mediante una interfaz maestro-detalle.
 * 
 * <p>Este controlador implementa una interfaz que muestra dos tablas relacionadas:
 * <ul>
 *   <li>Una tabla principal de participantes con sus datos personales y de contacto</li>
 *   <li>Una tabla secundaria que muestra los eventos asociados al participante seleccionado</li>
 * </ul>
 * </p>
 * 
 * <p>También proporciona funcionalidad para asignar y desasignar eventos a participantes,
 * así como para ver eventos disponibles para inscripción.</p>
 * 
 * @author Jesús
 * @version 1.0
 */
public class ParticipanteController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    /**
     * Barra de título personalizada que permite mover la ventana.
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
     * TableView que muestra la lista de participantes.
     * Actúa como tabla maestra en la relación maestro-detalle.
     */
    @FXML
    private TableView<Participante> tableView;

    /**
     * Columna para el nombre del participante.
     */
    @FXML
    private TableColumn<Participante, String> nombre;
    
    /**
     * Columna para el primer apellido del participante.
     */
    @FXML
    private TableColumn<Participante, String> apellido1;
    
    /**
     * Columna para el segundo apellido del participante.
     */
    @FXML
    private TableColumn<Participante, String> apellido2;
    
    /**
     * Columna para el email del participante.
     */
    @FXML
    private TableColumn<Participante, String> email;
    
    /**
     * Columna que contiene botones de acción para cada participante.
     */
    @FXML
    private TableColumn<Participante, Void> accionesParticipante;
    
    /**
     * TableView que muestra los eventos asociados al participante seleccionado.
     * Actúa como tabla detalle en la relación maestro-detalle.
     */
    @FXML
    private TableView<Evento> tableView1;
    
    /**
     * Columna para el nombre del evento.
     */
    @FXML
    private TableColumn<Evento, String> colEvNombre;
    
    /**
     * Columna para la descripción del evento.
     */
    @FXML
    private TableColumn<Evento, String> colEvDescripcion;
    
    /**
     * Columna para el lugar del evento.
     */
    @FXML
    private TableColumn<Evento, String> colEvLugar;
    
    /**
     * Columna para la fecha de inicio del evento.
     */
    @FXML
    private TableColumn<Evento, String> colEvFechaInicio;
    
    /**
     * Columna para la fecha de finalización del evento.
     */
    @FXML
    private TableColumn<Evento, String> colEvFechaFin;
    
    /**
     * Columna para la fecha en que el participante se inscribió en el evento.
     */
    @FXML
    private TableColumn<Evento, String> colFechaParticipacion;
    
    /**
     * Columna que contiene botones de acción para cada evento.
     */
    @FXML
    private TableColumn<Evento, Void> colAcciones;

    /**
     * Panel que muestra un mensaje cuando no hay eventos asignados.
     */
    @FXML
    private HBox hboxEventosDisponibles;
    
    /**
     * Botón para alternar entre ver eventos asignados y eventos disponibles.
     */
    @FXML
    private Button btnMostrarEventosDisponibles;

    //--------------------------------------------------
    // COLECCIONES DE DATOS
    //--------------------------------------------------
    /**
     * Lista observable de participantes que se muestra en la tabla principal.
     */
    private ObservableList<Participante> listaParticipantes = FXCollections.observableArrayList();
    
    /**
     * Lista observable de eventos que se muestra en la tabla secundaria.
     */
    private ObservableList<Evento> listaEventosParticipante = FXCollections.observableArrayList();
    
    /**
     * Mapa que relaciona nombres de eventos con sus objetos correspondientes.
     * Utilizado para la selección de eventos en el ComboBox.
     */
    private Map<String, Evento> eventosMap = new HashMap<>();
    
    /**
     * Lista observable de nombres de eventos para mostrar en el ComboBox.
     */
    private ObservableList<String> nombresEventos = FXCollections.observableArrayList();
    
    /**
     * Bandera que indica si se están mostrando todos los eventos disponibles
     * o solo los eventos en los que el participante está inscrito.
     */
    private boolean mostrandoEventosDisponibles = false;

    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    /**
     * Método de inicialización que se ejecuta al cargar la vista.
     * Configura los componentes, carga los datos iniciales y establece los listeners.
     */
    public void initialize() {
        try {
            cargarEventos();
            configurarColumnas();
            configurarMovimientoVentana();
            configurarSeleccionYEdicion();
            
            // Inicializar las tablas con sus respectivas listas
            tableView.setItems(listaParticipantes);
            tableView1.setItems(listaEventosParticipante);
            
            // Ocultar la tabla de eventos hasta que se seleccione un participante
            tableView1.setVisible(false);
            
            // Inicializar componentes adicionales
            if (hboxEventosDisponibles != null) {
                hboxEventosDisponibles.setVisible(false);
            }
            
            // Cargar los datos iniciales
            loadData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de inicialización", 
                "No se pudo inicializar correctamente el controlador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE COLUMNAS
    //--------------------------------------------------
    /**
     * Configura las columnas de ambas tablas, sus fábricas de valores y celdas,
     * así como los manejadores de eventos para acciones específicas.
     * Este método establece cómo se muestran y editan los datos en las tablas.
     */
    private void configurarColumnas() {
        try {
            // Configurar columnas de la tabla de participantes
            nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            apellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
            apellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
           
            // Configurar columnas de la tabla de eventos
            colEvNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colEvDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            colEvLugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
            colEvFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
            colEvFechaFin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
            colFechaParticipacion.setCellValueFactory(new PropertyValueFactory<>("fechaParticipacion")); 
           
            // Hacer editables las columnas de participantes
            nombre.setCellFactory(TextFieldTableCell.forTableColumn());
            apellido1.setCellFactory(TextFieldTableCell.forTableColumn());
            apellido2.setCellFactory(TextFieldTableCell.forTableColumn());
            email.setCellFactory(TextFieldTableCell.forTableColumn());
            
            // Configurar columna de eventos como ComboBox con interfaz personalizada
            configurarComboBoxEventos();
            
            // Configurar columna de acciones para la tabla de participantes
            configurarAccionesParticipante();
            
            // Configurar columna de acciones para la tabla de eventos
            configurarAccionesEvento();

            // Configurar políticas de redimensionamiento
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableView1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de configuración", 
                "No se pudieron configurar correctamente las columnas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura el ComboBox personalizado para la columna de eventos.
     * Este ComboBox permite cambiar el evento asociado a un participante.
     */
    private void configurarComboBoxEventos() {
        colEvNombre.setCellFactory(column -> {
            ComboBoxTableCell<Evento, String> cell = new ComboBoxTableCell<Evento, String>(nombresEventos) {
                {
                    setTooltip(new Tooltip("Haga clic para seleccionar un evento"));
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
                        
                        Label texto = new Label(item);
                        Label indicador = new Label("▼");
                        indicador.setStyle("-fx-text-fill: #666666; -fx-font-size: 8pt;");
                        
                        content.getChildren().addAll(texto, indicador);
                        setStyle("-fx-background-color: #3c3c3c; -fx-border-color: #555555; -fx-border-radius: 3;");
                        
                        setText(null);
                        setGraphic(content);
                    }
                }
            };
            
            return cell;
        });
        
        // Manejador para cuando se selecciona un evento en el ComboBox
        colEvNombre.setOnEditCommit(event -> {
            try {
                Evento evento = event.getRowValue();
                String nuevoNombre = event.getNewValue();
                Participante participante = tableView.getSelectionModel().getSelectedItem();
                
                if (participante != null && eventosMap.containsKey(nuevoNombre)) {
                    Evento nuevoEvento = eventosMap.get(nuevoNombre);
                    
                    // Actualizar la relación en la base de datos
                    try (Connection con = Conexion.conectarBD();
                         PreparedStatement ps = con.prepareStatement(
                             "UPDATE participa SET id_evento = ? WHERE id_persona = ? AND id_evento = ?")) {
                        
                        if (con == null) {
                            showAlert(AlertType.ERROR, "Error de conexión", 
                                "No se pudo establecer conexión con la base de datos");
                            return;
                        }
                        
                        ps.setInt(1, nuevoEvento.getId());
                        ps.setInt(2, participante.getId());
                        ps.setInt(3, evento.getId());
                        
                        int result = ps.executeUpdate();
                        if (result > 0) {
                            showAlert(AlertType.INFORMATION, "Cambio de evento", 
                                "El participante ha sido asignado al nuevo evento");
                            // Recargar los eventos 
                            cargarEventosDelParticipante(participante.getId());
                        } else {
                            showAlert(AlertType.ERROR, "Error", 
                                "No se pudo cambiar el evento del participante");
                        }
                    }
                }
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Error", 
                    "Error al cambiar el evento: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
    
    /**
     * Configura la columna de acciones para la tabla de participantes,
     * añadiendo botones de guardar, eliminar y ver eventos asociados.
     */
    private void configurarAccionesParticipante() {
        accionesParticipante.setCellFactory(new Callback<TableColumn<Participante, Void>, TableCell<Participante, Void>>() {
            @Override
            public TableCell<Participante, Void> call(final TableColumn<Participante, Void> param) {
                return new TableCell<Participante, Void>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnEliminar = new Button("✖");
                    private final Button btnVerMas = new Button("⤵");
    
                    {
                        btnGuardar.setTooltip(new Tooltip("Guardar cambios"));
                        btnEliminar.setTooltip(new Tooltip("Eliminar participante"));
                        btnVerMas.setTooltip(new Tooltip("Ver eventos"));
                        
                        btnGuardar.setOnAction(event -> {
                            try {
                                Participante p = getTableView().getItems().get(getIndex());
                                saveRow(p);
                            } catch (Exception e) {
                                showAlert(AlertType.ERROR, "Error", 
                                    "Error al guardar el participante: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                        
                        btnEliminar.setOnAction(event -> {
                            try {
                                tableView.getSelectionModel().select(getIndex());
                                deleteRow();
                            } catch (Exception e) {
                                showAlert(AlertType.ERROR, "Error", 
                                    "Error al eliminar el participante: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                        
                        btnVerMas.setOnAction(event -> {
                            try {
                                Participante p = getTableView().getItems().get(getIndex());
                                tableView.getSelectionModel().select(p);
                                
                                // Cargar y mostrar/ocultar eventos
                                cargarEventosDelParticipante(p.getId());
                                
                                if (tableView1.isVisible() && tableView.getSelectionModel().getSelectedItem() == p) {
                                    // Ocultar tabla de eventos
                                    tableView1.setVisible(false);
                                    btnVerMas.setText("⤵");
                                    if (hboxEventosDisponibles != null) {
                                        hboxEventosDisponibles.setVisible(false);
                                    }
                                    mostrandoEventosDisponibles = false;
                                } else {
                                    // Mostrar tabla de eventos
                                    tableView1.setVisible(true);
                                    btnVerMas.setText("⤴");
                                    if (hboxEventosDisponibles != null && listaEventosParticipante.isEmpty()) {
                                        hboxEventosDisponibles.setVisible(true);
                                    }
                                }
                            } catch (Exception e) {
                                showAlert(AlertType.ERROR, "Error", 
                                    "Error al mostrar eventos del participante: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }
    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Aplicar estilos CSS
                            btnGuardar.getStyleClass().add("action-button");
                            btnEliminar.getStyleClass().add("action-button");
                            btnVerMas.getStyleClass().add("action-button");
                            
                            HBox hBox = new HBox(5, btnGuardar, btnEliminar, btnVerMas);
                            hBox.setAlignment(Pos.CENTER);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }
    
    /**
     * Configura la columna de acciones para la tabla de eventos,
     * añadiendo botones para inscribir o desinscribir participantes.
     */
    private void configurarAccionesEvento() {
        colAcciones.setCellFactory(new Callback<TableColumn<Evento, Void>, TableCell<Evento, Void>>() {
            @Override
            public TableCell<Evento, Void> call(final TableColumn<Evento, Void> param) {
                return new TableCell<Evento, Void>() {
                    private final Button btnInscribir = new Button("✚");
                    private final Button btnDesinscribir = new Button("✖");

                    {
                        btnInscribir.setTooltip(new Tooltip("Inscribir en este evento"));
                        btnDesinscribir.setTooltip(new Tooltip("Desinscribir de este evento"));
                        
                        btnInscribir.setOnAction(event -> {
                            try {
                                // Añadir el participante al evento
                                int indice = getIndex();
                                if (indice >= 0 && indice < getTableView().getItems().size()) {
                                    Evento evento = getTableView().getItems().get(indice);
                                    Participante participante = tableView.getSelectionModel().getSelectedItem();
                                    if (participante != null) {
                                        inscribirParticipanteEnEvento(participante, evento);
                                        
                                        // Volver al modo normal
                                        mostrandoEventosDisponibles = false;
                                        if (btnMostrarEventosDisponibles != null) {
                                            btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                                        }
                                    } else {
                                        showAlert(AlertType.WARNING, "Selección requerida", 
                                            "Por favor, seleccione un participante primero");
                                    }
                                }
                            } catch (Exception e) {
                                showAlert(AlertType.ERROR, "Error", 
                                    "Error al inscribir al participante: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                        
                        btnDesinscribir.setOnAction(event -> {
                            try {
                                // Quitar al participante del evento
                                int indice = getIndex();
                                if (indice >= 0 && indice < getTableView().getItems().size()) {
                                    Evento evento = getTableView().getItems().get(indice);
                                    Participante participante = tableView.getSelectionModel().getSelectedItem();
                                    if (participante != null) {
                                        desinscribirParticipanteDeEvento(participante, evento);
                                    } else {
                                        showAlert(AlertType.WARNING, "Selección requerida", 
                                            "Por favor, seleccione un participante primero");
                                    }
                                }
                            } catch (Exception e) {
                                showAlert(AlertType.ERROR, "Error", 
                                    "Error al desinscribir al participante: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btnInscribir.getStyleClass().add("action-button");
                            btnDesinscribir.getStyleClass().add("action-button");
                            
                            HBox hBox = new HBox(5);
                            
                            // Verificar que el índice es válido
                            int index = getIndex();
                            ObservableList<Evento> items = getTableView().getItems();
                            if (index >= 0 && index < items.size()) {
                                Evento evento = items.get(index);
                                String fechaParticipacion = evento.getFechaParticipacion();
                                
                                if (mostrandoEventosDisponibles || fechaParticipacion == null || fechaParticipacion.isEmpty()) {
                                    hBox.getChildren().add(btnInscribir);
                                } else {
                                    hBox.getChildren().add(btnDesinscribir);
                                }
                                
                                hBox.setAlignment(Pos.CENTER);
                                setGraphic(hBox);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        });
    }

    /**
     * Inscribe a un participante en un evento, creando la relación en la tabla participa.
     * 
     * @param p El participante a inscribir
     * @param e El evento en el que se inscribirá al participante
     */
    private void inscribirParticipanteEnEvento(Participante p, Evento e) {
        // Usamos la fecha actual por defecto
        String fecha = java.time.LocalDate.now().toString(); 
        
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO participa (id_persona, id_evento, fecha) VALUES (?, ?, ?)")) {
            
            if (con == null) {
                showAlert(AlertType.ERROR, "Error de conexión", 
                    "No se pudo establecer conexión con la base de datos");
                return;
            }
            
            ps.setInt(1, p.getId());
            ps.setInt(2, e.getId());
            ps.setString(3, fecha);
            
            int result = ps.executeUpdate();
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Inscripción exitosa", 
                    "Participante inscrito en el evento correctamente");
                cargarEventosDelParticipante(p.getId());
            } else {
                showAlert(AlertType.ERROR, "Error de inscripción", 
                    "No se pudo inscribir al participante en el evento");
            }
        } catch (SQLException ex) {
            // Manejo específico para el caso de violación de clave primaria
            if (ex.getMessage().contains("Duplicate entry") || 
                ex.getMessage().contains("PRIMARY") || 
                ex.getMessage().contains("UNIQUE")) {
                
                showAlert(AlertType.WARNING, "Inscripción duplicada", 
                    "Este participante ya está inscrito en el evento seleccionado.");
            } else {
                showAlert(AlertType.ERROR, "Error de base de datos", 
                    "Error al inscribir: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Error inesperado", 
                "Error al inscribir al participante: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Desinscribe a un participante de un evento, eliminando la relación de la tabla participa.
     * 
     * @param p El participante a desinscribir
     * @param e El evento del que se desinscribirá al participante
     */
    private void desinscribirParticipanteDeEvento(Participante p, Evento e) {
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM participa WHERE id_persona = ? AND id_evento = ?")) {
            
            if (con == null) {
                showAlert(AlertType.ERROR, "Error de conexión", 
                    "No se pudo establecer conexión con la base de datos");
                return;
            }
            
            ps.setInt(1, p.getId());
            ps.setInt(2, e.getId());
            
            int result = ps.executeUpdate();
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Desinscripción exitosa", 
                    "Participante eliminado del evento correctamente");
                cargarEventosDelParticipante(p.getId());
            } else {
                showAlert(AlertType.ERROR, "Error de desinscripción", 
                    "No se pudo eliminar al participante del evento");
            }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Error de desinscripción", 
                "Error al desinscribir al participante: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //--------------------------------------------------
    // GESTIÓN DE EVENTOS
    //--------------------------------------------------
    /**
     * Carga la lista de todos los eventos disponibles para mostrarlos en el ComboBox.
     * Este método actualiza el mapa de eventos y la lista de nombres para selección.
     */
    private void cargarEventos() {
        try {
            ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
            Evento.getAll(listaEventos);
            
            nombresEventos.clear();
            eventosMap.clear();
            
            for (Evento evento : listaEventos) {
                String nombre = evento.getNombre();
                nombresEventos.add(nombre);
                eventosMap.put(nombre, evento);
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de carga", 
                "No se pudieron cargar los eventos disponibles: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los eventos en los que participa un participante específico.
     * Actualiza la tabla detalle con estos eventos y maneja la visibilidad
     * del mensaje cuando no hay eventos asignados.
     * 
     * @param participanteId ID del participante cuyos eventos se cargarán
     */
    private void cargarEventosDelParticipante(int participanteId) {
        try {
            // Limpiar la lista actual
            listaEventosParticipante.clear();
            
            // Cargar los eventos del participante
            Participante.getEventosForParticipante(participanteId, listaEventosParticipante);
            
            // Comprobar si hay eventos para este participante
            boolean tieneEventos = !listaEventosParticipante.isEmpty();
            
            // Mostrar/ocultar elementos según corresponda
            if (hboxEventosDisponibles != null) {
                hboxEventosDisponibles.setVisible(!tieneEventos && tableView1.isVisible());
            }
            
            // Volver al modo normal (no mostrar eventos disponibles)
            mostrandoEventosDisponibles = false;
            
            // Actualizar la vista
            tableView1.refresh();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de carga", 
                "No se pudieron cargar los eventos del participante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los eventos en los que el participante no está inscrito.
     * Estos son eventos disponibles para su asignación.
     * 
     * @param participanteId ID del participante
     */
    private void cargarEventosDisponibles(int participanteId) {
        try {
            listaEventosParticipante.clear();
            
            try (Connection con = Conexion.conectarBD();
                 PreparedStatement ps = con.prepareStatement(
                     "SELECT e.* FROM evento e " +
                     "WHERE e.id NOT IN (SELECT id_evento FROM participa WHERE id_persona = ?)"
                 )) {
                
                if (con == null) {
                    showAlert(AlertType.ERROR, "Error de conexión", 
                        "No se pudo establecer conexión con la base de datos");
                    return;
                }
                
                ps.setInt(1, participanteId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String descripcion = rs.getString("descripcion");
                    String lugar = rs.getString("lugar");
                    String fecha_inicio = rs.getString("fecha_inicio");
                    String fecha_fin = rs.getString("fecha_fin");
                    int id_categoria = rs.getInt("id_categoria");

                    Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                    evento.setFechaParticipacion(""); // No hay fecha de participación aún
                    listaEventosParticipante.add(evento);
                }
                
                // Mostrar mensaje especial si no hay eventos disponibles
                if (listaEventosParticipante.isEmpty() && hboxEventosDisponibles != null) {
                    Label lblMensaje = new Label("No hay más eventos disponibles para inscripción");
                    lblMensaje.setStyle("-fx-text-fill: white;");
                    
                    hboxEventosDisponibles.getChildren().clear();
                    hboxEventosDisponibles.getChildren().add(lblMensaje);
                    hboxEventosDisponibles.setVisible(true);
                }
            }
            
            // Actualizar la UI
            tableView1.refresh();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de carga", 
                "No se pudieron cargar los eventos disponibles: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Alterna la visualización entre eventos asignados y eventos disponibles.
     * Responde a la acción del botón "Ver eventos disponibles"/"Volver a eventos asignados".
     */
    @FXML
    public void mostrarEventosDisponibles() {
        try {
            Participante participante = tableView.getSelectionModel().getSelectedItem();
            if (participante != null) {
                mostrandoEventosDisponibles = !mostrandoEventosDisponibles;
                
                if (mostrandoEventosDisponibles) {
                    // Cambiar a mostrar todos los eventos disponibles
                    if (btnMostrarEventosDisponibles != null) {
                        btnMostrarEventosDisponibles.setText("Volver a eventos asignados");
                    }
                    cargarEventosDisponibles(participante.getId());
                } else {
                    // Volver a mostrar solo los eventos del participante
                    if (btnMostrarEventosDisponibles != null) {
                        btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                    }
                    cargarEventosDelParticipante(participante.getId());
                }
                
                // Asegurar que la tabla es visible
                tableView1.setVisible(true);
            } else {
                showAlert(AlertType.WARNING, "Selección requerida", 
                    "Por favor, seleccione un participante primero");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "Error al cambiar la vista de eventos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE VENTANA
    //--------------------------------------------------
    /**
     * Configura la funcionalidad para mover la ventana arrastrando la barra de título.
     * Implementa un comportamiento de arrastrar y soltar personalizado ya que JavaFX
     * no proporciona esta funcionalidad para ventanas sin decoración.
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
            // No es crítico si falla, solo afecta a la capacidad de mover la ventana
            System.err.println("Error al configurar el movimiento de ventana: " + e.getMessage());
        }
    }

    /**
     * Configura los listeners para la selección y edición de participantes.
     * Establece la tabla como editable y asigna comportamientos cuando
     * cambia la selección de participantes.
     */
    private void configurarSeleccionYEdicion() {
        try {
            // Listener para cuando se selecciona un participante
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Cargar los eventos del participante seleccionado
                    cargarEventosDelParticipante(newSelection.getId());
                }
                
                // Refrescar la vista
                tableView.refresh();
            });
            
            // Hacer editables las tablas
            tableView.setEditable(true);
            tableView1.setEditable(true);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de configuración", 
                "Error al configurar la selección y edición: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------
    // OPERACIONES CRUD
    //--------------------------------------------------
    /**
     * Carga todos los participantes desde la base de datos a la lista observable.
     * Esta operación reemplaza el contenido actual de la lista.
     */
    private void loadData() {
        try {
            listaParticipantes.clear();
            Participante.getAll(listaParticipantes);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de carga", 
                "No se pudieron cargar los participantes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Añade una nueva fila vacía a la tabla de participantes para su edición.
     * Crea un nuevo objeto Participante temporal y lo añade a la tabla,
     * seleccionándolo para edición inmediata.
     * 
     * @throws IOException Si ocurre un error de E/S
     */
    @FXML
    public void addRow() throws IOException {
        try {
            // Crear un nuevo Participante
            Participante nuevo = new Participante(0, "", "", "", "");
            listaParticipantes.add(nuevo);
            tableView.getSelectionModel().select(nuevo);
            tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de creación", 
                "No se pudo crear un nuevo participante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda los cambios de un participante en la base de datos.
     * Si la operación tiene éxito, recarga la lista de participantes
     * y selecciona nuevamente el participante guardado.
     * 
     * @param p El participante a guardar
     */
    public void saveRow(Participante p) {
        try {
            // Validación básica de datos
            if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
                showAlert(AlertType.WARNING, "Datos incompletos", 
                    "El nombre del participante no puede estar vacío.");
                return;
            }
            
            if (p.getEmail() == null || p.getEmail().trim().isEmpty()) {
                showAlert(AlertType.WARNING, "Datos incompletos", 
                    "El email del participante no puede estar vacío.");
                return;
            }
            
            int result = p.save();
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Guardado exitoso", 
                    "Participante guardado correctamente.");
                
                // Guardar el ID para reseleccionar después de recargar
                int participanteId = p.getId();
                
                // Recargar datos
                loadData();
                
                // Volver a seleccionar el participante después de recargar
                for (Participante part : listaParticipantes) {
                    if (part.getId() == participanteId) {
                        tableView.getSelectionModel().select(part);
                        // Actualizar también la lista de eventos
                        cargarEventosDelParticipante(participanteId);
                        break;
                    }
                }
            } else {
                showAlert(AlertType.ERROR, "Error al guardar", 
                    "No se pudo guardar el participante. Verifique los datos.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error al guardar", 
                "Se produjo un error al guardar el participante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina el participante seleccionado de la base de datos,
     * previa confirmación del usuario.
     * También elimina todas sus participaciones en eventos.
     */
    @FXML
    public void deleteRow() {
        try {
            Participante p = tableView.getSelectionModel().getSelectedItem();
            if (p == null) {
                showAlert(AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un participante para eliminar.");
                return;
            }
            
            // Pedir confirmación antes de eliminar
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("¿Está seguro de eliminar este participante?");
            alert.setContentText("Esta acción también eliminará todas sus inscripciones a eventos y no se puede deshacer.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int rows = p.delete(p.getId());
                if (rows > 0) {
                    // Eliminar de la lista y actualizar la interfaz
                    listaParticipantes.remove(p);
                    tableView1.setVisible(false);
                    if (hboxEventosDisponibles != null) {
                        hboxEventosDisponibles.setVisible(false);
                    }
                    showAlert(AlertType.INFORMATION, "Eliminación exitosa", 
                        "Participante eliminado correctamente.");
                } else {
                    showAlert(AlertType.ERROR, "Error al eliminar", 
                        "No se pudo eliminar el participante. Verifique la conexión a la base de datos.");
                }
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error al eliminar", 
                "Se produjo un error al eliminar el participante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------
    // CONTROL DE VENTANA
    //--------------------------------------------------
    /**
     * Minimiza la ventana actual.
     * 
     * @param event El evento de acción que desencadenó esta llamada
     */
    @FXML
    private void minimizarVentana(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setIconified(true);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo minimizar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maximiza o restaura la ventana actual.
     * 
     * @param event El evento de acción que desencadenó esta llamada
     */
    @FXML
    private void maximizarVentana(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo maximizar/restaurar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana actual.
     * 
     * @param event El evento de acción que desencadenó esta llamada
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "No se pudo cerrar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------
    // MÉTODO AUXILIAR PARA MOSTRAR ALERTAS
    //--------------------------------------------------
    /**
     * Muestra un cuadro de diálogo de alerta con el tipo, título y mensaje especificados.
     * Utiliza Platform.runLater para asegurar que la alerta se muestre en el hilo de JavaFX.
     * 
     * @param type El tipo de alerta (información, advertencia, error, etc.)
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
            // Si no se puede mostrar la alerta (por ejemplo, si no estamos en el hilo de JavaFX)
            System.err.println("[ALERTA " + type + "] " + title + ": " + message);
            e.printStackTrace();
        }
    }
}