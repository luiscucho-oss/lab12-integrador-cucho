# PROMPTS.md · Fase 2 (rama `mejora-ia`)

Una entrada por cada consulta a la IA.

## Consulta 1 · Pruebas del ListaViewModel (R12)
- Fecha: 11/11/2026
- Herramienta:
- Prompt: Pedir pruebas unitarias del `ListaViewModel` con un repositorio falso, cubriendo al menos éxito, sin internet sin datos y sin internet con datos guardados.
- Qué respondió (resumen): Propuso tres piezas de prueba: `FakeItemRepository` (guarda los datos en memoria como si fuera Room y la "API" responde lo que el test ponga en `respuestaApi`), `FakeNotificador` (anota las notificaciones en una lista en vez de mostrarlas) y `MainDispatcherRule` (cambia `Dispatchers.Main` por un `UnconfinedTestDispatcher`, porque `viewModelScope` usa Main). Con eso armó 7 pruebas: éxito con notificación, error sin datos, aviso con datos guardados, lista vacía, error HTTP 500, reintentar después de un error y favorito conservado al sincronizar.
- Qué usamos / qué cambiamos y por qué: Usamos las 7 pruebas. El repositorio falso conserva los favoritos al refrescar, igual que `ItemRepositoryImpl`, para poder probar la regla de PA-10 sin Room ni internet.
- Qué aprendimos: Como el ViewModel depende de la interface `ItemRepository` y no de la clase concreta, se puede reemplazar por una versión falsa sin tocar el ViewModel. También que `advanceUntilIdle()` deja terminar las corrutinas antes de revisar el estado.

## Consulta 2 · Pruebas del LoginViewModel
- Fecha: 11/11/2026
- Herramienta:
- Prompt: Pedir pruebas del `LoginViewModel` para la validación de campos vacíos y el manejo de la sesión.
- Qué respondió (resumen): Propuso un `FakeAuthRepository` que cuenta cuántas veces se llamó a `iniciarSesion`, y 4 pruebas: campos vacíos (muestra validación y no llama al repositorio), credenciales incorrectas (muestra el mensaje del repositorio), login correcto (entrega la sesión con su rol) y sesión guardada al abrir la app con cierre de sesión.
- Qué usamos / qué cambiamos y por qué: Usamos las 4 pruebas. La primera comprueba PA-04: con correo o clave vacíos no se llama a Firebase.
- Qué aprendimos: La validación está en el ViewModel y no en la pantalla, por eso se puede probar sin Firebase y sin emulador.

## Consulta 3 · Imágenes en modo sin conexión
- Fecha: 11/11/2026
- Herramienta:
- Prompt: Preguntar por qué en modo avión la lista se ve con el aviso de sin conexión, pero las imágenes salen vacías.
- Qué respondió (resumen): Explicó que Room guarda los datos, pero las imágenes van por la caché de Coil. Al revisar las cabeceras de la imagen con `curl -I`, el CDN de DummyJSON responde `Cache-Control: no-store`, y Coil respeta esa cabecera y no guarda la imagen en disco. Propuso que `ProductosApp` implemente `ImageLoaderFactory` con `respectCacheHeaders(false)`.
- Qué usamos / qué cambiamos y por qué: Aplicamos ese cambio en `ProductosApp`. Lo comprobamos en el emulador: abrir la app con internet, activar modo avión, cerrarla y volver a abrirla. La lista se ve con sus imágenes.
- Qué aprendimos: Revisar las cabeceras HTTP para entender por qué algo no se guarda, y que los datos (Room) y las imágenes (Coil) tienen cachés distintas.

## Reflexión final
- La IA sirvió para armar las pruebas y para encontrar la causa del problema de las imágenes, pero cada cambio hubo que comprobarlo: las pruebas se corrieron con `./gradlew testDebugUnitTest` (11 de 11 pasan) y la mejora de imágenes se verificó en el emulador.
- Las pruebas también sirven para explicar las reglas de la app: que los favoritos no se pierden al sincronizar y qué se muestra con y sin datos guardados cuando no hay internet.
