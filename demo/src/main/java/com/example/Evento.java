package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Clase que representa un evento en el sistema de gestión.
 * Almacena y gestiona información detallada sobre los eventos programados,
 * incluyendo fechas, ubicaciones, descripciones y relaciones con categorías.
 * 
 * <p>Esta clase proporciona métodos para realizar operaciones CRUD (Crear, Leer,
 * Actualizar y Eliminar) sobre eventos en la base de datos, así como para
 * gestionar la asignación de eventos a participantes y artistas.</p>
 * 
 * @author Jesús
 * @version 1.0
 * @see Categoria
 * @see Participante
 * @see Artista
 */
public class Evento {

    //--------------------------------------------------
    // PROPIEDADES
    //--------------------------------------------------
    /**
     * Identificador único del evento en la base de datos.
     */
    private IntegerProperty id;
    
    /**
     * Nombre descriptivo del evento.
     */
    private StringProperty nombre;
    
    /**
     * Descripción detallada del evento.
     */
    private StringProperty descripcion;
    
    /**
     * Lugar donde se realizará el evento.
     */
    private StringProperty lugar;
    
    /**
     * Fecha de inicio del evento (formato de cadena).
     */
    private StringProperty fecha_inicio;
    
    /**
     * Fecha de finalización del evento (formato de cadena).
     */
    private StringProperty fecha_fin;
    
    /**
     * Identificador de la categoría a la que pertenece el evento.
     */
    private IntegerProperty id_categoria;
    
    /**
     * Fecha en que un participante se inscribió en este evento.
     * Esta propiedad solo se utiliza en el contexto de relaciones con participantes.
     */
    private StringProperty fechaParticipacion;
    
    /**
     * Fecha en que un artista actúa en este evento.
     * Esta propiedad solo se utiliza en el contexto de relaciones con artistas.
     */
    private StringProperty fechaActuacion;

    //--------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------
    /**
     * Constructor de la clase Evento.
     * Inicializa un nuevo evento con todos sus atributos.
     * 
     * @param id Identificador único del evento
     * @param nombre Nombre del evento
     * @param descripcion Descripción detallada del evento
     * @param lugar Ubicación donde se realizará el evento
     * @param fecha_inicio Fecha de inicio del evento
     * @param fecha_fin Fecha de finalización del evento
     * @param id_categoria Identificador de la categoría asociada al evento
     */
    public Evento(int id, String nombre, String descripcion, String lugar, String fecha_inicio, String fecha_fin, int id_categoria) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.lugar = new SimpleStringProperty(lugar);
        this.fecha_inicio = new SimpleStringProperty(fecha_inicio);
        this.fecha_fin = new SimpleStringProperty(fecha_fin);
        this.id_categoria = new SimpleIntegerProperty(id_categoria);
        this.fechaParticipacion = new SimpleStringProperty("");
        this.fechaActuacion = new SimpleStringProperty("");
    }

    //--------------------------------------------------
    // PROPIEDADES JAVAFX
    //--------------------------------------------------
    /**
     * Obtiene la propiedad del ID para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad ID como IntegerProperty
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Obtiene la propiedad del nombre para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad nombre como StringProperty
     */
    public StringProperty nombreProperty() {
        return nombre;
    }

    /**
     * Obtiene la propiedad de la descripción para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad descripción como StringProperty
     */
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    /**
     * Obtiene la propiedad del lugar para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad lugar como StringProperty
     */
    public StringProperty lugarProperty() {
        return lugar;
    }

    /**
     * Obtiene la propiedad de la fecha de inicio para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad fecha_inicio como StringProperty
     */
    public StringProperty fecha_inicioProperty() {
        return fecha_inicio;
    }

    /**
     * Obtiene la propiedad de la fecha de fin para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad fecha_fin como StringProperty
     */
    public StringProperty fecha_finProperty() {
        return fecha_fin;
    }

    /**
     * Obtiene la propiedad del ID de la categoría para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad id_categoria como IntegerProperty
     */
    public IntegerProperty id_categoriaProperty() {
        return id_categoria;
    }
    
    /**
     * Obtiene la propiedad de la fecha de participación para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad fechaParticipacion como StringProperty
     */
    public StringProperty fechaParticipacionProperty() {
        return fechaParticipacion;
    }
    
    /**
     * Obtiene la propiedad de la fecha de actuación para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad fechaActuacion como StringProperty
     */
    public StringProperty fechaActuacionProperty() {
        return fechaActuacion;
    }

    //--------------------------------------------------
    // GETTERS Y SETTERS
    //--------------------------------------------------
    /**
     * Obtiene el ID del evento.
     * 
     * @return El ID como entero
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el ID del evento.
     * 
     * @param id El nuevo ID a asignar
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre del evento.
     * 
     * @return El nombre como String
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre del evento.
     * 
     * @param nombre El nuevo nombre a asignar
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene la descripción del evento.
     * 
     * @return La descripción como String
     */
    public String getDescripcion() {
        return descripcion.get();
    }

    /**
     * Establece la descripción del evento.
     * 
     * @param descripcion La nueva descripción a asignar
     */
    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    /**
     * Obtiene el lugar del evento.
     * 
     * @return El lugar como String
     */
    public String getLugar() {
        return lugar.get();
    }

    /**
     * Establece el lugar del evento.
     * 
     * @param lugar El nuevo lugar a asignar
     */
    public void setLugar(String lugar) {
        this.lugar.set(lugar);
    }

    /**
     * Obtiene la fecha de inicio del evento.
     * 
     * @return La fecha de inicio como String
     */
    public String getFecha_inicio() {
        return fecha_inicio.get();
    }

    /**
     * Establece la fecha de inicio del evento.
     * 
     * @param fecha_inicio La nueva fecha de inicio a asignar
     */
    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio.set(fecha_inicio);
    }

    /**
     * Obtiene la fecha de fin del evento.
     * 
     * @return La fecha de fin como String
     */
    public String getFecha_fin() {
        return fecha_fin.get();
    }

    /**
     * Establece la fecha de fin del evento.
     * 
     * @param fecha_fin La nueva fecha de fin a asignar
     */
    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin.set(fecha_fin);
    }

    /**
     * Obtiene el ID de la categoría del evento.
     * 
     * @return El ID de la categoría como entero
     */
    public int getId_categoria() {
        return id_categoria.get();
    }

    /**
     * Establece el ID de la categoría del evento.
     * 
     * @param id_categoria El nuevo ID de la categoría a asignar
     */
    public void setId_categoria(int id_categoria) {
        this.id_categoria.set(id_categoria);
    }
    
    /**
     * Obtiene la fecha en que un participante se inscribió en este evento.
     * 
     * @return La fecha de participación como String
     */
    public String getFechaParticipacion() {
        return fechaParticipacion.get();
    }
    
    /**
     * Establece la fecha en que un participante se inscribió en este evento.
     * 
     * @param fechaParticipacion La nueva fecha de participación a asignar
     */
    public void setFechaParticipacion(String fechaParticipacion) {
        this.fechaParticipacion.set(fechaParticipacion);
    }
    
    /**
     * Obtiene la fecha en que un artista actúa en este evento.
     * 
     * @return La fecha de actuación como String
     */
    public String getFechaActuacion() {
        return fechaActuacion.get();
    }
    
    /**
     * Establece la fecha en que un artista actúa en este evento.
     * 
     * @param fechaActuacion La nueva fecha de actuación a asignar
     */
    public void setFechaActuacion(String fechaActuacion) {
        this.fechaActuacion.set(fechaActuacion);
    }

    //--------------------------------------------------
    // MÉTODOS DE ACCESO A DATOS (ESTÁTICOS)
    //--------------------------------------------------
    /**
     * Obtiene todos los eventos desde la base de datos.
     * Recupera los registros de la tabla evento y los carga en la lista proporcionada.
     * 
     * @param listaEventos Lista observable donde se cargarán los eventos
     * @return La lista actualizada con los eventos, o null si no hay eventos
     */
    public static ObservableList<Evento> getAll(ObservableList<Evento> listaEventos) {
        String query = "SELECT * FROM evento";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para obtener los eventos");
                return listaEventos;
            }

            listaEventos.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String lugar = rs.getString("lugar");
                String fecha_inicio = rs.getString("fecha_inicio");
                String fecha_fin = rs.getString("fecha_fin");
                int id_categoria = rs.getInt("id_categoria");

                Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                listaEventos.add(evento);
            }
        } catch (SQLException e) {
            String errorMsg = "Error al recuperar eventos: ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "La tabla 'evento' no existe en la base de datos.";
            } else if (e.getMessage().contains("Unknown column")) {
                errorMsg += "Una de las columnas no existe: " + e.getMessage();
            } else {
                errorMsg += e.getMessage();
            }
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.err.println("Error de SQL en Evento.getAll(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al recuperar los eventos: " + e.getMessage());
            System.err.println("Error inesperado en Evento.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        
        return listaEventos;
    }

    /**
     * Busca un evento por su ID en la base de datos.
     * 
     * @param id1 ID del evento a buscar
     * @param listaEventos Lista observable donde se cargará el evento encontrado
     * @return La lista con el evento encontrado, o null si no se encuentra
     */
    public static ObservableList<Evento> getId(int id1, ObservableList<Evento> listaEventos) {
        String query = "SELECT * FROM evento WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para buscar el evento");
                return listaEventos;
            }
            
            ps.setInt(1, id1);
            ResultSet rs = ps.executeQuery();
            
            listaEventos.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String lugar = rs.getString("lugar");
                String fecha_inicio = rs.getString("fecha_inicio");
                String fecha_fin = rs.getString("fecha_fin");
                int id_categoria = rs.getInt("id_categoria");

                Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                listaEventos.add(evento);
            }
        } catch (SQLException e) {
            String errorMsg = "Error al buscar el evento con ID " + id1 + ": ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "La tabla 'evento' no existe en la base de datos.";
            } else {
                errorMsg += e.getMessage();
            }
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.err.println("Error de SQL en Evento.getId(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al buscar el evento: " + e.getMessage());
            System.err.println("Error inesperado en Evento.getId(): " + e.getMessage());
            e.printStackTrace();
        } 

        return listaEventos;
    }

    /**
     * Busca eventos por texto en el nombre o descripción.
     * Utiliza la cláusula LIKE para encontrar coincidencias parciales.
     * 
     * @param txt Texto a buscar
     * @param listaEventos Lista observable donde se cargarán los eventos encontrados
     * @return La lista con los eventos encontrados, o null si no se encuentra ninguno
     */
    public static ObservableList<Evento> getTxt(String txt, ObservableList<Evento> listaEventos) {
        String query = "SELECT * FROM evento WHERE nombre LIKE ? OR descripcion LIKE ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para buscar eventos");
                return listaEventos;
            }
            
            ps.setString(1, "%" + txt + "%");
            ps.setString(2, "%" + txt + "%");
            ResultSet rs = ps.executeQuery();
            
            listaEventos.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String lugar = rs.getString("lugar");
                String fecha_inicio = rs.getString("fecha_inicio");
                String fecha_fin = rs.getString("fecha_fin");
                int id_categoria = rs.getInt("id_categoria");

                Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                listaEventos.add(evento);
            }
        } catch (SQLException e) {
            String errorMsg = "Error al buscar eventos con el texto '" + txt + "': ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "La tabla 'evento' no existe en la base de datos.";
            } else {
                errorMsg += e.getMessage();
            }
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.err.println("Error de SQL en Evento.getTxt(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al buscar eventos: " + e.getMessage());
            System.err.println("Error inesperado en Evento.getTxt(): " + e.getMessage());
            e.printStackTrace();
        } 

        return listaEventos;
    }

    /**
     * Obtiene el último ID utilizado en la tabla evento.
     * Útil para asignar IDs secuenciales a nuevos eventos.
     * 
     * @return El último ID como entero, o 0 si no hay eventos o hubo un error
     */
    public static int getLastId() {
        String query = "SELECT MAX(id) FROM evento";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para obtener el último ID");
                return 0;
            }
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", 
                "Error al obtener el último ID de evento: " + e.getMessage());
            System.err.println("Error de SQL en Evento.getLastId(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al obtener el último ID: " + e.getMessage());
            System.err.println("Error inesperado en Evento.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    //--------------------------------------------------
    // MÉTODOS DE MANIPULACIÓN DE DATOS (INSTANCIA)
    //--------------------------------------------------
    /**
     * Guarda o actualiza el evento actual en la base de datos.
     * Si el evento ya existe (basado en su ID), se actualiza; si no, se inserta como nuevo.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int save() {
        // Validación básica de datos
        if (getNombre() == null || getNombre().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Datos incompletos", 
                "El nombre del evento no puede estar vacío.");
            return 0;
        }
        
        String querySelect = "SELECT * FROM evento WHERE id = ?";
        String queryUpdate = "UPDATE evento SET nombre = ?, descripcion = ?, lugar = ?, fecha_inicio = ?, fecha_fin = ?, id_categoria = ? WHERE id = ?";
        String queryInsert = "INSERT INTO evento VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.conectarBD()) {
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para guardar el evento");
                return 0;
            }
            
            // Primero verificamos si el evento ya existe
            try (PreparedStatement psSelect = con.prepareStatement(querySelect)) {
                psSelect.setInt(1, this.getId());
                ResultSet rs = psSelect.executeQuery();
                
                if (rs.next()) {
                    // Evento existente: actualizar
                    try (PreparedStatement psUpdate = con.prepareStatement(queryUpdate)) {
                        psUpdate.setString(1, this.getNombre());
                        psUpdate.setString(2, this.getDescripcion());
                        psUpdate.setString(3, this.getLugar());
                        psUpdate.setString(4, this.getFecha_inicio());
                        psUpdate.setString(5, this.getFecha_fin());
                        psUpdate.setInt(6, this.getId_categoria());
                        psUpdate.setInt(7, this.getId());
                        
                        int result = psUpdate.executeUpdate();
                        if (result > 0) {
                            mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", 
                                "Evento actualizado correctamente.");
                        }
                        return result;
                    }
                } else {
                    // Nuevo evento: insertar
                    try (PreparedStatement psInsert = con.prepareStatement(queryInsert)) {
                        // Si el ID es 0, usar el siguiente disponible
                        if (this.getId() == 0) {
                            this.setId(getLastId() + 1);
                        }
                        
                        psInsert.setInt(1, this.getId());
                        psInsert.setString(2, this.getNombre());
                        psInsert.setString(3, this.getDescripcion());
                        psInsert.setString(4, this.getLugar());
                        psInsert.setString(5, this.getFecha_inicio());
                        psInsert.setString(6, this.getFecha_fin());
                        psInsert.setInt(7, this.getId_categoria());
                        
                        int result = psInsert.executeUpdate();
                        if (result > 0) {
                            mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", 
                                "Evento creado correctamente.");
                        }
                        return result;
                    }
                }
            }
        } catch (SQLException e) {
            String errorMsg = "Error al guardar el evento: ";
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                errorMsg += "La categoría seleccionada no existe. Seleccione una categoría válida.";
            } else if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                errorMsg += "Ya existe un evento con este identificador. Intente con otro ID.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.err.println("Error de SQL en Evento.save(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al guardar el evento: " + e.getMessage());
            System.err.println("Error inesperado en Evento.save(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Elimina el evento actual de la base de datos.
     * Este método eliminará también todas las relaciones en la tabla participa.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int delete() {
        Connection con = null;
        try {
            con = Conexion.conectarBD();
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para eliminar el evento");
                return 0;
            }
            
            con.setAutoCommit(false); // Iniciar transacción
            
            // Primero eliminamos las participaciones relacionadas (no es necesario explícitamente por ON DELETE CASCADE)
            String queryParticipa = "DELETE FROM participa WHERE id_evento = ?";
            try (PreparedStatement ps = con.prepareStatement(queryParticipa)) {
                ps.setInt(1, this.getId());
                ps.executeUpdate();
                // No verificamos el resultado porque podría no tener participaciones
            }
            
            // Luego eliminamos el evento
            String queryEvento = "DELETE FROM evento WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(queryEvento)) {
                ps.setInt(1, this.getId());
                int result = ps.executeUpdate();
                
                con.commit();
                
                if (result > 0) {
                    mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", 
                        "El evento ha sido eliminado correctamente.");
                }
                
                return result;
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Revertir la transacción en caso de error
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            
            String errorMsg = "Error al eliminar el evento: ";
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                errorMsg += "Este evento está siendo referenciado por otros registros que no pueden ser eliminados automáticamente.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.err.println("Error de SQL en Evento.delete(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al eliminar el evento: " + e.getMessage());
            System.err.println("Error inesperado en Evento.delete(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restaurar autocommit
                    con.close(); // Cerrar la conexión
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
        return 0;
    }

    /**
     * Obtiene el ID de la categoría asociada al evento actual desde la base de datos.
     * Este método es útil para verificar la categoría asociada al evento.
     * 
     * @return El ID de la categoría como entero, o 0 si no se encuentra o hubo un error
     */
    public int getCategoria() {
        String query = "SELECT id_categoria FROM evento WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", 
                    "No se pudo conectar a la base de datos para obtener la categoría");
                return 0;
            }
            
            ps.setInt(1, this.getId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", 
                "Error al obtener la categoría del evento: " + e.getMessage());
            System.err.println("Error de SQL en Evento.getCategoria(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", 
                "Se produjo un error inesperado al obtener la categoría: " + e.getMessage());
            System.err.println("Error inesperado en Evento.getCategoria(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    //--------------------------------------------------
    // MÉTODOS AUXILIARES
    //--------------------------------------------------
    /**
     * Método auxiliar para mostrar alertas al usuario.
     * Centraliza la creación y visualización de alertas en la interfaz.
     * 
     * @param tipo Tipo de alerta (ERROR, INFORMATION, WARNING, etc.)
     * @param titulo Título de la alerta
     * @param mensaje Mensaje detallado a mostrar
     */
    private static void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        try {
            Platform.runLater(() -> {
                Alert alert = new Alert(tipo);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            });
        } catch (Exception e) {
            // Si no se puede mostrar la alerta (por ejemplo, si no estamos en un entorno JavaFX)
            System.err.println("[ALERTA " + tipo + "] " + titulo + ": " + mensaje);
        }
    }
    
    /**
     * Devuelve una representación en texto del evento, útil para depuración.
     * 
     * @return Una cadena con los datos principales del evento
     */
    @Override
    public String toString() {
        return "Evento [id=" + getId() + ", nombre=" + getNombre() + ", lugar=" + getLugar() + 
               ", fechas=" + getFecha_inicio() + " a " + getFecha_fin() + "]";
    }
}