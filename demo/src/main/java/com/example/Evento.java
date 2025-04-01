package com.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Evento {

    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty descripcion;
    private StringProperty lugar;
    private StringProperty fecha_inicio;
    private StringProperty fecha_fin;
    private IntegerProperty id_categoria;

    public Evento(int id, String nombre, String descripcion, String lugar, String fecha_inicio, String fecha_fin, int id_categoria) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.lugar = new SimpleStringProperty(lugar);
        this.fecha_inicio = new SimpleStringProperty(fecha_inicio);
        this.fecha_fin = new SimpleStringProperty(fecha_fin);
        this.id_categoria = new SimpleIntegerProperty(id_categoria);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public StringProperty lugarProperty() {
        return lugar;
    }

    public StringProperty fecha_inicioProperty() {
        return fecha_inicio;
    }

    public StringProperty fecha_finProperty() {
        return fecha_fin;
    }

    public IntegerProperty id_categoriaProperty() {
        return id_categoria;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public String getLugar() {
        return lugar.get();
    }

    public void setLugar(String lugar) {
        this.lugar.set(lugar);
    }

    public String getFecha_inicio() {
        return fecha_inicio.get();
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio.set(fecha_inicio);
    }

    public String getFecha_fin() {
        return fecha_fin.get();
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin.set(fecha_fin);
    }

    public int getId_categoria() {
        return id_categoria.get();
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria.set(id_categoria);
    }

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
            System.out.println("Error de SQL: " + e.getMessage());
        }
        if (listaEventos.isEmpty()) {
            return null;
        }
        return listaEventos;
    }

    public static ObservableList<Evento> getId(int id1, ObservableList<Evento> listaEventos) {
        Connection con = Conexion.conectarBD();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM evento WHERE id = " + id1);
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
            System.err.println("Error de SQL: " + e.getMessage());
        } 

        if (listaEventos.isEmpty()) {
            return null;
        } else {
            return listaEventos;
        }
    }

    public static ObservableList<Evento> getTxt(String txt, ObservableList<Evento> listaEventos) {
        Connection con = Conexion.conectarBD();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM evento WHERE nombre LIKE '%" + txt + "%' OR descripcion LIKE '%" + txt + "%'");
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
            System.err.println("Error de SQL: " + e.getMessage());
        } 

        if (listaEventos.isEmpty()) {
            return null;
        } else {
            return listaEventos;
        }
    }

    public static int getLastId() {
        Connection con = Conexion.conectarBD();
        int lastId = 0;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(id) FROM evento")) {
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return lastId;
    }

    public int save() {
        Connection con = Conexion.conectarBD();
        int filasAfectadas = 0;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM evento WHERE id=" + this.getId())) {
            if (rs.next()) {
                filasAfectadas = st.executeUpdate("UPDATE evento SET  nombre='" + this.getNombre() + "', descripcion='" + this.getDescripcion() + "', lugar='" + this.getLugar() + "', fecha_inicio='" + this.getFecha_inicio() + "', fecha_fin='" + this.getFecha_fin() + "', id_categoria='" + this.getId_categoria() + "' WHERE id=" + this.getId());
            } else {
                filasAfectadas = st.executeUpdate("INSERT INTO evento VALUES (" + this.getId() + ", '" + this.getNombre() + "', '" + this.getDescripcion() + "', '" + this.getLugar() + "', '" + this.getFecha_inicio() + "', '" + this.getFecha_fin() + "', '" + this.getId_categoria() + "')");
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    public int delete() {
        Connection con = Conexion.conectarBD();
        int filasAfectadas = 0;
        try (Statement st = con.createStatement()) {
            filasAfectadas = st.executeUpdate("DELETE FROM evento WHERE id=" + this.getId());
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    public int getCategoria() {
        int id_categoriaEncontrado = 0;
        Connection con = Conexion.conectarBD();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_categoria FROM evento WHERE id=" + this.getId())) {
            if (rs.next()) {
                id_categoriaEncontrado = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return id_categoriaEncontrado;
    }
}