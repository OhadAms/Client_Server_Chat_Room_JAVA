# Java Multi-User Chat Room Application  
**Real-Time Client-Server Chat with Private Messaging & Live User List**

[![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)](https://openjdk.org/)
[![Swing GUI](https://img.shields.io/badge/GUI-Swing-blue)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![TCP Sockets](https://img.shields.io/badge/Network-TCP%20Sockets-green)](https://docs.oracle.com/javase/tutorial/networking/sockets/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A fully functional, pure Java-based real-time chat room featuring broadcast and private messaging, unique username enforcement, live connected user list, and a clean responsive Swing GUI.

## Live Demo (YouTube)
- [Watch the video demonstration](https://www.youtube.com/watch?v=x9yFLKe0f6M)  


## Table of Contents
- [Features](#features)
- [Architecture & Design Patterns](#architecture--design-patterns)
- [How It Works (Technical Deep Dive)](#how-it-works-technical-deep-dive)
- [Technologies & Tools Used](#technologies--tools-used)
- [How to Run](#how-to-run)
- [Message Protocol](#message-protocol)
- [Default Configuration](#default-configuration)

## Features
- Multi-client real-time communication
- Unique username validation (server-side)
- Automatic guest username generation (`Guest1`, `Guest2`, …)
- Broadcast messages to all users
- Private messaging (click a user from the list)
- Dynamic connected users panel (updated in real time)
- Clean, intuitive Swing-based GUI
- State-based UI control (connected/disconnected)
- Graceful disconnect with “has left the chat” notification
- Thread-safe message handling
- No external dependencies — 100% pure Java

## Architecture & Design Patterns
| Mechanism                          | Implementation                              | Purpose                                      |
|------------------------------------|---------------------------------------------|----------------------------------------------|
| **Event-Based Messaging**          | `StringProducer` / `StringConsumer`         | Decoupled message broadcasting |
| **Mediator**                       | `MessageBoard` class                        | Central hub for routing messages and managing clients |
| **State**                          | `ConnectedState` / `DisconnectedState`     | Controls GUI behavior based on connection status |
| **Factory**                        | `SimpleClientGUIFactory`                    | Creates GUI instances cleanly                |
| **Proxy**                          | `ConnectionProxy`                           | Encapsulates socket communication per client |

## How It Works (Technical Deep Dive)

1. **Server Startup** — `ServerApplication` opens a `ServerSocket` on port **1300**
2. **Client Connection** — Each client gets a dedicated `ConnectionProxy` thread
3. **Username Validation** — Server checks if name is unique → rejects duplicates
4. **Message Flow**  
   Client → `ConnectionProxy.consume()` → `MessageBoard.consume()` → parsed & routed
5. **Live User List** — Server sends current usernames before every broadcast using `$$$`
6. **Disconnection** — Socket closes → thread ends → client removed → others notified

## Technologies & Tools Used
| Category            | Technology / Tool                         |
|---------------------|-------------------------------------------|
| Language            | Java 17+                                  |
| Networking          | Java Sockets (`Socket`, `ServerSocket`)   |
| GUI Framework       | Java Swing                                |
| Concurrency         | `Thread`, per-client dedicated threads   |
| Exception Handling  | Custom `ChatException`                    |
| Build Tool          | Plain `javac` (no Maven/Gradle required)  |

## How to Run

```bash
# 1. Clone the project
git clone https://github.com/your-username/java-chat-room.git
cd java-chat-room

# 2. Compile
javac il/ac/hit/patterns/*.java il/ac/hit/patterns/client/*.java il/ac/hit/patterns/server/*.java

# 3. Start the server
java il.ac.hit.patterns.server.ServerApplication

# 4. Start clients (as many as you want)
java il.ac.hit.patterns.client.SimpleTCPIPClient
```

## Message Protocol

| Type          | Format Example                                      | Description                                      |
|---------------|-----------------------------------------------------|--------------------------------------------------|
| **Broadcast** | `Alice ##$$$### All #$$$# hello everyone`           | Sent to all connected clients                    |
| **Private**   | `Bob ##$$$### Alice #$$$# this is a secret`         | Sent only to the user named **Alice**            |
| **User List + Message** | `Alice Bob Charlie $$$ Welcome to the chat!` | Space-separated list of online users followed by `$$$` and the actual message |
| **System**    | `Charlie -> has left the chat room!`                | Automatically generated when a client disconnects |

**Important:**  
The delimiters `##$$$###` (separates sender → recipient) and `#$$$#` (separates recipient → message) are **reserved** and blocked in user input to prevent protocol injection.

## Default Configuration

- **Server Host**: `127.0.0.1` (localhost)  
- **Port**: `1300`  
- **Guest Counter File**: `last_username.txt` (stores the last used Guest number and persists across application restarts)
