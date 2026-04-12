# Farm Market — Microservices Backend

Farm Market is a full-stack microservices-based marketplace platform where suppliers manage products, users place orders, and moderators control content.

The system is built using an event-driven architecture with Kafka and secured with JWT authentication through an API Gateway.

---

## Features

- JWT authentication with access and refresh tokens
- Role-based access control (USER, SUPPLIER, MODERATOR, ADMIN)
- API Gateway for centralized security and routing
- Supplier product management (CRUD)
- Product moderation (block/unblock)
- Order creation and payment flow
- Kafka-based event-driven communication
- Frontend with React + Redux and protected routes

---

## Architecture

The system consists of multiple services:

- **api-gateway** — centralized security, routing, CORS
- **auth-service** — authentication, JWT, roles
- **product-service** — product catalog and inventory
- **order-service** — orders and payment handling

Infrastructure:

- **MongoDB** — data storage
- **Kafka (Confluent Cloud)** — event streaming

Flow:

Client → API Gateway → Services → MongoDB  
                     ↘ Kafka
---
## 👥 Roles

- **USER**
  - browse products
  - create orders

- **SUPPLIER**
  - create / update / delete own products

- **MODERATOR**
  - block/unblock products
  - verify orders

- **ADMIN**
  - manage users and roles

---

## 🔐 Security

- JWT authentication
- Access token in `Authorization` header
- Refresh token in `httpOnly` cookie
- Stateless authentication
- API Gateway validates JWT and forwards:
  - `X-User-Login`
  - `X-User-Roles`

---

## 🔄 Authentication Flow

1. User logs in via auth-service
2. System returns:
   - access token (header)
   - refresh token (cookie)
3. Client sends requests via API Gateway
4. Gateway validates JWT
5. Gateway forwards user info via headers
6. Services authorize requests based on roles
7. Refresh endpoint issues new access token

---

## Kafka Events

The system uses Kafka for asynchronous communication:

- `product-created`
- `product-updated`
- `product-deleted`
- `order-paid`

Kafka enables loose coupling between services and scalability.

---

## Configuration

The project uses environment variables for sensitive data.

Create a `.env` file (or use deployment environment):

- MongoDB URI
- JWT secrets
- Kafka credentials
- Cloud storage API keys

A `.env.example` file is provided.

---

## Run Locally

```bash
docker-compose up --build
