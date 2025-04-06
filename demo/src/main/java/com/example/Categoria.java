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
 * Clase que representa una categoría en el sistema.
 * Incluye métodos para acceder y manipular los datos de las categorías.
 */
public class Categoria {

    //--------------------------------------------------
    // PROPIEDADES
    //--------------------------------------------------
    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty descripcion;

    //--------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------
    /**
     * Constructor de la clase Categoria.
     * 
     * @param id Identificador único de la categoría
     * @param nombre Nombre de la categoría
     * @param descripcion Descripción detallada de la categoría
     */
    public Categoria(int id, String nombre, String descripcion) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
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

    //--------------------------------------------------
    // GETTERS Y SETTERS
    //--------------------------------------------------
    /**
     * Obtiene el ID de la categoría.
     * @return El ID como entero
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el ID de la categoría.
     * @param id El nuevo ID
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre de la categoría.
     * @return El nombre como String
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre de la categoría.
     * @param nombre El nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene la descripción de la categoría.
     * @return La descripción como String
     */
    public String getDescripcion() {
        return descripcion.get();
    }

    /**
     * Establece la descripción de la categoría.
     * @param descripcion La nueva descripción
     */
    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    //--------------------------------------------------
    // MÉTODOS DE ACCESO A DATOS (ESTÁTICOS)
    //--------------------------------------------------
    /**
     * Obtiene todos los nombres de categorías desde la base de datos.
     * 
     * @param nombresCategorias Lista observable donde se cargarán los nombres
     * @return La lista actualizada con los nombres de las categorías
     */
    public static ObservableList<String> getNombres(ObservableList<String> nombresCategorias) {
        String query = "SELECT nombre FROM categoria";
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

    /**
     * Obtiene todas las categorías desde la base de datos.
     * 
     * @param listaCategoria Lista observable donde se cargarán las categorías
     * @return La lista actualizada con las categorías
     */
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
        return listaCategoria;
    }

    /**
     * Obtiene el último ID utilizado en la tabla categoría.
     * 
     * @return El último ID como entero, o 0 si no hay categorías
     */
    public static int getLastId() {
        String query = "SELECT MAX(id) FROM categoria";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return 0; // Si no hay registros
    }

    //--------------------------------------------------
    // MÉTODOS DE MANIPULACIÓN DE DATOS (INSTANCIA)
    //--------------------------------------------------
    /**
     * Guarda o actualiza la categoría actual en la base de datos.
     * Si el ID es 0, inserta una nueva categoría.
     * Si el ID es distinto de 0, actualiza la categoría existente.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int save() {
        String queryInsert = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";
        String queryUpdate = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection con = Conexion.conectarBD()) {
            if (this.getId() == 0) { // Si no tiene ID, es una nueva categoría
                try (PreparedStatement ps = con.prepareStatement(queryInsert)) {
                    ps.setString(1, this.getNombre());
                    ps.setString(2, this.getDescripcion());
                    return ps.executeUpdate();
                }
            } else { // Si tiene ID, actualiza la categoría existente
                try (PreparedStatement ps = con.prepareStatement(queryUpdate)) {
                    ps.setString(1, this.getNombre());
                    ps.setString(2, this.getDescripcion());
                    ps.setInt(3, this.getId());
                    return ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Elimina la categoría actual de la base de datos.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int delete() {
        String query = "DELETE FROM categoria WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, this.getId());
            return ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error de SQL: " + e.getMessage());
        }
        return 0;
    }
}