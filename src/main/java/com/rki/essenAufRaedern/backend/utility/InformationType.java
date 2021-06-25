package com.rki.essenAufRaedern.backend.utility;

public enum InformationType {
    KITCHEN, DRIVER, ADMINISTRATION;

    public static InformationType fromString(String val) {
        switch (val) {
            case "Kitchen":
                return KITCHEN;
            case "Driver":
                return DRIVER;
            case "Administration":
                return ADMINISTRATION;
        }
        throw new IllegalArgumentException("Invalid enum type!");
    }

    @Override
    public String toString() {
        return switch (this) {
            case KITCHEN -> "Kitchen";
            case DRIVER -> "Driver";
            case ADMINISTRATION -> "Administration";
        };
    }
}
