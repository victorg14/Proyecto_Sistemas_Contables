package org.example.proyectosistemascontables.modelos;

import org.example.proyectosistemascontables.Conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidasModel {
    private Integer id_partida;
    private Integer id_usuario;
    private Integer numero_partida;
    private String fecha;
    private String concepto;

    public PartidasModel() {
    }

    public PartidasModel(Integer id_partida, Integer id_usuario, Integer numero_partida, String fecha, String concepto) {
        this.id_partida = id_partida;
        this.id_usuario = id_usuario;
        this.numero_partida = numero_partida;
        this.fecha = fecha;
        this.concepto = concepto;
    }

    // Getters y Setters
    public Integer getId_partida() {
        return id_partida;
    }

    public void setId_partida(Integer id_partida) {
        this.id_partida = id_partida;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getNumero_partida() {
        return numero_partida;
    }

    public void setNumero_partida(Integer numero_partida) {
        this.numero_partida = numero_partida;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    // Método para insertar partida en la base de datos
    public Integer insertar() throws SQLException {
        String sql = "INSERT INTO tblPartidas (id_usuario, numero_partida, fecha, concepto) VALUES (?, ?, ?, ?) RETURNING id_partida";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id_usuario);
            pstmt.setInt(2, this.numero_partida);
            pstmt.setDate(3, Date.valueOf(this.fecha));
            pstmt.setString(4, this.concepto);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.id_partida = rs.getInt("id_partida");
                return this.id_partida;
            }
        }
        return null;
    }

    // Método para insertar con conexión externa (para transacciones)
    public Integer insertar(Connection conn) throws SQLException {
        String sql = "INSERT INTO tblPartidas (id_usuario, numero_partida, fecha, concepto) VALUES (?, ?, ?, ?) RETURNING id_partida";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id_usuario);
            pstmt.setInt(2, this.numero_partida);
            pstmt.setDate(3, Date.valueOf(this.fecha));
            pstmt.setString(4, this.concepto);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.id_partida = rs.getInt("id_partida");
                return this.id_partida;
            }
        }
        return null;
    }

    // Método para obtener el siguiente número de partida
    public static Integer obtenerSiguienteNumeroPartida() throws SQLException {
        String sql = "SELECT COALESCE(MAX(numero_partida), 0) + 1 as siguiente FROM tblPartidas";

        try (Connection conn = ConexionDB.connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 1;
    }

    // Sobrecarga para usar con conexión externa
    public static Integer obtenerSiguienteNumeroPartida(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(numero_partida), 0) + 1 as siguiente FROM tblPartidas";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 1;
    }

    // Método para obtener todas las partidas
    public static List<PartidasModel> obtenerTodas() throws SQLException {
        List<PartidasModel> partidas = new ArrayList<>();
        String sql = "SELECT * FROM tblPartidas ORDER BY numero_partida DESC";

        try (Connection conn = ConexionDB.connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PartidasModel partida = new PartidasModel(
                        rs.getInt("id_partida"),
                        rs.getInt("id_usuario"),
                        rs.getInt("numero_partida"),
                        rs.getDate("fecha").toString(),
                        rs.getString("concepto")
                );
                partidas.add(partida);
            }
        }
        return partidas;
    }

    // Método para eliminar una partida
    public void eliminar() throws SQLException {
        String sql = "DELETE FROM tblPartidas WHERE id_partida = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id_partida);
            pstmt.executeUpdate();
        }
    }

    // Método para actualizar una partida
    public void actualizar() throws SQLException {
        String sql = "UPDATE tblPartidas SET id_usuario = ?, numero_partida = ?, fecha = ?, concepto = ? WHERE id_partida = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id_usuario);
            pstmt.setInt(2, this.numero_partida);
            pstmt.setDate(3, Date.valueOf(this.fecha));
            pstmt.setString(4, this.concepto);
            pstmt.setInt(5, this.id_partida);
            pstmt.executeUpdate();
        }
    }

    // Método para buscar partida por ID
    public static PartidasModel buscarPorId(Integer idPartida) throws SQLException {
        String sql = "SELECT * FROM tblPartidas WHERE id_partida = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPartida);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PartidasModel(
                        rs.getInt("id_partida"),
                        rs.getInt("id_usuario"),
                        rs.getInt("numero_partida"),
                        rs.getDate("fecha").toString(),
                        rs.getString("concepto")
                );
            }
        }
        return null;
    }

    // Método para buscar por número de partida
    public static PartidasModel buscarPorNumero(Integer numeroPartida) throws SQLException {
        String sql = "SELECT * FROM tblPartidas WHERE numero_partida = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numeroPartida);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PartidasModel(
                        rs.getInt("id_partida"),
                        rs.getInt("id_usuario"),
                        rs.getInt("numero_partida"),
                        rs.getDate("fecha").toString(),
                        rs.getString("concepto")
                );
            }
        }
        return null;
    }
}