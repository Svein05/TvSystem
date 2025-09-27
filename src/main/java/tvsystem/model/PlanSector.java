package tvsystem.model;

/**
 * Plan de television específico para un sector.
 * Cada sector puede tener planes con precios y ofertas especificas.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class PlanSector {
    private String nombrePlan;
    private long precioMensual;
    private boolean ofertaActiva;
    private double descuento;
    private String codigoPlan;
    private String sectorAsociado;

    // Constructor
    public PlanSector(String codigoPlan, String nombrePlan, long precioMensual, boolean ofertaActiva, double descuento, String sectorAsociado) {
        this.codigoPlan = codigoPlan;
        this.nombrePlan = nombrePlan;
        this.precioMensual = precioMensual;
        this.ofertaActiva = ofertaActiva;
        this.descuento = descuento;
        this.sectorAsociado = sectorAsociado;
    }

    // -- SETTERS Y GETTERS

    public String getCodigoPlan() {
        return codigoPlan;
    }

    public void setCodigoPlan(String codigoPlan) {
        this.codigoPlan = codigoPlan;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public long getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(long precioMensual) {
        this.precioMensual = precioMensual;
    }

    public boolean getOfertaActiva() {
        return ofertaActiva;
    }

    public void setOfertaActiva(boolean ofertaActiva) {
        this.ofertaActiva = ofertaActiva;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getSectorAsociado() {
        return sectorAsociado;
    }

    public void setSectorAsociado(String sectorAsociado) {
        this.sectorAsociado = sectorAsociado;
    }

    // -- METODOS --
    
    public long calcularPrecioFinal(){
        if (ofertaActiva && descuento > 0) {
            return (long) (precioMensual * (1 - descuento));
        }
        return precioMensual;
    }
    
    public void activarOferta(double nuevoDescuento) {
        this.descuento = nuevoDescuento;
        this.ofertaActiva = true;
    }
    
    public void desactivarOferta() {
        this.ofertaActiva = false;
        this.descuento = 0.0;
    }
}