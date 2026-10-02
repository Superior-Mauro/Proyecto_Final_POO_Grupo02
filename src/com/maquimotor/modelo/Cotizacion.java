package com.maquimotor.modelo;

public class Cotizacion {
    private int idCotizacion;
    private int idCliente;
    private double subtotal;
    private double igv;
    private double total;

    // Constructores
    public Cotizacion() {
    }

    public Cotizacion(int idCotizacion, int idCliente, double subtotal, double igv, double total) {
        this.idCotizacion = idCotizacion;
        this.idCliente = idCliente;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
    }

    // Getters y Setters
    public int getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(int idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}