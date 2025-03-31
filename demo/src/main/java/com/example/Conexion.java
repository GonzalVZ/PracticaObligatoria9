package com.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
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
