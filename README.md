# Real-Time Ticketing System

A comprehensive real-time ticketing system with a **Backend**, **Frontend**, and **Command-Line Interface (CLI)**. This project supports real-time ticket management and transactions, leveraging modern technologies for efficient ticket handling.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [CLI](#cli)
- [WebSocket Integration](#websocket-integration)

---

## Features

- Real-time ticket sales and tracking.
- WebSocket support for live updates.
- Configurable ticket parameters (capacity, release rate, retrieval rate).
- CLI for quick interaction and testing.
- Interactive Frontend for user-friendly operations.
- Scalability for multiple vendors and customers.

---

## Technologies Used

### Backend
- Java (Spring Boot)
- WebSocket for real-time updates
- Maven for dependency management

### Frontend
- Angular
- Chart.js (via `ng2-charts`) for data visualization
- WebSocket for live data

### CLI
- Java-based
- Command-line ticket management and updates

---

## Project Structure

The repository is organized into three main components: **Backend**, **Frontend**, and **CLI**.

---

### Explanation of Key Directories

- **Backend**:
  - Contains Spring Boot application for handling API requests, managing ticket pools, and broadcasting WebSocket updates.
  
- **Frontend**:
  - Contains Angular application for user interaction, including real-time updates.

- **CLI**:
  - Command-line interface for quick interaction with the ticketing system, allowing configurations and real-time updates.


---

## Getting Started
### Prerequisites

Ensure you have the following installed:
- **Java 17+** for the Backend and CLI
- **Node.js 18+** and **npm** for the Frontend
- **Angular CLI** for frontend development
- **Maven** for Backend and CLI dependency management

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/RealTimeTicketingSystem.git
   cd RealTimeTicketingSystem
   
2. **Setup Backend**:
   Navigate to the `Backend` directory and build the project:
   ```bash
   cd Backend
   mvn clean install
   
3. **Setup Frontend**:
   Navigate to the `Frontend` directory and install dependencies:
   ```bash
   cd ../Frontend
   npm install

4. **Setup CLI**:
   Navigate to the `CLI` directory and install dependencies:
   ```bash
   cd ../CLI
   mvn clean install
   
---

## Usage

### Backend

1. **Navigate to the `Backend` directory**:
   ```bash
   cd Backend
   
2. **Run the application**:
   ```bash
   mvn spring-boot:run

3. **The backend will start on `(http://localhost:8080)`. Available endpoints**:
- API Base URL: `(http://localhost:8080)`
- WebSocket: `(ws://localhost:8080/ws)`


### Frontend

1. **Navigate to the `Frontend` directory**:
   ```bash
   cd ../Frontend
   
2. **Run the development server**:
   ```bash
   ng serve

3. **Access the application in your browser**:
- URL: `(http://localhost:4200)`


### CLI

1. **Navigate to the `CLI` directory**:
   ```bash
   cd ../CLI
   
2. **Run the CLI application**:
   ```bash
   mvn exec:java -Dexec.mainClass=com.amina.cli.Main
   
3. **Follow the interactive prompts to configure and manage the ticketing system via the terminal**

---

## WebSocket Integration

- **Backend**:
  - Broadcasts updates for ticket creation and sales.
  - Sends JSON messages for frontend and CLI integration.
    
- **Frontend**:
  - Displays live ticket updates 


