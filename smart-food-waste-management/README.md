# FoodBridge — Smart Food Waste Management System

A Spring Boot + Thymeleaf + MySQL college project that connects restaurants with NGOs to redirect safe surplus food.

## Technology
- Java 21, Spring Boot 3.4, Maven
- Spring MVC, Spring Security, Spring Data JPA, Thymeleaf
- MySQL 8, Bootstrap 5, CSS, JavaScript

## Requirements
1. JDK 21+
2. Maven 3.9+
3. MySQL 8 running locally
4. Internet connection for Bootstrap CDN (optional for core backend)

## Run on Windows / VS Code
1. Install Java 21, Maven and MySQL Server.
2. Open this folder in VS Code.
3. In MySQL Workbench, create a database if desired:
   ```sql
   CREATE DATABASE smart_food_waste;
   ```
4. Set the database credentials in `src/main/resources/application.properties` or environment variables:
   - `DB_URL` (default `jdbc:mysql://localhost:3306/smart_food_waste?...`)
   - `DB_USERNAME` (default `root`)
   - `DB_PASSWORD` (default empty)
5. Run in the terminal:
   ```powershell
   mvn spring-boot:run
   ```
6. Open `http://localhost:8080`.

## First admin login
Default local development credentials:
- Email: `admin@smartfood.local`
- Password: `Admin@12345`

Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` environment variables before first launch to seed your own admin. **Change the default before any public deployment.** The admin is seeded automatically and there is no public admin registration.

## Main workflow
1. Register a Restaurant or NGO using the Join menu.
2. Admin signs in and approves the registration.
3. Approved restaurant posts a food donation, expiry time, pickup address, and optionally captures GPS coordinates.
4. Approved NGO browses available listings, sorted by approximate distance when both sides have coordinates, and requests a donation.
5. Restaurant approves or rejects the claim.
6. NGO marks an approved claim picked up; restaurant confirms completion.
7. In-app notifications are stored in the database and shown in the dashboards as workflow messages are generated.

## Uploads and location
- Uploads are stored in the local `uploads/` directory. The current MVP permits JPG, PNG, WEBP and PDF, with a 5 MB per-file limit.
- Browser location requires user permission and a secure context (localhost is supported). If permission is denied, users can continue without GPS coordinates.
- Distance is straight-line Haversine distance, not road distance. Google Maps opens road directions using the saved coordinates.

## Database
Hibernate creates/updates tables from JPA entities (`users`, `food_donations`, `food_claims`, `notifications`) on first startup. Keep `ddl-auto=update` for local development only; use migrations for a production deployment.

## Current MVP scope / honest limitations
- This is a starting MVP. It includes core registration, role login, admin verification, donation posting, claims, pickup workflow, uploads, GPS capture, nearby sorting, notifications and expiry handling.
- Email/SMS/WhatsApp, WebSockets, profile editing, password reset, a formal audit log, private document streaming, advanced radius filtering, and production deployment hardening are not implemented in this initial build.
- Registration documents are currently served through the same local upload endpoint as images. For real deployment, verification documents must be private and accessible only to authorized admins.
- Before presenting, test the full workflow using a local MySQL database and separate Restaurant/NGO accounts.
