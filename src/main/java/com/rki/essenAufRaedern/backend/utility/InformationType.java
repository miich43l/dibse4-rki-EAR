package com.rki.essenAufRaedern.backend.utility;

public enum InformationType {
    Kitchen, Driver, Administration;

    public static InformationType fromString(String val) {
        switch (val) {
            case "Kitchen":
                return Kitchen;
            case "Driver":
                return Driver;
            case "Administration":
                return Administration;
        }
        throw new IllegalArgumentException("Invalid enum type!");
    }

    @Override
    public String toString() {
        return switch (this) {
            case Kitchen -> "Kitchen";
            case Driver -> "Driver";
            case Administration -> "Administration";
        };
    }
}
