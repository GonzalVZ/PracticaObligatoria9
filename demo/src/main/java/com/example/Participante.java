package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Clase que representa un participante en el sistema.
 * Hereda de Persona (con id, nombre, apellido1 y apellido2) y añade la propiedad email
 * y datos adicionales obtenidos de la tabla participa y evento.
 */
public class Participante extends Persona {

    private StringProperty email;
    private StringProperty participaFecha;
    private StringProperty evNombre;
    private StringProperty evDescripcion;
    private StringProperty evLugar;
    private StringProperty evFechaInicio;
    private StringProperty evFechaFin;
    private IntegerProperty eventoId;

    /**
     * Constructor simple (solo para guardar desde formularios, sin los datos de join).
     * 
     * @param id ID del participante
     * @param nombre Nombre del participante
     * @param apellido1 Primer apellido del participante
     * @param apellido2 Segundo apellido del participante
     * @param email Email del participante
     */
    public Participante(int id, String nombre, String apellido1, String apellido2, String email) {
        super(id, nombre, apellido1, apellido2);
        this.email = new SimpleStringProperty(email);
        this.participaFecha = new SimpleStringProperty("");
        this.evNombre = new SimpleStringProperty("");
        this.evDescripcion = new SimpleStringProperty("");
        this.evLugar = new SimpleStringProperty("");
        this.evFechaInicio = new SimpleStringProperty("");
        this.evFechaFin = new SimpleStringProperty("");
        this.eventoId = new SimpleIntegerProperty(0);
    }

    /**
     * Constructor que incluye campos del join para la consulta.
     * 
     * @param id ID del participante
     * @param per_nombre Nombre del participante
     * @param apellido1 Primer apellido del participante
     * @param apellido2 Segundo apellido del participante
     * @param email Email del participante
     * @param participaFecha Fecha de participación
     */
    public Participante(int id, String per_nombre, String apellido1, String apellido2, String email,
                    String participaFecha) {
        super(id, per_nombre, apellido1, apellido2);
        this.email = new SimpleStringProperty(email);
        this.participaFecha = new SimpleStringProperty(participaFecha);
        this.evNombre = new SimpleStringProperty("");
        this.evDescripcion = new SimpleStringProperty("");
        this.evLugar = new SimpleStringProperty("");
        this.evFechaInicio = new SimpleStringProperty("");
        this.evFechaFin = new SimpleStringProperty("");
        this.eventoId = new SimpleIntegerProperty(0);
    }

    /**
     * Constructor completo con todos los campos incluidos los de evento.
     * 
     * @param id ID del participante
     * @param per_nombre Nombre del participante
     * @param apellido1 Primer apellido del participante
     * @param apellido2 Segundo apellido del participante
     * @param email Email del participante
     * @param participaFecha Fecha de participación
     * @param ev_nombre Nombre del evento
     * @param ev_descripcion Descripción del evento
     * @param ev_lugar Lugar del evento
     * @param ev_fecha_inicio Fecha de inicio del evento
     * @param ev_fecha_fin Fecha de fin del evento
     * @param eventoId ID del evento
     */
    public Participante(int id, String per_nombre, String apellido1, String apellido2, String email,
                    String participaFecha, String ev_nombre, String ev_descripcion, String ev_lugar,
                    String ev_fecha_inicio, String ev_fecha_fin, int eventoId) {
        super(id, per_nombre, apellido1, apellido2);
        this.email = new SimpleStringProperty(email);
        this.participaFecha = new SimpleStringProperty(participaFecha);
        this.evNombre = new SimpleStringProperty(ev_nombre);
        this.evDescripcion = new SimpleStringProperty(ev_descripcion);
        this.evLugar = new SimpleStringProperty(ev_lugar);
        this.evFechaInicio = new SimpleStringProperty(ev_fecha_inicio);
        this.evFechaFin = new SimpleStringProperty(ev_fecha_fin);
        this.eventoId = new SimpleIntegerProperty(eventoId);
    }

    // Propiedades para JavaFX
    /**
     * Obtiene la propiedad del ID del evento.
     * @return La propiedad ID del evento como IntegerProperty
     */
    public IntegerProperty eventoIdProperty() {
        return eventoId;
    }
    
    /**
     * Obtiene el ID del evento.
     * @return El ID del evento como entero
     */
    public int getEventoId() {
        return eventoId.get();
    }
    
    /**
     * Establece el ID del evento.
     * @param id El nuevo ID del evento
     */
    public void setEventoId(int id) {
        this.eventoId.set(id);
    }

    /**
     * Obtiene la propiedad del email.
     * @return La propiedad email como StringProperty
     */
    public StringProperty emailProperty() {
        return email;
    }
    
    /**
     * Obtiene el email del participante.
     * @return El email como String
     */
    public String getEmail() {
        return email.get();
    }
    
    /**
     * Establece el email del participante.
     * @param email El nuevo email
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Obtiene la propiedad de la fecha de participación.
     * @return La propiedad fecha de participación como StringProperty
     */
    public StringProperty participaFechaProperty() {
        return participaFecha;
    }
    
    /**
     * Obtiene la fecha de participación.
     * @return La fecha de participación como String
     */
    public String getParticipaFecha() {
        return participaFecha.get();
    }
    
    /**
     * Establece la fecha de participación.
     * @param participaFecha La nueva fecha de participación
     */
    public void setParticipaFecha(String participaFecha) {
        this.participaFecha.set(participaFecha);
    }

    /**
     * Obtiene la propiedad del nombre del evento.
     * @return La propiedad nombre del evento como StringProperty
     */
    public StringProperty evNombreProperty() {
        return evNombre;
    }
    
    /**
     * Obtiene el nombre del evento.
     * @return El nombre del evento como String
     */
    public String getEvNombre() {
        return evNombre.get();
    }
    
    /**
     * Establece el nombre del evento.
     * @param evNombre El nuevo nombre del evento
     */
    public void setEvNombre(String evNombre) {
        this.evNombre.set(evNombre);
    }

    /**
     * Obtiene la propiedad de la descripción del evento.
     * @return La propiedad descripción del evento como StringProperty
     */
    public StringProperty evDescripcionProperty() {
        return evDescripcion;
    }
    
    /**
     * Obtiene la descripción del evento.
     * @return La descripción del evento como String
     */
    public String getEvDescripcion() {
        return evDescripcion.get();
    }
    
    /**
     * Establece la descripción del evento.
     * @param evDescripcion La nueva descripción del evento
     */
    public void setEvDescripcion(String evDescripcion) {
        this.evDescripcion.set(evDescripcion);
    }

    /**
     * Obtiene la propiedad del lugar del evento.
     * @return La propiedad lugar del evento como StringProperty
     */
    public StringProperty evLugarProperty() {
        return evLugar;
    }
    
    /**
     * Obtiene el lugar del evento.
     * @return El lugar del evento como String
     */
    public String getEvLugar() {
        return evLugar.get();
    }
    
    /**
     * Establece el lugar del evento.
     * @param evLugar El nuevo lugar del evento
     */
    public void setEvLugar(String evLugar) {
        this.evLugar.set(evLugar);
    }

    /**
     * Obtiene la propiedad de la fecha de inicio del evento.
     * @return La propiedad fecha de inicio del evento como StringProperty
     */
    public StringProperty evFechaInicioProperty() {
        return evFechaInicio;
    }
    
    /**
     * Obtiene la fecha de inicio del evento.
     * @return La fecha de inicio del evento como String
     */
    public String getEvFechaInicio() {
        return evFechaInicio.get();
    }
    
    /**
     * Establece la fecha de inicio del evento.
     * @param evFechaInicio La nueva fecha de inicio del evento
     */
    public void setEvFechaInicio(String evFechaInicio) {
        this.evFechaInicio.set(evFechaInicio);
    }

    /**
     * Obtiene la propiedad de la fecha de fin del evento.
     * @return La propiedad fecha de fin del evento como StringProperty
     */
    public StringProperty evFechaFinProperty() {
        return evFechaFin;
    }
    
    /**
     * Obtiene la fecha de fin del evento.
     * @return La fecha de fin del evento como String
     */
    public String getEvFechaFin() {
        return evFechaFin.get();
    }
    
    /**
     * Establece la fecha de fin del evento.
     * @param evFechaFin La nueva fecha de fin del evento
     */
    public void setEvFechaFin(String evFechaFin) {
        this.evFechaFin.set(evFechaFin);
    }
    
    // IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS DE Persona
    
    /**
     * Guarda o actualiza el participante en la base de datos.
     * Si el participante es nuevo, lo inserta; si ya existe, lo actualiza.
     * 
     * @return 1 si la operación tuvo éxito, 0 si falló
     */
    @Override
    public int save() {
        int result = 0;
        Connection con = null;
        try {
            con = Conexion.conectarBD();
            con.setAutoCommit(false);
            
            if (getId() == 0) {
                // INSERCIÓN DE UN NUEVO PARTICIPANTE
                
                // 1. Insertar en la tabla "persona"
                String queryPersona = "INSERT INTO persona (nombre, apellido1, apellido2) VALUES (?, ?, ?)";
                int nuevoId;
                try (PreparedStatement psPersona = con.prepareStatement(queryPersona, Statement.RETURN_GENERATED_KEYS)) {
                    psPersona.setString(1, getNombre());
                    psPersona.setString(2, getApellido1());
                    psPersona.setString(3, getApellido2());
                    psPersona.executeUpdate();
                    
                    // Obtener el ID generado
                    ResultSet rs = psPersona.getGeneratedKeys();
                    if (rs.next()) {
                        nuevoId = rs.getInt(1);
                        setId(nuevoId); // Actualizar el ID del objeto
                    } else {
                        throw new Exception("No se pudo obtener el ID generado para la persona.");
                    }
                }
                
                // 2. Insertar en la tabla "participante"
                String queryParticipante = "INSERT INTO participante (id_persona, email) VALUES (?, ?)";
                try (PreparedStatement psParticipante = con.prepareStatement(queryParticipante)) {
                    psParticipante.setInt(1, getId());
                    psParticipante.setString(2, getEmail());
                    psParticipante.executeUpdate();
                }
                
                // 3. Insertar en la tabla "participa" solo si hay un evento válido
                if (getEventoId() > 0) {
                    String queryParticipa = "INSERT INTO participa (id_persona, fecha, id_evento) VALUES (?, ?, ?)";
                    try (PreparedStatement psParticipa = con.prepareStatement(queryParticipa)) {
                        psParticipa.setInt(1, getId());
                        psParticipa.setString(2, getParticipaFecha().isEmpty() ? 
                                             java.time.LocalDate.now().toString() : getParticipaFecha());
                        psParticipa.setInt(3, getEventoId());
                        psParticipa.executeUpdate();
                    }
                }
            } else {
                // ACTUALIZACIÓN DE UN PARTICIPANTE EXISTENTE
                
                // 1. Actualizar datos de la tabla "persona"
                String queryPersona = "UPDATE persona SET nombre = ?, apellido1 = ?, apellido2 = ? WHERE id = ?";
                try (PreparedStatement psPersona = con.prepareStatement(queryPersona)) {
                    psPersona.setString(1, getNombre());
                    psPersona.setString(2, getApellido1());
                    psPersona.setString(3, getApellido2());
                    psPersona.setInt(4, getId());
                    psPersona.executeUpdate();
                }
                
                // 2. Actualizar datos de la tabla "participante"
                String queryParticipante = "UPDATE participante SET email = ? WHERE id_persona = ?";
                try (PreparedStatement psParticipante = con.prepareStatement(queryParticipante)) {
                    psParticipante.setString(1, getEmail());
                    psParticipante.setInt(2, getId());
                    psParticipante.executeUpdate();
                }
                
                // 3. Actualizar (o insertar) datos en la tabla "participa" SOLO si hay un evento válido
                if (getEventoId() > 0) {
                    String querySelectParticipa = "SELECT * FROM participa WHERE id_persona = ? AND id_evento = ?";
                    try (PreparedStatement psSelectP = con.prepareStatement(querySelectParticipa)) {
                        psSelectP.setInt(1, getId());
                        psSelectP.setInt(2, getEventoId());
                        ResultSet rs = psSelectP.executeQuery();
                        if (rs.next()) {
                            // Si el registro existe, se actualiza:
                            String queryUpdateParticipa = "UPDATE participa SET fecha = ? WHERE id_persona = ? AND id_evento = ?";
                            try (PreparedStatement psUpdateP = con.prepareStatement(queryUpdateParticipa)) {
                                psUpdateP.setString(1, getParticipaFecha().isEmpty() ? 
                                                  java.time.LocalDate.now().toString() : getParticipaFecha());
                                psUpdateP.setInt(2, getId());
                                psUpdateP.setInt(3, getEventoId());
                                psUpdateP.executeUpdate();
                            }
                        } else {
                            // Si no existe, se inserta:
                            String queryInsertParticipa = "INSERT INTO participa (id_persona, fecha, id_evento) VALUES (?, ?, ?)";
                            try (PreparedStatement psInsertP = con.prepareStatement(queryInsertParticipa)) {
                                psInsertP.setInt(1, getId());
                                psInsertP.setString(2, getParticipaFecha().isEmpty() ? 
                                                  java.time.LocalDate.now().toString() : getParticipaFecha());
                                psInsertP.setInt(3, getEventoId());
                                psInsertP.executeUpdate();
                            }
                        }
                    }
                }
            }
            
            con.commit();
            result = 1;
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error en Participante.save(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Elimina un participante de la base de datos.
     * También elimina sus relaciones en las tablas participa y participante.
     * 
     * @param id ID del participante a eliminar
     * @return 1 si la operación tuvo éxito, 0 si falló
     */
    @Override
    public int delete(int id) {
        Connection con = null;
        try {
            con = Conexion.conectarBD();
            con.setAutoCommit(false);
            
            // Primero eliminar de participa
            String queryParticipa = "DELETE FROM participa WHERE id_persona = ?";
            try (PreparedStatement psParticipa = con.prepareStatement(queryParticipa)) {
                psParticipa.setInt(1, id);
                psParticipa.executeUpdate();
            }
            
            // Luego eliminar de participante
            String queryParticipante = "DELETE FROM participante WHERE id_persona = ?";
            try (PreparedStatement psParticipante = con.prepareStatement(queryParticipante)) {
                psParticipante.setInt(1, id);
                psParticipante.executeUpdate();
            }
            
            // Finalmente eliminar de persona
            String queryPersona = "DELETE FROM persona WHERE id = ?";
            try (PreparedStatement psPersona = con.prepareStatement(queryPersona)) {
                psPersona.setInt(1, id);
                psPersona.executeUpdate();
            }
            
            con.commit();
            return 1;
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error en Participante.delete(): " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Obtiene el último ID de participante (realmente de persona) en la base de datos.
     * 
     * @return El último ID como entero
     */
    @Override
    public int getLastId() {
        String query = "SELECT MAX(id) FROM persona";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
             
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error en Participante.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Busca participantes por texto en el nombre, apellidos o email.
     * 
     * @param txt Texto a buscar
     * @return Lista observable con los participantes encontrados
     */
    @Override
    public ObservableList<Persona> get(String txt) {
        ObservableList<Persona> lista = FXCollections.observableArrayList();
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, part.email " +
                      "FROM persona p " +
                      "INNER JOIN participante part ON p.id = part.id_persona " +
                      "WHERE p.nombre LIKE ? OR p.apellido1 LIKE ? OR p.apellido2 LIKE ? OR part.email LIKE ?";
                      
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            String param = "%" + txt + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ps.setString(4, param);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String email = rs.getString("email");
                
                Participante participante = new Participante(id, nombre, apellido1, apellido2, email);
                lista.add(participante);
            }
        } catch (Exception e) {
            System.out.println("Error en Participante.get(String): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtiene un participante por su ID.
     * 
     * @param id ID del participante a buscar
     * @return El participante encontrado, o null si no existe
     */
    @Override
    public Participante get(int id) {
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, part.email " +
                      "FROM persona p " +
                      "INNER JOIN participante part ON p.id = part.id_persona " +
                      "WHERE p.id = ?";
                      
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String email = rs.getString("email");
                
                return new Participante(id, nombre, apellido1, apellido2, email);
            }
        } catch (Exception e) {
            System.out.println("Error en Participante.get(int): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los participantes de la base de datos.
     * 
     * @param lista Lista observable donde se cargarán los participantes
     * @return La lista actualizada con los participantes
     */
    public static ObservableList<Participante> getAll(ObservableList<Participante> lista) {
        String query = "SELECT p.id, p.nombre as per_nombre, p.apellido1, p.apellido2, " +
                       "part.email, MAX(pa.fecha) as participa_fecha " +
                       "FROM persona p " +
                       "INNER JOIN participante part ON p.id = part.id_persona " +
                       "LEFT JOIN participa pa ON p.id = pa.id_persona " +
                       "GROUP BY p.id, p.nombre, p.apellido1, p.apellido2, part.email";
        
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            lista.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String perNombre = rs.getString("per_nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String email = rs.getString("email");
                String participaFecha = rs.getString("participa_fecha");
                
                // Usar el constructor adecuado con la fecha
                Participante participante = new Participante(id, perNombre, apellido1, apellido2, email, 
                                                          participaFecha != null ? participaFecha : "");
                lista.add(participante);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Participante.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * Obtiene todos los eventos en los que participa un participante específico.
     * 
     * @param participanteId ID del participante
     * @param listaEventos Lista observable donde se cargarán los eventos
     * @return La lista actualizada con los eventos del participante
     */
    public static ObservableList<Evento> getEventosForParticipante(int participanteId, ObservableList<Evento> listaEventos) {
        String query = "SELECT e.*, p.fecha as fecha_participacion FROM evento e " +
                       "INNER JOIN participa p ON e.id = p.id_evento " +
                       "WHERE p.id_persona = ?";
        
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, participanteId);
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
                String fechaParticipacion = rs.getString("fecha_participacion");

                Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
                evento.setFechaParticipacion(fechaParticipacion);
                listaEventos.add(evento);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Participante.getEventosForParticipante(): " + e.getMessage());
            e.printStackTrace();
        }
        return listaEventos;
    }
}