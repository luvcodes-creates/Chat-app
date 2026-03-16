# Chat Application - Complete Project Documentation

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Features](#features)
4. [Technical Specifications](#technical-specifications)
5. [Advantages](#advantages)
6. [Disadvantages & Limitations](#disadvantages--limitations)
7. [Security Features](#security-features)
8. [Database Schema](#database-schema)
9. [Communication Protocol](#communication-protocol)
10. [Usage Instructions](#usage-instructions)
11. [Future Enhancements](#future-enhancements)

---

## 🎯 Project Overview

**Chat Application** is a real-time multi-user chat system built with Java. It features a client-server architecture where multiple clients can connect to a central server and exchange messages in real-time. The application uses MySQL for persistent user storage and authentication.

**Type:** Desktop Application  
**Language:** Java  
**Database:** MySQL (via XAMPP)  
**UI Framework:** Java Swing  
**Communication:** TCP/IP Sockets  
**Architecture:** Client-Server

---

## 🏗️ Architecture

### System Architecture
```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Client 1  │         │   Client 2  │         │   Client N  │
│  (Swing UI) │         │  (Swing UI) │         │  (Swing UI) │
└──────┬──────┘         └──────┬──────┘         └──────┬──────┘
       │                       │                         │
       │    TCP/IP Socket      │    TCP/IP Socket        │
       │    Communication      │    Communication        │
       │                       │                         │
       └───────────────────────┼─────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │   ChatServer        │
                    │  (Port 6001)        │
                    │  - Thread Pool       │
                    │  - Client Manager    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   ClientHandler     │
                    │  (Per Connection)   │
                    │  - Authentication    │
                    │  - Message Routing  │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   UserDB            │
                    │  - User Management  │
                    │  - Authentication   │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   MySQL Database    │
                    │   (XAMPP)           │
                    │   - Users Table     │
                    │   - Messages Table   │
                    │   - Sessions Table  │
                    └─────────────────────┘
```

### Component Breakdown

#### **Server Side:**
1. **ChatServer.java** - Main server application
   - Manages server socket
   - Handles client connections
   - Thread pool management
   - Client capacity control

2. **ClientHandler.java** - Per-client connection handler
   - Authentication (LOGIN/REGISTER)
   - Message receiving and broadcasting
   - Connection lifecycle management

3. **UserDB.java** - User database operations
   - User registration
   - User authentication
   - Password hashing (SHA-256 with salt)

4. **DatabaseManager.java** - MySQL connection management
   - Database connection pooling
   - Connection configuration

5. **MessageDB.java** - Message storage and retrieval
   - Save messages to database
   - Retrieve message history
   - Message persistence management

#### **Client Side:**
1. **ChatClient.java** - Main GUI application
   - Swing-based user interface
   - Login/Registration dialog
   - Chat display with formatting support (JTextPane)
   - Emoji picker
   - Text formatting toolbar (Bold, Italic)
   - Resizable and draggable window
   - Message history display

2. **ChatController.java** - Client-server communication
   - Socket connection management
   - Message sending/receiving
   - Authentication handling
   - Message history requests

---

## ✨ Features

### Core Features

1. **User Registration & Authentication**
   - New user registration
   - Secure login with password verification
   - Username uniqueness validation
   - Prevents duplicate logins (one user per session)

2. **Real-Time Messaging**
   - Broadcast messaging to all connected clients
   - Timestamped messages (HH:mm:ss format)
   - Automatic message display
   - Enter key to send messages

3. **Multi-User Support**
   - Supports up to 10 concurrent clients
   - Thread-safe client management
   - Concurrent message broadcasting

4. **Persistent User Storage**
   - MySQL database integration
   - User data persistence across sessions
   - Automatic table creation

5. **Graphical User Interface**
   - Modern Swing-based GUI
   - Real-time status indicators
   - Scrollable chat display with formatted text support
   - User-friendly login dialog
   - **Resizable window** - Users can resize to their preference (min: 400x500)
   - **Draggable window** - Move window by dragging header or status label
   - **Emoji support** - Emoji picker with 20 common emojis
   - **Text formatting** - Bold (*text*) and Italic (_text_) formatting

6. **Message History**
   - Messages automatically saved to database
   - Loads last 50 messages on login
   - Persistent message storage
   - Chronological message display

7. **Connection Management**
   - Automatic connection cleanup
   - Join/leave notifications
   - Graceful disconnection handling
   - Server capacity management

### Security Features

1. **Password Security**
   - SHA-256 hashing algorithm
   - Random 16-byte salt per user
   - Passwords never stored in plain text
   - Secure password comparison using `MessageDigest.isEqual()`

2. **SQL Injection Prevention**
   - Prepared statements for all database queries
   - Parameterized queries
   - Input validation

3. **Authentication Protection**
   - Prevents duplicate logins
   - Username validation
   - Password verification

---

## 🔧 Technical Specifications

### Server Specifications

| Property | Value |
|----------|-------|
| **Port** | 6001 |
| **Max Clients** | 10 |
| **Thread Pool Size** | 10 (Fixed) |
| **Protocol** | TCP/IP |
| **Data Format** | UTF-8 Strings |
| **Connection Type** | Persistent |

### Client Specifications

| Property | Value |
|----------|-------|
| **UI Framework** | Java Swing (JTextPane for formatting) |
| **Window Size** | 600x700 pixels (default), resizable |
| **Minimum Size** | 400x500 pixels |
| **Window Features** | Resizable, Draggable |
| **Communication** | TCP/IP Socket |
| **Input Method** | Text field + Enter key |
| **Formatting** | Bold, Italic, Emoji support |

### Database Specifications

| Property | Value |
|----------|-------|
| **Database Type** | MySQL |
| **Database Name** | chatapp |
| **Default Host** | localhost |
| **Default Port** | 3306 |
| **Default User** | root |
| **Default Password** | (empty) |

### Technology Stack

- **Language:** Java (JDK 8+)
- **UI Library:** Java Swing (javax.swing)
- **Database:** MySQL 5.7+ / MariaDB
- **JDBC Driver:** MySQL Connector/J 9.5.0
- **Networking:** Java NIO Sockets
- **Concurrency:** ExecutorService, ConcurrentHashMap
- **Security:** Java Security API (SHA-256, SecureRandom)

---

## ✅ Advantages

### 1. **Security**
- ✅ Secure password hashing (SHA-256 with salt)
- ✅ SQL injection prevention (prepared statements)
- ✅ No plain text password storage
- ✅ Thread-safe operations

### 2. **Performance**
- ✅ Thread pool for efficient connection handling
- ✅ ConcurrentHashMap for fast client lookups
- ✅ Database indexes for optimized queries
- ✅ Efficient message broadcasting

### 3. **Reliability**
- ✅ Persistent user storage (MySQL)
- ✅ Automatic table creation
- ✅ Graceful error handling
- ✅ Connection cleanup on disconnect

### 4. **User Experience**
- ✅ Simple, intuitive GUI
- ✅ Real-time messaging
- ✅ Status indicators
- ✅ Enter key support for quick messaging
- ✅ Join/leave notifications
- ✅ **Resizable window** - Customizable window size
- ✅ **Draggable window** - Easy window positioning
- ✅ **Emoji support** - Express with emojis
- ✅ **Text formatting** - Bold and italic text
- ✅ **Message history** - View previous conversations

### 5. **Scalability (Limited)**
- ✅ Multi-threaded server
- ✅ Configurable client limit
- ✅ Efficient resource management

### 6. **Maintainability**
- ✅ Clean code structure
- ✅ Separation of concerns
- ✅ Well-documented code
- ✅ Modular design

### 7. **Development**
- ✅ Easy to set up (XAMPP)
- ✅ Standard Java libraries
- ✅ No external dependencies (except MySQL connector)
- ✅ Cross-platform (Windows, Linux, macOS)

---

## ❌ Disadvantages & Limitations

### 1. **Scalability Limitations**
- ❌ **Fixed client limit (10)** - Hard-coded maximum
- ❌ **Single server instance** - No load balancing
- ❌ **No horizontal scaling** - Cannot distribute across multiple servers
- ❌ **Thread pool fixed size** - Not adaptive to load

### 2. **Feature Limitations**
- ❌ **No private messaging** - Only broadcast messages
- ✅ **Message history** - ✅ IMPLEMENTED - Messages stored and retrieved from database
- ❌ **No file sharing** - Text-only communication
- ✅ **Emoji support** - ✅ IMPLEMENTED - Emoji picker with common emojis
- ❌ **No user profiles** - Minimal user information
- ❌ **No message editing/deletion** - Messages are final
- ❌ **No typing indicators** - No "user is typing" feature
- ❌ **Limited formatting** - Only bold and italic (no underline, colors, etc.)

### 3. **Security Limitations**
- ❌ **No encryption** - Messages sent in plain text
- ❌ **No TLS/SSL** - Unencrypted TCP connection
- ❌ **No session management** - Basic authentication only
- ❌ **No rate limiting** - Vulnerable to spam
- ❌ **No input sanitization** - XSS potential in messages
- ❌ **No password strength requirements** - Weak passwords allowed

### 4. **User Experience Limitations**
- ✅ **Message persistence** - ✅ IMPLEMENTED - Messages saved to database
- ❌ **No offline messages** - Must be online to receive
- ❌ **No message search** - Cannot search chat history
- ❌ **No notifications** - No system notifications
- ❌ **No themes** - Fixed UI appearance
- ✅ **Resizable window** - ✅ IMPLEMENTED - Window can be resized
- ✅ **Draggable window** - ✅ IMPLEMENTED - Window can be moved

### 5. **Technical Limitations**
- ❌ **No connection pooling** - New connection per database operation
- ❌ **No message queuing** - No retry mechanism
- ❌ **No logging system** - Basic console output only
- ❌ **No configuration file** - Hard-coded settings
- ❌ **No graceful shutdown** - Abrupt disconnection handling

### 6. **Database Limitations**
- ✅ **Messages table** - ✅ IMPLEMENTED - Now used for message storage
- ❌ **Sessions table unused** - Created but not implemented
- ❌ **No connection pooling** - New connection per query
- ❌ **No database migration** - Manual schema updates

### 7. **Deployment Limitations**
- ❌ **Requires XAMPP** - MySQL dependency
- ❌ **Not cloud-ready** - Designed for local use
- ❌ **No containerization** - No Docker support
- ❌ **Manual setup** - Requires manual database setup

---

## 🔒 Security Features

### Implemented Security

1. **Password Hashing**
   - Algorithm: SHA-256
   - Salt: 16-byte random salt per user
   - Storage: Hex-encoded hash and salt
   - Comparison: Constant-time comparison using `MessageDigest.isEqual()`

2. **SQL Injection Prevention**
   - All queries use PreparedStatement
   - Parameterized queries
   - No string concatenation in SQL

3. **Authentication**
   - Username uniqueness enforced
   - Prevents duplicate logins
   - Password verification required

### Security Gaps

1. **No Transport Encryption**
   - Messages sent in plain text
   - Passwords transmitted unencrypted
   - Vulnerable to network sniffing

2. **No Input Validation**
   - No message length limits
   - No content filtering
   - Potential for XSS attacks

3. **No Rate Limiting**
   - Unlimited registration attempts
   - Unlimited login attempts
   - Vulnerable to brute force attacks

4. **No Session Management**
   - No session tokens
   - No session expiration
   - Basic connection-based auth only

---

## 💾 Database Schema

### Tables

#### 1. **users** Table
```sql
- user_id: INT (Primary Key, Auto Increment)
- username: VARCHAR(50) (Unique, Not Null)
- password_hash: VARCHAR(255) (Not Null)
- password_salt: VARCHAR(255) (Not Null)
- created_at: TIMESTAMP (Default: Current Timestamp)
- last_login: TIMESTAMP (Nullable)
```

**Indexes:**
- Primary key on `user_id`
- Unique index on `username` (automatic)

#### 2. **messages** Table
```sql
- message_id: INT (Primary Key, Auto Increment)
- sender_id: INT (Foreign Key → users.user_id)
- message_text: TEXT (Not Null)
- sent_at: TIMESTAMP (Default: Current Timestamp)
```

**Indexes:**
- Primary key on `message_id`
- Foreign key on `sender_id`
- Index on `sender_id` (idx_sender_id)
- Index on `sent_at` (idx_sent_at)

**Status:** ✅ **ACTIVELY USED** - Messages are automatically saved when sent and retrieved for history display.

#### 3. **sessions** Table
```sql
- session_id: INT (Primary Key, Auto Increment)
- user_id: INT (Foreign Key → users.user_id)
- token: VARCHAR(255) (Unique, Not Null)
- created_at: TIMESTAMP (Default: Current Timestamp)
- expires_at: TIMESTAMP (Nullable)
```

**Note:** Created for future use but **not implemented** yet.

---

## 📡 Communication Protocol

### Message Format

All messages use pipe-delimited format: `COMMAND|param1|param2`

### Client → Server Commands

1. **Registration**
   ```
   REGISTER|username|password
   ```
   **Response:**
   - `REGISTER_OK` - Success
   - `REGISTER_FAIL|reason` - Failure

2. **Login**
   ```
   LOGIN|username|password
   ```
   **Response:**
   - `LOGIN_OK` - Success
   - `LOGIN_FAIL|reason` - Failure

3. **Send Message**
   ```
   message_text
   ```
   (No command prefix for regular messages)
   - Supports formatting: `*bold*` and `_italic_`
   - Supports emojis

4. **Request Message History**
   ```
   GET_HISTORY
   ```
   **Response:**
   - `HISTORY_START` - Start of history
   - `HISTORY:message` - Each history message
   - `HISTORY_END` - End of history

5. **Disconnect**
   ```
   QUIT
   ```

### Server → Client Messages

1. **System Messages**
   - `username joined the chat`
   - `username left the chat`

2. **Chat Messages**
   - `[HH:mm:ss] username: message_text`
   - Supports formatting: `*bold*` and `_italic_`
   - Supports emojis

3. **Message History**
   - `HISTORY_START` - Marks start of history transmission
   - `HISTORY:[HH:mm:ss] username: message_text` - Each history message
   - `HISTORY_END` - Marks end of history transmission

4. **Error Messages**
   - `✗ error_message`
   - `✓ success_message`

### Connection Flow

```
1. Client connects to server (TCP socket)
2. Client sends LOGIN or REGISTER command
3. Server validates and responds
4. If successful, client enters chat loop
5. Client sends messages (broadcast to all)
6. Client sends QUIT or closes connection
7. Server cleans up and notifies others
```

---

## 📖 Usage Instructions

### Prerequisites
1. Java JDK 8 or higher
2. XAMPP (MySQL)
3. MySQL Connector/J JAR file

### Setup Steps

1. **Start MySQL**
   - Open XAMPP Control Panel
   - Start MySQL service

2. **Create Database**
   - Open phpMyAdmin
   - Run `schema_mysql.sql`

3. **Compile Server**
   ```powershell
   cd ChatApplication\server\src
   javac -cp "..\..\..\libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;." *.java
   ```

4. **Compile Client**
   ```powershell
   cd ChatApplication\client\src
   javac *.java
   ```

5. **Run Server**
   ```powershell
   cd ChatApplication\server\src
   java -cp "..\..\..\libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;." ChatServer
   ```

6. **Run Client** (in new terminal)
   ```powershell
   cd ChatApplication\client\src
   java ChatClient
   ```

### Using the Application

1. **First Time:**
   - Click "Register" button
   - Enter username and password
   - Click "Register"

2. **Subsequent Logins:**
   - Enter username and password
   - Click "Login"

3. **Chatting:**
   - Type message in input field
   - Press Enter or click "Send"
   - Messages appear in real-time
   - **Message history** automatically loads on login (last 50 messages)

4. **Using New Features:**
   - **Emoji:** Click 😀 button to open emoji picker
   - **Bold Text:** Click B button, type message, send (wraps with `*text*`)
   - **Italic Text:** Click I button, type message, send (wraps with `_text_`)
   - **Resize Window:** Drag window edges to resize
   - **Move Window:** Click and drag the blue header bar or status label

5. **Disconnect:**
   - Close the window or press Ctrl+C (server)

---

## 🚀 Future Enhancements

### High Priority
- [x] **Message Persistence** - ✅ COMPLETED - Messages stored in database
- [ ] **Private Messaging** - One-on-one chat
- [x] **Message History** - ✅ COMPLETED - Loads previous messages on login
- [ ] **TLS/SSL Encryption** - Encrypt connections
- [ ] **Session Management** - Implement sessions table

### Medium Priority
- [ ] **File Sharing** - Send files/images
- [ ] **User Profiles** - Profile pictures, status
- [ ] **Message Search** - Search chat history
- [ ] **Connection Pooling** - Optimize database connections
- [ ] **Configuration File** - External config (properties file)

### Low Priority
- [ ] **Themes** - Dark/light mode
- [x] **Emoji Support** - ✅ COMPLETED - Emoji picker implemented
- [x] **Resizable Window** - ✅ COMPLETED - Window can be resized
- [x] **Draggable Window** - ✅ COMPLETED - Window can be moved
- [x] **Text Formatting** - ✅ COMPLETED - Bold and italic support
- [ ] **More Formatting** - Underline, colors, font sizes
- [ ] **Typing Indicators** - "User is typing..."
- [ ] **Message Reactions** - Like/react to messages
- [ ] **Chat Rooms** - Multiple chat channels
- [ ] **Admin Features** - Kick/ban users
- [ ] **Logging System** - File-based logging
- [ ] **Docker Support** - Containerization

---

## 📊 Performance Metrics

### Current Capacity
- **Max Concurrent Users:** 10
- **Messages per Second:** ~100-500 (depends on network)
- **Database Queries:** ~2-3 per user operation
- **Memory Usage:** ~50-100 MB per client connection

### Bottlenecks
1. **Database Connections** - New connection per query
2. **Thread Pool Size** - Fixed at 10
3. **No Caching** - All queries hit database
4. **Synchronous Operations** - Blocking I/O

---

## 🐛 Known Issues

1. ✅ **Message Persistence** - ✅ FIXED - Messages now saved to database
2. ✅ **Messages Table** - ✅ FIXED - Now used for message storage
3. **No Error Recovery** - Client disconnects on any error
4. ✅ **Fixed Window Size** - ✅ FIXED - Window is now resizable
5. **No Connection Retry** - Client doesn't retry on connection failure
6. **Limited Formatting** - Only bold and italic (no underline, colors, etc.)
7. **History Limit** - Only loads last 50 messages (not configurable)

---

## 📝 Code Statistics

- **Total Java Files:** 7
- **Server Files:** 5 (ChatServer, ClientHandler, UserDB, DatabaseManager, MessageDB)
- **Client Files:** 2 (ChatClient, ChatController)
- **Total Lines of Code:** ~1,000+
- **Database Tables:** 3 (users, messages, sessions)
- **Dependencies:** 1 (MySQL Connector)
- **New Features Added:**
  - MessageDB.java (message persistence)
  - Emoji picker functionality
  - Text formatting (bold, italic)
  - Window resizing and dragging
  - Message history loading

---

## 🎓 Learning Outcomes

This project demonstrates:
- ✅ Client-Server Architecture
- ✅ Socket Programming
- ✅ Multi-threading
- ✅ Database Integration (JDBC)
- ✅ GUI Development (Swing)
- ✅ Security Best Practices (Hashing)
- ✅ Network Programming
- ✅ Concurrent Programming
- ✅ Message Persistence
- ✅ Rich Text Formatting (JTextPane)
- ✅ Event-Driven Programming (Mouse events)
- ✅ UI/UX Enhancements

---

## 📄 License & Credits

**Project Type:** Educational/Demonstration  
**Language:** Java  
**Database:** MySQL  
**UI Framework:** Java Swing

---

**Last Updated:** 2024  
**Version:** 1.1  
**Status:** Functional with enhanced features

### Recent Updates (Version 1.1)
- ✅ Added message history persistence
- ✅ Implemented emoji support with picker
- ✅ Added text formatting (bold, italic)
- ✅ Made window resizable and draggable
- ✅ Enhanced UI with formatting toolbar
- ✅ Created MessageDB class for message storage
