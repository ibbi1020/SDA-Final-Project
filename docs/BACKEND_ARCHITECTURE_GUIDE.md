# Beginner's Guide to Backend Architecture (Block20 Gym)

Welcome to the backend! If you've never written backend code before, this guide is for you. We will build the "brain" of the Gym Management System step-by-step.

---

## 1. The Big Picture (Analogy)

Imagine a **Restaurant**:

1.  **The Customer (User)**: Sits at the table and looks at the menu.
2.  **The Waiter (Frontend/Controller)**: Takes the order and brings it to the kitchen. They don't cook; they just pass messages.
3.  **The Chef (Service)**: Receives the order, checks if ingredients are available, and cooks the meal following a recipe (business rules).
4.  **The Pantry (Repository)**: Where raw ingredients (data) are stored. The Chef asks the Pantry for "3 eggs", and the Pantry provides them.
5.  **The Supplier (Database)**: The actual warehouse where all food is kept permanently.

### In our App:
- **Frontend (JavaFX)** = The Waiter (You've already built this!)
- **Service Layer** = The Chef (The brains/logic)
- **Repository Layer** = The Pantry (Talks to the database)
- **Database (SQL)** = The Supplier (Permanent storage)

---

## 2. The 3 Layers You Will Build

We split our code into 3 main layers so that one file doesn't do everything.

### Layer 1: Models (The Data)
**"What does a Member look like?"**
These are simple Java classes that represent your data. If you have a `members` table in your database, you'll have a `Member.java` class.
*   **Location**: `com.block20.models`

### Layer 2: Repositories (The Storage Access)
**"Save this member to the database."**
This is the **ONLY** place where SQL code lives. If you need to save, find, or delete data, you do it here.
*   **Location**: `com.block20.repositories`

### Layer 3: Services (The Logic)
**"Register a new member."**
This is where the "thinking" happens.
Example: "To register a member, first check if their email is unique. If yes, save them. If no, show an error."
*   **Location**: `com.block20.services`

---

## 3. Step-by-Step Implementation Guide

Follow this order to build a feature (e.g., "Member Registration") from scratch.

### Step 1: Create the Model (The Ingredients)
First, define what data you are working with.

**File:** `app/src/main/java/com/block20/models/Member.java`

```java
package com.block20.models;

import java.time.LocalDate;

public class Member {
    // These fields match your Database columns exactly
    private String memberId;
    private String fullName;
    private String email;
    private LocalDate joinDate;

    // Constructor
    public Member(String memberId, String fullName, String email) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.joinDate = LocalDate.now();
    }

    // Getters and Setters (Right-click -> Generate -> Getters and Setters)
    public String getMemberId() { return memberId; }
    public void setMemberId(String id) { this.memberId = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String name) { this.fullName = name; }
    
    // ... add the rest ...
}
```

---

### Step 2: Create the Repository (The Pantry)
Now, write the code to save and load this model from the database.

**Interface:** `com.block20.repositories.MemberRepository.java`
(An interface is just a list of promises: "I promise I can do these things")

```java
public interface MemberRepository {
    void save(Member member);
    Member findById(String id);
    List<Member> findAll();
}
```

**Implementation:** `com.block20.repositories.impl.MemberRepositoryImpl.java`
(This is the actual code that does the work)

```java
public class MemberRepositoryImpl implements MemberRepository {
    private Connection conn; // Your link to the database

    public MemberRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Member member) {
        // SQL query with ? as placeholders (Security best practice!)
        String sql = "INSERT INTO members (member_id, full_name, email) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Fill in the blanks
            stmt.setString(1, member.getMemberId());
            stmt.setString(2, member.getFullName());
            stmt.setString(3, member.getEmail());
            
            // Run the command
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace(); // Print error if something goes wrong
        }
    }
}
```

---

### Step 3: Create the Service (The Chef)
Now, write the business logic.

**Interface:** `com.block20.services.MemberService.java`

```java
public interface MemberService {
    void registerMember(String name, String email);
}
```

**Implementation:** `com.block20.services.impl.MemberServiceImpl.java`

```java
public class MemberServiceImpl implements MemberService {
    private MemberRepository memberRepo; // We need the pantry!

    public MemberServiceImpl(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    @Override
    public void registerMember(String name, String email) {
        // 1. Business Logic: Validate input
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address!");
        }

        // 2. Create the data object
        String newId = "MEM" + System.currentTimeMillis(); // Generate ID
        Member newMember = new Member(newId, name, email);

        // 3. Save to database (using Repository)
        memberRepo.save(newMember);
        
        System.out.println("Member registered successfully!");
    }
}
```

---

### Step 4: Connect to Frontend (The Waiter)
Finally, use the Service in your JavaFX Controller.

**File:** `com.block20.controllers.MemberController.java`

```java
public class MemberController {
    // The controller talks to the Service (The Chef)
    private MemberService memberService;

    // We pass the service in (Dependency Injection)
    public MemberController(MemberService service) {
        this.memberService = service;
    }

    // This runs when user clicks "Register" button
    public void onRegisterButtonClick() {
        String name = nameField.getText();
        String email = emailField.getText();

        try {
            // Call the backend!
            memberService.registerMember(name, email);
            showSuccessMessage("User saved!");
            
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }
}
```

---

## 4. How to "Wire" It All Together
You need a place to start the engine. This usually happens in your main `App.java`.

```java
public class App extends Application {
    @Override
    public void start(Stage stage) {
        // 1. Connect to Database
        Connection conn = DatabaseConnection.getConnection();

        // 2. Create Repository (Give it the connection)
        MemberRepository memberRepo = new MemberRepositoryImpl(conn);

        // 3. Create Service (Give it the repository)
        MemberService memberService = new MemberServiceImpl(memberRepo);

        // 4. Create Controller (Give it the service)
        MemberController controller = new MemberController(memberService);

        // 5. Show the GUI
        Scene scene = new Scene(controller);
        stage.setScene(scene);
        stage.show();
    }
}
```

---

## 5. Common Beginner Questions

### Q: Why so many files? Why not just write SQL in the Controller?
**A:** It seems easier at first, but it becomes a nightmare quickly.
- If you change your database from SQLite to MySQL, you'd have to rewrite every Controller.
- With layers, you only change the **Repository**. The rest of the app doesn't even know the database changed!

### Q: What is a DTO?
**A:** DTO stands for **Data Transfer Object**.
Think of it as a "Form".
- The `Member` model is your permanent record.
- A `MemberRegistrationDTO` is the form the user fills out.
- Sometimes they are the same, but often the Form has fields like "Confirm Password" that you don't save to the database.

### Q: What is Dependency Injection?
**A:** It's a fancy term for "Passing variables via the Constructor".
Instead of creating a `new MemberRepository()` inside the Service, you pass it in.
- **Bad:** `MemberRepository repo = new MemberRepository();` (Hard to test)
- **Good:** `public MemberService(MemberRepository repo) { ... }` (Flexible)

---

## 6. Checklist for Your First Feature

1.  [ ] **Database**: Create the table in your database (using SQL).
2.  [ ] **Model**: Create the Java class that matches the table.
3.  [ ] **Repository**: Write the interface and implementation to save/load the model.
4.  [ ] **Service**: Write the business logic (validations, calculations).
5.  [ ] **Controller**: Connect your buttons to the Service methods.
6.  [ ] **App.java**: Wire everything together in the `start()` method.

**You got this! Start with one simple feature (like "Add Member") and get it working end-to-end.**
