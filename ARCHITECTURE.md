# 🏗️ ServiGo – Complete System Architecture Document

## 1. System Overview & Technology Stack
ServiGo is a production-grade hyperlocal on-demand home services marketplace, architected as a highly scalable, multi-sided platform (Customer, Provider, Admin). It brings an "Uber-like" live dispatch, dynamic pricing, and matching experience to the home maintenance sector.

**Enterprise Tech Stack:**
*   **Mobile Apps (Customer & Provider):** Flutter (Dart) for unified iOS/Android codebases with high native performance.
*   **Web Dashboard (Admin):** Next.js (React), Tailwind CSS, TypeScript.
*   **Backend API:** Node.js using NestJS (strict TypeScript, dependency injection, Event-Driven Architecture).
*   **Primary Database:** PostgreSQL (relational integrity for payments/bookings) + PostGIS extension for high-performance geospatial queries.
*   **In-Memory Store & Queues:** Redis for live location caching, Pub/Sub, and BullMQ for the Dispatch Queue.
*   **Real-time Engine:** WebSocket Gateway via Socket.IO.
*   **Observability Layer:** Pino/Winston (Logs), Prometheus & Grafana (Monitoring), Sentry (Error Tracking).

---

## 🗄️ 2. Database Architecture (Production Schema)

### 👥 USERS TABLE
*   `userId` (UUID, Primary Key)
*   `name` (String)
*   `phone` (String, Unique)
*   `email` (String, Unique)
*   `passwordHash` (String)
*   `role` (Enum: CUSTOMER, PROVIDER, ADMIN)
*   `deviceId` (String, indexed for anti-fraud & binding)
*   `isActive` (Boolean, default: true)
*   `createdAt`, `updatedAt` (Timestamp)

### 🧑‍🔧 PROVIDERS TABLE
*   `providerId` (UUID, Primary Key)
*   `userId` (UUID, Foreign Key)
*   `skills` (Array of Strings: ['PLUMBING', 'ELECTRICAL'])
*   `experienceYears` (Int)
*   `ratingAverage` (Float, default: 0.0)
*   `totalJobsCompleted` (Int, default: 0)
*   `isVerified` (Boolean, default: false)
*   `availabilityStatus` (Enum: ONLINE, OFFLINE, ON_JOB)
*   `serviceRadiusKm` (Int)
*   `lastKnownLocation` (GeoJSON / Point) - *Cold storage synchronization*
*   `walletBalance` (Decimal)

### 📅 BOOKINGS TABLE (CORE TRANSACTION TABLE)
*   `bookingId` (UUID, Primary Key)
*   `customerId` (UUID, Foreign Key)
*   `providerId` (UUID, Foreign Key, Nullable until assigned)
*   `serviceId` (UUID, Foreign Key)
*   `status` (Enum: PENDING, ASSIGNED, ACCEPTED, ON_THE_WAY, STARTED, COMPLETED, EXPIRED, REJECTED, CANCELLED_BY_USER, CANCELLED_BY_PROVIDER, DISPUTE_RAISED)
*   `issueDescription` (Text)
*   `scheduledTime` (Timestamp)
*   `location` (JSON / Address + Lat, Lng)
*   `priceFinal` (Decimal)
*   `paymentStatus` (Enum: PENDING, COMPLETED, REFUND_INITIATED, REFUNDED)
*   `createdAt`, `updatedAt` (Timestamp)

### 💬 MESSAGES TABLE (CHAT SYSTEM)
*   `messageId` (UUID, Primary Key)
*   `bookingId` (UUID, Foreign Key)
*   `senderId` (UUID)
*   `receiverId` (UUID)
*   `messageType` (Enum: TEXT, IMAGE, LOCATION)
*   `content` (Text)
*   `timestamp` (Timestamp)

### 💳 PAYMENTS TABLE
*   `paymentId` (UUID, Primary Key)
*   `bookingId` (UUID, Foreign Key)
*   `amount` (Decimal)
*   `paymentMethod` (Enum: UPI, CASH, WALLET, CARD)
*   `transactionStatus` (Enum: SUCCESS, FAILED, PENDING)
*   `gatewayOrderId` (String) - *Razorpay Orders API reference*

### 👛 WALLET_LEDGER TABLE
*   `transactionId` (UUID, Primary Key)
*   `providerId` (UUID, Foreign Key)
*   `type` (Enum: CREDIT, DEBIT)
*   `reason` (Enum: JOB_PAYMENT, PENALTY, WITHDRAWAL, COMMISSION_FEE)
*   `amount` (Decimal)
*   `balanceAfter` (Decimal)
*   `timestamp` (Timestamp)

---

## 🔧 3. Event-Driven Microservices Architecture

The system utilizes an event-driven microservices architecture to handle load gracefully, separated by bounded contexts.

*   **API Gateway:** Handles Rate Limiting, JWT Auth verification, and RBAC routing.
*   **Auth Service:** Manages OTP verification, JWT generation (Access + Refresh tokens), and Device Binding.
*   **User/Provider Service:** Profile management, KYC verification workflows, and wallet balances.
*   **Booking Service:** Core CRUD for bookings, manages pricing algorithms.
*   **Dispatch Service ⭐:** (System Core) A dedicated microservice consuming booking tasks via Redis Queue (BullMQ). It processes spatial matching and pings providers asynchronously.
*   **Payment Service:** Integrates with Razorpay Orders API, handles split settlements and refunds.
*   **Notification Service:** Unified hub for FCM (Push), SMS (Twilio/AWS SNS), and Email (SendGrid).
*   **WebSocket Gateway:** Dedicated scaled socket servers handling live location streaming and chat channels.

---

## 🧠 4. Core Logic & Algorithms

### 🎯 1. Smart Matching Engine (Asynchronous Queue)
Triggered asynchronously when a booking is created (`POST /booking/create`):
1.  **Job Queued:** Booking details sent to Redis `DispatchQueue`.
2.  **Geospatial Query:** Locate providers via Redis GEO indexes (hot data).
3.  **Filtration:** Must match `booking.category` AND `availabilityStatus == ONLINE` AND `activeJobs == 0`.
4.  **Priority Scoring Formula:**
    `Match Score = (0.4 × Distance_Score) + (0.3 × Rating_Score) + (0.2 × Acceptance_Rate) + (0.1 × Response_Time)`
5.  **Fatigue & Fairness:** Apply penalties to providers who have received many pings recently to ensure fair job distribution.
6.  **Sequential Dispatching:** 
    * Emit WebSocket event to the highest-scoring provider.
    * If no response within 15 seconds, ping the next provider in the queue.
    * If queue is exhausted without acceptance, mark booking status as `EXPIRED`.

### 📍 2. Hybrid Provider Location System
Avoid burning PostGIS rows every few seconds:
*   **Hot Data (Live Tracking):** Providers emit location every 2-5s via WebSockets. Stored in **Redis GEO hashes** (TTL 60s) for live dispatch algorithms.
*   **Cold Data (Permanent State):** A batch-sync cron job pulls the latest coordinates from Redis every 60 seconds and updates the PostGIS `Providers.lastKnownLocation` column for historical analytics and debugging.

### 💰 3. Dynamic Pricing Engine
`Price = Base_Price + Distance_Charge + Emergency_Fee + Peak_Time_Multiplier`
*   **Distance Charge:** + ₹X per km if provider is > 3km away.
*   **Peak Time Multiplier:** Adaptive factor based on demand vs. available supply ratio in a given geofence.

---

## 🔔 5. Unified Notification Engine

Crucial for marketplace trust and reliability. Dispatches real-time updates through fallbacks (Push -> SMS):
*   Booking confirmed (Customer)
*   New Job Request (Provider)
*   Provider accepted / arriving / completed (Customer)
*   Payment successful / Refund initiated (Both)

---

## 🧑‍💼 6. Admin Control Panel (Next.js SaaS Dashboard)

**Key Modules:**
1.  **God's Eye View:** Real-time map displaying all online providers and active bookings (Socket.io listening).
2.  **Verification Desk:** UI to approve/reject provider KYC documents.
3.  **Surge Controller:** Manual override switches to activate peak pricing multipliers per geofence.
4.  **Dispute Resolution:** View booking history, chat logs, and issue refunds or penalty fees.
5.  **Service Catalog Manager:** Add new services, update base pricing, and change category icons without app updates.
6.  **Financial Dashboard:** Monitor split settlements, wallet withdrawals, and platform commissions.

---

## 🚀 Deployment & Scale Strategy

1.  **Phase 1 (MVP Deployment):** Monolithic Node.js server handling both matching and APIs. Hosted on AWS EC2 or Render. Managed PostgreSQL + Redis.
2.  **Phase 2 (City-wide Scale):** Split out the Dispatch Service and WebSocket Gateway into scalable Kubernetes pods.
3.  **Phase 3 (Nation-wide Expansion):** Full Kubernetes auto-scaling cluster. Event-driven Kafka pipeline for analytics. Read-replicas for PostgreSQL.
