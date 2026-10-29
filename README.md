# LemacDataLab

Proyecto semestral de **Desarrollo de Aplicaciones Móviles — DSY1105**.

Aplicación Android de autorregistro académico para organizar actividades relacionadas con neurodesarrollo, habilidades DBT, estado de ánimo y adicciones.

## Integrantes

- Marthin Celedon
- Martin Martiz

## Problemática

La información relacionada con estos cuatro ámbitos puede quedar distribuida entre distintas herramientas y registros. Esta fragmentación dificulta mantener la continuidad, consultar antecedentes y organizar las actividades de una persona usuaria.

## Objetivo general

Desarrollar una aplicación móvil que permita registrar, organizar y consultar información de autorreporte, utilizando exclusivamente datos ficticios o sintéticos.

## Funcionalidades implementadas

- Inicio con saludo según la hora del dispositivo, total de registros y última actividad agregada.
- Formulario para registrar actividades en los cuatro ámbitos del proyecto.
- Selección de fecha mediante calendario.
- Validación de campos obligatorios e intensidad numérica entera.
- Pantalla de resultado con confirmación visual animada.
- Historial de registros almacenados en el dispositivo.
- Edición de registros existentes sin duplicarlos.
- Eliminación con confirmación y mensaje de resultado.
- Persistencia local mediante Room: los datos se conservan al cerrar y abrir la aplicación.
- Actualización del resumen y del historial mediante Flow.
- Notificación local de prueba con solicitud de permiso cuando corresponde.
- Interfaz con tarjetas, iconos y temas claro y oscuro.

## Tecnologías utilizadas

- Android Studio
- Kotlin
- Jetpack Compose y Material 3
- Navigation Compose
- Arquitectura MVVM
- Room sobre SQLite
- Repository y DAO
- Corrutinas, viewModelScope y Flow
- KSP
- Git y GitHub

## Organización del código

El proyecto Android se encuentra en la carpeta `mobile`.

| Ubicación | Responsabilidad |
|---|---|
| `model/` | Modelos de datos y Entity de Room |
| `datos/` | DAO, Room Database y Repository |
| `viewmodel/` | Estado, validaciones y operaciones de las pantallas |
| `ui/pantallas/` | Pantallas desarrolladas con Compose |
| `ui/componentes/` | Componentes visuales reutilizables |
| `ui/theme/` | Colores, tipografía y temas |
| `navigation/` | Rutas de navegación |
| `notificaciones/` | Creación de la notificación local de prueba |

El acceso a los datos sigue este flujo:

**Interfaz → ViewModel → Repository → DAO → Room → SQLite**

Las operaciones de escritura utilizan corrutinas. Las consultas observables utilizan Flow para reflejar los cambios en la interfaz.

## Ejecución

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/Marthhin/LemacDataLab.git
   ```

2. Abrir la carpeta `LemacDataLab/mobile` en Android Studio.
3. Esperar la sincronización de Gradle e instalar los componentes del SDK que solicite el proyecto.
4. Seleccionar un emulador o dispositivo Android compatible con API 24 o superior.
5. Ejecutar la configuración `app` con **Run**.

Para compilar desde PowerShell, dentro de `mobile`:

```powershell
.\gradlew.bat assembleDebug
```

## Prueba del flujo principal

1. Crear un registro con datos ficticios.
2. Comprobar las validaciones intentando guardar campos obligatorios vacíos.
3. Guardar datos válidos y consultar el historial.
4. Editar un registro y verificar que no se duplique.
5. Cancelar una eliminación y comprobar que el registro permanezca.
6. Confirmar una eliminación.
7. Cerrar completamente la aplicación y volver a abrirla.
8. Verificar que los registros y sus modificaciones se conserven.

La notificación de prueba se encuentra en **Recordatorios → Probar notificación**.

## Limitaciones y pendientes

- Recursos de apoyo todavía no está implementado; aparece como «Próximamente».
- Las notificaciones se activan manualmente. Todavía no se programan por horario.
- La escala y los límites de intensidad están pendientes de validación; actualmente se comprueba que sea un número entero.
- Se utiliza un usuario ficticio de prueba, sin autenticación ni separación entre cuentas.
- El almacenamiento es local, sin sincronización con servicios externos.

## Uso académico y privacidad

Se deben utilizar únicamente datos ficticios o sintéticos.

La aplicación no entrega diagnósticos, tratamientos ni atención de urgencia, y no reemplaza la atención profesional.

La notificación utiliza un mensaje general y no incluye el contenido de los registros.

## Estado del proyecto

MVP académico en desarrollo, con un flujo CRUD funcional integrado con Room y MVVM, navegación entre pantallas y una interfaz renovada.

El desarrollo continúa de forma incremental: implementar, compilar, ejecutar, comprobar y realizar commit.
