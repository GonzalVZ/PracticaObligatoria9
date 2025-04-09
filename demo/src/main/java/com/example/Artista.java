package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Clase que representa a un artista en el sistema de gestión de eventos.
 * Hereda de {@link Persona} e incluye atributos específicos de un artista como
 * fotografía y obra destacada, así como métodos para gestionar su persistencia y
 * relación con eventos.
 * 
 * <p>Esta clase implementa operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para trabajar con datos de artistas en la base de datos.</p>
 * 
 * @author Jesús 
 * @version 1.0
 * @see Persona
 * @see Evento
 */
public class Artista extends Persona {
    
    /**
     * Propiedad que almacena la ruta o identificador de la fotografía del artista.
     */
    private StringProperty fotografia;
    
    /**
     * Propiedad que almacena una descripción de la obra destacada del artista.
     */
    private StringProperty obraDestacada;
    
    /**
     * Propiedad que almacena la fecha más reciente de actuación del artista.
     */
    private StringProperty actuaFecha;

    /**
     * Constructor que inicializa un artista con todos sus atributos, incluida la fecha de actuación.
     * 
     * @param id Identificador único del artista en la base de datos
     * @param nombre Nombre del artista 
     * @param apellido1 Primer apellido del artista
     * @param apellido2 Segundo apellido del artista
     * @param fotografia Ruta o identificador de la fotografía
     * @param obraDestacada Descripción de la obra destacada
     * @param actuaFecha Fecha de la actuación más reciente
     */
    public Artista(int id, String nombre, String apellido1, String apellido2, 
                   String fotografia, String obraDestacada, String actuaFecha) {
        super(id, nombre, apellido1, apellido2);
        this.fotografia = new SimpleStringProperty(fotografia);
        this.obraDestacada = new SimpleStringProperty(obraDestacada);
        this.actuaFecha = new SimpleStringProperty(actuaFecha);
    }

    /**
     * Constructor que inicializa un artista sin fecha de actuación.
     * Útil cuando se crea un nuevo artista que aún no tiene actuaciones programadas.
     * 
     * @param id Identificador único del artista en la base de datos
     * @param nombre Nombre del artista
     * @param apellido1 Primer apellido del artista
     * @param apellido2 Segundo apellido del artista
     * @param fotografia Ruta o identificador de la fotografía
     * @param obraDestacada Descripción de la obra destacada
     */
    public Artista(int id, String nombre, String apellido1, String apellido2, 
                   String fotografia, String obraDestacada) {
        this(id, nombre, apellido1, apellido2, fotografia, obraDestacada, "");
    }

    /**
     * Constructor vacío que inicializa un artista con valores predeterminados.
     * Útil para crear instancias temporales o para formularios de ingreso de datos.
     */
    public Artista() {
        super(0, "", "", "");
        this.fotografia = new SimpleStringProperty("");
        this.obraDestacada = new SimpleStringProperty("");
        this.actuaFecha = new SimpleStringProperty("");
    }
    
    /**
     * Obtiene la fotografía del artista.
     * 
     * @return La ruta o identificador de la fotografía como String
     */
    public String getFotografia() {
        return fotografia.get();
    }
    
    /**
     * Establece la fotografía del artista.
     * 
     * @param fotografia La nueva ruta o identificador de la fotografía
     */
    public void setFotografia(String fotografia) {
        this.fotografia.set(fotografia);
    }
    
    /**
     * Obtiene la propiedad de la fotografía para enlace bidireccional en JavaFX.
     * 
     * @return La propiedad StringProperty de la fotografía
     */
    public StringProperty fotografiaProperty() {
        return fotografia;
    }
    
    /**
     * Obtiene la obra destacada del artista.
     * 
     * @return La descripción de la obra destacada como String
     */
    public String getObraDestacada() {
        return obraDestacada.get();
    }
    
    /**
     * Establece la obra destacada del artista.
     * 
     * @param obraDestacada La nueva descripción de la obra destacada
     */
    public void setObraDestacada(String obraDestacada) {
        this.obraDestacada.set(obraDestacada);
    }
    
    /**
     * Obtiene la propiedad de la obra destacada para enlace bidireccional en JavaFX.
     * 
     * @return La propiedad StringProperty de la obra destacada
     */
    public StringProperty obraDestacadaProperty() {
        return obraDestacada;
    }
    
    /**
     * Obtiene la fecha de actuación más reciente del artista.
     * 
     * @return La fecha de actuación como String
     */
    public String getActuaFecha() {
        return actuaFecha.get();
    }
    
    /**
     * Establece la fecha de actuación del artista.
     * 
     * @param actuaFecha La nueva fecha de actuación
     */
    public void setActuaFecha(String actuaFecha) {
        this.actuaFecha.set(actuaFecha);
    }
    
    /**
     * Obtiene la propiedad de la fecha de actuación para enlace bidireccional en JavaFX.
     * 
     * @return La propiedad StringProperty de la fecha de actuación
     */
    public StringProperty actuaFechaProperty() {
        return actuaFecha;
    }
    
    /**
     * Método auxiliar para la clase Evento que establece la fecha de actuación.
     * Permite mantener la compatibilidad con la interfaz de Evento.
     * 
     * @param fechaActuacion La fecha de actuación a establecer
     */
    public void setFechaActuacion(String fechaActuacion) {
        setActuaFecha(fechaActuacion);
    }
    
    /**
     * Método auxiliar para la clase Evento que obtiene la fecha de actuación.
     * Permite mantener la compatibilidad con la interfaz de Evento.
     * 
     * @return La fecha de actuación como String
     */
    public String getFechaActuacion() {
        return getActuaFecha();
    }
    
    /**
     * Guarda o actualiza el artista en la base de datos, junto con sus
     * datos personales y específicos de artista.
     * 
     * <p>Si el ID es 0, crea un nuevo registro; de lo contrario, actualiza el existente.</p>
     * 
     * @return 1 si la operación fue exitosa, 0 si falló
     */
    @Override
    public int save() {
        int result = 0;
        Connection con = null;
        try {
            con = Conexion.conectarBD();
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos. Verifique su conexión y los datos de acceso.");
                return 0;
            }
            
            con.setAutoCommit(false);
            
            boolean isNew = (getId() == 0);
            String personaQuery;
            if (isNew) {
                personaQuery = "INSERT INTO persona (nombre, apellido1, apellido2) VALUES (?, ?, ?)";
            } else {
                personaQuery = "UPDATE persona SET nombre=?, apellido1=?, apellido2=? WHERE id=?";
            }
            
            // Guardamos en la tabla persona
            try (PreparedStatement psPersona = con.prepareStatement(personaQuery, Statement.RETURN_GENERATED_KEYS)) {
                psPersona.setString(1, getNombre());
                psPersona.setString(2, getApellido1());
                psPersona.setString(3, getApellido2());
                if (!isNew) psPersona.setInt(4, getId());
                
                result = psPersona.executeUpdate();
                
                if (result == 0) {
                    throw new SQLException("Error al guardar datos personales. Verifique que los campos no estén vacíos.");
                }
                
                if (isNew && result > 0) {
                    try (ResultSet rs = psPersona.getGeneratedKeys()) {
                        if (rs.next()) {
                            setId(rs.getInt(1));
                        } else {
                            throw new SQLException("No se pudo obtener el ID generado para el artista.");
                        }
                    }
                }
            }
            
            // Guardamos en la tabla artista
            String artistaQuery;
            if (isNew) {
                artistaQuery = "INSERT INTO artista (id_persona, fotografia, obra_destacada) VALUES (?, ?, ?)";
            } else {
                artistaQuery = "UPDATE artista SET fotografia=?, obra_destacada=? WHERE id_persona=?";
            }
            try (PreparedStatement psArtista = con.prepareStatement(artistaQuery)) {
                if (isNew) {
                    psArtista.setInt(1, getId());
                    psArtista.setString(2, getFotografia());
                    psArtista.setString(3, getObraDestacada());
                } else {
                    psArtista.setString(1, getFotografia());
                    psArtista.setString(2, getObraDestacada());
                    psArtista.setInt(3, getId());
                }
                
                result = psArtista.executeUpdate();
                if (result == 0) {
                    throw new SQLException("Error al guardar datos de artista. Verifique que los campos de fotografía y obra destacada sean válidos.");
                }
            }
            
            con.commit();
            mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", "El artista ha sido guardado correctamente en la base de datos.");
        } catch (SQLException e) {
            try { 
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                mostrarAlerta("Error en la transacción", "Error al revertir la transacción: " + ex.getMessage());
            }
            
            String errorMsg = "Error al guardar el artista: ";
            if (e.getMessage().contains("foreign key")) {
                errorMsg += "Existe una restricción de clave foránea. Asegúrese de que los datos relacionados existan.";
            } else if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                errorMsg += "Ya existe un artista con estos datos. No se permiten duplicados.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta("Error de base de datos", errorMsg);
            System.out.println("Error en Artista.save(): " + e.getMessage());
            e.printStackTrace();
            result = 0;
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Se produjo un error inesperado al guardar el artista: " + e.getMessage());
            System.out.println("Error inesperado en Artista.save(): " + e.getMessage());
            e.printStackTrace();
            result = 0;
        } finally {
            try { 
                if (con != null) { 
                    con.setAutoCommit(true); 
                    con.close(); 
                }
            } catch (SQLException e) {
                mostrarAlerta("Error al cerrar conexión", "No se pudo cerrar correctamente la conexión con la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * Elimina un artista de la base de datos por su ID.
     * Este método elimina todos los registros relacionados con el artista en las tablas:
     * participa, artista y persona (en ese orden para mantener integridad referencial).
     * 
     * @param id El ID del artista a eliminar
     * @return 1 si la eliminación fue exitosa, 0 si falló
     */
    @Override
    public int delete(int id) {
        int result = 0;
        Connection con = null;
        try {
            con = Conexion.conectarBD();
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos para eliminar el artista.");
                return 0;
            }
            
            con.setAutoCommit(false);
            
            // 1. Primero eliminamos las participaciones del artista
            String queryParticipa = "DELETE FROM participa WHERE id_persona = ?";
            try (PreparedStatement ps = con.prepareStatement(queryParticipa)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                // No verificamos el resultado porque podría no tener participaciones
            }
            
            // 2. Luego eliminamos el registro de la tabla artista
            String queryArtista = "DELETE FROM artista WHERE id_persona = ?";
            try (PreparedStatement ps = con.prepareStatement(queryArtista)) {
                ps.setInt(1, id);
                int artistaResult = ps.executeUpdate();
                if (artistaResult == 0) {
                    throw new SQLException("No se encontró el registro del artista con ID " + id);
                }
            }
            
            // 3. Finalmente eliminamos el registro de la tabla persona
            String queryPersona = "DELETE FROM persona WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(queryPersona)) {
                ps.setInt(1, id);
                result = ps.executeUpdate();
                if (result == 0) {
                    throw new SQLException("No se encontró la persona asociada con ID " + id);
                }
            }
            
            con.commit();
            mostrarAlerta(AlertType.INFORMATION, "Eliminación exitosa", "El artista ha sido eliminado correctamente.");
        } catch (SQLException e) {
            try { 
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                mostrarAlerta("Error en la transacción", "Error al revertir la transacción durante la eliminación: " + ex.getMessage());
            }
            
            String errorMsg = "Error al eliminar el artista: ";
            if (e.getMessage().contains("foreign key")) {
                errorMsg += "Este artista tiene relaciones que impiden su eliminación. Elimine primero esas referencias.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta("Error de base de datos", errorMsg);
            System.out.println("Error en Artista.delete(): " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            try { 
                if(con != null){ 
                    con.setAutoCommit(true); 
                    con.close(); 
                } 
            } catch (SQLException e) {
                mostrarAlerta("Error al cerrar conexión", "No se pudo cerrar correctamente la conexión con la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * Obtiene una lista de todos los artistas almacenados en la base de datos,
     * incluyendo sus datos personales, información específica de artista y
     * la fecha más reciente de actuación.
     * 
     * @param lista Lista observable donde se cargarán los artistas recuperados
     * @return La misma lista pasada como parámetro, pero poblada con los datos
     */
    public static ObservableList<Artista> getAll(ObservableList<Artista> lista) {
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, " +
                       "a.fotografia, a.obra_destacada, MAX(pa.fecha) as actua_fecha " +
                       "FROM persona p " +
                       "INNER JOIN artista a ON p.id = a.id_persona " +
                       "LEFT JOIN participa pa ON p.id = pa.id_persona " +
                       "GROUP BY p.id, p.nombre, p.apellido1, p.apellido2, a.fotografia, a.obra_destacada";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos para obtener los artistas.");
                return lista;
            }
             
            lista.clear();
            while(rs.next()){
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String fotografia = rs.getString("fotografia");
                String obraDestacada = rs.getString("obra_destacada");
                String actuaFecha = rs.getString("actua_fecha");
                Artista artista = new Artista(id, nombre, apellido1, apellido2, fotografia, obraDestacada,
                                              actuaFecha != null ? actuaFecha : "");
                lista.add(artista);
            }
        } catch(SQLException e) {
            String errorMsg = "Error al recuperar la lista de artistas: ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "Una de las tablas no existe en la base de datos. Verifique la estructura de la BD.";
            } else if (e.getMessage().contains("Unknown column")) {
                errorMsg += "Una columna no existe: " + e.getMessage() + ". Verifique la estructura de la BD.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta("Error de base de datos", errorMsg);
            System.out.println("Error de SQL en Artista.getAll(): " + e.getMessage());
            e.printStackTrace();
        } catch(Exception e) {
            mostrarAlerta("Error inesperado", "Se produjo un error inesperado al recuperar los artistas: " + e.getMessage());
            System.out.println("Error inesperado en Artista.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * Obtiene un artista específico por su ID, incluyendo datos personales
     * e información específica de artista.
     * 
     * @param id El ID del artista a buscar
     * @return El objeto Artista si se encuentra, o null si no existe
     */
    @Override
    public Artista get(int id) {
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, " +
                       "a.fotografia, a.obra_destacada " +
                       "FROM persona p " +
                       "INNER JOIN artista a ON p.id = a.id_persona " +
                       "WHERE p.id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos para buscar el artista.");
                return null;
            }
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String fotografia = rs.getString("fotografia");
                String obraDestacada = rs.getString("obra_destacada");
                return new Artista(id, nombre, apellido1, apellido2, fotografia, obraDestacada);
            } else {
                // No es un error que no exista, simplemente retornamos null
                return null;
            }
        } catch(SQLException e) {
            String errorMsg = "Error al buscar el artista con ID " + id + ": ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "Una tabla no existe. Verifique la estructura de la base de datos.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta("Error de base de datos", errorMsg);
            System.out.println("Error en Artista.get(int): " + e.getMessage());
            e.printStackTrace();
        } catch(Exception e) {
            mostrarAlerta("Error inesperado", "Se produjo un error inesperado al buscar el artista: " + e.getMessage());
            System.out.println("Error inesperado en Artista.get(int): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Busca artistas que coincidan con el texto de búsqueda en sus datos personales,
     * fotografía u obra destacada.
     * 
     * @param txt Texto a buscar en los campos del artista
     * @return Lista observable de personas (artistas) que coinciden con la búsqueda
     */
    @Override
    public ObservableList<Persona> get(String txt) {
        ObservableList<Persona> lista = FXCollections.observableArrayList();
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, a.fotografia, a.obra_destacada " +
                       "FROM persona p " +
                       "INNER JOIN artista a ON p.id = a.id_persona " +
                       "WHERE p.nombre LIKE ? OR p.apellido1 LIKE ? OR p.apellido2 LIKE ? " +
                       "OR a.fotografia LIKE ? OR a.obra_destacada LIKE ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos para buscar artistas.");
                return lista;
            }
            
            String param = "%" + txt + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ps.setString(4, param);
            ps.setString(5, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String fotografia = rs.getString("fotografia");
                String obraDestacada = rs.getString("obra_destacada");
                Artista artista = new Artista(id, nombre, apellido1, apellido2, fotografia, obraDestacada);
                lista.add(artista);
            }
        } catch (SQLException e) {
            String errorMsg = "Error al buscar artistas con el texto '" + txt + "': ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "Una tabla no existe. Verifique la estructura de la base de datos.";
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta("Error de base de datos", errorMsg);
            System.out.println("Error en Artista.get(String): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Se produjo un error inesperado al buscar artistas: " + e.getMessage());
            System.out.println("Error inesperado en Artista.get(String): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * Obtiene todos los eventos en los que participa un artista específico.
     * Consulta la tabla participa para encontrar las relaciones entre el artista y los eventos.
     * 
     * @param artistaId ID del artista cuyos eventos se desean recuperar
     * @param listaEventos Lista observable donde se cargarán los eventos recuperados
     * @return La misma lista pasada como parámetro, pero poblada con los datos
     */
    public static ObservableList<Evento> getEventosForArtista(int artistaId, ObservableList<Evento> listaEventos) {
        String query = "SELECT e.*, p.fecha as fecha_actuacion FROM evento e " +
                       "INNER JOIN participa p ON e.id = p.id_evento " +
                       "WHERE p.id_persona = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos para obtener los eventos del artista.");
                return listaEventos;
            }
            
            ps.setInt(1, artistaId);
            ResultSet rs = ps.executeQuery();
            listaEventos.clear();
            
            while(rs.next()){
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String lugar = rs.getString("lugar");
                String fecha_inicio = rs.getString("fecha_inicio");
                String fecha_fin = rs.getString("fecha_fin");
                int id_categoria = rs.getInt("id_categoria");
                String fechaActuacion = rs.getString("fecha_actuacion");
                
                Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                evento.setFechaActuacion(fechaActuacion);
                listaEventos.add(evento);
            }
        } catch(SQLException e) {
            String errorMsg = "Error al recuperar eventos del artista con ID " + artistaId + ": ";
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg += "La tabla participa no existe. Verifique la estructura de la base de datos.";
            } else if (e.getMessage().contains("Unknown column")) {
                errorMsg += "Una columna no existe: " + e.getMessage();
            } else {
                errorMsg += e.getMessage();
            }
            
            mostrarAlerta("Error de base de datos", errorMsg);
            System.out.println("Error de SQL en Artista.getEventosForArtista(): " + e.getMessage());
            e.printStackTrace();
        } catch(Exception e) {
            mostrarAlerta("Error inesperado", "Se produjo un error inesperado al recuperar los eventos del artista: " + e.getMessage());
            System.out.println("Error inesperado en Artista.getEventosForArtista(): " + e.getMessage());
            e.printStackTrace();
        }
        return listaEventos;
    }
    
    /**
     * Obtiene el último ID utilizado en la tabla persona.
     * Útil para asignar IDs a nuevos registros manualmente si es necesario.
     * 
     * @return El último ID como entero, o 0 si no hay registros o hubo un error
     */
    @Override
    public int getLastId() {
        String query = "SELECT MAX(id) FROM persona";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta("Error de conexión", "No se pudo conectar con la base de datos para obtener el último ID.");
                return 0;
            }
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", "Error al obtener el último ID de persona: " + e.getMessage());
            System.out.println("Error en Artista.getLastId(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Se produjo un error inesperado al obtener el último ID: " + e.getMessage());
            System.out.println("Error inesperado en Artista.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Método auxiliar para mostrar alertas informativas o de error.
     * Este método es privado y se utiliza para centralizar el código de mostrar alertas.
     * 
     * @param titulo Título de la alerta
     * @param mensaje Mensaje detallado a mostrar
     */
    private static void mostrarAlerta(String titulo, String mensaje) {
        mostrarAlerta(AlertType.ERROR, titulo, mensaje);
    }
    
    /**
     * Método auxiliar para mostrar alertas de cualquier tipo.
     * Este método es privado y se utiliza para centralizar el código de mostrar alertas.
     * 
     * @param tipo Tipo de alerta (ERROR, INFORMATION, WARNING, etc.)
     * @param titulo Título de la alerta
     * @param mensaje Mensaje detallado a mostrar
     */
    private static void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        // Ejecutar en el hilo de JavaFX
        try {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(tipo);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            });
        } catch (Exception e) {
            // Si no se puede mostrar la alerta (por ejemplo, si no estamos en un entorno JavaFX)
            System.out.println("[ALERTA " + tipo + "] " + titulo + ": " + mensaje);
            e.printStackTrace();
        }
    }
    
    /**
     * Devuelve una representación en texto del artista, útil para depuración.
     * 
     * @return Una cadena con los datos principales del artista
     */
    @Override
    public String toString() {
        return "Artista [id=" + getId() + ", nombre=" + getNombre() + " " + getApellido1() + " " + getApellido2() + 
               ", obra=" + getObraDestacada() + "]";
    }
}