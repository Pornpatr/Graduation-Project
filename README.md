# Automatic Office On Status using Beacon Application

This project is a mobile application that uses Bluetooth Low Energy (BLE) technology 
and Beacon to control the badge display system. The application communicates with the badge display system through MQTT (WebSocket) 
and allows users to turn on and off the badge display system remotely.

## Features
- Turn on/off the badge display system remotely
- Detect the presence of a user with the mobile device
- Use Beacon technology to identify the location of the user
- Support MQTT (WebSocket) for communication with the badge display system

## Tools and Technologies Used
- MQTT (WebSocket) for communication
- Android Studio with Firebase and Third-party libraries
- Arduino IDE for badge display system
- Java for Android application development
- C++ for badge display system programming

## Installation

1. Clone the repository to your local machine
2. Open the project in Android Studio
3. Build and run the project on your Android device or emulator
4. Connect the badge display system to your Arduino board using the Arduino IDE
5. Configure the MQTT settings in the badge display system code to match the settings in the Android application code
6. Upload the badge display system code to the Arduino board
7. Test the system by moving your mobile device in and out of the range of the Beacon

## Usage
1. Launch the application on your mobile device
2. Allow the application to access Bluetooth and location services
3. The application will automatically detect the presence of the Beacon and turn on the badge display system
4. To turn off the badge display system, simply move your mobile device out of the range of the Beacon

## Credits

  This project was developed by Pornopatr Chongcharoenkij as a part of GraduationProject
