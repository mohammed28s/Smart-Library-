# Software Requirements Specification (SRS)
## Smart Library System

- **Version:** 1.0
- **Author:** Mohammed Salim
- **Date:** 2026

## 1. Introduction

### 1.1 Purpose
The purpose of this document is to define the requirements for the Smart Library System, an online platform that allows users to browse, rent, or purchase books and enables library staff to manage inventory and revenue.

This document will guide development tools (such as Codex) to implement the backend, frontend, and infrastructure of the system.

### 1.2 Scope
The Smart Library System provides:

- **Customer features:**
  - Browse books
  - Buy books
  - Rent books
  - Receive barcode for purchased/rented books
  - Cancel orders
  - Request refunds

- **Library staff features:**
  - Add books
  - Update books
  - Delete books
  - Track inventory
  - View purchases and rentals
  - View revenue statistics

The system will be built using:

- **Backend:** Java Spring Boot
- **Frontend:** Angular
- **Database:** SQLite
- **DevOps:** Docker, AWS deployment
- **IDE:** VS Code

### 1.3 Definitions
- **User:** Customer using the system to buy or rent books.
- **Worker:** Library employee managing books and system operations.
- **Barcode:** Unique code generated after a purchase or rental, scanned by library staff.
- **Order:** Transaction representing a purchase or rental.

## 2. Overall System Description

### 2.1 Product Perspective
The Smart Library System is a full-stack web application consisting of:

- **Frontend:** Angular application for customers and staff
- **Backend:** Spring Boot REST API
- **Database:** SQLite storing system data
- **Infrastructure:** Docker containers deployed to AWS

### 2.2 User Classes
- **Customer can:**
  - Browse books
  - Buy or rent books
  - View orders
  - Cancel purchase
  - Request refund

- **Library Worker can:**
  - Manage book inventory
  - Monitor orders
  - Track revenue
  - Scan barcodes for validation

### 2.3 System Architecture
Frontend (Angular)  
⬇  
REST API (Spring Boot)  
⬇  
Database (SQLite)

Deployed via Docker containers to AWS cloud infrastructure.

## 3. Functional Requirements

- **FR-1 User Registration:**
  - The system shall allow users to create accounts.
  - Fields: username, password, full name.

- **FR-2 User Login:**
  - The system shall authenticate users using login credentials.
  - Authentication should use JWT tokens.

- **FR-3 Book Catalog:**
  - Users shall be able to view list of books, search books, and view book details.
  - Each book contains: title, author, isbn, price, stock, description.

- **FR-4 Purchase Book:**
  - Users shall be able to purchase books.
  - Steps: select book, choose quantity, confirm purchase, generate order.
  - System generates a barcode for the order.

- **FR-5 Rent Book:**
  - Users shall be able to rent books.
  - Rental includes: rental start date, due date.
  - Barcode will be generated for the rental.

- **FR-6 Barcode Generation:**
  - After purchase or rental, the system shall generate a unique barcode representing the order.
  - Barcode must be scannable by library scanners.

- **FR-7 Cancel Order:**
  - Users may cancel orders if the order has not been processed.
  - Order status changes to `CANCELLED`.

- **FR-8 Refund Request:**
  - Users may request refunds.
  - Process: user submits request, worker reviews request, system processes refund.
  - Order status becomes `REFUNDED`.

- **FR-9 Book Management (Worker):**
  - Workers shall be able to add, update, and delete books.
  - Fields: title, author, price, stock, description.

- **FR-10 Inventory Tracking:**
  - Workers shall be able to see total books, available stock, rented books, sold books.

- **FR-11 Revenue Analytics:**
  - Workers shall view total revenue, daily revenue, number of purchases, number of rentals.

## 4. Database Requirements

Required tables:
- users
- books
- orders
- order_items
- payments

Each order must contain:
- unique barcode
- order type (`BUY` or `RENT`)
- order status

## 5. External Interface Requirements

- **Frontend Interface:** Angular web app with customer dashboard and worker admin panel.
- **Barcode Interface:** Barcode generated using ZXing, format `CODE-128`.
- **Payment Interface:** Initial test payment gateway; future Stripe integration.

## 6. Non-Functional Requirements

- **Performance:**
  - 100 concurrent users
  - API response time under 2 seconds

- **Security:**
  - JWT authentication
  - Role-based authorization (`USER`, `WORKER`)

- **Reliability:**
  - Uptime target 99%

- **Scalability:**
  - Support migration from SQLite to PostgreSQL as needed

## 7. Deployment Requirements

Application must support:
- Docker container deployment
- AWS cloud target

Possible AWS services:
- ECS
- EC2
- Elastic Beanstalk

## 8. Future Enhancements

- AI book recommendations
- Mobile app
- QR code scanning
- Email notifications
- Book rating system
