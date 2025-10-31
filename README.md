# Job Tracker App

A full-stack job-application tracker built with **Spring Boot** (backend) and **Vue 3** (frontend), deployed via Docker.  
Users can register, log in (session-based authentication), and manage their own job applications.  
Each user only sees and manages **their own** data.


## 🚀 Features

- Register and log in securely (session-based authentication)
- Manage your personal job applications list
- Add, edit, delete, and view job applications easily
- Automatically store application dates and notes
- Fully responsive, clean UI
- Each user’s data is isolated and protected


## 🧰 Tech Stack & Structure

**Frontend (Vue 3)**
- Composition API, Vue Router, Fetch API (with credentials)
- Scoped CSS for component styling
- Located under `/frontend`

**Backend (Spring Boot 3)**
- Spring Security (session-based), Spring Data JPA, Hibernate
- BCrypt password hashing
- Located under `/app`

**Infrastructure**
- PostgreSQL (main DB), H2 (testing)
- Docker & Docker Compose for full-stack environment
- Nginx reverse proxy for serving the frontend


## 📦 Getting Started

### Prerequisites
- Docker & Docker Compose installed
- No local Java, Node.js, or PostgreSQL setup required — everything runs in Docker.


### Run App (Docker)
```bash
git clone https://github.com/AvdAris/job-tracker.git
cd job-tracker
docker compose up --build
```

Access URLs:
- Frontend → **https://localhost:3000**
- Backend API (proxied through Nginx) → **/api/...**


## ✅ Testing

### Run All Tests
```bash
./mvnw test
```

### Test Setup
- Uses `application-test.properties`
- In-memory **H2 database**
- Schema auto-created & dropped per test run
- `AuthController` tested via **MockMvc**
- `UserService` + `JobApplicationService` tested with **Mockito unit tests**


## 🗃️ Database Schema

### 🧑 User
| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Unique user ID |
| `email` | VARCHAR | Unique email address (used for login) |
| `password_hash` | VARCHAR | Encrypted password |
| `user_name` | VARCHAR | Display name |
| `created_at` | TIMESTAMP | Time of account creation |
| `updated_at` | TIMESTAMP | Last update timestamp |

### 💼 JobApplication
| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Application ID |
| `company_name` | VARCHAR | Company name |
| `job_title` | VARCHAR | Job title |
| `status` | VARCHAR | Current application status |
| `date_applied` | DATE | Application date |
| `notes` | TEXT | Optional notes |
| `user_id` | BIGINT (FK) | References `User.id` |

> Each job application belongs to exactly one user.  
> Deleting a user cascades and removes all their applications.


## 🔐 Authentication

- Users authenticate via **email + password** at `/api/auth/login`.
- Spring Security uses **session-based authentication** with secure cookies (`JSESSIONID`).
- Session persists automatically across page reloads via `credentials: 'include'` in frontend fetch calls.
- Unauthorized users are redirected to `/login`.


## 🚀 Future Improvements

- **Frontend:**
  - Replace basic HTML table with a modern data table component (e.g. Vuetify `v-data-table`) supporting pagination, sorting, and filtering.
  - Add smoother redirect behavior when session expires (optional UX enhancement).

- **Backend:**
 - Implement email verification and password reset functionality, and explore adding OAuth login options (e.g. Google, GitHub).
  - Add rate limiting to protect authentication endpoints.
  - Allow users to record upcoming interview dates and automatically receive reminders or email notifications when they approach.

- **Testing:**
  - Expand unit and integration test coverage (target: 100%).
  - Add test containers for integration testing against PostgreSQL.

- **Security & DevOps:**
  - Enforce HTTPS cookies and secure session handling in production.
  - Add GitHub Actions workflow for CI/CD (automated build and tests).



## 👥 Contributing

Contributions are welcome.  
To contribute:

1. Fork the repository
2. Create a new feature branch
3. Ensure tests pass (`./mvnw test`)
4. Open a pull request


## 👤 Author
**Made by Aristodimos Avdeliodis**  
[GitHub](https://github.com/AvdAris)


## 📄 License
MIT License — see [LICENSE](LICENSE) for details.


## 🙏 Acknowledgements
Thanks to the Spring Boot and Vue communities for clean frameworks and great tooling.
