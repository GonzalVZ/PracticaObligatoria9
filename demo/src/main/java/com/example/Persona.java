package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public abstract class Persona {

    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty apellido1;
    private StringProperty apellido2;

    public Persona(int id, String nombre, String apellido1, String apellido2) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido1 = new SimpleStringProperty(apellido1);
        this.apellido2 = new SimpleStringProperty(apellido2);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty apellido1Property() {
        return apellido1;
    }

    public StringProperty apellido2Property() {
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
        this.apellido2.set(apellido2);  // Corregido el error
    }

    // Métodos abstractos que las subclases deben implementar
    public abstract int save();
    public abstract int delete(int id);
    public abstract void getAll(ObservableList<Persona> lista);
    public abstract int getLastId();
    public abstract ObservableList<Persona> get(String txt);
    public abstract Persona get(int id);

}
