# 

**PROYECTO CYBERGUARD**

Plataforma de análisis y detección de comportamientos anómalos en redes

**Asignatura:**  
Programación avanzada

**Docente:**  
Faber Alemán

**Integrantes:**

Sergio Esteban Veloza Gonzalez  
Luifer Andres Hernandez Lambraño  
Marcos Antonio Pacheco Bautista  
Rafael Antonio Rodríguez Gamarra

Programa:  
Ingeniería de Sistemas

**Corporación Unificada Nacional de Educación Superior**  
Programa de Ingeniería de Sistemas  
Facultad de Ingeniería  
Sincelejo, Colombia  
2026

1. INTRODUCCIÓN

El crecimiento de las redes informáticas ha permitido que empresas, instituciones educativas y diferentes organizaciones conecten una gran cantidad de dispositivos para compartir información y utilizar servicios de manera rápida y eficiente. Sin embargo, este crecimiento también representa nuevos retos relacionados con la identificación, supervisión y gestión de eventos que puedan afectar la seguridad de la información.

En una infraestructura de red pueden coexistir computadores, teléfonos, servidores, impresoras, cámaras y otros dispositivos que generan diferentes tipos de actividad. Cuando no se cuenta con mecanismos adecuados para registrar y analizar estos eventos, puede resultar difícil determinar cuándo una actividad se encuentra fuera de los patrones esperados. Entre estas situaciones pueden encontrarse la aparición de dispositivos desconocidos, múltiples intentos de conexión, actividad repetitiva o solicitudes realizadas hacia diferentes puertos en periodos cortos.

La gestión adecuada de los eventos de seguridad requiere mecanismos que permitan identificarlos, analizarlos y determinar las acciones correspondientes ante situaciones que puedan representar un riesgo (National Institute of Standards and Technology \[NIST\], 2012). De igual manera, los principios actuales de gestión de ciberseguridad resaltan la importancia de identificar y analizar los riesgos asociados con los sistemas y activos tecnológicos (NIST, 2024).

A partir de esta problemática surge CyberGuard, una propuesta de software orientada al registro, monitoreo y análisis de eventos relacionados con dispositivos pertenecientes a una red autorizada. El proyecto será desarrollado principalmente mediante Java y contará con una arquitectura modular que permita separar la gestión de información, el procesamiento de eventos, el análisis de comportamientos y la generación de alertas.

La finalidad de esta primera etapa es establecer de manera clara el problema que se pretende solucionar, la solución propuesta, los usuarios involucrados, las funcionalidades principales, el alcance, los requerimientos, la arquitectura, las tecnologías, la metodología de desarrollo y la planificación inicial del proyecto.

2. PLANTEAMIENTO DEL PROBLEMA

Actualmente, una organización puede contar con una cantidad considerable de dispositivos conectados a una misma infraestructura de red. A medida que aumenta el número de equipos y servicios, también aumenta la cantidad de eventos que pueden producirse diariamente.

El problema no consiste necesariamente en la existencia de múltiples dispositivos conectados, sino en la dificultad para determinar cuándo una actividad puede representar una situación que requiere atención. La revisión manual de grandes cantidades de eventos puede consumir tiempo y dificultar la identificación oportuna de determinados comportamientos.

Por ejemplo, un administrador puede encontrar un dispositivo que aparece por primera vez en la red, una dirección IP que genera una cantidad inusual de solicitudes o un equipo que realiza múltiples intentos de conexión hacia diferentes puertos en un periodo corto. Este tipo de situaciones no significa necesariamente que exista un ataque, pero sí puede representar un comportamiento que requiere revisión.

La identificación y análisis de eventos constituye una parte importante de la gestión de incidentes de seguridad, debido a que permite recopilar información y determinar si una situación requiere atención o respuesta (NIST, 2012).

En contextos donde la información se revisa únicamente de manera manual, el administrador puede tener dificultades para relacionar eventos, dispositivos, fechas, frecuencias de actividad y condiciones específicas. Esta situación puede generar retrasos en la identificación de comportamientos potencialmente anómalos.

Por esta razón, se plantea el desarrollo de CyberGuard, una herramienta académica que permita centralizar información relacionada con dispositivos y eventos de una red autorizada y aplicar un conjunto de reglas para identificar comportamientos que puedan considerarse anómalos.

CyberGuard no pretende sustituir plataformas profesionales de seguridad informática ni funcionar como un sistema empresarial de detección y respuesta ante incidentes. Su propósito consiste en desarrollar una solución académica que permita aplicar conceptos de programación avanzada, estructuras de datos, algoritmos, bases de datos, arquitectura de software y fundamentos de ciberseguridad a una problemática concreta.

3. FORMULACIÓN DEL PROBLEMA

Pregunta principal

¿Cómo desarrollar una plataforma basada principalmente en Java que permita registrar, analizar y clasificar eventos de una red autorizada para facilitar la identificación de comportamientos potencialmente anómalos y generar alertas para su seguimiento?

4. JUSTIFICACIÓN

El proyecto CyberGuard se propone como una alternativa académica y tecnológica para abordar una problemática relacionada con el monitoreo y análisis de eventos de seguridad en redes informáticas.

Desde el punto de vista de la Ingeniería de Sistemas, el proyecto permite integrar diferentes áreas de conocimiento en una misma solución. Entre ellas se encuentran la programación orientada a objetos, estructuras de datos, algoritmos, bases de datos, arquitectura de software, desarrollo de aplicaciones y fundamentos de seguridad informática.

Uno de los principales intereses del proyecto es utilizar Java como lenguaje de desarrollo, debido a que permite implementar una estructura orientada a objetos y desarrollar diferentes componentes de una aplicación de manera organizada. Esto permitirá aplicar conocimientos propios de la asignatura de Programación Avanzada y evitar que el proyecto se limite únicamente al almacenamiento y consulta de información (Oracle, s. f.).

CyberGuard incorporará un componente de análisis basado en reglas. Esto significa que los eventos registrados no solamente serán almacenados, sino que podrán ser procesados para determinar si cumplen determinadas condiciones establecidas previamente.

Por ejemplo, si una dirección IP registra una cantidad elevada de intentos de conexión durante un periodo determinado, el sistema podrá identificar dicha situación y asignarle una clasificación de riesgo de acuerdo con las reglas definidas.

Desde una perspectiva práctica, una herramienta de este tipo puede servir como apoyo para un usuario autorizado que necesite consultar rápidamente los dispositivos registrados, los eventos producidos y las alertas que requieren mayor atención.

Desde el punto de vista académico, el proyecto representa un reto adecuado para la asignatura de Programación Avanzada, debido a que requiere planificar una solución compuesta por diferentes módulos y establecer una arquitectura que facilite la integración y evolución del sistema.

Además, el proyecto permitirá aplicar principios básicos de seguridad en el desarrollo de software. Los riesgos relacionados con aplicaciones y sistemas informáticos requieren considerar aspectos como control de acceso, protección de información e identificación de posibles vulnerabilidades (OWASP Foundation, 2021).

Finalmente, el desarrollo se realizará exclusivamente sobre redes, dispositivos y datos para los cuales exista autorización. Las herramientas de análisis que puedan utilizarse durante etapas posteriores estarán destinadas a entornos controlados y académicos.

5. OBJETIVOS

5.1 Objetivo general

Desarrollar una plataforma de software denominada CyberGuard, basada principalmente en Java, que permita registrar y analizar eventos de una red autorizada mediante reglas de detección, con el propósito de identificar comportamientos potencialmente anómalos, clasificarlos según su nivel de riesgo y generar alertas que faciliten su seguimiento.

5.2 Objetivos específicos

**5.2 Objetivos específicos**

1. Identificar y documentar los requerimientos funcionales y no funcionales de CyberGuard, y diseñar con base en ellos una arquitectura modular y una base de datos que soporten la gestión de usuarios, dispositivos, eventos, reglas y alertas.  
2. Definir reglas iniciales de análisis y un mecanismo de clasificación de riesgo para los eventos de una red autorizada, junto con una interfaz de consulta de dispositivos, eventos, alertas e historial.  
3. Establecer una metodología de trabajo que distribuya las actividades entre los cuatro integrantes y definir los tipos de pruebas para verificar los componentes del sistema en etapas posteriores.

DESCRIPCIÓN GENERAL DE LA SOLUCIÓN

CyberGuard será una plataforma orientada al registro y análisis de eventos relacionados con una red informática autorizada.

El sistema estará compuesto por diferentes módulos que trabajarán de manera coordinada para recibir información, almacenarla, procesarla, analizarla y presentar los resultados al usuario autorizado.

El flujo general propuesto será:

Red autorizada → Registro de eventos → Almacenamiento → Procesamiento → Análisis → Clasificación → Alerta → Consulta

El sistema almacenará información relacionada con los dispositivos y eventos registrados. Posteriormente, el componente de análisis aplicará reglas previamente establecidas para determinar si una actividad presenta características que puedan considerarse anómalas.

Cuando una condición determinada sea cumplida, CyberGuard podrá generar una alerta y asignar un nivel de riesgo.

Los niveles iniciales propuestos serán:

• Bajo  
• Medio  
• Alto  
• Crítico

La clasificación permitirá organizar las alertas de acuerdo con su prioridad y facilitar su consulta.

7. FUNCIONAMIENTO GENERAL PROPUESTO

El funcionamiento conceptual de CyberGuard se desarrollará mediante las siguientes etapas:

Paso 1\. Registro

El sistema recibirá o registrará información relacionada con dispositivos y eventos pertenecientes a una red autorizada.

Paso 2\. Almacenamiento

La información obtenida será almacenada en la base de datos para permitir su posterior consulta y análisis.

Paso 3\. Procesamiento

Los eventos registrados serán enviados al componente encargado de procesarlos y organizarlos.

Paso 4\. Análisis

El motor de análisis revisará los eventos utilizando las reglas previamente definidas.

Paso 5\. Clasificación

Dependiendo de las características y condiciones identificadas, el sistema determinará un nivel de riesgo.

Paso 6\. Generación de alerta

Cuando un evento cumpla una condición considerada relevante, el sistema generará una alerta asociada con la regla que haya sido activada.

Paso 7\. Consulta

El usuario autorizado podrá consultar los dispositivos, eventos, alertas e información histórica disponible en la plataforma.

8. ALCANCE Y LIMITACIONES

8.1 Alcance

El proyecto CyberGuard tendrá como alcance el diseño y desarrollo progresivo de una plataforma académica para el registro, análisis y clasificación de eventos asociados con una red autorizada.

El sistema incluirá inicialmente:

• Autenticación y gestión básica de usuarios.

• Gestión y consulta de dispositivos registrados.

• Registro y consulta de eventos.

• Procesamiento de eventos.

• Motor de reglas para identificar determinados comportamientos.

• Clasificación de eventos mediante niveles de riesgo.

• Generación y gestión de alertas.

• Panel de información para consultar indicadores básicos.

• Historial de eventos y alertas.

• Generación de reportes básicos.

• Base de datos para almacenar la información del sistema.

• Arquitectura modular que permita ampliar las funcionalidades posteriormente.

Los principales usuarios contemplados serán:

• Administrador: responsable de la configuración general, usuarios y reglas del sistema.

• Analista: Encargado de consultar eventos, revisar alertas y analizar comportamientos registrados.

• Usuario de consulta: tendrá acceso limitado a la información autorizada.

El proyecto estará orientado principalmente a un entorno académico y de laboratorio, utilizando información de prueba y redes sobre las cuales exista autorización.

8.2 Limitaciones

CyberGuard tendrá las siguientes limitaciones:

• No será diseñado como sustituto de plataformas profesionales de seguridad informática.

• No realizará ataques reales ni actividades destinadas a vulnerar sistemas.

• No realizará monitoreo de redes sin autorización.

• No tendrá como objetivo bloquear automáticamente dispositivos o conexiones.

• No incluirá inicialmente funciones avanzadas de inteligencia artificial o aprendizaje automático.

• No realizará análisis especializado de malware.

• Las reglas de detección estarán limitadas a los comportamientos definidos durante el desarrollo del proyecto.

• La capacidad de procesamiento dependerá de los recursos disponibles en el entorno académico donde sea ejecutado.

• La primera versión estará orientada a demostrar el funcionamiento de los conceptos planteados y no a soportar infraestructuras empresariales de gran escala.

Estas limitaciones permiten mantener el proyecto dentro de un alcance viable para la asignatura y evitar que se convierta en una solución de seguridad empresarial.

9. MÓDULOS DEL SISTEMA

9.1 Módulo de autenticación y usuarios

Permitirá controlar el acceso a la plataforma mediante credenciales y permisos asociados a cada tipo de usuario.

Se contemplan inicialmente tres tipos de usuario:

Administrador: tendrá acceso a la configuración general del sistema, gestión de usuarios y administración de reglas.

Analista: podrá consultar eventos, revisar alertas y realizar análisis de la información registrada.

Usuario de consulta: tendrá acceso limitado a la información autorizada.

9.2 Módulo de dispositivos

Permitirá administrar la información correspondiente a los dispositivos registrados en la red.

Entre los datos considerados se encuentran:

• Dirección IP.

• Dirección MAC.

• Nombre del dispositivo.

• Tipo de dispositivo.

• Estado.

• Fecha de registro.

• Última actividad.

9.3 Módulo de eventos

Permitirá registrar los eventos asociados con los dispositivos.

Un evento podrá contener:

• Identificador.

• Dispositivo relacionado.

• Dirección IP.

• Tipo de evento.

• Fecha y hora.

• Información adicional.

• Nivel de importancia.

9.4 Motor de análisis

Será uno de los componentes principales del proyecto.

Su función será evaluar los eventos utilizando reglas previamente definidas y determinar si existen características que puedan considerarse anómalas.

Por ejemplo, si un dispositivo genera una cantidad elevada de intentos de conexión durante un periodo corto, el sistema podrá identificar esta situación como un evento potencialmente anómalo.

Este componente permitirá aplicar estructuras de datos, algoritmos y principios de programación orientada a objetos.

9.5 Motor de reglas

Permitirá establecer las condiciones utilizadas para analizar los eventos.

Inicialmente, se podrán contemplar reglas relacionadas con:

* Cantidad de intentos de conexión.  
* Escaneo de diferentes puertos.  
* Aparición de dispositivos desconocidos.  
* Repetición de determinados eventos.  
* Frecuencia de actividad.  
* Acumulación de eventos asociados con un mismo dispositivo.

Las reglas serán definidas para un entorno académico y de laboratorio.

9.6 Sistema de alertas

Cuando un evento cumpla una condición determinada, el sistema podrá generar una alerta.

Una alerta podrá contener:

* Evento asociado.  
* Dispositivo relacionado.  
* Fecha y hora.  
* Regla activada.  
* Nivel de riesgo.  
* Estado de la alerta.

El estado de una alerta podrá ser:

* Pendiente.  
* En revisión.  
* Atendida.  
* Cerrada.

9.7 Panel de información

Se propone una interfaz donde el usuario autorizado pueda consultar información general del sistema.

Entre los indicadores iniciales se contemplan:

• Cantidad de dispositivos registrados.

• Cantidad de eventos.

• Cantidad de alertas.

• Alertas de riesgo alto.

• Alertas críticas. 

• Eventos recientes.

• Historial de actividad.

10. SISTEMA DE CLASIFICACIÓN DE RIESGO

Una característica importante del proyecto será establecer un mecanismo para clasificar determinados eventos según su nivel de riesgo.

Como propuesta inicial se utilizará una puntuación de riesgo asociada con diferentes situaciones detectadas por el sistema.

| Situación | Puntuación propuesta |
| :---- | :---- |
| Dispositivo no registrado | 10 |
| Actividad inusual | 10 |
| Repetición de conexiones | 15 |
| Múltiples intentos de conexión | 20 |
| Escaneo de diferentes puertos | 25 |
| Acumulación de eventos sospechosos | 30 |

A partir de la puntuación acumulada se establecerá inicialmente la siguiente clasificación:

| Puntuación | Nivel de riesgo |
| :---- | :---- |
| 0–20 | Bajo |
| 21–50 | Medio |
| 51–80 | Alto |
| 81–100 | Crítico |

Estos valores constituyen una propuesta inicial y podrán ajustarse durante las etapas posteriores de diseño y pruebas.

La finalidad de este mecanismo no será determinar de manera definitiva que una actividad corresponde a un ataque, sino identificar situaciones que requieren revisión por parte de un usuario autorizado.

11. REQUERIMIENTOS FUNCIONALES

RF01. Autenticación

El sistema deberá permitir que los usuarios registrados ingresen mediante credenciales y de acuerdo con los permisos asignados.

RF02. Gestión de usuarios

El administrador podrá crear, modificar, consultar y desactivar usuarios de acuerdo con sus permisos.

RF03. Gestión de dispositivos

El sistema permitirá registrar, actualizar y consultar dispositivos asociados con la red autorizada.

RF04. Registro de eventos

El sistema permitirá almacenar eventos relacionados con los dispositivos monitoreados.

RF05. Consulta de eventos

Los usuarios autorizados podrán consultar los eventos almacenados.

RF06. Análisis de eventos

El sistema analizará determinados eventos utilizando las reglas definidas.

RF07. Generación de alertas

El sistema deberá generar alertas cuando un evento cumpla las condiciones establecidas.

RF08. Clasificación de riesgo

El sistema deberá asignar un nivel de riesgo a los eventos o alertas de acuerdo con el mecanismo de clasificación definido.

RF09. Gestión de reglas

El administrador podrá consultar y modificar las reglas utilizadas para el análisis, de acuerdo con los permisos establecidos.

RF10. Historial

El sistema deberá mantener un historial de eventos y alertas registrados.

RF11. Panel de información

El sistema permitirá visualizar indicadores relacionados con eventos, dispositivos y alertas.

RF12. Reportes

El sistema podrá generar reportes básicos sobre los eventos y alertas registrados.

12. REQUERIMIENTOS NO FUNCIONALES

RNF01. Seguridad

La información almacenada deberá contar con mecanismos adecuados de protección y control de acceso para evitar el acceso no autorizado.

RNF02. Rendimiento

El sistema deberá procesar los eventos sin generar retrasos innecesarios en las operaciones principales.

RNF03. Escalabilidad

La arquitectura deberá permitir incorporar nuevos módulos y reglas sin requerir una reconstrucción completa del sistema.

RNF04. Mantenibilidad

El software deberá organizarse en componentes independientes que faciliten su mantenimiento, modificación y evolución.

RNF05. Usabilidad

La interfaz deberá presentar la información de manera clara, organizada y comprensible para los usuarios autorizados.

RNF06. Disponibilidad

El sistema deberá encontrarse disponible para los usuarios autorizados durante los periodos establecidos para su operación.

RNF07. Integridad

La información almacenada deberá conservar relaciones consistentes entre usuarios, dispositivos, eventos, reglas y alertas.

13. ARQUITECTURA PROPUESTA

Se propone utilizar una arquitectura por capas, debido a que permite separar las responsabilidades de los diferentes componentes y facilita el mantenimiento y evolución del software.

La estructura inicial será:

INTERFAZ

Panel de CyberGuard

↓

LÓGICA DE NEGOCIO

Usuarios – Dispositivos – Eventos

↓

MOTOR DE ANÁLISIS

Reglas – Puntuación – Riesgo

↓

ACCESO A DATOS

Persistencia y consultas

↓

BASE DE DATOS

Usuarios – Dispositivos – Eventos – Alertas

La capa de interfaz será responsable de presentar la información y permitir la interacción con el sistema.

La capa de lógica de negocio gestionará las operaciones principales relacionadas con usuarios, dispositivos y eventos.

El motor de análisis procesará los eventos y aplicará las reglas de detección.

La capa de acceso a datos gestionará la comunicación entre la aplicación y la base de datos.

Finalmente, la base de datos almacenará la información necesaria para el funcionamiento del sistema.

Esta separación permitirá distribuir las responsabilidades del software y facilitará la incorporación de modificaciones durante las siguientes etapas del proyecto.

14. TECNOLOGÍAS PROPUESTAS

14.1 Backend

• Java.

• Spring Boot.

Java será el lenguaje principal debido a su orientación a objetos y a que permite aplicar conceptos relacionados con programación avanzada y desarrollo estructurado de aplicaciones (Oracle, s. f.).

Spring Boot podrá utilizarse como framework para facilitar la construcción y organización del backend de la aplicación (VMware, s. f.).

14.2 Base de datos

• PostgreSQL o MySQL.

La base de datos será utilizada para almacenar información relacionada con usuarios, dispositivos, eventos, reglas y alertas.

14.3 Frontend

• HTML.

• CSS.

• JavaScript.

Estas tecnologías permitirán construir inicialmente la interfaz de usuario y presentar la información de manera organizada.

14.4 Control de versiones

• Git.

• GitHub.

Estas herramientas permitirán administrar las diferentes versiones del proyecto y facilitar la integración del trabajo desarrollado por los integrantes.

14.5 Entorno de desarrollo

• IntelliJ IDEA.

• Visual Studio Code.

• Eclipse.

El equipo podrá utilizar cualquiera de estos entornos de acuerdo con las necesidades de cada integrante.

14.6 Herramientas de apoyo para laboratorio

Podrán utilizarse herramientas como Nmap y Wireshark únicamente dentro de redes y entornos autorizados y controlados, con fines académicos y para la generación o análisis de información de prueba.

15. METODOLOGÍA DE DESARROLLO

CyberGuard será desarrollado utilizando la metodología ágil Scrum, debido a que permite organizar el trabajo mediante ciclos de desarrollo cortos, establecer prioridades y realizar entregas progresivas.

La utilización de Scrum permitirá dividir el proyecto en diferentes etapas de trabajo denominadas sprints. En cada sprint se seleccionarán actividades prioritarias del proyecto, se desarrollarán los componentes correspondientes y posteriormente se revisará el avance obtenido.

El proyecto contará con un Product Backlog, en el cual se registrarán las funcionalidades, actividades y necesidades identificadas. Estas tareas serán priorizadas de acuerdo con su importancia para el desarrollo de CyberGuard.

La aplicación de la metodología se organizará de la siguiente manera:

Planificación del sprint: el equipo seleccionará las actividades que serán desarrolladas durante el ciclo de trabajo.

Desarrollo: cada integrante realizará las actividades asignadas de acuerdo con sus responsabilidades.

Seguimiento: el equipo revisará periódicamente el estado de las tareas y los posibles inconvenientes.

Revisión: al finalizar cada sprint se verificará el avance de los componentes desarrollados.

Retroalimentación: se identificarán ajustes y nuevas actividades que deberán incorporarse a los siguientes ciclos.

Esta metodología permitirá mantener una organización progresiva del proyecto y facilitará la integración del trabajo realizado por los cuatro integrantes.

16. PLANIFICACIÓN INICIAL

| Fase | Actividades principales | Entregable |
| :---- | :---- | :---- |
| 1\. Análisis | Identificación del problema, objetivos, usuarios y requerimientos | Documento de análisis y requerimientos |
| 2\. Diseño | Arquitectura, módulos y estructura de base de datos | Diseño inicial del sistema |
| 3\. Desarrollo inicial | Gestión de usuarios, dispositivos y eventos | Primeros módulos del sistema |
| 4\. Motor de análisis | Desarrollo de reglas y clasificación de riesgo | Motor de análisis |
| 5\. Alertas e interfaz | Gestión de alertas, panel y consultas | Interfaz integrada |
| 6\. Integración | Integración de módulos y configuración del sistema | Versión integrada |
| 7\. Pruebas | Pruebas de los componentes y corrección de errores | Versión validada |
| 8\. Documentación | Elaboración de documentación y resultados | Documento final |

Las actividades serán gestionadas mediante un tablero de trabajo que permita identificar las tareas pendientes, las actividades en desarrollo y las tareas terminadas.

Para la organización del proyecto se podrán utilizar GitHub Projects, Trello u otra herramienta equivalente.

17. HERRAMIENTAS DE TRABAJO

Las principales herramientas de trabajo serán:

* Git y GitHub para control de versiones y colaboración.  
* Trello para la gestión de tareas.  
* Cursor y Claude Code para el desarrollo.  
* Java y Spring Boot para el desarrollo del software.  
* PostgreSQL o MySQL para la gestión de datos.  
* Herramientas de comunicación digital para coordinar las actividades del equipo.

18. CONSIDERACIONES DE SEGURIDAD Y USO RESPONSABLE

CyberGuard será desarrollado bajo un enfoque académico y de uso responsable. Las actividades de análisis estarán limitadas a redes, dispositivos y datos sobre los cuales el equipo tenga autorización.

Las herramientas utilizadas durante las etapas de laboratorio no serán empleadas para acceder, analizar o interferir con sistemas de terceros.

El proyecto se enfocará en registrar y analizar información disponible dentro del entorno autorizado, sin pretender determinar de manera absoluta que un comportamiento corresponde a un ataque.

Las alertas generadas por CyberGuard tendrán como finalidad facilitar la identificación de situaciones que requieran revisión. Esta aproximación resulta coherente con los procesos de identificación y análisis de eventos utilizados en la gestión de incidentes de seguridad (NIST, 2012).

20. CONCLUSIÓN

CyberGuard se plantea como un proyecto de Ingeniería de Sistemas enfocado en una problemática relacionada con el registro, monitoreo y análisis de eventos de seguridad en redes autorizadas.

La propuesta busca combinar los conocimientos de programación avanzada con una aplicación práctica orientada al análisis de información. En lugar de desarrollar únicamente un sistema de almacenamiento y consulta, se propone construir una plataforma capaz de procesar eventos, aplicar reglas, clasificar situaciones de riesgo y generar alertas para facilitar su seguimiento.

El proyecto utilizará Java como tecnología principal y estará organizado mediante una arquitectura modular por capas. Esta estructura permitirá separar las responsabilidades de los diferentes componentes y facilitará la integración progresiva del trabajo realizado por los integrantes.

El alcance establecido permitirá desarrollar una solución académica viable, evitando que el proyecto se extienda hacia funcionalidades propias de plataformas profesionales de seguridad informática. De esta manera, CyberGuard estará orientado al análisis de eventos y comportamientos potencialmente anómalos dentro de entornos autorizados y controlados.

La metodología Scrum permitirá organizar el desarrollo mediante ciclos de trabajo, actividades priorizadas y entregas progresivas. La planificación inicial también permitirá distribuir las responsabilidades entre los integrantes y establecer las fases necesarias para llevar el proyecto desde el análisis hasta las pruebas y documentación final.

Para esta primera entrega se han definido la problemática, la propuesta de solución, los objetivos, el alcance, las limitaciones, los módulos, los requerimientos, la arquitectura, las tecnologías, la metodología, los roles y la planificación inicial. El desarrollo del código, las pruebas y la validación del funcionamiento serán abordados en las siguientes etapas del proyecto.

REFERENCIAS

National Institute of Standards and Technology. (2012). *Computer security incident handling guide* (NIST Special Publication 800-61 Rev. 2). U.S. Department of Commerce. [https://doi.org/10.6028/NIST.SP.800-61r2](https://doi.org/10.6028/NIST.SP.800-61r2)

National Institute of Standards and Technology. (2024). *The NIST Cybersecurity Framework (CSF) 2.0*. U.S. Department of Commerce. [https://doi.org/10.6028/NIST.CSWP.29](https://doi.org/10.6028/NIST.CSWP.29)

Oracle. (s. f.). *Java documentation*. [https://docs.oracle.com/en/java/](https://docs.oracle.com/en/java/)

OWASP Foundation. (2021). *OWASP Top 10: The ten most critical web application security risks*. [https://owasp.org/www-project-top-ten/](https://owasp.org/www-project-top-ten/)

VMware. (s. f.). *Spring Boot documentation*. [https://docs.spring.io/spring-boot/index.html](https://docs.spring.io/spring-boot/index.html)

