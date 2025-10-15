package org.example.proyectosistemascontables.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL="jdbc:postgresql://localhost:5432/aurum_financedb";
    private static final String USER="postgres";
    private static final String PASS="Amilcarito";

    public static Connection connection(){
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Conectado a la base");
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
