package tvsystem.util;

import tvsystem.model.*;
import tvsystem.service.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;

/**
 * Gestor de archivos CSV para cargar y guardar datos del sistema.
 * Guarda información completa del cliente incluyendo estado de suscripción y precios.
 * 
 * @author Elias Manriquez
 */
public class CsvManager {
    
    private static String archivoActual = null;
    
    /**
     * Muestra un diálogo para seleccionar un archivo CSV existente o crear uno nuevo.
     */
    public static boolean seleccionarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de datos del sistema");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        
        String[] opciones = {"Abrir archivo existente", "Crear archivo nuevo", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(
            null,
            "¿Desea abrir un archivo existente o crear uno nuevo?",
            "Gestión de Datos",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        if (opcion == 2) return false; // Cancelar
        
        if (opcion == 0) { // Abrir existente
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            int resultado = fileChooser.showOpenDialog(null);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoActual = fileChooser.getSelectedFile().getAbsolutePath();
                return true;
            }
        } else { // Crear nuevo
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setSelectedFile(new File("datos_tv_system.csv"));
            int resultado = fileChooser.showSaveDialog(null);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                String ruta = fileChooser.getSelectedFile().getAbsolutePath();
                if (!ruta.endsWith(".csv")) {
                    ruta += ".csv";
                }
                archivoActual = ruta;
                return true;
            }
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
            System.out.println("📁 Archivo nuevo: " + archivoActual);
            System.out.println("🎯 Generando datos ficticios iniciales...");
            
            // Generar datos ficticios para archivo nuevo
            DatosFicticiosGenerator.generarClientesFicticios(sectorService, clienteService, planService);
            
            return true;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int clientesCargados = 0;
            
            System.out.println("📂 Cargando datos desde: " + archivoActual);
            
            // Leer cabecera
            String cabecera = reader.readLine();
            if (cabecera == null) {
                System.out.println("⚠ Archivo CSV vacío");
                return true;
            }
            
            // Determinar formato (nuevo o antiguo)
            boolean formatoAntiguo = cabecera.startsWith("TIPO,");
            boolean formatoNuevo = cabecera.startsWith("SECTOR,");
            
            if (!formatoAntiguo && !formatoNuevo) {
                System.out.println("⚠ Formato de CSV no reconocido");
                return true;
            }
            
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                
                try {
                    String sector, nombre, rut, domicilio, codigoPlan;
                    
                    if (formatoAntiguo && datos.length >= 6 && "CLIENTE".equals(datos[0])) {
                        // Formato antiguo: TIPO,SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,...
                        sector = datos[1];
                        nombre = datos[2];
                        rut = datos[3];
                        domicilio = datos[4];
                        codigoPlan = datos[5];
                    } else if (formatoNuevo && datos.length >= 5) {
                        // Formato nuevo: SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,...
                        sector = datos[0];
                        nombre = datos[1];
                        rut = datos[2];
                        domicilio = datos[3];
                        codigoPlan = datos[4];
                    } else {
                        continue; // Saltar líneas que no coinciden con ningún formato
                    }
                    
                    boolean exitoso = clienteService.agregarCliente(sector, nombre, rut, domicilio, codigoPlan);
                    if (exitoso) {
                        clientesCargados++;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error al cargar cliente: " + linea);
                }
            }
            
            System.out.println("✅ Datos cargados: " + clientesCargados + " clientes");
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
            
            System.out.println("💾 Guardado exitoso: " + clientes.size() + " clientes en " + archivoActual);
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