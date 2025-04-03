package com.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public  class Categoria {

    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty descripcion;

    public Categoria(int id, String nombre, String descripcion) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty apellido1Property() {
        return descripcion;
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



    public static ObservableList<String> getNombres(ObservableList<String> nombresCategorias) {
    ObservableList<String> listaNombres = FXCollections.observableArrayList();
    String query = "SELECT nombre FROM categoria";
    try (Connection con = Conexion.conectarBD();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(query)) {

        while (rs.next()) {
            String nombre = rs.getString("nombre");
            listaNombres.add(nombre); // Agregar el nombre a la lista
        }
    } catch (Exception e) {
        System.out.println("Error de SQL: " + e.getMessage());
    }
    return listaNombres;
}
public  static ObservableList<String> getNombres1(ObservableList<String> nombresCategorias) {
    String query = "SELECT nombre.categoria FROM evento INNER JOIN categoria ON evento.id_categoria=categoria.id" ;
    try (Connection con = Conexion.conectarBD();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(query)) {

        while (rs.next()) {
            String nombre = rs.getString("nombre");
            nombresCategorias.add(nombre); // Agregar el nombre a la lista
        }
    } catch (Exception e) {
        System.out.println("Error de SQL: " + e.getMessage());
    }
    return nombresCategorias;
}
    
 public static ObservableList<Categoria> getAll(ObservableList<Categoria> listaCategoria) {
        String query = "SELECT * FROM categoria"; 
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
    
            while (rs.next()) {
                
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                
    
                
                Categoria categoria = new Categoria(id, nombre, descripcion);
    
                
                listaCategoria.add(categoria);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        if (listaCategoria.isEmpty()) {
            return null;
        }
        return listaCategoria;
    }

    public static ObservableList<Categoria> getId(int id1,ObservableList<Categoria> listaCategoria){
        Integer idEncontrado = null;
        Connection con = Conexion.conectarBD();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categoria WHERE id = "+id1);
            while (rs.next()) {
                
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                
    
                
                Categoria categoria = new Categoria(id, nombre, descripcion);
    
                
                listaCategoria.add(categoria);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

        if (listaCategoria.isEmpty()) {
            return null;
        }
        return listaCategoria;
    }


    

    public static ObservableList<Categoria> getTxt(String txt,ObservableList<Categoria> listaCategoria){
        
        Connection con = Conexion.conectarBD();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre,descripcion FROM categoria WHERE nombre LIKE '%"+txt+"%' OR descripcion LIKE '%"+txt+"%'");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                
              

                Categoria evento = new Categoria(id, nombre, descripcion);

                listaCategoria.add(evento);

            } 
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (listaCategoria.isEmpty()) {
            return null;
        }else{
            return listaCategoria;
        }



    }


    public static int getLastId() {
        Connection con = Conexion.conectarBD();
        int lastId = 0;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) FROM categoria");
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return lastId;
    }
    
    
    public int save() {
        Connection con = Conexion.conectarBD();
        int filasAfectadas = 0;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categoria WHERE id=" + this.getId());
            if (rs.next()) {
                
                filasAfectadas = st.executeUpdate("UPDATE categoria SET  nombre='" + this.getNombre() + "', descripcion='" + this.getDescripcion()+ "' WHERE id=" + this.getId() );
            } else {
                filasAfectadas = st.executeUpdate("INSERT INTO categoria (nombre, descripcion) VALUES ('" + this.getNombre() + "', '" + this.getDescripcion() + "')");            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    public int delete() {
        Connection con = Conexion.conectarBD();
        int filasAfectadas = 0;
        try {
            Statement st = con.createStatement();
            filasAfectadas = st.executeUpdate("DELETE  FROM categoria WHERE id=" + this.getId());
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    






}
