import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/nombre_basedatos";
    private static final String USUARIO = "usuario";
    private static final String CONTRASENA = "contrasena";

    public static Connection conectar() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    public static void main(String[] args) {
        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultados = sentencia.executeQuery("SELECT * FROM tabla")) {

            while (resultados.next()) {
                System.out.println(resultados.getString("columna1"));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}