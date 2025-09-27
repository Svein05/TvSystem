package tvsystem.util;

import tvsystem.service.*;
import tvsystem.model.*;
import tvsystem.exception.ClienteInvalidoException;
import tvsystem.exception.SectorNoEncontradoException;
import java.util.*;

/**
 * Generador de clientes ficticios en el CSV
 */
public class DatosFicticiosGenerator {
    
    private static final String[] NOMBRES = {
        "María González", "Juan Pérez", "Ana López", "Carlos Rodríguez", "Laura Martínez",
        "Pedro Sánchez", "Carmen García", "Miguel Torres", "Isabel Ruiz", "Francisco Moreno",
        "Rosa Jiménez", "Antonio Fernández", "Lucía Díaz", "Manuel Álvarez", "Elena Romero",
        "José Luis Castro", "Patricia Vargas", "Roberto Silva", "Cristina Mendoza", "Daniel Herrera"
    };
    
    private static final String[] DIRECCIONES = {
        "Calle Mayor 123", "Avenida Central 456", "Plaza del Sol 789", "Calle de la Paz 321",
        "Paseo de los Olmos 654", "Calle Nueva 987", "Avenida Libertad 147", "Calle Real 258",
        "Plaza de Armas 369", "Calle del Carmen 741", "Avenida España 852", "Calle Victoria 963"
    };
    
    private static Random random = new Random();
    
    // Genera clientes ficticios y los agrega al CSV
    public static void generarClientesFicticios(SectorService sectorService, ClienteService clienteService, PlanService planService) {
        System.out.println("=== GENERANDO CLIENTES FICTICIOS ===");
        
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        List<PlanSector> planes = planService.obtenerTodosLosPlanes();
        
        if (sectores.isEmpty()) {
            System.out.println("No hay sectores disponibles.");
            return;
        }
        
        if (planes.isEmpty()) {
            System.out.println("No hay planes disponibles.");
            return;
        }
        
        // Generar 20 clientes ficticios
        for (int i = 0; i < 20; i++) {
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String rut = generarRutFicticio();
            String direccion = DIRECCIONES[random.nextInt(DIRECCIONES.length)];
            
            Sector sectorSeleccionado = sectores.get(random.nextInt(sectores.size()));
            
            PlanSector planSeleccionado = planes.get(random.nextInt(planes.size()));
            
            // Determinar configuracion de suscripcion segun el indice
            String estadoSuscripcion = "ACTIVA";
            java.time.LocalDate proximoVencimiento = java.time.LocalDate.now().plusMonths(1);
            boolean pagado = true;
            String descripcionEstado = "ACTIVO";
            
            if (i % 10 < 3) {
                estadoSuscripcion = "PROXIMA_A_VENCER";
                proximoVencimiento = java.time.LocalDate.now().plusDays(10);
                pagado = false;
                descripcionEstado = "PROXIMO A VENCER";
            } else if (i % 10 < 5) {
                estadoSuscripcion = "SUSPENDIDA";
                proximoVencimiento = java.time.LocalDate.now().minusDays(5);
                pagado = false;
                descripcionEstado = "SUSPENDIDO";
            }
            
            try {
                boolean clienteAgregado = clienteService.agregarCliente(
                    sectorSeleccionado.getNombre(), 
                    nombre, 
                    rut, 
                    direccion, 
                    planSeleccionado.getCodigoPlan(),
                    estadoSuscripcion,
                    proximoVencimiento,
                    pagado
                );
                
                if (clienteAgregado) {
                    System.out.println("Cliente " + nombre + " agregado a " + sectorSeleccionado.getNombre() + 
                                     " con plan " + planSeleccionado.getNombrePlan() + 
                                     " - Estado: " + descripcionEstado);
                }
            } catch (ClienteInvalidoException ex) {
                System.err.println("Error al crear cliente ficticio " + nombre + ": " + ex.getMessage());
            } catch (SectorNoEncontradoException ex) {
                System.err.println("Error de sector al crear cliente ficticio " + nombre + ": " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error inesperado al crear cliente ficticio " + nombre + ": " + ex.getMessage());
            }
        }
        
        System.out.println("=== GENERACIÓN COMPLETA ===");
    }
    
    // Genera un RUT ficticio valido
    private static String generarRutFicticio() {
        int numeroBase = 10000000 + random.nextInt(90000000);
        
        int suma = 0;
        int temp = numeroBase;
        int[] multiplicadores = {2, 3, 4, 5, 6, 7};
        int pos = 0;
        
        while (temp > 0) {
            suma += (temp % 10) * multiplicadores[pos % 6];
            temp /= 10;
            pos++;
        }
        
        int resto = suma % 11;
        String dv;
        if (resto == 0) {
            dv = "0";
        } else if (resto == 1) {
            dv = "K";
        } else {
            dv = String.valueOf(11 - resto);
        }
        
        return String.format("%d-%s", numeroBase, dv);
    }
    
    private static Cliente buscarClientePorRut(String rut, SectorService sectorService) {
        List<Sector> sectores = sectorService.obtenerTodosLosSectores();
        for (Sector sector : sectores) {
            List<Cliente> clientes = sector.getClientes();
            for (Cliente cliente : clientes) {
                if (rut.equals(cliente.getRut())) {
                    return cliente;
                }
            }
        }
        return null;
    }
}