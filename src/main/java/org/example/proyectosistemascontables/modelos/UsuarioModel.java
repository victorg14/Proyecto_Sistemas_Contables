package org.example.proyectosistemascontables.modelos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.proyectosistemascontables.Conexion.ConexionDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioModel {
    private Integer id_usuario;
    private String nombre;
    private String apellido;
    private String email;
    private String usuario;
    private String password;
    private String rol;
    private Boolean activo;

    // ==================== Constructores ====================
    public UsuarioModel() {
    }

    public UsuarioModel(Integer id_usuario, String nombre, String apellido, String email,
                        String usuario, String password, String rol, Boolean activo) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    // ==================== Getters y Setters ====================
    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    // ==================== Autenticación ====================
    public boolean desencriptarContraseña(String contraseña, String contraseñadb) {
        try {
            return BCrypt.checkpw(contraseña, contraseñadb);
        } catch (Exception e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }

    public Boolean Ingresar(String user, String pass) {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT password, id_usuario FROM tblUsuarios WHERE usuario = ? AND activo = true"
            );
            preparedStatement.setString(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String contra = resultSet.getString("password");
                return desencriptarContraseña(pass, contra);
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int recogerID(String user) {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id_usuario FROM tblUsuarios WHERE usuario = ?"
            );
            preparedStatement.setString(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_usuario");
            }
            return 0;

        } catch (SQLException e) {
            System.err.println("Error al recoger ID: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String recogerRol(int id) {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT rol FROM tblUsuarios WHERE id_usuario = ?"
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("rol");
            }
            return "";

        } catch (SQLException e) {
            System.err.println("Error al recoger rol: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== Operaciones CRUD ====================

    /**
     * Inserta un nuevo usuario en la base de datos
     */
    public int InsertarUsuario() {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO tblUsuarios(nombre, apellido, email, usuario, password, rol, activo) " +
                            "VALUES (?, ?, ?, ?, ?, ?, true)"
            );
            preparedStatement.setString(1, this.nombre);
            preparedStatement.setString(2, this.apellido);
            preparedStatement.setString(3, this.email);
            preparedStatement.setString(4, this.usuario);
            preparedStatement.setString(5, this.password);
            preparedStatement.setString(6, this.rol);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Actualiza un usuario existente (usa el ID del objeto)
     */
    public void actualizarUsuario() {
        if (this.id_usuario == null) {
            throw new IllegalStateException("No se puede actualizar: ID de usuario no definido");
        }

        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tblUsuarios SET nombre=?, apellido=?, email=?, usuario=?, " +
                            "password=?, rol=?, activo=? WHERE id_usuario=?"
            );
            preparedStatement.setString(1, this.nombre);
            preparedStatement.setString(2, this.apellido);
            preparedStatement.setString(3, this.email);
            preparedStatement.setString(4, this.usuario);
            preparedStatement.setString(5, this.password);
            preparedStatement.setString(6, this.rol);
            preparedStatement.setBoolean(7, this.activo);
            preparedStatement.setInt(8, this.id_usuario);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Usuario actualizado exitosamente");
            } else {
                System.out.println("No se encontró el usuario para actualizar");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Actualiza un usuario por ID (método existente mantenido para compatibilidad)
     */
    public int updateUsuario(int id) {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tblUsuarios SET nombre=?, apellido=?, email=?, usuario=?, " +
                            "password=?, rol=?, activo=? WHERE id_usuario=?"
            );
            preparedStatement.setString(1, this.nombre);
            preparedStatement.setString(2, this.apellido);
            preparedStatement.setString(3, this.email);
            preparedStatement.setString(4, this.usuario);
            preparedStatement.setString(5, this.password);
            preparedStatement.setString(6, this.rol);
            preparedStatement.setBoolean(7, this.activo);
            preparedStatement.setInt(8, id);

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Elimina un usuario de la base de datos (eliminación física)
     */
    public void eliminarUsuario() {
        if (this.id_usuario == null) {
            throw new IllegalStateException("No se puede eliminar: ID de usuario no definido");
        }

        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM tblUsuarios WHERE id_usuario = ?"
            );
            preparedStatement.setInt(1, this.id_usuario);

            int filasEliminadas = preparedStatement.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Usuario eliminado exitosamente");
            } else {
                System.out.println("No se encontró el usuario para eliminar");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Desactiva un usuario (eliminación lógica - recomendado)
     */
    public void desactivarUsuario() {
        if (this.id_usuario == null) {
            throw new IllegalStateException("No se puede desactivar: ID de usuario no definido");
        }

        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tblUsuarios SET activo = false WHERE id_usuario = ?"
            );
            preparedStatement.setInt(1, this.id_usuario);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                this.activo = false;
                System.out.println("Usuario desactivado exitosamente");
            }

        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== Consultas ====================

    public static ObservableList<UsuarioModel> getUsuario(int id) {
        Connection connection = ConexionDB.connection();
        ObservableList<UsuarioModel> usuario = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tblUsuarios WHERE id_usuario = ?"
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UsuarioModel usuarioModel = new UsuarioModel(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("email"),
                        resultSet.getString("usuario"),
                        resultSet.getString("password"),
                        resultSet.getString("rol"),
                        resultSet.getBoolean("activo")
                );
                usuario.add(usuarioModel);
            }
            return usuario;

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

    public static ObservableList<UsuarioModel> getUsuarios() {
        Connection connection = ConexionDB.connection();
        ObservableList<UsuarioModel> usuarios = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tblUsuarios ORDER BY id_usuario DESC"
            );
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UsuarioModel usuariosModel = new UsuarioModel(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("email"),
                        resultSet.getString("usuario"),
                        resultSet.getString("password"),
                        resultSet.getString("rol"),
                        resultSet.getBoolean("activo")
                );
                usuarios.add(usuariosModel);
            }
            return usuarios;

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

    public static ObservableList<UsuarioModel> buscarUsuarios(String filtro) {
        Connection connection = ConexionDB.connection();
        ObservableList<UsuarioModel> usuarios = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tblUsuarios WHERE " +
                            "usuario LIKE ? OR nombre LIKE ? OR apellido LIKE ? OR email LIKE ? " +
                            "ORDER BY id_usuario DESC"
            );
            String likeFiltro = "%" + filtro + "%";
            preparedStatement.setString(1, likeFiltro);
            preparedStatement.setString(2, likeFiltro);
            preparedStatement.setString(3, likeFiltro);
            preparedStatement.setString(4, likeFiltro);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UsuarioModel usuariosModel = new UsuarioModel(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("email"),
                        resultSet.getString("usuario"),
                        resultSet.getString("password"),
                        resultSet.getString("rol"),
                        resultSet.getBoolean("activo")
                );
                usuarios.add(usuariosModel);
            }
            return usuarios;

        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean verificarEmailExistente(String email) {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) as cantidad FROM tblUsuarios WHERE email = ?"
            );
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int cantidad = resultSet.getInt("cantidad");
                return cantidad > 0;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al verificar email: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean verificarUsuarioExistente(String usuario) {
        Connection connection = ConexionDB.connection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) as cantidad FROM tblUsuarios WHERE usuario = ?"
            );
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int cantidad = resultSet.getInt("cantidad");
                return cantidad > 0;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}