# MercadoLibre Playground

> Java 17 · Maven · JUnit 5 · Mockito · AssertJ · Lombok

## 🛠️ Requerimientos e Instalación (Windows)

Para ejecutar este proyecto, necesitas tener instalados **Java** y **Maven**.

### 1. Java JDK (Mínimo versión 17)
Ya tienes instalado Java 25 en tu sistema, lo cual es excelente. El proyecto está configurado para Java 17, así que funcionará sin problemas.
*   **Verificar:** Abre una terminal y escribe `java -version`.

### 2. Apache Maven
Es el gestor de dependencias y construcción. Como no está en tu terminal, sigue estos pasos:
1.  **Descargar:** Ve a [maven.apache.org](https://maven.apache.org/download.cgi) y descarga el "Binary zip archive".
2.  **Descomprimir:** Extrae el contenido en una carpeta (ej: `C:\Program Files\apache-maven-3.9.x`).
3.  **Variables de Entorno:**
    *   Busca "Editar las variables de entorno del sistema" en Windows.
    *   Crea una nueva variable de usuario llamada `MAVEN_HOME` con la ruta de la carpeta (ej: `C:\Program Files\apache-maven-3.9.x`).
    *   Busca la variable `Path`, edítala y agrega: `%MAVEN_HOME%\bin`.
4.  **Verificar:** Cierra y abre la terminal, luego escribe `mvn -version`.

---

## 🚀 Flujo de Desarrollo

Una vez configurado Maven, este es el flujo que seguirás para el challenge:

### 1. Preparación
```bash
# Limpiar compilaciones anteriores
mvn clean
```

### 2. Ciclo de Trabajo (Compilar y Probar)
Realiza cambios en tu código y asegúrate de que todo siga funcionando:
```bash
# Compilar el código
mvn compile

# Ejecutar TODOS los tests (Crucial para el challenge)
mvn test
```

## 🚀 Ejecución y Autenticación (MeLi Colombia)

Este proyecto está configurado para trabajar con **MercadoLibre Colombia (MCO)**. Para manejar las restricciones de la API (error 403), el sistema utiliza una **Estrategia de Búsqueda de Usuario**.

### 1. Obtención del Access Token (Requerido para datos reales)
Para una prueba manual, sigue estos pasos:
1. Crea una app en el [DevCenter de MeLi](https://developers.mercadolibre.com.co/devcenter/) con Redirect URI: `https://webhook.site/`.
2. Obtén el código de autorización pegando esto en tu navegador (usa tu `APP_ID`):  
   `https://auth.mercadolibre.com.co/authorization?response_type=code&client_id=TU_APP_ID&redirect_uri=https://webhook.site/`
3. Copia el parámetro `?code=...` de la URL de redirección en `webhook.site`.
4. Cambia el código por el token final usando `curl`:
   ```bash
   curl -X POST "https://api.mercadolibre.com/oauth/token" \
     -d "grant_type=authorization_code" \
     -d "client_id=TU_APP_ID" \
     -d "client_secret=TU_SECRET" \
     -d "code=EL_CODIGO" \
     -d "redirect_uri=https://webhook.site/"
   ```

### 2. Ejecución con Token
Para ejecutar la aplicación con tu token real en **Git Bash** o terminal Linux/Mac:
```bash
MELI_ACCESS_TOKEN="tu_access_token_aqui" mvn exec:java
```

> [!NOTE]
> Si no se proporciona un token, la aplicación activará automáticamente un **Fallback a Mocks**, permitiendo que el reporte se genere con datos de prueba para demostrar la funcionalidad.

### 3. Estrategia de Búsqueda (Bypass 403)
Debido a las políticas restrictivas de MeLi (Abril 2025), la búsqueda global (`/sites/MCO/search`) suele devolver 403. Por ello, el `MeliClient` implementa:
1. **Listar IDs:** Consulta `/users/{user_id}/items/search` para obtener los IDs de productos del usuario autenticado.
2. **Fetch Details:** Consulta `/items?ids=...` para obtener la información completa (precio, título, etc.).

---

## 📁 Estructura del Proyecto

```
meli-playground/
├── pom.xml                        ← Configuración (Compatibilidad Java 25 + Lombok)
├── README.md                      ← Esta guía
└── src/
    ├── main/
    │   ├── java/com/meli/challenge/
    │   │   ├── Main.java          ← Punto de entrada (Query configurado: "Lente")
    │   │   ├── client/            ← MeliClient con lógica de bypass 403
    │   │   ├── model/             ← DTOs y modelos de datos
    │   │   ├── service/           ← Lógica de negocio y procesamiento
    │   │   └── repository/        ← Persistencia en memoria
```

## 💡 Ejemplo de Uso Real
El proyecto está configurado en `Main.java` para buscar **"Lente"**. Al ejecutar con el token de un vendedor que tenga lentes publicados, verás un reporte detallado con el precio promedio y el listado de ítems reales.
