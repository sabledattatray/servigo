# 🏠 ServiGo – HyperLocal Home Services Platform

> A full-stack hyperlocal on-demand service marketplace connecting customers with verified local technicians for home repair and daily services like plumbing, electrical work, AC repair, cleaning, and more.

---

## 🚀 Overview

**ServiGo** is a scalable, fast, and modern service marketplace built for Android + Web ecosystems. It allows users to book home services instantly, track technicians in real-time, and pay securely.

It includes:
- 📱 Customer Mobile App (Android)
- 🔧 Technician App (Android)
- 🧑‍💼 Admin Dashboard (Web)
- ⚙️ Node.js Backend API

---

## ✨ Key Features

### 👨‍👩‍👧 Customers
- OTP-based login
- Service browsing & search
- Instant booking system
- Live technician tracking
- UPI & Cash payments
- Ratings & reviews
- Booking history

### 🔧 Technicians
- Registration & KYC verification
- Job request management
- Earnings dashboard
- Availability toggle
- Performance ratings

### 🧑‍💼 Admin Panel
- Manage users & technicians
- Add/edit services & pricing
- Booking control system
- Revenue tracking
- Ratings moderation
- Support ticket system

---

## 🧠 Smart Features
- AI-based service suggestions
- Auto technician matching algorithm
- Distance-based assignment
- Dynamic pricing support
- Fraud detection system (future-ready)

---

## 🏗️ Tech Stack

| Layer | Technology |
|------|------------|
| Mobile App | Flutter |
| Backend | Node.js (Express/NestJS) |
| Database | MongoDB |
| Admin Panel | Next.js |
| Auth | JWT + OTP (Firebase optional) |
| Maps | Google Maps API |
| Payments | Razorpay API |

---

## 📸 Screenshots

> *(Replace these with real screenshots after UI development)*

### 🏠 Home Dashboard
![Home](screenshots/home.png)

### 📦 Service Booking Flow
![Booking](screenshots/booking.png)

### 🧑‍🔧 Technician Dashboard
![Technician](screenshots/technician.png)

### 🧑‍💼 Admin Panel
![Admin](screenshots/admin.png)

---

## 📁 Project Structure

```bash
servigo-platform/
│
├── mobile-app/
│   ├── customer-app/
│   ├── technician-app/
│
├── backend/
│   ├── controllers/
│   ├── routes/
│   ├── models/
│   ├── middleware/
│
├── admin-panel/
│   ├── pages/
│   ├── components/
│
├── docs/
├── database/
└── README.md
