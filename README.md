# Blackjack API

## 📝 Descripción
Este proyecto es una API REST desarrollada con **Spring Boot** y **Spring WebFlux** para gestionar partidas de Blackjack de manera reactiva. Utiliza **MongoDB** para almacenar las partidas y **MySQL** para gestionar los jugadores y el ranking. La API está dockerizada y puede ejecutarse tanto de forma local como en contenedores Docker.

---

## 🗃️ Estructura de la Base de Datos

### 📌 MongoDB (Partidas de Blackjack)
- **`games` (Colección)**: Almacena las partidas de Blackjack.
    - `_id`: Identificador de la partida.
    - `playerId`: ID del jugador asociado.
    - `playerCards`: Lista de cartas del jugador.
    - `dealerCards`: Lista de cartas del dealer.
    - `status`: Estado de la partida (EN_PROGRESO, TERMINADA).
    - `moves`: Historial de jugadas realizadas.

### 📌 MySQL (Jugadores y Ranking)
- **`players` (Tabla)**: Almacena información de los jugadores.
    - `id`: Identificador del jugador.
    - `name`: Nombre del jugador.

- **`rankings` (Tabla)**: Guarda la puntuación de los jugadores.
    - `id`: Identificador del ranking.
    - `player_id`: Referencia a `players.id`.
    - `points`: Puntuación acumulada.

---

## 💻 Tecnologías Utilizadas
- **Java 17**: Lenguaje de programación principal.
- **Spring Boot 3**: Framework para la API.
- **Spring WebFlux**: Arquitectura reactiva.
- **R2DBC**: Conexión reactiva con MySQL.
- **MongoDB**: Base de datos NoSQL para partidas.
- **MySQL**: Base de datos relacional para jugadores y ranking.
- **Docker & Docker Compose**: Contenedorización de la aplicación.
- **Swagger/OpenAPI**: Documentación de la API.

---

## 📊 Requisitos
- **Java 17+**: Para ejecutar la aplicación.
- **Maven**: Para la gestión de dependencias.
- **MongoDB**: Servidor en ejecución (local o Docker).
- **MySQL**: Servidor en ejecución (local o Docker).
- **Docker**: Para ejecutar en contenedores.

---

## 🛠️ Instalación

### 🔹 Opcion 1: Ejecución Local
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/eze-ms/Blackjack-API-Backend
   cd blackjack-api
   ```
2. Configurar `application.properties` para conectar a bases de datos locales.
3. Iniciar MySQL y MongoDB.
4. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

### 🔹 Opcion 2: Ejecución con Docker
1. Construir y levantar los contenedores:
   ```bash
   docker-compose up -d --build
   ```
2. La API estará disponible en `http://localhost:8081`.

---

## 📌 Endpoints Principales

| Método | Endpoint               | Descripción |
|---------|------------------------|-------------|
| **POST** | `/game/new`            | Crear una nueva partida |
| **GET**  | `/game/{id}`           | Obtener detalles de una partida |
| **POST** | `/game/{id}/play`      | Realizar una jugada |
| **DELETE** | `/game/{id}/delete`  | Eliminar una partida |
| **GET**  | `/ranking`             | Obtener ranking de jugadores |
| **PUT**  | `/player/{playerId}`   | Cambiar nombre de un jugador |

---

## 🚀 Despliegue en Docker Hub

### **Tareas completadas**
- **Creación del `Dockerfile` y `.dockerignore`.**
- **Construcción y etiquetado de la imagen.**
- **Iniciar sesión en Docker Hub.**
- **Subida de la imagen al repositorio: `docker.io/ezemsdev/blackjack-api:latest`**
- **Pruebas exitosas en contenedores Docker.**

---

## 📢 Notas
- **Swagger UI** está disponible en: `http://localhost:8081/swagger-ui.html`.
- Para detener los contenedores Docker:
  ```bash
  docker-compose down
  ```

---
© 2025. Proyecto desarrollado por Ezequiel Macchi Seoane.

