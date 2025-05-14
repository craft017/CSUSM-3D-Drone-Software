# AirSim Android Drone Control System

## 📱 Overview
This project is a capstone initiative developed at California State University San Marcos (CSUSM), sponsored by MCTSSA (Marine Corps Tactical Systems Support Activity). It implements a two-phone drone control system using Android, AirSim, and WebSockets. One phone acts as the **User Controller** (manual and autopilot control), and the other as the **Drone Phone**, communicating with the drone simulation environment (AirSim).

It supports:
- Manual flight control (real-time commands)
- Autopilot waypoint queueing and execution
- Loiter patterns (e.g., RaceTrack)
- Camera streaming from the drone phone to the user phone
- WebSocket-based bidirectional communication

## 📐 Architecture Design
![real_arch_design drawio](https://github.com/user-attachments/assets/136d33fe-7878-471d-8a31-5d10a171f375)

### Layers

#### Presentation Layer
- Activities: `MainActivity`, `UserActivity`, `DroneActivity`
- ![image](https://github.com/user-attachments/assets/fe540112-d055-4335-960a-ac76b9257a8a)

- Fragments: `ManualFragment`, `AutopilotFragment`, `DronePhoneFragment`
- ![Screenshot 2025-05-08 210706](https://github.com/user-attachments/assets/40fba6fa-46d2-433c-9c8a-e867f7f3d933)
- ![Screenshot 2025-05-08 212558](https://github.com/user-attachments/assets/36a948bc-868a-4d4a-80f9-ab4346a7ef8f)



#### Business Layer
- `Orchestrator` (Command Manager)
- `Autopilot`, `Manual` (logic for autopilot/manual modes)
- `AutopilotCommand` base class
  - `GPSCommand`, `HeadingAndSpeed`, `LoiterPattern`
- `Pattern`, `RaceTrack` (for loiter patterns)

#### Communication Layer
- `WebSocketClientTesting`
- `AirSimFlightController`

#### External Systems
- AirSim (Unreal Engine drone simulator)
- CameraX (drone phone camera capture)

## 📊 Design Patterns Used
- **Model-View-Controller (MVC)**: Fragments = View, Orchestrator = Controller, Autopilot = Model
- **Command Pattern**: Each `AutopilotCommand` encapsulates behavior
- **Strategy Pattern**: `flightControllerInterface` allows swapping implementations
- **Observer Pattern**: WebSocket listeners with callbacks
- **Adapter + Facade Patterns**: Unifies user interaction and drone control behind Orchestrator

## 🔁 Component Relationships (UML-style)
![arch_design drawio](https://github.com/user-attachments/assets/0c30cdba-f499-4da9-858d-85b04d627b91)


## 🧪 Testing Strategy

### System Testing
- Verifies full command flow: UI → Orchestrator → WebSocket → Drone
- Camera streaming verified end-to-end
- Command queue executed correctly with visual confirmation

### Acceptance Testing
- Non-developer user verified control switching between Manual and Autopilot
- Verified altitude is maintained during loiter patterns
- All commands visible and deletable from UI queue

## Lessons Learned 
- AirSim's Z-axis control is tricky — it's based on absolute position, not velocity.
- It's critical to always repost your Runnable in Android handlers if you want loops to continue.
- Clear separation between UI and command logic (via Orchestrator) simplified testing.
- Using design patterns like Command and Strategy made extending flight modes easy.
- Live project management and sponsor interaction taught us to manage change and scope effectively.


