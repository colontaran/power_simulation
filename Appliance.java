public class Appliance {

    private String appName;
    private final int appID; // shouldn't be changed therefore it is constant
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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    // appID has no setter method since it is a constant variable

    public void setLocationID(int myLocationID) { // is this method needed? when are we changing locationID
        if (isIDValid(myLocationID)) {
            this.locationID = myLocationID;
        } else {
            System.out.println("Invalid Location ID. It must be an 8-digit number.");
        }
    }
    
    public void setOnPower(int onPower) { // is this method needed? when are we changing an appliance's wattage
        this.onPower = onPower;
    }

    public void setProbOn(float probOn) { // is this method needed? why would we change the probability of an appliance turning on
        this.probOn = probOn;
    }

    public void setLowPowerRF(float myLowPowerRF) { // is this method needed? why would we change an appliance's power efficiency
        this.lowPowerRF = myLowPowerRF;
    }

    public void setAppType(boolean appType) { // is this method needed? when would a method ever go from a smart appliance to a regular appliance, vice versa
        this.appType = appType;
        // recalculate lowPower if appType is changed
        if (this.appType && this.lowPowerRF > 0) {
            this.lowPower = this.onPower * (1 - this.lowPowerRF);
        } else {
            this.lowPower = 0.0f;
        }
    }

    public void setState(String myState) { // need this method to change state during simulation
        if (myState.equals("ON") || myState.equals("OFF") || (myState.equals("LOW") && this.isSmart())) {
            this.state = myState;
        } else {
            System.out.println("Invalid state. State can only be set to 'ON', 'OFF', or 'LOW' (if the appliance is smart).");
            System.out.println("Setting state to 'OFF'");
            this.state = "OFF";
        }
    }

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

    private boolean isIDValid(int myID) { // is this method needed? its only used when changing an ID
        // Check if the ID is 8 digits
        return (myID >= 10000000 && myID <= 99999999);
    }
}