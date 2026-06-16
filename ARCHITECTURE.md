# 🏗️ ServiGo – Complete System Architecture Document

## 1. System Overview & Technology Stack
ServiGo is a production-grade hyperlocal on-demand home services marketplace, architected as a highly scalable, multi-sided platform (Customer, Provider, Admin). It brings an "Uber-like" live dispatch, dynamic pricing, and matching experience to the home maintenance sector.

**Enterprise Tech Stack:**
*   **Mobile Apps (Customer & Provider):** Flutter (Dart) for unified iOS/Android codebases with high native performance.
*   **Web Dashboard (Admin):** Next.js (React), Tailwind CSS, TypeScript.
*   **Backend API:** Node.js using NestJS (strict TypeScript, dependency injection, microservices-ready).
*   **Primary Database:** PostgreSQL (relational integrity for payments/bookings) + PostGIS extension for high-performance geospatial queries, OR MongoDB with 2dsphere indexing for flexible document scaling.
*   **Real-time Engine:** WebSockets (Socket.IO) or Firebase Realtime Database for live tracking and job dispatch.
*   **Cloud Infrastructure:** AWS / Google Cloud (Dockerized deployment, Kubernetes for scaling).

---

## 🗄️ 2. Database Architecture (Production Schema)

### 👥 USERS COLLECTION / TABLE
*   `userId` (UUID, Primary Key)
*   `name` (String)
*   `phone` (String, Unique)
*   `email` (String, Unique)
*   `passwordHash` (String)
*   `role` (Enum: CUSTOMER, PROVIDER, ADMIN)
*   `profileImageUrl` (String)
*   `isActive` (Boolean, default: true)
*   `createdAt`, `updatedAt` (Timestamp)

### 🧑‍🔧 PROVIDERS COLLECTION / TABLE
*   `providerId` (UUID, Primary Key)
*   `userId` (UUID, Foreign Key)
*   `skills` (Array of Strings: ['PLUMBING', 'ELECTRICAL'])
*   `experienceYears` (Int)
*   `ratingAverage` (Float, default: 0.0)
*   `totalJobsCompleted` (Int, default: 0)
*   `isVerified` (Boolean, default: false)
*   `availabilityStatus` (Enum: ONLINE, OFFLINE, ON_JOB)
*   `serviceRadiusKm` (Int)
*   `currentLocation` (GeoJSON / Point) - *Indexed for spatial queries*
*   `earningsTotal` (Decimal)
*   `walletBalance` (Decimal)

### 🛠️ SERVICES COLLECTION / TABLE
*   `serviceId` (UUID, Primary Key)
*   `name` (String)
*   `category` (String)
*   `basePrice` (Decimal)
*   `estimatedTimeMinutes` (Int)
*   `isActive` (Boolean)
*   `description` (Text)
*   `iconUrl` (String)

### 📅 BOOKINGS TABLE (CORE TRANSACTION TABLE)
*   `bookingId` (UUID, Primary Key)
*   `customerId` (UUID, Foreign Key)
*   `providerId` (UUID, Foreign Key, Nullable until assigned)
*   `serviceId` (UUID, Foreign Key)
*   `status` (Enum: PENDING, ASSIGNED, ACCEPTED, ON_THE_WAY, STARTED, COMPLETED, CANCELLED)
*   `issueDescription` (Text)
*   `images` (Array of URLs)
*   `scheduledTime` (Timestamp)
*   `location` (JSON / Address + Lat, Lng)
*   `priceFinal` (Decimal)
*   `paymentStatus` (Enum: PENDING, COMPLETED, REFUNDED)
*   `createdAt`, `updatedAt` (Timestamp)

### 💳 PAYMENTS TABLE
*   `paymentId` (UUID, Primary Key)
*   `bookingId` (UUID, Foreign Key)
*   `amount` (Decimal)
*   `paymentMethod` (Enum: UPI, CASH, WALLET, CARD)
*   `transactionStatus` (Enum: SUCCESS, FAILED, PENDING)
*   `gatewayReferenceId` (String)

### ⭐ RATINGS TABLE
*   `ratingId` (UUID)
*   `bookingId` (UUID)
*   `customerId` (UUID)
*   `providerId` (UUID)
*   `stars` (Int, 1-5)
*   `reviewText` (Text)

---

## 🔧 3. Service Provider App (Flutter Details)

The Provider app is the engine of the marketplace. It requires a robust, distraction-free UI.

**1. Onboarding & Auth:**
*   Phone OTP login (Firebase Auth).
*   KYC flow (Upload PAN/Aadhaar images, take a selfie).
*   Skill & category selection matrix.

**2. Provider Dashboard:**
*   **Header:** Online/Offline toggle switch (Critical for dispatch engine).
*   **Stats Matrix:** Today's Earnings, Active Jobs, Acceptance Rate, Current Rating.
*   **Heatmap (Future):** Show areas with high customer demand.

**3. Job Request Engine (Live Dispatch):**
*   Full-screen takeover modal when a job matches.
*   Lays out: Distance, Estimated Earnings, Service Type.
*   15-second countdown timer to Accept/Reject (Uber-style).

**4. Active Job Flow:**
*   **Navigate:** Launches Google Maps intent to customer location.
*   **Status Toggles:** "Arrived" -> "Job Started" (requires OTP from customer to verify) -> "Job Completed".
*   **Invoice Generator:** Add extra parts/labor cost before final completion.

**5. Wallet & Earnings:**
*   Real-time payout ledger.
*   Withdraw to bank account (RazorpayX / Stripe Connect integration).

---

## ⚙️ 4. Backend API Structure (NestJS / Node.js)

### Auth & Navigation
*   `POST /auth/register` (Customer/Provider)
*   `POST /auth/verify-otp`

### Customer Gateway
*   `GET /api/v1/services?category=all`
*   `POST /api/v1/bookings/create` (Initiates the Smart Matching Engine)
*   `GET /api/v1/bookings/history`
*   `POST /api/v1/ratings`

### Provider Gateway
*   `PUT /api/v1/provider/status` (Update presence/Lat-Lng)
*   `GET /api/v1/provider/jobs/active`
*   `POST /api/v1/provider/jobs/:id/accept`
*   `PUT /api/v1/provider/jobs/:id/status` (Update flow state)

### Webhook & Payment Gateway
*   `POST /api/v1/payments/initiate`
*   `POST /webhook/razorpay` (Handling async payment success)

---

## 🧠 5. Core Logic & Algorithms

### 🎯 1. Smart Matching Engine (The Dispatcher)
Triggered asynchronously via `POST /booking/create`:
1.  **Geospatial Query:** Query providers where `ST_Distance(provider.location, customer.location) <= provider.serviceRadiusKm`.
2.  **Filter Filter:** Ensure `provider.skills` contains `booking.serviceCategory`.
3.  **State Filter:** Ensure `provider.availabilityStatus == ONLINE` and `activeJobs == 0`.
4.  **Priority Sorting Algorithm:**
    *   Sort primarily by `Distance` (nearest first).
    *   Secondary sort by `Rating` (reward top providers).
    *   Tertiary sort by `Acceptance Rate` (reward reliable providers).
5.  **Dispatch:** Emit WebSocket event to the top matched provider. If unaccepted in 15 seconds, ping the next provider in the queue.

### 💰 2. Dynamic Pricing Engine
Triggered at booking checkout:
*   `Base Price`: Derived from Service Catalog.
*   `Distance Charge`: + ₹X per km if provider is > 3km away.
*   `Peak Time Multiplier`: e.g., 1.5x during extreme weather or high demand / low supply ratios.
*   `Emergency Fee`: Flat premium for "ASAP / Within 30 Mins" SLA.

---

## 🧑‍💼 6. Admin Control Panel (Next.js SaaS Dashboard)

**Key Modules:**
1.  **God's Eye View:** Real-time map displaying all online providers and active bookings (Socket.io listening).
2.  **Verification Desk:** UI to approve/reject provider KYC documents.
3.  **Surge Controller:** Manual override switches to activate peak pricing multipliers per geofence.
4.  **Dispute Resolution:** View booking history, chat logs, and issue refunds or penalty fees.
5.  **Service Catalog Manager:** Add new services, update base pricing, and change category icons without app updates.

---

## 🚀 Deployment & Scale Strategy

1.  **Phase 1 (MVP Deployment):** Monolithic Node.js server handling both matching and APIs. Hosted on Render or Heroku. Managed PostgreSQL.
2.  **Phase 2 (City-wide Scale):** Split into Microservices:
    *   `API Gateway` (Rate limiting, auth)
    *   `Dispatch Service` (Heavy socket usage, memory intensive)
    *   `Core API Service` (CRUD operations)
3.  **Phase 3 (Nation-wide Expansion):** Kubernetes cluster auto-scaling, Redis for fast geofencing and caching active provider locations (instead of writing location to PostgeSQL every 10 seconds).
