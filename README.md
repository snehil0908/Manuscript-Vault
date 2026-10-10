<!-- #  ManuscriptVault — Digital Manuscript Management System -->

A full-stack role-based web application for managing digital manuscripts.  
**Stack:** Java (Spring Boot 3) · MySQL · HTML/CSS/JavaScript · Apache Tomcat (embedded)

<!-- --- -->

<!-- ## Project Structure -->

```
ManuscriptVault/
├── backend/                        # Spring Boot backend
│   ├── pom.xml                     # Maven dependencies
│   └── src/main/
│       ├── java/com/manuscriptvault/
│       │   ├── ManuscriptVaultApplication.java   # Entry point
│       │   ├── config/
│       │   │   ├── SecurityConfig.java            # Spring Security + CORS
│       │   │   ├── JwtUtil.java                   # JWT token generate/validate
│       │   │   └── JwtAuthFilter.java             # JWT request filter
│       │   ├── controller/
│       │   │   ├── AuthController.java            # /api/auth (login, signup)
│       │   │   ├── ManuscriptController.java      # /api/manuscripts (CRUD)
│       │   │   └── UserController.java            # /api/users (admin mgmt)
│       │   ├── service/
│       │   │   ├── AuthService.java               # Auth business logic
│       │   │   ├── ManuscriptService.java         # Manuscript business logic
│       │   │   └── UserService.java               # User management logic
│       │   ├── repository/
│       │   │   ├── UserRepository.java            # JPA user queries
│       │   │   └── ManuscriptRepository.java      # JPA manuscript queries
│       │   ├── model/
│       │   │   ├── User.java                      # User entity (JPA)
│       │   │   └── Manuscript.java                # Manuscript entity (JPA)
│       │   └── dto/
│       │       └── AuthDTOs.java                  # Request/response DTOs
│       └── resources/
│           └── application.properties             # DB + JWT config
│
├── frontend/                       # Static HTML frontend
│   ├── index.html                  # Redirect to login
│   ├── login.html                  # Login page
│   ├── signup.html                 # Registration page
│   ├── admin-dashboard.html        # Admin panel (CRUD + user mgmt)
│   ├── user-dashboard.html         # User panel (browse + search)
│   ├── css/
│   │   └── style.css               # Global stylesheet
│   └── js/
│       └── api.js                  # API client + auth helpers
│
├── database/
│   └── schema.sql                  # MySQL setup script
│
└── README.md                       # This file
```

<!-- --- -->

<!-- ##  Prerequisites -->

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17 or later | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org |
| MySQL | 8.0+ | https://dev.mysql.com/downloads |
| VS Code | Latest | https://code.visualstudio.com |
| Live Server (VSCode ext) | Latest | VS Code Marketplace |

<!-- --- -->
<!-- 
##  Step-by-Step Setup in VS Code -->

<!-- ### Step 1 — Clone / Extract the Project -->
```
Unzip ManuscriptVault.zip to your desired folder.
Open the ManuscriptVault/ folder in VS Code.
```
<!-- 
### Step 2 — Set Up MySQL Database -->

1. Open MySQL Workbench or MySQL CLI.
2. Run the database setup script:
```sql
-- In MySQL CLI:
mysql -u root -p < database/schema.sql

-- OR manually open the file and run it in MySQL Workbench.
```
3. This creates:
   - Database: `manuscript_vault`
   - Tables: `users`, `manuscripts`
   - Sample data: 2 users + 6 manuscripts

<!-- ### Step 3 — Configure the Backend -->

Open `backend/src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/manuscript_vault?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD   ← Change this!
```

<!-- ### Step 4 — Build & Run the Backend -->

Open a terminal in VS Code (`Ctrl + `` ` ``):

```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

You should see:
```
======================================
  ManuscriptVault Server is Running!
  URL: http://localhost:8080
======================================
```

<!-- ### Step 5 — Serve the Frontend -->

**Option A — VS Code Live Server (recommended):**
1. Install the "Live Server" extension in VS Code.
2. Right-click `frontend/login.html` → **Open with Live Server**.
3. The app opens at `http://127.0.0.1:5500/login.html`.

**Option B — Python HTTP server:**
```bash
cd frontend
python -m http.server 5500
# Then visit http://localhost:5500/login.html
```

**Option C — Open directly in browser:**
- Right-click `frontend/login.html` → Open with browser.
- Note: some browsers restrict `fetch()` on file:// — use Live Server instead.

<!-- --- -->

<!-- ##  Default Login Credentials -->

| Username | Password | Role |
|----------|----------|------|
| `admin`    | `admin123` | ADMIN |
| `john_doe` | `user123`  | USER |
| `jane_doe` | `user123`  | USER |

<!-- > You can also create new accounts via the signup page. -->

<!-- --- -->

<!-- ## API Reference -->
 
#/## Auth Endpoints (No token required)

| Method | URL | Body | Description |
|--------|-----|------|-------------|
| POST | `/api/auth/login` | `{username, password}` | Login → returns JWT |
| POST | `/api/auth/signup` | `{username, password, role}` | Register new user |

<!-- ### Manuscript Endpoints (JWT required) -->

| Method | URL | Role | Description |
|--------|-----|------|-------------|
| GET | `/api/manuscripts` | ALL | List all manuscripts |
| GET | `/api/manuscripts/{id}` | ALL | Get single manuscript |
| GET | `/api/manuscripts/search?query=...` | ALL | Global search |
| GET | `/api/manuscripts/search?title=...` | ALL | Search by title |
| GET | `/api/manuscripts/search?author=...` | ALL | Search by author |
| GET | `/api/manuscripts/filter?startDate=...&endDate=...` | ALL | Date range filter |
| GET | `/api/manuscripts/filter?genre=...` | ALL | Filter by genre |
| POST | `/api/manuscripts` | ADMIN | Add manuscript |
| PUT | `/api/manuscripts/{id}` | ADMIN | Update manuscript |
| DELETE | `/api/manuscripts/{id}` | ADMIN | Delete manuscript |

<!-- ### User Endpoints (JWT + ADMIN required) -->

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| DELETE | `/api/users/{id}` | Delete user |
| PUT | `/api/users/{id}/role` | Update user role |

<!-- --- -->

<!-- ## Authentication Flow -->

```
1. User submits login form
2. Frontend POST /api/auth/login
3. Backend verifies BCrypt password
4. Backend returns JWT token (24hr expiry)
5. Frontend stores token in sessionStorage
6. All subsequent API calls include: Authorization: Bearer <token>
7. Spring Security validates JWT on every request
8. Role-based access enforced via SecurityConfig rules
```

<!-- --- -->

<!-- ## Database Schema -->

```sql
-- Users Table
CREATE TABLE users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,   -- BCrypt hashed
    role     ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER'
);

-- Manuscripts Table
CREATE TABLE manuscripts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author      VARCHAR(150) NOT NULL,
    date        DATE         NOT NULL,
    description TEXT,
    genre       VARCHAR(100),
    language    VARCHAR(50),
    pages       INT
);
```

<!-- --- -->

<!-- ## VS Code Extensions (Recommended) -->

- **Extension Pack for Java** — Java language support
- **Spring Boot Extension Pack** — Spring Boot tools
- **Live Server** — Serve frontend files
- **MySQL** (by cweijan) — Database GUI in VS Code
- **REST Client** — Test API endpoints

<!-- --- -->

<!-- ## Troubleshooting -->

**Port 8080 already in use:**
```bash
# Change port in application.properties:
server.port=8081
# Update API_BASE in frontend/js/api.js to match
```

**Database connection failed:**
- Verify MySQL is running: `sudo service mysql start`
- Check credentials in `application.properties`
- Ensure `manuscript_vault` database exists

**CORS errors in browser:**
- Make sure the backend is running on port 8080
- Use Live Server (not file://) for the frontend

**BCrypt hash mismatch in schema.sql:**
- Delete the INSERT seed data and use `/api/auth/signup` to create users manually

**Maven not found:**
- Add Maven to PATH, or use `./mvnw spring-boot:run` (wrapper)

<!-- --- -->

<!-- ## Architecture -->

```
Frontend (HTML/CSS/JS)
        │
        │ fetch() + JWT Bearer Token
        ↓
Spring Boot REST API (Port 8080)
        │
   ┌────┴────────────────────────┐
   │  SecurityConfig + JwtFilter │
   └────┬────────────────────────┘
        │
   ┌────▼────┐  ┌─────────────────┐  ┌──────────────┐
   │Controller│→│    Service      │→│  Repository  │
   └─────────┘  └─────────────────┘  └──────┬───────┘
                                             │ JPA/Hibernate
                                             ↓
                                        MySQL Database
```

<!-- --- -->

<!-- ## 📝 License -->

This project is created for educational purposes.  
Free to use, modify, and distribute.
