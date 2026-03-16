# MySQL/XAMPP Setup Guide for Chat Application

## Prerequisites
- XAMPP installed on your system
- MySQL JDBC Driver (mysql-connector-java.jar)

---

## Step 1: Install MySQL JDBC Driver

### Option A: Download Manually
1. Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/
   - Choose "Platform Independent" (ZIP Archive)
   - Extract the ZIP file
   - Copy `mysql-connector-j-8.x.x.jar` (or `mysql-connector-java-8.x.x.jar`) to your `libs` folder:
   
   ```
   C:\Users\loveb\OneDrive\Desktop\luvproject\libs\mysql-connector-j-8.x.x.jar
   ```

### Option B: Using Maven (if using build tool)
Add to your `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

---

## Step 2: Start XAMPP and MySQL

1. **Open XAMPP Control Panel**
   - Launch XAMPP from your Start Menu or desktop shortcut

2. **Start MySQL Service**
   - Click the "Start" button next to MySQL
   - Wait until it shows "Running" status (green indicator)
   - If MySQL fails to start, check if port 3306 is already in use

3. **Verify MySQL is Running**
   - You should see "MySQL" with a green "Running" status
   - Port should be 3306 (default)

---

## Step 3: Create Database in phpMyAdmin

1. **Open phpMyAdmin**
   - In XAMPP Control Panel, click "Admin" button next to MySQL
   - OR open browser and go to: `http://localhost/phpmyadmin`

2. **Create Database**
   - Click on "New" in the left sidebar (or "Databases" tab)
   - Database name: `chatapp`
   - Collation: `utf8mb4_general_ci` (or `utf8mb4_unicode_ci`)
   - Click "Create" button

3. **Import Schema (Optional - tables will be created automatically)**
   - Select the `chatapp` database from the left sidebar
   - Click on "SQL" tab at the top
   - Copy and paste the contents of `database/schema_mysql.sql`
   - Click "Go" button
   - OR just let the application create tables automatically on first run

---

## Step 4: Configure Database Connection (if needed)

The default configuration in `DatabaseManager.java` uses:
- **Host**: `localhost`
- **Port**: `3306`
- **Database**: `chatapp`
- **Username**: `root`
- **Password**: `` (empty - default XAMPP MySQL password)

### If your MySQL password is different:
Edit `ChatApplication/server/src/DatabaseManager.java`:
```java
private static final String DB_PASSWORD = "your_password_here";
```

### If you want to change database name:
Edit `ChatApplication/server/src/DatabaseManager.java`:
```java
private static final String DB_NAME = "your_database_name";
```

---

## Step 5: Compile and Run

### Compile Server (include MySQL connector in classpath)

**Windows (PowerShell):**
```powershell
cd ChatApplication\server\src
javac -cp "..\..\..\libs\mysql-connector-j-8.x.x.jar;." *.java
```

**Or if using relative path from project root:**
```powershell
cd ChatApplication\server\src
javac -cp "..\..\..\libs\*;." *.java
```

### Run Server
```powershell
cd ChatApplication\server\src
java -cp "..\..\..\libs\mysql-connector-j-8.x.x.jar;." ChatServer
```

**Or using wildcard:**
```powershell
cd ChatApplication\server\src
java -cp "..\..\..\libs\*;." ChatServer
```

---

## Step 6: Verify Setup

1. **Check Server Startup**
   - Server should print: `✓ Chat Server started on port 6001`
   - You should see: `[DB] Successfully connected to MySQL database: chatapp`

2. **Verify Database Tables**
   - Open phpMyAdmin: `http://localhost/phpmyadmin`
   - Select `chatapp` database
   - You should see `users`, `messages`, and `sessions` tables

3. **Test the Application**
   - Run the client and try to register/login
   - Check in phpMyAdmin that users are being added to the `users` table

---

## Troubleshooting

### Problem: "MySQL JDBC Driver not found"
**Solution:** Make sure `mysql-connector-j-8.x.x.jar` is in your `libs` folder and included in classpath

### Problem: "Access denied for user 'root'@'localhost'"
**Solution:** 
- Check if MySQL password is set. If yes, update `DB_PASSWORD` in `DatabaseManager.java`
- Or reset MySQL password in XAMPP

### Problem: "Unknown database 'chatapp'"
**Solution:** Create the database in phpMyAdmin (Step 3)

### Problem: "Can't connect to MySQL server"
**Solution:** 
- Make sure MySQL is running in XAMPP Control Panel
- Check if port 3306 is available (not used by another MySQL instance)

### Problem: Port 3306 already in use
**Solution:**
- Stop other MySQL services running on your system
- Or change MySQL port in XAMPP config (requires updating `DB_PORT` in code)

---

## Viewing Data in phpMyAdmin

1. Open: `http://localhost/phpmyadmin`
2. Select `chatapp` database (left sidebar)
3. Click on `users` table to view registered users
4. You'll see columns: `user_id`, `username`, `password_hash`, `password_salt`, `created_at`, `last_login`

---

## Notes

- Default XAMPP MySQL password is **empty** (blank)
- Tables are created automatically when the server runs (if they don't exist)
- All user passwords are hashed with SHA-256 and salted (secure storage)
- You can now use phpMyAdmin to manage your database visually!

