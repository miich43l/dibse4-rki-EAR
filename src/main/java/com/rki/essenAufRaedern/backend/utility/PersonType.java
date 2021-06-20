package com.rki.essenAufRaedern.backend.utility;

public enum PersonType {
    Administration, Kitchen, Driver, Client, ContactPerson, LocalCommunity;

    public static PersonType fromString(String val) {
        switch (val) {
            case "Administrator":
                return Administration;
            case "Kitchen":
                return Kitchen;
            case "Driver":
                return Driver;
            case "Client":
                return Client;
            case "ContactPerson":
                return ContactPerson;
            case "LocalCommunity":
                return LocalCommunity;
        }

        throw new IllegalArgumentException("Invalid enum type!");
    }
}
