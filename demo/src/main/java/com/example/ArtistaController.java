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
 * Controlador para la gestión de artistas en la aplicación.
 * Permite ver, crear, editar y eliminar artistas, así como gestionar
 * sus relaciones con eventos mediante la tabla participa.
 * 
 * <p>Este controlador maneja dos tablas principales:
 * <ul>
 *   <li>Una tabla de artistas que muestra los datos básicos de cada artista</li>
 *   <li>Una tabla de eventos que muestra los eventos asociados al artista seleccionado</li>
 * </ul>
 * </p>
 * 
 * @author Jesús
 * @version 1.0
 */
public class ArtistaController {

    //--------------------------------------------------
    // CONTROLES DE INTERFAZ
    //--------------------------------------------------
    /**
     * Barra de título personalizada para mover la ventana.
     */
    @FXML
    private HBox barraTitulo;
    private double xOffset = 0;
    private double yOffset = 0;
    
    /**
     * Tabla principal que muestra la lista de artistas.
     */
    @FXML
    private TableView<Artista> tableView;
    
    /**
     * Columna para el nombre del artista.
     */
    @FXML
    private TableColumn<Artista, String> nombre;
    
    /**
     * Columna para el primer apellido del artista.
     */
    @FXML
    private TableColumn<Artista, String> apellido1;
    
    /**
     * Columna para el segundo apellido del artista.
     */
    @FXML
    private TableColumn<Artista, String> apellido2;
    
    /**
     * Columna para la fotografía del artista.
     */
    @FXML
    private TableColumn<Artista, String> fotografia;
    
    /**
     * Columna para la obra destacada del artista.
     */
    @FXML
    private TableColumn<Artista, String> obraDestacada;
    
    /**
     * Columna para botones de acción (guardar, eliminar, ver eventos).
     */
    @FXML
    private TableColumn<Artista, Void> accionesArtista;
    
    //--------------------------------------------------
    // TABLA DE EVENTOS (DETALLE)
    //--------------------------------------------------
    /**
     * Tabla que muestra los eventos asociados al artista seleccionado.
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
     * Columna para la fecha de fin del evento.
     */
    @FXML
    private TableColumn<Evento, String> colEvFechaFin;
    
    /**
     * Columna para la fecha de actuación del artista en el evento.
     */
    @FXML
    private TableColumn<Evento, String> colFechaActuacion;
    
    /**
     * Columna para botones de acción de eventos (añadir, quitar).
     */
    @FXML
    private TableColumn<Evento, Void> colAcciones;
    
    /**
     * Panel para mostrar mensaje cuando no hay eventos asignados.
     */
    @FXML
    private HBox hboxEventosDisponibles;
    
    /**
     * Botón para alternar entre eventos asignados y disponibles.
     */
    @FXML
    private Button btnMostrarEventosDisponibles;
    
    /**
     * Botón para añadir eventos explícitamente.
     */
    @FXML
    private Button btnAnadirEvento;
    
    //--------------------------------------------------
    // COLECCIONES DE DATOS
    //--------------------------------------------------
    /**
     * Lista observable de todos los artistas mostrados en la tabla principal.
     */
    private ObservableList<Artista> listaArtistas = FXCollections.observableArrayList();
    
    /**
     * Lista observable de eventos asociados al artista seleccionado.
     */
    private ObservableList<Evento> listaEventosArtista = FXCollections.observableArrayList();
    
    /**
     * Mapa para relacionar nombres de eventos con objetos Evento.
     */
    private Map<String, Evento> eventosMap = new HashMap<>();
    
    /**
     * Lista de nombres de eventos para el ComboBox.
     */
    private ObservableList<String> nombresEventos = FXCollections.observableArrayList();
    
    /**
     * Flag para controlar si se están mostrando eventos disponibles o asignados.
     */
    private boolean mostrandoEventosDisponibles = false;
    
    //--------------------------------------------------
    // INICIALIZACIÓN
    //--------------------------------------------------
    /**
     * Método de inicialización que se ejecuta al cargar la vista.
     * Configura las tablas, columnas, carga los datos iniciales y establece los listeners.
     */
    public void initialize() {
        cargarEventos();
        configurarColumnas();
        configurarMovimientoVentana();
        configurarSeleccionYEdicion();
        
        tableView.setItems(listaArtistas);
        tableView1.setItems(listaEventosArtista);
        tableView1.setVisible(false);
        if(hboxEventosDisponibles != null) {
            hboxEventosDisponibles.setVisible(false);
        }
        loadData();
    }
    
    //--------------------------------------------------
    // CONFIGURACIÓN DE COLUMNAS Y CELDAS
    //--------------------------------------------------
    /**
     * Configura las columnas de ambas tablas, establece sus propiedades, fábricas de celdas
     * y manejadores de eventos.
     */
    private void configurarColumnas() {
        // Columnas de la tabla de artistas
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        apellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        fotografia.setCellValueFactory(new PropertyValueFactory<>("fotografia"));
        obraDestacada.setCellValueFactory(new PropertyValueFactory<>("obraDestacada"));
        
        // Hacer editables las columnas de artistas
        nombre.setCellFactory(TextFieldTableCell.forTableColumn());
        apellido1.setCellFactory(TextFieldTableCell.forTableColumn());
        apellido2.setCellFactory(TextFieldTableCell.forTableColumn());
        fotografia.setCellFactory(TextFieldTableCell.forTableColumn());
        obraDestacada.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Columnas de la tabla de eventos
        colEvNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEvDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEvLugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
        colEvFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
        colEvFechaFin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
        colFechaActuacion.setCellValueFactory(new PropertyValueFactory<>("fechaActuacion"));
        
        // ComboBox en columna de nombre de evento para permitir cambios
        colEvNombre.setCellFactory(column -> {
            ComboBoxTableCell<Evento, String> cell = new ComboBoxTableCell<>(nombresEventos) {
                {
                    setTooltip(new Tooltip("Haga clic para seleccionar un evento"));
                }
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Crear un contenedor para el texto y el indicador de desplegable
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
        
        // Manejar cambios en el ComboBox de eventos
        colEvNombre.setOnEditCommit(event -> {
            Evento evento = event.getRowValue();
            String nuevoNombre = event.getNewValue();
            Artista artista = tableView.getSelectionModel().getSelectedItem();
            if(artista != null && eventosMap.containsKey(nuevoNombre)){
                Evento nuevoEvento = eventosMap.get(nuevoNombre);
                try(Connection con = Conexion.conectarBD();
                    PreparedStatement ps = con.prepareStatement("UPDATE participa SET id_evento = ? WHERE id_persona = ? AND id_evento = ?")){
                    ps.setInt(1, nuevoEvento.getId());
                    ps.setInt(2, artista.getId());
                    ps.setInt(3, evento.getId());
                    int res = ps.executeUpdate();
                    if(res>0){
                        showAlert(AlertType.INFORMATION, "Cambio de evento", "El artista ha sido asignado al nuevo evento");
                        cargarEventosDelArtista(artista.getId());
                    } else {
                        showAlert(AlertType.ERROR, "Error", "No se pudo cambiar el evento del artista");
                    }
                } catch(Exception ex){
                    showAlert(AlertType.ERROR, "Error", "Error al cambiar el evento: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        // Configurar columna de acciones para la tabla de artistas
        accionesArtista.setCellFactory(new Callback<TableColumn<Artista, Void>, TableCell<Artista, Void>>() {
            @Override
            public TableCell<Artista, Void> call(final TableColumn<Artista, Void> param) {
                return new TableCell<Artista, Void>() {
                    private final Button btnGuardar = new Button("✔");
                    private final Button btnEliminar = new Button("✖");
                    private final Button btnVerMas = new Button("⤵");
                    
                    {
                        btnGuardar.setTooltip(new Tooltip("Guardar cambios"));
                        btnEliminar.setTooltip(new Tooltip("Eliminar artista"));
                        btnVerMas.setTooltip(new Tooltip("Ver eventos"));
                        
                        btnGuardar.setOnAction(event -> {
                            Artista a = getTableView().getItems().get(getIndex());
                            saveRow(a);
                        });
                        btnEliminar.setOnAction(event -> {
                            tableView.getSelectionModel().select(getIndex());
                            deleteRow();
                        });
                        btnVerMas.setOnAction(event -> {
                            Artista a = getTableView().getItems().get(getIndex());
                            tableView.getSelectionModel().select(a);
                            cargarEventosDelArtista(a.getId());
                            if(tableView1.isVisible() && tableView.getSelectionModel().getSelectedItem()==a){
                                tableView1.setVisible(false);
                                btnVerMas.setText("⤵");
                                if(hboxEventosDisponibles!=null) hboxEventosDisponibles.setVisible(false);
                                mostrandoEventosDisponibles = false;
                            } else {
                                tableView1.setVisible(true);
                                btnVerMas.setText("⤴");
                                if(hboxEventosDisponibles!=null && listaEventosArtista.isEmpty()){
                                    hboxEventosDisponibles.setVisible(true);
                                }
                            }
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setGraphic(null);
                        } else {
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
                        btnInscribir.setTooltip(new Tooltip("Asignar a este evento"));
                        btnDesinscribir.setTooltip(new Tooltip("Quitar de este evento"));
                        
                        btnInscribir.setOnAction(event -> {
                            Evento evento = getTableView().getItems().get(getIndex());
                            Artista artista = tableView.getSelectionModel().getSelectedItem();
                            if(artista != null){
                                asignarArtistaAEvento(artista, evento);
                                cargarEventosDelArtista(artista.getId());
                                mostrandoEventosDisponibles = false;
                                if(btnMostrarEventosDisponibles!=null)
                                    btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                            }
                        });
                        
                        btnDesinscribir.setOnAction(event -> {
                            Evento evento = getTableView().getItems().get(getIndex());
                            Artista artista = tableView.getSelectionModel().getSelectedItem();
                            if(artista != null){
                                quitarArtistaDeEvento(artista, evento);
                                cargarEventosDelArtista(artista.getId());
                            }
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setGraphic(null);
                        } else {
                            btnInscribir.getStyleClass().add("action-button");
                            btnDesinscribir.getStyleClass().add("action-button");
                            HBox hBox = new HBox(5);
                            int index = getIndex();
                            ObservableList<Evento> items = getTableView().getItems();
                            if(index>=0 && index<items.size()){
                                Evento evento = items.get(index);
                                String fechaActuacion = evento.getFechaActuacion();
                                if(mostrandoEventosDisponibles || fechaActuacion==null || fechaActuacion.isEmpty()){
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
        
        // Configurar políticas de redimensionamiento de columnas
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    //--------------------------------------------------
    // OPERACIONES DE RELACIÓN ARTISTA-EVENTO
    //--------------------------------------------------
    /**
     * Asigna un artista a un evento mediante la creación de un registro en la tabla participa.
     * 
     * @param a El artista a asignar
     * @param e El evento al que se asignará el artista
     */
    private void asignarArtistaAEvento(Artista a, Evento e) {
        String fecha = java.time.LocalDate.now().toString();
        try(Connection con = Conexion.conectarBD();
            PreparedStatement ps = con.prepareStatement("INSERT INTO participa (id_persona, id_evento, fecha) VALUES (?, ?, ?)")){
            
            if (con == null) {
                showAlert(AlertType.ERROR, "Error de conexión", "No se pudo establecer conexión con la base de datos");
                return;
            }
            
            ps.setInt(1, a.getId());
            ps.setInt(2, e.getId());
            ps.setString(3, fecha);
            int res = ps.executeUpdate();
            if(res>0){
                showAlert(AlertType.INFORMATION, "Asignación", "Artista asignado al evento correctamente");
                // Recargar los eventos del artista
                cargarEventosDelArtista(a.getId());
                
                // Asegurar que se muestra la vista correcta después de asignar
                if(mostrandoEventosDisponibles) {
                    // Recargar los eventos disponibles para quitar el que acabamos de asignar
                    cargarEventosDisponibles(a.getId());
                } else {
                    // Asegurar que se muestran los eventos asignados
                    cargarEventosDelArtista(a.getId());
                }
                
                // Asegurar que la tabla está visible
                tableView1.setVisible(true);
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo asignar al artista al evento");
            }
        } catch(Exception ex){
            // Manejo específico para violación de clave primaria (evento ya asignado)
            if (ex.getMessage().contains("Duplicate entry") || ex.getMessage().contains("PRIMARY")) {
                showAlert(AlertType.ERROR, "Error", "Este artista ya está asignado a este evento");
            } else {
                showAlert(AlertType.ERROR, "Error", "Error al asignar: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Elimina la asignación de un artista a un evento, borrando el registro de la tabla participa.
     * 
     * @param a El artista a desvincular
     * @param e El evento del que se desvinculará
     */
    private void quitarArtistaDeEvento(Artista a, Evento e) {
        try(Connection con = Conexion.conectarBD();
            PreparedStatement ps = con.prepareStatement("DELETE FROM participa WHERE id_persona = ? AND id_evento = ?")){
            
            if (con == null) {
                showAlert(AlertType.ERROR, "Error de conexión", "No se pudo establecer conexión con la base de datos");
                return;
            }
            
            ps.setInt(1, a.getId());
            ps.setInt(2, e.getId());
            int res = ps.executeUpdate();
            if(res>0){
                showAlert(AlertType.INFORMATION, "Eliminación", "Artista eliminado del evento correctamente");
                cargarEventosDelArtista(a.getId());
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar al artista del evento");
            }
        } catch(Exception ex){
            showAlert(AlertType.ERROR, "Error", "Error al quitar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    //--------------------------------------------------
    // CARGA DE DATOS Y EVENTOS
    //--------------------------------------------------
    /**
     * Carga todos los eventos disponibles en la base de datos para
     * su uso en los ComboBox y otras operaciones.
     */
    private void cargarEventos() {
        ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
        Evento.getAll(listaEventos);
        nombresEventos.clear();
        eventosMap.clear();
        for(Evento evento : listaEventos) {
            String nom = evento.getNombre();
            nombresEventos.add(nom);
            eventosMap.put(nom, evento);
        }
    }
    
    /**
     * Carga los eventos asociados a un artista específico y actualiza
     * la interfaz según corresponda.
     * 
     * @param artistaId ID del artista cuyos eventos se cargarán
     */
    private void cargarEventosDelArtista(int artistaId) {
        listaEventosArtista.clear();
        
        try {
            Artista.getEventosForArtista(artistaId, listaEventosArtista);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudieron cargar los eventos del artista: " + e.getMessage());
            e.printStackTrace();
        }
        
        boolean tieneEventos = !listaEventosArtista.isEmpty();
        if(hboxEventosDisponibles != null) {
            hboxEventosDisponibles.setVisible(!tieneEventos && tableView1.isVisible());
        }
        mostrandoEventosDisponibles = false;
        tableView1.refresh();
    }
    
    /**
     * Muestra los eventos disponibles para el artista seleccionado
     * (aquellos en los que no participa actualmente).
     */
    @FXML
    public void mostrarEventosDisponibles() {
        Artista artista = tableView.getSelectionModel().getSelectedItem();
        if(artista != null) {
            mostrandoEventosDisponibles = !mostrandoEventosDisponibles;
            if(mostrandoEventosDisponibles){
                if(btnMostrarEventosDisponibles != null)
                    btnMostrarEventosDisponibles.setText("Volver a eventos asignados");
                cargarEventosDisponibles(artista.getId());
            } else {
                if(btnMostrarEventosDisponibles != null)
                    btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                cargarEventosDelArtista(artista.getId());
            }
            
            // Asegurar que la tabla de eventos es visible
            tableView1.setVisible(true);
        } else {
            showAlert(AlertType.WARNING, "Selección", "Debe seleccionar un artista primero");
        }
    }
    
    /**
     * Carga los eventos en los que el artista no participa actualmente
     * para permitir su asignación.
     * 
     * @param artistaId ID del artista para el que se cargarán eventos disponibles
     */
    private void cargarEventosDisponibles(int artistaId) {
        listaEventosArtista.clear();
        try(Connection con = Conexion.conectarBD();
            PreparedStatement ps = con.prepareStatement(
                // Consulta para mostrar solo eventos NO asignados al artista
                "SELECT e.* FROM evento e WHERE e.id NOT IN (SELECT id_evento FROM participa WHERE id_persona = ?)"
            )){
            
            if (con == null) {
                showAlert(AlertType.ERROR, "Error de conexión", "No se pudo establecer conexión con la base de datos");
                return;
            }
            
            ps.setInt(1, artistaId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String nom = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String lugar = rs.getString("lugar");
                String fecha_inicio = rs.getString("fecha_inicio");
                String fecha_fin = rs.getString("fecha_fin");
                int id_categoria = rs.getInt("id_categoria");
                Evento evento = new Evento(id, nom, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                evento.setFechaActuacion(""); // Marca como evento no asignado
                listaEventosArtista.add(evento);
            }
            
            // Mostrar mensaje si no hay eventos disponibles
            if (listaEventosArtista.isEmpty() && hboxEventosDisponibles != null) {
                // Modificar el mensaje para indicar que no hay eventos disponibles
                Label lblNoEventos = new Label("No hay más eventos disponibles para asignar");
                lblNoEventos.setTextFill(javafx.scene.paint.Color.WHITE);
                hboxEventosDisponibles.getChildren().clear();
                hboxEventosDisponibles.getChildren().add(lblNoEventos);
                hboxEventosDisponibles.setVisible(true);
            } else if (hboxEventosDisponibles != null) {
                hboxEventosDisponibles.setVisible(false);
            }
            
        } catch(Exception e){
            showAlert(AlertType.ERROR, "Error", "Error al cargar eventos disponibles: " + e.getMessage());
            System.out.println("Error al cargar eventos disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        tableView1.refresh();
    }
    
    //--------------------------------------------------
    // CONFIGURACIÓN DE LA INTERFAZ
    //--------------------------------------------------
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
    
    /**
     * Configura los listeners para la selección y edición de artistas.
     */
    private void configurarSeleccionYEdicion() {
        // Listener para cuando se selecciona un artista
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                cargarEventosDelArtista(newSelection.getId());
            }
            tableView.refresh();
        });
        
        // Hacer editables las tablas
        tableView.setEditable(true);
        tableView1.setEditable(true);
    }
    
    //--------------------------------------------------
    // MÉTODOS CRUD PARA ARTISTAS
    //--------------------------------------------------
    /**
     * Carga todos los artistas desde la base de datos.
     */
    private void loadData() {
        try {
            listaArtistas.clear();
            Artista.getAll(listaArtistas);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "No se pudieron cargar los datos de artistas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Añade una nueva fila vacía a la tabla de artistas para permitir la creación.
     * 
     * @throws IOException Si ocurre algún error de entrada/salida
     */
    @FXML
    public void addRow() throws IOException {
        Artista nuevo = new Artista(0, "", "", "", "", "");
        listaArtistas.add(nuevo);
        tableView.getSelectionModel().select(nuevo);
        tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
    }
    
    /**
     * Guarda los cambios realizados a un artista en la base de datos.
     * 
     * @param a El artista a guardar o actualizar
     */
    public void saveRow(Artista a) {
        try {
            int res = a.save();
            if(res > 0) {
                showAlert(AlertType.INFORMATION, "Guardado", "Artista guardado correctamente.");
                loadData();
                for(Artista art : listaArtistas) {
                    if(art.getId() == a.getId()){
                        tableView.getSelectionModel().select(art);
                        cargarEventosDelArtista(a.getId());
                        break;
                    }
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo guardar el artista.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Error al guardar el artista: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina el artista seleccionado de la base de datos,
     * previa confirmación del usuario.
     */
    @FXML
    public void deleteRow() {
        Artista a = tableView.getSelectionModel().getSelectedItem();
        if(a == null){
            showAlert(AlertType.WARNING, "Eliminar", "Seleccione un artista a eliminar.");
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿Está seguro de eliminar el artista?");
        alert.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            try {
                int rows = a.delete(a.getId());
                if(rows > 0){
                    listaArtistas.remove(a);
                    tableView1.setVisible(false);
                    if(hboxEventosDisponibles != null)
                        hboxEventosDisponibles.setVisible(false);
                    showAlert(AlertType.INFORMATION, "Eliminado", "Artista eliminado correctamente.");
                } else {
                    showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el artista.");
                }
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Error al eliminar el artista: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    //--------------------------------------------------
    // CONTROL DE VENTANA
    //--------------------------------------------------
    /**
     * Minimiza la ventana de la aplicación.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
    
    /**
     * Maximiza o restaura la ventana de la aplicación.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void maximizarVentana(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(stage.isMaximized()){
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }
    
    /**
     * Cierra la ventana de la aplicación.
     * 
     * @param event El evento que desencadenó esta acción
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    //--------------------------------------------------
    // UTILIDADES
    //--------------------------------------------------
    /**
     * Muestra un diálogo de alerta con el tipo, título y mensaje especificados.
     * 
     * @param type El tipo de alerta (información, error, etc.)
     * @param title El título del diálogo
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