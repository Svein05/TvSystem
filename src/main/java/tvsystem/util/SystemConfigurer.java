package tvsystem.util;

import tvsystem.util.LoggerHelper;
import javax.swing.*;
import java.awt.Color;

/**
 * Configura aspectos del sistema como Look & Feel y configuraciones de UI.
 * 
 * @author Elias Manriquez
 */
public class SystemConfigurer {
    
    // Configura el Look and Feel del sistema para una mejor apariencia
    public static void configurarLookAndFeel() {
        try {
            // Se usa NIMBUS
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    LoggerHelper.success("Look & Feel Nimbus configurado correctamente");
                    return;
                }
            }
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LoggerHelper.success("Look & Feel del sistema configurado");
            
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            LoggerHelper.warning("No se pudo configurar Look & Feel, usando por defecto");
        }
    }
    
    // Configura propiedades especificas de Swing
    public static void configurarSwing() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.select", new Color(0, 0, 0, 50));
        
        LoggerHelper.debug("Propiedades de Swing configuradas");
    }
    
    // Configura un boton con colores
    public static void configurarBotonConColor(JButton boton, Color backgroundColor, Color foregroundColor) {
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBackground(backgroundColor);
        boton.setForeground(foregroundColor);
        
        boton.putClientProperty("Nimbus.Overrides", new javax.swing.UIDefaults());
        boton.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
    }
    
    // Configura todas las opciones del sistema
    public static void configurarSistema() {
        configurarLookAndFeel();
        configurarSwing();
        LoggerHelper.init("Configuración del sistema completada");
    }
}