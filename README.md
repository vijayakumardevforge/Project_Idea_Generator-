# AI Project Idea Generator

[**Live Demo (Vercel)**](https://project-idea-generator-one.vercel.app/)

A smart, AI-powered web application that generates custom software project ideas tailored to your skill level, preferred programming language, framework, and domain. It uses the Hugging Face API (Qwen model) to dynamically architect project ideas complete with descriptions, features, database tables, API endpoints, and a step-by-step learning roadmap.

## 🚀 Features

- **Personalized Ideas:** Generates projects based on user inputs (Domain, Language, Framework, Skill Level).
- **Comprehensive Details:** Provides key features, suggested database tables, recommended endpoints, and a learning roadmap.
- **AI Integration:** Powered by Hugging Face's API (Qwen/Qwen2.5-7B-Instruct).
- **Responsive UI:** Clean, modern frontend built with HTML, CSS, and Vanilla JavaScript.
- **Robust Backend:** Built with Spring Boot and Java 21.
- **Data Persistence:** Uses PostgreSQL to manage application data.

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 4.1.0, Spring Data JPA
- **Frontend:** HTML5, CSS3, JavaScript
- **Database:** PostgreSQL
- **AI Provider:** Hugging Face API
- **Build Tool:** Maven
- **Utilities:** Lombok, Jackson

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 21** or higher
- **Maven**
- **PostgreSQL** (running on default port `5432`)
- **Hugging Face API Token** (You can get one for free at [huggingface.co](https://huggingface.co/settings/tokens))

## ⚙️ Setup & Installation

### 1. Database Configuration
Create a new PostgreSQL database named `aiprojects`:
```sql
CREATE DATABASE aiprojects;
```
Ensure your database credentials in `src/main/resources/application.properties` match your local PostgreSQL setup:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aiprojects
spring.datasource.username=postgres
spring.datasource.password=1234
```

### 2. Environment Variables
The application requires a Hugging Face API token to function. You must set this as an environment variable before starting the app.

**Windows (Command Prompt):**
```cmd
set HUGGINGFACE_API_TOKEN=your_token_here
```

**Windows (PowerShell):**
```powershell
$env:HUGGINGFACE_API_TOKEN="your_token_here"
```

**Linux / macOS:**
```bash
export HUGGINGFACE_API_TOKEN="your_token_here"
```

### 3. Build and Run

You can run the application directly using Maven:

```bash
./mvnw spring-boot:run
```
*(Or use `mvnw.cmd` on Windows)*

Alternatively, you can package it into a JAR and run it:
```bash
./mvnw clean package
java -jar target/project-idea-generator-0.0.1-SNAPSHOT.jar
```

## 🌐 Usage

Once the application is running, open your web browser and navigate to:

```text
http://localhost:8080
```

1. Fill out the form with your desired criteria (e.g., Python, Django, E-commerce, Intermediate).
2. Click **Generate Idea**.
3. Wait for the AI to process and return a comprehensive project architecture.

## 🌍 Deployment Architecture

The application has been successfully deployed to the cloud using a modern, split-stack approach:

- **Frontend Hosting:** [Vercel](https://vercel.com) (Static hosting for HTML/CSS/JS)
- **Backend API:** [Render](https://render.com) (Dockerized Spring Boot application)
- **Database:** [Supabase](https://supabase.com) (Managed PostgreSQL via connection pooling)
- **AI Model:** Hugging Face API

### Cloud Environment Variables
If deploying a fork of this project to Render, ensure the following environment variables are set:
- `DB_URL`: The JDBC pooler URL from Supabase (e.g., `jdbc:postgresql://aws-x...pooler.supabase.com:5432/postgres`)
- `DB_USERNAME`: Supabase pooler username
- `DB_PASSWORD`: Supabase database password
- `HUGGINGFACE_API_TOKEN`: Your Hugging Face API token
