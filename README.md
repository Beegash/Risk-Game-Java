# 🎮 Risk Game

A modern implementation of the classic Risk board game in Java, featuring a client-server architecture and a beautiful graphical user interface.

## 🌟 Features

- **Modern UI**: Clean and intuitive user interface with smooth animations
- **Network Play**: Client-server architecture supporting online multiplayer
- **Game Mechanics**:
  - Territory control and army placement
  - Strategic combat system with dice rolling
  - Continent bonuses
  - Fortification phase
  - Real-time game state updates
- **Player Features**:
  - Player name customization
  - Game chat system
  - Rematch functionality
  - Game rules display
  - Turn-based gameplay

## 🛠️ Technologies

- Java
- Swing for GUI
- Socket Programming for networking
- Object-Oriented Design Patterns

## 📋 Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven (for building the project)

## 🚀 Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/risk-game.git
cd risk-game
```

2. Build the project using Maven:
```bash
mvn clean install
```

## 🎮 How to Play

1. Start the server:
```bash
java -jar target/risk-game-server.jar
```

2. Launch the client:
```bash
java -jar target/risk-game-client.jar
```

3. Enter your player name and connect to the server
4. Wait for an opponent to join
5. Follow the on-screen instructions to play the game

## 🎲 Game Rules

### Turn Structure
1. **Place Armies**: Receive and place armies based on territories controlled
2. **Attack**: Attack adjacent territories using dice rolls
3. **Fortify**: Move armies between your territories

### Combat
- Attacker can roll up to 3 dice
- Defender can roll up to 2 dice
- Highest dice wins (defender wins ties)
- Must leave at least 1 army in attacking territory

### Victory
- Eliminate opponent's armies
- Capture all territories


## 👥 Authors

- İzzettin Furkan Özmen

## 🙏 Acknowledgments

- Classic Risk board game by Hasbro
- All contributors who have helped with the project 
