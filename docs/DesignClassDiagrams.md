# Block20 Gym Management System — Design Class Diagrams (GRASP-driven)

---

## UC1: Manage Member Profiles

### enterMemberDetails

**GRASP Patterns Applied**:
- **Controller**: MemberController handles system operation
- **Information Expert**: ValidationService validates data; DuplicateCheckService checks duplicates
- **Creator**: MemberRepository creates and stores Member (contains/records members)
- **Pure Fabrication**: ValidationService, DuplicateCheckService, IDGenerationService, MemberCardService, AuditService

```mermaid
classDiagram
    class MemberController {
        -memberRepo: MemberRepository
        -validationService: ValidationService
        -duplicateCheck: DuplicateCheckService
        -idGenerator: IDGenerationService
        -cardService: MemberCardService
        -auditService: AuditService
        +enterMemberDetails(name, contact, emergency) String
    }
    
    class MemberRepository {
        -members: List~Member~
        +save(member: Member) void
        +findByContact(contact: String) Member
        +create(id, name, contact, emergency) Member
    }
    
    class Member {
        -memberID: String
        -name: String
        -contactInfo: String
        -emergencyContact: String
        +getMemberID() String
    }
    
    class ValidationService {
        +validateMemberInfo(name, contact, emergency) boolean
    }
    
    class DuplicateCheckService {
        +checkDuplicate(contact: String, repo: MemberRepository) boolean
    }
    
    class IDGenerationService {
        +generateUniqueMemberID() String
    }
    
    class MemberCardService {
        +generateMemberCard(memberID: String) void
    }
    
    class AuditService {
        +logMemberCreation(memberID: String) void
    }
    
    MemberController "1" --> "1" MemberRepository : uses
    MemberController "1" --> "1" ValidationService : uses
    MemberController "1" --> "1" DuplicateCheckService : uses
    MemberController "1" --> "1" IDGenerationService : uses
    MemberController "1" --> "1" MemberCardService : uses
    MemberController "1" --> "1" AuditService : uses
    MemberRepository "1" --> "*" Member : creates/stores
```

### selectManageMembers

```mermaid
classDiagram
    class MemberController {
        +selectManageMembers() void
    }
```

### chooseCreateNewMember

```mermaid
classDiagram
    class MemberController {
        +chooseCreateNewMember() void
    }
```


---

## UC2: Process Member Enrollment

### activateMembership

**GRASP Patterns Applied**:
- **Controller**: EnrollmentController handles system operation
- **Information Expert**: MemberRepository retrieves member; PlanRepository retrieves plan; PricingService calculates fees
- **Creator**: EnrollmentRepository creates Enrollment; CredentialsService creates credentials
- **Pure Fabrication**: PricingService, PaymentService, CredentialsService, WelcomeService

```mermaid
classDiagram
    class EnrollmentController {
        -memberRepo: MemberRepository
        -planRepo: PlanRepository
        -pricingService: PricingService
        -paymentService: PaymentService
        -enrollmentRepo: EnrollmentRepository
        -credentialsService: CredentialsService
        -welcomeService: WelcomeService
        +activateMembership(memberID, planID) String
    }
    
    class MemberRepository {
        +findById(memberID: String) Member
        +activate(member: Member) void
    }
    
    class Member {
        -memberID: String
        -status: String
        +activate() void
    }
    
    class PlanRepository {
        +findById(planID: String) MembershipPlan
    }
    
    class MembershipPlan {
        -planID: String
        -name: String
        -basePrice: Money
        +getBasePrice() Money
    }
    
    class PricingService {
        +calculateEnrollmentFees(plan: MembershipPlan) Money
    }
    
    class PaymentService {
        -gateway: IPaymentGateway
        +processPayment(amount: Money, method: String) boolean
    }
    
    class IPaymentGateway {
        <<interface>>
        +charge(amount: Money) boolean
    }
    
    class EnrollmentRepository {
        +create(memberID, planID, paymentID) Enrollment
    }
    
    class Enrollment {
        -enrollmentID: String
        -memberID: String
        -planID: String
        -paymentID: String
        -enrollmentDate: Date
    }
    
    class CredentialsService {
        +generateCredentials(memberID: String) Credentials
    }
    
    class Credentials {
        -memberID: String
        -username: String
        -tempPassword: String
    }
    
    class WelcomeService {
        +sendWelcomePackage(memberID: String) void
    }
    
    EnrollmentController "1" --> "1" MemberRepository : uses
    EnrollmentController "1" --> "1" PlanRepository : uses
    EnrollmentController "1" --> "1" PricingService : uses
    EnrollmentController "1" --> "1" PaymentService : uses
    EnrollmentController "1" --> "1" EnrollmentRepository : uses
    EnrollmentController "1" --> "1" CredentialsService : uses
    EnrollmentController "1" --> "1" WelcomeService : uses
    MemberRepository "1" --> "*" Member : retrieves
    PlanRepository "1" --> "*" MembershipPlan : retrieves
    PaymentService "1" --> "1" IPaymentGateway : uses
    EnrollmentRepository "1" --> "*" Enrollment : creates
    CredentialsService "1" --> "*" Credentials : creates
```

### generateMembershipAgreement

```mermaid
classDiagram
    class EnrollmentController {
        -agreementService: AgreementService
        +generateMembershipAgreement(memberID, planID) Agreement
    }
    
    class AgreementService {
        +generate(memberID, planID) Agreement
    }
    
    class Agreement {
        -agreementID: String
        -memberID: String
        -planID: String
        -terms: String
    }
    
    EnrollmentController "1" --> "1" AgreementService : uses
    AgreementService "1" --> "*" Agreement : creates
```

### submitSignedAgreement

```mermaid
classDiagram
    class EnrollmentController {
        -agreementRepo: AgreementRepository
        +submitSignedAgreement(agreementID, signature) boolean
    }
    
    class AgreementRepository {
        +storeSignedAgreement(agreementID, signature) void
    }
    
    EnrollmentController "1" --> "1" AgreementRepository : uses
```

### enterPaymentInfo

*See UC4 processPayment for detailed payment flow*

```mermaid
classDiagram
    class EnrollmentController {
        -paymentService: PaymentService
        +enterPaymentInfo(amount, method, details) boolean
    }
    
    class PaymentService {
        +processPayment(amount, method) boolean
    }
    
    EnrollmentController "1" --> "1" PaymentService : delegates to
```


---

## UC3: Handle Membership Renewals

### processRenewal

**GRASP Patterns Applied**:
- **Controller**: RenewalController handles system operation
- **Information Expert**: MemberRepository finds member; PricingService calculates renewal fees
- **Creator**: RenewalRepository creates Renewal
- **Pure Fabrication**: PricingService, PaymentService, CardService, NotificationService

```mermaid
classDiagram
    class RenewalController {
        -memberRepo: MemberRepository
        -pricingService: PricingService
        -paymentService: PaymentService
        -renewalRepo: RenewalRepository
        -cardService: CardService
        -notificationService: NotificationService
        +retrieveMemberAccount(memberID) Member
        +processRenewal(memberID, planChange) String
    }
    
    class MemberRepository {
        +findById(memberID: String) Member
        +extendValidity(member: Member, days: int) void
    }
    
    class Member {
        -memberID: String
        -expiryDate: Date
        -planID: String
        +extendMembership(days: int) void
        +changePlan(newPlanID: String) void
    }
    
    class PricingService {
        +calculateRenewalFees(memberID: String, planChange: PlanChange) Money
    }
    
    class PaymentService {
        -gateway: IPaymentGateway
        +processPayment(amount: Money, method: String) boolean
    }
    
    class IPaymentGateway {
        <<interface>>
        +charge(amount: Money) boolean
    }
    
    class RenewalRepository {
        +create(memberID, amount, paymentID) Renewal
    }
    
    class Renewal {
        -renewalID: String
        -memberID: String
        -amount: Money
        -paymentID: String
        -renewalDate: Date
    }
    
    class CardService {
        +generateUpdatedCard(memberID: String) Card
    }
    
    class Card {
        -cardID: String
        -memberID: String
        -issueDate: Date
        -expiryDate: Date
    }
    
    class NotificationService {
        +sendRenewalConfirmation(memberID: String) void
    }
    
    RenewalController "1" --> "1" MemberRepository : uses
    RenewalController "1" --> "1" PricingService : uses
    RenewalController "1" --> "1" PaymentService : uses
    RenewalController "1" --> "1" RenewalRepository : uses
    RenewalController "1" --> "1" CardService : uses
    RenewalController "1" --> "1" NotificationService : uses
    MemberRepository "1" --> "*" Member : retrieves
    PaymentService "1" --> "1" IPaymentGateway : uses
    RenewalRepository "1" --> "*" Renewal : creates
    CardService "1" --> "*" Card : creates
```

---

## UC4: Process Member Payments

### processPayment

**GRASP Patterns Applied**:
- **Controller**: PaymentController handles system operation
- **Information Expert**: MemberRepository finds member; BillingItemService retrieves outstanding items
- **Creator**: TransactionRepository creates transaction records
- **Pure Fabrication**: BillingItemService, DiscountService, TotalsService, PaymentService, NotificationService
- **Indirection/Protected Variations**: IPaymentGateway interface

```mermaid
classDiagram
    class PaymentController {
        -memberRepo: MemberRepository
        -billingService: BillingItemService
        -discountService: DiscountService
        -totalsService: TotalsService
        -paymentService: PaymentService
        -transactionRepo: TransactionRepository
        -notificationService: NotificationService
        +enterMemberID(memberID) Member
        +processPayment(memberID, method, details) PaymentConfirmation
    }
    
    class MemberRepository {
        +findById(memberID: String) Member
    }
    
    class Member {
        -memberID: String
        -name: String
        -balance: Money
    }
    
    class BillingItemService {
        +listOutstanding(memberID: String) PaymentItem[]
    }
    
    class PaymentItem {
        -itemID: String
        -description: String
        -amount: Money
    }
    
    class DiscountService {
        +applyEligible(items: PaymentItem[], memberID: String) PaymentItem[]
    }
    
    class TotalsService {
        +compute(items: PaymentItem[]) Money
    }
    
    class PaymentService {
        -gateway: IPaymentGateway
        +process(method: String, details: PaymentDetails, amount: Money) PaymentConfirmation
    }
    
    class IPaymentGateway {
        <<interface>>
        +charge(details: PaymentDetails, amount: Money) Transaction
    }
    
    class PaymentConfirmation {
        -confirmationID: String
        -transactionID: String
        -amount: Money
        -status: String
    }
    
    class TransactionRepository {
        +record(memberID, items, amount, txnID) void
    }
    
    class NotificationService {
        +sendReceipt(memberID, txnID) void
    }
    
    PaymentController "1" --> "1" MemberRepository : uses
    PaymentController "1" --> "1" BillingItemService : uses
    PaymentController "1" --> "1" DiscountService : uses
    PaymentController "1" --> "1" TotalsService : uses
    PaymentController "1" --> "1" PaymentService : uses
    PaymentController "1" --> "1" TransactionRepository : uses
    PaymentController "1" --> "1" NotificationService : uses
    MemberRepository "1" --> "*" Member : retrieves
    BillingItemService "1" --> "*" PaymentItem : retrieves
    PaymentService "1" --> "1" IPaymentGateway : uses
```

---

## UC5: Generate Financial Reports

### generateReport

**GRASP Patterns Applied**:
- **Controller**: ReportingController handles system operation
- **Information Expert**: TransactionRepository retrieves transaction data
- **Creator**: ReportFormatter creates FinancialReport
- **Pure Fabrication**: AggregationService, ChartService, ReportFormatter
- **High Cohesion**: Each service has single focused responsibility

```mermaid
classDiagram
    class ReportingController {
        -transactionRepo: TransactionRepository
        -aggregationService: AggregationService
        -chartService: ChartService
        -reportFormatter: ReportFormatter
        +generateReport(type, range, params) FinancialReport
    }
    
    class TransactionRepository {
        +getTransactions(range: DateRange, params: Map) Transaction[]
    }
    
    class Transaction {
        -transactionID: String
        -memberID: String
        -amount: Money
        -type: String
        -timestamp: DateTime
    }
    
    class AggregationService {
        +compileMetrics(transactions: Transaction[], type: ReportType) Metrics
    }
    
    class Metrics {
        -totalRevenue: Money
        -memberCount: int
        -averageTransaction: Money
        -data: Map
    }
    
    class ChartService {
        +buildVisuals(metrics: Metrics) Charts
    }
    
    class Charts {
        -chartData: List
        -visualizations: List
    }
    
    class ReportFormatter {
        +formatReport(metrics: Metrics, visuals: Charts, type: ReportType) FinancialReport
    }
    
    class FinancialReport {
        -reportID: String
        -reportType: String
        -metrics: Metrics
        -charts: Charts
        -generatedDate: DateTime
    }
    
    ReportingController "1" --> "1" TransactionRepository : uses
    ReportingController "1" --> "1" AggregationService : uses
    ReportingController "1" --> "1" ChartService : uses
    ReportingController "1" --> "1" ReportFormatter : uses
    TransactionRepository "1" --> "*" Transaction : retrieves
    AggregationService "1" --> "1" Metrics : creates
    ChartService "1" --> "1" Charts : creates
    ReportFormatter "1" --> "*" FinancialReport : creates
```

### exportReport

```mermaid
classDiagram
    class ReportingController {
        -exportService: ExportService
        +exportReport(report: FinancialReport, format) File
    }
    
    class ExportService {
        +export(report: FinancialReport, format: ExportFormat) File
    }
    
    class File {
        -filename: String
        -content: byte[]
        -mimeType: String
    }
    
    ReportingController "1" --> "1" ExportService : uses
    ExportService "1" --> "*" File : creates
```

---

## UC6: Track Member Access

### presentIdentification / exitFacility

**GRASP Patterns Applied**:
- **Controller**: AccessController handles system operations
- **Information Expert**: MemberRepository finds member; AccessPolicyService verifies access
- **Creator**: AttendanceRepository creates/updates AttendanceRecord
- **Pure Fabrication**: AccessPolicyService, OccupancyService
- **Indirection**: AccessControlProxy for hardware

```mermaid
classDiagram
    class AccessController {
        -memberRepo: MemberRepository
        -accessPolicyService: AccessPolicyService
        -attendanceRepo: AttendanceRepository
        -accessControlProxy: AccessControlProxy
        -occupancyService: OccupancyService
        +presentIdentification(memberID) AccessDecision
        +exitFacility(memberID) void
    }
    
    class MemberRepository {
        +findById(memberID: String) Member
    }
    
    class Member {
        -memberID: String
        -status: String
        -expiryDate: Date
    }
    
    class AccessPolicyService {
        +verifyAccess(member: Member) boolean
    }
    
    class AttendanceRepository {
        +recordEntry(memberID: String) AttendanceRecord
        +recordExit(memberID: String) void
        +findActiveSession(memberID: String) AttendanceRecord
    }
    
    class AttendanceRecord {
        -recordID: String
        -memberID: String
        -entryTime: DateTime
        -exitTime: DateTime
    }
    
    class AccessControlProxy {
        +unlock() void
    }
    
    class OccupancyService {
        +increment() int
        +decrement() int
        +getCurrentOccupancy() int
    }
    
    class AccessDecision {
        -allowed: boolean
        -reason: String
    }
    
    AccessController "1" --> "1" MemberRepository : uses
    AccessController "1" --> "1" AccessPolicyService : uses
    AccessController "1" --> "1" AttendanceRepository : uses
    AccessController "1" --> "1" AccessControlProxy : uses
    AccessController "1" --> "1" OccupancyService : uses
    MemberRepository "1" --> "*" Member : retrieves
    AttendanceRepository "1" --> "*" AttendanceRecord : manages
```

---

## UC7: Monitor Gym Operations

### retrieveRealTimeData / generateDailyReport / metrics

**GRASP Patterns Applied**:
- **Controller**: OperationsController handles system operations
- **Information Expert**: Various services provide specialized operational data
- **Pure Fabrication**: OccupancyService, EquipmentUtilizationService, StaffAttendanceService, TrendAnalysisService, MetricsService, OperationsReportService
- **High Cohesion**: Each service handles a specific operational aspect

```mermaid
classDiagram
    class OperationsController {
        -occupancyService: OccupancyService
        -equipmentService: EquipmentUtilizationService
        -staffService: StaffAttendanceService
        -trendService: TrendAnalysisService
        -metricsService: MetricsService
        -reportService: OperationsReportService
        +retrieveRealTimeData() OpsSnapshot
        +generateDailyReport() OperationalReport
        +retrievePeakUsageHours() PeakHours
        +retrieveHourlyTrends() Trends
        +retrieveUsagePatterns(period) UsagePatterns
        +retrieveEquipmentZoneUtilization() ZoneUtilization
        +retrieveStaffAttendance() StaffCoverage
        +retrieveOperationalMetrics() Metrics
    }
    
    class OccupancyService {
        +getCurrentOccupancy() Occupancy
    }
    
    class Occupancy {
        -current: int
        -capacity: int
        -percentage: float
    }
    
    class EquipmentUtilizationService {
        +getZoneUtilization() ZoneUtilization
    }
    
    class ZoneUtilization {
        -zoneData: Map
        -timestamp: DateTime
    }
    
    class StaffAttendanceService {
        +getCoverage() StaffCoverage
    }
    
    class StaffCoverage {
        -onDuty: int
        -scheduled: int
        -coverage: float
    }
    
    class TrendAnalysisService {
        +getPeakHours() PeakHours
        +getHourlyVisitTrends() Trends
        +getUsagePatterns(period: Period) UsagePatterns
    }
    
    class PeakHours {
        -hours: List
        -data: Map
    }
    
    class Trends {
        -hourlyData: Map
    }
    
    class UsagePatterns {
        -patterns: Map
        -period: Period
    }
    
    class MetricsService {
        +getKeyMetrics() Metrics
    }
    
    class Metrics {
        -data: Map
    }
    
    class OperationsReportService {
        +compileDaily() OperationalReport
    }
    
    class OperationalReport {
        -reportID: String
        -date: Date
        -metrics: Metrics
        -summary: String
    }
    
    class OpsSnapshot {
        -occupancy: Occupancy
        -zones: ZoneUtilization
        -staff: StaffCoverage
        -timestamp: DateTime
    }
    
    OperationsController "1" --> "1" OccupancyService : uses
    OperationsController "1" --> "1" EquipmentUtilizationService : uses
    OperationsController "1" --> "1" StaffAttendanceService : uses
    OperationsController "1" --> "1" TrendAnalysisService : uses
    OperationsController "1" --> "1" MetricsService : uses
    OperationsController "1" --> "1" OperationsReportService : uses
```

---

## UC8: Manage Trainer Operations

### registerTrainer

**GRASP Patterns Applied**:
- **Controller**: TrainerController handles system operation
- **Information Expert**: CertificationValidationService validates certifications
- **Creator**: TrainerRepository creates Trainer; AuthService creates Credentials
- **Pure Fabrication**: CertificationValidationService, DocumentStorage, ScheduleService, AuthService, NotificationService

```mermaid
classDiagram
    class TrainerController {
        -certValidationService: CertificationValidationService
        -documentStorage: DocumentStorage
        -trainerRepo: TrainerRepository
        -scheduleService: ScheduleService
        -authService: AuthService
        -notificationService: NotificationService
        +initiateTrainerRegistration() FormSpec
        +enterPersonalInfo(name, contact, address) void
        +enterProfessionalInfo(certs, specs) void
        +registerTrainer(data) Trainer
    }
    
    class CertificationValidationService {
        +validate(certs: Certification[]) ValidationResults
    }
    
    class Certification {
        -certID: String
        -name: String
        -issuer: String
        -expiryDate: Date
    }
    
    class ValidationResults {
        -valid: boolean
        -messages: List
    }
    
    class DocumentStorage {
        +store(docs: Document[]) DocRef[]
    }
    
    class DocRef {
        -refID: String
        -url: String
    }
    
    class TrainerRepository {
        +saveProfile(name, contact, address, specs, certRefs) Trainer
    }
    
    class Trainer {
        -trainerID: String
        -name: String
        -contact: String
        -employmentStatus: String
    }
    
    class ScheduleService {
        +setAvailability(trainerID: String, availability: Slots) void
    }
    
    class AuthService {
        +createAccount(trainerID: String) Credentials
    }
    
    class Credentials {
        -username: String
        -tempPassword: String
    }
    
    class NotificationService {
        +sendWelcome(trainerID: String, credentials: Credentials) void
    }
    
    TrainerController "1" --> "1" CertificationValidationService : uses
    TrainerController "1" --> "1" DocumentStorage : uses
    TrainerController "1" --> "1" TrainerRepository : uses
    TrainerController "1" --> "1" ScheduleService : uses
    TrainerController "1" --> "1" AuthService : uses
    TrainerController "1" --> "1" NotificationService : uses
    TrainerRepository "1" --> "*" Trainer : creates
    AuthService "1" --> "*" Credentials : creates
```

---

## UC9: Schedule Personal Training Sessions

### createSession

**GRASP Patterns Applied**:
- **Controller**: SchedulingController handles system operation
- **Information Expert**: MemberRepository finds member; AvailabilityService checks trainer availability; PricingService calculates pricing
- **Creator**: SessionRepository creates TrainingSession
- **Pure Fabrication**: AvailabilityService, PricingService, PaymentService, NotificationService

```mermaid
classDiagram
    class SchedulingController {
        -memberRepo: MemberRepository
        -availabilityService: AvailabilityService
        -pricingService: PricingService
        -paymentService: PaymentService
        -sessionRepo: SessionRepository
        -notificationService: NotificationService
        +enterMemberID(memberID) Member
        +createSession(memberID, trainerID, date, time, type, duration) Booking
    }
    
    class MemberRepository {
        +findById(memberID: String) Member
    }
    
    class Member {
        -memberID: String
        -name: String
    }
    
    class AvailabilityService {
        +check(trainerID: String, date: Date, time: Time) boolean
    }
    
    class PricingService {
        +getPrice(type: String, duration: int) Money
    }
    
    class PaymentService {
        -gateway: IPaymentGateway
        +process(details: PaymentDetails, amount: Money) PaymentConfirmation
    }
    
    class IPaymentGateway {
        <<interface>>
        +charge(details: PaymentDetails, amount: Money) Transaction
    }
    
    class SessionRepository {
        +create(memberID, trainerID, date, time, type, duration, txnID) TrainingSession
    }
    
    class TrainingSession {
        -sessionID: String
        -memberID: String
        -trainerID: String
        -sessionDate: Date
        -sessionTime: Time
        -type: String
        -duration: int
        -status: String
    }
    
    class NotificationService {
        +notifyTrainer(trainerID: String, sessionID: String) void
        +notifyMember(memberID: String, sessionID: String) void
    }
    
    SchedulingController "1" --> "1" MemberRepository : uses
    SchedulingController "1" --> "1" AvailabilityService : uses
    SchedulingController "1" --> "1" PricingService : uses
    SchedulingController "1" --> "1" PaymentService : uses
    SchedulingController "1" --> "1" SessionRepository : uses
    SchedulingController "1" --> "1" NotificationService : uses
    MemberRepository "1" --> "*" Member : retrieves
    PaymentService "1" --> "1" IPaymentGateway : uses
    SessionRepository "1" --> "*" TrainingSession : creates
```

---

## UC10: Manage Equipment Inventory

### addEquipment

**GRASP Patterns Applied**:
- **Controller**: InventoryController handles system operation
- **Information Expert**: EquipmentRepository knows how to create Equipment
- **Creator**: EquipmentRepository creates Equipment; EquipmentIDGenerator creates IDs
- **Pure Fabrication**: EquipmentIDGenerator, DocumentStorage, WarrantyService, InventoryService

```mermaid
classDiagram
    class InventoryController {
        -idGenerator: EquipmentIDGenerator
        -equipmentRepo: EquipmentRepository
        -documentStorage: DocumentStorage
        -warrantyService: WarrantyService
        -inventoryService: InventoryService
        +initiateAddEquipment() FormSpec
        +enterEquipmentDetails(details) void
        +addEquipment(details) Equipment
    }
    
    class EquipmentIDGenerator {
        +generate() String
    }
    
    class EquipmentRepository {
        +create(id, name, type, model, manufacturer, serial, category, zone, condition) Equipment
        +save(equipment: Equipment, storageRefs: DocRef[]) void
    }
    
    class Equipment {
        -equipmentID: String
        -name: String
        -type: String
        -model: String
        -manufacturer: String
        -serialNumber: String
        -category: String
        -zone: String
        -condition: String
    }
    
    class DocumentStorage {
        +upload(photos: Image[], manuals: Document[]) DocRef[]
    }
    
    class DocRef {
        -refID: String
        -url: String
    }
    
    class WarrantyService {
        +record(equipmentID: String, purchaseInfo: PurchaseInfo) void
    }
    
    class InventoryService {
        +updateCounts(category: String) void
    }
    
    InventoryController "1" --> "1" EquipmentIDGenerator : uses
    InventoryController "1" --> "1" EquipmentRepository : uses
    InventoryController "1" --> "1" DocumentStorage : uses
    InventoryController "1" --> "1" WarrantyService : uses
    InventoryController "1" --> "1" InventoryService : uses
    EquipmentRepository "1" --> "*" Equipment : creates
```

---

## UC12: Process Overdue Accounts

### generateAndSendReminder

**GRASP Patterns Applied**:
- **Controller**: AccountsReceivableController handles system operation
- **Information Expert**: AccountsRepository retrieves account information; LateFeeService calculates fees
- **Creator**: OverdueLogRepository creates communication records
- **Pure Fabrication**: LateFeeService, NotificationService, OverdueLogRepository

```mermaid
classDiagram
    class AccountsReceivableController {
        -accountsRepo: AccountsRepository
        -lateFeeService: LateFeeService
        -notificationService: NotificationService
        -overdueLogRepo: OverdueLogRepository
        +selectAccount(memberID) Account
        +generateAndSendReminder(memberID, method) void
    }
    
    class AccountsRepository {
        +getAccount(memberID: String) Account
    }
    
    class Account {
        -memberID: String
        -balance: Money
        -dueDate: Date
        -status: String
    }
    
    class LateFeeService {
        +applyIfNeeded(account: Account) Account
        +calculateLateFee(daysOverdue: int) Money
    }
    
    class NotificationService {
        +composeReminder(memberID: String) Reminder
        +sendReminder(memberID: String, method: Channel) void
    }
    
    class Reminder {
        -reminderID: String
        -memberID: String
        -amount: Money
        -dueDate: Date
        -content: String
    }
    
    class OverdueLogRepository {
        +recordCommunication(memberID: String, method: Channel) void
    }
    
    AccountsReceivableController "1" --> "1" AccountsRepository : uses
    AccountsReceivableController "1" --> "1" LateFeeService : uses
    AccountsReceivableController "1" --> "1" NotificationService : uses
    AccountsReceivableController "1" --> "1" OverdueLogRepository : uses
    AccountsRepository "1" --> "*" Account : retrieves
    NotificationService "1" --> "*" Reminder : creates
```

### suspendMemberAccess

```mermaid
classDiagram
    class AccountsReceivableController {
        -memberRepo: MemberRepository
        -notificationService: NotificationService
        +suspendMemberAccess(memberID) void
    }
    
    class MemberRepository {
        +findById(memberID: String) Member
        +suspend(member: Member) void
    }
    
    class Member {
        -memberID: String
        -status: String
        +suspend() void
    }
    
    class NotificationService {
        +sendSuspensionNotice(memberID: String) void
    }
    
    AccountsReceivableController "1" --> "1" MemberRepository : uses
    AccountsReceivableController "1" --> "1" NotificationService : uses
    MemberRepository "1" --> "*" Member : manages
```

---

## UC15: Manage System Configuration

### modifySettings

**GRASP Patterns Applied**:
- **Controller**: ConfigurationController handles system operation
- **Information Expert**: ConfigRepository retrieves and persists settings
- **Creator**: ConfigRepository creates/updates Settings
- **Pure Fabrication**: ValidationEngine, AuditLogService, NotificationService
- **Protected Variations**: Settings abstraction shields from implementation changes

```mermaid
classDiagram
    class ConfigurationController {
        -configRepo: ConfigRepository
        -validationEngine: ValidationEngine
        -auditLogService: AuditLogService
        -notificationService: NotificationService
        +selectConfigurationCategory(category) Settings
        +modifySettings(settingName, newValue) void
        +confirmChanges() void
    }
    
    class ConfigRepository {
        +getCurrent(category: String) Settings
        +update(category: String, settingName: String, newValue: Object) void
    }
    
    class Settings {
        -category: String
        -settings: Map
        +getValue(key: String) Object
        +setValue(key: String, value: Object) void
    }
    
    class ValidationEngine {
        +validateChange(category: String, settingName: String, newValue: Object) ValidationResult
    }
    
    class ValidationResult {
        -valid: boolean
        -errors: List
    }
    
    class AuditLogService {
        +logChange(user: Admin, category: String, setting: String, oldValue: Object, newValue: Object) void
    }
    
    class NotificationService {
        +notifyAffectedUsers(category: String) void
    }
    
    ConfigurationController "1" --> "1" ConfigRepository : uses
    ConfigurationController "1" --> "1" ValidationEngine : uses
    ConfigurationController "1" --> "1" AuditLogService : uses
    ConfigurationController "1" --> "1" NotificationService : uses
    ConfigRepository "1" --> "*" Settings : manages
```

---


