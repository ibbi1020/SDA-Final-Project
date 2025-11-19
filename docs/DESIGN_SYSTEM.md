# Block20 Gym Management System - Design System

**Version:** 1.0  
**Last Updated:** November 12, 2025  
**Application Type:** Desktop Application (JavaFX)  
**Target Resolution:** 1920x1080 (optimized for 1366x768 minimum)

---

## Design Review Checklist

Before implementing any new UI component, verify:
- [ ] Uses colors from defined palette
- [ ] Follows spacing system (4px increments)
- [ ] Typography matches scale
- [ ] Includes all interactive states
- [ ] Has proper focus indicators
- [ ] Meets accessibility contrast requirements
- [ ] Follows animation guidelines
- [ ] Matches existing component patterns

---

## Quick Reference Card

### Most Common Values

```
COLORS:
  Primary: #2563EB
  Success: #10B981
  Warning: #F59E0B
  Error: #EF4444
  Text: #4B5563
  Background: #F9FAFB

SPACING:
  XS: 8px
  SM: 12px
  MD: 16px
  LG: 24px
  XL: 32px

TYPOGRAPHY:
  H1: 36px/700
  H2: 28px/600
  Body: 14px/400
  Caption: 12px/400

BORDER RADIUS:
  Small: 4px
  Medium: 6px
  Large: 8px
  XLarge: 12px

SHADOWS:
  Card: 0 1px 3px rgba(0,0,0,0.05)
  Elevated: 0 4px 6px rgba(0,0,0,0.07)
  Modal: 0 20px 25px rgba(0,0,0,0.1)
```

## Table of Contents
1. [Design Philosophy](#design-philosophy)
2. [Design Direction](#design-direction)
3. [Color Palette](#color-palette)
4. [Typography](#typography)
5. [Spacing System](#spacing-system)
6. [Layout Grid](#layout-grid)
7. [Component Specifications](#component-specifications)
8. [Interactive States](#interactive-states)
9. [Icons & Imagery](#icons--imagery)
10. [Motion & Animation](#motion--animation)
11. [Accessibility Guidelines](#accessibility-guidelines)
12. [JavaFX Implementation Notes](#javafx-implementation-notes)

---

## Design Philosophy

### Core Principles

**1. Clarity Over Cleverness**
- Every interface element serves a clear purpose
- No decorative elements that don't aid comprehension
- Information hierarchy is immediately apparent

**2. Efficiency in Action**
- Maximum 5 clicks to complete any task
- Frequent actions prominently displayed
- Quick actions accessible from any context

**3. Progressive Disclosure**
- Show only what's needed at each step
- Advanced options hidden behind clear toggles
- Contextual help available but not intrusive

**4. Professional Yet Approachable**
- Clean, modern aesthetic suitable for business environment
- Friendly enough for gym members to feel comfortable
- Conveys trust, reliability, and competence

**5. Consistent Mental Models**
- Same actions work the same way everywhere
- Navigation patterns remain predictable
- Visual language remains uniform across portals

---

## Design Direction

### Visual Style: **Modern Fitness Professional**

**Aesthetic Keywords:**
- Clean, Spacious, Energetic, Reliable, Modern, Confident

**Inspiration:**
- Contemporary SaaS dashboards (Stripe, Linear, Notion)
- Fitness brand aesthetics (Nike Training Club, Peloton)
- Professional business tools (Salesforce, HubSpot)

**NOT:**
- Overly sporty/athletic (avoid aggressive fitness aesthetics)
- Minimalist to the point of confusion
- Corporate/boring (avoid gray-on-gray enterprise look)

### Interface Personality

**Staff Portal:** Professional, efficient, data-rich  
**Member Portal:** Motivating, personal, achievement-focused

---

## Color Palette

### Primary Colors

```css
/* Primary Brand Color - Energetic Blue */
--primary-500: #2563EB;        /* Main interactive elements */
--primary-600: #1D4ED8;        /* Hover states */
--primary-700: #1E40AF;        /* Active/pressed states */
--primary-400: #3B82F6;        /* Lighter accent */
--primary-300: #81b7faff;        /* Disabled state */

/* Primary Dark Variant - Deep Navy (used in headers) */
--primary-dark: #1E293B;       /* Top navigation, sidebars */
--primary-darker: #0F172A;     /* Footer, deep backgrounds */
```

### Secondary Colors

```css
/* Success - Vibrant Green */
--success-500: #10B981;        /* Success messages, active status */
--success-600: #059669;        /* Hover on success buttons */
--success-100: #D1FAE5;        /* Success background */

/* Warning - Amber */
--warning-500: #F59E0B;        /* Warning messages, expiring soon */
--warning-600: #D97706;        /* Hover state */
--warning-100: #FEF3C7;        /* Warning background */

/* Error - Vibrant Red */
--error-500: #EF4444;          /* Error messages, overdue status */
--error-600: #DC2626;          /* Hover on error buttons */
--error-100: #FEE2E2;          /* Error background */

/* Info - Cyan */
--info-500: #06B6D4;           /* Info messages, tips */
--info-600: #0891B2;           /* Hover state */
--info-100: #CFFAFE;           /* Info background */
```

### Neutral Palette

```css
/* Grayscale for text and backgrounds */
--gray-50: #F9FAFB;            /* Page background */
--gray-100: #F3F4F6;           /* Card background, subtle dividers */
--gray-200: #E5E7EB;           /* Borders, dividers */
--gray-300: #D1D5DB;           /* Disabled text, placeholder */
--gray-400: #9CA3AF;           /* Secondary text, icons */
--gray-500: #6B7280;           /* Body text secondary */
--gray-600: #4B5563;           /* Body text primary */
--gray-700: #374151;           /* Headings */
--gray-800: #1F2937;           /* Strong headings */
--gray-900: #111827;           /* High emphasis text */
```

### Usage Guidelines

| Element | Color | Token |
|---------|-------|-------|
| Primary action buttons | Blue | `--primary-500` |
| Page backgrounds | Light gray | `--gray-50` |
| Card/panel backgrounds | White | `#FFFFFF` |
| Headings | Dark gray | `--gray-800` |
| Body text | Medium gray | `--gray-600` |
| Secondary text | Light gray | `--gray-500` |
| Borders/dividers | Light gray | `--gray-200` |
| Top navigation bar | Dark navy | `--primary-dark` |
| Sidebar (collapsed state) | Dark navy | `--primary-dark` |
| Active membership status | Green | `--success-500` |
| Expiring soon | Orange | `--warning-500` |
| Overdue/inactive | Red | `--error-500` |

---

## Typography

### Font Families

**Primary Font: Inter**
- **Reason:** Excellent screen readability, professional, modern
- **Fallback:** `-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif`
- **Usage:** All UI text, headings, body copy, buttons

**Monospace Font: JetBrains Mono**
- **Fallback:** `"Courier New", Consolas, monospace`
- **Usage:** Member IDs, transaction codes, technical data

### Type Scale

```css
/* Display - Large marketing/hero text */
--text-display: 48px / 56px;        /* Size / Line Height */
--font-weight-display: 700;         /* Bold */

/* H1 - Page titles */
--text-h1: 36px / 44px;
--font-weight-h1: 700;

/* H2 - Section headings */
--text-h2: 28px / 36px;
--font-weight-h2: 600;              /* Semibold */

/* H3 - Subsection headings */
--text-h3: 22px / 30px;
--font-weight-h3: 600;

/* H4 - Card titles */
--text-h4: 18px / 26px;
--font-weight-h4: 600;

/* Body Large - Emphasized body text */
--text-body-lg: 16px / 24px;
--font-weight-body-lg: 400;         /* Regular */

/* Body - Default body text */
--text-body: 14px / 22px;
--font-weight-body: 400;

/* Body Small - Secondary information */
--text-body-sm: 13px / 20px;
--font-weight-body-sm: 400;

/* Caption - Labels, helper text */
--text-caption: 12px / 18px;
--font-weight-caption: 400;

/* Overline - All caps labels */
--text-overline: 11px / 16px;
--font-weight-overline: 600;
--text-transform: uppercase;
--letter-spacing: 0.5px;

/* Button Text */
--text-button: 14px / 20px;
--font-weight-button: 500;          /* Medium */
```

### Typography Usage Map

| Element | Type Style | Color | Example |
|---------|-----------|--------|---------|
| Page title | H1 | `--gray-900` | "Member Management" |
| Section title | H2 | `--gray-800` | "Active Members" |
| Card title | H4 | `--gray-800` | "Monthly Revenue" |
| Body text | Body | `--gray-600` | Description text |
| Secondary info | Body Small | `--gray-500` | "Last updated 2 hours ago" |
| Input labels | Body Small | `--gray-700` | "Email Address" |
| Button text | Button | `#FFFFFF` | "Save Changes" |
| Table headers | Overline | `--gray-500` | "MEMBER NAME" |
| Helper text | Caption | `--gray-500` | "Maximum 50 characters" |
| Error message | Body Small | `--error-600` | "This field is required" |

---

## Spacing System

### Base Unit: 4px

All spacing follows a consistent 4px base unit system for mathematical harmony.

```css
/* Spacing Scale */
--space-1: 4px;      /* 0.25rem */
--space-2: 8px;      /* 0.5rem */
--space-3: 12px;     /* 0.75rem */
--space-4: 16px;     /* 1rem */    [BASE UNIT]
--space-5: 20px;     /* 1.25rem */
--space-6: 24px;     /* 1.5rem */
--space-8: 32px;     /* 2rem */
--space-10: 40px;    /* 2.5rem */
--space-12: 48px;    /* 3rem */
--space-16: 64px;    /* 4rem */
--space-20: 80px;    /* 5rem */
--space-24: 96px;    /* 6rem */
```

### Spacing Usage Guidelines

#### Component Internal Spacing

| Component | Padding | Gap Between Items |
|-----------|---------|-------------------|
| Button | `12px 20px` | - |
| Button (small) | `8px 16px` | - |
| Button (large) | `16px 28px` | - |
| Input field | `12px 16px` | - |
| Card | `24px` | - |
| Card (compact) | `16px` | - |
| Modal | `32px` | - |
| Dropdown item | `10px 16px` | - |
| Table cell | `12px 16px` | - |
| List item | `12px 16px` | `8px` |

#### Layout Spacing

| Context | Spacing | Usage |
|---------|---------|-------|
| Between form fields | `16px` | Vertical gap |
| Between form sections | `32px` | Visual grouping |
| Between cards in grid | `24px` | Consistent grid gap |
| Page content padding | `32px` | Desktop view |
| Page content padding | `24px` | Tablet view |
| Sidebar padding | `24px` | Internal padding |
| Section margins | `48px` | Vertical rhythm |
| Dashboard widgets | `24px` | Grid gap |

---

## Layout Grid

### Page Layout Structure

```
┌─────────────────────────────────────────────────────────┐
│  Top Navigation Bar (Height: 64px)                      │
├─────────┬───────────────────────────────────────────────┤
│         │                                               │
│ Sidebar │  Main Content Area                           │
│         │                                               │
│ 260px   │  Max-width: 1400px                           │
│ (fixed) │  Padding: 32px                               │
│         │                                               │
│         │                                               │
└─────────┴───────────────────────────────────────────────┘
```

### Grid System: 12-Column

```css
/* Container */
--container-max-width: 1400px;
--container-padding: 32px;

/* 12-column grid */
--grid-columns: 12;
--grid-gap: 24px;

/* Column widths (in 12-column grid) */
--col-1: 8.333%;
--col-2: 16.666%;
--col-3: 25%;
--col-4: 33.333%;
--col-6: 50%;
--col-8: 66.666%;
--col-9: 75%;
--col-12: 100%;
```

### Common Layout Patterns

#### Dashboard Cards (3-column)
```
┌──────────────┬──────────────┬──────────────┐
│  Card        │  Card        │  Card        │
│  (col-4)     │  (col-4)     │  (col-4)     │
│              │              │              │
└──────────────┴──────────────┴──────────────┘
```

#### Form Layout (2-column)
```
┌──────────────────────┬──────────────────────┐
│  Input (col-6)       │  Input (col-6)       │
├──────────────────────┴──────────────────────┤
│  Input (col-12)                             │
├─────────────────────────────────────────────┤
│  Textarea (col-12)                          │
└─────────────────────────────────────────────┘
```

#### Detail View (Sidebar + Content)
```
┌──────────┬────────────────────────────────┐
│          │                                │
│ Sidebar  │  Main Content                 │
│ (col-3)  │  (col-9)                      │
│          │                                │
└──────────┴────────────────────────────────┘
```

---

## Component Specifications

### Buttons

#### Primary Button
```css
/* Visual Properties */
background-color: var(--primary-500);
color: #FFFFFF;
font-size: 14px;
font-weight: 500;
padding: 12px 20px;
border-radius: 6px;
border: none;
box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
cursor: pointer;

/* States */
hover: {
  background-color: var(--primary-600);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

active: {
  background-color: var(--primary-700);
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

disabled: {
  background-color: var(--gray-300);
  cursor: not-allowed;
  opacity: 0.6;
}

focus: {
  outline: 2px solid var(--primary-400);
  outline-offset: 2px;
}
```

#### Secondary Button
```css
background-color: #FFFFFF;
color: var(--gray-700);
border: 1px solid var(--gray-300);
/* Other properties same as primary */

hover: {
  background-color: var(--gray-50);
  border-color: var(--gray-400);
}
```

#### Destructive Button
```css
background-color: var(--error-500);
color: #FFFFFF;
/* Other properties same as primary */

hover: {
  background-color: var(--error-600);
}
```

#### Button Sizes
```css
/* Small */
padding: 8px 16px;
font-size: 13px;
border-radius: 4px;

/* Medium (default) */
padding: 12px 20px;
font-size: 14px;
border-radius: 6px;

/* Large */
padding: 16px 28px;
font-size: 16px;
border-radius: 8px;
```

#### Icon Buttons
```css
/* Circular icon button */
width: 40px;
height: 40px;
padding: 0;
border-radius: 50%;
display: flex;
align-items: center;
justify-content: center;
```

---

### Input Fields

#### Text Input
```css
/* Visual Properties */
background-color: #FFFFFF;
border: 1px solid var(--gray-300);
border-radius: 6px;
padding: 12px 16px;
font-size: 14px;
color: var(--gray-900);
width: 100%;
height: 44px;

/* States */
focus: {
  border-color: var(--primary-500);
  outline: none;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

error: {
  border-color: var(--error-500);
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

disabled: {
  background-color: var(--gray-100);
  color: var(--gray-500);
  cursor: not-allowed;
}

placeholder: {
  color: var(--gray-400);
  font-style: italic;
}
```

#### Input Label
```css
display: block;
font-size: 13px;
font-weight: 500;
color: var(--gray-700);
margin-bottom: 6px;
```

#### Input Helper Text
```css
font-size: 12px;
color: var(--gray-500);
margin-top: 6px;
```

#### Input Error Message
```css
font-size: 12px;
color: var(--error-600);
margin-top: 6px;
display: flex;
align-items: center;
gap: 4px;
```

---

### Cards

#### Standard Card
```css
background-color: #FFFFFF;
border: 1px solid var(--gray-200);
border-radius: 8px;
padding: 24px;
box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

hover: {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07);
}
```

#### Dashboard Stat Card
```css
/* Same as standard card */
min-height: 140px;
display: flex;
flex-direction: column;
justify-content: space-between;

/* Header */
.card-label {
  font-size: 13px;
  color: var(--gray-500);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Value */
.card-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--gray-900);
  margin: 8px 0;
}

/* Footer/Change */
.card-footer {
  font-size: 13px;
  color: var(--gray-600);
  display: flex;
  align-items: center;
  gap: 4px;
}
```

#### Clickable Card
```css
/* Same as standard card */
cursor: pointer;
transition: all 0.2s ease;

hover: {
  border-color: var(--primary-500);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
  transform: translateY(-2px);
}
```

---

### Tables

#### Table Container
```css
background-color: #FFFFFF;
border: 1px solid var(--gray-200);
border-radius: 8px;
overflow: hidden;
```

#### Table Header
```css
background-color: var(--gray-50);
border-bottom: 1px solid var(--gray-200);

th {
  padding: 12px 16px;
  text-align: left;
  font-size: 11px;
  font-weight: 600;
  color: var(--gray-500);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
```

#### Table Body
```css
td {
  padding: 12px 16px;
  border-bottom: 1px solid var(--gray-100);
  font-size: 14px;
  color: var(--gray-700);
}

tr:last-child td {
  border-bottom: none;
}

tr:hover {
  background-color: var(--gray-50);
}
```

#### Table Actions Column
```css
/* Right-aligned action buttons */
text-align: right;
width: 120px;

button {
  margin-left: 8px;
}
```

---

### Modals/Dialogs

#### Modal Overlay
```css
position: fixed;
top: 0;
left: 0;
right: 0;
bottom: 0;
background-color: rgba(0, 0, 0, 0.5);
display: flex;
align-items: center;
justify-content: center;
z-index: 1000;
```

#### Modal Container
```css
background-color: #FFFFFF;
border-radius: 12px;
box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1),
            0 10px 10px -5px rgba(0, 0, 0, 0.04);
max-width: 600px;
width: 90%;
max-height: 90vh;
overflow-y: auto;
```

#### Modal Header
```css
padding: 24px 32px;
border-bottom: 1px solid var(--gray-200);

h2 {
  font-size: 22px;
  font-weight: 600;
  color: var(--gray-900);
}
```

#### Modal Body
```css
padding: 32px;
```

#### Modal Footer
```css
padding: 16px 32px;
border-top: 1px solid var(--gray-200);
display: flex;
justify-content: flex-end;
gap: 12px;
```

---

### Navigation

#### Top Navigation Bar
```css
height: 64px;
background-color: var(--primary-dark);
color: #FFFFFF;
display: flex;
align-items: center;
justify-content: space-between;
padding: 0 24px;
box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
position: fixed;
top: 0;
left: 0;
right: 0;
z-index: 100;
```

#### Logo Area
```css
display: flex;
align-items: center;
gap: 12px;
font-size: 20px;
font-weight: 700;
```

#### Navigation Links
```css
display: flex;
gap: 32px;

a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.2s;
}

a:hover {
  color: #FFFFFF;
  background-color: rgba(255, 255, 255, 0.1);
}

a.active {
  color: #FFFFFF;
  background-color: rgba(255, 255, 255, 0.15);
}
```

#### Sidebar Navigation
```css
width: 260px;
background-color: #FFFFFF;
border-right: 1px solid var(--gray-200);
padding: 24px 0;
position: fixed;
top: 64px;
left: 0;
bottom: 0;
overflow-y: auto;
```

#### Sidebar Section
```css
margin-bottom: 24px;

.section-label {
  padding: 0 24px;
  font-size: 11px;
  font-weight: 600;
  color: var(--gray-500);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}
```

#### Sidebar Link
```css
display: flex;
align-items: center;
gap: 12px;
padding: 10px 24px;
color: var(--gray-700);
text-decoration: none;
font-size: 14px;
font-weight: 500;
transition: all 0.2s;

icon {
  width: 20px;
  height: 20px;
  color: var(--gray-400);
}

&:hover {
  background-color: var(--gray-50);
  color: var(--gray-900);
  
  icon {
    color: var(--primary-500);
  }
}

&.active {
  background-color: var(--primary-50);
  color: var(--primary-700);
  border-left: 3px solid var(--primary-500);
  
  icon {
    color: var(--primary-500);
  }
}
```

---

### Badges & Status Indicators

#### Badge Base
```css
display: inline-flex;
align-items: center;
padding: 4px 10px;
border-radius: 12px;
font-size: 12px;
font-weight: 500;
```

#### Status Badges
```css
/* Success/Active */
.badge-success {
  background-color: var(--success-100);
  color: var(--success-700);
}

/* Warning */
.badge-warning {
  background-color: var(--warning-100);
  color: var(--warning-700);
}

/* Error/Inactive */
.badge-error {
  background-color: var(--error-100);
  color: var(--error-700);
}

/* Info */
.badge-info {
  background-color: var(--info-100);
  color: var(--info-700);
}

/* Neutral */
.badge-neutral {
  background-color: var(--gray-100);
  color: var(--gray-700);
}
```

#### Dot Indicator
```css
width: 8px;
height: 8px;
border-radius: 50%;
display: inline-block;
margin-right: 6px;

/* Color matches badge type */
.dot-success { background-color: var(--success-500); }
.dot-warning { background-color: var(--warning-500); }
.dot-error { background-color: var(--error-500); }
```

---

### Alerts & Notifications

#### Alert Container
```css
padding: 16px 20px;
border-radius: 8px;
display: flex;
align-items: start;
gap: 12px;
margin-bottom: 16px;
```

#### Alert Types
```css
/* Success */
.alert-success {
  background-color: var(--success-100);
  border-left: 4px solid var(--success-500);
  color: var(--success-800);
}

/* Warning */
.alert-warning {
  background-color: var(--warning-100);
  border-left: 4px solid var(--warning-500);
  color: var(--warning-800);
}

/* Error */
.alert-error {
  background-color: var(--error-100);
  border-left: 4px solid var(--error-500);
  color: var(--error-800);
}

/* Info */
.alert-info {
  background-color: var(--info-100);
  border-left: 4px solid var(--info-500);
  color: var(--info-800);
}
```

#### Toast Notification
```css
position: fixed;
bottom: 24px;
right: 24px;
min-width: 300px;
max-width: 500px;
background-color: var(--gray-900);
color: #FFFFFF;
padding: 16px 20px;
border-radius: 8px;
box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
z-index: 9999;
animation: slideInUp 0.3s ease;

@keyframes slideInUp {
  from {
    transform: translateY(100px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
```

---

### Dropdowns

#### Dropdown Button
```css
/* Same as secondary button */
display: flex;
align-items: center;
justify-content: space-between;
gap: 8px;
```

#### Dropdown Menu
```css
position: absolute;
top: 100%;
margin-top: 4px;
min-width: 200px;
background-color: #FFFFFF;
border: 1px solid var(--gray-200);
border-radius: 8px;
box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
padding: 8px 0;
z-index: 1000;
```

#### Dropdown Item
```css
padding: 10px 16px;
font-size: 14px;
color: var(--gray-700);
cursor: pointer;
transition: background-color 0.15s;

&:hover {
  background-color: var(--gray-50);
  color: var(--gray-900);
}

&.active {
  background-color: var(--primary-50);
  color: var(--primary-700);
}

&.destructive {
  color: var(--error-600);
}
```

---

### Search Bar

```css
position: relative;
width: 100%;
max-width: 400px;

input {
  width: 100%;
  padding: 10px 16px 10px 42px;
  border: 1px solid var(--gray-300);
  border-radius: 8px;
  font-size: 14px;
  background-color: #FFFFFF;
  
  &:focus {
    border-color: var(--primary-500);
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  }
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--gray-400);
}
```

---

## Interactive States

### State Definitions

```css
/* Default State */
transition: all 0.2s ease;

/* Hover State */
cursor: pointer;
/* Add subtle elevation or color change */

/* Active/Pressed State */
/* Add inset shadow or darker color */
transform: scale(0.98);

/* Focus State (keyboard navigation) */
outline: 2px solid var(--primary-500);
outline-offset: 2px;

/* Disabled State */
opacity: 0.6;
cursor: not-allowed;
pointer-events: none;

/* Loading State */
cursor: wait;
opacity: 0.7;
/* Add spinner animation */
```

### Transition Timings

```css
--transition-fast: 0.15s;        /* Hover effects */
--transition-normal: 0.2s;       /* Most interactions */
--transition-slow: 0.3s;         /* Modals, page transitions */
--transition-very-slow: 0.5s;    /* Complex animations */

/* Easing functions */
--ease-out: cubic-bezier(0.33, 1, 0.68, 1);
--ease-in: cubic-bezier(0.32, 0, 0.67, 0);
--ease-in-out: cubic-bezier(0.65, 0, 0.35, 1);
```

---

## Icons & Imagery

### Icon System

**Icon Library:** Material Symbols (Outlined variant)  
**Alternative:** Feather Icons, Lucide Icons

**Icon Sizes:**
```css
--icon-xs: 16px;      /* Inline with text */
--icon-sm: 20px;      /* Buttons, navigation */
--icon-md: 24px;      /* Standard UI icons */
--icon-lg: 32px;      /* Feature icons */
--icon-xl: 48px;      /* Hero icons */
```

**Icon Colors:**
```css
/* Primary icons */
color: var(--gray-500);

/* Active/hover icons */
color: var(--primary-500);

/* Secondary icons */
color: var(--gray-400);

/* Icons in colored contexts */
/* Match parent color or use white */
```

### Common Icons Usage

| Icon | Context | Size |
|------|---------|------|
| ➕ Add | Create buttons | 20px |
| ✏️ Edit | Edit buttons | 20px |
| 🗑️ Delete | Delete buttons | 20px |
| 👤 User | User profile, members | 20px |
| 📊 Chart | Reports, analytics | 20px |
| ⚙️ Settings | Configuration | 20px |
| 🔔 Notification | Alerts | 20px |
| 🔍 Search | Search inputs | 18px |
| ✓ Check | Success, completion | 20px |
| ✕ Close | Close buttons, errors | 20px |
| ⋮ More | Context menus | 20px |

### Member Profile Images

```css
.avatar {
  border-radius: 50%;
  background-color: var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: var(--gray-600);
}

/* Sizes */
.avatar-sm { width: 32px; height: 32px; font-size: 14px; }
.avatar-md { width: 40px; height: 40px; font-size: 16px; }
.avatar-lg { width: 64px; height: 64px; font-size: 24px; }
.avatar-xl { width: 96px; height: 96px; font-size: 36px; }
```

---

## Motion & Animation

### Animation Principles

1. **Purposeful**: Only animate to communicate state or draw attention
2. **Quick**: Most animations should be 150-300ms
3. **Natural**: Use easing functions that feel realistic
4. **Consistent**: Same interaction = same animation

### Standard Animations

#### Fade In
```css
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

animation: fadeIn 0.2s ease-out;
```

#### Slide In (from right)
```css
@keyframes slideInRight {
  from {
    transform: translateX(20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

animation: slideInRight 0.3s ease-out;
```

#### Scale In (for modals)
```css
@keyframes scaleIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

animation: scaleIn 0.2s ease-out;
```

#### Shake (for errors)
```css
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-4px); }
  20%, 40%, 60%, 80% { transform: translateX(4px); }
}

animation: shake 0.4s ease-in-out;
```

#### Loading Spinner
```css
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

animation: spin 1s linear infinite;
```

### When to Animate

✅ **Do animate:**
- Modal/dialog appearance
- Toast notifications
- Page transitions
- Loading states
- Hover feedback
- Status changes

❌ **Don't animate:**
- Static text
- Data tables (except loading)
- Form inputs (except validation feedback)
- Frequently repeated interactions

---

## Accessibility Guidelines

### Color Contrast

**WCAG 2.1 Level AA Compliance Required**

```css
/* Minimum contrast ratios */
Body text (14px+): 4.5:1
Large text (18px+ or 14px bold): 3:1
UI components: 3:1
```

**All color combinations in this design system meet these requirements.**

### Keyboard Navigation

#### Tab Order
- Logical flow: top to bottom, left to right
- Skip to main content link at top
- All interactive elements accessible via Tab
- Modal traps focus until closed

#### Focus Indicators
```css
/* Visible focus ring on all interactive elements */
*:focus {
  outline: 2px solid var(--primary-500);
  outline-offset: 2px;
}

/* Never remove focus indicators */
```

#### Keyboard Shortcuts

| Key | Action |
|-----|--------|
| Enter | Activate button/link |
| Space | Activate button, toggle checkbox |
| Esc | Close modal/dropdown |
| Arrow keys | Navigate lists/menus |
| Tab | Move to next element |
| Shift+Tab | Move to previous element |

### Screen Reader Support

```css
/* Visually hidden but available to screen readers */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
```

**Requirements:**
- All images have alt text
- Form inputs have labels (not just placeholders)
- Buttons have descriptive text (not just icons)
- Status changes announced with ARIA live regions
- Complex widgets have proper ARIA roles

---

## JavaFX Implementation Notes

### CSS Variables in JavaFX

JavaFX doesn't natively support CSS variables like web browsers. Use looked-up colors instead:

```css
/* Define in root stylesheet */
* {
  -fx-primary-500: #2563EB;
  -fx-gray-600: #4B5563;
  /* ... other colors */
}

/* Reference in components */
.button-primary {
  -fx-background-color: -fx-primary-500;
  -fx-text-fill: white;
}
```

### Font Loading

```java
// Load Inter font in Application.java
Font.loadFont(
  getClass().getResourceAsStream("/fonts/Inter-Regular.ttf"), 
  14
);
Font.loadFont(
  getClass().getResourceAsStream("/fonts/Inter-SemiBold.ttf"), 
  14
);
Font.loadFont(
  getClass().getResourceAsStream("/fonts/Inter-Bold.ttf"), 
  14
);
```

### FXML Styling

```xml
<!-- Apply style classes in FXML -->
<Button text="Save" styleClass="button-primary" />
<Label text="Error message" styleClass="text-error" />
<VBox spacing="16" styleClass="card" />
```

### Common Style Classes

Create these reusable style classes in your main stylesheet:

```css
/* Typography */
.text-h1 { -fx-font-size: 36px; -fx-font-weight: bold; }
.text-h2 { -fx-font-size: 28px; -fx-font-weight: 600; }
.text-body { -fx-font-size: 14px; }
.text-caption { -fx-font-size: 12px; -fx-text-fill: -fx-gray-500; }

/* Buttons */
.button-primary { -fx-background-color: -fx-primary-500; -fx-text-fill: white; }
.button-secondary { -fx-background-color: white; -fx-border-color: -fx-gray-300; }
.button-destructive { -fx-background-color: -fx-error-500; -fx-text-fill: white; }

/* Cards */
.card {
  -fx-background-color: white;
  -fx-border-color: -fx-gray-200;
  -fx-border-radius: 8px;
  -fx-background-radius: 8px;
  -fx-padding: 24px;
}

/* Form inputs */
.text-field {
  -fx-border-color: -fx-gray-300;
  -fx-border-radius: 6px;
  -fx-background-radius: 6px;
  -fx-padding: 12px 16px;
}
```

---

## Design System Maintenance

### Version Control
- This document is version controlled in `.github/DESIGN_SYSTEM.md`
- All changes require review before merging
- Major changes increment version number

### Design Tokens Export
Future implementation: Export design tokens to JSON for programmatic access:

```json
{
  "colors": {
    "primary": {
      "500": "#2563EB"
    }
  },
  "spacing": {
    "4": "16px"
  }
}
```

---