# Currency Converter

Currency Converter is a simple Java-based application that allows users to convert amounts from one currency to another.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Before you begin, ensure you have the following installed:
- Java JDK 11
- Maven
- Docker
- Docker Compose

### Building the Application

To build the application and its Docker image, run the following command from the root of the project:

```bash
mvn clean package
```

### Running the Application

To run the application, use Docker Compose with the appropriate environment file. You can choose between production and development environments.

#### Development:

```bash
docker-compose -f docker-compose.yaml --env-file .config/.env.dev up
```

#### Production:

```bash
docker-compose -f docker-compose.yaml --env-file .config/.env.prod up
```

### Using the Application

Once the application is running, you can convert currencies through the following endpoint:

```http
POST /currency/convert
```

#### Example Request

```json
{
  "from": "EUR",
  "to": "USD",
  "amount": "5.16"
}
```

### API Documentation

You can access the Swagger UI to explore the API documentation at:

```http
GET /swagger-ui.html
```

This page provides detailed information about the API endpoints, including request formats and available parameters.



