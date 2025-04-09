package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Clase que representa una categoría en el sistema de gestión de eventos.
 * Las categorías permiten agrupar eventos por tipo o temática para facilitar
 * su clasificación y búsqueda.
 * 
 * <p>Esta clase proporciona métodos para crear, recuperar, actualizar y eliminar
 * categorías en la base de datos, así como para obtener listas de categorías
 * o sus nombres para su uso en selectores e interfaces.</p>
 * 
 * @author Jesús
 * @version 1.0
 * @see Evento
 */
public class Categoria {

    //--------------------------------------------------
    // PROPIEDADES
    //--------------------------------------------------
    /**
     * Identificador único de la categoría en la base de datos.
     */
    private IntegerProperty id;
    
    /**
     * Nombre descriptivo de la categoría.
     */
    private StringProperty nombre;
    
    /**
     * Descripción detallada sobre el propósito o alcance de la categoría.
     */
    private StringProperty descripcion;

    //--------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------
    /**
     * Constructor de la clase Categoria.
     * Inicializa una nueva instancia con los valores proporcionados.
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
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad ID como IntegerProperty
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Obtiene la propiedad del nombre para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
     * @return La propiedad nombre como StringProperty
     */
    public StringProperty nombreProperty() {
        return nombre;
    }

    /**
     * Obtiene la propiedad de la descripción para uso en JavaFX.
     * Útil para vincular directamente esta propiedad en componentes JavaFX.
     * 
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
     * 
     * @return El ID como entero
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el ID de la categoría.
     * 
     * @param id El nuevo ID a asignar
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre de la categoría.
     * 
     * @return El nombre como String
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre de la categoría.
     * 
     * @param nombre El nuevo nombre a asignar
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene la descripción de la categoría.
     * 
     * @return La descripción como String
     */
    public String getDescripcion() {
        return descripcion.get();
    }

    /**
     * Establece la descripción de la categoría.
     * 
     * @param descripcion La nueva descripción a asignar
     */
    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    //--------------------------------------------------
    // MÉTODOS DE ACCESO A DATOS (ESTÁTICOS)
    //--------------------------------------------------
    /**
     * Obtiene todos los nombres de categorías desde la base de datos.
     * Este método es útil para llenar comboboxes o listas de selección
     * con solo los nombres de las categorías.
     * 
     * @param nombresCategorias Lista observable donde se cargarán los nombres
     * @return La lista actualizada con los nombres de las categorías
     */
    public static ObservableList<String> getNombres(ObservableList<String> nombresCategorias) {
        String query = "SELECT nombre FROM categoria";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos para obtener categorías.");
                return nombresCategorias;
            }
            
            nombresCategorias.clear(); // Limpiamos la lista antes de añadir elementos
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                nombresCategorias.add(nombre); // Agregar el nombre a la lista
            }
        } catch (SQLException e) {
            String errorMsg = "Error al recuperar nombres de categorías: " + e.getMessage();
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.out.println("Error de SQL en Categoria.getNombres(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", "Ocurrió un problema al cargar los nombres de categorías: " + e.getMessage());
            System.out.println("Error inesperado en Categoria.getNombres(): " + e.getMessage());
            e.printStackTrace();
        }
        return nombresCategorias;
    }

    /**
     * Obtiene todas las categorías desde la base de datos.
     * Este método recupera las categorías completas con todos sus datos.
     * 
     * @param listaCategoria Lista observable donde se cargarán las categorías
     * @return La lista actualizada con todas las categorías
     */
    public static ObservableList<Categoria> getAll(ObservableList<Categoria> listaCategoria) {
        String query = "SELECT * FROM categoria";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos para obtener categorías.");
                return listaCategoria;
            }
            
            listaCategoria.clear(); // Limpiamos la lista antes de añadir elementos
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");

                Categoria categoria = new Categoria(id, nombre, descripcion);
                listaCategoria.add(categoria);
            }
        } catch (SQLException e) {
            String errorMsg = "Error al recuperar categorías: " + e.getMessage();
            if (e.getMessage().contains("doesn't exist")) {
                errorMsg = "La tabla de categorías no existe en la base de datos.";
            }
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.out.println("Error de SQL en Categoria.getAll(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", "Ocurrió un problema al cargar las categorías: " + e.getMessage());
            System.out.println("Error inesperado en Categoria.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return listaCategoria;
    }

    /**
     * Obtiene el último ID utilizado en la tabla categoría.
     * Útil para asignar IDs a nuevos registros manualmente si es necesario.
     * 
     * @return El último ID como entero, o 0 si no hay categorías o hubo un error
     */
    public static int getLastId() {
        String query = "SELECT MAX(id) FROM categoria";
        try (Connection con = Conexion.conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos para obtener el último ID.");
                return 0;
            }
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", "Error al recuperar el último ID: " + e.getMessage());
            System.out.println("Error de SQL en Categoria.getLastId(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", "Ocurrió un problema al obtener el último ID: " + e.getMessage());
            System.out.println("Error inesperado en Categoria.getLastId(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0; // Si no hay registros o hubo un error
    }

    /**
     * Obtiene una categoría específica por su ID.
     * 
     * @param categoriaId El ID de la categoría a buscar
     * @return La categoría encontrada, o null si no existe
     */
    public static Categoria getById(int categoriaId) {
        String query = "SELECT * FROM categoria WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos para buscar la categoría.");
                return null;
            }
            
            ps.setInt(1, categoriaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                return new Categoria(id, nombre, descripcion);
            }
        } catch (SQLException e) {
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", "Error al buscar la categoría: " + e.getMessage());
            System.out.println("Error de SQL en Categoria.getById(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", "Ocurrió un problema al buscar la categoría: " + e.getMessage());
            System.out.println("Error inesperado en Categoria.getById(): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos para guardar la categoría.");
                return 0;
            }
            
            // Validación básica de datos
            if (getNombre() == null || getNombre().trim().isEmpty()) {
                mostrarAlerta(AlertType.WARNING, "Datos incompletos", "El nombre de la categoría no puede estar vacío.");
                return 0;
            }
            
            if (this.getId() == 0) { // Si no tiene ID, es una nueva categoría
                try (PreparedStatement ps = con.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, this.getNombre());
                    ps.setString(2, this.getDescripcion());
                    
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        // Obtener el ID generado
                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            setId(rs.getInt(1));
                        }
                        mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", "Categoría creada correctamente.");
                    }
                    return result;
                }
            } else { // Si tiene ID, actualiza la categoría existente
                try (PreparedStatement ps = con.prepareStatement(queryUpdate)) {
                    ps.setString(1, this.getNombre());
                    ps.setString(2, this.getDescripcion());
                    ps.setInt(3, this.getId());
                    
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", "Categoría actualizada correctamente.");
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            String errorMsg = "Error al guardar la categoría: ";
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                errorMsg += "Ya existe una categoría con este nombre.";
            } else {
                errorMsg += e.getMessage();
            }
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.out.println("Error de SQL en Categoria.save(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", "Ocurrió un problema al guardar la categoría: " + e.getMessage());
            System.out.println("Error inesperado en Categoria.save(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Elimina la categoría actual de la base de datos.
     * Este método borrará la categoría y afectará a todos los eventos que la utilizan.
     * 
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si falló)
     */
    public int delete() {
        String query = "DELETE FROM categoria WHERE id = ?";
        try (Connection con = Conexion.conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (con == null) {
                mostrarAlerta(AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos para eliminar la categoría.");
                return 0;
            }
            
            ps.setInt(1, this.getId());
            int result = ps.executeUpdate();
            
            if (result > 0) {
                mostrarAlerta(AlertType.INFORMATION, "Operación exitosa", "La categoría ha sido eliminada correctamente.");
            }
            return result;
        } catch (SQLException e) {
            String errorMsg = "Error al eliminar la categoría: ";
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                errorMsg += "Esta categoría está siendo utilizada por eventos y no puede eliminarse. " +
                           "Debe eliminar o reasignar los eventos asociados primero.";
            } else {
                errorMsg += e.getMessage();
            }
            mostrarAlerta(AlertType.ERROR, "Error de base de datos", errorMsg);
            System.out.println("Error de SQL en Categoria.delete(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error inesperado", "Ocurrió un problema al eliminar la categoría: " + e.getMessage());
            System.out.println("Error inesperado en Categoria.delete(): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    //--------------------------------------------------
    // MÉTODOS DE UTILIDAD
    //--------------------------------------------------
    /**
     * Muestra un cuadro de diálogo de alerta con el tipo, título y mensaje especificados.
     * 
     * @param tipo El tipo de alerta (ERROR, INFORMATION, WARNING, etc.)
     * @param titulo El título del cuadro de diálogo
     * @param mensaje El mensaje a mostrar
     */
    private static void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        try {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(tipo);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensaje);
                alert.showAndWait();
            });
        } catch (Exception e) {
            // Si no se puede mostrar la alerta (por ejemplo, si no estamos en un entorno JavaFX)
            System.out.println("[ALERTA " + tipo + "] " + titulo + ": " + mensaje);
        }
    }
    
    /**
     * Devuelve una representación en texto de la categoría, útil para depuración.
     * 
     * @return Una cadena con los datos principales de la categoría
     */
    @Override
    public String toString() {
        return "Categoria [id=" + getId() + ", nombre=" + getNombre() + "]";
    }
}