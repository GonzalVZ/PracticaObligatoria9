package com.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    
 public static void getAll(ObservableList<Categoria> listaCategoria) {
        String query = "SELECT * FROM categoria"; // Asegúrate de que la tabla 'evento' y sus columnas existan en la base de datos
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
    
            while (rs.next()) {
                // Asegúrate de que los nombres de las columnas coincidan con los de tu tabla en la base de datos
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                
    
                // Crea un nuevo objeto Categoria con los datos obtenidos
                Categoria categoria = new Categoria(id, nombre, descripcion);
    
                // Agrega el objeto Categoria a la lista
                listaCategoria.add(categoria);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
    }

    public static int getId(int id){
        Integer idEncontrado = null;
        Connection con = Conexion.conectarBD();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id FROM categoria WHERE id = "+id);
            if (rs.next()) {
                idEncontrado = rs.getInt(1);
                
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

        return idEncontrado;


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
                // Si el usuario ya existía, lo modificamos
                //Statement st = con.prepareStatement("UPDATE evento SET  nombre='?', descripcion='?', lugar='?', fecha_inicio='?', fecha_fin='" + this.getFecha_fin() + "', id_categoria='" + this.getId_categoria() + "' WHERE id=" + this.getId());
                //st.setString(1, this.getNombre());
                //st.setString(2, this.getDescripcion());
                filasAfectadas = st.executeUpdate("UPDATE categoria SET  nombre='" + this.getNombre() + "', descripcion='" + this.getDescripcion() );
            } else {
                // Si el usuario no existía, lo añadimos
                filasAfectadas = st.executeUpdate("INSERT INTO evento VALUES (" + this.getId() + ", '" + this.getNombre() + "', '" + this.getDescripcion() );
            }
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
            filasAfectadas = st.executeUpdate("DELETE FROM categoria WHERE id=" + this.getId());
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    






}
