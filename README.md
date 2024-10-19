# ChatRoom

A simple distributed application demonstration using Java Sockets to create a chat room where multiple clients can communicate with each other.

## Overview
This is a demonstration of a distributed application where clients can join **password-protected chat rooms** and communicate with each other. The application allows users to set a password for their chat room to ensure privacy and security. Additionally, users can see all active members within the chat room.

## Features
- **Password-protected chat rooms**: The user who creates a chat room sets a password, and any client joining must enter the correct password.
- **List active users**: Users can list all active users in the chat room by typing `/list`.
- **Real-time communication**: Multiple clients can join the same chat room and send messages in real-time.

## How to Run the Application

1. **Run the Chat Server**:
   - First, compile and run the `ChatServer.java` file to start the chat server.
   ```
   javac ChatServer.java
   java ChatServer

1. **Run the Chat Client**:
   - First, compile and run the `ChatClient.java` file to start the chat server.
   ```
   javac ChatClient.java
   java ChatClient
