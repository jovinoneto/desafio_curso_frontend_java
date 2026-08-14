## Backend Integration

The frontend communicates with the [Course API Backend](https://github.com/jovinoneto/desafio_curso_backend_java_api) through REST API calls using Spring's `RestTemplate`.

The backend provides authentication, authorization, and the main business resources consumed by the frontend.

### Main Resources

| Resource       | Endpoint      | Purpose             |
| -------------- | ------------- | ------------------- |
| Authentication | `/auth/login` | User authentication |
| Users          | `/users`      | User management     |
| Courses        | `/courses`    | Course management   |
| Categories     | `/categories` | Category management |

## Authentication

Authentication is handled by Spring Security through a custom `ExternalAuthenticationProvider`, which delegates credential validation to the backend API.

The authentication flow is:

1. The user submits their email and password.
2. The frontend sends the credentials to `/auth/login`.
3. The backend validates the credentials and returns a JWT token and user information.
4. The frontend maintains the authenticated user in the Spring Security context.
5. The JWT is included as a `Bearer` token in requests to protected backend endpoints.
6. Access to protected pages and operations is controlled according to the user's role.

## Getting Started

### Prerequisites

* **Java 21** or higher
* **Maven 3.6+**
* **PostgreSQL** (required by the backend)
* The [Course API Backend](https://github.com/jovinoneto/desafio_curso_backend_java_api) running on `http://localhost:8080`

### 1. Clone the Repository

```bash
git clone https://github.com/jovinoneto/desafio_curso_frontend.git
cd desafio_curso_frontend
```

### 2. Configure the Backend URL

Configure the backend URL in the frontend application:

```properties
backend.url=http://localhost:8080
```

### 3. Start the Backend

Open a terminal in the backend project directory and run:

```bash
./mvnw spring-boot:run
```

The backend API should be available at:

```text
http://localhost:8080
```

### 4. Start the Frontend

From the frontend project directory, run:

```bash
./mvnw spring-boot:run
```

The frontend will start using the configured backend API.

### 5. Access the Application

Open the application in your browser using the port configured for the frontend.
