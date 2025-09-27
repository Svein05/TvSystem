package tvsystem.util;

import tvsystem.model.*;
import tvsystem.service.*;
import java.io.*;
import java.util.*;

/**
 * Gestor de archivos CSV para cargar y guardar datos del sistema.
 * 
 * @author Maximiliano Rodriguez
 * @author Elias Manriquez
 */
public class CsvManager {
    
    private static String archivoActual = null;
    
    /**
     * Solicita al usuario seleccionar archivo CSV
     * @return true si se seleccionó archivo, false si se cancelo
     */
    public static boolean seleccionarArchivo() {
        String rutaSeleccionada = FileDialogHelper.seleccionarArchivoCsv();
        if (rutaSeleccionada != null) {
            archivoActual = rutaSeleccionada;
            return true;
        }
        return false;
    }
    
    // Carga los datos desde el archivo CSV seleccionado.
    public static boolean cargarDatos(SectorService sectorService, ClienteService clienteService, PlanService planService) {
        if (archivoActual == null) return false;
        
        File archivo = new File(archivoActual);
        if (!archivo.exists()) {
            LoggerHelper.info("Archivo nuevo: " + archivoActual);
            LoggerHelper.info("Generando datos ficticios iniciales...");
            
            // Generar datos ficticios para archivo nuevo
            DatosFicticiosGenerator.generarClientesFicticios(sectorService, clienteService, planService);
            
            return true;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int clientesCargados = 0;
            Map<String, Double> descuentosPorPlan = new HashMap<>();
            
            LoggerHelper.info("Cargando datos desde: " + archivoActual);
            
            // Leer cabecera
            String cabecera = reader.readLine();
            if (cabecera == null) {
                LoggerHelper.warning("Archivo CSV vacío");
                return true;
            }
            
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                
                try {
                    String sector, nombre, rut, domicilio, codigoPlan;
                    String estadoSuscripcion = "ACTIVA"; // Estado por defecto
                    boolean pagado = true; // Por defecto pagado
                    java.time.LocalDate proximoVencimiento = null;
                    double descuentoPlan = 0.0;
                    boolean tieneDescuento = false;
                    
                    sector = datos[0];
                    nombre = datos[1];
                    rut = datos[2];
                    domicilio = datos[3];
                    codigoPlan = datos[4];
                        
                    // Leer estado de suscripcion si existe 
                    if (datos.length >= 8) {
                        estadoSuscripcion = datos[7].trim().isEmpty() ? "ACTIVA" : datos[7];
                    }
                        
                    if (datos.length >= 11) {
                        try {
                            descuentoPlan = Double.parseDouble(datos[9]); // Campo DESCUENTO
                            tieneDescuento = descuentoPlan > 0.0;
                                
                            // Almacenar descuento por plan para aplicarlo una sola vez
                            if (tieneDescuento && !descuentosPorPlan.containsKey(codigoPlan)) {
                                descuentosPorPlan.put(codigoPlan, descuentoPlan);
                            }
                        } catch (NumberFormatException e) {
                            descuentoPlan = 0.0;
                        }
                    }
                    
                    // Leer campos adicionales si existen (formato extendido con 13 campos)
                    if (datos.length >= 13) {
                        // Campo PAGADO (posicion 11)
                        String pagadoStr = datos[11].trim();
                        pagado = "true".equalsIgnoreCase(pagadoStr);
                        
                        // Campo PROXIMO_VENCIMIENTO (posicion 12)
                        String vencimientoStr = datos[12].trim();
                        if (!vencimientoStr.isEmpty()) {
                            try {
                                proximoVencimiento = java.time.LocalDate.parse(vencimientoStr);
                            } catch (Exception e) {
                                // Si falla el parsing, usar valor por defecto
                                proximoVencimiento = null;
                            }
                        }
                    }
                    
                    // Usar la sobrecarga para crear cliente con estado personalizado
                    boolean exitoso;
                    if (proximoVencimiento != null) {
                        // Usar sobrecarga con estado personalizado
                        exitoso = clienteService.agregarCliente(sector, nombre, rut, domicilio, codigoPlan,
                                                              estadoSuscripcion, proximoVencimiento, pagado);
                    } else {
                        // Usar metodo original
                        exitoso = clienteService.agregarCliente(sector, nombre, rut, domicilio, codigoPlan);
                        // Si se creo exitoso, actualizar el estado manualmente
                        if (exitoso) {
                            Cliente clienteAgregado = clienteService.obtenerClientePorRut(rut);
                            if (clienteAgregado != null && clienteAgregado.getSuscripcion() != null) {
                                clienteAgregado.getSuscripcion().setEstado(estadoSuscripcion);
                                clienteAgregado.getSuscripcion().setPagado(pagado);
                            }
                        }
                    }
                    
                    if (exitoso) {
                        clientesCargados++;
                        LoggerHelper.debug("Cliente cargado: " + nombre + " - Estado: " + estadoSuscripcion + 
                                         " - Pagado: " + pagado + " - Vencimiento: " + proximoVencimiento);
                    }
                } catch (Exception e) {
                    System.err.println("Error al cargar cliente: " + linea);
                }
            }
            
            // Aplicar descuentos a los planes despues de cargar todos los clientes
            for (Map.Entry<String, Double> entry : descuentosPorPlan.entrySet()) {
                String codigoPlan = entry.getKey();
                Double descuento = entry.getValue();
                
                PlanSector plan = planService.obtenerPlanPorCodigo(codigoPlan);
                if (plan != null) {
                    plan.activarOferta(descuento);
                    LoggerHelper.success("Descuento " + (descuento * 100) + "% aplicado al plan: " + codigoPlan);
                }
            }
            
            LoggerHelper.success("Datos cargados: " + clientesCargados + " clientes");
            if (!descuentosPorPlan.isEmpty()) {
                LoggerHelper.info("Ofertas restauradas: " + descuentosPorPlan.size() + " planes con descuentos");
            }
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al leer archivo CSV: " + e.getMessage());
            return false;
        }
    }
    
    // Guarda todos los datos actuales en el archivo CSV
    public static boolean guardarDatos(SectorService sectorService, ClienteService clienteService, PlanService planService) {
        if (archivoActual == null) {
            System.err.println("No hay archivo seleccionado");
            return false;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoActual))) {
            writer.println("SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL,PAGADO,PROXIMO_VENCIMIENTO");
            
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            for (Cliente cliente : clientes) {
                if (cliente.getSuscripcion() != null) {
                    Suscripcion suscripcion = cliente.getSuscripcion();
                    PlanSector plan = suscripcion.getPlan();
                    
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%d,%.2f,%d,%s,%s%n",
                        escapar(plan.getSectorAsociado()),
                        escapar(cliente.getNombre()),
                        escapar(cliente.getRut()),
                        escapar(cliente.getDomicilio()),
                        escapar(plan.getCodigoPlan()),
                        suscripcion.getFechaInicio(),
                        suscripcion.getFechaTermino() != null ? suscripcion.getFechaTermino() : "",
                        escapar(suscripcion.getEstado() != null ? suscripcion.getEstado() : "ACTIVA"),
                        plan.getPrecioMensual(),
                        plan.getDescuento(),
                        plan.calcularPrecioFinal(),
                        suscripcion.isPagado() ? "true" : "false",
                        suscripcion.getProximoVencimiento() != null ? suscripcion.getProximoVencimiento() : ""
                    );
                }
            }
            
            LoggerHelper.success("Guardado exitoso: " + clientes.size() + " clientes en " + archivoActual);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
            return false;
        }
    }
    
    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto.contains(",") ? "\"" + texto + "\"" : texto;
    }
    
    public static String getArchivoActual() {
        return archivoActual;
    }
    
    public static boolean tieneArchivoSeleccionado() {
        return archivoActual != null;
    }
}