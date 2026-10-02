package com.maquimotor.modelo;

public class Equipo {
    private int idEquipo;
    private String codigo;
    private String numeroSerie;
    private String marca;
    private String modelo;
    private int stock;
    private double precioUnitario;

    public Equipo() {}

    public Equipo(int idEquipo, String codigo, String numeroSerie, String marca, String modelo, int stock, double precioUnitario) {
        this.idEquipo = idEquipo;
        this.codigo = codigo;
        this.numeroSerie = numeroSerie;
        this.marca = marca;
        this.modelo = modelo;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
    }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}