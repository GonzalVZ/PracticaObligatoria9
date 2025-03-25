package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
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



    public Evento(int id,String nombre,String descripcion,String lugar,String fecha_inicio,String fecha_fin,int id_categoria){

        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.lugar = new SimpleStringProperty(lugar);
        this.fecha_inicio = new SimpleStringProperty(fecha_inicio);
        this.fecha_fin = new SimpleStringProperty(fecha_fin);
        this.id_categoria = new SimpleIntegerProperty(id_categoria);



    }

    public IntegerProperty idProperty(){
        return id;
    }
    
    public StringProperty nombreProperty(){
        return nombre;
    }

    public StringProperty descripcionProperty(){
        return descripcion;
    }

    public StringProperty lugarProperty(){
        return lugar;
    }

    public StringProperty fecha_inicioProperty(){
        return fecha_inicio;
    }

    public StringProperty fecha_finProperty(){
        return fecha_fin;
    }

    public IntegerProperty id_categoriaProperty(){
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


    public static void getAll(ObservableList<Evento> listaEventos) {
        String query = "SELECT * FROM evento"; // Asegúrate de que la tabla 'evento' y sus columnas existan en la base de datos
        try (Connection con = conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
    
            while (rs.next()) {
                // Asegúrate de que los nombres de las columnas coincidan con los de tu tabla en la base de datos
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String lugar = rs.getString("lugar");
                String fecha_inicio = rs.getString("fecha_inicio");
                String fecha_fin = rs.getString("fecha_fin");
                int id_categoria = rs.getInt("id_categoria");
    
                // Crea un nuevo objeto Evento con los datos obtenidos
                Evento evento = new Evento(id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria);
    
                // Agrega el objeto Evento a la lista
                listaEventos.add(evento);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
    }

    
    
    private static Connection conectarBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/EventoDB", "root", "root");
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
            return null;
        }
    }


}
