<p align="center">
  <img src="imagenes/logoMaquimotor.png" alt="Logo MaquiMotor Perú S.A.C." width="450"/>
</p>

# ⚙️ Sistema de Gestión de Inventario y Cotizaciones — MaquiMotor Perú S.A.C.

Sistema de gestión desarrollado en **Java** para la administración de inventario de maquinaria industrial, clientes y cotizaciones comerciales.

El sistema permite controlar el stock, gestionar clientes corporativos, generar cotizaciones con cálculo de **IGV (18%)**, realizar reservas temporales de productos y cerrar o anular operaciones comerciales.

---

## 📋 Características principales

* 🔐 Autenticación de usuarios por roles.
* 👥 Gestión de clientes corporativos.
* 📦 Gestión de inventario de maquinaria industrial.
* 🔧 Visualización de fichas técnicas de equipos.
* ⚠️ Control y alertas de stock mínimo.
* 🧾 Generación de cotizaciones.
* 🧮 Cálculo automático de subtotal, IGV y total.
* 🔒 Reserva temporal de stock durante 48 horas.
* 📄 Exportación de proformas en formato `.txt`.
* 💰 Confirmación de pago y cierre de venta.
* 🔄 Liberación de reservas y anulación de cotizaciones.
* 🗄️ Persistencia de información mediante Microsoft SQL Server.

---

# 🛠️ Tecnologías utilizadas

| Tecnología        | Versión / Detalle                                       |
| ----------------- | ------------------------------------------------------- |
| Java              | JDK 17 o superior                                       |
| Base de datos     | Microsoft SQL Server 2019 / 2022 / Express              |
| JDBC              | Microsoft SQL Server JDBC Driver                        |
| IDE               | Visual Studio Code / IntelliJ IDEA / NetBeans / Eclipse |
| GUI               | Java                                                    |
| Sistema operativo | Windows 10/11, Linux o macOS                            |

### Driver JDBC

El proyecto incluye el siguiente controlador:

```text
lib/mssql-jdbc-13.6.0.jre11.jar
```

---

# 📁 Estructura del proyecto

```text
MaquiMotor/
│
├── lib/
│   └── mssql-jdbc-13.6.0.jre11.jar
│
├── src/
│   └── com/
│       └── maquimotor/
│           ├── app/
│           │   └── Main.java
│           │
│           ├── conexion/
│           │   ├── ConexionDB.java
│           │   └── InicializadorDB.java
│           │
│           ├── dao/
│           │   └── ...
│           │
│           ├── modelo/
│           │   └── ...
│           │
│           └── vista/
│               └── ...
│
├── bin/
│
├── schema_maquimotor.sql
│
└── README.md
```

---

# 💻 Requisitos del entorno

Antes de ejecutar el proyecto, asegúrate de tener instalado:

### 1. Java JDK

Se requiere:

```text
JDK 17 o superior
```

Verifica la instalación desde la terminal:

```bash
java -version
```

También se recomienda configurar la variable de entorno:

```text
JAVA_HOME
```

---

### 2. Microsoft SQL Server

Se requiere una instancia de:

* SQL Server 2019
* SQL Server 2022
* SQL Server Express

La autenticación debe estar configurada en **Modo Mixto**, permitiendo autenticación mediante SQL Server.

También se recomienda verificar:

* TCP/IP habilitado.
* Puerto `1433` disponible.
* Servicio SQL Server activo.
* SQL Server Browser activo cuando sea necesario.

---

### 3. Visual Studio Code

Se recomienda utilizar:

**Visual Studio Code + Extension Pack for Java**

También es posible utilizar:

* IntelliJ IDEA
* NetBeans
* Eclipse

---

# 🗄️ Configuración de la base de datos

Existen dos métodos para inicializar la base de datos.

## Inicialización automática

### Paso 1

Abrir:

* SQL Server Management Studio (SSMS)

### Paso 2

Conectarse a la instancia local de SQL Server.

### Paso 3

El proyecto incluye la clase:

```text
InicializadorDB.java
```

Esta clase permite verificar e inicializar automáticamente las tablas y registros necesarios durante el primer inicio del sistema.

---

# 🔌 Configuración de conexión

Antes de ejecutar el proyecto, revisar el archivo:

```text
src/com/maquimotor/conexion/ConexionDB.java
```

La configuración debe coincidir con la instancia local de SQL Server.

Ejemplo:

```java
private static final String HOST = "localhost";
private static final int PORT = 1433;
private static final String DB_NAME = "MaquiMotorDB";
private static final String USER = "sa";
private static final String PASS = "TuPassword";
```

### Parámetros

| Parámetro     | Valor                             |
| ------------- | --------------------------------- |
| Host          | `localhost`                       |
| Puerto        | `1433`                            |
| Base de datos | `MaquiMotorDB`                    |
| Usuario       | Usuario configurado en SQL Server |
| Contraseña    | Contraseña del usuario            |

---

# ▶️ Ejecución desde Visual Studio Code

## 1. Abrir el proyecto

Descomprimir el proyecto en una carpeta local.

En Visual Studio Code seleccionar:

```text
File → Open Folder...
```

Seleccionar la carpeta raíz del proyecto.

---

## 2. Verificar el driver JDBC

En el panel:

```text
JAVA PROJECTS
```

verificar que el archivo:

```text
lib/mssql-jdbc-13.6.0.jre11.jar
```

esté incluido dentro de:

```text
Referenced Libraries
```

---

## 3. Ejecutar la aplicación

Navegar hasta:

```text
src/com/maquimotor/app/Main.java
```

Hacer clic derecho sobre `Main.java` y seleccionar:

```text
Run Java
```

También puede utilizarse:

```text
F5
```

Si la configuración es correcta, se mostrará la pantalla:

```text
LoginView
```

---

# 🖥️ Ejecución desde la terminal

También es posible compilar y ejecutar el proyecto directamente desde `cmd` o PowerShell.

## 1. Abrir la terminal

Ubicarse en la carpeta raíz del proyecto:

```bash
cd ruta/del/proyecto
```

---

## 2. Compilar

Ejecutar:

```bash
javac -cp "lib/mssql-jdbc-13.6.0.jre11.jar" -d bin src/com/maquimotor/conexion/*.java src/com/maquimotor/modelo/*.java src/com/maquimotor/dao/*.java src/com/maquimotor/vista/*.java src/com/maquimotor/app/*.java
```

Los archivos compilados serán almacenados en:

```text
bin/
```

---

## 3. Ejecutar

En Windows:

```bash
java -cp "bin;lib/mssql-jdbc-13.6.0.jre11.jar" com.maquimotor.app.Main
```

En Linux/macOS, utilizar `:` como separador del classpath:

```bash
java -cp "bin:lib/mssql-jdbc-13.6.0.jre11.jar" com.maquimotor.app.Main
```

---

# 🔐 Credenciales de acceso

El sistema cuenta con usuarios de prueba preconfigurados.

### 👤 Asesor Comercial

```text
Usuario:     asesor1
Contraseña:  123456
Rol:         Asesor Comercial
```
### 👤 Jefe de Almacen

```text
Usuario:     almacen1
Contraseña:  123456
Rol:         Jefe de Almacen
```
### 👨‍💼 Administrador

```text
Usuario:     admin
Contraseña:  admin123
Rol:         Administrador
```

> ⚠️ Estas credenciales son únicamente para pruebas y demostración. En un entorno productivo deben cambiarse.

---

# 🚀 Flujo principal del sistema

## 1. 🔐 Autenticación

Al iniciar la aplicación aparecerá la pantalla de inicio de sesión.

Ingresar utilizando uno de los usuarios disponibles.

---

## 2. 📦 Gestión de inventario

Desde el **Menú Principal**, ingresar a:

```text
Inventario
```

Desde esta sección es posible:

* Visualizar las maquinarias industriales.
* Consultar especificaciones técnicas.
* Revisar información del motor.
* Consultar disponibilidad.
* Controlar el stock.
* Identificar productos con stock crítico mediante alertas visuales.

---

## 3. 👥 Gestión de clientes

Ingresar a:

```text
Clientes
```

El sistema permite:

* Registrar nuevos clientes corporativos.
* Consultar clientes existentes.
* Validar RUC o DNI.
* Verificar que el RUC tenga 11 dígitos y el DNI 8 dígitos.

---

# 🧾 Generación de cotizaciones

Para generar una nueva cotización:

### Paso 1

Ingresar a:

```text
Nueva Cotización
```

### Paso 2

Seleccionar un cliente corporativo registrado.

### Paso 3

Agregar las maquinarias requeridas.

### Paso 4

Indicar la cantidad solicitada.

### Paso 5

El sistema calculará automáticamente:

```text
Subtotal
IGV (18%)
Total
```

### Paso 6

Presionar:

```text
Registrar Cotización
```

Al registrar la cotización, el sistema:

1. Descuenta las unidades correspondientes del stock físico.
2. Reserva temporalmente las unidades.
3. Mantiene la reserva durante **48 horas**.

---

# 📚 Historial y cierre comercial

Ingresar a:

```text
Historial de Cotizaciones
```

Seleccionar una cotización que se encuentre en estado:

```text
Vigente
```

El sistema proporciona las siguientes operaciones.

---

## 📄 Exportar Proforma

Permite generar una proforma formal para el cliente en formato:

```text
.txt
```

La proforma incluye el membrete y la información correspondiente a la cotización.

---

## 💰 Confirmar Pago — Cerrar Venta

La opción:

```text
Confirmar Pago (Cerrar Venta)
```

permite consolidar definitivamente la operación.

Al cerrar la venta:

* Se confirma la operación comercial.
* El stock queda definitivamente afectado.
* La enajenación del stock no puede revertirse mediante el flujo normal del sistema.

---

## 🔄 Liberar Reserva / Anular

La opción:

```text
Liberar Reserva / Anular
```

permite cancelar la cotización.

Al realizar esta operación:

1. La cotización pasa a estado anulada.
2. Se libera la reserva.
3. Las unidades reservadas regresan al stock general.

---

# 🔄 Flujo de una cotización

```text
                 ┌──────────────────┐
                 │   Nueva Cotización│
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Seleccionar      │
                 │ Cliente          │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Agregar equipos  │
                 │ y cantidades     │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Calcular         │
                 │ Subtotal + IGV   │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Registrar        │
                 │ Cotización       │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │ Reserva 48 horas │
                 └───────┬──────┬───┘
                         │      │
              ┌──────────┘      └──────────┐
              ▼                             ▼
     ┌──────────────────┐          ┌──────────────────┐
     │ Confirmar Pago   │          │ Liberar Reserva  │
     │ / Cerrar Venta   │          │ / Anular         │
     └────────┬─────────┘          └────────┬─────────┘
              │                             │
              ▼                             ▼
     ┌──────────────────┐          ┌──────────────────┐
     │ Venta definitiva │          │ Stock restaurado │
     └──────────────────┘          └──────────────────┘
```

---

# 🗃️ Modelo de datos

Las principales entidades utilizadas por el sistema son:

```text
Usuario
   │
   │
   └───────────────┐
                   │
Cliente ───────► Cotizacion
                   │
                   ▼
           DetalleCotizacion
                   │
                   ▼
                 Equipo
                   │
                   ▼
             FichaTecnica
```

### Entidades principales

| Tabla               | Descripción                                       |
| ------------------- | ------------------------------------------------- |
| `Usuario`           | Usuarios y roles del sistema                      |
| `Cliente`           | Información de clientes corporativos              |
| `FichaTecnica`      | Especificaciones técnicas de los equipos          |
| `Equipo`            | Maquinaria disponible e información de stock      |
| `Cotizacion`        | Cabecera de las cotizaciones                      |
| `DetalleCotizacion` | Equipos y cantidades incluidos en cada cotización |

---

# 📊 Cálculo de cotización

El sistema utiliza un **IGV del 18%**.

La operación se realiza de la siguiente manera:

```text
Subtotal = Σ (Precio × Cantidad)

IGV = Subtotal × 0.18

Total = Subtotal + IGV
```

### Ejemplo

```text
Subtotal: S/ 10,000.00

IGV (18%): S/ 1,800.00

Total: S/ 11,800.00
```

---

# ⏱️ Sistema de reservas

Cuando una cotización es registrada, las unidades solicitadas quedan reservadas durante:

```text
48 horas
```

El objetivo es evitar que el mismo stock pueda ser asignado simultáneamente a otra operación comercial.

Existen dos resultados principales:

```text
Cotización
     │
     ├──► Confirmar pago
     │        │
     │        └──► Venta definitiva
     │
     └──► Anular
              │
              └──► Liberación del stock
```

---

# 🧪 Usuarios de prueba

| Usuario  | Contraseña    | Rol              |
| -------- | ------------- | ---------------- |
| `asesor1`| `123456`      | Asesor Comercial |
|`almacen1`| `123456`      | Jefe de Almacen  |
| `admin`  | `admin123`    | Administrador    |

---

# 🛡️ Recomendaciones de seguridad

Para utilizar el sistema en un entorno real se recomienda:

* No almacenar contraseñas directamente en el código fuente.
* Utilizar variables de entorno para las credenciales.
* Implementar hash seguro de contraseñas.
* No utilizar el usuario `sa` en producción.
* Utilizar usuarios SQL con permisos mínimos necesarios.
* Validar todas las entradas del usuario.
* Utilizar consultas preparadas mediante `PreparedStatement`.
* Mantener actualizado el driver JDBC.
* Configurar correctamente las reglas del firewall.
* No publicar credenciales reales en GitHub.

---

# 👨‍💻 Autor / Proyecto

**MaquiMotor Perú S.A.C.**

Sistema de gestión de inventario y cotizaciones desarrollado como solución para la administración comercial de maquinaria industrial.

---

# 📄 Licencia

Este proyecto es de uso académico.

Si se requiere utilizar el sistema comercialmente, deberán definirse las condiciones de licencia correspondientes.
