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
 * Controlador para la gestión de artistas.
 * Implementa la funcionalidad de listar, agregar, modificar y eliminar artistas,
 * así como asignar eventos mediante la tabla participa.
 */
public class ArtistaController {

    // Controles de interfaz
    @FXML
    private HBox barraTitulo;
    private double xOffset = 0;
    private double yOffset = 0;
    
    @FXML
    private TableView<Artista> tableView;
    @FXML
    private TableColumn<Artista, String> nombre;       // persona.nombre
    @FXML
    private TableColumn<Artista, String> apellido1;    // persona.apellido1
    @FXML
    private TableColumn<Artista, String> apellido2;    // persona.apellido2
    @FXML
    private TableColumn<Artista, String> fotografia;   // artista.fotografia
    @FXML
    private TableColumn<Artista, String> obraDestacada;  // artista.obra_destacada
    @FXML
    private TableColumn<Artista, Void> accionesArtista;
    
    // Tabla para los eventos asociados al artista
    @FXML
    private TableView<Evento> tableView1;
    @FXML
    private TableColumn<Evento, String> colEvNombre;      // evento.nombre
    @FXML
    private TableColumn<Evento, String> colEvDescripcion; // evento.descripcion
    @FXML
    private TableColumn<Evento, String> colEvLugar;       // evento.lugar
    @FXML
    private TableColumn<Evento, String> colEvFechaInicio; // evento.fecha_inicio
    @FXML
    private TableColumn<Evento, String> colEvFechaFin;    // evento.fecha_fin
    @FXML
    private TableColumn<Evento, String> colFechaActuacion; // fecha de actuación
    @FXML
    private TableColumn<Evento, Void> colAcciones;
    
    @FXML
    private HBox hboxEventosDisponibles;
    @FXML
    private Button btnMostrarEventosDisponibles;
    
    // Listas de datos
    private ObservableList<Artista> listaArtistas = FXCollections.observableArrayList();
    private ObservableList<Evento> listaEventosArtista = FXCollections.observableArrayList();
    
    // Mapa para nombres de eventos
    private Map<String, Evento> eventosMap = new HashMap<>();
    private ObservableList<String> nombresEventos = FXCollections.observableArrayList();
    
    private boolean mostrandoEventosDisponibles = false;
    
    // Inicialización de la vista
    public void initialize() {
        cargarEventos();
        configurarColumnas();
        configurarMovimientoVentana();
        configurarSeleccionYEdicion();
        
        tableView.setItems(listaArtistas);
        tableView1.setItems(listaEventosArtista);
        tableView1.setVisible(false);
        if(hboxEventosDisponibles!=null) {
            hboxEventosDisponibles.setVisible(false);
        }
        loadData();
    }
    
    private void configurarColumnas() {
        // Columnas de la tabla de artistas
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        apellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        fotografia.setCellValueFactory(new PropertyValueFactory<>("fotografia"));
        obraDestacada.setCellValueFactory(new PropertyValueFactory<>("obraDestacada"));
        
        // Editables
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
        
        // ComboBox en columna de nombre de evento
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
        
        // Columna de acciones para artistas
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
        
        // Columna de acciones para la tabla de eventos
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
        
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView1.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void asignarArtistaAEvento(Artista a, Evento e) {
        String fecha = java.time.LocalDate.now().toString();
        try(Connection con = Conexion.conectarBD();
            PreparedStatement ps = con.prepareStatement("INSERT INTO participa (id_persona, id_evento, fecha) VALUES (?, ?, ?)")){
            ps.setInt(1, a.getId());
            ps.setInt(2, e.getId());
            ps.setString(3, fecha);
            int res = ps.executeUpdate();
            if(res>0){
                showAlert(AlertType.INFORMATION, "Asignación", "Artista asignado al evento correctamente");
                cargarEventosDelArtista(a.getId());
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo asignar al artista al evento");
            }
        } catch(Exception ex){
            showAlert(AlertType.ERROR, "Error", "Error al asignar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void quitarArtistaDeEvento(Artista a, Evento e) {
        try(Connection con = Conexion.conectarBD();
            PreparedStatement ps = con.prepareStatement("DELETE FROM participa WHERE id_persona = ? AND id_evento = ?")){
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
    
    private void cargarEventosDelArtista(int artistaId) {
        listaEventosArtista.clear();
        Artista.getEventosForArtista(artistaId, listaEventosArtista);
        boolean tieneEventos = !listaEventosArtista.isEmpty();
        if(hboxEventosDisponibles != null) {
            hboxEventosDisponibles.setVisible(!tieneEventos && tableView1.isVisible());
        }
        mostrandoEventosDisponibles = false;
        tableView1.refresh();
    }
    
    @FXML
    public void mostrarEventosDisponibles() {
        Artista artista = tableView.getSelectionModel().getSelectedItem();
        if(artista != null) {
            mostrandoEventosDisponibles = !mostrandoEventosDisponibles;
            if(mostrandoEventosDisponibles){
                if(btnMostrarEventosDisponibles != null)
                    btnMostrarEventosDisponibles.setText("Volver a eventos asignados");
                cargarEventosDisponibles(artista.getId());
                if(hboxEventosDisponibles != null)
                    hboxEventosDisponibles.setVisible(false);
            } else {
                if(btnMostrarEventosDisponibles != null)
                    btnMostrarEventosDisponibles.setText("Ver eventos disponibles");
                cargarEventosDelArtista(artista.getId());
                if(hboxEventosDisponibles != null && listaEventosArtista.isEmpty())
                    hboxEventosDisponibles.setVisible(true);
            }
        }
    }
    
    private void cargarEventosDisponibles(int artistaId) {
        listaEventosArtista.clear();
        try(Connection con = Conexion.conectarBD();
            PreparedStatement ps = con.prepareStatement(
                "SELECT e.* FROM evento e WHERE e.id NOT IN (SELECT id_evento FROM participa WHERE id_persona = ?)"
            )){
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
                evento.setFechaActuacion("");
                listaEventosArtista.add(evento);
            }
        } catch(Exception e){
            System.out.println("Error al cargar eventos disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        tableView1.refresh();
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
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                cargarEventosDelArtista(newSelection.getId());
            }
            tableView.refresh();
        });
        tableView.setEditable(true);
        tableView1.setEditable(true);
    }
    
    private void loadData() {
        listaArtistas.clear();
        Artista.getAll(listaArtistas);
    }
    
    @FXML
    public void addRow() throws IOException {
        Artista nuevo = new Artista(0, "", "", "", "", "");
        listaArtistas.add(nuevo);
        tableView.getSelectionModel().select(nuevo);
        tableView.edit(tableView.getSelectionModel().getSelectedIndex(), nombre);
    }
    
    public void saveRow(Artista a) {
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
    }
    
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
        }
    }
    
    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    private void maximizarVentana(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(stage.isMaximized()){
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }
    
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}