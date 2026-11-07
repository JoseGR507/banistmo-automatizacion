# Automatización Banistmo - Java + Gradle + Serenity Screenplay

## Objetivo
Automatizar el flujo descrito en el reto técnico:
1. Abrir https://www.banistmo.com/personas/
2. Entrar a Productos y servicios.
3. Seleccionar Préstamos de autos.
4. Entrar a Préstamos de Automóviles Regulares.
5. Abrir Tasas y tarifas y descargar Prohibiciones.
6. Validar que el archivo descargado sea un PDF legible cuyo contenido incluya el título Prohibiciones y texto asociado al documento.

## Requisitos
- JDK 17 o superior.
- Gradle 8.x (o usar Gradle Wrapper generado localmente).
- Microsoft Edge actualizado.
- Conexión a Internet.

Serenity/Selenium gestiona la descarga del driver compatible cuando `webdriver.autodownload = true`.

## Ejecutar en Windows PowerShell
```powershell
gradle clean test
```

Si quieres forzar Edge:
```powershell
gradle clean test -Dwebdriver.driver=edge
```

Los PDFs se guardan en `downloads/`. Los reportes Serenity se generan en `target/site/serenity/`.

## Estructura
- `tasks/`: acciones Screenplay de navegación y descarga.
- `questions/`: validación del PDF descargado.
- `stepdefinitions/`: prueba JUnit 5 que orquesta el actor.
- `serenity.conf`: configuración de Edge y carpeta de descargas.

## Validaciones implementadas
- Existe un archivo PDF en la carpeta de descarga.
- El archivo tiene firma `%PDF-`.
- El archivo tiene al menos una página.
- El texto extraído contiene `Prohibiciones`.
- El texto contiene además `Acuerdo` u `Operaciones` como comprobación secundaria.

## Importante: ajuste de selectores
La página puede cambiar su menú, texto visible o estructura HTML. Si la prueba falla en una acción de navegación, inspecciona el DOM con Edge DevTools y ajusta los `Target.locatedBy(...)` en:
`src/test/java/com/banistmo/automation/tasks/`.

El sitio puede mostrar banners de cookies, ventanas emergentes o menús responsivos que requieran un paso adicional. La automatización debe ejecutarse en una ventana visible primero (`headless` no está activado).

## Git / GitHub
Crea un repositorio vacío en GitHub y luego ejecuta:
```bash
git init
git add .
git commit -m "Initial commit - Banistmo Screenplay automation"
git branch -M main
git remote add origin https://github.com/USUARIO/banistmo-screenplay-java.git
git push -u origin main
```

Antes de la sustentación, no modifiques el repositorio después de completar el reto. Conserva el enlace y el hash del commit final para el informe.

## Limitaciones conocidas
Este starter no afirma que la ejecución haya sido exitosa en tu equipo. Es necesario correrlo en Windows con Edge y verificar los selectores reales, el comportamiento de descarga y el contenido del PDF. El documento objetivo se valida por su contenido, no solamente por el nombre del archivo.
