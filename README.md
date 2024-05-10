# Rezerve™: A Java Reservation System


## How to Run

### Set up
Clone the GitHub Repository
```
git clone https://github.com/hollyxj/ReservationSystem.git
```
Navigate to the project directory
```
cd ReservationSystem
```

### Run 1 instance of a server, 1 instance of a GUI
```
cd src/run
javac run.java
java --add-opens java.base/java.time=ALL-UNNAMED run
```

### Run a server instance
```
cd ReservationSystem/src/server
javac MainFrame.java
java --add-opens java.base/java.time=ALL-UNNAMED MainFrame
```
### Run a GUI instance
```
cd ReservationSystem/src/gui
javac RezServer.java
java --add-opens java.base/java.time=ALL-UNNAMED RezServer
```
