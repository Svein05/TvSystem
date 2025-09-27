package tvsystem.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper centralizado para logging del sistema.
 * Reemplaza System.out.println dispersos con logging configurable.
 * 
 * @author Elias Manriquez
 */
public class LoggerHelper {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static boolean debugEnabled = true;
    private static boolean infoEnabled = true;
    
    public enum LogLevel {
        DEBUG("🔧"), INFO("ℹ️"), SUCCESS("✅"), WARNING("⚠️"), ERROR("❌");
        
        private final String emoji;
        
        LogLevel(String emoji) {
            this.emoji = emoji;
        }
        
        public String getEmoji() {
            return emoji;
        }
    }
    
    /**
     * Log de información general
     */
    public static void info(String mensaje) {
        if (infoEnabled) {
            log(LogLevel.INFO, mensaje);
        }
    }
    
    /**
     * Log de depuración
     */
    public static void debug(String mensaje) {
        if (debugEnabled) {
            log(LogLevel.DEBUG, mensaje);
        }
    }
    
    /**
     * Log de éxito
     */
    public static void success(String mensaje) {
        log(LogLevel.SUCCESS, mensaje);
    }
    
    /**
     * Log de advertencia
     */
    public static void warning(String mensaje) {
        log(LogLevel.WARNING, mensaje);
    }
    
    /**
     * Log de error
     */
    public static void error(String mensaje) {
        log(LogLevel.ERROR, mensaje);
    }
    
    /**
     * Log con excepción
     */
    public static void error(String mensaje, Exception e) {
        log(LogLevel.ERROR, mensaje + ": " + e.getMessage());
    }
    
    /**
     * Log de operaciones del sistema (siempre visible)
     */
    public static void system(String mensaje) {
        System.out.println(formatMessage("🖥️", mensaje));
    }
    
    /**
     * Log de inicialización del sistema
     */
    public static void init(String mensaje) {
        System.out.println(formatMessage("🚀", mensaje));
    }
    
    /**
     * Log de datos/estadísticas
     */
    public static void data(String mensaje) {
        if (infoEnabled) {
            log(LogLevel.INFO, "📊 " + mensaje);
        }
    }
    
    /**
     * Habilita/deshabilita logs de debug
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
    
    /**
     * Habilita/deshabilita logs de info
     */
    public static void setInfoEnabled(boolean enabled) {
        infoEnabled = enabled;
    }
    
    /**
     * Log condicional (solo si la condición es verdadera)
     */
    public static void debugIf(boolean condition, String mensaje) {
        if (condition && debugEnabled) {
            log(LogLevel.DEBUG, mensaje);
        }
    }
    
    // --- MÉTODOS PRIVADOS ---
    
    private static void log(LogLevel level, String mensaje) {
        System.out.println(formatMessage(level.getEmoji(), mensaje));
    }
    
    private static String formatMessage(String emoji, String mensaje) {
        return String.format("[%s] %s %s", 
            LocalDateTime.now().format(TIMESTAMP_FORMAT), 
            emoji, 
            mensaje);
    }
    
    // --- MÉTODOS DE CONVENIENCIA PARA OPERACIONES COMUNES ---
    
    /**
     * Log de archivo cargado
     */
    public static void fileLoaded(String fileName, int itemCount) {
        success(String.format("Archivo cargado: %s (%d elementos)", fileName, itemCount));
    }
    
    /**
     * Log de archivo guardado
     */
    public static void fileSaved(String fileName, int itemCount) {
        success(String.format("Archivo guardado: %s (%d elementos)", fileName, itemCount));
    }
    
    /**
     * Log de operación completada
     */
    public static void operationCompleted(String operation) {
        success("Operación completada: " + operation);
    }
    
    /**
     * Log de UI actualizada
     */
    public static void uiUpdated(String component) {
        debug("UI actualizada: " + component);
    }
    
    /**
     * Log de servicio inicializado
     */
    public static void serviceInitialized(String serviceName) {
        init("Servicio inicializado: " + serviceName);
    }
}