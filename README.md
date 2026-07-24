# 💡 AI Project Idea Generator

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://supabase.com/)
[![Redis](https://img.shields.io/badge/Redis-Upstash-FF4438?style=for-the-badge&logo=redis&logoColor=white)](https://upstash.com/)
[![AI Engine](https://img.shields.io/badge/AI%20Model-Qwen%202.5--7B-FFD21E?style=for-the-badge&logo=huggingface&logoColor=black)](https://huggingface.co/Qwen/Qwen2.5-7B-Instruct)
[![Live Demo](https://img.shields.io/badge/Live%20Demo-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://project-idea-generator-bvbt.onrender.com/)

> **Spark Your Imagination. Build the Future.**  
> A smart, AI-powered systems architect and roadmap generator that dynamically synthesizes custom software engineering projects tailored to your exact skill level, preferred language, framework, and domain.

---

## 🌟 Live Demo

Experience the full application live in production:  
👉 **[https://project-idea-generator-bvbt.onrender.com/](https://project-idea-generator-bvbt.onrender.com/)**

---

## 📑 Table of Contents

- [Overview](#-overview)
- [System Architecture](#-system-architecture)
- [Key Features](#-key-features)
- [Tech Stack](#️-tech-stack)
- [API Reference](#-api-reference)
- [Setup & Installation](#-setup--installation)
- [Cloud Deployment](#-cloud-deployment)

---

## 🔬 Overview

**AI Project Idea Generator** acts as your personal Senior Technical Project Manager. Powered by Hugging Face's `Qwen/Qwen2.5-7B-Instruct` large language model, it architects production-grade project specifications complete with:
- **Tailored Descriptions & Features** matched to your domain (`E-Commerce`, `Fintech`, `AI/ML`, etc.).
- **Relational Database Schemas** with suggested tables and columns.
- **API Architectural Blueprints** recommending optimal communication patterns (`REST`, `GraphQL`, `WebSockets`).
- **Phased Sprint Roadmaps** providing actionable step-by-step milestones.

---

## 🏗️ System Architecture

The application is built on a modern decoupled architecture, combining a responsive Vanilla JS/Glassmorphism frontend with a Spring Boot 4 / Java 21 backend, distributed across specialized cloud storage providers:

```mermaid
flowchart TB
    subgraph Client ["🖥️ Client Layer"]
        UI["Web Browser (HTML5 / CSS3 / JS)"]
        PDF["Client-Side PDF Engine (jsPDF + Marked)"]
    end

    subgraph CloudAPI ["☁️ Render Cloud API"]
        API["Spring Boot 4.1.0 Backend API (Java 21)"]
        SEC["Spring Security & Brute-Force Listener"]
    end

    subgraph Storage ["💾 Data & Cache Layer"]
        Redis["Upstash Redis Cache (Session History / 24h TTL)"]
        Postgres["Supabase Managed PostgreSQL (JSONB Entity Storage)"]
    end

    subgraph AI ["🧠 Hugging Face API"]
        Qwen["Qwen 2.5-7B-Instruct (JSON & Roadmap Synthesis)"]
    end

    UI -->|"REST API / JSON"| API
    API -->|"REST API / JSON"| UI
    UI -->|"Render Vector PDF"| PDF
    API -->|"Authenticate & Audit"| SEC
    API -->|"Session History (List Ops)"| Redis
    Redis -->|"Cached Ideas"| API
    API -->|"JPA / Hibernate"| Postgres
    Postgres -->|"Entity Data"| API
    API -->|"Bearer Token / Prompts"| Qwen
    Qwen -->|"Architectural Output"| API
```

### ⚙️ How It Works:
1. **Request Flow:** The frontend submits user preferences (`Skill Level`, `Language`, `Framework`, `Domain`) with custom headers forcing zero-cache fresh synthesis (`x-use-cache: 0`).
2. **Resilient AI Inference:** The backend communicates with Hugging Face using a 3-attempt exponential retry loop with automatic mock fallback capabilities.
3. **Dual-Layer Persistence:** Transient session histories are stored in **Upstash Redis** (in-memory, 24h TTL), while user favorites and security logs are permanently saved in **Supabase PostgreSQL** using native `JSONB` columns.
4. **Brute-Force Shield:** `AuthenticationFailureListener` monitors failed login attempts in real time, extracting client IPs (`X-Forwarded-For`) to prevent brute-force attacks.

---

## ✨ Key Features

- **🧠 Dynamic AI Architecting:** Generates customized project structures based on your skill level (`Beginner`, `Intermediate`, `Advanced`).
- **🗺️ Interactive Roadmaps & PDF Export:** Synthesizes deep, multi-sprint implementation plans and allows native client-side vector PDF export via `jsPDF` and `Marked`.
- **🎨 Cyberpunk Glassmorphism UI:** Features an animated neural wake-up portal, radar sweeps, interactive onboarding walkthrough guide, and a floating feedback modal.
- **🔐 Enterprise Security:** Integrates `BCrypt` hashing, session authentication, and automated IP tracking for failed login attempts.
- **📊 Role-Protected Admin Dashboard:** Real-time statistics (`Users Today`, `Ideas Today`), feedback reviews, and security audit logs accessible via `/admin.html`.

---

## 🛠️ Tech Stack

| Layer | Technologies | Key Role |
| :--- | :--- | :--- |
| **Backend Core** | **Java 21**, **Spring Boot 4.1.0** | High-performance REST server & DI framework |
| **Data Access** | **Spring Data JPA**, **Hibernate ORM 6** | Entity mapping and native `JSONB` type handling |
| **Security** | **Spring Security 6**, **BCrypt** | Authentication, CORS rules, and brute-force event listener |
| **Database** | **PostgreSQL (Supabase)** | Managed relational database with connection pooling |
| **Caching** | **Redis (Upstash)** | Distributed serverless session history (`history:session:*`) |
| **AI Integration** | **Spring RestClient**, **Jackson** | HTTP communication with Hugging Face (`Qwen 2.5-7B`) |
| **Frontend UI** | **HTML5**, **CSS3**, **Vanilla JS** | Mobile-responsive Grid layout with Glassmorphism animations |

---

## 🔌 API Reference

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/projects/generate` | Generates a new project architecture via Hugging Face | Optional (`X-Session-Id`) |
| `GET` | `/api/projects/history` | Retrieves temporary session history from Redis cache | Optional (`X-Session-Id`) |
| `POST` | `/api/projects/{id}/roadmap` | Triggers generation of a detailed multi-sprint roadmap | Public |
| `POST` | `/api/projects/{id}/save` | Permanently saves a project idea to the authenticated user | Yes |
| `POST` | `/api/auth/login` | Authenticates user credentials and initializes `HttpSession` | No |
| `GET` | `/api/admin/stats` | Returns system analytics (`totalUsersToday`, `totalIdeasToday`) | `ROLE_ADMIN` |

---

## ⚙️ Setup & Installation

### 1. Prerequisites
- **Java JDK 21+** and **Maven** (`./mvnw` wrapper included)
- **PostgreSQL** and **Redis** (local or cloud instances)
- **Hugging Face API Token** (free at [huggingface.co/settings/tokens](https://huggingface.co/settings/tokens))

### 2. Database & Environment Configuration
Create a PostgreSQL database named `aiprojects`:
```sql
CREATE DATABASE aiprojects;
```

Set your environment variables in your terminal:

**Windows (CMD / PowerShell):**
```powershell
$env:HUGGINGFACE_API_TOKEN="your_huggingface_token_here"
```

**Linux / macOS:**
```bash
export HUGGINGFACE_API_TOKEN="your_huggingface_token_here"
export REDIS_URL="redis://localhost:6379"
```

### 3. Build & Run
Run the application directly using the Maven wrapper:
```bash
./mvnw spring-boot:run
# Or on Windows: mvnw.cmd spring-boot:run
```

Once started, open your browser and navigate to:  
👉 **[http://localhost:8080](http://localhost:8080)**

---

## ☁️ Cloud Deployment

When deploying to platforms like **Render**, ensure the following environment variables are configured in your dashboard:

| Variable | Example Value |
| :--- | :--- |
| `DB_URL` | `jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?prepareThreshold=0` |
| `DB_USERNAME` | `postgres.your_supabase_project_id` |
| `DB_PASSWORD` | `YourSupabaseDatabasePassword` |
| `HUGGINGFACE_API_TOKEN` | `hf_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx` |
| `REDIS_URL` | `rediss://default:xxxxxx@eu1-xxxxxx.upstash.io:6379` |

---
<p align="center">
  Built with ❤️ using <strong>Java 21</strong>, <strong>Spring Boot 4</strong>, and <strong>Hugging Face AI</strong>.
</p>
   
 