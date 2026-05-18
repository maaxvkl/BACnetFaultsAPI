# BACnet Faults API

A Spring Boot based REST API for importing, storing and querying BACnet fault data exported from [BACnetAlarmScanner](https://github.com/maaxvkl/BACnetAlarmScanner/tree/v1.0)

The project demonstrates a practical OT/IT integration workflow: BACnet-related fault data is exported as Excel, imported through a REST API, parsed dynamically and persisted into PostgreSQL for further querying and analysis.

---

## Features

- Import BACnet fault exports from Excel
- Dynamic property parsing
- PostgreSQL persistence using JSONB
- REST API for querying and managing fault data
- JdbcTemplate-based SQL access
- Dockerized deployment
- Linux server test environment
- Structured backend architecture

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring JDBC / JdbcTemplate
- PostgreSQL
- JSONB
- Apache POI
- Docker
- Linux / Ubuntu Server
- Maven

---

## Architecture

```
BACnet Scanner
    ↓ Excel Export
    ↓ Spring Boot REST API
    ↓ Excel Parser
    ↓ Service Layer
    ↓ Repository Layer / JdbcTemplate
    ↓ PostgreSQL
    ↓ REST JSON Responses
```

---

## Use Case

The API was designed for importing BACnet fault exports from building automation or industrial environments.

The imported Excel files may contain dynamic property columns depending on:
- BACnet object type
- selected scanner properties
- export configuration

The application automatically maps these dynamic columns into JSONB structures without requiring database schema changes.

---

## Example Data Model

### Core Fields

| Field | Type |
|-------|------|
| device_ip | VARCHAR |
| data_type | VARCHAR |
| object_name | VARCHAR |
| object_description | TEXT |
| event_state | VARCHAR |

### Dynamic Properties

Stored inside PostgreSQL JSONB:

```json
{
  "presentValue": "true",
  "statusFlags": "inAlarm",
  "reliability": "noFaultDetected"
}
```

---

## PostgreSQL Schema

```sql
CREATE TABLE faults (
    id SERIAL PRIMARY KEY,
    device_ip VARCHAR(50),
    data_type VARCHAR(100),
    object_name VARCHAR(255),
    object_description TEXT,
    event_state VARCHAR(100),
    properties JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## REST Endpoints

### Import Excel File

**POST** `/faults/saveAll`
- Content-Type: `multipart/form-data`
- Imports an Excel export into PostgreSQL

### Get All Faults

**GET** `/faults/getAll`
- Returns all stored fault entries

### Get Faults By Data Type

**GET** `/faults/findBY/{dataType}`

Example:
```
GET /faults/findBy/binary-input
```

### Get All Device IPs

**GET** `/faults/getDeviceIps`
- Returns all stored device IP addresses

### Delete All Faults

**DELETE** `/api/deleteAll`
- Deletes all stored entries

---

## Example JSON Response

```json
{
  "deviceIp": "192.168.1.50",
  "dataType": "Binary Input",
  "objectName": "Fire Alarm",
  "objectDescription": "Main fire alarm input",
  "eventState": "FAULT",
  "properties": {
    "presentValue": "true",
    "statusFlags": "inAlarm"
  }
}
```

---

## Project Structure

```
src/main/java/
├── controller/        → REST endpoints
├── service/          → Business logic and orchestration
├── parser/           → Excel parsing and dynamic property handling
├── repository/       → JdbcTemplate SQL queries
└── model/            → Fault domain object
```

---

## Dynamic Excel Parsing

The parser supports dynamic BACnet property columns.

The first columns are treated as fixed fields:

| Column Index | Field Name |
|------|-----------|
| 0 | device_ip |
| 1 | data_type |
| 2 | object_name |
| 3 | object_description |
| 4 | event_state |

Additional columns are automatically mapped into JSONB:

```
Excel Header → JSON Key
Cell Value   → JSON Value
```

This allows flexible imports without changing the database schema.

---

## JdbcTemplate Approach

The project intentionally uses JdbcTemplate instead of a full ORM abstraction.

**Advantages:**
- Transparent SQL
- More control over queries
- Simpler debugging
- Lightweight persistence layer
- Good fit for integration services and import workflows

**Example query:**

```java
public List<Fault> findByDataType(String dataType) {
    String sql = """
        SELECT *
        FROM faults
        WHERE data_type = ?
    """;
     return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("object_name"), dataType );
}
```

---

## Linux Server Test Environment

The application was tested in a Linux-based server environment using Ubuntu Server.

The setup consists of:

```
Ubuntu Server VM
    ↓ PostgreSQL Database
    ↓ Spring Boot REST API
    ↓ Postman / External Client
```

Both the REST API and PostgreSQL run on the same Linux server.

The API connects internally via:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bacnetalarms
```

External clients access the API through:

```
http://SERVER_IP:8080/api/faults
```

This simulates a realistic backend deployment scenario where the database remains internal to the server.

---

## Docker

The application can be containerized using Docker.

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: bacnetalarms
      POSTGRES_USER: bacnetuser
      POSTGRES_PASSWORD: password123
    ports:
      - "5432:5432"

  api:
    build: .
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
```

---

## Build & Run

### Maven Build

```bash
mvn clean package -DskipTests
```

### Run With Docker Compose

```bash
docker compose up --build
```

### Run JAR Directly

```bash
java -jar app.jar
```

---

## Planned Improvements

- Swagger/OpenAPI documentation
- GitHub Actions CI/CD pipeline
- Docker Compose persistence volumes
- Integration tests
- Authentication and authorization
- Kubernetes deployment experiments

---

## Learning Focus

This project was built to explore practical backend and infrastructure topics related to OT/IT integration and Industrial IoT.

**Covered topics include:**

- REST API development
- PostgreSQL and SQL
- Linux server deployment
- Docker containerization
- Dynamic Excel parsing
- JSONB persistence
- Backend architecture
- JdbcTemplate
- Infrastructure fundamentals
- OT/IT integration workflows

---
