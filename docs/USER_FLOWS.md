# User Flows - Block20 Gym Management System

## Design Principles
- **Maximum 3 clicks to complete any task**
- **Smart defaults**: Pre-fill known information
- **Inline actions**: Minimize navigation between screens
- **Progressive disclosure**: Show only what's needed at each step
- **Contextual shortcuts**: Quick actions from search results and lists

---

## EPIC M1: Member Management

### M1.1 - Create New Member
**Flow**: Dashboard → Members → "+ New Member"
1. Click "+ New Member" button
2. Fill form (Name, Contact, Emergency Contact) → Auto-validate
3. Click "Create Member" → Success toast → Auto-navigate to Member Profile

### M1.2 - Search/View Members
**Flow**: Dashboard → Members Search (inline)
1. Type in search bar (real-time results)
2. Click member card → Member Profile opens

### M1.3 - Update Member Details
**Flow**: Member Profile (from M1.2)
1. Click "✏ Edit" button (inline edit mode)
2. Modify fields → Click "Save" → Success toast

### M1.4 - Freeze/Deactivate Member
**Flow**: Member Profile (from M1.2)
1. Click "⋮ More" → Select "Freeze" or "Deactivate"
2. Confirm modal → Status updates immediately

---

## EPIC E2: Enrollment & Payments

### E2.1 - Enroll New Member
**Flow**: Member Profile → "Enroll in Membership"
1. Click "Enroll" → Plan selection (cards with pricing)
2. Select plan → Review & confirm (auto-calculated dates)
3. Process payment → Success confirmation

### E2.2 - Process Payment
**Flow**: Integrated into E2.1, R3.1, R3.4
1. Enter payment method (card/cash)
2. Click "Process Payment" → Gateway validation
3. Receipt generated automatically

### E2.3 - Generate Receipt
**Flow**: Auto-triggered after E2.2
- Auto-generated → Email sent + Download option shown

### E2.4 - Record Non-Payment
**Flow**: Members → Overdue tab → Member card
1. Click overdue member → See payment status
2. Click "Mark Contacted" → Add note → Save

### E2.5 - Payment Plans
**Flow**: Member Profile → Payments → "+ Payment Plan"
1. Click "+ Payment Plan" → Select installment schedule
2. Confirm → Auto-scheduled reminders set

---

## EPIC R3: Renewals

### R3.1 - Manual Renewal (Staff)
**Flow**: Member Profile → "Renew Membership"
1. Click "Renew" → Pre-filled with current plan
2. Modify if needed → Process payment
3. New membership period activated

### R3.2 - Automatic Reminders
**Flow**: System automated (Config → Notifications)
- No user action: System sends reminders 14, 7, 3 days before expiry

### R3.3 - Track Expiring Memberships
**Flow**: Dashboard → "Expiring Soon" widget
1. Click widget → Filtered member list
2. Click member → Quick renew button

### R3.4 - Member Self-Renewal
**Flow**: Member Portal → Dashboard → "Renew Membership"
1. Click "Renew Now" → Plan selection
2. Review & confirm → Payment
3. Confirmation email sent

---

## EPIC P4: Payments & Billing

### P4.1 - View Payment History
**Flow**: Member Profile → Payments tab
- List auto-loads with filters (Date, Type, Status)

### P4.2 - Refund Payment
**Flow**: Payment History (from P4.1) → Payment row
1. Click payment → "⋮ More" → "Refund"
2. Enter reason → Confirm → Gateway processes

---

## EPIC FR5: Financial Reporting

### FR5.1 - Monthly Revenue Report
**Flow**: Reports → Financial → "Revenue"
1. Select month (defaults to current) → Click "Generate"
2. Report renders → Export button shown

### FR5.2 - Outstanding Balances
**Flow**: Reports → Financial → "Outstanding"
- Auto-generates current data → Export option

### FR5.3 - Member Financial Dashboard
**Flow**: Member Portal → Payments & Billing
- Auto-loads: Payment history, upcoming dues, receipts

---

## EPIC AC6: Access Control

### AC6.1 - Manual Check-In
**Flow**: Dashboard → Check-In/Out widget
1. Scan/enter member ID → Member card appears
2. Click "✓ Check In" → Timestamp recorded

### AC6.2 - Manual Check-Out
**Flow**: Same as AC6.1
- Click "⨯ Check Out" → Session duration calculated

### AC6.3 - View Current Occupancy
**Flow**: Dashboard widget (auto-refresh)
- Real-time count displayed → Click for details

### AC6.4 - Attendance Tracking
**Flow**: Member Portal → Attendance
- Auto-loads calendar view + statistics

---

## EPIC OM7: Operations Management

### OM7.2 - Historical Reports
**Flow**: Reports → Operations → "Attendance History"
1. Select date range → Click "Generate"
2. Charts render → Export options shown

---

## EPIC TM8: Trainer Management

### TM8.1 - Add/Update Trainer
**Flow**: Trainers → "+ New Trainer"
1. Fill form (Name, Specialization, Contact)
2. Click "Add Trainer" → Profile created

### TM8.3 - Trainer Schedules
**Flow**: Trainer Profile → Schedule tab
1. Click "+ Add Availability" → Select time slots
2. Save → Calendar updates

---

## EPIC TS9: Training Sessions

### TS9.1 - Book Session (Staff)
**Flow**: Member Profile → Training → "+ Book Session"
1. Select trainer + date/time → Confirm
2. Member notified automatically

### TS9.2 - Book Session (Member)
**Flow**: Member Portal → Training → "Book Session"
1. Select trainer → See available slots
2. Click slot → Confirm → Notification sent

### TS9.3 - Cancel/Reschedule
**Flow**: Training Sessions list → Session card
1. Click "⋮ More" → "Reschedule" or "Cancel"
2. Select new time (reschedule) → Confirm

---

## EPIC EM10: Equipment Management

### EM10.1 - Add/Update Equipment
**Flow**: Equipment → "+ Add Equipment"
1. Fill form (Name, Type, Purchase Date)
2. Click "Add" → Equipment registered

### EM10.2 - Schedule Maintenance
**Flow**: Equipment → Equipment card → "Schedule Maintenance"
1. Select date + technician → Add notes
2. Save → Reminder set

---

## EPIC OA11: Overdue Accounts

### OA11.1 - Identify Overdue
**Flow**: Dashboard → "Overdue Accounts" widget
- Auto-populates → Click for filtered list

### OA11.2 - Send Reminders
**Flow**: Overdue list → Member card
1. Click "Send Reminder" → Template auto-fills
2. Modify if needed → Send

### OA11.3 - Record Payment Arrangements
**Flow**: Member Profile → Payments → "+ Payment Plan"
- Same as E2.5

---

## EPIC SC12: System Configuration

### SC12.1 - Membership Plans
**Flow**: Configuration → Membership Plans → "✏ Edit Plan"
1. Modify pricing/duration → Save
2. Changes apply to new enrollments only

### SC12.2 - Payment Gateway
**Flow**: Configuration → Integrations → Payment Gateway
1. Enter API credentials → Test connection
2. Save → Gateway activated

### SC12.3 - User Roles
**Flow**: Configuration → Users → User row → "Edit Role"
1. Select role from dropdown → Save
2. Permissions update immediately

### SC12.4 - Audit Logging
**Flow**: Configuration → Audit Logs
- Auto-displays recent activities → Filter/export options

### SC12.5 - Notification Templates
**Flow**: Configuration → Notifications → Template card
1. Click "✏ Edit" → Modify template
2. Preview → Save → Applied to future notifications

---

## Mobile-Specific Flows

### Member Portal (Mobile)
**Navigation**: Bottom nav (Home, Membership, Training, Payments, More)
- All flows above work identically with touch-optimized buttons
- Swipe gestures: Swipe left on session → Cancel/Reschedule

### Staff Portal (Tablet)
**Quick Check-In**: 
1. Home screen → Barcode scanner icon
2. Scan member card → Auto check-in (1 tap)

---

## Flow Optimization Notes

**Reduced Clicks**:
- Check-in: 2 clicks (scan + confirm) vs traditional 4+ clicks
- Enrollment: 3 clicks (plan → review → pay) vs traditional 6+ clicks
- Renewal: 2 clicks (renew → confirm) with smart defaults

**Smart Defaults Applied**:
- Renewal: Pre-fills current plan and contact info
- Payments: Remembers last payment method
- Reports: Default to current month/week
- Training: Shows only available time slots

**Inline Actions**:
- Edit member details without leaving profile
- Process payments from multiple contexts (enrollment, renewal, profile)
- Send reminders directly from overdue list

**Progressive Disclosure**:
- Advanced filters hidden in "⋮ More" menus
- Payment plan details expand on click
- Audit logs load recent 20, "Load more" for history
