# Block20 Gym Management System - Detailed Feature List

This document outlines the features to be implemented for the Block20 Gym Management System, derived from User Stories and User Flows.

---

## 1. Member Management
**Epic:** M1

### 1.1 Member Profile Creation
*   **Description:** Ability for administrators to create new member profiles with comprehensive personal and contact information.
*   **User Stories:** M1.1
*   **User Flows:** M1.1 (Dashboard → Members → "+ New Member")
*   **Priority:** High
*   **Key Capabilities:**
    *   Input validation (email, phone).
    *   Duplicate member checking.
    *   Auto-generation of unique Member ID.
    *   Emergency contact recording.
    *   PDF Member Card generation.
*   **Implementation Brief:**
    *   **Design Perspective:** We will use the **Controller** pattern (`MemberController`) to orchestrate the creation flow. The controller will delegate validation logic to a dedicated `ValidationService` (**Information Expert**) to ensure the controller remains thin. A `DuplicateCheckService` will handle the logic for identifying existing members to prevent data redundancy.
    *   **Patterns:**
        *   **Controller (GRASP):** `MemberController` handles the UI events.
        *   **Pure Fabrication (GRASP):** `IDGenerationService` for creating unique IDs without cluttering the domain model.
        *   **Creator (GRASP):** `MemberRepository` handles the persistence of the new `Member` object.
    *   **Diagram Reference:** [UC1: Manage Member Profiles - enterMemberDetails](DesignClassDiagrams.md#entermemberdetails)

### 1.2 Member Search & Filtering
*   **Description:** Advanced search functionality to quickly locate members by ID, name, email, or phone.
*   **User Stories:** M1.4
*   **User Flows:** M1.2 (Dashboard → Members Search)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Real-time search results.
    *   Filtering by status (Active, Inactive, etc.).
    *   Filtering by plan type and expiration.
    *   Pagination for large datasets.
*   **Implementation Brief:**
    *   **Design Perspective:** Implement a flexible search method in `MemberRepository` that accepts a criteria object (Specification Pattern) or multiple optional parameters. This allows for dynamic query construction based on user input. The `MemberController` will expose this via a `searchMembers` method.
    *   **Patterns:**
        *   **Repository (DDD/Enterprise):** `MemberRepository` abstracts the data access logic.
        *   **Specification (DDD - optional):** To encapsulate complex search rules if the filtering logic grows.
    *   **Diagram Reference:** [UC1: Manage Member Profiles - selectManageMembers](DesignClassDiagrams.md#selectmanagemembers)

### 1.3 Member Profile Update
*   **Description:** Capability to edit existing member details.
*   **User Stories:** M1.2
*   **User Flows:** M1.3 (Member Profile → Edit)
*   **Priority:** High
*   **Key Capabilities:**
    *   Inline editing of details.
    *   Audit trail of changes.
    *   Validation of updated information.
    *   Immutable Member ID.
*   **Implementation Brief:**
    *   **Design Perspective:** The `Member` domain entity should have methods to update its state (e.g., `updateContactInfo`), ensuring the domain model protects its invariants (**Information Expert**). The `MemberController` retrieves the entity, calls the update methods, and then asks the `MemberRepository` to save the changes. An `AuditService` should be invoked to log the changes (**Pure Fabrication**).
    *   **Patterns:**
        *   **Information Expert (GRASP):** The `Member` class knows how to update its own fields validly.
        *   **Observer (GoF - optional):** Could be used to trigger audit logs automatically on state changes.
    *   **Diagram Reference:** [UC1: Manage Member Profiles](DesignClassDiagrams.md#uc1-manage-member-profiles)

### 1.4 Member Deactivation/Freezing
*   **Description:** Functionality to deactivate or freeze member accounts.
*   **User Stories:** M1.3
*   **User Flows:** M1.4 (Member Profile → More → Freeze/Deactivate)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Reason selection for deactivation.
    *   Outstanding dues check before deactivation.
    *   Status update to "Inactive" or "Frozen".
    *   Access credential revocation.
*   **Implementation Brief:**
    *   **Design Perspective:** This is a state transition for the `Member` entity. We should implement a State pattern if the logic for different states (Active, Frozen, Inactive) becomes complex. For now, simple methods like `deactivate()` on the `Member` class will suffice. The `MemberController` must first check for outstanding balances using the `BillingItemService` before allowing this transition.
    *   **Patterns:**
        *   **State (GoF):** To manage the different behaviors associated with member states.
        *   **Facade (GoF):** A `MemberManagementFacade` could wrap the check-balance-then-deactivate logic to simplify the controller.
    *   **Diagram Reference:** [UC1: Manage Member Profiles](DesignClassDiagrams.md#uc1-manage-member-profiles)

---

## 2. Member Enrollment
**Epic:** E2

### 2.1 Enrollment Process
*   **Description:** Structured wizard/form for enrolling new members into specific plans.
*   **User Stories:** E2.1
*   **User Flows:** E2.1 (Member Profile → Enroll)
*   **Priority:** High
*   **Key Capabilities:**
    *   Plan selection (Basic, Premium, Student, Senior).
    *   Fee calculation (including taxes).
    *   Duplicate enrollment prevention.
    *   Progress saving.
*   **Implementation Brief:**
    *   **Design Perspective:** The `EnrollmentController` acts as the coordinator. It uses `PricingService` (**Pure Fabrication**) to calculate fees based on the selected `MembershipPlan`. The `EnrollmentRepository` is responsible for creating the persistent `Enrollment` record.
    *   **Patterns:**
        *   **Controller (GRASP):** `EnrollmentController` manages the multi-step process.
        *   **Builder (GoF):** Useful for constructing the complex `Enrollment` object with many optional fields.
    *   **Diagram Reference:** [UC2: Process Member Enrollment - activateMembership](DesignClassDiagrams.md#activatemembership)

### 2.2 Enrollment Payment
*   **Description:** Secure processing of initial membership fees.
*   **User Stories:** E2.2
*   **User Flows:** E2.2 (Integrated into Enrollment)
*   **Priority:** High
*   **Key Capabilities:**
    *   Support for Credit/Debit/Cash.
    *   Gateway integration (PCI DSS compliant).
    *   Transaction recording.
    *   Receipt generation (E2.3).
*   **Implementation Brief:**
    *   **Design Perspective:** Use the `PaymentService` to handle the interaction with the payment gateway. This service should use the **Adapter** pattern to wrap the external payment provider (e.g., Stripe, PayPal), allowing us to switch providers without changing our core logic.
    *   **Patterns:**
        *   **Adapter (GoF):** `IPaymentGateway` interface with concrete implementations for specific providers.
        *   **Protected Variations (GRASP):** Isolating the system from the volatility of external payment APIs.
    *   **Diagram Reference:** [UC2: Process Member Enrollment - activateMembership](DesignClassDiagrams.md#activatemembership)

### 2.3 Membership Agreement
*   **Description:** Generation and signing of legal membership contracts.
*   **User Stories:** E2.3
*   **Priority:** High
*   **Key Capabilities:**
    *   PDF agreement generation.
    *   Digital signature capture.
    *   Storage of signed documents.
*   **Implementation Brief:**
    *   **Design Perspective:** An `AgreementService` (**Pure Fabrication**) handles the generation of the PDF document. It takes the `Member` and `MembershipPlan` as inputs. The `AgreementRepository` stores the metadata and the file path (or blob).
    *   **Patterns:**
        *   **Strategy (GoF):** Could be used if we have different agreement templates for different plan types.
    *   **Diagram Reference:** [UC2: Process Member Enrollment - generateMembershipAgreement](DesignClassDiagrams.md#generatemembershipagreement)

### 2.4 Access Credential Generation
*   **Description:** Automatic creation of login credentials for the member portal.
*   **User Stories:** E2.4
*   **Priority:** High
*   **Key Capabilities:**
    *   Unique username/password generation.
    *   Secure hashing.
    *   Email/SMS delivery of credentials.
*   **Implementation Brief:**
    *   **Design Perspective:** `CredentialsService` is responsible for generating secure random passwords and hashing them before storage. It should interact with a `NotificationService` to send the credentials to the user securely.
    *   **Patterns:**
        *   **Pure Fabrication (GRASP):** `CredentialsService` handles security logic that doesn't fit naturally into the `Member` entity.
    *   **Diagram Reference:** [UC2: Process Member Enrollment - activateMembership](DesignClassDiagrams.md#activatemembership)

---

## 3. Membership Renewals
**Epic:** R3

### 3.1 Expiration Tracking
*   **Description:** Automated system to identify and flag expiring memberships.
*   **User Stories:** R3.1
*   **User Flows:** R3.3 (Dashboard → Expiring Soon)
*   **Priority:** High
*   **Key Capabilities:**
    *   Daily batch jobs.
    *   Identification at 30, 15, 7 day thresholds.
*   **Implementation Brief:**
    *   **Design Perspective:** A scheduled task (Cron job) invokes a `RenewalService` which queries the `MemberRepository` for members with `expiryDate` within the target ranges. This decouples the temporal logic from the user-driven controllers.
    *   **Patterns:**
        *   **Observer (GoF):** The system "observes" time and notifies the relevant services when a condition is met.
    *   **Diagram Reference:** [UC3: Handle Membership Renewals](DesignClassDiagrams.md#uc3-handle-membership-renewals)

### 3.2 Renewal Reminders
*   **Description:** Automated notifications sent to members about upcoming expirations.
*   **User Stories:** R3.2
*   **User Flows:** R3.2 (System Automated)
*   **Priority:** High
*   **Key Capabilities:**
    *   Email/SMS notifications.
    *   Customizable templates.
    *   Tracking of reminder delivery.
*   **Implementation Brief:**
    *   **Design Perspective:** The `NotificationService` should be used here. It can use a **Template Method** pattern to define the structure of a notification (header, body, footer) while allowing subclasses or configuration to define the specific content for renewal reminders.
    *   **Patterns:**
        *   **Template Method (GoF):** For consistent notification formatting.
    *   **Diagram Reference:** [UC3: Handle Membership Renewals - processRenewal](DesignClassDiagrams.md#processrenewal)

### 3.3 Staff-Assisted Renewal
*   **Description:** Interface for staff to process renewals for members in-person.
*   **User Stories:** R3.3
*   **User Flows:** R3.1 (Member Profile → Renew)
*   **Priority:** High
*   **Key Capabilities:**
    *   Discount calculation (Early bird, Loyalty).
    *   Plan changes during renewal.
    *   Payment processing.
*   **Implementation Brief:**
    *   **Design Perspective:** `RenewalController` handles the interaction. `PricingService` is critical here to calculate the correct renewal fee, applying any logic for "Early Bird" or "Loyalty" discounts. This logic should be encapsulated in the service, not the controller.
    *   **Patterns:**
        *   **Strategy (GoF):** `PricingService` can use different pricing strategies (Standard, Discounted, Promotional) based on the context.
    *   **Diagram Reference:** [UC3: Handle Membership Renewals - processRenewal](DesignClassDiagrams.md#processrenewal)

### 3.4 Self-Service Renewal
*   **Description:** Member portal feature allowing members to renew their own subscriptions.
*   **User Stories:** R3.4
*   **User Flows:** R3.4 (Member Portal → Renew)
*   **Priority:** High
*   **Key Capabilities:**
    *   Online payment processing.
    *   Real-time account update.
*   **Implementation Brief:**
    *   **Design Perspective:** Reuse the `RenewalController` logic but expose it via a different view or API endpoint for the member portal. The underlying business logic in `RenewalService` and `PaymentService` remains the same, ensuring consistency between staff-assisted and self-service renewals.
    *   **Patterns:**
        *   **Facade (GoF):** A `RenewalFacade` could provide a simplified interface for the frontend to call, handling the complexity of pricing, payment, and updating records in one go.
    *   **Diagram Reference:** [UC3: Handle Membership Renewals](DesignClassDiagrams.md#uc3-handle-membership-renewals)

### 3.5 Non-Renewal Handling
*   **Description:** Process to handle and record reasons for members leaving.
*   **User Stories:** R3.5
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Reason recording (Cost, Relocation, etc.).
    *   Exit survey distribution.
*   **Implementation Brief:**
    *   **Design Perspective:** This is a data collection task. The `RenewalController` should provide an endpoint to capture the "Non-Renewal Reason". This data should be stored in a separate `ExitSurvey` entity or a related table for analysis.
    *   **Patterns:**
        *   **Controller (GRASP):** Handles the input of the reason.
    *   **Diagram Reference:** [UC3: Handle Membership Renewals](DesignClassDiagrams.md#uc3-handle-membership-renewals)

---

## 4. Payment Processing
**Epic:** P4

### 4.1 General Payment Processing
*   **Description:** Point-of-sale functionality for various fees (training, penalties, etc.).
*   **User Stories:** P4.1
*   **User Flows:** E2.2 (Reusable flow)
*   **Priority:** High
*   **Key Capabilities:**
    *   Member obligation retrieval.
    *   Multiple payment methods.
    *   Receipt generation.
*   **Implementation Brief:**
    *   **Design Perspective:** `PaymentController` coordinates the flow. `BillingItemService` aggregates all outstanding charges (membership, training, etc.). `TotalsService` calculates the final amount. `PaymentService` processes the transaction. This separation ensures that fee calculation is distinct from payment execution.
    *   **Patterns:**
        *   **Composite (GoF):** If we have complex billing items (e.g., bundles), a Composite pattern could treat individual items and bundles uniformly.
        *   **Controller (GRASP):** `PaymentController` is the entry point.
    *   **Diagram Reference:** [UC4: Process Member Payments - processPayment](DesignClassDiagrams.md#processpayment)

### 4.2 Payment History & Balances
*   **Description:** Viewable history of all transactions and current outstanding balances.
*   **User Stories:** P4.2
*   **User Flows:** P4.1 (Member Profile → Payments tab)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Transaction details (Date, Amount, Method).
    *   Outstanding balance breakdown.
    *   Refund processing (Flow P4.2).
*   **Implementation Brief:**
    *   **Design Perspective:** `TransactionRepository` is the key here. It needs efficient query methods to retrieve history by member. `BillingItemService` calculates the current balance by summing unpaid items.
    *   **Patterns:**
        *   **Repository (DDD):** For efficient data retrieval.
    *   **Diagram Reference:** [UC4: Process Member Payments](DesignClassDiagrams.md#uc4-process-member-payments)

---

## 5. Financial Reporting
**Epic:** FR5

### 5.1 Financial Report Generation
*   **Description:** Creation of detailed financial reports (Revenue, Expenses, Sales).
*   **User Stories:** FR5.1
*   **User Flows:** FR5.1 (Reports → Financial)
*   **Priority:** High
*   **Key Capabilities:**
    *   Date range selection.
    *   Visualization (Charts/Graphs).
    *   KPI calculation.
*   **Implementation Brief:**
    *   **Design Perspective:** `ReportingController` delegates to `AggregationService` to crunch the numbers from `TransactionRepository`. `ChartService` then takes these metrics and prepares the data structure for visualization. This pipeline approach separates data retrieval, processing, and presentation.
    *   **Patterns:**
        *   **Builder (GoF):** To construct the complex `FinancialReport` object step-by-step (metrics, then charts, then summary).
        *   **Pure Fabrication (GRASP):** `AggregationService` and `ChartService`.
    *   **Diagram Reference:** [UC5: Generate Financial Reports - generateReport](DesignClassDiagrams.md#generatereport)

### 5.2 Report Export
*   **Description:** Ability to export reports for external use.
*   **User Stories:** FR5.2
*   **User Flows:** FR5.1 (Export option)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Formats: PDF, Excel, CSV.
    *   Formatted output.
*   **Implementation Brief:**
    *   **Design Perspective:** Use the **Strategy Pattern** for the `ExportService`. We can have `PdfExportStrategy`, `CsvExportStrategy`, etc. The controller simply passes the report and the desired format to the service, which selects the correct strategy.
    *   **Patterns:**
        *   **Strategy (GoF):** To interchange export algorithms (PDF vs CSV) seamlessly.
        *   **Polymorphism (GRASP):** Handling different file formats through a common interface.
    *   **Diagram Reference:** [UC5: Generate Financial Reports - exportReport](DesignClassDiagrams.md#exportreport)

### 5.3 Real-Time Financial Dashboard
*   **Description:** Live dashboard for managers showing key financial metrics.
*   **User Stories:** FR5.3
*   **User Flows:** FR5.3 (Member Portal/Manager Dashboard)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Real-time revenue tracking.
    *   Membership distribution charts.
    *   Payment status overview.
*   **Implementation Brief:**
    *   **Design Perspective:** This requires a highly responsive backend. `MetricsService` should provide a lightweight "snapshot" of the current state. We might implement caching (Proxy Pattern) to prevent overloading the database with frequent dashboard refreshes.
    *   **Patterns:**
        *   **Proxy (GoF):** To add a caching layer for expensive metric calculations.
    *   **Diagram Reference:** [UC7: Monitor Gym Operations - retrieveRealTimeData](DesignClassDiagrams.md#retrieverealtimedata)

---

## 6. Member Access Control
**Epic:** AC6

### 6.1 Manual Check-In/Out
*   **Description:** Reception interface for logging member entry and exit.
*   **User Stories:** AC6.1, AC6.2
*   **User Flows:** AC6.1, AC6.2 (Dashboard → Check-In/Out)
*   **Priority:** High
*   **Key Capabilities:**
    *   Member verification (Status, Expiry).
    *   Occupancy counter update.
    *   Session duration calculation.
*   **Implementation Brief:**
    *   **Design Perspective:** `AccessController` is the entry point. It uses `AccessPolicyService` to verify if the member is allowed to enter (Active status, not expired). `AttendanceRepository` records the event. `OccupancyService` updates the live count.
    *   **Patterns:**
        *   **Chain of Responsibility (GoF - optional):** `AccessPolicyService` could check a chain of rules (IsActive -> IsNotBanned -> HasPaid).
        *   **Controller (GRASP):** `AccessController`.
    *   **Diagram Reference:** [UC6: Track Member Access - presentIdentification](DesignClassDiagrams.md#presentidentification--exitfacility)

### 6.2 Real-Time Occupancy
*   **Description:** Monitor current facility usage against capacity.
*   **User Stories:** AC6.3
*   **User Flows:** AC6.3 (Dashboard widget)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Percentage display.
    *   Peak hour identification.
    *   Capacity alerts.
*   **Implementation Brief:**
    *   **Design Perspective:** `OccupancyService` is a singleton or scoped service that maintains the current count. It should be thread-safe. `AccessController` updates it, and `OperationsController` reads from it for the dashboard.
    *   **Patterns:**
        *   **Singleton (GoF):** To ensure a single source of truth for the current occupancy count (conceptually, though implementation might vary).
    *   **Diagram Reference:** [UC6: Track Member Access](DesignClassDiagrams.md#uc6-track-member-access)

### 6.3 Attendance History
*   **Description:** Historical record of member visits.
*   **User Stories:** AC6.4
*   **User Flows:** AC6.4 (Member Portal → Attendance)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Visit logs.
    *   Usage statistics.
*   **Implementation Brief:**
    *   **Design Perspective:** Simple retrieval via `AttendanceRepository`. The `AccessController` or a dedicated `AttendanceController` can expose this data.
    *   **Patterns:**
        *   **Repository (DDD):** Standard data access.
    *   **Diagram Reference:** [UC6: Track Member Access](DesignClassDiagrams.md#uc6-track-member-access)

---

## 7. Trainer & Session Management
**Epic:** TM8, TS9 (Derived from Flows)

### 7.1 Trainer Management
*   **Description:** Manage trainer profiles and schedules.
*   **User Flows:** TM8.1, TM8.3
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Create/Update Trainer profiles.
    *   Manage availability slots.
*   **Implementation Brief:**
    *   **Design Perspective:** `TrainerController` manages the lifecycle. `CertificationValidationService` ensures trainers are qualified. `ScheduleService` handles the complexity of time slots and availability.
    *   **Patterns:**
        *   **Controller (GRASP):** `TrainerController`.
    *   **Diagram Reference:** [UC8: Manage Trainer Operations - registerTrainer](DesignClassDiagrams.md#registertrainer)

### 7.2 Session Booking
*   **Description:** Booking system for personal training sessions.
*   **User Flows:** TS9.1 (Staff), TS9.2 (Member)
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Slot selection.
    *   Booking confirmation.
    *   Cancellation/Rescheduling (TS9.3).
*   **Implementation Brief:**
    *   **Design Perspective:** This is a resource allocation problem. `ScheduleService` must ensure no double-booking. It acts as the **Information Expert** for availability.
    *   **Patterns:**
        *   **Information Expert (GRASP):** `ScheduleService` owns the schedule data and logic.
    *   **Diagram Reference:** [UC8: Manage Trainer Operations](DesignClassDiagrams.md#uc8-manage-trainer-operations)

---

## 8. Operations & Configuration
**Epic:** OM7, EM10, OA11, SC12 (Derived from Flows)

### 8.1 Equipment Management
*   **Description:** Track gym equipment and maintenance schedules.
*   **User Flows:** EM10.1, EM10.2
*   **Priority:** Low
*   **Key Capabilities:**
    *   Equipment inventory.
    *   Maintenance scheduling.
*   **Implementation Brief:**
    *   **Design Perspective:** A CRUD interface managed by `EquipmentController`. `MaintenanceService` could schedule alerts for upcoming service dates.
    *   **Patterns:**
        *   **Observer (GoF):** To notify staff when maintenance is due.
    *   **Diagram Reference:** [UC7: Monitor Gym Operations](DesignClassDiagrams.md#uc7-monitor-gym-operations)

### 8.2 Overdue Account Management
*   **Description:** Tools to manage and collect from overdue accounts.
*   **User Flows:** OA11.1, OA11.2, OA11.3
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Identify overdue members.
    *   Send payment reminders.
    *   Create payment plans.
*   **Implementation Brief:**
    *   **Design Perspective:** `BillingItemService` identifies overdue items. `NotificationService` sends the reminders. `PaymentController` handles the setup of payment plans.
    *   **Patterns:**
        *   **Composite (GoF):** To treat a "Payment Plan" (collection of future payments) similarly to a single payment obligation.
    *   **Diagram Reference:** [UC4: Process Member Payments](DesignClassDiagrams.md#uc4-process-member-payments)

### 8.3 System Configuration
*   **Description:** Admin tools for system setup.
*   **User Flows:** SC12.1 - SC12.5
*   **Priority:** Medium
*   **Key Capabilities:**
    *   Membership plan editing.
    *   Payment gateway setup.
    *   User role management.
    *   Audit log viewing.
    *   Notification template editing.
*   **Implementation Brief:**
    *   **Design Perspective:** A `ConfigurationController` allows admins to modify system settings. These settings should be loaded into a `SystemConfig` singleton or injected into services that need them.
    *   **Patterns:**
        *   **Singleton (GoF):** `SystemConfig` to provide global access to configuration settings (or use Dependency Injection for better testability).
    *   **Diagram Reference:** N/A (General System Utility)
