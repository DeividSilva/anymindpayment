# E-commerce Payment System

## Overview

This project is an e-commerce payment system that manages:
- Payment processing with varying commission rates and points systems based on payment methods.
- Transaction tracking with reports by date ranges.

It uses **Java 21** with **Spring Boot 3.4** and includes configurations for **PostgreSQL** as the database.

---

## Architecture

### Diagram: System Architecture
```mermaid
flowchart TD
    Client[Client API] -->|Requests| API[Spring Boot Application]
    API -->|Uses| Controller[Controllers]
    Controller -->|Delegates| Service[Services]
    Service -->|Checks/Updates Cache| Cache[Redis Cache]
    Service -->|Interacts| Repository[Repositories]
    Repository -->|Queries| Database[PostgreSQL]
    API -->|Swagger UI| Client
```

---

## Sequence Diagrams

### Payment Flow
```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Service
    participant Cache
    participant Repository
    participant DB

    Client->>API: POST /api/payment
    API->>Service: Validate request
    Service->>Cache: Check if Payment Method Exists on Cache
    Cache->>Service: Retrieve Payment Method if it Exists on Cache
    Service->>Repository: Retrieve Payment Method
    Repository->>DB: Query Payment Method
    DB->>Repository: Return Payment Method
    Repository->>Service: Return Payment Method
    Service->>Cache: Save on Cache if not Exists and Payment Method not null
    Service->>Service: Calculate final price and points
    Service->>Repository: Save transaction
    Repository->>DB: Insert transaction
    DB->>Repository: Confirm insert
    Repository->>Service: Confirm save
    Service->>API: Return final price and points
    API->>Client: Response with result
```

### Transaction Report
```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Service
    participant Repository
    participant DB

    Client->>API: POST /api/admin/sales
    API->>Service: Validate date range
    Service->>Repository: Query transactions by date range
    Repository->>DB: Execute query
    DB->>Repository: Return transactions
    Repository->>Service: Return transactions
    Service->>API: Return aggregated report
    API->>Client: Response with report
```

---

## How to Run

### Prerequisites
1. **Docker** and **Docker Compose** installed.
2. **JDK 21** and **Maven** if running locally.

### Steps to Run
1. **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd anymindpayment
    ```

2. **Run with Docker**:

   The API is built directly within the Dockerfile during the image creation process.
    ```bash
    docker-compose up --build
    ```

3. **Access the application**:
   - API Documentation (Swagger UI): [http://localhost:8090/swagger-ui](http://localhost:8080/swagger-ui)

4. **Stop the application**:
    ```bash
    docker-compose down
    ```

---

## Key Endpoints

### Payment Processing
- **POST** `/api/payment`: Process a payment and calculate final price and points.

### Transaction Report
- **POST** `/api/admin/sales`: Fetch sales and points data within a date range.

---

## Testing

To execute unit and integration tests:
```bash
mvn test
```

---

