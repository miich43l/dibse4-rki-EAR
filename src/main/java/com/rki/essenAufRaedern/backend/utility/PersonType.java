package com.rki.essenAufRaedern.backend.utility;

public enum PersonType {
    ADMINISTRATION, KITCHEN, DRIVER, CLIENT, CONTACT_PERSON, LOCAL_COMMUNITY, DEVELOPER;

    public static PersonType fromString(String val) {
        switch (val) {
            case "Administrator":
                return ADMINISTRATION;
            case "Kitchen":
                return KITCHEN;
            case "Driver":
                return DRIVER;
            case "Client":
                return CLIENT;
            case "ContactPerson":
                return CONTACT_PERSON;
            case "LocalCommunity":
                return LOCAL_COMMUNITY;
            case "Developer":
                return DEVELOPER;
        }

        throw new IllegalArgumentException("Invalid enum type!");
    }
}
