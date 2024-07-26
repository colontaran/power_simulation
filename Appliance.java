public class Appliance {

    private String applianceID;
    private String locationID;
    private String appName;
    private int onPower;
    private float probOn;
    private boolean appType;
    private float lowPower;
    private String state;
    private static int cnt = 100000;

    public Appliance(String myLocationID, String myAppName, int myOnPower, float myProbOn, boolean myAppType, float myLowPower) {
        this.locationID = myLocationID;
        this.appName = myAppName;
        this.onPower = myOnPower;
        this.probOn = myProbOn;
        this.appType = myAppType;
        this.lowPower = myLowPower;
        this.applianceID = "" + cnt;
        ++Appliance.cnt; //increment cnt every time new appliance is created
    }
    
    public String getApplianceID() {
        return this.applianceID;
    }

    public void setApplianceID(String myApplianceID) {
        if (isIDValid(myApplianceID)) {
            this.applianceID = myApplianceID;
        } else {
            this.applianceID = "" + cnt;
            ++Appliance.cnt;
        }
    }

    public String getLocationID() {
        return this.locationID;
    }

    public void setLocationID(String myLocationID) {
        if (isIDValid(myLocationID)) {
            this.locationID = myLocationID;
        } else {
            System.out.println("Invalid Location ID. It must be an 8-digit string.");
        }
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getOnPower() {
        return this.onPower;
    }

    public void setOnPower(int onPower) {
        this.onPower = onPower;
    }

    public float getProbOn() {
        return this.probOn;
    }

    public void setProbOn(float probOn) {
        this.probOn = probOn;
    }

    public boolean getAppType() {
        return this.appType;
    }

    public void setAppType(boolean appType) {
        this.appType = appType;
    }

    public float getLowPower() {
        return this.lowPower;
    }

    public void setLowPower(float lowPower) {
        this.lowPower = lowPower;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String myState) {
        if (myState.equals("ON") || myState.equals("OFF") || (myState.equals("LOW") && this.getAppType())) {
            this.state = myState;
        } else {
            System.out.println("Invalid state. State can only be set to 'ON', 'OFF', or 'LOW' (if the appliance is smart).");
            System.out.println("Setting state to 'OFF'");
            this.state = "OFF";
        }
    }

    public float getPowerConsumption() {
        String state = this.getState();
        
        if (state.equals("ON")) {
            return (float) this.onPower;
        } else if (state.equals("LOW") && this.getAppType()) {
            return (float) this.onPower * (1 - this.getLowPower());
        } else {
            return 0.0f;
        }
    }

    private boolean isIDValid(String myID) {
        // Check if the string is null
        if (myID == null) {
            return false;
        }
        // Check if the string length is exactly 8
        if (myID.length() != 8) {
            return false;
        }
        // Check if all characters are digits
        for (int i = 0; i < myID.length(); i++) {
            if (!Character.isDigit(myID.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}