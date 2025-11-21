# cuentas-de-cobro-Readme

Created by: Pastor Emilio Betancourt O
Created time: Jun 10, 2025

**README.md - Documentación de cuentas de cobro**

** Captura de Pantalla

<img width="945" height="761" alt="cuentasdecobroimagen" src="https://github.com/user-attachments/assets/d4927c3a-40e0-48be-9067-4e96ed7aa3f6" />

<img width="747" height="601" alt="image" src="https://github.com/user-attachments/assets/482dc9e4-5c00-46a1-81d9-08e47bc9c648" />


### Arquitectura del Sistema

Diagrama de Arquitectura

<img width="813" height="616" alt="image" src="https://github.com/user-attachments/assets/4932b87e-ef80-4c9f-a21f-4c8b2f9a9212" />

### Base de datos

<img width="1353" height="748" alt="image" src="https://github.com/user-attachments/assets/fed48240-b8d9-49c7-a4c0-5c917380cc91" />


### Estructura de Carpetas y Archivo

El sistema esta compuesto por tres principales estructuras de codigo: **korlon**, **Phobos**, cada una con su propia organización y propositos especificos.

📂 Estructura de Carpetas
🔹 Frontend (Brainiac)
```

/public
   ├── icons/
   ├── favicon.ico
/src
   ├── app/            # Páginas y lógica Next.js
   ├── domain/         # Modelos, repositorios y casos de uso
   ├── infrastructure/ # Persistencia, IoC, APIs externas
   ├── lib/            # Config, interfaces, utils
   ├── presentation/   # UI (Atomic Design: atoms, molecules, organisms, templates)
   ├── types/          # Definiciones de tipos globales
   ├── utils/          # Funciones auxiliares
```
```
🔹 Backend 1 (Korlon)
/src/main/java/com/comfandi/korlon
   ├── api/            # DTOs y contratos de entrada/salida
   ├── config/         # Configuración de Spring Boot
   ├── controller/     # Controladores REST
   ├── entities/       # Entidades JPA
   ├── enums/          # Enumeraciones de dominio
   ├── manager/        # Lógica de negocio
   ├── mapper/         # MapStruct / ModelMapper
   ├── repositories/   # Repositorios JPA
   ├── services/       # Servicios de negocio
   ├── utils/          # Utilidades
/resources
   ├── jasper/         # Reportes Jasper
   └── application.properties
```
```
🔹 Backend 2 (Phobos)
/src/main/java/com/comfandi/phobos
   ├── api/            # Interfaces de exposición
   ├── config/         # Configuración de Spring Boot
   ├── controller/     # Endpoints REST
   ├── entity/         # Entidades JPA
   ├── exception/      # Manejo de errores
   ├── mapper/         # MapStruct / ModelMapper
   ├── repository/     # Repositorios JPA
   ├── service/        # Lógica de negocio
   ├── util/           # Funciones auxiliares
/resources
   ├── db/migrations/  # Scripts de migraciones
   └── application.properties
```

### Patrones de Diseño y Modularización

- Frontend (Brainiac – Next.js + Clean Architecture)

- Clean Architecture → Separación clara entre domain, infrastructure, presentation.

- Repository Pattern → Acceso a datos desacoplado.

- Use Cases (CQRS) → Reglas de negocio encapsuladas.

- IoC / Dependency Injection → Gestión de dependencias en ioc/.

- Atomic Design → Componentes UI modulares y reutilizables.

- NextAuth + Keycloak → Autenticación segura.

- Backend (Korlon & Phobos – Spring Boot)

- DDD (Domain-Driven Design) → Organización en paquetes por dominio (controller, service, repository).

- Repository Pattern → Uso de JPA para abstracción de acceso a datos.

- DTO & Mapper Pattern → Conversión entre entidades y DTOs.

- JasperReports (Korlon) → Generación de reportes PDF.

- Spring Scheduler (Phobos) → Procesos programados.

- Integration Pattern → Comunicación entre microservicios y APIs externas.

### Variables de Entorno
🔹 Phobos
spring.application.name=phobos
server.port=8081
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

api.korlon_baseurl=${KORLON_BASE_URL}
api.korlon-user-name=${KORLON_USER_NAME}
api.korlon-user-password=${KORLON_USER_PASSWORD}
aws.region=${AWS_REGION}
aws.bucket-name=${AWS_BUCKET_NAME}
aws.access-key=${AWS_ACCESS_KEY}
aws.secret-key=${AWS_SECRET_KEY}

🔹 Korlon
spring.application.name=korlon
server.port=8082
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

app.file-path-template=${KORLON_TEMPLATE_PATH}
app.file-path-download=${KORLON_DOWNLOAD_PATH}
spring.mail.host=${SMTP_MAIL_HOST}
spring.mail.port=587
spring.mail.username=${SMTP_MAIL_USERNAME}
spring.mail.password=${SMTP_MAIL_PASSWORD}

🔹 Brainiac (Frontend)
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=secret_key
NEXT_PUBLIC_API_URL=http://localhost:8081/api
KEYCLOAK_CLIENT_ID=cuentas-client
KEYCLOAK_ISSUER=http://localhost:8080/realms/comfandi

### 🚀 Levantar el Proyecto
Frontend
cd brainiac
yarn install
yarn dev

Backend Korlon
cd korlon
./mvnw spring-boot:run

Backend Phobos
cd phobos
./mvnw spring-boot:run

Con Docker
docker-compose up --build

### 📦 Dependencias Clave
Frontend

Next.js

Redux Toolkit

Formik + Yup

TailwindCSS + MUI

Inversify

NextAuth.js

Backend

Spring Boot

Spring Data JPA

PostgreSQL

JasperReports (Korlon)

AWS SDK

Lombok

Maven


