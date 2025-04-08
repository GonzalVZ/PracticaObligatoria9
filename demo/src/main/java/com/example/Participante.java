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
     */
    public Participante(int id, String per_nombre, String apellido1, String apellido2, String email,
                    String participaFecha) {
        super(id, per_nombre, apellido1, apellido2);
        this.email = new SimpleStringProperty(email);
        this.participaFecha = new SimpleStringProperty(participaFecha);
        
    }

    // Propiedades para JavaFX
    public IntegerProperty eventoIdProperty() {
        return eventoId;
    }
    public int getEventoId() {
        return eventoId.get();
    }
    public void setEventoId(int id) {
        this.eventoId.set(id);
    }

    public StringProperty emailProperty() {
        return email;
    }
    public String getEmail() {
        return email.get();
    }
    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty participaFechaProperty() {
        return participaFecha;
    }
    public String getParticipaFecha() {
        return participaFecha.get();
    }
    public void setParticipaFecha(String participaFecha) {
        this.participaFecha.set(participaFecha);
    }

    
    // IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS DE Persona
    
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
            
            // 3. Insertar en la tabla "participa"
            if (getEventoId() > 0) {
                String queryParticipa = "INSERT INTO participa (id_persona, fecha, id_evento) VALUES (?, ?, ?)";
                try (PreparedStatement psParticipa = con.prepareStatement(queryParticipa)) {
                    psParticipa.setInt(1, getId());
                    psParticipa.setString(2, getParticipaFecha());
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
            
            // 3. Actualizar (o insertar) datos en la tabla "participa"
            String querySelectParticipa = "SELECT * FROM participa WHERE id_persona = ?";
            try (PreparedStatement psSelectP = con.prepareStatement(querySelectParticipa)) {
                psSelectP.setInt(1, getId());
                ResultSet rs = psSelectP.executeQuery();
                if (rs.next()) {
                    // Si el registro existe, se actualiza:
                    String queryUpdateParticipa = "UPDATE participa SET fecha = ?, id_evento = ? WHERE id_persona = ?";
                    try (PreparedStatement psUpdateP = con.prepareStatement(queryUpdateParticipa)) {
                        psUpdateP.setString(1, getParticipaFecha());
                        psUpdateP.setInt(2, getEventoId());
                        psUpdateP.setInt(3, getId());
                        psUpdateP.executeUpdate();
                    }
                } else {
                    // Si no existe, se inserta:
                    String queryInsertParticipa = "INSERT INTO participa (id_persona, fecha, id_evento) VALUES (?, ?, ?)";
                    try (PreparedStatement psInsertP = con.prepareStatement(queryInsertParticipa)) {
                        psInsertP.setInt(1, getId());
                        psInsertP.setString(2, getParticipaFecha());
                        psInsertP.setInt(3, getEventoId());
                        psInsertP.executeUpdate();
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
            System.out.println("Error de SQL en Participante.delete(): " + e.getMessage());
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
     * Obtiene todos los participantes a partir de la consulta JOIN.
     *
     * @param lista Lista observable donde se cargarán los participantes
     * @return La lista actualizada con los participantes
     */
    public static ObservableList<Participante> getAll(ObservableList<Participante> lista) {
        String query = "SELECT persona.id, persona.nombre as per_nombre, persona.apellido1, persona.apellido2, " +
                       "participante.email, participa.fecha as participa_fecha, " +
                       "FROM participa " +
                       "INNER JOIN persona ON participa.id_persona = persona.id " +
                       "INNER JOIN participante ON participante.id_persona = persona.id";
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
               

                Participante participante = new Participante(id, perNombre, apellido1, apellido2, email,participaFecha);
                lista.add(participante);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Participante.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int getLastId() {
        String query = "SELECT MAX(id_persona) FROM participante";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next())
                return rs.getInt(1);
        } catch (Exception e) {
            System.out.println("Error de SQL en Participante.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public ObservableList<Persona> get(String txt) {
        ObservableList<Persona> lista = FXCollections.observableArrayList();
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, part.email " +
                       "FROM persona p INNER JOIN participante part ON p.id = part.id_persona " +
                       "WHERE p.nombre LIKE ? OR p.apellido1 LIKE ? OR p.apellido2 LIKE ? OR part.email LIKE ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, "%" + txt + "%");
            ps.setString(2, "%" + txt + "%");
            ps.setString(3, "%" + txt + "%");
            ps.setString(4, "%" + txt + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String perNombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String email = rs.getString("email");
                Participante participante = new Participante(id, perNombre, apellido1, apellido2, email);
                lista.add(participante);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Participante.get(String): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Persona get(int id) {
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, part.email " +
                       "FROM persona p INNER JOIN participante part ON p.id = part.id_persona " +
                       "WHERE p.id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String perNombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String email = rs.getString("email");
                return new Participante(id, perNombre, apellido1, apellido2, email);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Participante.get(int): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}