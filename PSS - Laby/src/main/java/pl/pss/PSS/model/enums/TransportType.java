package pl.pss.PSS.model.enums;

public enum TransportType {
    CAR("Auto"),
    TRAIN("Pociąg"),
    BUS("Autobus");

    private final String transportType;

    TransportType(String transportType){
        this.transportType = transportType;
    }

    public String getTransportType() {
        return transportType;
    }
}
