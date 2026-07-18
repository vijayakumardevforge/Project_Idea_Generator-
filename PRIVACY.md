# Privacy Policy - AI Project Idea Generator

This Privacy Policy explains how the AI Project Idea Generator handles data collected from users interacting with the live application.

## 1. Data We Collect and Process
* **Session & Cache Data**: We process user-selected preferences (Skill Level, Language, Framework, Domain) to generate project ideas. Transient history is temporarily cached using Upstash Redis with a 24-hour Time-To-Live (TTL).
* **Saved Projects**: If you choose to save a project, this data is permanently stored in our Supabase PostgreSQL database.
* **Security Logs & IP Addresses**: To prevent brute-force attacks and abuse, our security subsystem logs failed login attempts alongside the client's IP address (`X-Forwarded-For`).

## 2. Third-Party Services
Our application relies on the following cloud providers to function:
* **Hugging Face API**: User prompts are sent to the Qwen 2.5-7B-Instruct model to generate software architecture plans.
* **Render**: Hosts our Spring Boot backend application.
* **Supabase**: Hosts our managed PostgreSQL database.
* **Upstash**: Hosts our serverless Redis cache layer.

We do not sell, rent, or trade your data to any third-party advertisers.

## 3. Open Source Contributors
Please note that this is a public GitHub repository. Any contributions made via Pull Requests or Issues will permanently link your public GitHub profile and commit email address to the project's history.

## 4. Contact
For any questions regarding this policy, please open an Issue directly in this repository.
