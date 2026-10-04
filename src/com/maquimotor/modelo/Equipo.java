package com.maquimotor.modelo;

public class Equipo {
    private int idEquipo;
    private String codigo;
    private String numeroSerie;
    private String marca;
    private String modelo;
    private String aplicacion; // Minería o Agricultura
    private double precioBase;
    private int stockDisponible;
    private int stockMinimo;
    private String estado; // 'Disponible', 'Reservado', 'Mantenimiento'
    private FichaTecnica fichaTecnica;

    public Equipo() {}

    public Equipo(int idEquipo, String codigo, String numeroSerie, String marca, String modelo, 
                  String aplicacion, double precioBase, int stockDisponible, int stockMinimo, 
                  String estado, FichaTecnica fichaTecnica) {
        this.idEquipo = idEquipo;
        this.codigo = codigo;
        this.numeroSerie = numeroSerie;
        this.marca = marca;
        this.modelo = modelo;
        this.aplicacion = aplicacion;
        this.precioBase = precioBase;
        this.stockDisponible = stockDisponible;
        this.stockMinimo = stockMinimo;
        this.estado = estado;
        this.fichaTecnica = fichaTecnica;
    }

    public boolean esStockCritico() {
        return this.stockDisponible <= this.stockMinimo;
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

    public String getAplicacion() { return aplicacion; }
    public void setAplicacion(String aplicacion) { this.aplicacion = aplicacion; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public int getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public FichaTecnica getFichaTecnica() { return fichaTecnica; }
    public void setFichaTecnica(FichaTecnica fichaTecnica) { this.fichaTecnica = fichaTecnica; }

    @Override
    public String toString() {
        return codigo + " - " + marca + " " + modelo + " (Disp: " + stockDisponible + ")";
    }
}