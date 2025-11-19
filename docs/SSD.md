# Block20 Gym Management System - System Sequence Diagrams

## UC1: Manage Member Profiles (Main Success Scenario)

**Actor:** Administrator  
**System Events:** selectManageMembers, chooseCreateNewMember, enterMemberDetails

```mermaid
sequenceDiagram
    participant A as :Administrator
    participant S as :System
    
    A->>S: selectManageMembers()
    S-->>A: member management interface
    
    A->>S: chooseCreateNewMember()
    S-->>A: member entry form
    
    A->>S: enterMemberDetails(name, phoneNumber, email, emergencyContactInfo)
    S-->>A: memberID, confirmation message
```

---

## UC2: Process Member Enrollment (Main Success Scenario)

```mermaid
sequenceDiagram
    participant S as :Staff
    participant SYS as :System

    S->>SYS: initiateEnrollment()
    SYS-->>S: enrollmentForm

    S->>SYS: enterMemberInfo(name, contact, emergencyContact)
    SYS-->>S: membershipPlans

    S->>SYS: selectMembershipPlan(planID)
    SYS-->>S: calculatedFees

    S->>SYS: enterPaymentInfo(paymentDetails)
    SYS-->>S: paymentConfirmation

    S->>SYS: generateMembershipAgreement()
    SYS-->>S: agreementDocument

    S->>SYS: submitSignedAgreement()
    SYS-->>S: accessCredentials

    S->>SYS: activateMembership()
    SYS-->>S: welcomeConfirmation
```

---

## UC3: Handle Membership Renewals (Main Success Scenario)

```mermaid
sequenceDiagram
    participant S as :Staff
    participant SYS as :System

    S->>SYS: retrieveMemberAccount(memberID)
    SYS-->>S: membershipDetails, expirationDate

    S->>SYS: processRenewal(memberID, planChange)
    SYS-->>S: renewalFees

    S->>SYS: processPayment(paymentDetails)
    SYS-->>S: paymentConfirmation

    S->>SYS: extendMembershipValidity(memberID)
    SYS-->>S: renewalConfirmation, updatedCard
```

---

## UC4: Process Member Payments (Main Success Scenario)

```mermaid
sequenceDiagram
    participant S as :Staff
    participant SYS as :System

    S->>SYS: initiatePaymentProcessing()
    SYS-->>S: promptMemberIdentification

    S->>SYS: enterMemberID(memberID)
    SYS-->>S: memberAccount, outstandingObligations

    S->>SYS: processPayment(amount, method)
    SYS-->>S: paymentConfirmation, receipt
```

---

## UC5: Generate Financial Reports (Main Success Scenario)

```mermaid
sequenceDiagram
    participant M as :Manager
    participant SYS as :System

    M->>SYS: selectFinancialReports()
    SYS-->>M: reportGenerationInterface

    M->>SYS: selectReportType(type)
    M->>SYS: specifyDateRange(startDate, endDate)
    M->>SYS: selectReportParameters(params)
    SYS-->>M: reportPreview

    M->>SYS: generateReport()
    SYS-->>M: financialReport

    M->>SYS: exportReport(format)
    SYS-->>M: exportedFile
```

---
## UC6: Track Member Access (Main Success Scenario)

```mermaid
sequenceDiagram
    participant M as :Member
    participant SYS as :System

    M->>SYS: presentIdentification(memberID)
    SYS-->>M: accessGranted, welcomeMessage

    M->>SYS: exitFacility(memberID)
    SYS-->>M: exitRecorded
```

---

## UC7: Monitor Gym Operations (Main Success Scenario)

```mermaid
sequenceDiagram
    participant MGR as :Manager
    participant SYS as :System

    MGR->>SYS: openOperationalDashboard()
    SYS-->>MGR: dashboardInterface

    MGR->>SYS: retrieveRealTimeData()
    SYS-->>MGR: currentOccupancy, capacityUtilization

    MGR->>SYS: retrievePeakUsageHours()
    SYS-->>MGR: peakHoursData

    MGR->>SYS: retrieveHourlyTrends()
    SYS-->>MGR: hourlyVisitTrends

    MGR->>SYS: retrieveUsagePatterns(period)
    SYS-->>MGR: usagePatternCharts

    MGR->>SYS: retrieveEquipmentZoneUtilization()
    SYS-->>MGR: zoneUtilizationRates

    MGR->>SYS: retrieveStaffAttendance()
    SYS-->>MGR: staffCoverage

    MGR->>SYS: retrieveOperationalMetrics()
    SYS-->>MGR: avgDuration, busiestDays

    MGR->>SYS: generateDailyReport()
    SYS-->>MGR: operationalReport
```

---

## UC8: Manage Trainer Operations (Main Success Scenario)

```mermaid
sequenceDiagram
    participant HR as :HRManager
    participant SYS as :System
    participant T as :Trainer

    HR->>SYS: selectTrainerManagement()
    SYS-->>HR: managementInterface

    HR->>SYS: initiateTrainerRegistration()
    SYS-->>HR: registrationForm

    HR->>SYS: enterPersonalInfo(name, contact, address)
    HR->>SYS: enterProfessionalInfo(certifications, specializations)
    SYS-->>HR: validationResults

    HR->>SYS: uploadCertificationDocuments(documents)
    SYS-->>HR: uploadConfirmation

    HR->>SYS: createTrainerSchedule(availability)
    HR->>SYS: setSpecializationAreas(areas)
    
    SYS-->>HR: assignedTrainerID

    HR->>SYS: enterEmploymentDetails(contractType, payRate, commission)
    
    SYS-->>HR: trainerProfileCreated

    SYS->>T: sendWelcomeEmail(credentials)
```

---

## UC9: Schedule Personal Training Sessions (Main Success Scenario)

```mermaid
sequenceDiagram
    participant S as :Staff
    participant SYS as :System
    participant T as :Trainer

    S->>SYS: initiateSessionScheduling()
    SYS-->>S: bookingInterface

    S->>SYS: enterMemberID(memberID)
    SYS-->>S: memberEligibilityConfirmed

    S->>SYS: selectTrainer(trainerID)
    SYS-->>S: availabilityCalendar

    S->>SYS: selectDateTime(date, time)
    SYS-->>S: conflictCheckResult

    S->>SYS: specifySessionDetails(type, duration)
    SYS-->>S: sessionPricing

    S->>SYS: processPayment(paymentDetails)
    SYS-->>S: paymentConfirmation

    SYS->>T: sendSessionConfirmation()
    
    SYS-->>S: bookingConfirmation
```

---

## UC10: Manage Equipment Inventory (Main Success Scenario)

```mermaid
sequenceDiagram
    participant FM as :FacilitiesManager
    participant SYS as :System

    FM->>SYS: selectEquipmentManagement()
    SYS-->>FM: inventoryInterface

    FM->>SYS: initiateAddEquipment()
    SYS-->>FM: equipmentForm

    FM->>SYS: enterEquipmentDetails(name, type, model, manufacturer)
    FM->>SYS: enterSerialNumber(serial, purchaseInfo)
    FM->>SYS: selectCategory(category)
    FM->>SYS: assignGymZone(zone)
    FM->>SYS: setInitialCondition(condition)
    
    FM->>SYS: uploadDocuments(photos, manuals)
    SYS-->>FM: uploadConfirmation

    SYS-->>FM: generatedEquipmentID

    FM->>SYS: recordPurchaseAndWarranty(date, warrantyInfo)
    
    SYS-->>FM: equipmentRecordSaved
```

---

## UC12: Process Overdue Accounts (Main Success Scenario)

```mermaid
sequenceDiagram
    participant AS as :AccountingStaff
    participant SYS as :System

    AS->>SYS: reviewOverdueAccountsList()
    SYS-->>AS: prioritizedOverdueAccounts

    AS->>SYS: selectAccount(memberID)
    SYS-->>AS: accountDetails, overdueAmount, daysPastDue

    AS->>SYS: generatePaymentReminder(memberID)
    SYS-->>AS: reminderNotice

    AS->>SYS: sendReminder(memberID, communicationMethod)
    SYS-->>AS: reminderSent

    AS->>SYS: recordMemberContact(memberID)
    SYS-->>AS: paymentOptions

    AS->>SYS: processPayment(memberID, amount, method)
    SYS-->>AS: paymentConfirmation, updatedBalance
```

---

## UC15: Manage System Configuration (Main Success Scenario)

```mermaid
sequenceDiagram
    participant ADMIN as :Administrator
    participant SYS as :System

    ADMIN->>SYS: selectSystemConfiguration()
    SYS-->>ADMIN: configurationDashboard

    ADMIN->>SYS: selectConfigurationCategory(category)
    SYS-->>ADMIN: currentSettings

    ADMIN->>SYS: modifySettings(settingName, newValue)
    SYS-->>ADMIN: validationResult

    ADMIN->>SYS: confirmChanges()
    SYS-->>ADMIN: confirmationPrompt

    ADMIN->>SYS: approveConfiguration()
    
    SYS-->>ADMIN: successConfirmation
    SYS-->>ADMIN: changeLogEntry
```

---
