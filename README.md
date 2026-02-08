# SalesSavvy Backend

SalesSavvy is a backend service for an e-commerce application built using **Java Spring Boot**.  
It provides secure REST APIs with **JWT-based authentication**, **role-based authorization**, and seamless integration with a MySQL database.

---

## Features

- User authentication and authorization using JWT
- Role-based access control (ADMIN & CUSTOMER)
- Secure login and logout functionality
- Product, cart, order, and payment management APIs
- RESTful API architecture
- Exception handling and validation
- MySQL database integration

---

##  Tech Stack

- **Backend:** Java, Spring Boot
- **Security:** Spring Security, JWT
- **Database:** MySQL
- **Build Tool:** Maven
- **API Style:** REST
- **Version Control:** Git & GitHub

---

##  User Roles

### * Admin
- Manage products
- View and manage orders
- Admin-specific secured endpoints
- View Business details

### * Customer
- User registration and login
- Browse products
- Add to cart
- Place orders
- Make payments

---

## 📂 Project Structure

```text
sales-savvy-backend
├── src/main/java
│   └── com.salessavvy.app
│       ├── admin
│       │   ├── controller
│       │   ├── service
│       │   └── service.impl
│       ├── user
│       │   ├── controller
│       │   ├── service
│       │   ├── service.impl
│       │   └── repository
│       └── common
│           └── entity
│
├── src/main/resources
│   ├── application.properties
│   ├── static
│   └── templates
│
├── src/test/java
│   └── com.salessavvy.app
│
├── pom.xml
├── Dockerfile
└── README.md


