import java.util.Comparator;

public class Appliance {

    private String appName;
    private int appID; // shouldn't be changed therefore it is constant
    private int locationID;
    private int onPower;
    private float lowPowerRF; // RF stands for reduction factor, 
    private float lowPower = 0.0f;
    private float probOn; // probability of appliance turning on in a step
    private boolean appType; // true: smart, false: regular
    private String state; // ON, LOW, OFF
    private static int count = 10000000; // 8 digit ID

    /* CONSTRUCTOR METHODS */

    public Appliance(int myLocationID, String myAppName, int myOnPower, float myProbOn, boolean myAppType, float myLowPowerRF) {
        this.locationID = myLocationID;
        this.appName = myAppName;
        this.onPower = myOnPower;
        this.probOn = myProbOn;
        this.appType = myAppType;
        this.lowPowerRF = myLowPowerRF;
        this.appID = Appliance.count++;
        if (appType && this.lowPowerRF > 0) {
            this.lowPower = this.onPower * (1 - this.lowPowerRF);
        }
        this.state = "OFF"; //default state is OFF
    }
    
    /* GETTER METHODS */

    public String getAppName() {
        return this.appName;
    }

    public int getAppID() {
        return this.appID;
    }

    public int getLocationID() {
        return this.locationID;
    }

    public int getOnPower() {
        return this.onPower;
    }

    public float getProbOn() {
        return this.probOn;
    }

    public float getLowPowerRF() {
        return this.lowPowerRF;
    }
    
    public boolean isSmart() { // getAppType() method but renamed for better readability
        return this.appType;
    }

    public String getState() {
        return this.state;
    }

    /* SETTER METHODS */

    public void setState(String myState) { // need this method to change state during simulation
        if (myState.equals("ON") || myState.equals("OFF") || (myState.equals("LOW") && this.isSmart())) {
            this.state = myState;
        } else {
            System.out.println("Invalid state. State can only be set to 'ON', 'OFF', or 'LOW' (if the appliance is smart).");
            System.out.println("Setting state to 'OFF'");
            this.state = "OFF";
        }
    }

    /* OTHER METHODS */

    public float getPowerConsumption() { // need this method to get power consumption during simulation
        switch (this.state) {
            case "ON":
                return (float) this.onPower;
            case "LOW":
                return this.lowPower;
            case "OFF":
                return 0.0f;
            default:
                System.out.println("Invalid state. State can only be set to 'ON', 'OFF', or 'LOW' (if the appliance is smart): ");
                System.out.println("Returning value: 0.0");
                return 0.0f;
        }
    }

    public float getPowerChange() { // need this method to get power consumption during simulation
        return (float) (this.onPower - this.lowPower);
    }

    public void printInfo() {
        System.out.printf("|   %-8d   |   %-8d   | %-50s  | %7d | %14.4f | %-6b | %26.2f |\n", 
        this.appID, this.locationID, this.appName, this.onPower, this.probOn, this.appType, this.lowPowerRF);
    }

    public void printUserAddedAppInfo(String newAppId, int newLocationID, String newAppName, int newOnPower, float newProbOn, boolean newAppType, float newLowPowerRF) {
        System.out.printf("|   %-8d   |   %-8d   | %-50s  | %7d | %14.4f | %-6b | %26.2f |\n", 
        newAppId, newLocationID, newAppName, newOnPower, newProbOn, newAppType, newLowPowerRF);
    }

    // Comparator for sorting the list by ON state first and then OnPower descending
    public static Comparator<Appliance> firstSort = 
        Comparator
        .comparing(Appliance::getState) // Sort by states in ascending order so LOW, then OFF, then ON
        .reversed() // reverse to have ON come first
        .thenComparing(
            Comparator
            .comparing(Appliance::isSmart) // then sort by smart appliance
            .reversed()) // Sort by so smart appliance (true: 1) shows up first
            .thenComparing(
                Comparator
                .comparingInt(Appliance::getOnPower) // then sort by onPower
                .reversed()); // Sort by onPower in descending order

    public static Comparator<Appliance> location = Comparator.comparingInt(Appliance::getLocationID); // Sort by location in ascending order

    @Override
    public String toString() {
 
        return "[ appID=" + appID + ", locationID="
               + locationID + ", appName=" + appName
                + ", onPower=" + onPower + ", probOn="
                 + probOn + ", appType=" + appType + 
                 ", lowPowerRF=" + lowPowerRF + "]";
    }
}