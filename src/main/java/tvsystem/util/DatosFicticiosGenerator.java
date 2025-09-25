package tvsystem.util;

import tvsystem.service.*;
import tvsystem.model.*;
import java.util.*;

/**
 * Utilidad para generar clientes ficticios en el CSV
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
    
    /**
     * Genera clientes ficticios y los agrega al CSV
     */
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
        
        // Generar 20 clientes ficticios con diferentes estados
        for (int i = 0; i < 20; i++) {
            // Datos del cliente
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String rut = generarRutFicticio();
            String direccion = DIRECCIONES[random.nextInt(DIRECCIONES.length)];
            
            // Sector aleatorio
            Sector sectorSeleccionado = sectores.get(random.nextInt(sectores.size()));
            
            // Plan aleatorio
            PlanSector planSeleccionado = planes.get(random.nextInt(planes.size()));
            
            // Agregar cliente al sector usando el servicio
            boolean clienteAgregado = clienteService.agregarCliente(
                sectorSeleccionado.getNombre(), 
                nombre, 
                rut, 
                direccion, 
                planSeleccionado.getCodigoPlan()
            );
            
            if (clienteAgregado) {
                // Para algunos clientes, modificar su estado de pago para crear variedad
                Cliente clienteCreado = buscarClientePorRut(rut, sectorService);
                if (clienteCreado != null && clienteCreado.getSuscripcion() != null) {
                    
                    // 30% de clientes próximos a vencer (no han pagado y quedan menos de 2 semanas)
                    if (i % 10 < 3) {
                        // Simular que la suscripción vence en 10 días y no ha pagado
                        java.time.LocalDate vencimientoProximo = java.time.LocalDate.now().plusDays(10);
                        clienteCreado.getSuscripcion().setProximoVencimiento(vencimientoProximo);
                        clienteCreado.getSuscripcion().setPagado(false);
                        System.out.println("Cliente " + nombre + " configurado como PRÓXIMO A VENCER");
                    }
                    // 20% de clientes suspendidos (vencidos)
                    else if (i % 10 < 5) {
                        // Simular que la suscripción ya venció
                        java.time.LocalDate vencimientoAnterior = java.time.LocalDate.now().minusDays(5);
                        clienteCreado.getSuscripcion().setProximoVencimiento(vencimientoAnterior);
                        clienteCreado.getSuscripcion().setPagado(false);
                        System.out.println("Cliente " + nombre + " configurado como SUSPENDIDO");
                    }
                    // El resto (50%) permanecen activos con pagos al día
                    else {
                        clienteCreado.getSuscripcion().setPagado(true);
                        clienteCreado.getSuscripcion().setUltimaFechaPago(java.time.LocalDate.now().minusDays(random.nextInt(20)));
                        System.out.println("Cliente " + nombre + " configurado como ACTIVO");
                    }
                }
                
                System.out.println("Cliente " + nombre + " agregado a " + sectorSeleccionado.getNombre() + " con plan " + planSeleccionado.getNombrePlan());
            }
        }
        
        System.out.println("=== GENERACIÓN COMPLETA ===");
    }
    
    /**
     * Genera un RUT ficticio válido
     */
    private static String generarRutFicticio() {
        // Generar un número base de 7-8 dígitos
        int numeroBase = 10000000 + random.nextInt(90000000);
        
        // Calcular dígito verificador simple
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
    
    /**
     * Busca un cliente por RUT en todos los sectores
     */
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