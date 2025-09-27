package tvsystem.util;

import tvsystem.util.LoggerHelper;
import javax.swing.*;
import java.awt.Color;

/**
 * Configura aspectos del sistema como Look & Feel y configuraciones de UI.
 * Extrae responsabilidades de configuración de TvSystemApplication.
 * 
 * @author Elias Manriquez
 */
public class SystemConfigurer {
    
    /**
     * Configura el Look and Feel del sistema para una mejor apariencia
     */
    public static void configurarLookAndFeel() {
        try {
            // Usar Nimbus para mejor soporte de colores personalizados
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    LoggerHelper.success("Look & Feel Nimbus configurado correctamente");
                    return;
                }
            }
            
            // Si no está Nimbus, usar el del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LoggerHelper.success("Look & Feel del sistema configurado");
            
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            LoggerHelper.warning("No se pudo configurar Look & Feel, usando por defecto");
        }
    }
    
    /**
     * Configura propiedades específicas de Swing
     */
    public static void configurarSwing() {
        // Configurar anti-aliasing para texto más nítido
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Configurar comportamiento de tooltip
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        
        // Permitir que los botones muestren colores personalizados
        UIManager.put("Button.focus", new Color(0, 0, 0, 0)); // Transparent focus
        UIManager.put("Button.select", new Color(0, 0, 0, 50)); // Semi-transparent selection
        
        LoggerHelper.debug("Propiedades de Swing configuradas");
    }
    
    /**
     * Configura un botón con colores personalizados asegurándo que se muestren
     */
    public static void configurarBotonConColor(JButton boton, Color backgroundColor, Color foregroundColor) {
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBackground(backgroundColor);
        boton.setForeground(foregroundColor);
        
        // Para que funcione con diferentes Look & Feel
        boton.putClientProperty("Nimbus.Overrides", new javax.swing.UIDefaults());
        boton.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
    }
    
    /**
     * Configura todas las opciones del sistema
     */
    public static void configurarSistema() {
        configurarLookAndFeel();
        configurarSwing();
        LoggerHelper.init("Configuración del sistema completada");
    }
}