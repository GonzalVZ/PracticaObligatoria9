package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Clase que representa a un artista en el sistema.
 * Hereda de Persona e incluye los datos específicos de artista.
 */
public class Artista extends Persona {
    
    private StringProperty fotografia;
    private StringProperty obraDestacada;
    private StringProperty actuaFecha;

    // Constructor completo
    public Artista(int id, String nombre, String apellido1, String apellido2, 
                   String fotografia, String obraDestacada, String actuaFecha) {
        super(id, nombre, apellido1, apellido2);
        this.fotografia = new SimpleStringProperty(fotografia);
        this.obraDestacada = new SimpleStringProperty(obraDestacada);
        this.actuaFecha = new SimpleStringProperty(actuaFecha);
    }

    // Constructor sin fecha de actuación
    public Artista(int id, String nombre, String apellido1, String apellido2, 
                   String fotografia, String obraDestacada) {
        this(id, nombre, apellido1, apellido2, fotografia, obraDestacada, "");
    }

    // Constructor vacío
    public Artista() {
        super(0, "", "", "");
        this.fotografia = new SimpleStringProperty("");
        this.obraDestacada = new SimpleStringProperty("");
        this.actuaFecha = new SimpleStringProperty("");
    }
    
    // Getters y setters
    public String getFotografia() {
        return fotografia.get();
    }
    
    public void setFotografia(String fotografia) {
        this.fotografia.set(fotografia);
    }
    
    public StringProperty fotografiaProperty() {
        return fotografia;
    }
    
    public String getObraDestacada() {
        return obraDestacada.get();
    }
    
    public void setObraDestacada(String obraDestacada) {
        this.obraDestacada.set(obraDestacada);
    }
    
    public StringProperty obraDestacadaProperty() {
        return obraDestacada;
    }
    
    public String getActuaFecha() {
        return actuaFecha.get();
    }
    
    public void setActuaFecha(String actuaFecha) {
        this.actuaFecha.set(actuaFecha);
    }
    
    public StringProperty actuaFechaProperty() {
        return actuaFecha;
    }
    
    // Método para guardar o actualizar
    @Override
    public int save() {
        int result = 0;
        Connection con = null;
        try {
            con = Conexion.conectarBD();
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
                
                if (isNew && result > 0) {
                    try (ResultSet rs = psPersona.getGeneratedKeys()) {
                        if (rs.next()) {
                            setId(rs.getInt(1));
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
            }
            con.commit();
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            System.out.println("Error en Artista.save(): " + e.getMessage());
            e.printStackTrace();
            result = 0;
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (Exception e) { e.printStackTrace(); }
        }
        return result;
    }
    
    // Elimina el registro de artista, eliminando primero las relaciones en participa, luego en artista y persona.
    @Override
    public int delete(int id) {
        int result = 0;
        Connection con = null;
        try {
            con = Conexion.conectarBD();
            con.setAutoCommit(false);
            String queryParticipa = "DELETE FROM participa WHERE id_persona = ?";
            try (PreparedStatement ps = con.prepareStatement(queryParticipa)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            String queryArtista = "DELETE FROM artista WHERE id_persona = ?";
            try (PreparedStatement ps = con.prepareStatement(queryArtista)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            String queryPersona = "DELETE FROM persona WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(queryPersona)) {
                ps.setInt(1, id);
                result = ps.executeUpdate();
            }
            con.commit();
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            System.out.println("Error en Artista.delete(): " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            try { if(con != null){ con.setAutoCommit(true); con.close(); } } catch (Exception e) { e.printStackTrace(); }
        }
        return result;
    }
    
    // Consulta todos los artistas
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
        } catch(Exception e) {
            System.out.println("Error de SQL en Artista.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    // Obtiene un artista por su ID
    @Override
    public Artista get(int id) {
        String query = "SELECT p.id, p.nombre, p.apellido1, p.apellido2, " +
                       "a.fotografia, a.obra_destacada " +
                       "FROM persona p " +
                       "INNER JOIN artista a ON p.id = a.id_persona " +
                       "WHERE p.id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                String fotografia = rs.getString("fotografia");
                String obraDestacada = rs.getString("obra_destacada");
                return new Artista(id, nombre, apellido1, apellido2, fotografia, obraDestacada);
            }
        } catch(Exception e){
            System.out.println("Error en Artista.get(int): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * Obtiene los eventos en los que participa un artista.
     * Se usa la tabla participa para relacionar persona y evento.
     */
    public static ObservableList<Evento> getEventosForArtista(int artistaId, ObservableList<Evento> listaEventos) {
        String query = "SELECT e.*, p.fecha as fecha_actuacion FROM evento e " +
                       "INNER JOIN participa p ON e.id = p.id_evento " +
                       "WHERE p.id_persona = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
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
        } catch(Exception e) {
            System.out.println("Error de SQL en Artista.getEventosForArtista(): " + e.getMessage());
            e.printStackTrace();
        }
        return listaEventos;
    }
    
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
            System.out.println("Error en Artista.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}