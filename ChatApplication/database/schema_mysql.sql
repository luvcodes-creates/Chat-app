-- Chat Application Database Schema
-- MySQL Database Schema for XAMPP

-- Create database (run this first in phpMyAdmin SQL tab)
CREATE DATABASE IF NOT EXISTS chatapp;
USE chatapp;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Messages Table
CREATE TABLE IF NOT EXISTS messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Sessions Table (for future use)
CREATE TABLE IF NOT EXISTS sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Indexes for performance
-- Note: The username column already has a UNIQUE constraint which creates an index automatically

-- IMPORTANT: If you get "Duplicate key name" errors when running this script,
-- it means the indexes already exist. You can safely ignore those errors OR
-- comment out the index creation lines below and run the script again.

-- For MySQL 8.0.19 and later, you can use CREATE INDEX IF NOT EXISTS:
-- CREATE INDEX IF NOT EXISTS idx_sender_id ON messages(sender_id);
-- CREATE INDEX IF NOT EXISTS idx_sent_at ON messages(sent_at);

-- For older MySQL versions, run these lines (ignore errors if indexes already exist):
CREATE INDEX idx_sender_id ON messages(sender_id);
CREATE INDEX idx_sent_at ON messages(sent_at);

