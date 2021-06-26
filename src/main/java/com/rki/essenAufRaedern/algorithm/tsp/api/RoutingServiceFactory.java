package com.rki.essenAufRaedern.algorithm.tsp.api;

/**
 * @author Thomas Widmann
 * Factory to create a new routing service instance.
 */
public class RoutingServiceFactory {

    private static final RoutingServiceFactory oInstance = new RoutingServiceFactory();

    private RoutingServiceFactory() {

    }

    public static RoutingServiceFactory get() {
        return oInstance;
    }

    public IRoutingService createGraphHopperRoutingService() {
        String strApiKey = "f60a64e4-618f-4ac2-8de4-500ab1ce5bea";
        return new RoutingServiceGraphHopper(strApiKey);
    }
}
