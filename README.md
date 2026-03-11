# Flux-Guard-Java

## Abstract

A production-quality real-time fraud detection system designed to identify and prevent fraudulent transactions through intelligent rule-based analysis, real-time monitoring, and comprehensive analytics. The system provides immediate transaction evaluation, customizable fraud detection rules, real-time alerts, and detailed reporting capabilities.

## System Capabilities

- **Real-time Transaction Processing**: Instant ingestion and evaluation of financial transactions
- **Rule-Based Fraud Detection**: Customizable rule engine with dynamic risk scoring
- **Live Alert System**: WebSocket-based real-time notifications for suspicious activities
- **Admin Dashboard**: Comprehensive analytics with charts, KPIs, and transaction insights
- **Rule Management**: Visual rule editor for creating and managing fraud detection logic
- **Audit Trail**: Complete transaction history with detailed evaluation breakdowns
- **Retry & Reset Mechanisms**: Manual review and re-evaluation capabilities

## Non-Functional Goals

- **Latency Target**: < 200ms transaction evaluation time
- **SLA**: 99.9% uptime for transaction processing
- **Security**: JWT authentication, role-based access control, encrypted data transmission
- **Scalability**: Designed for horizontal scaling with async processing capabilities

## Technology Stack

### Backend
- **Java 17+** with Spring Boot 3.x
- **Spring Web, Data JPA, Security, WebSocket**
- **PostgreSQL** (hosted on Supabase)
- **Flyway** for database migrations
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for API documentation

### Frontend
- **React 18+** with TypeScript
- **TailwindCSS** for styling
- **Recharts** for data visualization
- **React Router** for navigation
- **Zustand** for state management
- **WebSocket** client for real-time updates
- **Axios** for API communication

### DevOps
- **Docker** for containerization
- **GitHub Actions** for CI/CD
- **Supabase** for database and authentication
- **Render/Railway** for backend hosting
- **Vercel/Netlify** for frontend hosting

## Project Structure

```
fraud-detection/
├── backend/                    # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fraud/detection/
│   │   │   │   ├── config/           # Configuration classes
│   │   │   │   ├── controller/       # REST controllers
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── entity/           # JPA entities
│   │   │   │   ├── repository/       # Data repositories
│   │   │   │   ├── service/          # Business logic
│   │   │   │   ├── rules/            # Rule engine components
│   │   │   │   ├── websocket/        # WebSocket handlers
│   │   │   │   └── security/         # Security configuration
│   │   │   └── resources/
│   │   │       ├── db/migration/     # Flyway migrations
│   │   │       └── application.yml   # Application config
│   │   └── test/                     # Unit & integration tests
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   # React application
│   ├── src/
│   │   ├── components/         # Reusable UI components
│   │   ├── pages/              # Page components
│   │   ├── utils/              # Utilities (API, WebSocket)
│   │   ├── state/              # State management
│   │   ├── types/              # TypeScript types
│   │   └── styles/             # Global styles
│   ├── public/
│   ├── Dockerfile
│   ├── package.json
│   └── tailwind.config.js
│
├── docker-compose.yml          # Local development setup
├── .github/workflows/          # CI/CD pipelines
└── docs/                       # Additional documentation
```

## Features & Modules

### 1. Transaction Input Module
- REST API for transaction ingestion
- Client-side and server-side validation
- Support for batch uploads and single transactions
- Fields: transaction ID, user ID, amount, currency, accounts, geolocation, device fingerprint

### 2. Processing & Validation Module
- Data normalization and enrichment
- Historical feature computation (velocity, averages)
- Blacklist checking
- Rate limiting and throttling

### 3. Fraud Logic Module (Core)
- Custom rule engine with expression parser
- Risk scoring algorithm (weighted rule aggregation)
- Rule evaluation audit trail
- Support for complex conditions (AND, OR, time windows)
- Configurable thresholds (APPROVE/REVIEW/REJECT)

### 4. Alert & Notification Module
- Real-time WebSocket alerts
- Email/SMS integration (optional)
- Alert management (read, dismiss, bulk actions)
- Severity-based routing

### 5. Reporting & Dashboard Module
- KPI cards (total transactions, flagged ratio, avg risk)
- Time-series charts (transaction volume over time)
- Risk score distribution
- Top suspicious accounts
- Filterable transaction tables

### 6. Output & Feedback Module
- Transaction detail view with timeline
- Rule evaluation breakdown
- Admin action buttons (approve, reject, mark false positive)
- Retry and reset capabilities

## Database Schema

### Core Tables
- **transactions**: Transaction records with risk scores and status
- **users**: User accounts and profiles
- **rules**: Fraud detection rules with expressions
- **rule_evaluations**: Audit trail of rule matches
- **alerts**: Alert records with status tracking
- **audit_logs**: System and admin action logs

### Materialized Views
- **txn_agg_by_hour**: Hourly aggregated metrics for dashboard

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login (returns JWT)
- `POST /api/auth/register` - Admin registration

### Transactions
- `POST /api/transactions` - Ingest new transaction
- `GET /api/transactions/{id}` - Get transaction details
- `GET /api/transactions` - List transactions (paginated, filterable)
- `POST /api/transactions/{id}/retry` - Re-evaluate transaction
- `POST /api/transactions/{id}/reset` - Reset transaction status

### Rules
- `GET /api/rules` - List all rules
- `POST /api/rules` - Create new rule
- `PUT /api/rules/{id}` - Update rule
- `DELETE /api/rules/{id}` - Delete rule
- `GET /api/rules/{id}/test` - Test rule against sample data

### Dashboard
- `GET /api/dashboard/summary` - Get KPI summary
- `GET /api/dashboard/time-series` - Get time-series data
- `GET /api/dashboard/risk-distribution` - Get risk score distribution

### WebSocket
- `CONNECT /ws` - WebSocket connection
- `SUBSCRIBE /topic/alerts` - Subscribe to global alerts
- `SUBSCRIBE /user/queue/alerts` - Subscribe to user-specific alerts

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL (or Supabase account)

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd fraud-detection
```

2. **Setup environment variables**
```bash
# Backend (.env in backend/)
DB_URL=jdbc:postgresql://localhost:5432/fraud_detection
DB_USER=postgres
DB_PASS=your_password
JWT_SECRET=your_jwt_secret
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_supabase_key

# Frontend (.env in frontend/)
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
```

3. **Start with Docker Compose**
```bash
docker-compose up -d
```

4. **Or run manually**

Backend:
```bash
cd backend
./mvnw spring-boot:run
```

Frontend:
```bash
cd frontend
npm install
npm run dev
```

5. **Access the application**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- API Docs: http://localhost:8080/swagger-ui.html

### Default Admin Credentials
- Username: `admin@fraud-detection.com`
- Password: `Admin@123`

## Rule Expression Examples

```json
{
  "id": "rule_high_amount",
  "name": "High Amount Single Transaction",
  "description": "Flags transactions above $10,000",
  "expression": "amount > 10000 AND currency == 'USD'",
  "weight": 85,
  "severity": "HIGH",
  "action": "REJECT",
  "active": true
}
```

```json
{
  "id": "rule_velocity",
  "name": "Rapid Transaction Velocity",
  "description": "Flags users with >10 transactions in 1 hour",
  "expression": "txn_count_last_1h > 10",
  "weight": 70,
  "severity": "MEDIUM",
  "action": "FLAG",
  "active": true
}
```

## Risk Scoring Algorithm

1. Evaluate all active rules against transaction
2. Sum weights of triggered rules
3. Apply penalty factors (new device, unusual location, etc.)
4. Normalize to 0-100 scale
5. Apply thresholds:
   - **0-49**: APPROVED (low risk)
   - **50-79**: FLAGGED (manual review)
   - **80-100**: REJECTED (high risk)

## Security Features

- JWT-based authentication
- Role-based access control (ADMIN, USER)
- Parameterized SQL queries (JPA)
- Safe expression evaluation (no arbitrary code execution)
- Rate limiting on ingestion API
- HTTPS enforcement
- CORS configuration
- Input validation and sanitization

## Performance Optimizations

- Database indexes on frequently queried columns
- Materialized views for dashboard aggregations
- In-memory rule caching
- Connection pooling
- Async processing for non-critical operations
- Batch operations for bulk updates

## Testing

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test

# E2E tests
npm run test:e2e
```

## Deployment

### Backend (Render/Railway)
1. Connect GitHub repository
2. Set environment variables
3. Deploy from `main` branch
4. Run Flyway migrations automatically

### Frontend (Vercel/Netlify)
1. Connect GitHub repository
2. Set build command: `npm run build`
3. Set output directory: `dist`
4. Deploy from `main` branch

### Database (Supabase)
1. Create new project
2. Copy connection string
3. Run migrations via Flyway or Supabase dashboard

## Future Enhancements (Phase II)

- **Machine Learning Integration**: ML-based fraud scoring
- **Advanced Analytics**: Anomaly detection, pattern recognition
- **Multi-tenant Support**: Isolated environments for different organizations
- **External Integrations**: Third-party risk scoring APIs
- **Mobile App**: React Native mobile application
- **Blockchain Integration**: Immutable audit trail

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues and questions:
- GitHub Issues: [Create an issue](https://github.com/your-org/fraud-detection/issues)
- Email: support@fraud-detection.com
- Documentation: [Wiki](https://github.com/your-org/fraud-detection/wiki)

## Acknowledgments

- Spring Boot team for the excellent framework
- React community for frontend tools
- Supabase for database and authentication services

## 📚 Further Documentation
See [INDEX.md](./INDEX.md) for full project documentation.
