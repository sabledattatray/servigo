# 🏠 ServiGo – HyperLocal Home Services Platform (Android)

## 📌 Project Overview

ServiGo is a production-grade HyperLocal Home Services Android application built using modern Android architecture principles. It enables users to book on-demand services such as plumbing, electrical repair, AC servicing, and household maintenance with real-time booking management and offline-first reliability.

The system is designed with a scalable multi-layer architecture, supporting offline access, reactive UI updates, and modular expansion for future SaaS deployment.

## ⚙️ Core Architecture Highlights

### 📦 Offline-First Data Layer (Room DB)
- Implemented Room Database with KSP (Kotlin Symbol Processing)
- Local persistence for:
  - Service catalog
  - Booking history
- Auto-seeding of default service data on first launch
- Fully offline-capable UI fallback layer
- Ensures smooth performance even on low network conditions

### 🧠 Reactive State Management
- Built using ViewModel + StateFlow architecture
- Clean separation of UI state and business logic
- Real-time updates for:
  - Booking creation
  - Service selection
  - Location capture
- Structured MVVM pattern for scalability

### 🧭 Navigation & App Structure
- Single-Activity architecture
- 3-tab core navigation system:
  - 🏠 Home
  - 📦 Bookings
  - 👤 Profile

This acts as the central dashboard for the entire application.

## 🎨 UI / UX Design System

### 🌙 Modern Adaptive Theme (Material 3)
- Fully implemented Jetpack Compose Material 3 system
- Deep dark-mode optimized UI

### 🎨 Design Tokens
- Primary palette:
  - Electric Blue (Primary Accent)
  - Muted Orange (Secondary Accent)
- Background system:
  - Slate / Charcoal gradients
  - Soft layered surfaces

### 🧱 Visual Language
- 32dp rounded container system
- 24dp inner form rounding
- Minimal elevation design (1dp border system)
- Soft shadow replacement with subtle stroke outlines

### 🎭 Professional Polish Theme Upgrade
A full UI refinement pass was applied to align the product with enterprise-grade SaaS aesthetics:
- Muted professional color palette:
  - `#1A1C1E` (Base background)
  - `#2D2F33` (Surface layers)
- Reduced visual noise for productivity-focused UX
- Refined gradient usage (soft surface blending instead of harsh colors)
- Clean hierarchy-based UI layout system
- Consistent spacing and modular card system

## 📍 Booking System

### Core Functionality
- Service selection flow
- Issue description input
- Scheduled + instant booking support
- Location capture integration
- Structured booking persistence layer

### Data Handling
- Booking state managed via ViewModel
- Stored in local SQLite/Room tables
- Fully reactive UI updates via StateFlow pipeline

## 🖼️ App Assets & Branding
- Custom vector-based launcher icon created
- Home service silhouette design
- Lightweight asset system (no runtime network dependency)
- Optimized for fast app startup performance

## 🏗️ Technical Stack
- Kotlin (Android Native)
- Jetpack Compose
- Room Database (KSP)
- ViewModel Architecture
- StateFlow / Coroutines
- Material 3 Design System

## 🚀 Key Engineering Strengths
- Offline-first architecture design
- Modular and scalable MVVM structure
- Reactive UI system with clean state flow
- Production-grade UI consistency system
- Performance-optimized rendering pipeline
- Clean separation of UI / domain / data layers

## 📌 Project Status

- ✔ Core App Completed
- ✔ UI/UX Design System Completed
- ✔ Offline Persistence Implemented
- ✔ Booking System Implemented
- 🚧 Backend + Cloud Integration (Future Phase)

## 👨‍💻 Developer
**Datta Sable**
Full Stack Developer | Android Engineer | BI & Automation Specialist

---
⭐ **Summary**
ServiGo is engineered as a scalable hyperlocal service ecosystem, combining offline-first Android architecture with modern UI engineering principles to deliver a fast, reliable, and production-ready user experience.
