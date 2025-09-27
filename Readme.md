# 📺 Sistema de Gestión de Televisión

## 📋 Descripción

TvSystem es una aplicación integral de gestión para empresas de televisión por cable que permite administrar clientes, sectores, planes de servicio y realizar captación automática de clientes. El sistema ofrece tanto una interfaz gráfica de usuario moderna como funcionalidades de importación/exportación de datos mediante archivos CSV.

### ⭐ Características Principales

- 👥 **Gestión de Clientes**: Registro, modificación y consulta de clientes con validación de RUT chileno
- 🏘️ **Administración de Sectores**: Organización territorial de clientes y planes específicos por zona en la región de Valparaíso. 
- 📋 **Gestión de Planes**: Creación y administración de planes de televisión con ofertas dinámicas
- 🎯 **Captación Automática**: Sistema inteligente de identificación de sectores débiles y activación de ofertas
- 🖥️ **Interfaz Gráfica**: Aplicación desktop con visualización de datos mediante gráficos y tablas
- 📂 **Importación/Exportación**: Manejo de datos mediante archivos CSV
- ✅ **Validación de Datos**: Sistema robusto de validación incluye RUTs chilenos

## 💻 Requisitos del Sistema

### 🛠️ Software Necesario

- ☕ **Java 11** o superior
- 🔧 **Apache Maven 3.6+** (para compilación)
- 🌟 **NetBeans IDE** (recomendado para desarrollo)
- 🖥️ Sistema operativo: Windows, macOS, o Linux

### 📦 Dependencias

El proyecto utiliza las siguientes tecnologías:
- 🖼️ Java Swing para la interfaz gráfica
- 🔗 Maven para gestión de dependencias
- 🎨 AbsoluteLayout de NetBeans para diseño de ventanas

## ⚙️ Instalación y Ejecución

### 🌟 Ejecución en NetBeans IDE

1. **📥 Descargar NetBeans IDE**: Asegúrese de tener NetBeans instalado (versión 12 o superior recomendada)
2. **📁 Abrir el proyecto**: Seleccionar "Open Project" y navegar hasta la carpeta del proyecto TvSystem
3. **🔨 Compilar**: Hacer clic derecho en el proyecto → "Clean and Build"
4. **▶️ Ejecutar**: Hacer clic derecho en el proyecto → "Run"

> 💡 **Nota**: NetBeans manejará automáticamente las dependencias de Maven y la configuración del proyecto.

## 📖 Guía de Uso

### 🚀 Inicio de la Aplicación

Al ejecutar TvSystem, el sistema:

1. ⚡ **Inicializa automáticamente** repositorios y servicios
2. 📊 **Carga datos base** del sistema (sectores y planes predefinidos)
3. 📂 **Solicita archivo CSV** para cargar datos existentes o crear uno nuevo
4. 🖥️ **Abre la interfaz gráfica** principal

### 📄 Gestión de Archivos CSV

#### 🔄 Al Iniciar
- 📋 El sistema presenta opciones para abrir un archivo existente o crear uno nuevo
- 📝 Los archivos CSV deben seguir el formato: `SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL`

#### 📊 Formato del Archivo CSV
```csv
SECTOR,NOMBRE,RUT,DOMICILIO,PLAN,FECHA_INICIO,FECHA_TERMINO,ESTADO_SUSCRIPCION,PRECIO_BASE,DESCUENTO,PRECIO_FINAL
CARTAGENA,Carmen Garci­a,30.251.795-9,Avenida Libertad 147,BASICO_CARTAGENA,Fri Sep 26 12:53:30 CEST 2025,Sun Oct 26 12:53:30 CET 2025,ACTIVA,15000,0,00,15000
VILLA_ALEMANA,Maria Gonzalez,78.314.061-6,Calle del Carmen 741,PREMIUM_VILLA_ALEMANA,Fri Sep 26 12:53:30 CEST 2025,Sun Oct 26 12:53:30 CET 2025,ACTIVA,25000,0,00,25000
```

### ⚡ Funcionalidades Principales

#### 👥 1. Gestión de Clientes

**➕ Agregar Cliente:**
- 🖱️ Haga clic en la pestaña "Gestión de Clientes" ubicada en la esquina superior izquierda
- 🖱️ Haga clic en la pestaña "Agregar Cliente"
- ✍️ Complete los campos: Nombre, RUT, Domicilio, Sector y Plan
- ✅ Hay 3 tipos de planes distintos: Básico, Familiar y Premium.
- 💾 Haga clic en "Aceptar"
- ✅ El sistema validará automáticamente el RUT chileno e indicará que ha sido guardado con éxito

**🔍 Buscar y Modificar:**
- 🔎 Use la barra de búsqueda para filtrar clientes por zonas específicas, planes o estado. También se puede ordenar clientes por RUT, nombre, zona o planes
- 👆 Seleccione un cliente de la tabla para ver/editar/eliminar
- ✏️ Modifique información en el panel de detalles
> Para modificar nombre o domicilio haga clic en editar (botón ubicado abajo en el centro). Si desea editar el estado del plan haga clic en ver detalles y luego en suscripción.  

#### 🏘️ 2. Visualización de Sectores

**📋 Panel de Sectores:**
- 🗂️ Vista organizada en cuadrícula con botones para cada sector
- 📊 Cada botón muestra: nombre del sector, número de clientes e ingresos
- 🎨 Indicadores visuales de colores para identificar sectores en gráficos
- 🖱️ Haga clic en cualquier sector para ver información detallada

**📝 Detalles del Sector:**
- 📋 Pestaña cliente con una lista completa de clientes del sector
- ✏️ También se pueden editar los clientes desde esta pestaña 
- 📈 Estadísticas de ocupación y ofertas activas

#### 🎯 3. Sistema de Captación

- 🔍 El sistema identifica sectores con menos de X clientes. Usted podrá determinarlo en la pestaña "Gestión de Sectores" ubicada en la parte de abajo, donde podrá elegir el umbral mínimo para cada sector. Luego debe presionar el botón "Filtrar" y los sectores que tengan menos del mínimo se marcarán en rojo 
- 💸 También tendrá la opción de aplicar descuento automáticamente siguiendo los siguientes criterios: 
    - Sectores muy críticos (0-33% del umbral): 30% descuento
    - Sectores críticos (34-66% del umbral): 20% descuento  
    - Sectores moderados (67-99% del umbral): 15% descuento
- 👀 Luego de aplicar el descuento se mostrará una pestaña donde podemos visualizar los descuentos aplicados según el umbral seleccionado, también podremos ver en qué categorías se encuentran los sectores (Crítico, Muy Crítico, Moderado)
- 🎯 También podrá ver el resumen general ubicado en la parte de arriba a la izquierda en la pestaña "Gestión de Sectores" donde se muestra información como en qué sector hay más clientes, menos clientes, mayores ingresos, y el promedio de clientes.

#### 📊 4. Reportes y Estadísticas

**📈 Gráficos Disponibles:**
- 🥧 Distribución de clientes por sector (gráfico circular)
- 📊 Ingresos por sector (gráfico de barras)

**📊 Datos Estadísticos:**
- 👥 Total de clientes registrados
- 🏆 Sectores más y menos populares
- 💰 Ingresos estimados por sector
- 🎯 Efectividad de ofertas activas

## 🏗️ Estructura del Proyecto

### 🏛️ Arquitectura del Sistema

El proyecto sigue una arquitectura en capas organizada de la siguiente manera:

```
src/main/java/tvsystem/
├── app/                    # Punto de entrada de la aplicación
│   └── TvSystemApplication.java
├── model/                  # Modelos de datos
│   ├── Cliente.java
│   ├── Pago.java
│   ├── PlanSector.java
│   ├── Sector.java
│   └── Suscripcion.java
├── repository/             # Capa de acceso a datos
│   ├── ClienteRepository.java
│   ├── PlanRepository.java
│   └── SectorRepository.java
├── service/                # Lógica de negocio
│   ├── CaptacionService.java
│   ├── ClienteService.java
│   ├── PlanService.java
│   └── SectorService.java
├── util/                   # Utilidades del sistema
│   ├── CsvManager.java
│   ├── DataInitializer.java
│   ├── DatosFicticiosGenerator.java
│   └── RutValidator.java
└── view/                   # Interfaces de usuario
    ├── cli/
    │   └── ConsoleInterface.java
    └── gui/
        ├── ClienteDetailDialog.java
        ├── MainWindow.java
        └── SectorDetailDialog.java
```

### 🧩 Componentes Principales

#### 📊 Modelos de Datos
- 👤 **Cliente**: Representa un cliente con información personal y suscripción
- 🏘️ **Sector**: Agrupa clientes por zona geográfica con planes específicos
- 📋 **PlanSector**: Define planes de televisión con precios y ofertas por sector
- 📝 **Suscripcion**: Gestiona el estado de suscripción de un cliente
- 💳 **Pago**: Registra información de pagos (Muestra si pagó o no)

#### ⚙️ Servicios de Negocio
- 👥 **ClienteService**: Gestión integral de clientes incluyendo validaciones
- 🏘️ **SectorService**: Administración de sectores y análisis de ocupación
- 📋 **PlanService**: Manejo de planes, precios y ofertas
- 🎯 **CaptacionService**: Lógica de captación automática y ofertas estratégicas

## � Tecnologías Utilizadas

### 🔧 Backend
- **Java 11**: Lenguaje de programación principal
- **Maven**: Gestión de dependencias y construcción del proyecto
- **Arquitectura MVC**: Separación clara entre modelo, vista y controlador

### 🎨 Frontend
- **Java Swing**: Biblioteca para interfaz gráfica de usuario
- **AbsoluteLayout**: Layout manager de NetBeans para diseño preciso
- **Graphics2D**: Para elementos gráficos avanzados y visualizaciones

### 🏗️ Arquitectura
- **Patrón Repository**: Para acceso y gestión de datos
- **Patrón Service**: Para lógica de negocio
- **Inyección de dependencias**: Manual mediante constructores
- **Almacenamiento en memoria**: Con persistencia opcional en CSV

### ✅ Validaciones Robustas
- **Validación de RUT chileno** con algoritmo de dígito verificador
- **Formato CSV flexible** con manejo de errores
- **Validación de datos de entrada** en todas las capas
- **Manejo de excepciones** informativo y detallado

### 📈 Reportes y Estadísticas
- **14 sectores geográficos** preconfigurados (Región de Valparaíso)
- **3 tipos de planes base**: Básico ($15.000), Premium ($25.000), Familiar ($35.000)
- **Gráficos interactivos** con colores personalizados
- **Exportación/importación** de datos en formato CSV

### 🎯 Algoritmo de Captación Automática
1. **Identificación**: Detecta sectores con menos de 5 clientes
2. **Activación**: Aplica descuento del 15% automáticamente
3. **Monitoreo**: Genera reportes de efectividad
4. **Escalado**: Descuentos progresivos según criticidad del sector

### 🏛️ Patrones de Diseño Implementados
- **Repository Pattern**: Abstracción de acceso a datos
- **Service Layer**: Encapsulación de lógica de negocio
- **Observer Pattern**: Para actualizaciones de interfaz
- **Strategy Pattern**: Para diferentes tipos de reportes
- **Factory Pattern**: Para creación de entidades

## 🧪 Testing y Diagnóstico

### ⚡ Verificaciones Automáticas
- **Integridad de repositorios**: Verifica inicialización correcta
- **Validación de servicios**: Comprueba dependencias
- **Estado de datos**: Confirma carga de sectores y planes
- **Memoria del sistema**: Monitorea uso de recursos

## 📊 Métricas del Sistema

### 📈 Estadísticas de Rendimiento
- **Tiempo de inicialización**: < 2 segundos promedio
- **Capacidad de clientes**: Ilimitada (limitado por memoria)
- **Sectores soportados**: Configurable (14 por defecto)
- **Planes por sector**: 3 planes base 

### 🎯 KPIs de Captación
- **Tasa de captación**: Clientes nuevos por sector/mes
- **Efectividad de ofertas**: % de conversión con descuentos
- **Penetración de mercado**: % de población cubierta por sector
- **Sectores críticos**: Número de sectores con ≤ 1 cliente

## 🔒 Seguridad y Validaciones

### 🛡️ Controles de Seguridad
- **Validación de entrada**: Todos los datos se validan antes del procesamiento
- **Prevención de duplicados**: Control de RUTs únicos por sector
- **Manejo de excepciones**: Error handling robusto en todas las capas
- **Logs detallados**: Trazabilidad completa de operaciones

## 👥 Autores

### 👨‍💻 **Elias Manriquez**
- 🔧 Desarrollo del sistema de servicios y repositorios
- 🎯 Implementación del algoritmo de captación automática de clientes
- 📊 Creación del sistema de análisis de sectores débiles
- ⚙️ Desarrollo de validaciones robustas (RUT chileno, datos de entrada)
- 🗄️ Arquitectura de almacenamiento en memoria y gestión de datos
- 🔍 Sistema de reportes y estadísticas avanzadas
- 🛠️ Configuración de Maven y estructura del proyecto

### 🎨 **Maximiliano Rodriguez** 
- 🖥️ Diseño e implementación de la interfaz gráfica principal (MainWindow)
- 📋 Desarrollo de ventanas de diálogo y formularios interactivos
- 📊 Creación de gráficos y visualizaciones de datos (circular y barras)
- 🎨 Diseño de la experiencia de usuario y flujos de navegación
- 🗂️ Implementación del sistema de cuadrícula de sectores con indicadores visuales
- 📄 Desarrollo del sistema de importación/exportación CSV
- 🔄 Integración entre frontend y backend para funcionalidades completas