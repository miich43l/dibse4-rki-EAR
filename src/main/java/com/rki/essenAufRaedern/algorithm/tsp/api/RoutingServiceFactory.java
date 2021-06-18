package com.rki.essenAufRaedern.algorithm.tsp.api;

public class RoutingServiceFactory {

    private static final RoutingServiceFactory oInstance = new RoutingServiceFactory();

    private RoutingServiceFactory() {

    }

    public static RoutingServiceFactory get() {
        return oInstance;
    }

    public IRoutingService createGraphHopperRoutingService() {
        String strApiKey = "e84f7985-a334-44e0-834a-7b085b38935d";
        return new RoutingServiceGraphHopper(strApiKey);
    }
}
