package com.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controlador para la vista de gestión de participantes.
 * Permite ver, añadir, editar y eliminar participantes, así como gestionar
 * su relación con los eventos en los que participan.
 */
public class ParticipanteController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    @FXML
    private HBox barraTitulo;
    private double xOffset = 0;
    private double yOffset = 0;

    // TableView y columnas para mostrar los participantes (tabla maestra)
    @FXML
    private TableView<Participante> tableView;

    @FXML
    private TableColumn<Participante, String> nombre;      // persona.nombre
    @FXML
    private TableColumn<Participante, String> apellido1;   // persona.apellido1
    @FXML
    private TableColumn<Participante, String> apellido2;   // persona.apellido2
    @FXML
    private TableColumn<Participante, String> email;       // participante.email
    @FXML
    private TableColumn<Participante, Void> accionesParticipante;
    
    // TableView y columnas para mostrar los eventos (tabla detalle)
    @FXML
    private TableView<Evento> tableView1;
    
    @FXML
    private TableColumn<Evento, String> colEvNombre;       // evento.nombre
    @FXML
    private TableColumn<Evento, String> colEvDescripcion;  // evento.descripcion
    @FXML
    private TableColumn<Evento, String> colEvLugar;        // evento.lugar
    @FXML
    private TableColumn<Evento, String> colEvFechaInicio;  // evento.fecha_inicio
    @FXML
    private TableColumn<Evento, String> colEvFechaFin;     // evento.fecha_fin
    @FXML
    private TableColumn<Evento, String> colFechaParticipacion; // fecha de participación
    @FXML
    private TableColumn<Evento, Void> colAcciones;

    // Nuevo componente para mostrar mensaje cuando no hay eventos
    @FXML
    private HBox hboxEventosDisponibles;
    
    @FXML
    private Button btnMostrarEventosDisponibles;

    // Listas de datos
    private ObservableList<Participante> listaParticipantes = FXCollections.observableArrayList();
    private ObservableList<Evento> listaEventosParticipante = FXCollections.observableArrayList();
    
    // Mapa para relacionar nombres de eventos con objetos Evento
    private Map<String, Evento> eventosMap = new HashMap<>();
    
    // Lista de eventos para el ComboBox
    private ObservableList<String> nombresEventos = FXCollections.observableArrayList();
    
    // Flag para controlar si se muestran todos los eventos o solo los del participante
    private boolean mostrandoEventosDisponibles = false;

    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    /**
     * Método de inicialización que se ejecuta al cargar la vista.
     * Configura los componentes, carga los datos iniciales y establece los listeners.
     */
    public void initialize() {
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
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE COLUMNAS
    //--------------------------------------------------
    /**
     * Configura las columnas de ambas tablas, sus fábricas de valores y celdas,
     * así como los manejadores de eventos para acciones específicas.
     */
    private void configurarColumnas() {
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
        colFechaParticipacion.setCellValueFactory(new PropertyValueFactory<>("fechaParticipacion")); // fecha de participación
       
        // Hacer editables las columnas de participantes
        nombre.setCellFactory(TextFieldTableCell.forTableColumn());
        apellido1.setCellFactory(TextFieldTableCell.forTableColumn());
        apellido2.setCellFactory(TextFieldTableCell.forTableColumn());
        email.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Configurar columna de eventos como ComboBox con interfaz personalizada para la segunda tabla
        colEvNombre.setCellFactory(column -> {
            ComboBoxTableCell<Evento, String> cell = new ComboBoxTableCell<Evento, String>(nombresEventos) {
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
            };
            
            return cell;
        });
        
        // Configurar manejador de eventos para cuando se selecciona un nuevo evento en el combo
        colEvNombre.setOnEditCommit(event -> {
            Evento evento = event.getRowValue();
            String nuevoNombre = event.getNewValue();
            Participante participante = tableView.getSelectionModel().getSelectedItem();
            
            if (participante != null && eventosMap.containsKey(nuevoNombre)) {
                Evento nuevoEvento = eventosMap.get(nuevoNombre);
                
                // Actualizar la relación en la base de datos
                try (Connection con = Conexion.conectarBD();
                     PreparedStatement ps = con.prepareStatement("UPDATE participa SET id_evento = ? WHERE id_persona = ? AND id_evento = ?")) {
                    
                    ps.setInt(1, nuevoEvento.getId());
                    ps.setInt(2, participante.getId());
                    ps.setInt(3, evento.getId());
                    
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        showAlert(AlertType.INFORMATION, "Cambio de evento", "El participante ha sido asignado al nuevo evento");
                        // Recargar los eventos del participante para reflejar el cambio
                        cargarEventosDelParticipante(participante.getId());
                    } else {
                        showAlert(AlertType.ERROR, "Error", "No se pudo cambiar el evento del participante");
                    }
                } catch (Exception ex) {
                    showAlert(AlertType.ERROR, "Error", "Error al cambiar el evento: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Configurar columna de acciones para la tabla de participantes
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
                            Participante p = getTableView().getItems().get(getIndex());
                            saveRow(p);
                        });
                        
                        btnEliminar.setOnAction(event -> {
                            tableView.getSelectionModel().select(getIndex());
                            deleteRow();
                        });
                        
                        btnVerMas.setOnAction(event -> {
                            Participante p = getTableView().getItems().get(getIndex());
                            tableView.getSelectionModel().select(p);
                            
                            // Cargar los eventos del participante
                            cargarEventosDelParticipante(p.getId());
                            
                            // Mostrar u ocultar la tabla de eventos
                            if (tableView1.isVisible() && tableView.getSelectionModel().getSelectedItem() == p) {
                                tableView1.setVisible(false);
                                btnVerMas.setText("⤵");
                                // También ocultar el mensaje si está visible
                                if (hboxEventosDisponibles != null) {
                                    hboxEventosDisponibles.setVisible(false);
                                }
                                mostrandoEventosDisponibles = false;
                            } else {
                                tableView1.setVisible(true);
                                btnVerMas.setText("⤴");
                                // Mostrar mensaje si no hay eventos
                                if (hboxEventosDisponibles != null && listaEventosParticipante.isEmpty()) {
                                    hboxEventosDisponibles.setVisible(true);
                                }
                            }
                        });
                    }
        
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Personalizar estilos de botones
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
        
        // Configurar columna de acciones para la tabla de eventos
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
                            // Añadir el participante al evento
                            Evento evento = getTableView().getItems().get(getIndex());
                            Participante participante = tableView.getSelectionModel().getSelectedItem();
                            if (participante != null) {
                                // Crear la relación en la base de datos
                                inscribirParticipanteEnEvento(participante, evento);
                                
                                // Actualizar la vista
                                cargarEventosDelParticipante(participante.getId());
                                
                                // Volver a mostrar solo los eventos del participante después de inscribir
                                mostrandoEventosDisponibles = false;
                                if (btnMostrarEventosDisponibles != null) {
                                    btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                                }
                            }
                        });
                        
                        btnDesinscribir.setOnAction(event -> {
                            // Quitar al participante del evento
                            Evento evento = getTableView().getItems().get(getIndex());
                            Participante participante = tableView.getSelectionModel().getSelectedItem();
                            if (participante != null) {
                                // Eliminar la relación de la base de datos
                                desinscribirParticipanteDeEvento(participante, evento);
                                
                                // Actualizar la vista
                                cargarEventosDelParticipante(participante.getId());
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
                                setGraphic(null); // Si el índice no es válido, no mostramos nada
                            }
                        }
                    }
                };
            }
        });

        // Configurar políticas de redimensionamiento
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Inscribe a un participante en un evento, creando la relación en la tabla participa.
     * 
     * @param p El participante a inscribir
     * @param e El evento en el que se inscribirá al participante
     */
    private void inscribirParticipanteEnEvento(Participante p, Evento e) {
        String fecha = java.time.LocalDate.now().toString(); // Usamos la fecha actual por defecto
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement("INSERT INTO participa (id_persona, id_evento, fecha) VALUES (?, ?, ?)")) {
            
            ps.setInt(1, p.getId());
            ps.setInt(2, e.getId());
            ps.setString(3, fecha);
            
            int result = ps.executeUpdate();
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Inscripción", "Participante inscrito en el evento correctamente");
                cargarEventosDelParticipante(p.getId());
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo inscribir al participante en el evento");
            }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Error", "Error al inscribir: " + ex.getMessage());
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
             PreparedStatement ps = con.prepareStatement("DELETE FROM participa WHERE id_persona = ? AND id_evento = ?")) {
            
            ps.setInt(1, p.getId());
            ps.setInt(2, e.getId());
            
            int result = ps.executeUpdate();
            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Desinscripción", "Participante eliminado del evento correctamente");
                cargarEventosDelParticipante(p.getId());
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar al participante del evento");
            }
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Error", "Error al desinscribir: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE EVENTOS
    //--------------------------------------------------
    /**
     * Carga la lista de todos los eventos disponibles para mostrarlos en el ComboBox.
     */
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
    
    /**
     * Carga los eventos en los que participa un participante específico.
     * 
     * @param participanteId ID del participante cuyos eventos se cargarán
     */
    private void cargarEventosDelParticipante(int participanteId) {
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
    }

    /**
     * Método para cargar eventos disponibles (no asignados al participante)
     * @param participanteId ID del participante
     */
    private void cargarEventosDisponibles(int participanteId) {
        listaEventosParticipante.clear();
        
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT e.* FROM evento e " +
                 "WHERE e.id NOT IN (SELECT id_evento FROM participa WHERE id_persona = ?)"
             )) {
            
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
        } catch (Exception e) {
            System.out.println("Error al cargar eventos disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Actualizar la UI
        tableView1.refresh();
    }
    
    /**
     * Muestra u oculta los eventos disponibles para inscripción
     */
    @FXML
    public void mostrarEventosDisponibles() {
        Participante participante = tableView.getSelectionModel().getSelectedItem();
        if (participante != null) {
            mostrandoEventosDisponibles = !mostrandoEventosDisponibles;
            
            if (mostrandoEventosDisponibles) {
                // Cambiar a mostrar todos los eventos disponibles
                if (btnMostrarEventosDisponibles != null) {
                    btnMostrarEventosDisponibles.setText("Volver a eventos asignados");
                }
                cargarEventosDisponibles(participante.getId());
                
                // Ocultar el mensaje
                if (hboxEventosDisponibles != null) {
                    hboxEventosDisponibles.setVisible(false);
                }
            } else {
                // Volver a mostrar solo los eventos del participante
                if (btnMostrarEventosDisponibles != null) {
                    btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                }
                cargarEventosDelParticipante(participante.getId());
                
                // Mostrar el mensaje si no hay eventos
                if (hboxEventosDisponibles != null && listaEventosParticipante.isEmpty()) {
                    hboxEventosDisponibles.setVisible(true);
                }
            }
        }
    }

    //--------------------------------------------------
    // CONFIGURACIÓN DE MOVIMIENTO Y EDICIÓN
    //--------------------------------------------------
    /**
     * Configura la funcionalidad para mover la ventana arrastrando la barra de título.
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

    /**
     * Configura los listeners para la selección y edición de participantes.
     */
    private void configurarSeleccionYEdicion() {
        // Listener para cuando se selecciona un participante
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Cargar los eventos del participante seleccionado
                cargarEventosDelParticipante(newSelection.getId());
                
                // No mostramos automáticamente la tabla, solo al hacer clic en "Ver más"
                // tableView1.setVisible(false);
            }
            
            // Refrescar la vista
            tableView.refresh();
        });
        
        // Hacer editables las tablas
        tableView.setEditable(true);
        tableView1.setEditable(true);
    }

    //--------------------------------------------------
    // MANEJO DE DATOS
    //--------------------------------------------------
    /**
     * Carga todos los participantes desde la base de datos a la lista observable.
     */
    private void loadData() {
        listaParticipantes.clear();
        Participante.getAll(listaParticipantes);
    }

    /**
     * Añade una nueva fila vacía a la tabla de participantes para su edición.
     * 
     * @throws IOException Si ocurre un error de E/S
     */
    @FXML
    public void addRow() throws IOException {
        // Crear un nuevo Participante (sin datos de join, se rellenarán al guardar)
        Participante nuevo = new Participante(0, "", "", "", "");
        listaParticipantes.add(nuevo);
        tableView.getSelectionModel().select(nuevo);
        tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
    }

    /**
     * Guarda los cambios de un participante en la base de datos.
     * 
     * @param p El participante a guardar
     */
    public void saveRow(Participante p) {
        int result = p.save();
        if (result > 0) {
            showAlert(AlertType.INFORMATION, "Guardado", "Participante guardado correctamente.");
            loadData();
            
            // Volver a seleccionar el participante después de recargar los datos
            for (Participante part : listaParticipantes) {
                if (part.getId() == p.getId()) {
                    tableView.getSelectionModel().select(part);
                    // Actualizar también la lista de eventos
                    cargarEventosDelParticipante(p.getId());
                    break;
                }
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "No se pudo guardar el participante.");
        }
    }

    /**
     * Elimina el participante seleccionado de la base de datos.
     */
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
                tableView1.setVisible(false);
                if (hboxEventosDisponibles != null) {
                    hboxEventosDisponibles.setVisible(false);
                }
                showAlert(AlertType.INFORMATION, "Eliminado", "Participante eliminado correctamente.");
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el participante.");
            }
        }
    }

    //--------------------------------------------------
    // CONTROLES DE VENTANA
    //--------------------------------------------------
    /**
     * Minimiza la ventana actual.
     * 
     * @param event El evento de acción que desencadenó esta llamada
     */
    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Maximiza o restaura la ventana actual.
     * 
     * @param event El evento de acción que desencadenó esta llamada
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
     * @param event El evento de acción que desencadenó esta llamada
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //--------------------------------------------------
    // MÉTODO AUXILIAR PARA MOSTRAR ALERTAS
    //--------------------------------------------------
    /**
     * Muestra un cuadro de diálogo de alerta con el tipo, título y mensaje especificados.
     * 
     * @param type El tipo de alerta (información, advertencia, error, etc.)
     * @param title El título del cuadro de diálogo
     * @param message El mensaje a mostrar
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}