package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos MySQL.
 * Proporciona métodos para establecer y verificar conexiones con la base de datos
 * que almacena la información de eventos, artistas y participantes.
 * 
 * <p>Esta clase implementa un patrón de acceso simple mediante métodos estáticos
 * para facilitar la obtención de conexiones a la base de datos en cualquier
 * punto de la aplicación.</p>
 * 
 * @author Jesús
 * @version 1.0
 */
public class Conexion {
    
    /**
     * URL de la base de datos MySQL.
     * Define la ubicación del servidor, puerto y nombre de la base de datos.
     */
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/EventoDB";
    
    /**
     * Nombre de usuario para la conexión a la base de datos.
     */
    private static final String DB_USER = "root";
    
    /**
     * Contraseña para la conexión a la base de datos.
     */
    private static final String DB_PASSWORD = "root";
    
    /**
     * Nombre del driver JDBC para MySQL.
     */
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Establece una conexión con la base de datos MySQL.
     * 
     * <p>Este método carga el driver JDBC de MySQL e intenta establecer una conexión
     * utilizando las credenciales y URL configuradas. Es el método principal para
     * obtener una conexión activa desde cualquier parte de la aplicación.</p>
     * 
     * @return Un objeto Connection si la conexión es exitosa, o null si falla
     */
    public static Connection conectarBD() {
        try {
            // Cargar el driver JDBC
            Class.forName(JDBC_DRIVER);
            
            // Establecer la conexión a la base de datos
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            // Error específico cuando no se encuentra el driver
            System.err.println("Error: Driver MySQL no encontrado - " + e.getMessage());
            return null;
        } catch (SQLException e) {
            // Error específico de SQL (problemas de conexión, credenciales, etc.)
            System.err.println("Error de SQL al conectar: " + e.getMessage());
            System.err.println("Código de error SQL: " + e.getErrorCode());
            return null;
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            System.err.println("Error inesperado al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica si la base de datos está disponible intentando establecer una conexión.
     * Este método es útil para comprobar la conectividad antes de realizar operaciones.
     * 
     * @return true si la conexión puede establecerse, false en caso contrario
     */
    public static boolean verificarConexion() {
        Connection conn = null;
        try {
            conn = conectarBD();
            return conn != null;
        } finally {
            cerrarConexion(conn);
        }
    }
    
    /**
     * Cierra de forma segura una conexión a la base de datos.
     * Este método se encarga de liberar recursos asociados a una conexión.
     * 
     * @param conn La conexión a cerrar, puede ser null
     */
    public static void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}