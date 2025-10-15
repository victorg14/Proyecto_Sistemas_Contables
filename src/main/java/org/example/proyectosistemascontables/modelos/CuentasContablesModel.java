package org.example.proyectosistemascontables.modelos;

public class CuentasContablesModel {
    private Integer id_cuenta;
    private String codigo_cuenta;
    private String nombre_cuenta;
    private String tipo_cuenta;
    private Integer nivel;
    private Integer id_cuenta_padre;
    private Boolean estado;

    public CuentasContablesModel() {
    }

    public CuentasContablesModel(Integer id_cuenta, String codigo_cuenta, String nombre_cuenta, String tipo_cuenta, Integer nivel, Integer id_cuenta_padre, Boolean estado) {
        this.id_cuenta = id_cuenta;
        this.codigo_cuenta = codigo_cuenta;
        this.nombre_cuenta = nombre_cuenta;
        this.tipo_cuenta = tipo_cuenta;
        this.nivel = nivel;
        this.id_cuenta_padre = id_cuenta_padre;
        this.estado = estado;
    }

    public Integer getId_cuenta() {
        return id_cuenta;
    }

    public void setId_cuenta(Integer id_cuenta) {
        this.id_cuenta = id_cuenta;
    }

    public String getCodigo_cuenta() {
        return codigo_cuenta;
    }

    public void setCodigo_cuenta(String codigo_cuenta) {
        this.codigo_cuenta = codigo_cuenta;
    }

    public String getNombre_cuenta() {
        return nombre_cuenta;
    }

    public void setNombre_cuenta(String nombre_cuenta) {
        this.nombre_cuenta = nombre_cuenta;
    }

    public String getTipo_cuenta() {
        return tipo_cuenta;
    }

    public void setTipo_cuenta(String tipo_cuenta) {
        this.tipo_cuenta = tipo_cuenta;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getId_cuenta_padre() {
        return id_cuenta_padre;
    }

    public void setId_cuenta_padre(Integer id_cuenta_padre) {
        this.id_cuenta_padre = id_cuenta_padre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
