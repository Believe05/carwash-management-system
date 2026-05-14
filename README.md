# 🚗 Car Wash Management System

A full-stack web application for managing car wash bookings, payments, and customer loyalty rewards. Built with Spring Boot and deployed on Render.

## 🌐 Live Demo

**URL:** [https://carwash-management-system-ck3y.onrender.com](https://carwash-management-system-ck3y.onrender.com)

> ⚠️ **Note:** Free tier spins down after 15 minutes of inactivity. First visit may take 30-50 seconds to wake up.

---

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Architecture](#system-architecture)
- [Database Schema](#database-schema)
- [Installation & Setup](#installation--setup)
- [Deployment](#deployment)
- [Usage Guide](#usage-guide)
- [Project Structure](#project-structure)
- [Contributors](#contributors)
- [License](#license)

---

## ✨ Features

### 👤 Client Portal
- User registration and login (Spring Security)
- Book car wash services (Basic, Premium, Deluxe)
- View booking history and status
- Make payments (Credit Card, Cash, UPI)
- View loyalty points and membership tier
- Automatic loyalty points earning on payments

### 👔 Admin Portal
- Dashboard with key metrics
- Manage customers
- Approve/reject bookings
- Mark bookings as completed
- Generate daily reports
- View analytics with charts
- Manage loyalty program

### 💎 Loyalty Program
- **Bronze:** 0-499 points (0% discount, 10 points per R10)
- **Silver:** 500-999 points (5% discount, 12 points per R10)
- **Gold:** 1000+ points (10% discount, 15 points per R10)

### 📊 Reports & Analytics
- Daily revenue reports
- Monthly performance tracking
- Service popularity charts
- Top customers leaderboard
- Revenue trend graphs

### 📱 Additional Features
- WhatsApp integration on contact page
- Printable payment receipts
- Responsive design (Bootstrap 5)

---

## 🛠 Technologies Used

| Category | Technology |
|----------|------------|
| **Backend** | Spring Boot 3.2.0, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf, Bootstrap 5, HTML5, CSS3 |
| **Database** | PostgreSQL (Production), MySQL (Local Development) |
| **Charts** | Chart.js |
| **Icons** | Font Awesome 6 |
| **Build Tool** | Maven |
| **Deployment** | Render (PaaS) |
| **Version Control** | Git / GitHub |

---


## 🏗 System Architecture
┌─────────────────────────────────────────────────────────────┐
│ Client Browser │
│ (Desktop / Mobile) │
└─────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────┐
│ Render Cloud Platform │
│ ┌─────────────────────┐ ┌─────────────────────────────┐ │
│ │ Web Service │ │ PostgreSQL Database │ │
│ │ (Spring Boot) │◄──►│ (Render Managed) │ │
│ │ Port: 8080 │ │ │ │
│ └─────────────────────┘ └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────┐
│ GitHub Repository │
│ (Source Code Management) │
└─────────────────────────────────────────────────────────────┘



---

## 📊 Database Schema

### Tables Structure

| Table | Description |
|-------|-------------|
| `users` | User authentication and roles |
| `customers` | Customer details and loyalty info |
| `bookings` | Car wash bookings |
| `payments` | Payment transactions |
| `loyalty_transactions` | Loyalty points history |

### ER Diagram
users (1) ──────► (1) customers (1) ──────► (many) bookings (1) ──────► (1) payments
│
└─────────────────────► (many) loyalty_transactions



---

## 🚀 Installation & Setup

### Prerequisites

- Java 17 or higher
- MySQL (for local development)
- Maven
- Git

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Believe05/carwash-management-system.git
   cd carwash-management-system
Configure MySQL database

Create a database named carwash_db

Update src/main/resources/application.properties with your credentials

Build the project

bash
mvn clean package
Run the application

bash
mvn spring-boot:run
Access the application

Open browser: http://localhost:8080

Default Credentials (After Registration)
Role	Email	Password
Admin	(register as Admin)	(your chosen password)
Client	(register as Client)	(your chosen password)

---
### 🌍 Deployment
Deployed on Render
Web Service: Spring Boot application running on Render

Database: PostgreSQL managed by Render

Auto-deploy: Connected to GitHub, deploys on push

### Environment Variables (Render)
Variable	Description
SPRING_DATASOURCE_URL	PostgreSQL connection URL
SPRING_DATASOURCE_USERNAME	Database username
SPRING_DATASOURCE_PASSWORD	Database password
SPRING_PROFILES_ACTIVE	prod (activates production config)

---
### 📖 Usage Guide
### Client Flow
Sign Up - Create a new account (select "Client" role)

Login - Access your dashboard

Book a Wash - Select service type and vehicle details

Wait for Admin - Admin approves and completes the booking

Make Payment - Pay via Credit Card, Cash, or UPI

Earn Points - Loyalty points automatically added

Check Status - View booking history and payment status

### Admin Flow
Login as Admin

Dashboard - View statistics and quick actions

Manage Bookings - Approve or reject pending bookings

Complete Bookings - Mark as completed when service is done

Daily Reports - Generate reports for any date

Analytics - View charts and customer leaderboard

Loyalty Program - Monitor points distribution

---

## 📁 Project Structure


carwash-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/carwash/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── static/css/      # Stylesheets
│   │       ├── templates/       # Thymeleaf templates
│   │       │   ├── admin/       # Admin pages
│   │       │   ├── client/      # Client pages
│   │       │   └── *.html       # Public pages
│   │       └── application.properties
│   └── test/                    # Unit tests
├── Dockerfile                    # Docker configuration
├── pom.xml                       # Maven dependencies
└── README.md                     # This file

---

## 👥 Contributors
Name	Role
Believe05	Full Stack Developer
Reflection Statement
Each team member has written a 1000-word reflection statement describing their role in the project and what they learned throughout the semester. These are available in the project documentation.

---

## 🔧 Troubleshooting
Common Issues
Issue	Solution
Database connection error	Check environment variables in Render
Tables not created	Set spring.jpa.hibernate.ddl-auto=create first time
Payment not working	Ensure admin has marked booking as COMPLETED
Login fails	Verify email and password, check user role
Slow first load	Free tier cold start - wait 30-50 seconds
Support
For issues, contact:

Email: support@carwashpro.co.za

WhatsApp: +27 82 123 4567 (via website)

### 📄 License
This project was developed for educational purposes as part of the INT316D course requirement.

### 🙏 Acknowledgments
Spring Boot Documentation

Render Cloud Platform

Bootstrap 5

Chart.js

Font Awesome.
