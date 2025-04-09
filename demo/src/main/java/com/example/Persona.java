package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * Clase abstracta que representa una persona en el sistema.
 * Sirve como superclase para entidades específicas como Participantes y Artistas,
 * proporcionando propiedades y métodos comunes.
 * 
 * <p>Esta clase utiliza propiedades JavaFX para permitir la vinculación
 * bidireccional con los controles de la interfaz gráfica.</p>
 * 
 * <p>Las subclases deben implementar los métodos abstractos definidos
 * para operaciones de persistencia específicas según el tipo de persona.</p>
 * 
 * @author Jesús
 * @version 1.0
 * @see Participante
 * @see Artista
 */
public abstract class Persona {

    //--------------------------------------------------
    // PROPIEDADES
    //--------------------------------------------------
    /**
     * Identificador único de la persona en la base de datos.
     */
    private IntegerProperty id;
    
    /**
     * Nombre de la persona.
     */
    private StringProperty nombre;
    
    /**
     * Primer apellido de la persona.
     */
    private StringProperty apellido1;
    
    /**
     * Segundo apellido de la persona.
     */
    private StringProperty apellido2;

    //--------------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------------
    /**
     * Constructor que inicializa una persona con todos sus atributos básicos.
     * 
     * @param id Identificador único de la persona
     * @param nombre Nombre de la persona
     * @param apellido1 Primer apellido de la persona
     * @param apellido2 Segundo apellido de la persona
     */
    public Persona(int id, String nombre, String apellido1, String apellido2) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido1 = new SimpleStringProperty(apellido1);
        this.apellido2 = new SimpleStringProperty(apellido2);
    }

    //--------------------------------------------------
    // PROPIEDADES JAVAFX
    //--------------------------------------------------
    /**
     * Obtiene la propiedad del ID para uso en JavaFX.
     * Permite la vinculación bidireccional en controles JavaFX.
     * 
     * @return La propiedad ID como IntegerProperty
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Obtiene la propiedad del nombre para uso en JavaFX.
     * Permite la vinculación bidireccional en controles JavaFX.
     * 
     * @return La propiedad nombre como StringProperty
     */
    public StringProperty nombreProperty() {
        return nombre;
    }

    /**
     * Obtiene la propiedad del primer apellido para uso en JavaFX.
     * Permite la vinculación bidireccional en controles JavaFX.
     * 
     * @return La propiedad del primer apellido como StringProperty
     */
    public StringProperty apellido1Property() {
        return apellido1;
    }

    /**
     * Obtiene la propiedad del segundo apellido para uso en JavaFX.
     * Permite la vinculación bidireccional en controles JavaFX.
     * 
     * @return La propiedad del segundo apellido como StringProperty
     */
    public StringProperty apellido2Property() {
        return apellido2;
    }

    //--------------------------------------------------
    // GETTERS Y SETTERS
    //--------------------------------------------------
    /**
     * Obtiene el ID de la persona.
     * 
     * @return El ID como entero
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el ID de la persona.
     * 
     * @param id El nuevo ID a establecer
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre de la persona.
     * 
     * @return El nombre como String
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre de la persona.
     * 
     * @param nombre El nuevo nombre a establecer
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene el primer apellido de la persona.
     * 
     * @return El primer apellido como String
     */
    public String getApellido1() {
        return apellido1.get();
    }

    /**
     * Establece el primer apellido de la persona.
     * 
     * @param apellido1 El nuevo primer apellido a establecer
     */
    public void setApellido1(String apellido1) {
        this.apellido1.set(apellido1);
    }

    /**
     * Obtiene el segundo apellido de la persona.
     * 
     * @return El segundo apellido como String
     */
    public String getApellido2() {
        return apellido2.get();
    }

    /**
     * Establece el segundo apellido de la persona.
     * 
     * @param apellido2 El nuevo segundo apellido a establecer
     */
    public void setApellido2(String apellido2) {
        this.apellido2.set(apellido2);
    }

    //--------------------------------------------------
    // MÉTODOS ABSTRACTOS
    //--------------------------------------------------
    /**
     * Guarda o actualiza la persona en la base de datos.
     * Este método debe implementarse en las clases hijas para gestionar
     * la persistencia específica según el tipo de persona.
     * 
     * @return 1 si la operación tuvo éxito, 0 si falló
     */
    public abstract int save();
    
    /**
     * Elimina una persona de la base de datos por su ID.
     * Este método debe implementarse en las clases hijas para gestionar
     * la eliminación específica según el tipo de persona.
     * 
     * @param id El ID de la persona a eliminar
     * @return 1 si la eliminación tuvo éxito, 0 si falló
     */
    public abstract int delete(int id);
    
    /**
     * Obtiene el último ID utilizado en la tabla correspondiente.
     * Útil para asignar IDs secuenciales a nuevos registros.
     * 
     * @return El último ID como entero, o 0 si no hay registros o hubo un error
     */
    public abstract int getLastId();
    
    /**
     * Busca personas que coincidan con el texto de búsqueda.
     * Este método debe implementarse en las clases hijas para realizar
     * búsquedas específicas según el tipo de persona.
     * 
     * @param txt El texto a buscar en los campos de la persona
     * @return Una lista observable de personas que coinciden con la búsqueda
     */
    public abstract ObservableList<Persona> get(String txt);
    
    /**
     * Obtiene una persona por su ID.
     * Este método debe implementarse en las clases hijas para recuperar
     * un registro específico según el tipo de persona.
     * 
     * @param id El ID de la persona a recuperar
     * @return El objeto Persona si se encuentra, o null si no existe
     */
    public abstract Persona get(int id);
    
    /**
     * Devuelve una representación en texto de la persona.
     * Esta implementación básica incluye el ID y el nombre completo.
     * Las subclases pueden sobrescribirla para mostrar información adicional.
     *
     * @return Una cadena con los datos principales de la persona
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Persona [id=").append(getId());
        sb.append(", nombre=").append(getNombre());
        sb.append(", apellidos=").append(getApellido1());
        
        String ap2 = getApellido2();
        if (ap2 != null && !ap2.isEmpty()) {
            sb.append(" ").append(ap2);
        }
        
        sb.append("]");
        return sb.toString();
    }
}