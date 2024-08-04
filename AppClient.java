/* This is a stub code. You can modify it as you wish. */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

class AppClient{
	
	public void readAppFile(String file, ArrayList<Appliance> appliances){ // method to read the comma seperated appliance file.
		Scanner scan = null;
		try {
			File myFile=new File(file);
			scan=new Scanner(myFile);//each line has the format
			//locationID,name of app,onPower,probability of staying on, smart or not,Smart appliances (if "on") power reduction percent when changed to "low" status(floating point, i.e..33=33%).
			//String str;
			int lineCount = 1;
			while(scan.hasNextLine()) {
				String[] appAttributes = scan.nextLine().split(",");
				
				if (appAttributes.length != 6) {
                    System.out.println("Invalid format in file. Skipping line " + lineCount + ".");
                    ++lineCount;
					continue;
                }
				try {
					int locationID = Integer.parseInt(appAttributes[0]);
					String appName = appAttributes[1];
					int onPower = Integer.parseInt(appAttributes[2]);
					float probOn = Float.parseFloat(appAttributes[3]);
					boolean appType = Boolean.parseBoolean(appAttributes[4]);
					float lowPowerRF = Float.parseFloat(appAttributes[5]);

					Appliance newAppliance = new Appliance(locationID, appName, onPower, probOn, appType, lowPowerRF);
					appliances.add(newAppliance);
				} catch (NumberFormatException e) {
					System.out.println("Invalid number format encountered. Skipping line " + lineCount + ".");
				} finally {
					++lineCount;
				}
			}
			System.out.println("Appliances from the file have been added to the list!");
		}catch(IOException ioe){ 
			System.out.println("The file can not be read");
		}finally{
			if (scan != null) {
				scan.close();
			}
		}
	}
	
	public static void main(String []args){
		AppClient app = new AppClient();
		//User interactive part
		String option1;
		ArrayList<Appliance> appliances = new ArrayList<>();
		Scanner scan = new Scanner(System.in);

		while(true){// Application menu to be displayed to the user.
			System.out.println("\nSelect an option:");
			System.out.println("Type \"A\" Add an appliance");
			System.out.println("Type \"D\" Delete an appliance");	
			System.out.println("Type \"C\" Clear all appliances");	
			System.out.println("Type \"L\" List the appliances");
			System.out.println("Type \"F\" Read Appliances from a file");
			System.out.println("Type \"S\" To Start the simulation");
			System.out.println("Type \"Q\" Quit the program");
			option1 = scan.nextLine().trim();
			// System.out.println("User input: [" + option1 + "]");  // Debugging line

			/* Complete the skeleton code below */
			switch(option1) { //menu switch case - Colin
				case "A":
					//add appliance code here - Colin
					app.addApp(scan, appliances); //turned this section into a method for readability
					break;
				case "D":
					if (appliances.isEmpty()) {
						System.out.println("There are no appliances!!!");
						break;
					}
					//delete appliance code here - Colin
					app.delApp(scan, appliances); //turned this section into a method for readability
					break;
				case "C":
					appliances = new ArrayList<>();
					System.out.println("Appliances list has been cleared.");
					break;
				case "L":
					if (appliances.isEmpty()) {
						System.out.println("There are no appliances!!!");
						break;
					}
					//list appliances code here - Colin
					app.listApps(appliances); //turned this section into a method for readability
					break;
				case "F":
					//read appliances from a file code here - Colin
					System.out.println("Enter path to file you would like to read (use forward slashes '/' or double backslashes '\\\\'): ");
					String fileName = scan.nextLine();
					app.readAppFile(fileName, appliances);
					break;
				case "S":
	// INPUT HANDLING
				// CHECK IF APPLIANCES LIST IS EMPTY BEFORE STARTING
					if (appliances.isEmpty()) {
						System.out.println("There are no appliances!!!");
						break;
					}
				// make sure all appliances are turned off
					for (Appliance appliance : appliances) {
						appliance.setState("OFF");
					}

					int totalAllowablePower;
					int numSteps;
					String answer;
					Random rand;
					String seedString;

					while(true){ // Input for total power of simulation
						totalAllowablePower = readIntegerInput(scan, "Enter total allowable wattage for simulation: ");
						if (totalAllowablePower > 0) {
							break;
						}
					}
					
					while (true) { // input for number of steps for simulation
						numSteps = readIntegerInput(scan, "Enter the number of steps you would like to run the simulation for: ");
						if (numSteps > 0) {
							break;
						}
					}
					
					do { // input for answer of y or n
						answer = readStringInput(scan, "Do you want a set seed (y/n)?");
					} while(!answer.equals("y") && !answer.equals("n"));

					if (answer.equals("y")) {
						int seed = readIntegerInput(scan, "Enter seed for random number generator: ");
						rand = new Random(seed);
						seedString = String.valueOf(seed);
					}
					else {
						rand = new Random();
						seedString = "none";
					}
					System.out.println();
	// INITIALIZE
					float[][] totalPowerConsumedTracker = new float[numSteps][3]; // store in nested array [][0] is value after turning on, and [][1] is value after turning low, and [][2]] is value after browning out
					HashMap<Integer, Integer> locationsOnOrLow = new HashMap<>(); // track number of appliances that are in use in each location
					HashMap<Integer, Integer> affectedLocations = new HashMap<>(); // track number of brown outs at each location for entire simulation
					ArrayList<Map.Entry<Integer, Integer>> locationsOnOrLowList;
					int[] totalAppliancesOn = new int[numSteps];
					int[] appliancesTurnedOn = new int[numSteps];
					int[] totalAppliancesLow = new int[numSteps];
					int[] appliancesTurnedLow = new int[numSteps];
					int[] totalAppliancesOff = new int[numSteps];
					int[] appliancesTurnedOff = new int[numSteps];
					int[] numBrownOuts = new int[numSteps];

					// initialize totalPowerConsumedTracker to 0.0f
					FileOutputStream dataStream = null;
					PrintWriter dataWriter = null;
					try {
						dataStream = new FileOutputStream("powerSimulationData.txt");
						dataWriter = new PrintWriter(dataStream);
						for (int i = 0; i < numSteps; ++i) {
							totalPowerConsumedTracker[i][0] = 0.0f;
							totalPowerConsumedTracker[i][1] = 0.0f;
							totalPowerConsumedTracker[i][2] = 0.0f;
						}
					// initalize affected locaitons to 0
						for (Appliance appliance : appliances) {
							int roomID = appliance.getLocationID();
							affectedLocations.put(roomID, 0);
						}

		// start loop here	
						for (int currStep = 0; currStep < numSteps; ++currStep) {
						// reset brownout boolean
							boolean startBrownOut = false;
						// initialize or reset appliance counts for each step
							System.out.println("Step " + currStep + ":");
							for (Appliance appliance : appliances) {
								int roomID = appliance.getLocationID();
								locationsOnOrLow.put(roomID, 0);
							}

						// randomly turn on appliances each step and increment count if ON
							for (Appliance appliance : appliances) {
								float probOn = appliance.getProbOn();
								float randFloat = rand.nextFloat();
								if (probOn >= randFloat && !appliance.getState().equals("ON")) {
									appliance.setState("ON");
									++appliancesTurnedOn[currStep];
								}
							}

						// calculate total power consumption
							for (Appliance appliance : appliances) {
								totalPowerConsumedTracker[currStep][0] += appliance.getPowerConsumption(); // add wattage to total power consumption
							}
							
						// sort appliances list by "ON" state then by smart appliance then by wattage descending
							appliances.sort(Appliance.firstSort);
							
							
							totalPowerConsumedTracker[currStep][1] = totalPowerConsumedTracker[currStep][0];
							totalPowerConsumedTracker[currStep][2] = totalPowerConsumedTracker[currStep][0];
							if (totalPowerConsumedTracker[currStep][0] > totalAllowablePower) { 
								for (Appliance appliance : appliances) { // iterate through appliances
									String currState = appliance.getState();
									if (!appliance.isSmart() || currState.equals("OFF")) {
										startBrownOut = true; // brown out starts when there only way left to decrease power consumption is by turning off appliances
										break;
									}
									else { // set smart appliance to LOW, subtract power change from total power consumption
										appliance.setState("LOW");
										++appliancesTurnedLow[currStep]; // increment number of appliances turned to LOW in this step
										totalPowerConsumedTracker[currStep][1] -= appliance.getPowerChange();
										if (totalPowerConsumedTracker[currStep][1] <= totalAllowablePower) { // check if total power consumption is less than or equal to allowable
											totalPowerConsumedTracker[currStep][2] = totalPowerConsumedTracker[currStep][1];
											break;
										}
									}
								}

							// count the number of on appliances per room (on or low)
								for (Appliance appliance : appliances) {
									int roomID = appliance.getLocationID();
									if (!appliance.getState().equals("OFF")) {
										locationsOnOrLow.put(roomID, locationsOnOrLow.get(roomID) + 1);
									}
								}
			
							// // i dont know how to directly sort a hashmap so we will take the keys and values of the hashmap and create a room object for each pair :-)
							// Set<Map.Entry<Integer, Integer>> appOnCountSet = appOnCount.entrySet(); // entrySet() returns Set<Map.Entry<Integer, Integer>> so the we get a set of map entries from the hashmap
							// // must turn the set into an array list because the set doesnt keep track of order but list can be sorted
							// ArrayList<Map.Entry<Integer, Integer>> appOnCountList = new ArrayList<>(appOnCountSet);
							// simplify the two lines to one line
								locationsOnOrLowList = new ArrayList<>(locationsOnOrLow.entrySet());
			
							// sort the list of map entries of key: roomID and value: applianceCount by value ascending
								locationsOnOrLowList.sort(Map.Entry.comparingByValue());
			
							// now we take the keys and values each map entry in our sorted list and put them into separate arrays we can use later
								int[] sortedRoomIDs = new int[locationsOnOrLow.size()];
								int[] sortedCounts = new int[locationsOnOrLow.size()];
			
								for (int i = 0; i < locationsOnOrLowList.size(); i++) {
									Map.Entry<Integer, Integer> pair = locationsOnOrLowList.get(i);
									sortedRoomIDs[i] = pair.getKey();
									sortedCounts[i] = pair.getValue();
								}

								totalPowerConsumedTracker[currStep][2] = totalPowerConsumedTracker[currStep][1];
								if (startBrownOut) {
									numBrownOuts[currStep] = app.startBrownOut(currStep, appliances, affectedLocations, totalPowerConsumedTracker, appliancesTurnedOff, sortedRoomIDs, totalAllowablePower);
								}
							}

							for (Appliance appliance : appliances) {
								String currState = appliance.getState();
								if (currState.equals("ON")) {
									++totalAppliancesOn[currStep];
								} else if (currState.equals("LOW")) {
									++totalAppliancesLow[currStep];
								} else {
									++totalAppliancesOff[currStep];
								}

							}
						}
					}
					catch (FileNotFoundException e) {
						System.out.println("File not found: " + e.getMessage());
						e.printStackTrace();
					}
					finally {
						if (dataWriter != null) {
							dataWriter.close();
						}
					}

					
					for (int i = 0; i < numSteps; ++i) {
						int step = i + 1;
						System.out.printf("STEP %d:\n", step);
						System.out.printf("Number of appliances turned to ON:  %d\n", appliancesTurnedOn[i]);
						System.out.printf("Number of appliances turned to LOW: %d\n", appliancesTurnedLow[i]);
						System.out.printf("Number of appliances turned to OFF: %d\n", appliancesTurnedOff[i]);
						System.out.printf("Total number of appliances set to ON:  %d\n", totalAppliancesOn[i]);
						System.out.printf("Total number of appliances set to LOW: %d\n", totalAppliancesLow[i]);
						System.out.printf("Total number of appliances set to OFF: %d\n", totalAppliancesOff[i]);
						System.out.printf("Total power consumption after turning ON:   %.2f\n", totalPowerConsumedTracker[i][0]);
						System.out.printf("Total power consumption after turning LOW:  %.2f\n", totalPowerConsumedTracker[i][1]);
						System.out.printf("Total power consumption after browning out: %.2f\n", totalPowerConsumedTracker[i][2]);
						System.out.printf("Number of locations browned out: %d\n\n", numBrownOuts[i]);
					}

				// // MAXIMUM EDGE CASE 
				// 	System.out.println("Appliances still off: ");
				// 	float offPower = 0.0f;
				// 	for (Appliance appliance : appliances) {
				// 		if (appliance.getState().equals("OFF")) {
				// 			System.out.println(appliance);
				// 			offPower += appliance.getOnPower();
				// 		}
				// 	}
				// 	float missingPower = totalAllowablePower - totalPowerConsumedTracker[numSteps - 1][2];
				// 	System.out.println("Missing power from " + totalAllowablePower + ": " + missingPower);
				// 	System.out.println("Power of OFF appliances: " + offPower);

				// find max effected location - Colin
					int maxBrownOuts = Collections.max(affectedLocations.values()); // values() returns a Collection of values from map
					ArrayList<Integer> maxLocations = new ArrayList<>(); // maxLocations is a list instead of an int in case of multiple max values
					for (Map.Entry<Integer, Integer> entry : affectedLocations.entrySet()) {
						int value = entry.getValue();
						if (value == maxBrownOuts) {
							maxLocations.add(entry.getKey());
						}
					}
					Collections.sort(maxLocations);
					System.out.println("Max effected location(s): " + maxLocations);
					System.out.println("Simulation seed: " + seedString);
					System.out.println("Total allowed power: " + totalAllowablePower);

//TODO: write appliances/locations that were affected during each interval
					//use appliance IDs and location IDs
					
					break;
				case "Q":
					//quit the program
					System.out.println("Quiting the program.");
					scan.close();
					return;

				// default:
				// 	System.out.println("Invalid option. Returning to menu.");
			}
		}
	}

	// brown out handling - Colin
	private int startBrownOut(int currStep, ArrayList<Appliance> appliances, HashMap<Integer, Integer> affectedLocations, 
	float[][] totalPowerConsumedTracker, int[] appliancesTurnedOff, int[] sortedRoomIDs, float totalAllowablePower) {
		int numBrownOuts = 0;
		for (int roomID : sortedRoomIDs) {
			for (Appliance appliance : appliances) {
				if (appliance.getLocationID() == roomID) {
					totalPowerConsumedTracker[currStep][2] -= appliance.getPowerConsumption();
					appliance.setState("OFF");
					++appliancesTurnedOff[currStep];
				}
			}
			// for a brown out, increment the count of the roomID in affectedLocations
			affectedLocations.put(roomID, affectedLocations.get(roomID) + 1);
			++numBrownOuts;
			if (totalPowerConsumedTracker[currStep][2] <= totalAllowablePower) {
				break;
			}
		}
		return numBrownOuts;
	}

	// add appliance - Colin
	private void addApp(Scanner scan, ArrayList<Appliance> appliances) {
		System.out.println("Enter appliance details: ");
		try {

			//Validate Location ID - Colin
			int locationID;
			while (true) {
				locationID = readIntegerInput(scan, "Enter the location ID of the appliance (8-digit number): ");
				if (locationID >= 10000000 && locationID <= 99999999) {
					break;
				} else {
					System.out.println("Location ID must be an 8-digit number.");
				}
			}

			//Validate appliance name - Colin
			String appName;
			while (true) {
				System.out.println("Enter the name of the appliance: ");
				appName = scan.nextLine();
				if (appName != null && !appName.trim().isEmpty()) { //trim removes all leading and trailing spaces, then isEmpty checks if the string is ""
					break;
				} else {
					System.out.println("Name cannot be null or empty.");
				}
			}

			//Validate Power Used - Colin
			int onPower;
			while (true) {
				onPower = readIntegerInput(scan, "Enter the power used by the appliance in the 'ON' state (>0): ");
				if (onPower > 0) {
					break;
				} else {
					System.out.println("Power must be greater than 0.");
				}
			}

			//Validate Probability of Turning On - Colin
			float probOn;
			while (true) {
				probOn = readFloatInput(scan, "Enter the probability of appliance turning on (0 < x < 1): ");
				if (probOn >= 0.0 && probOn <= 1.0) {
					break;
				} else {
					System.out.println("Probability must be between 0 and 1.");
				}
			}

			//Validate Appliance Type - Colin
			boolean appType = readBooleanInput(scan, "Enter appliance type (true: smart, false: regular): ");
			
			//Validate Power Reduction - Colin
			float lowPowerRF;
			while (true) {
				if (appType) {
					lowPowerRF = readFloatInput(scan, "Enter the percentage reduction of power when appliance is turned to 'LOW' state (0 < x < 1): ");
					if (lowPowerRF >= 0.0 && lowPowerRF <= 1.0) {
						break;
					} else {
						System.out.println("Power reduction must be between 0 and 1.");
					}
				} else {
					lowPowerRF = 0.0f;
					break;
				}
			}

			//Create and Add new Appliance to appliance list - Colin
			Appliance newAppliance = new Appliance(locationID, appName, onPower, probOn, appType, lowPowerRF);
			appliances.add(newAppliance);
			System.out.println("A new appliance has been added to the list! Appliance ID: " + newAppliance.getAppID());
		} catch (NumberFormatException e) {
			System.out.println("Invalid number format encountered. Returning to menu.");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid input format. Returning to menu.");
		}
	}

	// delete appliance - Colin
	private void delApp(Scanner scan, ArrayList<Appliance> appliances) {
		int target;
		while (true) {
			target = readIntegerInput(scan, "Enter the ID of the appliance you would like to delete (8-digits) or (-1 to quit): ");
			if (target >= 10000000 && target <= 99999999) {
				break;
			} else if (target == -1) {
				return;
			} else {
				System.out.println("Location ID must be an 8-digit number.");
			}
		}
		
		boolean isRemoved = false;
		int i = 0;
		while (i < appliances.size()) {
			if (appliances.get(i).getAppID() == target) {
				appliances.remove(i);
				isRemoved = true;
				break;
			}
			++i;
		}

		if (isRemoved) {
			System.out.println("Appliance was deleted.");
		} else {
			System.out.println("Appliance not found.");
		}
	}

	// list appliances - Colin
	private void listApps(ArrayList<Appliance> appliances) {
		System.out.printf("| Appliance ID | Location ID  | Appliance Name                                      | Wattage | On Probability |  Type  | Low Power Reduction Factor |\n");
		System.out.printf("|====================================================================================================================================================|\n");
		for (Appliance appliance : appliances) {
			appliance.printInfo();
		}
	}

	// read input and validate integer type - Colin
	private static int readIntegerInput(Scanner scnr, String prompt) {
		int input = 0;
		boolean validInput;
		do {
			validInput = true;
			try {
				System.out.println(prompt);
				input = scnr.nextInt();
				scnr.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input error: Input is not an integer.");
				scnr.nextLine();
				validInput = false;
			}
		} while(!validInput);
		return input;
	}
	
	// read input and validate float type - Colin
	private static float readFloatInput(Scanner scnr, String prompt) {
		float input = 0.0f;
		boolean validInput;
		do {
			validInput = true;
			System.out.println(prompt);
			try {
				input = scnr.nextFloat();
				scnr.nextLine();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input error: Input is not a double.");
				scnr.nextLine();
				validInput = false;
			}
		} while(!validInput);
		return input;
	}

	// read input and validate boolean type - Colin
	private static boolean readBooleanInput(Scanner scnr, String prompt) {
		boolean input = false;
		boolean validInput;
		do {
			validInput = true;
			System.out.print(prompt);
			String inputString = scnr.nextLine();
			if (inputString.equals("true")) {
				input = true;
			} else if (inputString.equals("false")) {
				input = false;
			} else {
            	System.out.println("Invalid input error: Input is not 'true' or 'false'.");
            	validInput = false;
			}
		} while(!validInput);
		return input;
	}
	
	// read string type - Colin
	private static String readStringInput(Scanner scnr, String prompt) {
		System.out.println(prompt);
		String input = scnr.nextLine().trim();
		return input;
	}
}