package tvsystem.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Helper para manejo de dialogos de archivos.
 * 
 * @author Elias Manriquez
 */
public class FileDialogHelper {
    
    // Muestra dialogo para seleccionar archivo CSV existente o crear uno nuevo
    public static String seleccionarArchivoCsv() {
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
        
        if (opcion == 2) return null;
        
        if (opcion == 0) {
            return mostrarDialogoAbrirArchivo(fileChooser);
        } else {
            return mostrarDialogoGuardarArchivo(fileChooser);
        }
    }
    
    // Muestra dialogo para seleccionar ubicación donde guardar reporte TXT
    public static String seleccionarUbicacionReporte(String nombreDefault) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Análisis");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos TXT (*.txt)", "txt"));
        fileChooser.setSelectedFile(new File(nombreDefault));
        
        int resultado = fileChooser.showSaveDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            if (!rutaArchivo.endsWith(".txt")) {
                rutaArchivo += ".txt";
            }
            return rutaArchivo;
        }
        
        return null;
    }
    
    // Muestra mensaje informativo
    public static void mostrarInformacion(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(
            null, 
            mensaje, 
            titulo, 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Muestra mensaje de error
    public static void mostrarError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(
            null, 
            mensaje, 
            titulo, 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    // --- METODOS PRIVADOS ---
    
    private static String mostrarDialogoAbrirArchivo(JFileChooser fileChooser) {
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        int resultado = fileChooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
    
    private static String mostrarDialogoGuardarArchivo(JFileChooser fileChooser) {
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File("datos_tv_system.csv"));
        int resultado = fileChooser.showSaveDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".csv")) {
                ruta += ".csv";
            }
            return ruta;
        }
        return null;
    }
}