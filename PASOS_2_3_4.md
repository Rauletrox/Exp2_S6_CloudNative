# Paso 2, 3 y 4

## Paso 2: Modelo de datos

Se creó la entidad `GuiaDespacho` con la tabla `guia_despacho`.

Campos incluidos:

- `id`
- `numeroGuia`
- `transportista`
- `fechaDespacho`
- `cliente`
- `direccionEntrega`
- `estado`
- `archivoS3`
- `createdAt`
- `updatedAt`

Archivo:

- `src/main/java/com/appcloud/gestiondepedidosyguias/entity/GuiaDespacho.java`

## Paso 3: CRUD completo

Se implementó un controlador REST con los endpoints solicitados:

- `POST /api/guias`
- `GET /api/guias`
- `PUT /api/guias/{id}`
- `DELETE /api/guias/{id}`
- `GET /api/guias/buscar?transportista=&fecha=`
- `POST /api/guias/{id}/upload`
- `GET /api/guias/{id}/download`

Archivos principales:

- `src/main/java/com/appcloud/gestiondepedidosyguias/controller/GuiaDespachoController.java`
- `src/main/java/com/appcloud/gestiondepedidosyguias/service/GuiaDespachoService.java`
- `src/main/java/com/appcloud/gestiondepedidosyguias/service/impl/GuiaDespachoServiceImpl.java`
- `src/main/java/com/appcloud/gestiondepedidosyguias/repository/GuiaDespachoRepository.java`
- `src/main/java/com/appcloud/gestiondepedidosyguias/service/S3StorageService.java`
- `src/main/java/com/appcloud/gestiondepedidosyguias/service/impl/AwsS3StorageService.java`

## Paso 4: Spring Security

Se configuró Spring Security como Resource Server JWT para integrar Azure AD B2C.

Reglas:

- `GET /api/guias/*/download` requiere `ROLE_LECTOR`
- `"/api/guias/**"` requiere `ROLE_ADMIN`

Archivo:

- `src/main/java/com/appcloud/gestiondepedidosyguias/config/SecurityConfig.java`

## Configuracion de ejemplo

Variables principales en `src/main/resources/application.properties`:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`
- `AWS_REGION`
- `AWS_S3_BUCKET_NAME`

## Paso 5: Docker

Se agregaron los archivos necesarios para ejecutar el proyecto con Docker:

- `.dockerignore`
- `.env`
- `docker-compose.yml`
- `Dockerfile`

Con esto el proyecto puede correr en:

- Local
- Docker
- EC2

sin cambiar el codigo de Spring Boot, solo modificando variables de entorno.
