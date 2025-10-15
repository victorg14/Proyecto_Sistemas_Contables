package org.example.proyectosistemascontables.modelos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.proyectosistemascontables.Conexion.ConexionDB;

import java.sql.*;

public class DocumentoFuenteModel {
    private Integer id_documento;
    private String nombre_documento;
    private String descripcion;
    private String tipo_documento;
    private String fecha_creacion;

    public DocumentoFuenteModel() {
    }

    public DocumentoFuenteModel(Integer id_documento, String nombre_documento, String descripcion, String tipo_documento, String fecha_creacion) {
        this.id_documento = id_documento;
        this.nombre_documento = nombre_documento;
        this.descripcion = descripcion;
        this.tipo_documento = tipo_documento;
        this.fecha_creacion = fecha_creacion;
    }

    public Integer getId_documento() {
        return id_documento;
    }

    public void setId_documento(Integer id_documento) {
        this.id_documento = id_documento;
    }

    public String getNombre_documento() {
        return nombre_documento;
    }

    public void setNombre_documento(String nombre_documento) {
        this.nombre_documento = nombre_documento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }


    // Método para insertar partida en la base de datos
    public Integer insertar() throws SQLException {
        String sql = "INSERT INTO tbldocumentos_fuente (nombre_documento, descripcion, tipo_documento, fecha_creacion) VALUES (?, ?, ?, now()) RETURNING id_documento";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.nombre_documento);
            pstmt.setString(2, this.descripcion);
            pstmt.setString(3, this.tipo_documento);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.id_documento = rs.getInt("id_documento");
                return this.id_documento;
            }
        }
        return null;
    }

    public static ObservableList<DocumentoFuenteModel> getDocumento(int id) {
        Connection connection = ConexionDB.connection();
        ObservableList<DocumentoFuenteModel> documento = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tbldocumentos_fuente WHERE id_documento = ?"
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DocumentoFuenteModel doc = new DocumentoFuenteModel(
                        resultSet.getInt("id_documento"),
                        resultSet.getString("nombre_documento"),
                        resultSet.getString("descripcion"),
                        resultSet.getString("tipo_documento"),
                        resultSet.getString("fecha_creacion")
                );
                documento.add(doc);
            }
            return documento;

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
