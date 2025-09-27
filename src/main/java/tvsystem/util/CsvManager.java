package tvsystem.util;

import tvsystem.model.*;
import tvsystem.service.*;
import java.io.*;
import java.util.*;

/**
 * Gestor de archivos CSV para cargar y guardar datos del sistema.
 * Refactorizado para separar lógica I/O de UI.
 * 
 * @author Elias Manriquez
 */
public class CsvManager {
    
    private static String archivoActual = null;
    
    /**
     * Solicita al usuario seleccionar archivo CSV
     * @return true si se seleccionó archivo, false si se canceló
     */
    public static boolean seleccionarArchivo() {
        String rutaSeleccionada = FileDialogHelper.seleccionarArchivoCsv();
        if (rutaSeleccionada != null) {
            archivoActual = rutaSeleccionada;
            return true;
        }
        return false;
    }
    
    /**
     * Carga los datos desde el archivo CSV seleccionado.
     */
    public static boolean cargarDatos(SectorService sectorService, ClienteService clienteService, PlanService planService) {
        if (archivoActual == null) return false;
        
        File archivo = new File(archivoActual);
        if (!archivo.exists()) {
            LoggerHelper.info("📁 Archivo nuevo: " + archivoActual);
            LoggerHelper.info("🎯 Generando datos ficticios iniciales...");
            
            // Generar datos ficticios para archivo nuevo
            DatosFicticiosGenerator.generarClientesFicticios(sectorService, clienteService, planService);
            
            return true;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int clientesCargados = 0;
            Map<String, Double> descuentosPorPlan = new HashMap<>(); // Para evitar aplicar descuentos múltiples veces
            
            LoggerHelper.info("📂 Cargando datos desde: " + archivoActual);
            
            // Leer cabecera
            String cabecera = reader.readLine();
            if (cabecera == null) {
                LoggerHelper.warning("⚠ Archivo CSV vacío");
                return true;
            }
            
            // Determinar formato (nuevo o antiguo)
            boolean formatoAntiguo = cabecera.startsWith("TIPO,");
            boolean formatoNuevo = cabecera.startsWith("SECTOR,");
            
            if (!formatoAntiguo && !formatoNuevo) {
                LoggerHelper.warning("⚠ Formato de CSV no reconocido");
                return true;
            }
            
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                
                try {
                    String sector, nombre, rut, domicilio, codigoPlan;
                    String estadoSuscripcion = "ACTIVA"; // Estado por defecto
                    double descuentoPlan = 0.0;
                    boolean tieneDescuento = false;
                    
                    if (formatoAntiguo && datos.length >= 6 && "CLIENTE".equals(datos[0])) {
                        // Formato antiguo: TIPO,SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,...
                        sector = datos[1];
                        nombre = datos[2];
                        rut = datos[3];
                        domicilio = datos[4];
                        codigoPlan = datos[5];
                    } else if (formatoNuevo && datos.length >= 5) {
                        // Formato nuevo: SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL
                        sector = datos[0];
                        nombre = datos[1];
                        rut = datos[2];
                        domicilio = datos[3];
                        codigoPlan = datos[4];
                        
                        // Leer estado de suscripción si existe (posición 7)
                        if (datos.length >= 8) {
                            estadoSuscripcion = datos[7].trim().isEmpty() ? "ACTIVA" : datos[7];
                        }
                        
                        // Si tiene formato completo con descuento (11 campos), extraer el descuento
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
                    } else {
                        continue; // Saltar líneas que no coinciden con ningún formato
                    }
                    
                    boolean exitoso = clienteService.agregarCliente(sector, nombre, rut, domicilio, codigoPlan);
                    if (exitoso) {
                        // Después de agregar el cliente, establecer el estado de suscripción
                        Cliente clienteAgregado = clienteService.obtenerClientePorRut(rut);
                        if (clienteAgregado != null && clienteAgregado.getSuscripcion() != null) {
                            clienteAgregado.getSuscripcion().setEstado(estadoSuscripcion);
                            LoggerHelper.debug("Estado restaurado para " + nombre + ": " + estadoSuscripcion);
                        }
                        clientesCargados++;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error al cargar cliente: " + linea);
                }
            }
            
            // Aplicar descuentos a los planes después de cargar todos los clientes
            for (Map.Entry<String, Double> entry : descuentosPorPlan.entrySet()) {
                String codigoPlan = entry.getKey();
                Double descuento = entry.getValue();
                
                PlanSector plan = planService.obtenerPlanPorCodigo(codigoPlan);
                if (plan != null) {
                    plan.activarOferta(descuento);
                    LoggerHelper.success("✅ Descuento " + (descuento * 100) + "% aplicado al plan: " + codigoPlan);
                }
            }
            
            LoggerHelper.success("✅ Datos cargados: " + clientesCargados + " clientes");
            if (!descuentosPorPlan.isEmpty()) {
                LoggerHelper.info("🎯 Ofertas restauradas: " + descuentosPorPlan.size() + " planes con descuentos");
            }
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Guarda todos los datos actuales en el archivo CSV con información completa.
     */
    public static boolean guardarDatos(SectorService sectorService, ClienteService clienteService, PlanService planService) {
        if (archivoActual == null) {
            System.err.println("❌ No hay archivo seleccionado");
            return false;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoActual))) {
            
            // Cabecera optimizada - eliminando campos redundantes
            writer.println("SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL");
            
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            for (Cliente cliente : clientes) {
                if (cliente.getSuscripcion() != null) {
                    Suscripcion suscripcion = cliente.getSuscripcion();
                    PlanSector plan = suscripcion.getPlan();
                    
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%d,%.2f,%d%n",
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
                        plan.calcularPrecioFinal()
                    );
                }
            }
            
            LoggerHelper.success("💾 Guardado exitoso: " + clientes.size() + " clientes en " + archivoActual);
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar: " + e.getMessage());
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