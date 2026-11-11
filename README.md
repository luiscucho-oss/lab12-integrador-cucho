# Lab Integrador · Catálogo de Productos

App Android en Kotlin con **Jetpack Compose + MVVM + Retrofit + Room + Firebase Auth/Firestore** y notificaciones locales.

- **Curso:** Programación en Móviles · Tecsup
- **Laboratorio:** 11-12
- **Autor:** [luiscucho-oss](https://github.com/luiscucho-oss)
- **Grupo:** GXX

## Tema y variante

| Tema | API | Variante |
|---|---|---|
| Productos | [DummyJSON](https://dummyjson.com) | Categoría `smartphones` |

| Uso | Endpoint |
|---|---|
| Lista | `GET https://dummyjson.com/products/category/smartphones` |
| Detalle | `GET https://dummyjson.com/products/{id}` |

La categoría está en una sola constante: `AppContainer.CATEGORIA`.

## Qué hace la app

| Función | Usuario | Admin |
|---|---|---|
| Ingresar con correo y contraseña | Sí | Sí |
| Ver lista y detalle | Sí | Sí |
| Marcar favoritos y ver Favoritos | Sí | Sí |
| Cerrar sesión | Sí | Sí |
| Panel Admin: totales, Sincronizar y Limpiar caché | No | Sí |

- **Login (R01):** valida campos vacíos sin llamar a Firebase y muestra un mensaje claro si las credenciales fallan.
- **Roles (R02):** el rol se lee de Firestore (`usuarios/{uid}` → campo `rol`). La app no decide el rol.
- **Lista (R03):** estados Cargando, Error (con Reintentar) y Vacío.
- **Detalle (R04):** usa el segundo endpoint (`/products/{id}`) con imágenes, precio, calificación, stock y descripción.
- **Room (R06):** lo descargado se guarda en el celular. La lista siempre sale de Room.
- **Favoritos (R07):** se conservan al cerrar la app y al sincronizar.
- **Sin conexión (R08):** sin internet se muestran los datos guardados con un aviso. Si no hay nada guardado, aparece el error con Reintentar.
- **Notificación (R09):** al terminar de sincronizar muestra «Se guardaron N elementos». Usa canal y permiso `POST_NOTIFICATIONS`. Al tocarla se abre la app.
- **Panel Admin (R10):** total guardado, total de favoritos, Sincronizar y Limpiar caché.
- **Sesión (R11):** se mantiene al reabrir la app y hay botón Cerrar sesión.

## Arquitectura (MVVM)

```
UI (Compose) ──eventos──▶ ViewModel ──▶ Repository (interface) ──▶ Retrofit (DTO)
     ▲                       │                 │                 ├─▶ Room (Entity) ← fuente de verdad
     └──────estado───────────┘                 │                 └─▶ Firebase (Auth + Firestore)
                             └──avisa──▶ Notificador (interface)
```

- Las pantallas solo observan `StateFlow<UiState<T>>` con `collectAsStateWithLifecycle()` y llaman funciones del ViewModel.
- Ningún composable usa Retrofit, Room ni Firebase.
- Los ViewModel no reciben `Context`: la notificación se pide por la interface `Notificador`.
- Los ViewModel dependen de las interfaces `ItemRepository` y `AuthRepository`, nunca de las clases concretas.
- Tres modelos distintos: `ItemDto` (API), `ItemEntity` (Room) e `Item` (UI), convertidos en `Mappers.kt`.
- Los ViewModel se crean con `ViewModelFactory`, que recibe el `AppContainer`.

### Estructura

```
app/src/main/java/com/tecsup/productos/
├── MainActivity.kt
├── ProductosApp.kt            ← Application: guarda el AppContainer
├── AppContainer.kt            ← crea API, base de datos y repositorios
├── data/
│   ├── remote/                ApiService · RetrofitClient · dto/ItemDto
│   ├── local/                 ItemEntity · ItemDao · AppDatabase
│   ├── auth/                  AuthRepository · FirebaseAuthRepository
│   ├── repository/            ItemRepository · ItemRepositoryImpl
│   └── mapper/Mappers.kt      Dto → Entity → modelo
├── domain/model/              Item · Sesion (+ enum Rol)
├── notification/              Notificador · NotificadorAndroid
└── ui/
    ├── navigation/AppNav.kt
    ├── UiState.kt · ViewModelFactory.kt · MensajeError.kt
    ├── login/ · lista/ · detalle/ · favoritos/ · admin/
    ├── components/            Cargando · ErrorView · Vacio · ItemFila · ListaItems
    └── theme/
```

## Cómo compilar

1. Abrir el proyecto en Android Studio (versión reciente, con JDK 17 o superior).
2. Colocar el `google-services.json` de tu proyecto de Firebase dentro de `app/` (ver la sección siguiente). **Este archivo no está en el repositorio.**
3. *Sync Project with Gradle Files* y ejecutar en un emulador con Google Play (API 26 o superior).

Desde la terminal:

```bash
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Configurar Firebase

1. En <https://console.firebase.google.com/> crear un proyecto (se puede desactivar Analytics).
2. Registrar una app Android con el paquete **`com.tecsup.productos`**.
3. Descargar `google-services.json` y colocarlo en `app/`.
4. **Authentication → Método de acceso:** activar *Correo electrónico/contraseña*.
5. **Authentication → Usuarios:** crear dos usuarios de prueba, uno admin y uno usuario.
6. **Firestore Database:** crear la base de datos y la colección `usuarios`, con un documento por usuario:
   - ID del documento: el **UID** del usuario (se copia desde Authentication → Usuarios).
   - Campos: `rol` (texto: `admin` o `usuario`) y `nombre` (texto).
7. **Firestore → Reglas:** pegar y publicar:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /usuarios/{uid} {
      allow read: if request.auth != null && request.auth.uid == uid;
      allow write: if false;
    }
  }
}
```

> Validar el rol en la app solo cambia lo que se muestra. Lo que protege los datos son las reglas de Firestore.

### Usuarios de prueba

| Rol | Correo |
|---|---|
| admin | `admin@...` |
| usuario | `usuario@...` |

Las contraseñas no se suben al repositorio.

## Pruebas unitarias (R12)

En `app/src/test/`, con un repositorio y un notificador falsos (`fakes/`):

- `ListaViewModelTest` (7 pruebas): éxito con notificación, sin internet sin datos (error), sin internet con datos (aviso), lista vacía, error HTTP 500, reintentar después de un error y favorito conservado al sincronizar.
- `LoginViewModelTest` (4 pruebas): campos vacíos sin llamar a Firebase, credenciales incorrectas, login correcto con rol y sesión guardada con cierre de sesión.

```bash
./gradlew testDebugUnitTest
```

## Fase 2 (rama `mejora-ia`)

- Pruebas unitarias del `ListaViewModel` y del `LoginViewModel`.
- Imágenes visibles sin conexión: el CDN responde `Cache-Control: no-store`, así que Coil se configura para guardarlas igual en la caché de disco (`ProductosApp.newImageLoader`).
- Registro de consultas en [`PROMPTS.md`](PROMPTS.md).

## Pruebas de aceptación

- [ ] PA-01 Ingresar con `usuario`: entra al catálogo y no ve el panel Admin.
- [ ] PA-02 Ingresar con `admin`: entra al catálogo y ve la pestaña Admin.
- [ ] PA-03 Contraseña incorrecta: mensaje de error claro.
- [ ] PA-04 Correo o clave vacíos: muestra validación y no llama a Firebase.
- [ ] PA-05 Primera apertura con internet: Cargando → lista → notificación «Se guardaron N elementos».
- [ ] PA-06 Tocar un elemento: muestra el detalle del segundo endpoint.
- [ ] PA-07 Marcar un favorito y cerrar la app: al reabrir sigue en Favoritos.
- [ ] PA-08 Modo avión con datos guardados: la lista se ve con el aviso de sin conexión.
- [ ] PA-09 Modo avión sin datos: pantalla de error con Reintentar.
- [ ] PA-10 Sincronizar después de marcar un favorito: el favorito no se pierde.
- [ ] PA-11 Admin pulsa Limpiar caché: lista vacía y totales en 0; al sincronizar vuelven.
- [ ] PA-12 Cerrar sesión y reabrir: vuelve al login.
- [ ] PA-13 Girar la pantalla: no se pierde el estado ni se vuelve a descargar.
- [ ] PA-14 Negar el permiso de notificaciones: la app funciona normal, sin avisos.

## Capturas

| Login | Lista | Detalle |
|---|---|---|
| ![Login](docs/capturas/login.png) | ![Lista](docs/capturas/lista.png) | ![Detalle](docs/capturas/detalle.png) |

| Favoritos | Panel Admin | Notificación |
|---|---|---|
| ![Favoritos](docs/capturas/favoritos.png) | ![Admin](docs/capturas/admin.png) | ![Notificación](docs/capturas/notificacion.png) |

## Reparto de tareas

| Integrante | Tareas |
|---|---|
| luiscucho-oss | |
| | |

## Versiones

| Herramienta | Versión |
|---|---|
| Gradle | 9.8.0 |
| Android Gradle Plugin | 9.4.1 |
| Kotlin | 2.4.20 |
| KSP | 2.3.6 |
| Compose BOM | 2026.09.00 |
| Retrofit | 2.11.0 |
| Room | 2.8.5 |
| Firebase BOM | 35.1.0 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 26 |
