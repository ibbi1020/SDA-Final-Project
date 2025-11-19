# Block20 Gym Management System — Detailed Sequence Diagrams (Design)

These detailed sequence diagrams are derived from the System Sequence Diagrams in `SSD.md` and follow the rules in `.github/instructions/detailed-sd-rules.md`. They show internal object collaborations for the main success scenarios. Naming uses abstract, intention-level verbs and domain vocabulary.

---

## UC1: Manage Member Profiles — enterMemberDetails

```mermaid
sequenceDiagram
    actor Admin as :Administrator
    participant MC as :MemberController
    participant Dup as :DuplicateCheckerService
    participant IDG as :MemberIDGenerator
    participant MR as :MemberRepository
    participant Card as :CardService
    participant Audit as :AuditLogService

    Admin->>MC: enterMemberDetails(name, phone, email, emergency)
    MC->>Dup: checkDuplicate(name, phone, email)
    Dup-->>MC: duplicateFound=false

    MC->>IDG: generate()
    IDG-->>MC: memberID

    MC->>MR: create(memberID, name, phone, email, emergency)
    MR-->>MC: member

    MC->>MR: save(member)
    MR-->>MC: saved

    MC->>Card: generateCard(member)
    Card-->>MC: memberCardPDF

    MC->>Audit: log("Member created", memberID)
    Audit-->>MC: logged

    MC-->>Admin: memberID, confirmation
```

---

## UC1: Manage Member Profiles — chooseCreateNewMember

```mermaid
sequenceDiagram
    actor Admin as :Administrator
    participant MC as :MemberController

    Admin->>MC: chooseCreateNewMember()
    MC-->>Admin: memberEntryForm
```

---

## UC2: Process Member Enrollment — activateMembership

```mermaid
sequenceDiagram
    actor Staff as :Staff
    participant EC as :EnrollmentController
    participant MR as :MemberRepository
    participant Plan as :MembershipPlanRepository
    participant Price as :PricingService
    participant Pay as :PaymentService
    participant PG as :PaymentGateway
    participant Cred as :CredentialService
    participant Notif as :NotificationService

    Staff->>EC: initiateEnrollment()
    EC-->>Staff: enrollmentForm

    Staff->>EC: enterMemberInfo(name, contact, emergency)
    EC->>MR: createPending(name, contact, emergency)
    MR-->>EC: pendingMember

    Staff->>EC: selectMembershipPlan(planID)
    EC->>Plan: get(planID)
    Plan-->>EC: plan

    EC->>Price: calculateFees(plan)
    Price-->>EC: fees
    EC-->>Staff: fees

    Staff->>EC: enterPaymentInfo(paymentDetails)
    EC->>Pay: process(paymentDetails, fees)
    Pay->>PG: charge(paymentDetails, fees)
    PG-->>Pay: approved(txnId)
    Pay-->>EC: paymentConfirmation

    Staff->>EC: activateMembership(pendingMember)
    EC->>MR: activate(pendingMember, plan)
    MR-->>EC: member

    EC->>Cred: createAccessCredentials(member)
    Cred-->>EC: credentials

    EC->>Notif: sendWelcome(member, credentials)
    Notif-->>EC: sent

    EC-->>Staff: welcomeConfirmation
```

---

## UC3: Handle Membership Renewals — processRenewal

```mermaid
sequenceDiagram
    actor Staff as :Staff
    participant RC as :RenewalController
    participant MR as :MemberRepository
    participant Price as :PricingService
    participant Pay as :PaymentService
    participant PG as :PaymentGateway
    participant Card as :CardService
    participant Notif as :NotificationService

    Staff->>RC: retrieveMemberAccount(memberID)
    RC->>MR: findById(memberID)
    MR-->>RC: member
    RC-->>Staff: membershipDetails, expirationDate

    Staff->>RC: processRenewal(memberID, planChange)
    RC->>Price: calculateRenewalFees(member, planChange)
    Price-->>RC: renewalFees
    RC-->>Staff: renewalFees

    Staff->>RC: processPayment(paymentDetails)
    RC->>Pay: process(paymentDetails, renewalFees)
    Pay->>PG: charge(paymentDetails, renewalFees)
    PG-->>Pay: approved(txnId)
    Pay-->>RC: paymentConfirmation

    RC->>MR: extendValidity(member)
    MR-->>RC: updatedMember

    RC->>Card: generateUpdatedCard(updatedMember)
    Card-->>RC: updatedCard

    RC->>Notif: sendRenewalConfirmation(updatedMember)
    Notif-->>RC: sent

    RC-->>Staff: renewalConfirmation, updatedCard
```

---

## UC4: Process Member Payments — processPayment

```mermaid
sequenceDiagram
    actor Staff as :Staff
    participant PC as :PaymentController
    participant MR as :MemberRepository
    participant Items as :BillingItemService
    participant Discounts as :DiscountService
    participant Total as :TotalsService
    participant Pay as :PaymentService
    participant PG as :PaymentGateway
    participant TR as :TransactionRepository
    participant Notif as :NotificationService

    Staff->>PC: enterMemberID(memberID)
    PC->>MR: findById(memberID)
    MR-->>PC: member

    PC->>Items: listOutstanding(member)
    Items-->>PC: obligations
    PC->>Discounts: applyEligible(obligations, member)
    Discounts-->>PC: adjustedItems
    PC->>Total: compute(adjustedItems)
    Total-->>PC: amountDue
    PC-->>Staff: amountDue

    Staff->>PC: processPayment(method, paymentDetails)
    PC->>Pay: process(method, paymentDetails, amountDue)
    Pay->>PG: charge(paymentDetails, amountDue)
    PG-->>Pay: approved(txnId)
    Pay-->>PC: paymentConfirmation

    PC->>TR: record(member, adjustedItems, amountDue, txnId)
    TR-->>PC: recorded

    PC->>Notif: sendReceipt(member, txnId)
    Notif-->>PC: sent

    PC-->>Staff: paymentConfirmation, receipt
```

---

## UC5: Generate Financial Reports — generateReport

```mermaid
sequenceDiagram
    actor Manager as :Manager
    participant RepC as :ReportingController
    participant TR as :TransactionRepository
    participant Agg as :AggregationService
    participant Charts as :ChartService
    participant Format as :ReportFormatter

    Manager->>RepC: generateReport(type, dateRange, params)
    RepC->>TR: getTransactions(dateRange, params)
    TR-->>RepC: transactions

    RepC->>Agg: compileMetrics(transactions, type)
    Agg-->>RepC: metrics

    RepC->>Charts: buildVisuals(metrics)
    Charts-->>RepC: visuals

    RepC->>Format: formatReport(metrics, visuals, type)
    Format-->>RepC: report

    RepC-->>Manager: financialReport
```

---

## UC5: Generate Financial Reports — exportReport

```mermaid
sequenceDiagram
    actor Manager as :Manager
    participant RepC as :ReportingController
    participant Format as :ReportFormatter

    Manager->>RepC: exportReport(format)
    RepC->>Format: export(currentReport, format)
    Format-->>RepC: exportedFile
    RepC-->>Manager: exportedFile
```

---

## UC6: Track Member Access — presentIdentification

```mermaid
sequenceDiagram
    actor Member as :Member
    participant AC as :AccessController
    participant MR as :MemberRepository
    participant Policy as :AccessPolicyService
    participant Att as :AttendanceRepository
    participant Gate as :AccessControlProxy

    Member->>AC: presentIdentification(memberID)
    AC->>MR: findById(memberID)
    MR-->>AC: member

    AC->>Policy: verifyAccess(member)
    Policy-->>AC: accessAllowed=true

    AC->>Att: recordEntry(member)
    Att-->>AC: entryRecorded

    AC->>Gate: unlock()
    Gate-->>AC: unlocked

    AC-->>Member: accessGranted, welcomeMessage
```

---

## UC7: Monitor Gym Operations — retrieveRealTimeData

```mermaid
sequenceDiagram
    actor Manager as :Manager
    participant Ops as :OperationsController
    participant Occ as :OccupancyService
    participant Equip as :EquipmentUtilizationService
    participant StaffSvc as :StaffAttendanceService

    Manager->>Ops: retrieveRealTimeData()
    Ops->>Occ: getCurrentOccupancy()
    Occ-->>Ops: currentOccupancy, capacityUtilization

    Ops->>Equip: getZoneUtilization()
    Equip-->>Ops: zoneUtilizationRates

    Ops->>StaffSvc: getCoverage()
    StaffSvc-->>Ops: staffCoverage

    Ops-->>Manager: currentOccupancy, capacityUtilization, zoneUtilizationRates, staffCoverage
```

---

## UC7: Monitor Gym Operations — generateDailyReport

```mermaid
sequenceDiagram
    actor Manager as :Manager
    participant Ops as :OperationsController
    participant Rep as :OperationsReportService

    Manager->>Ops: generateDailyReport()
    Ops->>Rep: compileDaily()
    Rep-->>Ops: operationalReport
    Ops-->>Manager: operationalReport
```

---

## UC7: Monitor Gym Operations — retrievePeakUsageHours

```mermaid
sequenceDiagram
    actor Manager as :Manager
    participant Ops as :OperationsController
    participant Trends as :TrendAnalysisService

    Manager->>Ops: retrievePeakUsageHours()
    Ops->>Trends: getPeakHours()
    Trends-->>Ops: peakHoursData
    Ops-->>Manager: peakHoursData
```

---

## UC7: Monitor Gym Operations — retrieveOperationalMetrics

```mermaid
sequenceDiagram
    actor Manager as :Manager
    participant Ops as :OperationsController
    participant Metrics as :MetricsService

    Manager->>Ops: retrieveOperationalMetrics()
    Ops->>Metrics: getKeyMetrics()
    Metrics-->>Ops: avgDuration, busiestDays
    Ops-->>Manager: avgDuration, busiestDays
```

---

## UC8: Manage Trainer Operations — registerTrainer

```mermaid
sequenceDiagram
    actor HR as :HRManager
    participant TC as :TrainerController
    participant Val as :CertificationValidationService
    participant Doc as :DocumentStorage
    participant TR as :TrainerRepository
    participant Sched as :ScheduleService
    participant Auth as :AuthService
    participant Notif as :NotificationService

    HR->>TC: initiateTrainerRegistration()
    TC-->>HR: registrationForm

    HR->>TC: enterPersonalInfo(name, contact, address)
    HR->>TC: enterProfessionalInfo(certifications, specializations)

    TC->>Val: validate(certifications)
    Val-->>TC: validationResults

    TC->>Doc: store(certificationDocuments)
    Doc-->>TC: storedRefs

    TC->>TR: saveProfile(name, contact, address, specializations, storedRefs)
    TR-->>TC: trainer

    TC->>Sched: setAvailability(trainer, availability)
    Sched-->>TC: scheduled

    TC->>Auth: createAccount(trainer)
    Auth-->>TC: credentials

    TC->>Notif: sendWelcome(trainer, credentials)
    Notif-->>TC: sent

    TC-->>HR: trainerProfileCreated
```

---

## UC9: Schedule Personal Training Sessions — createSession

```mermaid
sequenceDiagram
    actor Staff as :Staff
    participant SC as :SchedulingController
    participant MR as :MemberRepository
    participant Avail as :AvailabilityService
    participant Price as :PricingService
    participant Pay as :PaymentService
    participant PG as :PaymentGateway
    participant Sess as :SessionRepository
    participant Notif as :NotificationService

    Staff->>SC: enterMemberID(memberID)
    SC->>MR: findById(memberID)
    MR-->>SC: member

    Staff->>SC: selectTrainer(trainerID)
    Staff->>SC: selectDateTime(date, time)

    SC->>Avail: check(trainerID, date, time)
    Avail-->>SC: conflict=false

    Staff->>SC: specifySessionDetails(type, duration)
    SC->>Price: getPrice(type, duration)
    Price-->>SC: sessionPrice

    SC->>Pay: process(paymentDetails, sessionPrice)
    Pay->>PG: charge(paymentDetails, sessionPrice)
    PG-->>Pay: approved(txnId)
    Pay-->>SC: paymentConfirmation

    SC->>Sess: create(member, trainerID, date, time, type, duration, txnId)
    Sess-->>SC: booking

    SC->>Notif: notifyTrainer(trainerID, booking)
    Notif-->>SC: sent

    SC->>Notif: notifyMember(member, booking)
    Notif-->>SC: sent

    SC-->>Staff: bookingConfirmation
```

---

## UC10: Manage Equipment Inventory — addEquipment

```mermaid
sequenceDiagram
    actor FM as :FacilitiesManager
    participant IC as :InventoryController
    participant EquipRepo as :EquipmentRepository
    participant Doc as :DocumentStorage
    participant Warranty as :WarrantyService
    participant InvSvc as :InventoryService
    participant IDG as :EquipmentIDGenerator

    FM->>IC: initiateAddEquipment()
    IC-->>FM: equipmentForm

    FM->>IC: enterEquipmentDetails(name, type, model, manufacturer)
    FM->>IC: enterSerialNumber(serial, purchaseInfo)
    FM->>IC: selectCategory(category)
    FM->>IC: assignGymZone(zone)
    FM->>IC: setInitialCondition(condition)

    IC->>IDG: generate()
    IDG-->>IC: equipmentID

    IC->>EquipRepo: create(equipmentID, name, type, model, manufacturer, serial, category, zone, condition)
    EquipRepo-->>IC: equipment

    IC->>Doc: upload(photos, manuals)
    Doc-->>IC: storageRefs

    IC->>Warranty: record(equipment, purchaseInfo)
    Warranty-->>IC: recorded

    IC->>EquipRepo: save(equipment, storageRefs)
    EquipRepo-->>IC: saved

    IC->>InvSvc: updateCounts(category)
    InvSvc-->>IC: updated

    IC-->>FM: equipmentRecordSaved(equipmentID)
```

---

## UC12: Process Overdue Accounts — generateAndSendReminder

```mermaid
sequenceDiagram
    actor AR as :AccountingStaff
    participant ARC as :AccountsReceivableController
    participant AccRepo as :AccountsRepository
    participant Late as :LateFeeService
    participant Notif as :NotificationService
    participant Log as :OverdueLogRepository

    AR->>ARC: selectAccount(memberID)
    ARC->>AccRepo: getAccount(memberID)
    AccRepo-->>ARC: account

    ARC->>Late: applyIfNeeded(account)
    Late-->>ARC: updatedAccount

    ARC->>Notif: composeReminder(updatedAccount)
    Notif-->>ARC: reminderNotice

    ARC->>Notif: sendReminder(updatedAccount, method)
    Notif-->>ARC: reminderSent

    ARC->>Log: recordCommunication(updatedAccount)
    Log-->>ARC: recorded

    ARC-->>AR: reminderSent
```

---

## UC12: Process Overdue Accounts — processPayment (reference)

```mermaid
sequenceDiagram
    participant PC as :PaymentController
    Note over PC: See UC4: processPayment for full flow
```

---

## UC15: Manage System Configuration — modifySettings

```mermaid
sequenceDiagram
    actor Admin as :Administrator
    participant ConfC as :ConfigurationController
    participant Repo as :ConfigRepository
    participant Validate as :ValidationEngine
    participant Audit as :AuditLogService
    participant Notif as :NotificationService

    Admin->>ConfC: selectConfigurationCategory(category)
    ConfC->>Repo: getCurrent(category)
    Repo-->>ConfC: currentSettings

    Admin->>ConfC: modifySettings(settingName, newValue)
    ConfC->>Validate: validateChange(category, settingName, newValue)
    Validate-->>ConfC: validationResult=ok

    Admin->>ConfC: confirmChanges()
    ConfC->>Repo: update(category, settingName, newValue)
    Repo-->>ConfC: updated

    ConfC->>Audit: logChange(admin, category, settingName)
    Audit-->>ConfC: logged

    ConfC->>Notif: notifyAffectedUsers(category)
    Notif-->>ConfC: notified

    ConfC-->>Admin: successConfirmation
```

---


