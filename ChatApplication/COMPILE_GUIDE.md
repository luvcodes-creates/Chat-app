# Chat Application - Compilation Guide

## Prerequisites
- Java JDK installed (Java 8 or higher)
- MySQL connector JAR file in `libs` folder
- XAMPP MySQL running (for server)

---

## Step 1: Compile the Server

The server requires the MySQL connector JAR in the classpath.

### Windows PowerShell:

```powershell
# Navigate to server source directory
cd ChatApplication\server\src

# Compile all Java files with MySQL connector in classpath
javac -cp "..\..\..\libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;." *.java
```

**Or using wildcard (if JAR is in libs root):**
```powershell
cd ChatApplication\server\src
javac -cp "..\..\..\libs\*;." *.java
```

### What this does:
- Compiles all `.java` files in the `server/src` directory
- Includes MySQL connector JAR in the classpath (needed for database connection)
- Outputs `.class` files in the same directory

---

## Step 2: Compile the Client

The client doesn't need the MySQL connector (it only connects to the server).

### Windows PowerShell:

```powershell
# Navigate to client source directory
cd ChatApplication\client\src

# Compile all Java files
javac *.java
```

---

## Step 3: Run the Server

**Before running, make sure:**
1. XAMPP MySQL is running
2. Database `chatapp` exists (run `schema_mysql.sql` in phpMyAdmin)

### Windows PowerShell:

```powershell
# Navigate to server source directory
cd ChatApplication\server\src

# Run the server with MySQL connector in classpath
java -cp "..\..\..\libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;." ChatServer
```

**Or using wildcard:**
```powershell
cd ChatApplication\server\src
java -cp "..\..\..\libs\*;." ChatServer
```

**Expected output:**
```
✓ Chat Server started on port 6001
✓ Maximum clients: 10
✓ Waiting for client connections...
```

---

## Step 4: Run the Client

Run the client in a **separate terminal window** (keep server running).

### Windows PowerShell:

```powershell
# Navigate to client source directory
cd ChatApplication\client\src

# Run the client
java ChatClient
```

**Expected:**
- A GUI window will open
- Login dialog will appear
- Enter server details (localhost, port 6001) and credentials

---

## Quick Compile Scripts

### Compile Everything (PowerShell):

Save this as `compile.ps1` in the `ChatApplication` folder:

```powershell
# Compile Server
Write-Host "Compiling server..." -ForegroundColor Green
cd server\src
javac -cp "..\..\..\libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;." *.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Server compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Server compilation failed!" -ForegroundColor Red
    exit 1
}

# Compile Client
Write-Host "Compiling client..." -ForegroundColor Green
cd ..\..\client\src
javac *.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Client compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Client compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n✓ All compilation complete!" -ForegroundColor Green
cd ..\..
```

Run it:
```powershell
.\compile.ps1
```

---

## Troubleshooting

### Error: "mysql-connector-j-9.5.0.jar not found"
**Solution:** Check the path to the JAR file. The full path should be:
```
libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar
```

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Solution:** Make sure the MySQL connector JAR is in the classpath when running the server.

### Error: "Cannot find symbol" or compilation errors
**Solution:** 
- Make sure you're in the correct directory (`server\src` or `client\src`)
- Check that all `.java` files are present
- Verify Java is installed: `java -version`

### Error: "Access denied for user 'root'@'localhost'"
**Solution:** 
- Check MySQL is running in XAMPP
- Verify database `chatapp` exists
- Check password in `DatabaseManager.java` (default is empty for XAMPP)

---

## Project Structure

```
ChatApplication/
├── server/
│   └── src/
│       ├── ChatServer.java
│       ├── ClientHandler.java
│       ├── DatabaseManager.java
│       └── UserDB.java
├── client/
│   └── src/
│       ├── ChatClient.java
│       └── ChatController.java
└── database/
    └── schema_mysql.sql
```

---

## Notes

- **Server** requires MySQL connector (for database access)
- **Client** doesn't need MySQL connector (only connects to server via socket)
- Compiled `.class` files will be in the same directories as `.java` files
- Keep the server running while testing the client
- You can run multiple client instances to test multi-user chat
