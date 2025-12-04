# Contexto del Pipeline de Integración Continua (CI/CD) - FlagKit

## Descripción General
Este documento describe la infraestructura de Integración Continua (CI) y Despliegue Continuo (CD) utilizada en el proyecto **FlagKit**. El objetivo del pipeline es garantizar la calidad del código (análisis estático, cobertura de pruebas) y automatizar la publicación de la librería en el repositorio Nexus.

## Tecnologías y Herramientas

### 1. Orquestación y Ejecución
*   **GitHub Actions**: Plataforma principal de CI/CD. Define los flujos de trabajo (`workflows`) que se ejecutan ante eventos como `push` o `pull_request`.
*   **Docker**: Se utiliza para crear un entorno de construcción reproducible y aislado.
    *   **Dockerfile**: Define la imagen personalizada que contiene las herramientas necesarias (Android SDK, Java, Gradle, Ruby/Fastlane) para compilar y analizar el proyecto.
*   **Self-Hosted Runners**: Los jobs se ejecutan en runners propios (`runs-on: self-hosted`), lo que permite mayor control sobre el hardware y acceso a recursos internos (como el Nexus on-premise).

### 2. Automatización de Tareas
*   **Fastlane**: Herramienta utilizada para simplificar y automatizar las tareas de despliegue y testeo.
    *   **Fastfile**: Define las "lanes" (carriles) de ejecución, como `:scan_code` (para análisis) y `:publish_nexus` (para publicación).
    *   Abstrae la complejidad de los comandos de Gradle y gestiona parámetros como versiones y módulos.

### 3. Calidad de Código y Seguridad
*   **SonarQube**: Plataforma para el análisis estático de código y gestión de la deuda técnica.
    *   Se integra mediante el plugin de Gradle (`org.sonarqube`).
    *   Verifica métricas como duplicidad, complejidad ciclomática, bugs potenciales y vulnerabilidades.
*   **Kover (Kotlin Coverage)**: Plugin de Gradle (`org.jetbrains.kotlinx.kover`) utilizado para medir la cobertura de código de los tests unitarios. Genera reportes XML compatibles con JaCoCo que son consumidos por SonarQube.

### 4. Construcción y Gestión de Dependencias
*   **Gradle (Kotlin DSL)**: Sistema de construcción del proyecto.
    *   **build.gradle.kts**: Configuración de dependencias, plugins y tareas de compilación.
    *   **scripts/publisher.gradle.kts**: Script personalizado para configurar la publicación en Maven/Nexus.

### 5. Configuración y Secretos
*   **ci.properties**: Archivo local (no versionado o gestionado en el entorno) que contiene variables de configuración específicas del entorno de CI.
*   **GitHub Secrets**: Almacenamiento seguro de credenciales críticas como `SONAR_TOKEN`, `NEXUS_USER` y `NEXUS_PASSWORD`, que se inyectan como variables de entorno en los contenedores.

## Flujos de Trabajo (Workflows)

### 1. Code Quality Check (PR)
*   **Archivo**: `.github/workflows/pr-quality.yml` (o similar).
*   **Disparador**: `pull_request` hacia `main` o `develop`.
*   **Pasos Principales**:
    1.  **Setup**: Construcción de la imagen Docker y preparación del entorno.
    2.  **Quality Gate**:
        *   Ejecuta `fastlane scan_code`.
        *   Corre tests unitarios (`testDebugUnitTest`).
        *   Genera reporte de cobertura (`koverXmlReport`).
        *   Ejecuta análisis de SonarQube, excluyendo módulos irrelevantes (app).
        *   Falla si no se cumplen los umbrales de calidad definidos en SonarQube.

### 2. Publish Release (Main)
*   **Archivo**: `.github/workflows/main-release.yml`.
*   **Disparador**: `push` a la rama `main`.
*   **Pasos Principales**:
    1.  **Setup**: Similar al flujo de PR.
    2.  **Quality Gate**: Ejecuta el mismo análisis de calidad para asegurar que la versión a publicar es estable.
    3.  **Publish Release**:
        *   Ejecuta `fastlane publish_nexus`.
        *   Publica el artefacto (AAR/JAR) en el repositorio Nexus configurado.
        *   Utiliza la versión definida en el pipeline o `ci.properties`.

## Estructura de Archivos Relevantes

| Archivo / Directorio | Descripción |
| :--- | :--- |
| `.github/workflows/` | Definición de los pipelines de GitHub Actions. |
| `fastlane/Fastfile` | Scripts de automatización (Lanes) para test y deploy. |
| `Dockerfile` | Definición del entorno de construcción (Imagen Docker). |
| `ci.properties` | Propiedades de configuración del entorno de CI. |
| `build.gradle.kts` | Configuración de build y plugins (incluyendo Kover). |
| `scripts/publisher.gradle.kts` | Lógica de publicación a Maven/Nexus. |
