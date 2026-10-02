package com.maquimotor.modelo;

public class DetalleCotizacion {
    private int idDetalle;
    private int idCotizacion;
    private Equipo equipo;
    private int cantidad;
    private double precioUnitario;
    private double subtotalItem;

    public DetalleCotizacion() {}

    public DetalleCotizacion(int idDetalle, int idCotizacion, Equipo equipo, int cantidad, double precioUnitario, double subtotalItem) {
        this.idDetalle = idDetalle;
        this.idCotizacion = idCotizacion;
        this.equipo = equipo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotalItem = subtotalItem;
    }

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdCotizacion() { return idCotizacion; }
    public void setIdCotizacion(int idCotizacion) { this.idCotizacion = idCotizacion; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotalItem() { return subtotalItem; }
    public void setSubtotalItem(double subtotalItem) { this.subtotalItem = subtotalItem; }
}