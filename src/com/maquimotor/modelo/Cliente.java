package com.maquimotor.modelo;

public class Cliente {
    private int idCliente;
    private String razonSocial;
    private String documento;
    private String contacto;
    private String direccionDespacho;

    public Cliente() {}

    public Cliente(int idCliente, String razonSocial, String documento, String contacto, String direccionDespacho) {
        this.idCliente = idCliente;
        this.razonSocial = razonSocial;
        this.documento = documento;
        this.contacto = contacto;
        this.direccionDespacho = direccionDespacho;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getDireccionDespacho() { return direccionDespacho; }
    public void setDireccionDespacho(String direccionDespacho) { this.direccionDespacho = direccionDespacho; }
}