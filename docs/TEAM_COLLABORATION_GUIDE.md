# Team Collaboration Guide for Block20 Gym Management System

## Table of Contents
1. [Project Overview](#project-overview)
2. [GitHub Repository Structure](#github-repository-structure)
3. [Development Workflow](#development-workflow)
4. [Branch Strategy](#branch-strategy)
5. [Team Roles & Responsibilities](#team-roles--responsibilities)
6. [Communication Guidelines](#communication-guidelines)
7. [Code Review Process](#code-review-process)
8. [Integration Strategy](#integration-strategy)

---

## Project Overview

**Project Name:** Block20 - Gym and Fitness Center Management System  
**Architecture:** Three-tier application (Frontend, Backend, Database)  
**Tech Stack:**
- **Frontend:** JavaFX 21 (Desktop Application)
- **Backend:** Vanilla Java (to be implemented)
- **Database:** To be decided (see DATABASE_GUIDE.md)

**Current Status:** Frontend implementation complete, Backend and Database to be developed.

---

## GitHub Repository Structure

### Recommended Branch Structure

```
main (protected)
├── development (default branch for ongoing work)
├── frontend/
│   ├── feature/login-ui
│   ├── feature/member-portal
│   ├── bugfix/overflow-fix
│   └── refactor/css-cleanup
├── backend/
│   ├── feature/member-service
│   ├── feature/authentication
│   ├── feature/payment-processing
│   └── bugfix/validation-error
└── database/
    ├── feature/schema-setup
    ├── feature/migrations
    └── bugfix/constraint-fix
```

### Branch Naming Convention

Use this format: `<type>/<layer>/<description>`

**Types:**
- `feature/` - New functionality
- `bugfix/` - Bug fixes
- `hotfix/` - Critical production fixes
- `refactor/` - Code improvements without changing functionality
- `docs/` - Documentation updates
- `test/` - Test additions or updates

**Layers:**
- `frontend/` - JavaFX UI changes
- `backend/` - Java backend logic
- `database/` - Database schema or queries
- `integration/` - Cross-layer work

**Examples:**
```
feature/backend/member-authentication
bugfix/frontend/table-rendering
feature/database/payment-schema
feature/integration/member-enrollment-flow
```

---

## Development Workflow

### 1. Getting Started (For New Team Members)

```powershell
# Clone the repository
git clone https://github.com/ibbi1020/SDA-Final-Project.git
cd SDA-Final-Project

# Checkout development branch
git checkout development

# Pull latest changes
git pull origin development

# Create your feature branch
git checkout -b feature/backend/your-feature-name
```

### 2. Daily Development Cycle

```powershell
# Start of day - sync with development branch
git checkout development
git pull origin development
git checkout your-feature-branch
git merge development  # Resolve conflicts if any

# Work on your feature
# ... make changes ...

# Commit frequently with clear messages
git add .
git commit -m "feat(backend): implement member registration service"

# End of day - push your work
git push origin your-feature-branch
```

### 3. Completing a Feature

```powershell
# Ensure all tests pass
.\gradlew test

# Ensure code builds
.\gradlew build

# Push final changes
git push origin your-feature-branch

# Create Pull Request on GitHub
# Request review from team members
```

---

## Branch Strategy

### Protected Branches

#### `main` Branch
- **Purpose:** Production-ready code only
- **Protection Rules:**
  - Require pull request reviews (minimum 2 approvals)
  - Require status checks to pass
  - No direct commits allowed
  - Only merge from `development` branch
  - Enforce linear history (squash or rebase merges)

#### `development` Branch
- **Purpose:** Integration branch for all features
- **Protection Rules:**
  - Require pull request reviews (minimum 1 approval)
  - Require status checks to pass
  - No direct commits allowed
  - Merge from feature branches

### Feature Branches
- Created from `development`
- Merged back to `development` via Pull Request
- Deleted after successful merge
- Short-lived (ideally < 1 week)

### Branch Lifecycle

```
development
    ↓ create
feature/backend/member-service
    ↓ work
feature/backend/member-service (commits)
    ↓ pull request
development (after review & merge)
    ↓ periodic release
main (tagged release)
```

---

## Team Roles & Responsibilities

### 1. Frontend Team (UI/UX)
**Responsibilities:**
- Maintain and enhance JavaFX controllers
- Implement new UI features based on user stories
- Fix UI bugs and improve user experience
- Ensure responsive design and accessibility
- Update CSS styling system

**Key Files:**
- `app/src/main/java/com/block20/controllers/`
- `app/src/main/java/com/block20/views/`
- `app/src/main/resources/com/block20/styles/`

**Dependencies:** 
- Need backend API contracts (interfaces) before integration
- Mock data until backend APIs are ready

---

### 2. Backend Team (Business Logic)
**Responsibilities:**
- Implement business logic and services
- Create RESTful API endpoints (or method interfaces for desktop app)
- Handle data validation and processing
- Implement authentication and authorization
- Write unit and integration tests

**Key Directories (to be created):**
- `app/src/main/java/com/block20/services/`
- `app/src/main/java/com/block20/repositories/`
- `app/src/main/java/com/block20/models/`
- `app/src/main/java/com/block20/utils/`

**Dependencies:**
- Need database schema before implementing repositories
- Need frontend contracts (what data UI needs)

---

### 3. Database Team (Data Persistence)
**Responsibilities:**
- Design database schema
- Write migration scripts
- Create indexes and optimize queries
- Implement backup and recovery procedures
- Write database documentation

**Key Files (to be created):**
- `database/schema/`
- `database/migrations/`
- `database/seeds/` (test data)

**Dependencies:**
- Need domain models from backend team
- Need performance requirements from frontend team

---

### 4. Integration Team (Cross-Layer)
**Responsibilities:**
- Connect frontend to backend
- Ensure data flows correctly between layers
- Write end-to-end tests
- Handle error propagation
- Performance optimization

**Focus Areas:**
- Controller → Service integration
- Service → Repository integration
- Repository → Database integration

---

## Communication Guidelines

### Daily Standup (15 minutes)
**Time:** Start of each work session  
**Format:** Each team member answers:
1. What did I complete yesterday?
2. What will I work on today?
3. Any blockers or dependencies?

### Weekly Sync (30 minutes)
**Time:** Once per week  
**Agenda:**
- Demo completed features
- Review integration points
- Plan next week's work
- Address technical challenges

### Communication Channels

#### GitHub Issues
- **Bug Reports:** Use template with reproduction steps
- **Feature Requests:** Link to user stories
- **Technical Discussions:** Tag relevant team members

**Labels:**
- `frontend`, `backend`, `database`
- `bug`, `enhancement`, `documentation`
- `priority-high`, `priority-medium`, `priority-low`
- `blocked`, `in-progress`, `ready-for-review`

#### Pull Request Discussions
- Ask questions on specific code lines
- Suggest improvements
- Request clarification
- Share knowledge

---

## Code Review Process

### Pull Request Guidelines

#### Creating a Pull Request

1. **Title Format:** `[TYPE] Brief description`
   - Examples: `[FEATURE] Add member authentication service`
   - `[BUGFIX] Fix date formatting in enrollment`

2. **Description Template:**
```markdown
## Description
Brief summary of changes

## Related Issues
Closes #123

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings or errors
```

3. **Screenshots/Videos:** Include for UI changes

#### Reviewing a Pull Request

**Reviewer Checklist:**
- [ ] Code follows Java best practices
- [ ] No security vulnerabilities
- [ ] Error handling is comprehensive
- [ ] Tests are adequate
- [ ] Documentation is clear
- [ ] Performance considerations addressed
- [ ] No unnecessary dependencies

**Review Feedback Format:**
- 🔴 **Must Fix:** Critical issues blocking merge
- 🟡 **Should Fix:** Important but not blocking
- 🟢 **Nice to Have:** Suggestions for improvement
- 💡 **Question:** Seeking clarification

**Response Time:**
- First response: Within 24 hours
- Full review: Within 48 hours
- Re-review after changes: Within 24 hours

---

## Integration Strategy

### Phase 1: API Contracts (Week 1-2)
**Goal:** Define interfaces between layers

**Backend Team:**
- Create service interfaces (method signatures)
- Define DTOs (Data Transfer Objects)
- Document expected inputs/outputs

**Frontend Team:**
- Review and validate contracts
- Provide feedback on data needs
- Create mock implementations

**Example Contract:**
```java
// Service Interface
public interface MemberService {
    MemberDTO registerMember(MemberRegistrationRequest request) 
        throws ValidationException;
    
    MemberDTO getMemberById(String memberId) 
        throws MemberNotFoundException;
    
    List<MemberDTO> searchMembers(MemberSearchCriteria criteria);
}
```

### Phase 2: Backend Implementation (Week 3-5)
**Goal:** Implement business logic and database integration

**Backend Team:**
- Implement service classes
- Create repository layer
- Write unit tests

**Database Team:**
- Create schema
- Implement repositories
- Add seed data

**Frontend Team:**
- Continue using mocks
- Prepare for integration

### Phase 3: Integration (Week 6-7)
**Goal:** Connect all layers

**Integration Team:**
- Replace mocks with real services
- Test end-to-end flows
- Fix integration issues

**All Teams:**
- Fix bugs discovered during integration
- Performance testing
- User acceptance testing

### Phase 4: Testing & Refinement (Week 8)
**Goal:** Ensure quality and performance

**All Teams:**
- Load testing
- Security testing
- Bug fixes
- Documentation updates

---

## Best Practices

### Git Commits

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting changes
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Build process or tooling

**Examples:**
```
feat(backend): implement member authentication service

- Add JWT token generation
- Implement password hashing with BCrypt
- Add login attempt rate limiting

Closes #45
```

```
fix(frontend): resolve table overflow in member registry

Fixed FlowPane wrapping to prevent horizontal scroll.
Card max-width set to 280px.

Fixes #67
```

### Code Quality

1. **Follow SOLID Principles**
2. **Write self-documenting code**
3. **Add comments for complex logic**
4. **Keep methods small and focused**
5. **Use meaningful variable names**
6. **Handle errors gracefully**
7. **Write tests for critical paths**

### Documentation

1. **Update README.md** when adding features
2. **Document API contracts** in code and docs
3. **Keep .github docs** up to date
4. **Add inline documentation** for public methods
5. **Document configuration** changes

---

## Conflict Resolution

### Technical Disagreements
1. Discuss in team meeting
2. Present pros/cons of each approach
3. Vote if consensus not reached
4. Document decision in GitHub issue

### Merge Conflicts
1. Person with newest changes resolves conflicts
2. Ask for help if unsure
3. Test thoroughly after resolving
4. Update tests if needed

### Blocked Work
1. Post in team channel immediately
2. Tag person who can unblock
3. Work on other tasks while waiting
4. Escalate if blocked > 24 hours

---

## Getting Help

### For Beginners
- Check BACKEND_ARCHITECTURE_GUIDE.md
- Check DATABASE_GUIDE.md
- Ask in team channel
- Pair program with experienced member

### For Everyone
- Search GitHub issues for similar problems
- Check Stack Overflow
- Read official documentation
- Ask team lead for guidance

---

## Success Metrics

### Individual
- Code review participation
- Pull request quality
- Timely responses
- Test coverage

### Team
- Sprint velocity
- Bug rate
- Code quality metrics
- Integration success rate

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Nov 18, 2025 | Initial guide created |

---

## Resources

- [User Stories](USER_STORIES.md)
- [User Flows](USER_FLOWS.md)
- [Design System](DESIGN_SYSTEM.md)
- [Backend Architecture Guide](BACKEND_ARCHITECTURE_GUIDE.md)
- [Database Guide](DATABASE_GUIDE.md)

---

**Remember:** Good communication and collaboration are as important as good code! 🚀
