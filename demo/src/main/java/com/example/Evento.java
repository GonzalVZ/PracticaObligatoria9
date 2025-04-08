package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * Clase que representa un evento en el sistema.
 * Incluye métodos para acceder y manipular los datos de los eventos.
 */
public class Evento {

    //--------------------------------------------------
    // PROPIEDADES
    //--------------------------------------------------
    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty descripcion;
    private StringProperty lugar;
    private StringProperty fecha_inicio;
    private StringProperty fecha_fin;
    private IntegerProperty id_categoria;
    private StringProperty fechaParticipacion; // Fecha en que un participante se inscribió en este evento
    private StringProperty fechaActuacion; // Fecha en que un artista actúa en este evento

    //--------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------
    /**
     * Constructor de la clase Evento.
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
     * @return La propiedad ID como IntegerProperty
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Obtiene la propiedad del nombre para uso en JavaFX.
     * @return La propiedad nombre como StringProperty
     */
    public StringProperty nombreProperty() {
        return nombre;
    }

    /**
     * Obtiene la propiedad de la descripción para uso en JavaFX.
     * @return La propiedad descripción como StringProperty
     */
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    /**
     * Obtiene la propiedad del lugar para uso en JavaFX.
     * @return La propiedad lugar como StringProperty
     */
    public StringProperty lugarProperty() {
        return lugar;
    }

    /**
     * Obtiene la propiedad de la fecha de inicio para uso en JavaFX.
     * @return La propiedad fecha_inicio como StringProperty
     */
    public StringProperty fecha_inicioProperty() {
        return fecha_inicio;
    }

    /**
     * Obtiene la propiedad de la fecha de fin para uso en JavaFX.
     * @return La propiedad fecha_fin como StringProperty
     */
    public StringProperty fecha_finProperty() {
        return fecha_fin;
    }

    /**
     * Obtiene la propiedad del ID de la categoría para uso en JavaFX.
     * @return La propiedad id_categoria como IntegerProperty
     */
    public IntegerProperty id_categoriaProperty() {
        return id_categoria;
    }
    
    /**
     * Obtiene la propiedad de la fecha de participación para uso en JavaFX.
     * @return La propiedad fechaParticipacion como StringProperty
     */
    public StringProperty fechaParticipacionProperty() {
        return fechaParticipacion;
    }
    
    /**
     * Obtiene la propiedad de la fecha de actuación para uso en JavaFX.
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
     * @return El ID como entero
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el ID del evento.
     * @param id El nuevo ID
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre del evento.
     * @return El nombre como String
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre del evento.
     * @param nombre El nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene la descripción del evento.
     * @return La descripción como String
     */
    public String getDescripcion() {
        return descripcion.get();
    }

    /**
     * Establece la descripción del evento.
     * @param descripcion La nueva descripción
     */
    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    /**
     * Obtiene el lugar del evento.
     * @return El lugar como String
     */
    public String getLugar() {
        return lugar.get();
    }

    /**
     * Establece el lugar del evento.
     * @param lugar El nuevo lugar
     */
    public void setLugar(String lugar) {
        this.lugar.set(lugar);
    }

    /**
     * Obtiene la fecha de inicio del evento.
     * @return La fecha de inicio como String
     */
    public String getFecha_inicio() {
        return fecha_inicio.get();
    }

    /**
     * Establece la fecha de inicio del evento.
     * @param fecha_inicio La nueva fecha de inicio
     */
    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio.set(fecha_inicio);
    }

    /**
     * Obtiene la fecha de fin del evento.
     * @return La fecha de fin como String
     */
    public String getFecha_fin() {
        return fecha_fin.get();
    }

    /**
     * Establece la fecha de fin del evento.
     * @param fecha_fin La nueva fecha de fin
     */
    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin.set(fecha_fin);
    }

    /**
     * Obtiene el ID de la categoría del evento.
     * @return El ID de la categoría como entero
     */
    public int getId_categoria() {
        return id_categoria.get();
    }

    /**
     * Establece el ID de la categoría del evento.
     * @param id_categoria El nuevo ID de la categoría
     */
    public void setId_categoria(int id_categoria) {
        this.id_categoria.set(id_categoria);
    }
    
    /**
     * Obtiene la fecha en que un participante se inscribió en este evento.
     * @return La fecha de participación como String
     */
    public String getFechaParticipacion() {
        return fechaParticipacion.get();
    }
    
    /**
     * Establece la fecha en que un participante se inscribió en este evento.
     * @param fechaParticipacion La nueva fecha de participación
     */
    public void setFechaParticipacion(String fechaParticipacion) {
        this.fechaParticipacion.set(fechaParticipacion);
    }
    
    /**
     * Obtiene la fecha en que un artista actúa en este evento.
     * @return La fecha de actuación como String
     */
    public String getFechaActuacion() {
        return fechaActuacion.get();
    }
    
    /**
     * Establece la fecha en que un artista actúa en este evento.
     * @param fechaActuacion La nueva fecha de actuación
     */
    public void setFechaActuacion(String fechaActuacion) {
        this.fechaActuacion.set(fechaActuacion);
    }

    //--------------------------------------------------
    // MÉTODOS DE ACCESO A DATOS (ESTÁTICOS)
    //--------------------------------------------------
    /**
     * Obtiene todos los eventos desde la base de datos.
     * 
     * @param listaEventos Lista observable donde se cargarán los eventos
     * @return La lista actualizada con los eventos, o null si no hay eventos
     */
    public static ObservableList<Evento> getAll(ObservableList<Evento> listaEventos) {
        String query = "SELECT * FROM evento";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

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
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        if (listaEventos.isEmpty()) {
            return null;
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
            ps.setInt(1, id1);
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
                listaEventos.add(evento);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.getId(): " + e.getMessage());
            e.printStackTrace();
        } 

        if (listaEventos.isEmpty()) {
            return null;
        } else {
            return listaEventos;
        }
    }

    /**
     * Busca eventos por texto en el nombre o descripción.
     * 
     * @param txt Texto a buscar
     * @param listaEventos Lista observable donde se cargarán los eventos encontrados
     * @return La lista con los eventos encontrados, o null si no se encuentra ninguno
     */
    public static ObservableList<Evento> getTxt(String txt, ObservableList<Evento> listaEventos) {
        String query = "SELECT * FROM evento WHERE nombre LIKE ? OR descripcion LIKE ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, "%" + txt + "%");
            ps.setString(2, "%" + txt + "%");
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
                listaEventos.add(evento);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.getTxt(): " + e.getMessage());
            e.printStackTrace();
        } 

        if (listaEventos.isEmpty()) {
            return null;
        } else {
            return listaEventos;
        }
    }

    /**
     * Obtiene el último ID utilizado en la tabla evento.
     * 
     * @return El último ID como entero, o 0 si no hay eventos
     */
    public static int getLastId() {
        String query = "SELECT MAX(id) FROM evento";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    //--------------------------------------------------
    // MÉTODOS DE MANIPULACIÓN DE DATOS (INSTANCIA)
    //--------------------------------------------------
    /**
     * Guarda o actualiza el evento actual en la base de datos.
     * Si el evento ya existe, lo actualiza; si no, lo inserta.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int save() {
        String querySelect = "SELECT * FROM evento WHERE id = ?";
        String queryUpdate = "UPDATE evento SET nombre = ?, descripcion = ?, lugar = ?, fecha_inicio = ?, fecha_fin = ?, id_categoria = ? WHERE id = ?";
        String queryInsert = "INSERT INTO evento VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.conectarBD();
             PreparedStatement psSelect = con.prepareStatement(querySelect)) {
            
            psSelect.setInt(1, this.getId());
            ResultSet rs = psSelect.executeQuery();
            
            if (rs.next()) {
                // Actualizar evento existente
                try (PreparedStatement psUpdate = con.prepareStatement(queryUpdate)) {
                    psUpdate.setString(1, this.getNombre());
                    psUpdate.setString(2, this.getDescripcion());
                    psUpdate.setString(3, this.getLugar());
                    psUpdate.setString(4, this.getFecha_inicio());
                    psUpdate.setString(5, this.getFecha_fin());
                    psUpdate.setInt(6, this.getId_categoria());
                    psUpdate.setInt(7, this.getId());
                    return psUpdate.executeUpdate();
                }
            } else {
                // Insertar nuevo evento
                try (PreparedStatement psInsert = con.prepareStatement(queryInsert)) {
                    psInsert.setInt(1, this.getId());
                    psInsert.setString(2, this.getNombre());
                    psInsert.setString(3, this.getDescripcion());
                    psInsert.setString(4, this.getLugar());
                    psInsert.setString(5, this.getFecha_inicio());
                    psInsert.setString(6, this.getFecha_fin());
                    psInsert.setInt(7, this.getId_categoria());
                    return psInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.save(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Elimina el evento actual de la base de datos.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int delete() {
        String query = "DELETE FROM evento WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, this.getId());
            return ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.delete(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Obtiene el ID de la categoría asociada al evento actual desde la base de datos.
     * 
     * @return El ID de la categoría como entero, o 0 si no se encuentra
     */
    public int getCategoria() {
        String query = "SELECT id_categoria FROM evento WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, this.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL en Evento.getCategoria(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}