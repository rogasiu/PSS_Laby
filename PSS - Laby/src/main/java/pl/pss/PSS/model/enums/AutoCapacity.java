package pl.pss.PSS.model.enums;

public enum AutoCapacity {
    NONE("NONE"),
    EQUAL_GREATER(">=900m3"),
    LESS("<900m3");

    private final String autoCapacity;

    AutoCapacity(String autoCapacity){
        this.autoCapacity = autoCapacity;
    }

    public String getAutoCapacity(){
        return autoCapacity;
    }
}
