# Java Demo App

A Spring Boot REST API for practicing local development, Docker, Amazon ECR, ECS Fargate, and EKS.

## Stack
- Java 17
- Spring Boot 3.5.x
- Maven
- REST API and JUnit 5
- Docker (later phase)
- Amazon ECR, ECS Fargate, EKS (later phases)
- Terraform and Jenkins (later phases)

## Prerequisites
- JDK 17
- Maven 3.9+
- PowerShell on Windows
- Docker Desktop for the Docker phase

## Run locally
From the project root:

```powershell
mvn spring-boot:run
```

Default port: `8080`.

## Endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/` | Application information |
| GET | `/health` | Basic application health |
| GET | `/api/v1/message` | Example JSON message |
| GET | `/api/v1/info` | Application name, version, Java runtime |

## Environment variables
| Variable | Default |
|---|---|
| `APP_NAME` | `java-demo-app` |
| `APP_VERSION` | `1.0.0` |
| `SERVER_PORT` | `8080` |
| `LOG_LEVEL` | `INFO` |
| `APP_LOG_LEVEL` | `INFO` |

Example PowerShell configuration (applies to processes started in this same terminal):

```powershell
$env:APP_NAME = "java-demo-app"
$env:APP_VERSION = "1.0.0"
$env:SERVER_PORT = "8080"
mvn spring-boot:run
```

## Build and test (Phase 2)
```powershell
mvn clean test
mvn clean package
```

The JAR is generated at `target/java-demo-app.jar`.

## Docker (Phase 3)
Build the JAR first, then build and run the image:

```powershell
mvn clean package
docker build -t java-demo-app:1.0.0 .
docker run --rm -p 8080:8080 java-demo-app:1.0.0
```

## Deployment roadmap
1. Local application
2. API and unit test verification
3. Docker image
4. Amazon ECR
5. ECS Fargate
6. EKS and Kubernetes
7. Terraform and Jenkins CI/CD
8. Monitoring, rollback, and cleanup

## Security
- Do not commit credentials or secrets.
- Use secure AWS authentication when AWS deployment begins.
- Container runs as a non-root user.
- Review AWS costs before creating billable infrastructure.
