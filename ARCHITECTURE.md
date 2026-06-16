# 🏗️ ServiGo – Complete System Architecture Document

## 1. System Overview & Technology Stack
ServiGo is an enterprise-grade hyperlocal on-demand home services marketplace. Architected as a highly scalable, multi-sided platform (Customer, Provider, Admin), it delivers an "Uber-like" live dispatch, dynamic pricing, and matching experience using event-driven microservices.

**Enterprise Tech Stack:**
*   **Mobile Apps (Customer & Provider):** Flutter (Dart) for high-performance, unified iOS/Android codebases.
*   **Web Dashboard (Admin):** Next.js (React), Tailwind CSS, TypeScript.
*   **Backend API & Microservices:** Node.js / NestJS (TypeScript, Dependency Injection, State Machine enforcement).
*   **Primary Database:** PostgreSQL (relational integrity, financial ledgers) + PostGIS (geospatial).
*   **Event Bus & Streaming:** Kafka or NATS for asynchronous, replayable inter-service communication.
*   **In-Memory Store & Queues:** Redis for hot location caching + BullMQ for DLQ-enabled task queues.
*   **Search & Discovery:** ElasticSearch or Meilisearch for high-speed localized service queries.
*   **Real-time Engine:** WebSocket Gateway via Socket.IO, horizontally scaled.
*   **AI & ML Layer:** TensorFlow/PyTorch serving (Matching/Fraud), LLM agents powered by Gemini (Support/Chat).
*   **Observability Layer:** Pino (Structured Logs), Prometheus & Grafana (Monitoring), Sentry (Errors).
*   **Cloud Infrastructure:** Kubernetes (K8s) on AWS/GCP, multi-tenant ready by city.

---

## 🗄️ 2. Database Architecture (Production Schema)

### 👥 USERS TABLE
*   `userId` (UUID, Primary Key)
*   `name`, `phone`, `email`
*   `passwordHash`, `role` (CUSTOMER, PROVIDER, ADMIN, B2B_CORP)
*   `deviceId` (String, indexed for anti-fraud & binding)
*   `createdAt`, `updatedAt`

### 🧑‍🔧 PROVIDERS TABLE
*   `providerId` (UUID, Primary Key)
*   `userId` (UUID, Foreign Key)
*   `skills` (Array of Strings)
*   `ratingAverage`, `totalJobsCompleted`, `performanceScore` (For ML Matching)
*   `availabilityStatus` (Enum: ONLINE, OFFLINE, ON_JOB)
*   `h3Index` (String) - *Hexagonal hierarchical spatial index for hyper-fast bucketing*
*   `lastKnownLocation` (GeoJSON / Point) - *Batch-synced cold storage from Redis*

### 📋 KYC & BACKGROUND CHECKS
*   `documentId` (UUID)
*   `providerId` (UUID)
*   `documentType` (AADHAAR, PAN, DRIVING_LICENSE, SELFIE)
*   `verificationStatus` (PENDING, UNDER_REVIEW, APPROVED, REJECTED)
*   `policeVerificationStatus` (Enum)

### 📅 BOOKINGS TABLE (CORE TRANSACTION)
*   `bookingId` (UUID, Primary Key)
*   `customerId`, `providerId`, `serviceId`
*   `status` (PENDING, ASSIGNED, ACCEPTED, STARTED, COMPLETED, EXPIRED, REJECTED, CANCELLED, DISPUTE_RAISED)
*   `scheduledTime`, `priceFinal`
*   `paymentStatus` (PENDING, ESCROW_HELD, COMPLETED, REFUND_INITIATED, REFUNDED)

### 👛 ESCROW & WALLET LEDGER
*   `transactionId` (UUID, Primary Key)
*   `walletType` (PROVIDER, PLATFORM_ESCROW, CUSTOMER_WALLET)
*   `type` (CREDIT, DEBIT)
*   `reason` (JOB_PAYMENT, COMMISSION_FEE, PENALTY, WITHDRAWAL)
*   `amount`, `balanceAfter`

### 🛡️ DISPUTES & AUDIT_LOG
*   **Disputes:** `disputeId`, `bookingId`, `status` (RAISED, INVESTIGATING, RESOLVED, REFUNDED), `evidenceFiles`
*   **Audit Log:** `logId`, `entityType`, `entityId`, `action`, `oldValue`, `newValue`, `performedBy`

### 📈 GROWTH & PRICING
*   **Pricing Rules:** `surgeRuleId`, `multiplier`, `condition` (RAIN, WEEKEND, PEAK)
*   **Coupons:** `code`, `discountValue`, `usageLimit`
*   **Referrals:** `referrerId`, `referredId`, `rewardStatus`

---

## 🔧 3. Event-Driven Microservices Layer

Communication executes over an **Event Bus (Kafka)** rather than fragile HTTP chains, ensuring fault tolerance and replayability.

*   **API Gateway:** Routes external traffic, validates JWTs, applies Rate Limiting and **Idempotency Keys** (crucial for protecting transactions).
*   **Booking Service:** Owns the Booking State Machine (enforces valid lifecycle transitions: `PENDING -> ASSIGNED -> ACCEPTED`).
*   **Dispatch & AI Match Service ⭐:** Consumes booking events. Applies H3 Geo-boxing and ML-based dispatch rankings.
*   **Trust & Safety Service:** Handles KYC workflows, Background Checks, SOS alerts, and Fraud/Abuse detection.
*   **Escrow & Payment Service:** Syncs with Razorpay Orders API, manages B2B splits, holds funds in Escrow until job completion.
*   **Growth Service:** Manages Coupons, Referrals, Provider Subscriptions (Lead tiers), and Loyalty points.

---

## 🧠 4. Core Distributed Logic & AI Algorithms

### 🎯 1. Smart AI Matching Engine (ML Powered)
Transitions from static scoring to a trained Machine Learning ranking model.
*   **Inputs:** Real-time distance (Redis GEO/H3), Provider Performance Score (acceptance rate, quality rating, cancellation rate), predictive arrival time, and customer lifetime value.
*   **Fatigue & Fairness Rules:** Penalizes dispatching to providers overloaded with recent pings.
*   **Fallback:** DLQ (Dead Letter Queue) handles expired bookings, alerting operational admins before SLA breach.

### 💰 2. Dynamic Pricing Engine (Surge & Escrow)
`Price = Base_Price + Distance_Charge + Surge_Multiplier + Emergency_Fee + Platform_Commission`
*   **Surge Rules:** Algorithmic multipliers based on supply/demand balance per H3 hexagon, weather triggers, or peak hours.
*   **Escrow Protection:** Customer payment is held in a virtual Platform Escrow. Payout is only settled to the Provider's Wallet upon verified job completion (Quality QC passed).

### 🛡️ 3. Quality & Fraud Prevention Layers
*   **Service QC:** Providers must upload Before/After photos to initiate completion.
*   **AI Fraud Detection:** Detects GPS spoofing (velocity anomalies), coordinated review farming, and promo-code abuse through device fingerprinting.

---

## 🏥 5. Trust, Safety & Operational Excellence

1.  **SOS Center & Background Checks:** 
    * "Emergency Button" in Customer/Provider apps loops to a 24/7 Safety Desk.
    * Deep identity integration matching Aadhaar/PAN and routine Police Verifications.
2.  **Dispute Resolution Center:**
    * Structured dispute pipeline integrating chat logs, Before/After photos, and GPS trails to resolve conflicts transparently.
3.  **Provider Subscriptions:** 
    * Tiered progression (e.g., Free, Pro, Premium) unlocking unlimited leads, higher visibility, or discounted commissions.

---

## 🏢 6. Enterprise Expansion

1.  **Multi-Service Custom Packages:** Cart logic allowing bundled checkout (e.g., Home Deep Clean + AC Service).
2.  **Corporate / B2B Accounts:** `organizations` and `cost_centers` allowing offices to bulk-book maintenance directly with net-30 invoicing.
3.  **White-Label Franchise Model:** Multi-tenant separation. Super-Admin dashboard manages regional Master Franchises representing physical nodes (Mumbai, Pune, etc.) with localized pricing and commissions.

---

## 🔒 7. DevOps, Security & Disaster Recovery

*   **Edge Security:** AWS WAF / Cloudflare to drop DDoS packets and malicious payloads before reaching the API Gateway.
*   **Secrets Management:** HashiCorp Vault or AWS Secrets Manager to safeguard DB credentials and payment API keys. No `.env` commits.
*   **Backup & Disaster Recovery (DR):** 
    * Hourly Redis EBS snapshots. Continuous Log Shipping for PostgreSQL (Point-In-Time-Recovery).
    * `RPO (Recovery Point Objective): 15 min`, `RTO (Recovery Time Objective): 30 min`.
*   **Multi-Tenancy:** City-level isolation inside K8s (separate Redis namespaces per region) preventing total-system catastrophic failure.
