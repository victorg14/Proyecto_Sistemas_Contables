package org.example.proyectosistemascontables.modelos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.proyectosistemascontables.Conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallesPartidaModel {
    private Integer id_detalle;
    private Integer id_partida;
    private Integer id_cuenta;
    private Integer id_usuario;
    private Integer id_documento;
    private String descripcion;
    private Double debe;
    private Double haber;
    private Timestamp fecha_creacion;

    public DetallesPartidaModel() {
    }

    public DetallesPartidaModel(Integer id_detalle, Integer id_partida, Integer id_cuenta, Integer id_documento, String descripcion, Double debe, Double haber) {
        this.id_detalle = id_detalle;
        this.id_partida = id_partida;
        this.id_cuenta = id_cuenta;
        this.id_documento = id_documento;
        this.descripcion = descripcion;
        this.debe = debe;
        this.haber = haber;
    }

    // Constructor completo con todos los campos
    public DetallesPartidaModel(Integer id_detalle, Integer id_partida, Integer id_cuenta, Integer id_usuario, Integer id_documento,
                                String descripcion, Double debe, Double haber, Timestamp fecha_creacion) {
        this.id_detalle = id_detalle;
        this.id_partida = id_partida;
        this.id_cuenta = id_cuenta;
        this.id_usuario = id_usuario;
        this.id_documento = id_documento;
        this.descripcion = descripcion;
        this.debe = debe;
        this.haber = haber;
        this.fecha_creacion = fecha_creacion;
    }

    // Getters y Setters


    public Integer getId_documento() {
        return id_documento;
    }

    public void setId_documento(Integer id_documento) {
        this.id_documento = id_documento;
    }

    public Integer getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(Integer id_detalle) {
        this.id_detalle = id_detalle;
    }

    public Integer getId_partida() {
        return id_partida;
    }

    public void setId_partida(Integer id_partida) {
        this.id_partida = id_partida;
    }

    public Integer getId_cuenta() {
        return id_cuenta;
    }

    public void setId_cuenta(Integer id_cuenta) {
        this.id_cuenta = id_cuenta;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getDebe() {
        return debe;
    }

    public void setDebe(Double debe) {
        this.debe = debe;
    }

    public Double getHaber() {
        return haber;
    }

    public void setHaber(Double haber) {
        this.haber = haber;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    // Método para insertar detalle de partida (con conexión propia)
    public void insertar() throws SQLException {
        String sql = "INSERT INTO tblDetalles_partida (id_partida, id_cuenta, id_usuario, id_documento, descripcion, debe, haber) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, this.id_partida);
            pstmt.setInt(2, this.id_cuenta);

            if (this.id_usuario != null) {
                pstmt.setInt(3, this.id_usuario);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, this.id_documento);
            pstmt.setString(5, this.descripcion);
            pstmt.setDouble(6, this.debe != null ? this.debe : 0.0);
            pstmt.setDouble(7, this.haber != null ? this.haber : 0.0);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.id_detalle = rs.getInt(1);
            }
        }
    }

    // Método para insertar
    public void insertar(Connection conn) throws SQLException {
        String sql = "INSERT INTO tblDetalles_partida (id_partida, id_cuenta, id_usuario, id_documento, descripcion, debe, haber) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, this.id_partida);
            pstmt.setInt(2, this.id_cuenta);

            if (this.id_usuario != null) {
                pstmt.setInt(3, this.id_usuario);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, this.id_documento);
            pstmt.setString(5, this.descripcion);
            pstmt.setDouble(6, this.debe != null ? this.debe : 0.0);
            pstmt.setDouble(7, this.haber != null ? this.haber : 0.0);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.id_detalle = rs.getInt(1);
            }
        }
    }

    // Método para obtener detalles por partida
    public static List<DetallesPartidaModel> obtenerPorPartida(Integer idPartida) throws SQLException {
        List<DetallesPartidaModel> detalles = new ArrayList<>();
        String sql = "SELECT d.*, c.nombre_cuenta as descripcion_cuenta " +
                "FROM tblDetalles_partida d " +
                "INNER JOIN tblCuentasContables c ON d.id_cuenta = c.id_cuenta " +
                "WHERE d.id_partida = ? " +
                "ORDER BY d.id_detalle";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPartida);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetallesPartidaModel detalle = new DetallesPartidaModel(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_partida"),
                        rs.getInt("id_cuenta"),
                        rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null,
                        rs.getInt("id_documento"),
                        rs.getString("descripcion_cuenta"),
                        rs.getDouble("debe"),
                        rs.getDouble("haber"),
                        rs.getTimestamp("fecha_creacion")
                );
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    // Método para eliminar detalle
    public void eliminar() throws SQLException {
        String sql = "DELETE FROM tblDetalles_partida WHERE id_detalle = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id_detalle);
            pstmt.executeUpdate();
        }
    }

    // Método para actualizar detalle
    public void actualizar() throws SQLException {
        String sql = "UPDATE tblDetalles_partida SET id_cuenta = ?, id_usuario = ?, descripcion = ?, " +
                "debe = ?, haber = ? WHERE id_detalle = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id_cuenta);

            if (this.id_usuario != null) {
                pstmt.setInt(2, this.id_usuario);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, this.descripcion);
            pstmt.setDouble(4, this.debe != null ? this.debe : 0.0);
            pstmt.setDouble(5, this.haber != null ? this.haber : 0.0);
            pstmt.setInt(6, this.id_detalle);
            pstmt.executeUpdate();
        }
    }

    // Método para calcular totales de una partida
    public static double[] calcularTotales(Integer idPartida) throws SQLException {
        String sql = "SELECT SUM(debe) as total_debe, SUM(haber) as total_haber " +
                "FROM tblDetalles_partida WHERE id_partida = ?";

        try (Connection conn = ConexionDB.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPartida);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new double[]{
                        rs.getDouble("total_debe"),
                        rs.getDouble("total_haber")
                };
            }
        }
        return new double[]{0.0, 0.0};
    }

    // Método para obtener todos los detalles
    public static List<DetallesPartidaModel> obtenerTodos() throws SQLException {
        List<DetallesPartidaModel> detalles = new ArrayList<>();
        String sql = "SELECT d.*, c.nombre_cuenta as descripcion_cuenta " +
                "FROM tblDetalles_partida d " +
                "INNER JOIN tblCuentasContables c ON d.id_cuenta = c.id_cuenta " +
                "ORDER BY d.id_partida, d.id_detalle";

        try (Connection conn = ConexionDB.connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DetallesPartidaModel detalle = new DetallesPartidaModel(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_partida"),
                        rs.getInt("id_cuenta"),
                        rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null,
                        rs.getInt("id_documento"),
                        rs.getString("descripcion_cuenta"),
                        rs.getDouble("debe"),
                        rs.getDouble("haber"),
                        rs.getTimestamp("fecha_creacion")
                );
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    // Método para obtener todos los detalles
    public static ObservableList<DetallesPartidaModel> getdetalles() {
        Connection connection = ConexionDB.connection();
        ObservableList<DetallesPartidaModel> detalles = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tbldetalles_partida ORDER BY fecha_creacion DESC"
            );
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DetallesPartidaModel detalle = new DetallesPartidaModel(
                        resultSet.getInt("id_detalle"),
                        resultSet.getInt("id_partida"),
                        resultSet.getInt("id_cuenta"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getInt("id_documento"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("debe"),
                        resultSet.getDouble("haber"),
                        resultSet.getTimestamp("fecha_creacion")
                );
               detalles.add(detalle);
            }
            return detalles;

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static ObservableList<DetallesPartidaModel> getdetallesFiltro(Timestamp inicio, Timestamp fin) {
        Connection connection = ConexionDB.connection();
        ObservableList<DetallesPartidaModel> detalles = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tbldetalles_partida WHERE fecha_creacion BETWEEN ? AND ?"
            );
            preparedStatement.setTimestamp(1, inicio);
            preparedStatement.setTimestamp(2, fin);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DetallesPartidaModel detalle = new DetallesPartidaModel(
                        resultSet.getInt("id_detalle"),
                        resultSet.getInt("id_partida"),
                        resultSet.getInt("id_cuenta"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getInt("id_documento"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("debe"),
                        resultSet.getDouble("haber"),
                        resultSet.getTimestamp("fecha_creacion")
                );
                detalles.add(detalle);
            }
            return detalles;

        } catch (SQLException e) {
            System.err.println("Error al obtener los detalles: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ObservableList<DetallesPartidaModel> getdetallesFiltroMayor(Timestamp inicio, Timestamp fin, String cuenta) {
        Connection connection = ConexionDB.connection();
        ObservableList<DetallesPartidaModel> detalles = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tbldetalles_partida WHERE fecha_creacion BETWEEN ? AND ? AND descripcion = ?"
            );
            preparedStatement.setTimestamp(1, inicio);
            preparedStatement.setTimestamp(2, fin);
            preparedStatement.setString(3, cuenta);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DetallesPartidaModel detalle = new DetallesPartidaModel(
                        resultSet.getInt("id_detalle"),
                        resultSet.getInt("id_partida"),
                        resultSet.getInt("id_cuenta"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getInt("id_documento"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("debe"),
                        resultSet.getDouble("haber"),
                        resultSet.getTimestamp("fecha_creacion")
                );
                detalles.add(detalle);
            }
            return detalles;

        } catch (SQLException e) {
            System.err.println("Error al obtener los detalles: " + e.getMessage());
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