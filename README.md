# REMOTE DIRECTORY MONITOR SYSTEM
*(Hệ thống Giám sát Thư mục Từ xa)*

## Student Information (Thông tin Sinh viên)

| Thông tin        | Chi tiết                      |
|:-----------------|:------------------------------|
| **Họ và tên**    | **Đoàn Thành Phát**           |
| **MSSV**         | **23127241**                  |
| **Email**        | **dtphat23@clc.fitus.edu.vn** |
| **Môn học**      | **Lập trình ứng dụng Java**   |
| **Demo Video**   | **...**                       |

---

## 📖 Project Description

This is a distributed Client-Server application designed to monitor file system changes in real-time. It allows a central Server to track file creations, deletions, and modifications on multiple remote Clients.

### Key Features
* **Real-time Monitoring:** Uses Java NIO `WatchService` to detect changes instantly.
* **Recursive Tracking:** Automatically monitors sub-directories and newly created folders.
* **Remote Control:** Server can command specific Clients to start/stop monitoring.
* **Live Dashboard:** Server GUI displays active clients and a real-time log of file events.
* **Multi-threaded:** Handles multiple clients simultaneously without blocking.

## Technology Stack
* **Language:** Java (JDK 25)
* **Networking:** TCP/IP Sockets (Serializable Objects)
* **GUI:** Java Swing

## How to Run

### 1. Run the Server
* Run `server/ServerMain.java`.
* The Server Dashboard will open on port `9999`.

### 2. Run the Client
* Run `client/view/ClientFrame.java`.
* Enter Server IP (default: `localhost`) and Port (`9999`).
* Click **"Kết nối" (Connect)**.

### 3. Usage
1.  On Server GUI, right-click a connected Client.
2.  Select **"Giám sát thư mục..." (Monitor Directory)**.
3.  Choose a folder path on the Client machine.
4.  Modifications in that folder will appear on the Server Log instantly.

## 📂 Project Structure

```text
src/
├── client/          # Client-side logic & GUI
├── server/          # Server-side logic & GUI
└── common/          # Shared Protocol (Message, Constants)