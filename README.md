# Farm Market — Microservices Backend

Farm Market is a microservices-based backend for an online marketplace
where suppliers manage products, users place orders, and all services
communicate via Kafka using an event-driven architecture.

## Tech Stack
- Java 21
- Spring Boot 3
- Spring Security + JWT
- MongoDB
- Kafka (Confluent Cloud)
- Docker & Docker Compose
- API Gateway
- React + Redux (frontend)

## Architecture
- api-gateway — centralized security, routing and CORS
- auth-service — authentication and role management
- product-service — product catalog and inventory
- order-service — orders and payments

## Roles
- USER — browse products, create orders
- SUPPLIER — manage own products
- MODERATOR — block/unblock products, verify orders
- ADMIN — manage users and roles

## Security
- JWT authentication
- Access token in Authorization header
- Refresh token in httpOnly cookie
- Gateway propagates X-User-Login / X-User-Roles

## Kafka Events
- product-created
- product-updated
- product-deleted
- order-paid


## Configuration

The project uses environment variables for sensitive configuration.

Before running the application, create a `.env` file (or use your deployment
platform settings) and provide the following variables:

- MongoDB connection URI
- JWT secret keys
- Kafka bootstrap servers and credentials
- Image storage service API keys (cloud-based image hosting)

A `.env.example` file is provided as a reference.

## How to run
- docker-compose up
