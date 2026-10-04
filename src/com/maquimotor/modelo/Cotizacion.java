package com.maquimotor.modelo;

public class Cotizacion {
    private int idCotizacion;
    private int idCliente;
    private int idUsuario;
    private String estado;
    private double subtotal;
    private double igv;
    private double total;

    public Cotizacion() {
        this.estado = "Vigente";
    }

    public Cotizacion(int idCotizacion, int idCliente, int idUsuario, double subtotal, double igv, double total) {
        this.idCotizacion = idCotizacion;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.estado = "Vigente";
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
    }

    public int getIdCotizacion() { return idCotizacion; }
    public void setIdCotizacion(int idCotizacion) { this.idCotizacion = idCotizacion; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIgv() { return igv; }
    public void setIgv(double igv) { this.igv = igv; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}