# Block20 Gym Management System - User Stories

## Document Overview
This document contains user stories derived from the fully-dressed use cases, system sequence diagrams, and domain models. Each story includes acceptance criteria and references to related business rules.

**Project:** Block20 - Comprehensive Gym Management System  
**Version:** 1.0  
**Last Updated:** November 2025

---

## Epic 1: Member Management

### Story M1.1: Create New Member Profile
**ID:** M1.1  
**Epic:** Member Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 5

**As a** Gym Administrator  
**I want to** create a new member profile with comprehensive personal information  
**So that** I can maintain accurate and organized member records

**Acceptance Criteria:**
- [ ] System displays member creation form with fields: name, phone, email, emergency contact info
- [ ] System validates email format before saving
- [ ] System validates phone number format (international format support)
- [ ] System checks for duplicate members based on name + contact combination
- [ ] System auto-generates unique member ID upon successful creation
- [ ] System prevents duplicate email addresses in database
- [ ] System creates audit trail entry for member creation
- [ ] System generates PDF member card for new member
- [ ] System displays confirmation message with generated member ID
- [ ] Response time for profile save is under 2 seconds

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass with database
- Manual testing completed
- Documentation updated

**Related Use Case:** UC1 - Manage Member Profiles  
**Related Classes:** Member, MemberController, MemberRepository, ValidationService, DuplicateCheckService, IDGenerationService

**Business Rules:**
- Member status types: Active, Inactive, Suspended, Frozen
- Emergency contact information is mandatory
- Data retention policy: Inactive member data retained for 1 year
- Privacy compliance: GDPR compliant member data storage

---

### Story M1.2: Update Existing Member Profile
**ID:** M1.2  
**Epic:** Member Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 5

**As a** Gym Administrator  
**I want to** update existing member information (name, contact details, emergency contact)  
**So that** I can keep member records current and accurate

**Acceptance Criteria:**
- [ ] System retrieves member by ID or name search
- [ ] System displays current member information in editable form
- [ ] System validates all changes before saving
- [ ] System prevents duplicate email updates if email already exists
- [ ] System creates audit trail showing what fields were changed
- [ ] System displays timestamp of last update
- [ ] System shows confirmation of successful update
- [ ] System prevents changes to member ID (immutable)
- [ ] Response time for profile update is under 2 seconds

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass with database
- Manual testing completed

**Related Use Case:** UC1 - Manage Member Profiles

---

### Story M1.3: Deactivate Member Account
**ID:** M1.3  
**Epic:** Member Management  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Gym Administrator  
**I want to** deactivate member accounts for inactive members  
**So that** I can maintain accurate member status and prevent access for inactive members

**Acceptance Criteria:**
- [ ] System displays reason selection dropdown for deactivation
- [ ] System checks for outstanding dues before deactivation
- [ ] System displays warning if member has unpaid balance
- [ ] System allows deactivation even with outstanding dues (with administrator confirmation)
- [ ] System updates member status to "Inactive"
- [ ] System disables access credentials
- [ ] System prevents access to gym facilities immediately
- [ ] System creates audit trail entry with deactivation reason
- [ ] System sends deactivation confirmation to member
- [ ] Inactive members can be reactivated within 30 days without rejoin fee

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC1 - Manage Member Profiles (Alternative Scenario 3a)

---

### Story M1.4: Search and Filter Members
**ID:** M1.4  
**Epic:** Member Management  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Gym Administrator  
**I want to** search for members by ID, name, email, or phone number  
**So that** I can quickly locate member records

**Acceptance Criteria:**
- [ ] System provides search box on member management interface
- [ ] System supports search by: member ID, name, email, phone number
- [ ] System displays search results in table format
- [ ] System supports filtering by member status (Active, Inactive, Suspended, Frozen)
- [ ] System supports filtering by membership plan type
- [ ] System supports filtering by membership expiration date range
- [ ] Search is case-insensitive
- [ ] Search results are paginated (10, 25, 50 items per page)
- [ ] Search completes within 1 second for 10,000+ members
- [ ] System highlights matching text in results

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed
- Performance tested with large datasets

**Related Use Case:** UC1 - Manage Member Profiles

---

## Epic 2: Member Enrollment

### Story E2.1: Initiate Member Enrollment Process
**ID:** E2.1  
**Epic:** Member Enrollment  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Sales Staff  
**I want to** guide prospective members through a structured enrollment process  
**So that** I can efficiently onboard new members and collect all necessary information

**Acceptance Criteria:**
- [ ] System displays enrollment form with all required fields
- [ ] System collects: name, contact info, emergency contact, date of birth
- [ ] System displays available membership plans (Basic, Premium, Student, Senior)
- [ ] System shows plan features and pricing clearly
- [ ] System calculates membership fees and applicable taxes
- [ ] System validates all member information before proceeding
- [ ] System prevents duplicate enrollments (checks against existing members)
- [ ] System allows returning members to upgrade/change plans
- [ ] Enrollment process can be completed within 10 minutes
- [ ] System saves enrollment progress to allow resumption

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC2 - Process Member Enrollment  
**Related Classes:** EnrollmentController, MemberRepository, PlanRepository, PricingService

**Business Rules:**
- Default membership plans:
  - Basic: $29.99/month - Access during off-peak hours
  - Premium: $49.99/month - 24/7 access, guest privileges
  - Student: $24.99/month - Valid student ID required
  - Senior: $19.99/month - Age 60+
- Enrollment cancellation window: 7 days with full refund

---

### Story E2.2: Process Enrollment Payment
**ID:** E2.2  
**Epic:** Member Enrollment  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Sales Staff  
**I want to** securely process membership payment during enrollment  
**So that** I can collect payment and activate member access

**Acceptance Criteria:**
- [ ] System supports payment methods: credit card, debit card, cash (with receipt)
- [ ] System integrates with secure payment gateway (PCI DSS compliant)
- [ ] System displays payment form with card details fields
- [ ] System validates card information before processing
- [ ] System shows payment confirmation with transaction ID
- [ ] System handles payment failures gracefully with retry option
- [ ] System records payment in transaction history
- [ ] System generates receipt (printed and/or emailed)
- [ ] System logs all payment transactions for audit purposes
- [ ] Payment fails safely without compromising member data

**Definition of Done:**
- Code peer reviewed and approved
- Security review completed
- Integration tests pass with payment gateway
- Manual testing completed
- PCI DSS compliance verified

**Related Use Case:** UC2 - Process Member Enrollment (steps 7-8)  
**Related Classes:** PaymentService, PaymentController, IPaymentGateway

**Business Rules:**
- PCI DSS compliance required for payment processing
- Transaction rollback on database failure

---

### Story E2.3: Generate and Sign Membership Agreement
**ID:** E2.3  
**Epic:** Member Enrollment  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** Sales Staff  
**I want to** generate membership agreement documents and collect member signature  
**So that** I can ensure legal compliance and document member acceptance of terms

**Acceptance Criteria:**
- [ ] System generates membership agreement document with member and plan details
- [ ] System displays agreement in digital format (PDF)
- [ ] System allows member to review agreement terms
- [ ] System captures member digital signature (e-signature support)
- [ ] System stores signed agreement with audit timestamp
- [ ] System retrieves and displays signed agreements when needed
- [ ] Agreement templates are customizable by administrators
- [ ] System associates agreement with enrollment record
- [ ] Multiple agreement versions can be managed

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed
- Legal review completed

**Related Use Case:** UC2 - Process Member Enrollment (steps 9-10)  
**Related Classes:** AgreementService, AgreementRepository

**Business Rules:**
- Agreement templates stored in document repository

---

### Story E2.4: Create Member Access Credentials
**ID:** E2.4  
**Epic:** Member Enrollment  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 5

**As a** System  
**I want to** automatically generate secure access credentials for new members  
**So that** members can immediately access gym facilities and online systems

**Acceptance Criteria:**
- [ ] System generates unique credentials for each member
- [ ] System includes member ID and temporary password
- [ ] System follows password policy: Minimum 8 characters, complexity requirements
- [ ] System sends credentials via email/SMS to member
- [ ] System enforces password change on first login
- [ ] System stores credentials securely (hashed)
- [ ] System maintains credential history for audit purposes
- [ ] System can reset credentials if needed
- [ ] Credentials expire after configured time period

**Definition of Done:**
- Code peer reviewed and approved
- Security review completed
- Unit tests achieve 95%+ coverage
- Manual testing completed

**Related Use Case:** UC2 - Process Member Enrollment (step 11)  
**Related Classes:** CredentialsService, Credentials

**Business Rules:**
- Password policy: Minimum 8 characters, complexity requirements, 90-day expiration

---

## Epic 3: Membership Renewals

### Story R3.1: Track Upcoming Membership Expirations
**ID:** R3.1  
**Epic:** Membership Renewals  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** System  
**I want to** automatically identify members with upcoming expiration dates  
**So that** renewal reminders can be sent proactively

**Acceptance Criteria:**
- [ ] System runs daily batch job to check expiring memberships
- [ ] System identifies memberships expiring within 30, 15, and 7 days
- [ ] System maintains list of members flagged for renewal notification
- [ ] System handles edge cases (timezone differences, leap years)
- [ ] System supports customizable notification thresholds
- [ ] System prevents duplicate notifications
- [ ] System logs all expiration checks for audit purposes

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC3 - Handle Membership Renewals (step 1)  
**Related Classes:** RenewalController, MemberRepository

---

### Story R3.2: Send Renewal Reminders to Members
**ID:** R3.2  
**Epic:** Membership Renewals  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 5

**As a** System  
**I want to** send renewal reminders within member access portal as a popup notification 
**So that** members are reminded of upcoming expiration and renewal benefits

**Acceptance Criteria:**
- [ ] System sends automated renewal reminder 30 days before expiration
- [ ] System sends second reminder 15 days before expiration
- [ ] System sends final reminder 7 days before expiration
- [ ] System supports email and SMS notification channels
- [ ] System allows members to set preferred notification method
- [ ] System tracks reminder delivery status
- [ ] Reminder templates include renewal benefits and discounts
- [ ] System allows administrators to send manual renewal reminders
- [ ] System prevents multiple reminders on same day
- [ ] Renewal reminder includes link to online renewal portal

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration with notification service verified
- Manual testing completed

**Related Use Case:** UC3 - Handle Membership Renewals (steps 2-3)  
**Related Classes:** NotificationService, RenewalNotification

**Business Rules:**
- Automated renewal reminders at 30, 15, and 7 days before expiration
- Grace period of 3 days after expiration before access suspension

---

### Story R3.3: Process Membership Renewal
**ID:** R3.3  
**Epic:** Membership Renewals  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Staff Member  
**I want to** process membership renewal and extend access for existing members  
**So that** members can continue gym access without interruption

**Acceptance Criteria:**
- [ ] System retrieves member account and displays current membership details
- [ ] System shows membership expiration date and status
- [ ] System displays renewal fee and applicable discounts
- [ ] System calculates discounts: 10% for early renewals (30+ days in advance), 5% loyalty discount (3+ consecutive years)
- [ ] Member can change membership plan during renewal
- [ ] System recalculates fees if plan change occurs
- [ ] System processes renewal payment securely
- [ ] System extends membership validity period
- [ ] System generates updated membership card
- [ ] System sends renewal confirmation to member
- [ ] System updates all relevant database records
- [ ] Renewal can be processed within 10 minutes
- [ ] System supports early renewals (up to 60 days before expiration)

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC3 - Handle Membership Renewals  
**Related Classes:** RenewalController, MemberRepository, PricingService, PaymentService

**Business Rules:**
- Renewal discount policy: 10% for early renewals (30+ days in advance)
- Loyalty bonus: 5% discount for members renewing for 3+ consecutive years
- Grace period of 3 days after expiration before access suspension

---

### Story R3.4: Self-Service Member Renewal Through Member Portal
**ID:** R3.4  
**Epic:** Membership Renewals  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** Member  
**I want to** renew my membership directly through the member portal  
**So that** I can extend my access conveniently without visiting the gym

**Acceptance Criteria:**
- [ ] System displays renewal option in member portal
- [ ] Portal shows membership expiration date and renewal deadline
- [ ] Portal displays renewal fee and applicable discounts
- [ ] Member can view available membership plans and choose renewal option
- [ ] Portal displays renewal benefits and terms clearly
- [ ] Member can select payment method (credit card, debit card)
- [ ] System processes payment securely through payment gateway
- [ ] System confirms renewal with new expiration date
- [ ] System sends confirmation email with updated membership details
- [ ] System updates membership record in real-time
- [ ] Portal allows early renewals (up to 60 days before expiration)
- [ ] Portal prevents duplicate renewal requests within same day

**Definition of Done:**
- Code peer reviewed and approved
- Security review completed
- Unit tests achieve 90%+ coverage
- Integration tests pass with payment gateway
- Manual testing completed

**Related Use Case:** UC3 - Handle Membership Renewals (Self-Service)  
**Related Classes:** MemberPortalController, RenewalController, PaymentService

---

### Story R3.5: Handle Non-Renewal Member Requests
**ID:** R3.5  
**Epic:** Membership Renewals  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Staff Member  
**I want to** handle members who choose not to renew their membership  
**So that** I can record reasons for non-renewal and maintain accurate records

**Acceptance Criteria:**
- [ ] System displays non-renewal reason dropdown
- [ ] Staff can select reason: cost, relocation, lost interest, facility issues, etc.
- [ ] System records reason in member profile
- [ ] System schedules membership expiration in system
- [ ] System sends exit survey to member
- [ ] System maintains member data for reactivation within 30 days
- [ ] System sends special retention offer option to member
- [ ] System generates analytics report on non-renewal reasons

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC3 - Handle Membership Renewals (Alternative Scenario 8b)

**Business Rules:**
- Lapsed memberships can be reactivated within 30 days without rejoining fee

---

## Epic 4: Payment Processing

### Story P4.1: Process Member Payments
**ID:** P4.1  
**Epic:** Payment Processing  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Cashier/Staff Member  
**I want to** process member payments for various obligations  
**So that** I can collect payment and maintain accurate financial records

**Acceptance Criteria:**
- [ ] System displays member search interface
- [ ] Staff can search by member ID or scan member card
- [ ] System retrieves and displays member outstanding obligations
- [ ] System shows all payment items: membership fees, training sessions, penalties
- [ ] System calculates and applies applicable discounts
- [ ] System calculates late fees if applicable
- [ ] System displays total amount due
- [ ] System supports multiple payment methods: cash, credit card, debit card, check
- [ ] System processes payment through secure payment gateway
- [ ] System updates member account balance after successful payment
- [ ] System generates payment receipt (simply shown on screen with option to print)
- [ ] System records transaction in audit trail
- [ ] System handles payment failures gracefully

**Definition of Done:**
- Code peer reviewed and approved
- Security review completed
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC4 - Process Member Payments  
**Related Classes:** PaymentController, PaymentService, BillingItemService, TransactionRepository

---

### Story P4.2: View Payment History and Outstanding Balances
**ID:** P4.2  
**Epic:** Payment Processing  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Staff Member or Accountant  
**I want to** view member payment history and current outstanding balances  
**So that** I can provide accurate information to members and track financial status

**Acceptance Criteria:**
- [ ] System displays member payment history with dates and amounts
- [ ] System shows payment methods used for each transaction
- [ ] System displays current outstanding balance
- [ ] System breaks down outstanding balance by category (membership, sessions, late fees)
- [ ] System allows filtering by date range
- [ ] System shows transaction details on click (transaction ID, method, receipt)
- [ ] System displays payment trends (graphical representation)
- [ ] System allows export of payment history (CSV, PDF)
- [ ] Response time for payment history retrieval is under 2 seconds

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC4 - Process Member Payments

---

## Epic 5: Financial Reporting

### Story FR5.1: Generate Financial Reports
**ID:** FR5.1  
**Epic:** Financial Reporting  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Manager or Accountant  
**I want to** generate comprehensive financial reports for analysis and decision-making  
**So that** I can understand financial performance and trends

**Acceptance Criteria:**
- [ ] System displays report generation interface with type selection
- [ ] System supports report types: Revenue, Expenses, Membership Sales, Member Collections, Account Receivable
- [ ] System allows date range selection (daily, weekly, monthly, yearly, custom)
- [ ] System displays report parameters (filters, grouping options)
- [ ] System allows preview before generating final report
- [ ] System generates comprehensive report with metrics and visualizations
- [ ] Report includes totals, subtotals, and comparisons to previous periods
- [ ] Report displays charts: bar charts, line graphs, pie charts
- [ ] System calculates KPIs: total revenue, average transaction, member growth rate
- [ ] Report generation completes within 30 seconds for standard date ranges
- [ ] System maintains report generation history

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed
- Performance tested

**Related Use Case:** UC5 - Generate Financial Reports  
**Related Classes:** ReportingController, TransactionRepository, AggregationService, ChartService

---

### Story FR5.2: Export Reports in Multiple Formats
**ID:** FR5.2  
**Epic:** Financial Reporting  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Manager  
**I want to** export reports in multiple formats (PDF, Excel, CSV)  
**So that** I can share reports with stakeholders and perform further analysis

**Acceptance Criteria:**
- [ ] System supports export formats: PDF, Excel (XLSX), CSV
- [ ] System maintains formatting in exported documents
- [ ] System includes all report data and charts in export
- [ ] System adds metadata: report type, date generated, export date, export format
- [ ] System allows scheduled/automated exports
- [ ] Exported files are saved to system with unique naming
- [ ] System allows email export directly to recipients
- [ ] Export completes within 10 seconds for standard reports
- [ ] System maintains export history for audit purposes

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed
- Integration with export libraries verified

**Related Use Case:** UC5 - Generate Financial Reports (exportReport)  
**Related Classes:** ExportService, ReportFormatter

---

### Story FR5.3: View Real-Time Financial Dashboard
**ID:** FR5.3  
**Epic:** Financial Reporting  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 6

**As a** Manager or Owner  
**I want to** view real-time financial dashboard with key metrics  
**So that** I can monitor financial health at a glance

**Acceptance Criteria:**
- [ ] System displays dashboard on login for authorized users
- [ ] Dashboard shows key metrics: total revenue (today, this month, this year), number of active members
- [ ] Dashboard displays membership distribution by plan type
- [ ] Dashboard shows payment status: collected, pending, overdue
- [ ] Dashboard includes revenue trends chart (last 12 months)
- [ ] Dashboard shows member acquisition trends
- [ ] Dashboard displays late collections alerts
- [ ] Dashboard is refreshed automatically (every 5 minutes)
- [ ] Dashboard allows drill-down to detailed reports
- [ ] Dashboard is responsive and works on mobile devices

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed
- Performance tested for responsiveness

**Related Use Case:** UC7 - Monitor Gym Operations (Real-Time Data)

---

## Epic 6: Member Access Control

### Story AC6.1: Check In Member at Facility
**ID:** AC6.1  
**Epic:** Member Access Control  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** Reception Staff  
**I want to** manually record member check-in at the gym facility  
**So that** I can maintain accurate attendance records and manage facility occupancy

**Acceptance Criteria:**
- [ ] System provides check-in interface for reception staff
- [ ] Reception staff can search member by ID, name, or member card details
- [ ] System displays member information and current status
- [ ] System verifies member ID and validates active status
- [ ] System checks membership expiration date
- [ ] System checks if member is suspended or frozen
- [ ] System allows check-in only if membership is active and not expired
- [ ] Reception staff clicks "Check In" button to record check-in
- [ ] System records check-in time with timestamp
- [ ] System updates occupancy counter
- [ ] System displays welcome message with member name
- [ ] System handles failed check-in attempts gracefully
- [ ] System logs all check-in attempts for audit purposes
- [ ] Check-in process completes within 3 seconds

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC6 - Track Member Access (presentIdentification)  
**Related Classes:** AccessController, MemberRepository, AccessPolicyService, AttendanceRepository

---

### Story AC6.2: Check Out Member from Facility
**ID:** AC6.2  
**Epic:** Member Access Control  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 5

**As a** Reception Staff  
**I want to** manually record member check-out from the facility  
**So that** I can calculate session duration and update occupancy

**Acceptance Criteria:**
- [ ] System provides check-out interface for reception staff
- [ ] Reception staff can search member by ID, name, or member card details
- [ ] System retrieves corresponding check-in record for the member
- [ ] System displays current session information (check-in time, duration so far)
- [ ] Reception staff clicks "Check Out" button to record check-out
- [ ] System records check-out time with timestamp
- [ ] System calculates session duration
- [ ] System updates occupancy counter (decreases)
- [ ] System displays session summary (duration, time spent)
- [ ] System logs check-out event for audit purposes
- [ ] System handles edge cases (no corresponding check-in, multiple check-ins)
- [ ] Check-out process completes within 3 seconds

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC6 - Track Member Access (exitFacility)  
**Related Classes:** AccessController, AttendanceRepository

---

### Story AC6.3: Monitor Real-Time Facility Occupancy
**ID:** AC6.3  
**Epic:** Member Access Control  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 6

**As a** Manager or Staff  
**I want to** view real-time occupancy status of the facility  
**So that** I can manage capacity and ensure safety compliance

**Acceptance Criteria:**
- [ ] System displays current occupancy count
- [ ] System shows occupancy as percentage of facility capacity
- [ ] System displays occupancy trend chart (last 24 hours)
- [ ] System identifies peak hours
- [ ] System alerts when occupancy exceeds 90% of capacity
- [ ] System allows override of capacity limit by managers (with logging)
- [ ] System breaks down occupancy by zone/area
- [ ] Occupancy data updates in real-time (every 10 seconds)
- [ ] System maintains occupancy history for analytics

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed
- Real-time update verified

**Related Use Case:** UC7 - Monitor Gym Operations  
**Related Classes:** OccupancyService, AttendanceRecord

---

### Story AC6.4: View Member Attendance Records
**ID:** AC6.4  
**Epic:** Member Access Control  
**Priority:** Medium  
**Complexity:** Low  
**Story Points:** 4

**As a** Member or Manager  
**I want to** view member attendance records and usage patterns  
**So that** I can track fitness commitment and identify trends

**Acceptance Criteria:**
- [ ] System displays member check-in history with dates and times
- [ ] System shows session duration for each visit
- [ ] System calculates average visits per week/month
- [ ] System allows filtering by date range
- [ ] System displays attendance trends (graphical representation)
- [ ] System identifies peak usage times for member
- [ ] System calculates member utilization rate
- [ ] System exports attendance history (CSV, PDF)
- [ ] Members can view their own attendance (online portal)
- [ ] Managers can view any member's attendance

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC6 - Track Member Access

---

## Epic 7: Operations Monitoring

### Story OM7.2: Generate Operational Reports
**ID:** OM7.2  
**Epic:** Operations Monitoring  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Manager  
**I want to** generate detailed operational reports  
**So that** I can analyze facility performance and identify improvement areas

**Acceptance Criteria:**
- [ ] System generates daily operational reports automatically
- [ ] Report includes: total visits, peak hours, average session duration
- [ ] Report includes equipment utilization rates by zone
- [ ] Report includes staff attendance and coverage metrics
- [ ] Report displays comparison to previous periods (daily, weekly, monthly)
- [ ] Report identifies trends and anomalies
- [ ] Report includes alerts for unusual patterns
- [ ] Managers can generate custom reports with selected parameters
- [ ] Reports are exportable in PDF and Excel formats
- [ ] Report generation completes within 30 seconds

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed
- Performance tested

**Related Use Case:** UC7 - Monitor Gym Operations (generateDailyReport)  
**Related Classes:** OperationsReportService, TrendAnalysisService

---

## Epic 8: Trainer Management

### Story TM8.1: Register New Trainer
**ID:** TM8.1  
**Epic:** Trainer Management  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** HR Manager  
**I want to** register new trainers with their qualifications and specializations  
**So that** I can maintain accurate trainer records and manage training operations

**Acceptance Criteria:**
- [ ] System displays trainer registration form
- [ ] Form collects: name, contact info, address, specialization areas
- [ ] System requires certification information and documents
- [ ] System validates certifications against issuing bodies
- [ ] System allows upload of certification documents (PDF, images)
- [ ] System stores documents securely in document repository
- [ ] System validates all trainer information before saving
- [ ] System generates unique trainer ID
- [ ] System creates trainer schedule and availability calendar
- [ ] System creates trainer account with credentials
- [ ] System sends welcome email with credentials
- [ ] System configures trainer payment/commission settings
- [ ] Registration process can be completed within 15 minutes

**Definition of Done:**
- Code peer reviewed and approved
- Security review completed
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC8 - Manage Trainer Operations (registerTrainer)  
**Related Classes:** TrainerController, TrainerRepository, CertificationValidationService, DocumentStorage

---

### Story TM8.2: Manage Trainer Schedules and Availability
**ID:** TM8.2  
**Epic:** Trainer Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** HR Manager or Trainer  
**I want to** manage trainer availability and schedules  
**So that** I can coordinate training sessions and resource allocation

**Acceptance Criteria:**
- [ ] System displays trainer availability calendar
- [ ] Trainers can set availability: days, times, zones
- [ ] Trainers can mark time off (vacation, sick leave)
- [ ] System prevents scheduling during unavailable times
- [ ] System allows bulk availability updates
- [ ] System supports recurring availability patterns
- [ ] System displays trainer workload (sessions per day/week)
- [ ] System alerts when trainer exceeds maximum sessions per day
- [ ] Managers can override trainer availability (with logging)
- [ ] System supports seasonal schedule variations

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC8 - Manage Trainer Operations (step 5)  
**Related Classes:** ScheduleService, Trainer

---

## Epic 9: Training Sessions

### Story TS9.1: Schedule Personal Training Sessions
**ID:** TS9.1  
**Epic:** Training Sessions  
**Priority:** High  
**Complexity:** High  
**Story Points:** 8

**As a** Staff Member  
**I want to** schedule personal training sessions between members and trainers  
**So that** I can coordinate training activities and manage sessions

**Acceptance Criteria:**
- [ ] System displays session scheduling interface
- [ ] Staff selects member ID (with member validation)
- [ ] Staff selects trainer and session date/time
- [ ] System checks trainer availability against schedule
- [ ] System checks member eligibility for training sessions
- [ ] System prevents scheduling conflicts
- [ ] Staff specifies session type (personal, group) and duration
- [ ] System calculates session price based on trainer and duration
- [ ] System processes payment before confirming booking
- [ ] System generates confirmation for member and trainer
- [ ] System sends email/SMS notifications to both parties
- [ ] Scheduling process completes within 10 minutes
- [ ] System maintains session history and cancellation tracking

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC9 - Schedule Personal Training Sessions (createSession)  
**Related Classes:** SchedulingController, SessionRepository, AvailabilityService, PricingService, PaymentService

---

### Story TS9.2: Manage Training Session Packages
**ID:** TS9.2  
**Epic:** Training Sessions  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 6

**As a** Manager  
**I want to** create and manage training session packages  
**So that** I can offer flexible training options and encourage member commitment

**Acceptance Criteria:**
- [ ] System allows creation of session packages (e.g., 10 sessions, 20 sessions)
- [ ] Manager specifies: package name, total sessions, price, validity period
- [ ] System calculates price per session and displays savings
- [ ] System tracks remaining sessions for each member package
- [ ] System allows refund of unused sessions (based on policy)
- [ ] System alerts member when sessions are expiring
- [ ] System prevents expiration without member notification
- [ ] System allows package rollover to next period (with policy constraints)
- [ ] Members can purchase packages online or through staff

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC9 - Schedule Personal Training Sessions  
**Related Classes:** SessionPackage, SchedulingController

---

### Story TS9.3: Track Training Session Attendance
**ID:** TS9.3  
**Epic:** Training Sessions  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Trainer or Staff  
**I want to** record training session attendance and mark completion  
**So that** I can track member progress and account for session usage

**Acceptance Criteria:**
- [ ] System displays scheduled sessions for the day
- [ ] Trainer can mark attendance and record session notes
- [ ] System tracks session completion time and duration
- [ ] System deducts session from member package balance
- [ ] System records no-show/cancellation with reason
- [ ] System updates attendance records
- [ ] System generates attendance report
- [ ] Trainers can record session performance notes
- [ ] System maintains session history for member progress tracking

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC9 - Schedule Personal Training Sessions (tracking)

---

## Epic 10: Equipment Management

### Story EM10.1: Add Equipment to Inventory
**ID:** EM10.1  
**Epic:** Equipment Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** Facilities Manager  
**I want to** add new equipment to the gym inventory  
**So that** I can maintain accurate equipment records and track assets

**Acceptance Criteria:**
- [ ] System displays equipment entry form
- [ ] Form collects: name, type, model, manufacturer, serial number
- [ ] System assigns unique equipment ID
- [ ] Manager selects equipment category and gym zone
- [ ] Manager sets initial condition status
- [ ] System allows upload of equipment photos and manuals
- [ ] Manager enters purchase date and warranty information
- [ ] Manager sets maintenance schedule
- [ ] System validates all information before saving
- [ ] System generates equipment QR code for tracking
- [ ] System displays confirmation with equipment ID
- [ ] Equipment can be added within 5 minutes

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC10 - Manage Equipment Inventory (addEquipment)  
**Related Classes:** InventoryController, EquipmentRepository, Equipment

---

### Story EM10.2: Track Equipment Maintenance
**ID:** EM10.2  
**Epic:** Equipment Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** Facilities Manager  
**I want to** schedule and track equipment maintenance  
**So that** I can ensure equipment is properly maintained and safe for use

**Acceptance Criteria:**
- [ ] System displays maintenance schedule for all equipment
- [ ] System alerts when maintenance is due (based on schedule)
- [ ] Manager can create maintenance request with description
- [ ] System tracks maintenance history for each equipment
- [ ] System records maintenance completion date and technician
- [ ] System allows maintenance work notes and observations
- [ ] System marks equipment as out-of-service during maintenance
- [ ] System generates maintenance report and schedule
- [ ] System calculates maintenance costs
- [ ] System integrates with warranty information
- [ ] System tracks warranty claims

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Manual testing completed

**Related Use Case:** UC10 - Manage Equipment Inventory  
**Related Classes:** MaintenanceRequest, InventoryController, Equipment

---

## Epic 11: Overdue Account Management

### Story OA11.1: Identify and Track Overdue Accounts
**ID:** OA11.1  
**Epic:** Overdue Account Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 6

**As a** Accounting Staff  
**I want to** identify members with overdue payments  
**So that** I can initiate collection processes

**Acceptance Criteria:**
- [ ] System calculates days overdue for each outstanding obligation
- [ ] System generates prioritized list of overdue accounts
- [ ] System sorts by: days overdue, amount due, member status
- [ ] System displays overdue amount and payment history
- [ ] System identifies repeat offenders
- [ ] System tracks overdue account status
- [ ] System sends automated alerts for highly overdue accounts (30, 60, 90+ days)
- [ ] System prevents high-overdue members from further privileges
- [ ] System maintains overdue tracking history

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC12 - Process Overdue Accounts  
**Related Classes:** AccountsReceivableController, LateFee

---

### Story OA11.2: Generate and Send Payment Reminders
**ID:** OA11.2  
**Epic:** Overdue Account Management  
**Priority:** High  
**Complexity:** Medium  
**Story Points:** 5

**As a** Accounting Staff  
**I want to** generate and send payment reminders to members with overdue balances  
**So that** I can encourage payment and reduce collection issues

**Acceptance Criteria:**
- [ ] System generates reminder notice with details: amount, due date, days overdue
- [ ] System supports multiple reminder levels: first, second, final notice
- [ ] System calculates and displays late fees in reminder
- [ ] System displays payment options and methods
- [ ] Staff can send reminders via email, SMS, or postal mail
- [ ] System tracks reminder delivery status
- [ ] System prevents duplicate reminders on same day
- [ ] System records member contact date and communication method
- [ ] System displays contact history for member
- [ ] System generates reminder batch reports

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC12 - Process Overdue Accounts (generateAndSendReminder)  
**Related Classes:** NotificationService, OverdueLogRepository

---

### Story OA11.3: Suspend Member Access for Non-Payment
**ID:** OA11.3  
**Epic:** Overdue Account Management  
**Priority:** Medium  
**Complexity:** Medium  
**Story Points:** 5

**As a** Manager  
**I want to** suspend member access for excessive non-payment  
**So that** I can encourage payment and protect facility interests

**Acceptance Criteria:**
- [ ] System identifies members exceeding overdue threshold (e.g., 30+ days or $100+)
- [ ] System automatically suspends access after configured threshold
- [ ] System displays suspension reason and outstanding balance at check-in
- [ ] System prevents suspended members from booking training sessions
- [ ] System sends notification to member about suspension
- [ ] System displays payment options and required amount
- [ ] System reactivates access upon payment
- [ ] System logs all suspension/reactivation events
- [ ] Manager can manually override suspension with justification
- [ ] System prevents abuse of override functionality

**Definition of Done:**
- Code peer reviewed and approved
- Unit tests achieve 90%+ coverage
- Integration tests pass
- Manual testing completed

**Related Use Case:** UC12 - Process Overdue Accounts (suspendMemberAccess)

---


### Epic Priority and Release Planning

| Epic | Priority | Target Release | Total Story Points |
|------|----------|-----------------|-------------------|
| Member Management | Critical | MVP | 20 |
| Member Enrollment | Critical | MVP | 19 |
| Payment Processing | Critical | MVP | 13 |
| Member Access Control | Critical | MVP | 17 |
| Membership Renewals | High | MVP + 1 Month | 25 |
| Financial Reporting | High | MVP + 1 Month | 19 |
| Operations Monitoring | High | MVP + 2 Months | 8 |
| Trainer Management | High | Release 2 | 14 |
| Training Sessions | High | Release 2 | 19 |
| Equipment Management | High | Release 2 | 12 |
| Overdue Account Management | High | MVP + 1 Month | 11 |
| System Configuration | Critical | MVP | 34 |

---

## Notes for Development Team

1. **MVP Scope:** Focus on Member Management, Enrollment, Payment Processing, and System Configuration for initial release
2. **Dependencies:** Many stories have dependencies on underlying infrastructure (database, payment gateway, notification system)
3. **Non-Functional Requirements:** All stories require performance, security, and data validation testing
4. **Acceptance Criteria:** Should be refined during sprint planning with specific test cases
5. **Technical Debt:** Review and refactor common patterns after MVP delivery
6. **Future Considerations:** Mobile app development, API exposure, advanced analytics, AI-based recommendations

---
