package com.rki.essenAufRaedern.algorithm.tsp.api;

/**
 * @author Thomas Widmann
 * Factory to create a new routing service instance.
 */
public class RoutingServiceFactory {

    private static final RoutingServiceFactory instance = new RoutingServiceFactory();

    public static RoutingServiceFactory get() {
        return instance;
    }

    public IRoutingService createGraphHopperRoutingService() {
        String strApiKey = "8c543b1b-33ff-4a7b-98ae-8dc88471391d";
        return new RoutingServiceGraphHopper(strApiKey);
    }
}
