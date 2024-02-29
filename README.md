# Smart Farming Water Management System

## Overview
This project develops a smart farming system to intelligently manage water troughs across extensive cow pens, enhancing water accessibility and quality for livestock on large farms. Utilizing LoRa connectivity and MQTT protocols, each node monitors water level and quality, controls water input, and can empty troughs to maintain optimal conditions, all while interfacing with a ThingsBoard server for comprehensive control and analysis.

## Key Functionalities

### Water Level and Quality Control
The system uses water level, pH, and conductivity sensors to maintain optimal water conditions. It intelligently controls water pumps and valves, and can drain troughs if contamination is detected, based on aggregated data exceeding set thresholds.

### Monitoring, Control, and Data Analysis
Operators can monitor node statuses, remotely control operations, and adjust water quality parameters through the ThingsBoard server. The system facilitates detailed data analysis to optimize farm operations and identify issues such as trough leakages.

### Problem Detection and Correction
Immediate data transmission alerts operators to potential problems, with automated responses to alarms, including the capability to empty contaminated troughs autonomously.

## Key Parts

### Nodes
The prototype combines physical ESP8266 boards and simulated devices via Node-Red.

#### Node Implementation
The physical node implemented for this project consists of the embedded platform ESP8266, responsible for measuring the water level, pH level, and conductivity of a trough where the controller must be installed. 
The microcontroller not only manages the sensors and reports the measured values to the gateway, but also is in charge of maintaining the water level and the quality of the water within a remotely configurable range. The simulated node is implemented through Node-Red, it generates measurements that are sent to the physically implemented gateway and receives the commands generated on the Thingsboard platform.

#### Node Intelligence
To ensure the quality and water level within specified ranges in the trough, the node must process information obtained from the sensors and make decisions accordingly. 
In the case of water level, four different limits are defined. The upper and lower limits mark a dangerous situation that must be avoided. The limits in the middle represent the range in which the water should be maintained at all times.  
To maintain the parameters within the required ranges, the microcontroller is capable of controlling a pump that supplies water from a reservoir.
For pH level and conductivity, periodic samples are collected to compute an average value every 24 hours. If the average falls outside the specified limits, the valve will open to extract the contaminated water and replace it with clean water supplied by the pump. In the simulations the average is computed every 20s. 

### Gateway
A Raspberry Pi 3 was utilized to implement a gateway facilitating communication between the nodes, also referred to as the measurement layer, and the server, known as the application layer.
To enhance communication performance, the gateway can decode and encode various types of packets. Packets intended for communication with the server are defined as JSON objects, while the packets exchanged with the nodes are integers. This facilitates efficient decoding on the nodes and helps conserve energy consumption. 

### Server
The remote server used in this project is ThingsBoard, an open-source IoT platform for data collection, processing, visualization, and device management. Every implemented node is configured on the platform as a device using a common device profile for all nodes which communicates through an MQTT connection. These devices follow a rule chain where commands and data reception are programmed. 

### Mobile Android Application
For mobile monitoring of the system, an Android application was developed. The application collects real-time data from the system server and provides a dashboard for users to visualize and analyze the data, as well as the possibility to send commands through the server to the nodes.

### SQLite Database
To store the data and for data analysis purposes, an SQLite database was implemented. The data stored is the timestamp of the telemetry, the values for water level, pH and conductivity and the corresponding device ID. That way, the data can be queried using SQL or exported in different formats (such as CSV and JSON) for data analysis with different tools. One of the main points to analyze is leak detection.

## Conclusion
This smart farming system represents a significant step toward modernizing agricultural practices, offering precise water management solutions. While this project showcases a functioning prototype, full-scale implementation would further enhance operational efficiency and resource sustainability on farms.

## Contributors
- Quentin Mathieu
- Omar Fantoni
- Antonio Cabanas
