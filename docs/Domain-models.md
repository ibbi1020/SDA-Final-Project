# Block20 Gym Management System - Domain Model

**Unified Domain Model** (Use Cases 1-12, 15)

**Core Classes:** Member, Staff, Trainer, MembershipPlan, Membership, Payment, Enrollment, Renewal, EmergencyContact, Receipt, PaymentItem, RenewalNotification, FinancialReport, AttendanceRecord, TrainingSession, SessionPackage, Equipment, GymZone, MaintenanceRequest, Certification, PaymentPlan, LateFee

```mermaid
classDiagram
    %% Core People/Roles
    class Member {
        memberID: String
        name: String
        phoneNumber: String
        email: String
        status: String
        dateJoined: Date
        accountBalance: Money
    }
    
    class Staff {
        staffID: String
        name: String
        role: String
    }
    
    class Trainer {
        trainerID: String
        name: String
        phoneNumber: String
        email: String
        specialization: String
        employmentStatus: String
    }
    
    class EmergencyContact {
        name: String
        phoneNumber: String
        relationship: String
    }
    
    %% Membership & Plans
    class MembershipPlan {
        planID: String
        name: String
        price: Money
        duration: Integer
        features: String
    }
    
    class Membership {
        membershipID: String
        startDate: Date
        expirationDate: Date
        status: String
    }
    
    %% Business Transactions
    class Enrollment {
        enrollmentID: String
        enrollmentDate: Date
        agreementSigned: Boolean
    }
    
    class Renewal {
        renewalID: String
        renewalDate: Date
        previousExpirationDate: Date
        newExpirationDate: Date
    }
    
    class Payment {
        paymentID: String
        amount: Money
        paymentDate: DateTime
        paymentMethod: String
        transactionID: String
        category: String
    }
    
    %% Transaction Line Items & Records
    class PaymentItem {
        itemID: String
        description: String
        amount: Money
        itemType: String
    }
    
    class Receipt {
        receiptID: String
        issueDate: DateTime
        totalAmount: Money
    }
    
    %% Notifications & Reports
    class RenewalNotification {
        notificationID: String
        sentDate: Date
        channel: String
        daysBeforeExpiration: Integer
    }
    
    class FinancialReport {
        reportID: String
        reportType: String
        periodStart: Date
        periodEnd: Date
        generationDate: DateTime
        totalRevenue: Money
    }
    
    %% Attendance & Sessions (UC6, UC9)
    class AttendanceRecord {
        recordID: String
        checkInTime: DateTime
        checkOutTime: DateTime
        sessionDuration: Integer
    }
    
    class TrainingSession {
        sessionID: String
        sessionDate: Date
        startTime: Time
        duration: Integer
        sessionType: String
        status: String
    }
    
    class SessionPackage {
        packageID: String
        packageName: String
        totalSessions: Integer
        remainingSessions: Integer
        price: Money
    }
    
    %% Equipment & Facilities (UC10)
    class Equipment {
        equipmentID: String
        name: String
        serialNumber: String
        category: String
        manufacturer: String
        purchaseDate: Date
        warrantyExpiration: Date
        conditionStatus: String
    }
    
    class GymZone {
        zoneID: String
        zoneName: String
        capacity: Integer
    }
    
    class MaintenanceRequest {
        requestID: String
        requestDate: Date
        reason: String
        status: String
        completionDate: Date
    }
    
    %% Trainer Management (UC8)
    class Certification {
        certificationID: String
        certificationName: String
        issuingBody: String
        issueDate: Date
        expirationDate: Date
    }
    
    %% Payment Plans & Late Fees (UC12)
    class PaymentPlan {
        planID: String
        startDate: Date
        totalAmount: Money
        installmentAmount: Money
        remainingBalance: Money
        numberOfInstallments: Integer
        status: String
    }
    
    class LateFee {
        feeID: String
        amount: Money
        daysOverdue: Integer
        appliedDate: Date
    }
    
    %% Associations - Member Related
    Member "1" -- "1..*" EmergencyContact : Has
    Member "1" -- "1" Membership : Has
    Membership "*" -- "1" MembershipPlan : Based-on
    Member "1" -- "*" AttendanceRecord : Has
    Member "1" -- "*" RenewalNotification : Receives
    
    %% Associations - Staff Related
    Staff "1" -- "*" Enrollment : Processes
    Staff "1" -- "*" Renewal : Processes
    Staff "1" -- "*" Payment : Processes
    Staff "1" -- "*" TrainingSession : Schedules
    
    %% Associations - Transactions
    Enrollment "1" -- "1" Member : Creates
    Enrollment "1" -- "1" Payment : Includes
    
    Renewal "*" -- "1" Member : For
    Renewal "1" -- "1" Payment : Requires
    
    Payment "*" -- "1" Member : For
    Payment "1" -- "1" Receipt : Generates
    Payment "1" -- "1..*" PaymentItem : Contains
    
    %% Associations - Reports
    FinancialReport "1" -- "*" Payment : Analyzes
    
    %% Associations - Training Sessions (UC9)
    TrainingSession "*" -- "1" Member : For
    TrainingSession "*" -- "1" Trainer : With
    SessionPackage "*" -- "1" Member : Belongs-to
    
    %% Associations - Equipment & Facilities (UC10)
    Equipment "*" -- "1" GymZone : Located-in
    MaintenanceRequest "*" -- "1" Equipment : For
    
    %% Associations - Trainer (UC8)
    Trainer "1" -- "*" Certification : Holds
    
    %% Associations - Payment Plans & Late Fees (UC12)
    PaymentPlan "*" -- "1" Member : For
    LateFee "*" -- "1" Member : Charged-to
```

---

## Use Case Coverage

- **UC1 (Manage Member Profiles):** Member, Staff, EmergencyContact
- **UC2 (Process Member Enrollment):** Enrollment, MembershipPlan, Payment
- **UC3 (Handle Membership Renewals):** Membership, Renewal, RenewalNotification
- **UC4 (Process Member Payments):** Payment, Receipt, PaymentItem
- **UC5 (Generate Financial Reports):** FinancialReport
- **UC6 (Manage Member Check-in/Check-out):** AttendanceRecord
- **UC7 (Monitor Gym Operations):** _(No new domain classes - monitoring/reporting)_
- **UC8 (Manage Trainer Operations):** Trainer, Certification
- **UC9 (Schedule Personal Training Sessions):** TrainingSession, SessionPackage
- **UC10 (Manage Equipment Inventory):** Equipment, GymZone, MaintenanceRequest
- **UC12 (Process Overdue Accounts):** PaymentPlan, LateFee
- **UC15 (Manage System Configuration):** _(No domain classes - software configuration)_

---