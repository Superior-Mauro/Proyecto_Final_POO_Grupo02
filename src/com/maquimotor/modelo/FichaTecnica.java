package com.maquimotor.modelo;

public class FichaTecnica {
    private int idFicha;
    private double potenciaHP;
    private double cilindrada;
    private String combustible;
    private String compatibilidad;

    public FichaTecnica() {}

    public FichaTecnica(double potenciaHP, double cilindrada, String combustible, String compatibilidad) {
        this.potenciaHP = potenciaHP;
        this.cilindrada = cilindrada;
        this.combustible = combustible;
        this.compatibilidad = compatibilidad;
    }

    public int getIdFicha() { return idFicha; }
    public void setIdFicha(int idFicha) { this.idFicha = idFicha; }

    public double getPotenciaHP() { return potenciaHP; }
    public void setPotenciaHP(double potenciaHP) { this.potenciaHP = potenciaHP; }

    public double getCilindrada() { return cilindrada; }
    public void setCilindrada(double cilindrada) { this.cilindrada = cilindrada; }

    public String getCombustible() { return combustible; }
    public void setCombustible(String combustible) { this.combustible = combustible; }

    public String getCompatibilidad() { return compatibilidad; }
    public void setCompatibilidad(String compatibilidad) { this.compatibilidad = compatibilidad; }

    public String getResumen() {
        return potenciaHP + " HP | " + cilindrada + "L | " + combustible;
    }
}