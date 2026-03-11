# Contributing to Fraud Detection System

Thank you for your interest in contributing to the Fraud Detection System! This document provides guidelines and instructions for contributing.

## 🤝 How to Contribute

### Reporting Bugs

1. **Check existing issues** to avoid duplicates
2. **Use the bug report template** when creating a new issue
3. **Include detailed information**:
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (OS, Java version, Node version)
   - Screenshots if applicable
   - Error logs

### Suggesting Features

1. **Check existing feature requests** first
2. **Use the feature request template**
3. **Provide clear use cases** and benefits
4. **Consider implementation complexity**

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes** following our coding standards
4. **Write tests** for new functionality
5. **Update documentation** as needed
6. **Commit with clear messages** (see commit guidelines below)
7. **Push to your fork** (`git push origin feature/amazing-feature`)
8. **Open a Pull Request** with detailed description

## 📝 Coding Standards

### Backend (Java)

```java
// Use meaningful names
public class TransactionService {
    private final TransactionRepository repository;
    
    // JavaDoc for public methods
    /**
     * Process a new transaction and evaluate for fraud.
     * @param request Transaction details
     * @return Processed transaction with risk score
     */
    public TransactionResponse processTransaction(TransactionRequest request) {
        // Implementation
    }
}
```

**Standards:**
- Follow Java naming conventions (camelCase, PascalCase)
- Use Lombok annotations to reduce boilerplate
- Write JavaDoc for public APIs
- Keep methods focused (Single Responsibility)
- Use dependency injection
- Handle exceptions appropriately
- Write unit tests (minimum 70% coverage)

### Frontend (TypeScript/React)

```typescript
// Use functional components with TypeScript
interface TransactionCardProps {
  transaction: Transaction
  onRetry: (id: string) => void
}

export function TransactionCard({ transaction, onRetry }: TransactionCardProps) {
  return (
    <div className="glass-card p-6">
      {/* Component content */}
    </div>
  )
}
```

**Standards:**
- Use TypeScript for type safety
- Functional components with hooks
- Props interfaces for all components
- Descriptive component and variable names
- Extract reusable logic into custom hooks
- Use TailwindCSS utility classes
- Follow React best practices

### Database

- Use Flyway migrations for schema changes
- Name migrations: `V{version}__{description}.sql`
- Include rollback scripts when possible
- Add indexes for frequently queried columns
- Document complex queries

## 🧪 Testing Guidelines

### Backend Tests

```java
@SpringBootTest
class TransactionServiceTest {
    
    @Autowired
    private TransactionService service;
    
    @Test
    void shouldEvaluateHighRiskTransaction() {
        // Arrange
        TransactionRequest request = createHighValueTransaction();
        
        // Act
        TransactionResponse response = service.processTransaction(request);
        
        // Assert
        assertThat(response.getRiskScore()).isGreaterThan(80);
    }
}
```

### Frontend Tests

```typescript
import { render, screen } from '@testing-library/react'
import { TransactionCard } from './TransactionCard'

describe('TransactionCard', () => {
  it('displays transaction amount', () => {
    const transaction = createMockTransaction()
    render(<TransactionCard transaction={transaction} />)
    expect(screen.getByText(/5000 USD/i)).toBeInTheDocument()
  })
})
```

## 📋 Commit Message Guidelines

Use conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**

```
feat(rules): add support for time-based rules

Implement time window evaluation for transaction velocity checks.
Adds support for checking transaction frequency within specified time periods.

Closes #123
```

```
fix(alerts): resolve WebSocket reconnection issue

Fix bug where WebSocket connection was not automatically reconnecting
after network interruption.

Fixes #456
```

## 🔄 Development Workflow

1. **Sync your fork** with upstream regularly
2. **Create feature branch** from `develop`
3. **Make changes** in small, logical commits
4. **Test thoroughly** (unit + integration)
5. **Update documentation** if needed
6. **Rebase on develop** before submitting PR
7. **Submit PR** with clear description
8. **Address review comments** promptly

## 🏗️ Project Structure

```
fraud-detection/
├── backend/
│   ├── src/main/java/com/fraud/detection/
│   │   ├── controller/    # REST endpoints
│   │   ├── service/       # Business logic
│   │   ├── repository/    # Data access
│   │   ├── entity/        # Domain models
│   │   ├── dto/           # Data transfer objects
│   │   ├── rules/         # Rule engine
│   │   └── security/      # Security config
│   └── src/test/          # Tests
├── frontend/
│   ├── src/
│   │   ├── components/    # Reusable components
│   │   ├── pages/         # Page components
│   │   ├── utils/         # Utilities
│   │   └── state/         # State management
│   └── tests/             # Tests
└── docs/                  # Documentation
```

## 🐛 Debugging Tips

### Backend

```bash
# Enable debug logging
./mvnw spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.fraud.detection=DEBUG"

# Run specific test
./mvnw test -Dtest=TransactionServiceTest

# Profile application
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

### Frontend

```bash
# Enable verbose logging
VITE_LOG_LEVEL=debug npm run dev

# Run specific test
npm test -- TransactionCard

# Build with source maps
npm run build -- --sourcemap
```

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [TailwindCSS Documentation](https://tailwindcss.com/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 🎯 Priority Areas for Contribution

### High Priority
- [ ] Machine learning integration
- [ ] Email/SMS notification system
- [ ] Advanced analytics features
- [ ] Performance optimizations
- [ ] Additional fraud detection rules

### Medium Priority
- [ ] Multi-language support (i18n)
- [ ] Dark/light theme toggle
- [ ] Export functionality (CSV, PDF)
- [ ] Batch transaction processing
- [ ] Advanced filtering and search

### Low Priority
- [ ] Mobile responsive improvements
- [ ] Accessibility enhancements
- [ ] Additional chart types
- [ ] Custom dashboard widgets
- [ ] Integration with external APIs

## ✅ Pull Request Checklist

Before submitting a PR, ensure:

- [ ] Code follows project style guidelines
- [ ] All tests pass (`./mvnw test` and `npm test`)
- [ ] New tests added for new features
- [ ] Documentation updated
- [ ] Commit messages follow conventions
- [ ] No console.log or debug statements
- [ ] No commented-out code
- [ ] Branch is up to date with develop
- [ ] PR description is clear and detailed

## 🔐 Security

- **Never commit secrets** (API keys, passwords)
- **Use environment variables** for configuration
- **Report security issues** privately via email
- **Follow OWASP guidelines** for web security
- **Keep dependencies updated**

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

## 💬 Communication

- **GitHub Issues**: Bug reports and feature requests
- **Pull Requests**: Code contributions
- **Discussions**: General questions and ideas
- **Email**: kabileshc.dev@gmail.com (security issues only)

## 🙏 Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- Project documentation

Thank you for contributing to making financial transactions safer! 🛡️

## Code of Conduct
Please follow standard professional conduct when contributing.
