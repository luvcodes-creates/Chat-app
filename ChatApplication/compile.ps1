# Chat Application Compilation Script
# Run this script to compile both server and client

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Chat Application - Compilation Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set paths
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$libsPath = Join-Path (Split-Path -Parent $projectRoot) "libs"
$mysqlJar = Join-Path $libsPath "mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar"
$serverSrc = Join-Path $projectRoot "server\src"
$clientSrc = Join-Path $projectRoot "client\src"

# Check if MySQL JAR exists
if (-not (Test-Path $mysqlJar)) {
    Write-Host "ERROR: MySQL connector JAR not found at:" -ForegroundColor Red
    Write-Host $mysqlJar -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Please extract mysql-connector-j-9.5.0.zip to libs folder" -ForegroundColor Red
    exit 1
}

# Compile Server
Write-Host "Compiling server..." -ForegroundColor Green
Push-Location $serverSrc
try {
    javac -cp "$mysqlJar;." *.java
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Server compiled successfully!" -ForegroundColor Green
    } else {
        Write-Host "✗ Server compilation failed!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
} catch {
    Write-Host "✗ Error compiling server: $_" -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

Write-Host ""

# Compile Client
Write-Host "Compiling client..." -ForegroundColor Green
Push-Location $clientSrc
try {
    javac *.java
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Client compiled successfully!" -ForegroundColor Green
    } else {
        Write-Host "✗ Client compilation failed!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
} catch {
    Write-Host "✗ Error compiling client: $_" -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✓ All compilation complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "To run the server:" -ForegroundColor Yellow
Write-Host "  cd server\src" -ForegroundColor White
Write-Host "  java -cp `"..\..\..\libs\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar;.`" ChatServer" -ForegroundColor White
Write-Host ""
Write-Host "To run the client (in a new terminal):" -ForegroundColor Yellow
Write-Host "  cd client\src" -ForegroundColor White
Write-Host "  java ChatClient" -ForegroundColor White
Write-Host ""
