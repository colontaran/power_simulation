/* This is a stub code. You can modify it as you wish. */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

class AppClient{
	
	public void readAppFile(String file, ArrayList<Appliance> appliances){ // method to read the comma seperated appliance file.
		Scanner scan = null;
		try {
			File myFile=new File(file);
			scan=new Scanner(myFile);//each line has the format
			//locationID,name of app,onPower,probability of staying on, smart or not,Smart appliances (if "on") power reduction percent when changed to "low" status(floating point, i.e..33=33%).
			//String str;
			
			/*Complete the method*/
			while(scan.hasNextLine()) {
				String[] appAttributes = scan.nextLine().split(",");
				
				if (appAttributes.length != 6) {
                    System.out.println("Invalid format in file. Skipping this line.");
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
					System.out.println("Invalid number format encountered. Skipping this line.");
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
			System.out.println("Select an option:");
			System.out.println("Type \"A\" Add an appliance");
			System.out.println("Type \"D\" Delete an appliance");	
			System.out.println("Type \"L\" List the appliances");
			System.out.println("Type \"F\" Read Appliances from a file");
			System.out.println("Type \"S\" To Start the simulation");
			System.out.println("Type \"Q\" Quit the program");
			option1 = scan.nextLine().trim();
			System.out.println("User input: [" + option1 + "]");  // Debugging line

			/* Complete the skeleton code below */
			switch(option1) { //menu switch case - Colin
				case "A":
					//add appliance code here - Colin
					app.addApp(scan, appliances); //turned this section into a method for readability
					break;
				case "D":
					//delete appliance code here - Colin
					app.delApp(scan, appliances); //turned this section into a method for readability
					break;
				case "L":
					//list appliances code here - Colin
					app.listApps(appliances); //turned this section into a method for readability
					break;
				case "F":
					//read appliances from a file code here - Colin
					System.out.println("Enter path to file you would like to read: ");
					String fileName = scan.nextLine();
					app.readAppFile(fileName, appliances);
					break;
				case "S":
					//TODO: start the simulation code here
					int totalAllowablePower;
					while(true){ // Input for total power of simulation
						totalAllowablePower = readIntegerInput(scan, "Enter total allowable wattage for simulation: ");
						if (totalAllowablePower > 0) {
							break;
						}
					}
					
					int numSteps;
					while (true) {
						numSteps = readIntegerInput(scan, "Enter the number of steps you would like to run the simulation for: ");
						if (numSteps > 0) {
							break;
						}
					}
					
					int seed;
					String answer;
					String seedString;
					do {
						answer = readStringInput(scan, "Do you want a set seed (y/n)?");
					} while(!answer.equals("y") && !answer.equals("n"));
					Random rand;
					if (answer.equals("y")) {
						seed = readIntegerInput(scan, "Enter seed for random number generator: ");
						rand = new Random(seed);
						seedString = String.valueOf(seed);
					}
					else {
						rand = new Random();
						seedString = "none";
					}

					// create hashmap of appliance count for each room
					HashMap<Integer, Integer> applianceOnOrLowCount = new HashMap<>();
					ArrayList<Map.Entry<Integer, Integer>> applianceOnOrLowCountList;
					int[] applianceOnCount = new int[numSteps];
					int[] applianceLowCount = new int[numSteps];
					int[] numBrownOuts = new int[numSteps];
					float totalPowerConsumed;

// start loop here	
					for (int currStep = 0; currStep < numSteps; ++currStep) {
						totalPowerConsumed = 0.0f;
						
						// initialize or reset counts for each room
						for (Appliance appliance : appliances) {
							int roomID = appliance.getLocationID();
							applianceOnOrLowCount.put(roomID, 0);
						}

						// randomly turn on appliances each step and increment count if ON
						for (Appliance appliance : appliances) {
							float prob = rand.nextFloat();
							if (appliance.getProbOn() < prob) {
								appliance.setState("ON");
							}
							if (appliance.getState().equals("ON")){
								++applianceOnCount[currStep];
							}
						}

						// calculate total power consumption
						for (Appliance appliance : appliances) {
							totalPowerConsumed += appliance.getPowerConsumption(); // add wattage to total power consumption
						}
						System.out.println(totalPowerConsumed);

						// sort appliances list by "ON" smart appliances then by wattage descending
						appliances.sort(Appliance.firstSort);
						
						applianceLowCount[currStep] = 0; // number of smart appliances turned to LOW

						boolean startBrownOut = false;
						while (totalPowerConsumed > totalAllowablePower) { 
							for (Appliance appliance : appliances) { // iterate through appliances
								String currState = appliance.getState();
								if (!appliance.isSmart() || currState.equals("OFF")) {
									startBrownOut = true; // brown out starts when there only way left to decrease power consumption is by turning off appliances
									break;
								}
								else { // set smart appliance to LOW, subtract power change from total power consumption
									appliance.setState("LOW");
									++applianceLowCount[currStep]; // increment number of appliances turned to LOW in this step
									totalPowerConsumed -= appliance.getPowerChange();
									if (totalPowerConsumed <= totalAllowablePower) { // check if total power consumption is less than or equal to allowable
										break;
									}
								}
							}
							
							// start counting the number of affected appliances per room
							for (Appliance appliance : appliances) {
								int roomID = appliance.getLocationID();
								// increment count value for matching roomID of appliance if appliance is not OFF
								if (!appliance.getState().equals("OFF")) {
									applianceOnOrLowCount.put(roomID, applianceOnOrLowCount.get(roomID) + 1);
								}
							}
		
							// // i dont know how to sort a hashmap so we will take the keys and values of the hashmap and create a room object for each pair :-)
							// Set<Map.Entry<Integer, Integer>> appOnCountSet = appOnCount.entrySet(); // entrySet() returns Set<Map.Entry<Integer, Integer>> so the we get a set of map entries from the hashmap
							// // must turn the set into an array list because the set doesnt keep track of order but list can be sorted
							// ArrayList<Map.Entry<Integer, Integer>> appOnCountList = new ArrayList<>(appOnCountSet);
							// simplify the two lines to one line
							applianceOnOrLowCountList = new ArrayList<>(applianceOnOrLowCount.entrySet());
		
							// sort the list of map entries of key: roomID and value: applianceCount by value ascending
							applianceOnOrLowCountList.sort(Map.Entry.comparingByValue());
		
							// now we take the keys and values each map entry in our sorted list and put them into separate arrays we can use later
							int[] sortedRoomIDs = new int[applianceOnOrLowCountList.size()];
							int[] sortedCounts = new int[applianceOnOrLowCountList.size()];
		
							for (int i = 0; i < applianceOnOrLowCountList.size(); i++) {
								Map.Entry<Integer, Integer> pair = applianceOnOrLowCountList.get(i);
								sortedRoomIDs[i] = pair.getKey();
								sortedCounts[i] = pair.getValue();
							}

							numBrownOuts[currStep] = 0;
							if (startBrownOut) {
								numBrownOuts[currStep] = app.startBrownOut(appliances, sortedRoomIDs, totalPowerConsumed, totalAllowablePower);
								// for (int roomID : sortedRoomIDs) {

								// 	// for all appliances with matching room ID, turn OFF
								// 	for (Appliance appliance : appliances) {
								// 		int targetID = appliance.getLocationID();
								// 		if (targetID == roomID) {
								// 			appliance.setState("OFF");
								// 			// adjust total power consumed by subtracting power consumption of appliance
								// 			totalPowerConsumed -= appliance.getPowerConsumption();
								// 		}
								// 	}
								// 	++numBrownOuts[currStep]; // increment number of locations browned out
						
								// 	// check if power is below allowable if so, stop browning out and return number of brown outs
								// 	if (totalPowerConsumed <= totalAllowablePower) {
								// 		break;
								// 	}
								// }
								break;
							}
						}

						// For each time step, print to the screen, the number of smart appliances turned to “LOW” and the number of locations browned out
						System.out.println("Number of appliances turned on: " + applianceOnCount[currStep]);
						System.out.println("Number of appliances turned low: " + applianceLowCount[currStep]);
						System.out.println("Number of locations turned off: " + numBrownOuts[currStep]);
					}

					System.out.println("Simulation seed: " + seedString);
					System.out.println(Arrays.toString(applianceOnCount));
					System.out.println(Arrays.toString(applianceLowCount));
					System.out.println(Arrays.toString(numBrownOuts));
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
	private int startBrownOut(ArrayList<Appliance> appliances, int[] sortedRoomIDs, float totalPowerConsumed, float totalAllowablePower) {
		int numBrownOuts = 0;

		for (int roomID : sortedRoomIDs) {
			for (Appliance appliance : appliances) {
				if (appliance.getLocationID() == roomID) {
					appliance.setState("OFF");
					totalPowerConsumed -= appliance.getPowerConsumption();
				}
			}
			numBrownOuts++;
			if (totalPowerConsumed <= totalAllowablePower) {
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
		System.out.println("Enter the ID of the appliance you would like to delete (8-digits): ");
		int target = scan.nextInt();
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
		System.out.printf("|---------------------------------------------------------------------------------------------------------------------------------------------------|\n");
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