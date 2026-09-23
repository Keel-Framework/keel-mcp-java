<h1>
<img src="img/logo_keel_framework.png" width="480" alt="Keel">
</h1>

> **Reference Architecture & Maven Archetype for Building MCP Servers** · Java 25 · Spring Boot 4
---
![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?logo=springboot)
![Spring AI](https://img.shields.io/badge/Spring%20AI-MCP-6DB33F?logo=spring)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
---
## Introducción

⚓ **Keel-Framework/MCP** es la *quilla* sobre la que construyes tu servidor MCP (Model Context Protocol): una **arquitectura de referencia modular, estandarizada y organizada por capas**, diseñada para proporcionar una base técnica común para desarrollar servidores MCP sobre **Spring AI**.

> [!IMPORTANT]
> **Keel no es un wrapper de Spring AI ni un simple generador de proyectos.**
>
> **Spring AI** proporciona las capacidades y abstracciones necesarias para implementar MCP, mientras que **Keel** define una **arquitectura, un conjunto de módulos reutilizables y unas convenciones técnicas** para construir servidores MCP de forma estandarizada.

Keel encapsula capacidades técnicas transversales en **módulos independientes y componibles**, evitando que cada proyecto tenga que implementar y configurar las mismas soluciones de infraestructura.

El **Maven Archetype** forma parte de Keel como mecanismo de *scaffolding*, permitiendo generar nuevos MCP Servers que parten de esta arquitectura y de sus capacidades técnicas. De esta forma, el desarrollo puede centrarse en las capacidades funcionales del servidor: **Tools, Resources y Prompts**.

### 🧩 Capacidades proporcionadas

Entre las capacidades proporcionadas por Keel se incluyen:

- **Autenticación y seguridad** — validación de JWT contra un proveedor de identidad OIDC
- **Gestión de sesión MCP** — ciclo de vida del `Mcp-Session-Id`
- **Transporte Streamable HTTP** — endpoint de comunicación con los hosts MCP
- **Configuración de CORS** — para clientes basados en navegador
- **Caché local** — basada en Caffeine
- **Integración con servicios HTTP** — cliente REST preconfigurado para las Tools
- **Observabilidad** — logs estructurados

🚀 **Get started:** utiliza **"Use this template"** en GitHub o clona el repositorio para crear tu propio **MCP Server basado en Keel**.

---
## Índice

- [<img src="img/keel-icon.svg" height="16" alt=""> **Arquitectura de Keel**](#arquitectura-de-keel)
  - [Visión general](#visión-general)
  - [Keel en el ecosistema Spring AI MCP](#keel-en-el-ecosistema-spring-ai-mcp)
  - [Módulos del Framework](#módulos-del-framework)
  - [Estructura Maven del Framework](#estructura-maven-del-framework)
  - [Requisitos del Framework](#requisitos-del-framework)
  - [Stack tecnológico](#stack-tecnológico)
  
- [🧭 **Principios de Diseño y Desarrollo**](#principios-de-diseño-y-desarrollo)
  - [Introducción](#introducción)
  - [Principios Arquitectónicos](#principios-arquitectónicos)

- [🚀 **Quickstart & Scaffolding**](#quickstart--scaffolding)
  - [Opción A — Generar un MCP Server mediante el Installer JAR](#opción-a--generar-un-mcp-server-mediante-el-installer-jar)
    - [(1-2) Obtener el Installer JAR](#1-2-obtener-el-installer-jar)
    - [(2-2) Generar el Scaffolding](#2-2-generar-el-scaffolding)

  - [Opción B — Clonar y adaptar el Framework Keel](#opción-b--clonar-y-adaptar-el-framework-keel)
    - [(1-2) Clone/Build del Framework](#1-2-clonebuild-del-framework)
    - [(2-2) Generación del Scaffolding](#2-2-generación-del-scaffolding)

  - [Resultado de la Generación del Scaffolding](#resultado-de-la-generación-del-scaffolding)

  - [Ejecutar el Servidor MCP](#ejecutar-el-servidor-mcp)
    - [Resumen del Proceso de Arranque](#resumen-del-proceso-de-arranque)

  - [Métodos e interacción con el MCP Server](#métodos-e-interacción-con-el-mcp-server)
    - [Health / Info](#health--info)
    - [`initialize`](#initialize)
    - [`tools/list`](#toolslist)
    - [`tools/call`](#toolscall)
    - [`resources/list`](#resourceslist)
    - [`prompts/list`](#promptslist)
  
- [🏗️ **Developer MCP Server — Keel Scaffolding**](#developer-mcp-server--keel-scaffolding)
  - [Scaffolding Arquitectónico](#scaffolding-arquitectónico)
  - [Nomenclatura de los Módulos](#nomenclatura-de-los-módulos)
  - [Componentes del MCP Server Generado por el Archetype de Keel](#componentes-del-mcp-server-generado-por-el-archetype-de-keel)
  - [Configuración YAML del MCP Server](#configuración-yaml-del-mcp-server)
    - [(1-10) Configuración de la identidad de la aplicación](#1-10-configuración-de-la-identidad-de-la-aplicación)
    - [(2-10) Configuración del servidor MCP — Spring AI](#2-10-configuración-del-servidor-mcp--spring-ai)
    - [(3-10) Configuración del servidor HTTP y TLS](#3-10-configuración-del-servidor-http-y-tls)
    - [(4-10) Configuración del pool de conexiones HTTP](#4-10-configuración-del-pool-de-conexiones-http)
    - [(5-10) Configuración del adaptador REST](#5-10-configuración-del-adaptador-rest)
    - [(6-10) Configuración de la autenticación JWT del transporte](#6-10-configuración-de-la-autenticación-jwt-del-transporte)
    - [(7-10) Configuración de la gestión de sesión y transporte Streamable HTTP](#7-10-configuración-de-la-gestión-de-sesión-y-transporte-streamable-http)
    - [(8-10) Configuración de la observabilidad](#8-10-configuración-de-la-observabilidad)
    - [(9-10) Configuración de la gestión de la caché](#9-10-configuración-de-la-gestión-de-la-caché)
    - [(10-10) Configuración de Actuator y logging](#10-10-configuración-de-actuator-y-logging)
  - [Desarrollo de Tools MCP](#desarrollo-de-tools-mcp)
    - [`@Tool` — Implementación](#tool--implementación)
    - [Estructura estándar de una Tool (stack Spring AI)](#estructura-estándar-de-una-tool-stack-spring-ai)
    - [Obtener el RestClient en la Tool](#obtener-el-restclient-en-la-tool)
  - [Desarrollo de Prompts MCP](#desarrollo-de-prompts-mcp)
    - [`@Prompt` — Implementación](#prompt--implementación)
    - [Estructura estándar de un Prompt (stack Spring AI)](#estructura-estándar-de-un-prompt-stack-spring-ai)
  - [Desarrollo de Resources MCP](#desarrollo-de-resources-mcp)
    - [`@Resources` — Implementación](#resources--implementación)
    - [Estructura estándar de un Resource (stack Spring AI)](#estructura-estándar-de-un-resource-stack-spring-ai)

- [🤝 **Contribuir**](#contribuir)

- [📄 **Licencia**](#licencia)

---
## <img src="img/keel-icon.svg" height="24" alt=""> Arquitectura de Keel

La arquitectura de **Keel** define la estructura técnica sobre la que se construyen los MCP Servers generados a partir del arquetipo. Está organizada por capas y componentes, separando las capacidades funcionales de las capacidades técnicas transversales proporcionadas por Keel.

### Visión general

La arquitectura de **Keel** combina una **Reference Architecture**, capacidades funcionales y transversales reutilizables, y mecanismos de *build & scaffolding* para estandarizar el desarrollo de nuevos servidores MCP.

El siguiente diagrama muestra la **visión conceptual de Keel**, su relación con **Spring AI** como framework para la implementación de MCP y con **MCP (Model Context Protocol)** como protocolo de comunicación, así como el papel de **Maven** en el *build* y *scaffolding* de los proyectos.

![Keel Ecosystem](/img/Keel_Ecosystem_Architecture_v1.png)

### Keel en el ecosistema Spring AI MCP

Un **MCP Server** basado en Keel se construye sobre el stack tecnológico Java / Spring Boot, utilizando Spring AI como framework para la implementación de las capacidades de MCP.

![Spring AI](/img/stack_spring_ai_v1.png)

### Módulos del Framework

El framework **Keel** está compuesto por un conjunto de módulos independientes, organizados según su responsabilidad arquitectónica. Cada módulo encapsula una capacidad técnica específica y puede evolucionar de forma independiente, manteniendo contratos y dependencias claramente definidos.

Los módulos se organizan principalmente en las siguientes áreas:

- **Common** — capacidades y componentes técnicos comunes.
- **Adapters** — integraciones con servicios y componentes externos.
- **Transport** — capacidades relacionadas con la seguridad, sesion/autenticacion, protocolo de transporte y la comunicación client x MCP Server.
- **Observability** — capacidades relacionadas con observabilidad y logging.

Esta organización permite mantener una **clara separación de responsabilidades**, reducir el acoplamiento entre componentes y facilitar la evolución, reutilización y composición de las capacidades proporcionadas por Keel.

El framework se apoya, además, en **dependencias de terceros**, como **Spring AI, Spring Boot y Caffeine**, cuya gestión de versiones y compatibilidad se centraliza mediante el **Maven BOM (Bill of Materials)** del proyecto.

Todos los módulos comparten el **Group ID** `io.keelframework.mcp` y la **versión definida a nivel de proyecto**, garantizando una gestión de dependencias consistente dentro del stack.

![Modulos](./img/keelFramework_components.png)

| **Módulo** | **Artefacto**                   | **Responsabilidad / Propósito** |
|---|---------------------------------|---|
| 🏗️ **Parent / BOM** | `keel-mcp-java`                 | Gestión centralizada de **dependencias, versiones, plugins y propiedades comunes** del framework. |
| 🔐 **Common · JWT** | `keel-common-jwt`                    | Decodificación de **JWT**, verificación de firma y extracción de claims. |
| ⚠️ **Common · Exceptions** | `keel-common-exceptions`             | Excepciones base y modelo de errores compartido por los distintos módulos del framework, incluyendo `McpToolException`. |
| 🔑 **Adapter · Auth IdP** | `keel-adapter-auth-idp`              | Integración con proveedores de identidad mediante **OIDC**, incluyendo **Red Hat SSO / Keycloak**, JWKS e introspección de tokens. |
| ⚡ **Adapter · Cache** | `keel-adapter-cache-caffeine`        | Abstracción de caché local basada en **Caffeine**, con configuración de TTL y tamaño máximo. |
| 🌐 **Adapter · REST Client** | `keel-adapter-rest-client`           | Cliente HTTP estandarizado y preconfigurado para el consumo de **APIs REST externas** desde las Tools MCP. |
| 🛡️ **Transport · Auth** | `keel-transport-auth`           | Filtro Servlet responsable de aplicar **autenticación** sobre los endpoints MCP. |
| 🔄 **Transport · Session** | `keel-transport-session`        | Gestión del contexto de sesión de transporte, incluyendo autenticación y `Mcp-Session-Id`, con mecanismos de **creación, validación y expiración**. |
| 📡 **Transport · Streamable HTTP** | `keel-transport-streamable-mvc` | Implementación del transporte MCP basado en **Streamable HTTP**, proporcionando el endpoint de comunicación entre clientes y servidores MCP. |
| 🌍 **Transport · CORS** | `keel-transport-cors`                | Configuración y gestión de **CORS** para clientes MCP basados en navegador. |
| 📊 **Observability** | `keel-observability-logging`         | Generación y estandarización de **logs estructurados** para facilitar la monitorización y trazabilidad del servidor MCP. |


### Estructura Maven del Framework

La estructura Maven de **Keel** organiza los diferentes componentes del framework en módulos independientes, agrupados por responsabilidad arquitectónica.
El proyecto se estructura a partir de un **POM padre / BOM**, que centraliza la gestión de dependencias, versiones, plugins y propiedades comunes, mientras que los módulos se agrupan en cuatro áreas principales: **Common**, **Adapters**, **Transport** y **Observability**.

```text
keel-mcp-java/
    │
    ├── keel-mcp-common/
    │   ├── keel-common-jwt/
    │   └── keel-common-exceptions/
    │
    ├── keel-mcp-adapters/
    │   ├── keel-adapter-auth-idp/
    │   ├── keel-adapter-cache-caffeine/
    │   └── keel-adapter-rest-client/
    │
    ├── keel-mcp-archetype/
    │
    ├── keel-mcp-archetype-installer/
    │
    ├── keel-mcp-transport/
    │   ├── keel-transport-auth/
    │   ├── keel-transport-cors/
    │   ├── keel-transport-session/
    │   └── keel-transport-streamable-mvc/
    │
    └── keel-mcp-observability/
    │   └── keel-observability-logging/
    │
    └── README._en.md
    │
    └── README._es.md
    │   
    └── pom.xml   
 
        
        
```

### Requisitos del Framework

Para utilizar **Keel**, generar nuevos **MCP Servers** y compilar los módulos del framework, el entorno de desarrollo debe cumplir con los siguientes requisitos y versiones de referencia.

Los requisitos definidos a continuación corresponden a las tecnologías base sobre las que se construye Keel. Las dependencias específicas del framework, incluyendo **Spring Boot, Spring AI y sus dependencias transitivas**, son gestionadas y versionadas de forma centralizada mediante el **Maven BOM** del proyecto.


| 🏷️ **Categoría** | 🔧 **Requisito** | 📌 **Versión / Detalle** |
|---|---|---|
| ☕ **JDK** | Java Development Kit | **JDK 25** |
| 🌱 **Framework** | Spring Boot | **4.0.6** |
| 🔌 **MCP** | Spring AI MCP Server `spring-ai-mcp-server-webmvc` | **2.0.0-M6** |
| 📦 **Build** | Maven | **Maven 3.9+** |

### Stack tecnológico

La arquitectura de **Keel** se construye sobre un stack tecnológico moderno basado en **Java 25**, **Spring Boot 4** y **Spring AI**, utilizando **MCP (Model Context Protocol)** como estándar de comunicación entre clientes y servidores MCP.

**Spring AI** proporciona las abstracciones necesarias para implementar las capacidades MCP de forma nativa mediante anotaciones como `@Tool`, `@Resource` y `@Prompt`, mientras que **Keel** aporta la arquitectura, los módulos reutilizables y las capacidades transversales necesarias para estandarizar la construcción de MCP Servers.

La siguiente tabla resume las principales tecnologías y componentes que forman parte del stack de Keel:

| **Capa** | **Tecnología / Componente**                      | **Responsabilidad** |
|---|--------------------------------------------------|---|
| ☕ **Runtime** | **JDK 25** · Virtual Threads                     | Runtime y modelo de concurrencia para las operaciones del servidor y las comunicaciones **Streamable HTTP**. |
| 🌱 **Framework** | **Spring Framework 7.x** · Spring Boot **4.0.6** | IoC/DI, autoconfiguración, configuración y ciclo de vida de la aplicación. |
| 🤖 **MCP Framework** | **Spring AI 2.0.0-M6**                           | Proporciona las abstracciones para implementar y registrar **Tools, Resources y Prompts**. |
| 🔌 **Protocol** | **Model Context Protocol (MCP)**                 | Estándar de interacción entre clientes y servidores MCP. |
| 🌐 **Transport** | **Streamable HTTP**                              | Mecanismo de transporte HTTP utilizado para la comunicación entre clientes y servidores MCP. |
| 🔐 **Security** | **Spring Security** · JWT · OIDC / JWKS | Autenticación y autorización de peticiones mediante tokens JWT y validación de identidad con OIDC / JWKS. |
| 🔄 **Session** | **MCP Session Management**                       | Gestión del contexto técnico asociado a las sesiones de transporte MCP. |
| ⚡ **Caching** | **Caffeine**                                     | Caché local para reducir accesos repetitivos a sistemas y servicios externos. |
| 📊 **Observability** | **JSON Structured Logging**                      | Estandarización de eventos **TECHNICAL**, **FUNCTIONAL** y **SECURITY**. |
| 🏗️ **Scaffolding** | **Maven Archetype**                              | Generación de proyectos MCP basados en la arquitectura y módulos de Keel. |

---
## 🧭 Principios de Diseño y Desarrollo

Esta sección define los principios fundamentales que deben guiar el diseño, desarrollo y evolución de los MCP Servers construidos sobre la arquitectura Keel/MCP.

Estos principios establecen las responsabilidades, límites y criterios arquitectónicos que debe respetar un MCP Server para integrarse de forma consistente con el resto de la plataforma. Su objetivo es garantizar una arquitectura homogénea, facilitar la evolución de las soluciones y mantener una clara separación de responsabilidades entre el MCP Server y las diferentes capas de la arquitectura.

Se han definido seis principios fundamentales que deben considerarse durante las fases de análisis, diseño e implementación de cualquier MCP Server basado en Keel.

Estos principios deben entenderse como directrices arquitectónicas, y no únicamente como recomendaciones de desarrollo. Su cumplimiento permite mantener el MCP Server dentro de su ámbito de responsabilidad, evitando incorporar capacidades que pertenecen a otras capas de la arquitectura, como los servicios de negocio, orquestadores o API Gateway.

En conjunto, estos principios delimitan claramente el alcance y las responsabilidades de un MCP Server, estableciendo dónde termina su responsabilidad y dónde comienza la del resto de la plataforma.

### Principios Arquitectónicos

Los siguientes seis principios constituyen la guía de referencia para el diseño y desarrollo de cualquier MCP Server dentro de la arquitectura Keel/MCP.

![PrincipioDevMCP](./img/principios-desarrollo.png)

---

## 🚀 Quickstart & Scaffolding

Keel proporciona **dos alternativas para comenzar el desarrollo de un nuevo MCP Server**, dependiendo del nivel de personalización requerido:

* **🚀 **Installer JAR** — Recommended:** genera rápidamente un nuevo MCP Server a partir de la arquitectura estándar de Keel, sin necesidad de clonar ni compilar el código fuente del framework.
* 🏗️ **Framework Source:** permite clonar el código fuente de Keel, estudiar la arquitectura y adaptar o extender sus módulos según las necesidades específicas del proyecto.

### 🚀 Opción A — Generar un MCP Server mediante el Installer JAR

Esta es la opción recomendada para los equipos de desarrollo que desean **utilizar la arquitectura estándar de Keel** y comenzar rápidamente la implementación de las capacidades funcionales del MCP Server.

El instalador `keel-mcp-archetype-installer-X.X.X.jar` encapsula los componentes necesarios para generar automáticamente el **scaffolding inicial** del proyecto.

#### (1-2) Obtener el Installer JAR

Descarga el artefacto:

```text
keel-mcp-archetype-installer-X.X.X.jar
```

#### (2-2) Generar el Scaffolding

Ejecuta el instalador proporcionando el nombre, versión, dominio y groupId del nuevo MCP Server:

```bash
java -jar keel-mcp-archetype-installer-X.X.X.jar <nombre-mcp> <version-micro> <dominio-negocio> <groupId>
```

| **Parámetro**     | **Descripción**                                            |
|-------------------| ---------------------------------------------------------- |
| `nombre-mcp`      | Nombre del proyecto MCP que se generará.                   |
| `version-micro`   | Versión inicial del proyecto generado.                     |
| `dominio-negocio` | Dominio o área funcional a la que pertenece el MCP Server. |
| `groupId`         | GroupId Maven del proyecto generado (tu propio namespace en dominio inverso). |


Por ejemplo:

```bash
$ java -jar keel-mcp-archetype-installer-1.0.0.jar mcp-petstore-sample 1.0.0 petstore io.keelframework.sample

Downloaded from central: https://repo.maven.apache.org/maven2/archetype-catalog.xml (18 MB at 9.6 MB/s)
[INFO] Archetype repository not defined. Using the one from [io.keelframework.mcp:keel-mcp-archetype:1.0.0] found in catalog local
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: keel-mcp-archetype:1.0.0
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: io.keelframework.sample
[INFO] Parameter: artifactId, Value: mcp-petstore-sample
[INFO] Parameter: version, Value: 1.0.0
[INFO] Parameter: package, Value: io.keelframework.sample
[INFO] Parameter: packageInPathFormat, Value: io/keelframework/sample
[INFO] Parameter: package, Value: io.keelframework.sample
[INFO] Parameter: micro, Value: mcppetstoresample
[INFO] Parameter: domain, Value: petstore
[INFO] Parameter: domainName, Value: petstore
[INFO] Parameter: groupId, Value: io.keelframework.sample
[INFO] Parameter: artifactId, Value: mcp-petstore-sample
[INFO] Parameter: transport, Value: mvc
[INFO] Parameter: version, Value: 1.0.0
[INFO] Parameter: microName, Value: mcp-petstore-sample
[INFO] Parameter: architectureVersion, Value: 1.0.0
[INFO] Project created from Archetype in dir: \keel-testing\mcp-petstore-sample
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.402 s
[INFO] ------------------------------------------------------------------------
```


El instalador genera automáticamente la estructura base del **MCP Server**, incluyendo los módulos `_boot`, `_mcp`, `_model` y la configuración necesaria para comenzar el desarrollo.

> [!NOTE]
> El **Installer JAR** está diseñado como mecanismo de *scaffolding*. Su objetivo es proporcionar una forma rápida y estandarizada de iniciar nuevos proyectos MCP Server basados en la arquitectura de Keel.

### 🏗️ Opción B — Clonar y adaptar el Framework Keel

Esta opción está orientada a **arquitectos y equipos que necesitan conocer, personalizar o extender la arquitectura de Keel**.

El código fuente del framework permite acceder directamente a los módulos que componen la arquitectura, incluyendo:

* `common-*`
* `adapter-*`
* `transport-*`
* `observability-*`

#### (1-2) Clone/Build del Framework

```bash
# Clone
git clone https://github.com/Keel-Framework/keel-mcp-java.git
# Navega hasta el framework y compila sus módulos:
cd keel-mcp-java
# Build:
mvn clean install -U
```
#### (2-2) Generación del Scaffolding

Para generar un nuevo **scaffolding de Keel para un MCP Server**, se requiere previamente el siguiente artefacto:
`keel-mcp-archetype-installer-X.X.X.jar`

```bash
# Ejecuta el instalador desde directory proyecto   
java -jar  keel-mcp-archetype-installer-X.X.X.jar <nombre-mcp> <version-micro> <dominio-negocio> 
```
Por ejemplo:

```bash
java -jar keel-mcp-archetype-installer-1.0.0.jar mcp-petstore-sample 1.0.0 petstore io.keelframework.sample
```

### Resultado de la Generación del Scaffolding

Una vez ejecutado el Keel Installer, el proceso de generación finaliza correctamente y crea el nuevo MCP Server en el directorio especificado.

La siguiente salida muestra un ejemplo de ejecución del Maven Archetype, incluyendo los parámetros utilizados durante la generación y la confirmación de la creación del proyecto mediante BUILD SUCCESS.


```bash
Downloaded from central: https://repo.maven.apache.org/maven2/archetype-catalog.xml (18 MB at 9.6 MB/s)
[INFO] Archetype repository not defined. Using the one from [io.keelframework.mcp:keel-mcp-archetype:1.0.0] found in catalog local
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: keel-mcp-archetype:1.0.0
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: io.keelframework.sample
[INFO] Parameter: artifactId, Value: mcp-petstore-sample
[INFO] Parameter: version, Value: 1.0.0
[INFO] Parameter: package, Value: io.keelframework.sample
[INFO] Parameter: packageInPathFormat, Value: io/keelframework/sample
[INFO] Parameter: package, Value: io.keelframework.sample
[INFO] Parameter: micro, Value: mcppetstoresample
[INFO] Parameter: domain, Value: petstore
[INFO] Parameter: domainName, Value: petstore
[INFO] Parameter: groupId, Value: io.keelframework.sample
[INFO] Parameter: artifactId, Value: mcp-petstore-sample
[INFO] Parameter: transport, Value: mvc
[INFO] Parameter: version, Value: 1.0.0
[INFO] Parameter: microName, Value: mcp-petstore-sample
[INFO] Parameter: architectureVersion, Value: 1.0.0
[INFO] Project created from Archetype in dir: \keel-testing\mcp-petstore-sample
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.402 s
[INFO] Finished at: 2026-09-23T15:41:39+02:00
[INFO] ------------------------------------------------------------------------
```
El resultado es un nuevo proyecto MCP Server basado en la arquitectura de Keel, preparado para continuar con la configuración y el desarrollo de sus capacidades MCP.

### Ejecutar el Servidor MCP

Independientemente de la opción utilizada para obtener el scaffolding, una vez generado el proyecto:

```bash
# Navigate to the project root directory 
cd <directory-root-install-project-mcp>
```
```bash
# Build the project
mvn clean install -U

[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for mcp-petstore-sample 1.0.0:
[INFO]
[INFO] mcp-petstore-sample ................................ SUCCESS [  2.143 s]
[INFO] mcp-petstore-sample-model .......................... SUCCESS [ 13.131 s]
[INFO] mcp-petstore-sample-mcp ............................ SUCCESS [  9.044 s]
[INFO] mcp-petstore-sample-boot ........................... SUCCESS [ 15.537 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  40.382 s
[INFO] Finished at: 2026-09-23T16:09:10+02:00
[INFO] ------------------------------------------------------------------------

```
```bash
# Navigate to the Spring Boot Module
cd <directory-root-install-project-mcp>/-boot
```
```bash
# Run the application
\mcp-petstore-sample-boot
mvn spring-boot:run

[INFO] Attaching agents: []
INFO] Attaching agents: []

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.6)

2026-09-23 16:13:49.918 INFO  [main] io.keelframework.sample.Application - The following 1 profile is active: "local"
2026-09-23 16:13:50.959 INFO  [main] org.springframework.cloud.context.scope.GenericScope - BeanFactory id=8bec6352-6229-3720-a6b5-fe2cede6c401
2026-09-23 16:13:51.248 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'org.springframework.ai.mcp.server.common.autoconfigure.annotations.McpServerAnnotationScannerAutoConfiguration' of type [org.springframework.ai.mcp.server.common.autoconfigure.annotations.McpServerAnnotationScannerAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.252 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'serverAnnotatedBeanRegistry' of type [org.springframework.ai.mcp.server.common.autoconfigure.annotations.McpServerAnnotationScannerAutoConfiguration$ServerMcpAnnotatedBeans] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.255 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration' of type [org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.258 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration' of type [org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.260 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'loadBalancerClientsDefaultsMappingsProvider' of type [org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration$$Lambda/0x000000009f4ffaf8] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.262 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'defaultsBindHandlerAdvisor' of type [org.springframework.cloud.commons.config.DefaultsBindHandlerAdvisor] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.265 WARN  [main] org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'spring.ai.mcp.server.annotation-scanner-org.springframework.ai.mcp.server.common.autoconfigure.annotations.McpServerAnnotationScannerProperties' of type [org.springframework.ai.mcp.server.common.autoconfigure.annotations.McpServerAnnotationScannerProperties] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [serverAnnotatedMethodBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.
2026-09-23 16:13:51.630 INFO  [main] org.springframework.boot.tomcat.TomcatWebServer - Tomcat initialized with port 8080 (http)
2026-09-23 16:13:51.649 INFO  [main] org.apache.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8080"]
2026-09-23 16:13:51.652 INFO  [main] org.apache.catalina.core.StandardService - Starting service [Tomcat]
2026-09-23 16:13:51.652 INFO  [main] org.apache.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/11.0.21]
2026-09-23 16:13:51.743 INFO  [main] org.springframework.boot.web.context.servlet.WebApplicationContextInitializer - Root WebApplicationContext: initialization completed in 1807 ms
2026-09-23 16:13:52.157 INFO  [main] io.keelframework.mcp.adapters.rest.client.McpRestClientFactory - REST CLIENT FACTORY initialized socketTimeout=60000ms connectTimeout=60000ms requestTimeout=60000ms maxTotal=200 maxPerRoute=20 keepAlive=10000ms ssl=disabled
2026-09-23 16:13:52.848 INFO  [main] org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration - Enable tools capabilities, notification: true
2026-09-23 16:13:52.951 WARN  [main] org.springframework.ai.mcp.annotation.provider.tool.SyncMcpToolProvider - No tool methods found in the provided tool objects: []
2026-09-23 16:13:52.954 INFO  [main] org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration - Registered tools: 1
2026-09-23 16:13:52.954 INFO  [main] org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration - Enable resources capabilities, notification: true
2026-09-23 16:13:52.957 INFO  [main] org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration - Enable resources templates capabilities, notification: true
2026-09-23 16:13:52.960 INFO  [main] org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration - Enable prompts capabilities, notification: true
2026-09-23 16:13:52.963 INFO  [main] org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration - Enable completions capabilities
2026-09-23 16:13:53.244 WARN  [main] org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration -

Using generated security password: 50932139-003d-488f-ac7c-08b197af0673

This generated password is for development use only. Your security configuration must be updated before running your application in production.

2026-09-23 16:13:53.291 INFO  [main] org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer$InitializeUserDetailsManagerConfigurer - Global AuthenticationManager configured with UserDetailsService bean with name inMemoryUserDetailsManager
2026-09-23 16:13:53.450 INFO  [main] org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver - Exposing 3 endpoints beneath base path '/actuator'
2026-09-23 16:13:53.631 INFO  [main] org.apache.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8080"]
2026-09-23 16:13:53.672 INFO  [main] org.springframework.boot.tomcat.TomcatWebServer - Tomcat started on port 8080 (http) with context path '/'
2026-09-23 16:13:53.695 INFO  [main] io.keelframework.sample.Application - Started Application in 5.106 seconds (process running for 6.176)
```

#### Resumen del Proceso de Arranque

La aplicación debe iniciar correctamente y mostrar en los logs la inicialización satisfactoria del **Spring Boot Application Context** y de los componentes base de **Keel/MCP**, sin errores relacionados con la configuración, resolución de dependencias o inicialización de los módulos del framework.

Como referencia, durante el arranque deben poder identificarse las siguientes evidencias:

| **Validación** | **Evidencia esperada** |
|---|---|
| ✅ **Spring Local Profile** | `profile is active: "local"` |
| ✅ **JKS / SSL Truststore** | `SSL TRUSTSTORE loading path=classpath:user-truststore.jks type=JKS` |
| ✅ **Tomcat Instance** | `Tomcat initialized with port 8080 (http)` |
| ✅ **REST Client Initialization** | `REST CLIENT FACTORY initialized socketTimeout=60000ms connectTimeout=60000ms requestTimeout=60000ms maxTotal=200 maxPerRoute=20 keepAlive=10000ms ssl=enabled` |
| ✅ **Actuator** | `Exposing 3 endpoints beneath base path '/actuator'` |
| ✅ **MCP Capabilities** | `Enable tools capabilities, notification: true` |
| ✅ **Tools Registration** | `Registered tools: 1` |
| ✅ **Application Started** | `Started Application in 12.536 seconds` |


El endpoint del servidor MCP queda disponible en `http://localhost:8080/mcp`

### Métodos e interacción con el MCP Server

Un servidor MCP basado en **Keel** proporciona los mecanismos necesarios para interactuar con las capacidades estándar del protocolo **MCP**, desde la inicialización de la conexión hasta el descubrimiento de **Tools, Resources y Prompts**.

En esta sección se detallan los principales **métodos MCP**, su propósito y los ejemplos de invocación correspondientes.

| **Método MCP** | **Propósito** | **Invocación** |
|---|---|---|
| ❤️ `health` | Verificar la disponibilidad y el estado del MCP Server. | `GET /health` |
| 🚀 `initialize` | Inicializar la sesión MCP y establecer las capacidades soportadas por el cliente y el servidor. | `POST /mcp` |
| 🧩 `tools/list` | Obtener la lista de **Tools** disponibles en el MCP Server. | `POST /mcp` |
| ⚙️ `tools/call` | Invocar una **Tool** específica del MCP Server. | `POST /mcp` |
| 📚 `resources/list` | Obtener la lista de **Resources** disponibles. | `POST /mcp` |
| 📖 `resources/read` | Leer el contenido de un **Resource** específico. | `POST /mcp` |
| 💬 `prompts/list` | Obtener la lista de **Prompts** disponibles. | `POST /mcp` |
| 📝 `prompts/get` | Obtener un **Prompt** específico y sus mensajes asociados. | `POST /mcp` |
---

#### Health / Info

**Finalidad:** Verificar el estado y disponibilidad del MCP Server, proporcionando información básica sobre la aplicación y su ejecución.

| **Method** | **Endpoint** |
|------------|---|
| `GET`      | `http://localhost:8080/actuator/health` |
| `GET`      | `http://localhost:8080/actuator/info` |


#### `initialize` 

**Finalidad:** Inicializar la sesión MCP, permitiendo que el cliente y el servidor intercambien información sobre sus **capacidades, versión del protocolo e información de identificación** antes de comenzar la interacción.


| **Method** | **Endpoint** |
|---|---|
| `POST` | `http://localhost:8080/mcp` |

**Headers:**

| **Header** | **Value** |
|---|---|
| `Content-Type` | `application/json` |
| `Accept` | `text/event-stream, application/json` |
| `Authorization` | `Bearer <jwt>` |

**Request Body:**

```json
{
  "jsonrpc": "2.0",
  "method": "initialize",
  "id": 1,
  "params": {
    "protocolVersion": "2025-03-26",
    "capabilities": {},
    "clientInfo": {
      "name": "test",
      "version": "1.0"
    }
  }
}
```
**Response Header:**
```
mcp-session-id	: 6ad4267c-1694-4718-9ebe-828d18b7076a
```


**Response Body:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "protocolVersion": "2025-03-26",
    "capabilities": {
      "completions": {},
      "logging": {},
      "prompts": {
        "listChanged": true
      },
      "resources": {
        "subscribe": false,
        "listChanged": true
      },
      "tools": {
        "listChanged": true
      }
    },
    "serverInfo": {
      "name": "mcp-poc",
      "version": "1.0.0"
    }
  }
}
```

#### `tools/list`

**Finalidad:** Obtener la lista de **Tools** disponibles en el MCP Server, incluyendo su nombre, descripción y esquema de entrada, permitiendo al cliente conocer qué capacidades puede invocar.

| **Method** | **Endpoint** |
|---|---|
| `POST` | `http://localhost:8080/mcp` |

**Headers:**

| **Header** | **Value**                            |
|---|--------------------------------------|
| `Content-Type` | `application/json`                   |
| `Accept` | `text/event-stream, application/json` |
| `Authorization` | `Bearer <jwt>`                       |
| `Mcp-Session-Id` | `<session-id>`                       |


**Request Body:**

```json
{
  "jsonrpc": "2.0",
  "method": "tools/list",
  "id": 2
}
```
**Response Body:**
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "tools": [
    ]
  }
}
```

#### `tools/call`

**Finalidad:** Invocar una **Tool específica** del MCP Server, proporcionando los parámetros de entrada necesarios para ejecutar la operación solicitada.


| **Method** | **Endpoint** |
|---|---|
| `POST` | `http://localhost:8080/mcp` |

**Headers:**

| **Header** | **Value**                            |
|---|--------------------------------------|
| `Content-Type` | `application/json`                   |
| `Accept` | `text/event-stream, application/json` |
| `Authorization` | `Bearer <jwt>`                       |
| `Mcp-Session-Id` | `<session-id>`                       |

**Request Body:**

```json
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "id": 6,
  "params": {
    "name": "suma",
    "arguments": {
      "a": 1,
      "b": 1
    }
  }
}
```

**Response Body:**
```json
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "id": 6,
  "params": {
    "name": "suma",
    "arguments": {
      "a": 1,
      "b": 1
    }
  }
}
```

#### `resources/list`

**Finalidad:** Obtener la lista de **Resources** disponibles en el MCP Server, permitiendo al cliente descubrir los recursos que puede consultar.

| **Method** | **Endpoint** |
|---|---|
| `POST` | `http://localhost:8080/mcp` |

**Headers:**

| **Header** | **Value**                            |
|---|--------------------------------------|
| `Content-Type` | `application/json`                   |
| `Accept` | `text/event-stream, application/json` |
| `Authorization` | `Bearer <jwt>`                       |
| `Mcp-Session-Id` | `<session-id>`                       |

**Request Body:**

```json
{
  "jsonrpc": "2.0",
  "method": "resources/list",
  "id": 1
}
```

**Response Body:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "resources": [
    ]
  }
}
```


#### `prompts/list`

**Finalidad:** Obtener la lista de **Prompts** disponibles en el MCP Server, permitiendo al cliente descubrir los prompts que puede utilizar.

| **Method** | **Endpoint** |
|---|---|
| `POST` | `http://localhost:8080/mcp` |

**Headers:**

| **Header** | **Value**                            |
|---|--------------------------------------|
| `Content-Type` | `application/json`                   |
| `Accept` | `text/event-stream, application/json` |
| `Authorization` | `Bearer <jwt>`                       |
| `Mcp-Session-Id` | `<session-id>`                       |

**Request Body:**

```json
{
  "jsonrpc": "2.0",
  "method": "prompts/list",
  "id": 1
}
```
**Response Body:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "prompts": []
  }
}
```
---

## 🏗️ Developer MCP Server — Keel Scaffolding

Esta sección describe cómo Keel materializa su arquitectura de referencia en un MCP Server generado mediante scaffolding, proporcionando una estructura inicial estandarizada sobre la que los equipos pueden desarrollar sus capacidades funcionales.

El scaffolding combina la estructura arquitectónica, los módulos técnicos reutilizables y las convenciones de desarrollo definidas por Keel, proporcionando una base común para la construcción de nuevos MCP Servers.

### Scaffolding Arquitectónico

Desde Keel-Framework/MCP proponemos un scaffolding base para MCP Servers, que integra los starters y módulos fundamentales que conforman la arquitectura de Keel.

Este scaffolding proporciona una estructura inicial estandarizada y preconfigurada, permitiendo a los equipos de desarrollo centrarse desde el inicio en la implementación de la lógica de dominio, al tiempo que garantiza la integración con las capacidades técnicas y transversales comunes proporcionadas por Keel.

El scaffolding genera una estructura de módulos y paquetes estandarizada y organizada por responsabilidades, proporcionando la base arquitectónica sobre la que se implementará el MCP Server.

Cada módulo encapsula un ámbito técnico o funcional específico, favoreciendo la separación de responsabilidades, el bajo acoplamiento y la evolución independiente de los componentes.
| **Módulo / Carpeta** | **Tipo** | **Responsabilidad** | **Contenido** |
|---|---|---|---|
| `_boot` | Módulo Maven | **Arranque y configuración del servidor** | Módulo ejecutable que contiene `Application.java`, `bootstrap.yml`, `application*.yml` y la configuración necesaria para el arranque de la aplicación. Puede empaquetarse como WAR desplegable mediante `ServletInitializer` o ejecutarse localmente como aplicación Spring Boot. |
| `_mcp` | Módulo Maven | **Implementación de las capacidades MCP** | Contiene los componentes relacionados con el MCP Server, incluyendo **Tools, Resources y Prompts**, así como la lógica funcional expuesta mediante el protocolo MCP y los componentes necesarios para su integración con el framework. |
| `_model` | Módulo Maven | **Modelo de dominio** | Contiene modelos, DTOs, entidades y *mappers* mediante **MapStruct**, así como las estructuras de datos y contratos utilizados por los diferentes módulos del proyecto. |

**Ejemplo de Proyecto Generado por el scaffolding:**

El siguiente ejemplo muestra la estructura de un **MCP Server generado mediante el Maven Archetype de Keel**.

![Modulos](./img/scaffolding_package.png)

### Nomenclatura de los Módulos

Los módulos estándar definidos en el **Maven Archetype de Keel** utilizan identificadores base (`_boot`, `_mcp` y `_model`) que se renombran automáticamente durante el proceso de generación del proyecto, incorporando el **nombre del proyecto** y manteniendo el **literal estándar definido por la arquitectura**.

Por ejemplo, para un proyecto denominado:

- **Nombre del proyecto:** `keel-mcp-petstore-sample`

Los módulos generados serán:

- `petstore-sample-boot`
- `petstore-sample-mcp`
- `petstore-sample-model`

De esta forma, se mantiene una **nomenclatura homogénea y estandarizada** para todos los proyectos generados a partir del **Maven Archetype de Keel**.

### Componentes del MCP Server Generado por el Archetype de Keel

El **Maven Archetype de Keel** genera una estructura base de **MCP Server** preparada para comenzar el desarrollo de las capacidades funcionales del proyecto.

La estructura generada está organizada mediante una **separación clara de responsabilidades**, diferenciando los componentes relacionados con el **arranque de la aplicación**, la **implementación de las capacidades MCP**, el **modelo de dominio** y la **configuración de los procesos de CI/CD**.

Esta organización proporciona una base común para los MCP Servers generados mediante Keel, facilitando la **estandarización de la estructura del proyecto**, el **bajo acoplamiento entre módulos** y la **evolución independiente de sus componentes**.

Sobre esta estructura base, el equipo de desarrollo puede incorporar la lógica específica del dominio y extender las capacidades MCP mediante **Tools, Resources y Prompts**, manteniendo separadas las responsabilidades funcionales de las capacidades técnicas proporcionadas por **Keel**.

La siguiente estructura muestra los principales módulos, paquetes y ficheros generados por el Archetype

**Scaffolding base de estructura de paquetes:**
```text
<name-project-mcp>/
├── Dockerfile
├── pom.xml
│
├── _boot/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── Application.java
│           │
│           └── resources/
│               ├── application.yml
│               ├── application-dev.yml
│               ├── application-pre.yml
│               ├── application-pro.yml
│               ├── bootstrap.yml
│               └──user-truststore.jks
│
├── _mcp/
│   ├── pom.xml
│   ├── prompts/
│   │   └── Prompts.java
│   ├── resources/
│   │   └── Resources.java
│   └── tools/
│       └── Tools.java
│
├── _model/
│   ├── pom.xml
│   ├── dto/
│   │   └── HelloWorldItemDTO.java
│   ├── entity/
│   │   └── HelloWorldItemEntity.java
│   ├── errors/
│   │   └── ExemploErrorsMsg.java
│   └── mapper/
│       └── HelloWorldItemMapper.java
│
```  
### Configuración YAML del MCP Server

La configuración del **MCP Server** generado por **Keel** se centraliza principalmente en el fichero `application.yml`, ubicado en el módulo `_boot`, dentro de `src/main/resources`.

Este fichero contiene la configuración principal de la aplicación y permite definir los parámetros necesarios para el arranque y funcionamiento del servidor en los diferentes entornos de ejecución.

Esta sección está dividida en **10 aspectos técnicos de configuración** que describen el propósito de cada bloque del **MCP Server generado por Keel**, proporcionando el contexto necesario para comprender qué controla cada propiedad antes de modificarla.

#### (1-9) Configuración de la Identidad de la aplicación

La configuración del bloque `spring` define la identidad y el comportamiento base de la aplicación Spring Boot.

```yaml
spring:
  threads:
    virtual:
      enabled: true
  main:
    web-application-type: servlet
```
| Propiedad | Descripción |
|---|---|
| `spring.threads.virtual.enabled` | Activa *Virtual Threads* (Java 21+). Cada request y cada invocación de Tool se ejecuta en un hilo virtual ligero, lo que mejora la escalabilidad bajo I/O intensivo (llamadas REST a backends) sin necesitar pools grandes de hilos de plataforma. |
| `spring.main.web-application-type` | `servlet` fuerza el stack Servlet (Tomcat / WebMVC), coherente con el transporte Streamable HTTP síncrono. |

#### (2-9) Configuración del Servidor MCP (Spring AI)

El bloque `spring.ai.mcp.server` configura el comportamiento del servidor MCP y define el tipo de servidor, el protocolo de transporte y los mecanismos utilizados para registrar las capacidades MCP.

```yaml
spring:
  ai:
    mcp:
      server:
        type: SYNC
        protocol: STREAMABLE
        stdio:
          enabled: false
        streamable-http:
          mcp-endpoint: /mcp
          keep-alive-interval: 30s
        annotation-scanner:
          enabled: true
```
| Propiedad | Descripción |
|---|---|
| `type: SYNC` | El servidor MCP opera en modo síncrono (`McpSyncServer`), no reactivo. |
| `protocol: STREAMABLE` | Usa el transporte *Streamable HTTP*, el recomendado por la especificación MCP para servidores remotos. |
| `stdio.enabled: false` | Descarta el transporte por consola: este servidor corre como servicio HTTP independiente, no como subproceso local de un cliente. |
| `streamable-http.mcp-endpoint` | Ruta en la que se expone el protocolo MCP. |
| `streamable-http.keep-alive-interval` | Cada 30 s se envía un ping para mantener viva la conexión con el cliente. |
| `annotation-scanner.enabled` | Habilita el escaneo automático de beans anotados con `@McpTool`, `@McpResource`, `@McpPrompt` y `@McpComplete`. |

#### (3-9) Configuración del servidor HTTP y TLS

El bloque `server` define el puerto HTTP utilizado por la aplicación y la configuración TLS del servidor.

```yaml
server:
  port: 8080
  ssl:
    enabled: false
```

| Propiedad | Descripción |
|---|---|
| `server.port: 8080` | Puerto HTTP en el que escucha la aplicación. |
| `server.ssl.enabled: false` | Deshabilita TLS directamente en Tomcat. En el entorno de despliegue, TLS puede terminar en una capa anterior como un Ingress, Load Balancer o API Gateway.|

#### (4-9) Configuración del Adaptador REST 

El bloque `keel.adapters.rest-client:` configura el cliente REST utilizado por las Tools para comunicarse con servicios externos.

```yaml
keel:
  adapters:
    rest-client:
      # ==========================================
      # Keel Framework — RestClient Configuration
      # Configuration: SSL / REST Services / HTTP Client
      # ==========================================
      ssl:
        enabled: true
        trust-store: ${TRUSTSTORE_LOCAL}
        trust-store-password: ${TRUSTSTORE_PASSWORD}
        trust-store-type: ${TRUSTSTORE_TYPE}

      services: { }
      # services:
      #   name-service:
      #     base-url:     https://api.ejemplo.sca.es
      #     log-requests: true
      http-client:
        socket-timeout: 60000
        connect-timeout: 60000
        request-timeout: 60000
        max-total-connections: 200
        max-connections-per-route: 20
        connection-time-to-live: 300000
        keep-alive: 10000
```

`ssl` es la configuración TLS compartida por todas las llamadas salientes; `services.<nombre>` registra cada backend que las Tools pueden invocar a través de `McpRestClientFactory`. Cada servicio se identifica por su clave (`product`) y se resuelve por nombre desde el código.
> La contraseña del truststore no debe tener valor por defecto en el YAML; inyéctala siempre por variable de entorno o secreto.

El bloque `keel.mcp.httpclient` configura el cliente HTTP compartido utilizado por adapter-rest-client para realizar llamadas a servicios backend desde las Tools.

```yaml
keel:
  mcp:
    httpclient:
      connect-timeout: 5000
      socket-timeout: 30000
      request-timeout: 30000
      max-total-connections: 200
      max-connections-per-route: 20
      connection-time-to-live: 300000
      keep-alive: 10000
```
Configura el pool de conexiones compartido que `adapter-rest-client` utiliza para invocar los backends desde las Tools. Valores en milisegundos.

| Propiedad | Significado |
|---|---|
| `connect-timeout` | Tiempo máximo para establecer la conexión TCP. |
| `socket-timeout` | Tiempo máximo esperando datos una vez establecida la conexión. |
| `request-timeout` | Tiempo máximo total por request. |
| `max-total-connections` | Conexiones simultáneas en todo el pool. |
| `max-connections-per-route` | Conexiones simultáneas por cada host destino. |
| `connection-time-to-live` | Vida máxima de una conexión antes de reciclarla. |
| `keep-alive` | Tiempo que una conexión inactiva se mantiene abierta para reutilización. |

> ⚠️ Ajusta los timeouts al SLA real de cada backend. Un timeout largo hace que una Tool quede bloqueada ese tiempo antes de fallar, y el cliente MCP percibe la latencia completa. Como referencia, `connect-timeout` rara vez necesita más de 5 s.


#### (5-9) Configuración de la autenticación JWT del transporte

El bloque `keel.mcp.transport.auth` configura la autenticación de las peticiones dirigidas al MCP Server.

```yaml
keel:
  mcp:
    transport:
      auth:
        enabled: true
        jwt:
          issuer: ${JWT_ISSUER:https://sso.example.com/realms/mcp}
          audience: ${JWT_AUDIENCE:mcp-server}
          jwks-ttl: 1h
        excluded-paths:
          - /actuator/health
          - /actuator/info
```     
| Propiedad | Descripción |
|---|---|
| `auth.enabled` | El endpoint MCP exige un JWT válido en la cabecera `Authorization: Bearer`. |
| `jwt.issuer` | Proveedor de identidad OIDC (Keycloak / Red Hat SSO) que emite los tokens. Las claves públicas se obtienen del endpoint JWKS del issuer. |
| `jwt.audience` | Audiencia esperada en el claim `aud`. **Debe definirse**: una audiencia sin validar permite reutilizar tokens emitidos para otras aplicaciones. |
| `jwt.jwks-ttl` | Tiempo que se cachean las claves públicas antes de refrescarlas. |
| `excluded-paths` | Rutas exentas de autenticación, necesarias para que los *probes* de Kubernetes / OpenShift funcionen sin token. |

> ⚠️ La validación de audience debe configurarse de acuerdo con las políticas de seguridad del entorno. Una validación incompleta de los claims del JWT puede reducir el nivel de seguridad de la autenticació

#### (6-9) Configuración de la gestión de sesión y transporte Streamable HTTP

El bloque `keel.mcp.transport.session` configura la gestión de las sesiones MCP, mientras que `keel.mcp.transport.streamable` define parámetros específicos del canal Streamable HTTP.

```yaml
keel:
  mcp:
    transport:
      session:
        max-sessions: 500
        session-timeout: 30m
        log-events: false
```

| Propiedad | Descripción |
|---|---|
| `session.max-sessions` | Límite de sesiones MCP concurrentes. Al alcanzarlo, las nuevas inicializaciones se rechazan. |
| `session.session-timeout` | Una sesión sin actividad se cierra y se libera tras este tiempo. |
| `session.log-events` | Registra en detalle los eventos de ciclo de vida de sesión (creación, validación, expiración). Útil en desarrollo. |


#### (7-9) Configuración de la Observabilidad

El bloque `observability` controla la generación de logs estructurados asociados a los diferentes tipos de eventos del MCP Server.
Logs JSON estructurados, activables por nivel mediante variables de entorno:

```yaml
keel:
  mcp:
    observability:
      enabled: ${MCP_OBSERVABILITY_ENABLED:true}
      technical: ${MCP_OBSERVABILITY_TECHNICAL:true}
      functional: ${MCP_OBSERVABILITY_FUNCTIONAL:true}
      security: ${MCP_OBSERVABILITY_SECURITY:true}
```

| Nivel | Qué registra |
|---|---|
| `technical` | Requests y responses HTTP hacia los backends. |
| `functional` | Invocaciones de Tools MCP: nombre, argumentos, resultado y duración. |
| `security` | Eventos de autenticación JWT y de gestión de sesiones. |

#### (8-9) Configuración de la gestión de la caché

El bloque `kell.mcp.cache` configura la caché local basada en `Caffeine` utilizada por los componentes de Keel.

```yaml
keel:
  adapters:
    cache:
      expire-after-write: 30m
      expire-after-access: 30m
      maximum-size: 500
      record-stats: true
```
Keel utiliza esta caché en memoria como almacén de estado del propio protocolo: guarda el `Mcp-Session-Id` de cada sesión activa, tal como exige la especificación para que la comunicación continúe entre requests, y el JWT asociado a esa sesión.

Por ese motivo sus valores deben estar alineados con los de sesión:

- `expire-after-access` ≥ `session.session-timeout` (una entrada de caché no debe expirar antes que la sesión que representa).
- `maximum-size` ≥ `session.max-sessions`.
- `record-stats` habilita las estadísticas de hit/miss, expuestas vía Actuator.

#### (9-9) Configuración de Actuator y logging

El bloque `management` configura los endpoints de Spring Boot Actuator, mientras que el bloque logging establece los niveles de logging de la aplicación.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    root: INFO
    io.github.keelframework.mcp: INFO
```

| Propiedad | Descripción |
|---|---|
| `management.endpoints.web.exposure.include` | Define los endpoints de Actuator expuestos mediante HTTP. En este caso: health, info y metrics. |
| `management.endpoint.health.show-details` | Configura el nivel de detalle mostrado por el endpoint de health check. |
| `logging.level.root` | Define el nivel de logging global de la aplicación. |
| `logging.level.com.framework.observability.logging` | Define el nivel de logging específico para los componentes de observabilidad de Keel. |

Expone `/actuator/health`, `/actuator/info` y `/actuator/metrics`. `show-details: when-authorized` muestra el detalle de los *health indicators* solo a usuarios autenticados; usa `always` únicamente en entornos locales, ya que el detalle puede revelar hosts y estados de dependencias internas.

### Desarrollo de Tools MCP

Esta sección describe el modelo de implementación de las **Tools MCP** en Keel, tomando como arquitectura base de referencia el arquetipo **Keel** para generar proyectos MCP Server basados en **Spring AI**.

Las Tools se desarrollan en el módulo `<proyecto>-mcp` del proyecto generado, dentro del package `<base-package>.mcp.tools` (por ejemplo, `io.github.keelframework.mcp.sample.tools`).

#### `@Tool` — Implementación

Una **Tool** representa una capacidad de negocio expuesta por el MCP Server para que pueda ser invocada por un agente o un LLM. Aunque su implementación es relativamente sencilla, todas las Tools deben seguir un conjunto de convenciones que garanticen una arquitectura homogénea, fácilmente mantenible y consistente entre proyectos.

Keel proporciona una estructura base que estandariza la implementación de las Tools, incluyendo la organización del código, la comunicación con los sistemas backend, el manejo de errores, la observabilidad y el registro de las capacidades disponibles para el modelo.

La implementación de una Tool involucra 7 aspectos técnicos, que se describen en las siguientes secciones:

| # | Aspecto | Elemento clave | Obligatorio |
|:-:|---|---|:-:|
| 1 | Definición del método | Paquete `tools`, bean de Spring (`@Component`), firma tipada | ✅ |
| 2 | Anotación `@Tool` en el método | `description` con formato: **QUÉ HACE / CUÁNDO USAR / QUÉ DEVUELVE** | ✅ |
| 3 | Anotación `@ToolParam` en los parámetros | `description` (tipo + valores válidos + ejemplo), `required` | ✅ |
| 4 | Manejo de errores | `try/catch`; lanzar `McpToolException`, que Keel convierte en un error MCP estructurado | ✅ |
| 5 | Observabilidad | Traza con `mcplog.logTool(servicio, ruta, httpStatus, duración)` en éxito y en error | ✅ |
| 6 | Registro de la Tool | Bean `ToolCallbackProvider` explícito: `MethodToolCallbackProvider.builder().toolObjects(bean).build()` | ✅ |
| 7 | Naming convention | Verbo + sustantivo, `camelCase` (ej.: `getProduct`) | ✅ |

#### Estructura estándar de una Tool (stack Spring AI)

Toda Tool desarrollada sobre Keel debe seguir una estructura común que facilite su comprensión tanto por los desarrolladores como por el LLM. Esta estructura incluye la definición del método, la descripción funcional mediante las anotaciones `@Tool` y `@ToolParam`, la invocación a los servicios backend, el registro de trazas de observabilidad y un manejo uniforme de errores.

El siguiente ejemplo muestra la estructura recomendada:

```java
    @Tool(description = """
        Find a pet by its identifier.
        Use when the user asks about a specific pet or mentions an ID.
        Returns the pet's name, category and status.
        """)
    public PetstoreDTO.PetResponse findPet(
        @ToolParam(description = "Numeric ID of the pet. Example: 1, 2, 3")
        long petId) {

      long start = System.nanoTime();
      String path = "/pet/" + petId;
      try {
        RestResponse<PetstoreDTO.PetResponse> response = petClient.get(
                "/pet/{petId}", PetstoreDTO.PetResponse.class, petId);
    
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);
        return response.body();
      }
      catch (RestClientException e) {
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
        log.warn(PetstoreErrorsMsg.PET_NOT_FOUND_MSG, petId, path, e.getMessage());
        throw new McpToolException(SERVICE_NAME, path, 500,
                "Could not retrieve pet " + petId, e);
      }
    }
```
### Obtener el RestClient en la Tool

Una vez registrado el backend en `application.yml` (ver [Adaptador REST: servicios backend](#adaptador-rest-servicios-backend)), el módulo `adapter-rest-client` crea automáticamente un `McpRestClient` asociado a ese `<nombre-servicio>`.

Keel no inyecta el cliente directamente: inyecta por constructor una factory, `McpRestClientFactory`, que entrega el cliente correspondiente a cada backend a partir del mismo nombre lógico usado en la configuración. Esto permite resolver el cliente en tiempo de ejecución y que una misma Tool pueda hablar con varios backends sin cambiar su constructor.

```java
private final McpRestClientFactory restClientFactory;
private McpRestClient productClient;

public ProductTools(McpRestClientFactory restClientFactory) {
    this.restClientFactory = restClientFactory;
}

@PostConstruct
void init() {
    this.productClient = restClientFactory.getClient("product");   // clave de application.yml
}
```
Si la Tool usa un único backend, resuélvelo una vez en `@PostConstruct` como en el ejemplo; si necesita varios, llama a `getClient(...)` en cada método.

**Paso a paso: componentes a inyectar:**

| Componente | Rol |
|---|---|
| `McpRestClientFactory` | Fábrica que entrega un `McpRestClient` ya configurado (base-url, TLS, timeouts) para un servicio dado, mediante `getClient("<nombre-servicio>")`. |
| `McpRestClient` | Cliente HTTP asociado a un backend concreto. Expone `get`, `post`, `put`, `delete`, etc., y devuelve un `model.io.keelframework.mcp.adapters.rest.RestResponse<T>` con el cuerpo tipado y el código HTTP. |

**1. Obtener el cliente del backend**

Dentro de la Tool (o una vez en `@PostConstruct`), resuelve el cliente con el nombre de servicio registrado en `application.yml`:

```java
McpRestClient client = restClientFactory.getClient("product");
```

El string `"product"` debe coincidir exactamente con la clave definida en `keel.mcp.adapters.rest.services.<nombre-servicio>`. Si no coincide, la factory lanza una `IllegalArgumentException` al arrancar. <!-- TODO: confirmar excepción real -->

**2. Invocar el endpoint**

Usa `client.get(...)` (o el verbo HTTP que corresponda) indicando el path relativo, la clase de respuesta esperada y los parámetros de path o query. El base-url, el TLS y los timeouts ya vienen resueltos desde la configuración del adaptador:

```java
model.io.keelframework.mcp.adapters.rest.RestResponse<ProductoDTO.ProductResponse> response =
        client.get("/product/{id}", ProductoDTO.ProductResponse.class, productId);

ProductoDTO.ProductResponse product = response.body();
int status = response.httpStatus();
```

### Desarrollo de Prompts MCP

Esta sección describe el modelo de implementación de los **Prompts MCP** en Keel, tomando como arquitectura base de referencia el arquetipo **Keel** para generar proyectos MCP Server basados en **Spring AI**.

Los Promtps se desarrollan en el módulo `<proyecto>-mcp` del proyecto generado, dentro del package `<base-package>.mcp.prompt` (por ejemplo, `io.github.keelframework.mcp.sample.tools`).

#### `@Prompt` Implementación

Un **Prompt** representa una plantilla de conversación expuesta por el MCP Server para guiar al LLM en la ejecución de flujos funcionales específicos del dominio de negocio que cubre el servidor.

A diferencia de una Tool, que expone una capacidad de negocio ejecutable, un Prompt no ejecuta lógica por sí mismo: estructura y condiciona el razonamiento del LLM, indicándole qué Tools invocar, en qué orden, con qué validaciones y bajo qué formato de respuesta.

Aunque su implementación es relativamente sencilla, todos los Prompts deben seguir un conjunto de convenciones que garanticen una arquitectura homogénea, fácilmente mantenible y consistente entre proyectos.

Keel proporciona una estructura base que estandariza la implementación de los Prompts, incluyendo la organización del código, la definición de argumentos de entrada, la construcción de los mensajes de plantilla y el control de flujos multi-tool.

**Aspectos técnicos de la implementación de un Prompt**

La implementación de un Prompt involucra seis aspectos técnicos:

| # | Aspecto | Elemento clave | Obligatorio |
|:-:|---|---|:-:|
| 1 | Definición del método | Paquete `prompts`, bean de Spring (`@Component`), método que retorna `McpSchema.GetPromptResult` | ✅ |
| 2 | Anotación `@McpPrompt` en el método | `name` (formato: `<contexto>-<accion>`, kebab-case) + `description` con formato: **QUÉ FLUJO GUÍA / CUÁNDO USARLO** | ✅ |
| 3 | Anotación `@McpArg` en los parámetros | `description` (tipo + formato/valores válidos + ejemplo), `required` | ✅ |
| 4 | Construcción del mensaje | `List<McpSchema.PromptMessage>` con instrucciones explícitas: Tools a invocar, orden, dependencias entre pasos, formato de salida y manejo de casos sin resultado | ✅ |
| 5 | Rol del mensaje | `USER` (el LLM decide libremente) vs `USER` + `ASSISTANT` (formato de respuesta forzado / *few-shot*) | ✅ |
| 6 | Naming convention del método | Verbo + sustantivo, `camelCase` (ej.: `buscarProducto`) | ✅ |

> Los Prompts anotados con `@McpPrompt` se registran automáticamente en el servidor gracias a `spring.ai.mcp.server.annotation-scanner.enabled: true`; a diferencia de las Tools, no requieren un bean de registro explícito.

### Estructura estándar de un Prompt (stack Spring AI)

Todo Prompt desarrollado sobre Keel debe seguir una estructura común que facilite su comprensión tanto por los desarrolladores como por el LLM. Esta estructura incluye la definición del método, la descripción funcional mediante las anotaciones `@McpPrompt` y `@McpArg`, la construcción de la plantilla de mensajes (`McpSchema.GetPromptResult`) que guía al LLM en la invocación de las Tools necesarias, y la definición explícita del formato de respuesta esperado.

El siguiente ejemplo muestra la estructura recomendada:

```java
    @McpPrompt(
            name = "petstore-find-pet",
            description = "Template for looking up a pet by its ID. " +
                    "Use when the user wants full details about one specific pet."
    )
    public McpSchema.GetPromptResult findPetPrompt(
            @McpArg(description = "Numeric ID of the pet. Example: 1, 2, 3")
            String petId) {
        return new McpSchema.GetPromptResult(
                "Look up a pet by ID",
                List.of(
                        new McpSchema.PromptMessage(
                                McpSchema.Role.USER,
                                new McpSchema.TextContent(
                                        "Give me the full details of the pet with ID " + petId + ".\n" +
                                                "Include its name, category and current status."))
                ));
    }
```
### Desarrollo de Resources MCP

Esta sección describe el modelo de implementación de los **Resources MCP** en Keel, tomando como arquitectura base de referencia el arquetipo **Keel** para generar proyectos MCP Server basados en **Spring AI**.

Los Resources se desarrollan en el módulo `<proyecto>-mcp` del proyecto generado, dentro del package `<base-package>.mcp.resources` (por ejemplo, `io.github.keelframework.mcp.sample.resources`).

#### `@Resources` Implementación

Un **Resource** representa una fuente de información expuesta por el MCP Server para dotar al LLM del contexto y las reglas de negocio necesarias antes de razonar o de invocar una Tool.

A diferencia de una Tool, que expone una capacidad de negocio ejecutable, un Resource no ejecuta lógica de negocio ni produce efectos: solo entrega contenido de lectura (estático o dinámico) que el LLM puede consultar para informarse.

Aunque su implementación es relativamente sencilla, todos los Resources deben seguir un conjunto de convenciones que garanticen una arquitectura homogénea, fácilmente mantenible y consistente entre proyectos.

Keel proporciona una estructura base que estandariza la implementación de los Resources, incluyendo la organización del código, la definición de la URI, la construcción del contenido devuelto y el criterio de cuándo el contenido debe ser estático o dinámico.

**Aspectos técnicos de la implementación de un Resource**

La implementación de un Resource involucra cinco aspectos técnicos:

| # | Aspecto | Elemento clave | Obligatorio |
|:-:|---|---|:-:|
| 1 | Definición del método | Paquete `resources`, bean de Spring (`@Component`), método que retorna `McpSchema.ReadResourceResult` | ✅ |
| 2 | Anotación `@McpResource` en el método | `uri` (formato: `<esquema>://<contexto>/<recurso>`), `name` (kebab-case: `<contexto>-<recurso>`), `mimeType` y `description` con formato: **QUÉ CONTIENE / CUÁNDO CONSULTARLO** | ✅ |
| 3 | Construcción del contenido | `List<McpSchema.TextResourceContents>` dentro del `ReadResourceResult`, con la misma `uri` y `mimeType` declarados en la anotación | ✅ |
| 4 | Origen del contenido | **Estático** (constante en código, no cambia) vs **dinámico** (obtenido del backend en tiempo real vía `McpRestClient`, siempre de solo lectura) | ✅ |
| 5 | Naming convention del método | Verbo + sustantivo, `camelCase` (ej.: `getProductRules`) | ✅ |

> Los Resources anotados con `@McpResource` se registran automáticamente mediante `spring.ai.mcp.server.annotation-scanner.enabled: true`, igual que los Prompts.

#### Estructura estándar de un Resource (stack Spring AI)

Todo Resource desarrollado sobre Keel debe seguir una estructura común que facilite su comprensión tanto por los desarrolladores como por el LLM. Esta estructura incluye la definición del método, la descripción funcional mediante la anotación `@McpResource`, la construcción del contenido de respuesta (`McpSchema.ReadResourceResult`) que expone la información al LLM, y la definición explícita del origen del contenido (estático o dinámico) y su formato.

El siguiente ejemplo muestra la estructura recomendada:

```java
    @McpResource(
            uri = "petstore://tools-guide",
            name = "Petstore tools guide",
            description = "Describes when and how to use each available Petstore tool. " +
                    "Consult when the LLM needs to decide which tool to call.",
            mimeType = "text/plain"
    )
    public String toolsGuide() {
        return """
                AVAILABLE TOOLS IN petstore-sample:
 
                1. findPet(petId)
                   - When to use:  the user asks about a specific pet or gives an ID
                   - Parameters:   petId (long) — numeric identifier of the pet
                   - Example:      "What's the status of pet 5?"
                   - Returns:      id, name, category and status of the pet
 
                2. listPets(status)
                   - When to use:  the user wants to browse pets by availability
                   - Parameters:   status (String) — one of: available, pending, sold
                   - Example:      "Show me all available pets"
                   - Returns:      a list of pets matching that status, plus the total count
 
                3. registerPet(name, category, status)
                   - When to use:  the user wants to add a new pet to the store
                   - Parameters:   name (String), category (String), status (String, one of:
                                   available, pending, sold)
                   - Example:      "Register a new dog named Rex, available"
                   - Returns:      the newly registered pet, including its assigned ID
 
                GENERAL RULES:
                - Never invent pet data — always use the tools for real information
                - If a required parameter is missing, ask the user before calling the tool
                - "status" only accepts: available, pending, sold — reject anything else
                  and ask the user to pick one of these three values
                """;
    }
```
Este Resource es de origen **estático**: su contenido está definido en código y no cambia entre invocaciones. Para un Resource **dinámico** (por ejemplo, el catálogo de ramos vigentes), el método obtendría el contenido del backend mediante `McpRestClient` en cada lectura, manteniendo la misma estructura de respuesta.

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Para mantener el proyecto coherente:

- **Errores y propuestas:** abre un [issue](https://github.com/keelframework/keel-mcp/issues) describiendo el problema o la mejora.
- **Cambios pequeños** (typos, documentación, correcciones evidentes): envía directamente un pull request.
- **Cambios grandes** (nuevos módulos, cambios de arquitectura o de convenciones): abre primero un issue para discutir el enfoque antes de invertir tiempo en el código.
- Los pull requests deben compilar con `mvn clean verify` y respetar las convenciones descritas en este README.

Keel es un proyecto personal de código abierto; los tiempos de respuesta pueden variar.

---

## 📄 Licencia

Distribuido bajo la [Apache License 2.0](LICENSE).

**[Keel Framework](https://github.com/keelframework/keel-mcp)** · Creado y mantenido por **Ricardo Marzochi** ([LinkedIn](https://www.linkedin.com/in/ricardo-marzochi-0863705/))