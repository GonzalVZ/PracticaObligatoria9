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

public class Persona {


    private IntegerProperty id;

    private StringProperty nombre;

    private StringProperty apellido1;

    private StringProperty apellido2;

    



    public Persona(int id,String nombre,String apellido1,String apellido2){

        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido1 = new SimpleStringProperty(apellido1);
        this.apellido2 = new SimpleStringProperty(apellido2);
      



    }

    public IntegerProperty idProperty(){
        return id;
    }
    
    public StringProperty nombreProperty(){
        return nombre;
    }

    public StringProperty apellido1Property(){
        return apellido1;
    }

    public StringProperty apellido2Property(){
        return apellido2;
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

    public String getApellido1() {
        return apellido1.get();
    }

    public void setApellido1(String apellido1) {
        this.apellido1.set(apellido1);
    }

    public String getApellido2() {
        return apellido2.get();
    }

    public void setApellido2(String apellido2) {
        this.apellido1.set(apellido2);
    }

    public static void getAll(ObservableList<Persona> listaPersona) {
        String query = "SELECT * FROM persona"; // Asegúrate de que la tabla 'persona' y sus columnas existan en la base de datos
        try (Connection con = conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
    
            while (rs.next()) {
                // Asegúrate de que los nombres de las columnas coincidan con los de tu tabla en la base de datos
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                
    
                // Crea un nuevo objeto Persona con los datos obtenidos
                Persona persona = new Persona(id, nombre, apellido1, apellido2);
    
                // Agrega el objeto Persona a la lista
                listaPersona.add(persona);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
    }



    
    public static void getAll(ObservableList<Persona> listaPersona) {
        String query = "SELECT * FROM persona"; // Asegúrate de que la tabla 'evento' y sus columnas existan en la base de datos
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

    public static int getId(int id){
        Integer idEncontrado = null;
        Connection con = conectarBD();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id FROM evento WHERE id = "+id);
            if (rs.next()) {
                idEncontrado = rs.getInt(1);
                
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

        return idEncontrado;


    }

    public static ObservableList<Evento> getTxt(String txt,ObservableList<Evento> listaEventos){
        
        Connection con = conectarBD();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre,descripcion FROM evento WHERE nombre LIKE '%"+txt+"%' OR descripcion LIKE '%"+txt+"%'");
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
            // TODO: handle exception
        }

        if (listaEventos.isEmpty()) {
            return null;
        }else{
            return listaEventos;
        }



    }


    public static int getLastId() {
        Connection con = conectarBD();
        int lastId = 0;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) FROM evento");
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
        Connection con = conectarBD();
        int filasAfectadas = 0;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM evento WHERE id=" + this.getId());
            if (rs.next()) {
                // Si el usuario ya existía, lo modificamos
                //Statement st = con.prepareStatement("UPDATE evento SET  nombre='?', descripcion='?', lugar='?', fecha_inicio='?', fecha_fin='" + this.getFecha_fin() + "', id_categoria='" + this.getId_categoria() + "' WHERE id=" + this.getId());
                //st.setString(1, this.getNombre());
                //st.setString(2, this.getDescripcion());
                filasAfectadas = st.executeUpdate("UPDATE evento SET  nombre='" + this.getNombre() + "', descripcion='" + this.getDescripcion() + "', lugar='" + this.getLugar() + "', fecha_inicio='" + this.getFecha_inicio() + "', fecha_fin='" + this.getFecha_fin() + "', id_categoria='" + this.getId_categoria() + "' WHERE id=" + this.getId());
            } else {
                // Si el usuario no existía, lo añadimos
                filasAfectadas = st.executeUpdate("INSERT INTO evento VALUES (" + this.getId() + ", '" + this.getNombre() + "', '" + this.getDescripcion() + "', '" + this.getLugar() + "', '" + this.getFecha_inicio() + "', '" + this.getFecha_fin() + "', '" + this.getId_categoria() + "')");
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    public int delete() {
        Connection con = conectarBD();
        int filasAfectadas = 0;
        try {
            Statement st = con.createStatement();
            filasAfectadas = st.executeUpdate("DELETE FROM evento WHERE id=" + this.getId());
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return filasAfectadas;
    }

    public int getCategoria(){
    int id_categoriaEncontrado = 0;
        Connection con = conectarBD();
        
        try {
            Statement st = con.createStatement();
            ResultSet rs =  st.executeQuery("SELECT id_categoria FROM evento WHERE id=" + this.getId());
            if (rs.next()) {
                id_categoriaEncontrado = rs.getInt(1);
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return id_categoriaEncontrado;
    }
    




}

