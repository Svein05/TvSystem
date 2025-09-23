package tvsystem.model;

import java.util.Date;

/**
 * Representa un pago realizado por un cliente.
 * Permite gestionar el estado de pagos.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class Pago {
    private String id;
    private Cliente cliente;
    private Suscripcion suscripcion;
    private Date fechaPago;
    private Date fechaVencimiento;
    private long monto;
    private String estado; // PENDIENTE, PAGADO, VENCIDO
    private String metodoPago;
    
    public Pago(String id, Cliente cliente, Suscripcion suscripcion, Date fechaVencimiento, long monto) {
        this.id = id;
        this.cliente = cliente;
        this.suscripcion = suscripcion;
        this.fechaVencimiento = fechaVencimiento;
        this.monto = monto;
        this.estado = "PENDIENTE";
        this.fechaPago = null;
        this.metodoPago = "";
    }

    // -- SETTERS Y GETTERS --

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public long getMonto() {
        return monto;
    }

    public void setMonto(long monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    // -- METODOS --
    
    public void marcarComoPagado(Date fechaPago, String metodoPago) {
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.estado = "PAGADO";
    }
    
    public boolean estaVencido() {
        Date hoy = new Date();
        return hoy.after(fechaVencimiento) && !"PAGADO".equals(estado);
    }
    
    public void marcarComoVencido() {
        if (estaVencido()) {
            this.estado = "VENCIDO";
        }
    }
    
    public long getDiasVencimiento() {
        if (!estaVencido()) return 0;
        
        Date hoy = new Date();
        long diferencia = hoy.getTime() - fechaVencimiento.getTime();
        return diferencia / (24 * 60 * 60 * 1000); // Convertir a días
    }
}