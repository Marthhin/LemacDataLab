# Integración GET con Retrofit · Recursos de apoyo

## Qué información se solicita
La pantalla **Recursos** muestra ejercicios y lecturas breves por ámbito (neurodesarrollo, DBT, ánimo y adicciones).
Antes estaban escritos en el código (`CatalogoContenidos.kt`). Ahora se descargan desde una API.

## Endpoint
```
GET https://raw.githubusercontent.com/Marthhin/LemacDataLab/main/docs/api/recursos.json
```
- URL base (Retrofit): `https://raw.githubusercontent.com/Marthhin/LemacDataLab/main/docs/api/`
- Ruta (`@GET`): `recursos.json`

Es una API estática de prueba: el archivo `docs/api/recursos.json` de este repositorio se sirve por HTTPS como JSON.

## JSON recibido (extracto)
```json
[
  {
    "id": 3,
    "idAmbito": 2,
    "titulo": "Habilidad STOP",
    "resumen": "Una pausa breve para no reaccionar de inmediato ante una emoción intensa.",
    "tipo": "EJERCICIO",
    "minutos": 2,
    "pasos": [
      "Para: no actúes todavía.",
      "Toma distancia: respira y da un paso atrás.",
      "Observa qué está pasando dentro y fuera de ti.",
      "Procede con atención, eligiendo qué hacer."
    ]
  }
]
```

## Data class
`datos/remoto/RecursoDto.kt`
```kotlin
data class RecursoDto(
    val id: Int,
    val idAmbito: Int,
    val titulo: String,
    val resumen: String,
    val tipo: String,
    val minutos: Int,
    val pasos: List<String>?
)
```
Gson convierte cada objeto del JSON en un `RecursoDto` (los nombres de los campos coinciden).
Luego `aContenido()` lo transforma al modelo de la app (`Contenido`), convirtiendo `idAmbito` en el enum `Ambito`
y descartando ámbitos desconocidos.

## Retrofit
- `datos/remoto/RecursosApiService.kt`: `@GET("recursos.json") suspend fun obtenerRecursos(): List<RecursoDto>`
- `datos/remoto/ClienteRetrofit.kt`: crea `Retrofit` con la URL base, `GsonConverterFactory`
  y un `OkHttpClient` con tiempo límite de 10 s y registro de la solicitud en Logcat (`okhttp.OkHttpClient`).

## Arquitectura
```
PantallaRecursos (Compose)
   → RecursosViewModel   (StateFlow<EstadoRecursos>)
   → RepositorioContenidos
   → RecursosApiService
   → Retrofit + OkHttp
   → API (recursos.json)
```

## Estados en la interfaz
`EstadoCarga` (sealed interface en `RecursosViewModel.kt`):
- **Cargando**: indicador circular y «Cargando recursos…».
- **Datos**: filtros por ámbito y la lista de recursos.
- **Error**: ícono, mensaje (sin conexión / error HTTP / respuesta inválida) y botón **Reintentar**.

Para mostrar el estado de error en la revisión: activar el modo avión y tocar **Reintentar**.
