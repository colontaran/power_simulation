/* This is a stub code. You can modify it as you wish. */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

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
			option1=scan.nextLine();
			/* Complete the skeleton code below */
			switch(option1) {
				case "A":
					//add appliance code here
					app.addApp(scan, appliances); //turned this section into a method for readability
					break;
				case "D":
					//delete appliance code here
					app.delApp(scan, appliances); //turned this section into a method for readability
					break;
				case "L":
					//TODO: list appliances code here

					break;
				case "F":
					//read appliances from a file code here
					System.out.println("Enter path to file you would like to read: ");
					String fileName = scan.nextLine();
					app.readAppFile(fileName, appliances);
					System.out.println("Appliances from the file have been added to the list!");
					break;
				case "S":
					//TODO: start the simulation code here
					break;
				case "Q":
					//quit the program
					break;
				default:
					System.out.println("Invalid option. Returning to menu.");
					continue; //loop back for another attempt
			}
			if (option1.equals("Q")) {
				break; //exits the while if user chooses to quit
			}
			
		}
		scan.close();
		
	}

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
	
}