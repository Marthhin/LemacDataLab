# API REST simulada (mock) · LemacDataLab

La contraparte no entrega una API (respuesta 1.2), así que la app incluye su propio backend simulado
dentro del dispositivo: `datos/api/ServidorSimulado.kt`. Atiende solicitudes REST con cuerpos JSON,
agrega una latencia de 250 ms y guarda su estado en `servidor_simulado.json` (almacenamiento interno de la app).
Cada solicitud queda en Logcat con la etiqueta `ApiSimulada`.

- Base: `/api/v1`
- Autenticación: token simulado en el encabezado lógico `token` (se guarda cifrado con Android Keystore).
- Formato de fechas: milisegundos UTC (`fechaModificacion`, `fechaAceptacion`); `fecha` del registro en `dd/MM/yyyy`.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/login` | Inicia sesión y entrega un token |
| GET | `/consentimiento` | Versión vigente y consentimiento aceptado |
| POST | `/consentimiento` | Acepta el consentimiento |
| GET | `/registros?desde={ms}` | Cambios desde una fecha (incluye eliminados) |
| POST | `/registros` | Crea un registro |
| PUT | `/registros/{uuid}` | Crea o actualiza un registro |
| DELETE | `/registros/{uuid}?fechaModificacion={ms}` | Borrado lógico |

Códigos: `200/201` éxito · `400` datos incompletos · `401` token inválido · `403` registro ajeno ·
`404` no encontrado · `409` existe una versión más reciente · `422` versión de consentimiento no vigente.

## Usuarios del dataset sintético

| Alias | Contraseña | Objetivo personal |
|---|---|---|
| camila.rojas | Lemac2026 | No consumir alcohol de lunes a jueves |
| diego.munoz | Lemac2026 | Reducir el cigarro a 3 por día |
| valentina.soto | Lemac2026 | Llamar a mi contacto de apoyo cuando aparezca el impulso |

## Ejemplos

### POST /auth/login
```json
{ "alias": "camila.rojas", "contrasena": "Lemac2026" }
```
Respuesta `200`:
```json
{
  "token": "tok_3f1c9a0e5b6d4f0c8a2e7d1b9c4f6a21",
  "expiraEnSegundos": 300,
  "usuario": { "id": 1, "alias": "camila.rojas", "nombre": "Camila Rojas",
               "objetivo": "No consumir alcohol de lunes a jueves" }
}
```

### GET /consentimiento
Respuesta `200`:
```json
{ "versionVigente": "1.0", "aceptado": { "version": "1.0", "fechaAceptacion": 1762740000000 } }
```

### POST /consentimiento
```json
{ "version": "1.0", "fechaAceptacion": 1762740000000 }
```
Respuesta `201`:
```json
{ "version": "1.0", "fechaAceptacion": 1762740000000 }
```

### PUT /registros/{uuid}
```json
{
  "uuid": "6b0f2d7e-1a4c-4d8e-9f3b-2c5a7e9d1f40",
  "idAmbito": 3,
  "fecha": "10/11/2026",
  "tipoActividad": "Registro de hoy",
  "intensidad": 2,
  "elevacion": 1,
  "horasSueno": 6.5,
  "altoRiesgo": false,
  "contexto": "Registro diario",
  "nota": null,
  "emocion": "Cansancio",
  "habilidad": null,
  "estrategia": null,
  "desencadenante": null,
  "accionProtectora": null,
  "resultado": null,
  "editado": false,
  "eliminado": false,
  "fechaModificacion": 1762741200000
}
```
Respuesta `200`: el mismo registro con `idUsuario`.

Respuesta `409` (el servidor tiene una versión más nueva):
```json
{ "error": "Existe una versión más reciente", "registro": { "uuid": "6b0f2d7e-…", "fechaModificacion": 1762741900000 } }
```

### DELETE /registros/{uuid}?fechaModificacion=1762742000000
Respuesta `200`: el registro con `"eliminado": true`.

### GET /registros?desde=1762700000000
Respuesta `200`:
```json
{ "registros": [ { "uuid": "6b0f2d7e-…", "idAmbito": 3, "eliminado": false, "fechaModificacion": 1762741200000 } ],
  "fechaServidor": 1762742100000 }
```

## Sincronización (respuesta 1.1)

`datos/api/Sincronizador.kt` corre al iniciar sesión y desde **Perfil → Sincronizar ahora**:

1. Sube los registros locales con `fechaModificacion` posterior a la última sincronización
   (PUT, o DELETE si están eliminados).
2. Si el servidor responde `409`, gana su versión (es la más reciente) y la versión local se guarda en el
   historial de versiones del registro, así no se pierde.
3. Descarga los cambios desde la última sincronización y aplica la regla **última modificación gana, por registro**.
4. Las eliminaciones son lógicas y compiten con las ediciones con la misma regla.
