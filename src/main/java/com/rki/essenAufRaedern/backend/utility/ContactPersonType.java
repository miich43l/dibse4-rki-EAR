package com.rki.essenAufRaedern.backend.utility;

public enum ContactPersonType {
    FAMILY_MEMBER, NEIGHBOR, DOCTOR;

    @Override
    public String toString() {
        return switch (this) {
            case FAMILY_MEMBER -> "Familienmitglied";
            case NEIGHBOR -> "Nachbar";
            case DOCTOR -> "Arzt";
        };
    }
}
