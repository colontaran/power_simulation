README for Appliance Simulation Program

Overview:
=========
This program simulates the power consumption of various appliances in a building. It allows users to add, delete, list, and manage appliances, read appliances from a file, and run a simulation based on user-defined parameters.

Client Class:
=============
The main client class for this program is `AppClient`.

How to Execute:
===============
1. Compile the program using the following command:
   javac AppClient.java

2. Run the program using the command:
   java AppClient

3. Follow the on-screen menu to interact with the program:
   - Type "A" to add an appliance.
   - Type "D" to delete an appliance.
   - Type "C" to clear all appliances.
   - Type "L" to list all appliances.
   - Type "F" to read appliances from a file.
   - Type "S" to start the simulation.
   - Type "Q" to quit the program.

4. When selecting to read appliances from a file, use forward slashes '/' or double backslashes '\\' in the file path.

How to Generate a List of Appliances:
=====================================
1. Compile the program using the following command:
   javac ApplianceGenerator.java

2. Run the program using the command:
   java ApplianceGenerator

3. The program will generate a new file 'output.txt' with the correct format. The file can be entered into the simulation as a list of appliances.

File Format for Reading Appliances:
===================================
The file should contain comma-separated values in the following format:
   locationID,appName,onPower,probOn,appType,lowPowerRF

   - locationID: Integer (8-digit number)
   - appName: String (name of the appliance)
   - onPower: Integer (power consumption in ON state)
   - probOn: Float (probability of staying ON, between 0 and 1)
   - appType: Boolean (true for smart appliances, false for regular appliances)
   - lowPowerRF: Float (power reduction percentage when in LOW state, between 0 and 1; applicable for smart appliances only)

Example:
========
12345678,Refrigerator,150,0.8,true,0.2
87654321,Heater,2000,0.3,false,0.0

Simulation Output:
==================
The simulation will output the power consumption details for each step and indicate the number of appliances turned ON, LOW, and OFF during each step. It will also highlight any brownouts that occur during the simulation.

Notes:
======
- Ensure the input file is correctly formatted to avoid errors during reading.
- The simulation results are stored in a file named "powerSimulationData.txt".

Authors:
========
Colin Tran - colinjtran@gmail.com
Destiny Thomson-Shen