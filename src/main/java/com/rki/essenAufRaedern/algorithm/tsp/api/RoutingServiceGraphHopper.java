package com.rki.essenAufRaedern.algorithm.tsp.api;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class RoutingServiceGraphHopper implements IRoutingService {

    public RoutingServiceGraphHopper(String strApiKey) {
        m_strApiKey = strApiKey;
    }

    @Override
    public AdjacencyMatrix requestAdjacencyMatrix(List<Point2D> coordinates) {
        if(coordinates.size() < 2) {
            throw new IllegalArgumentException("Too less points! 2 Points are minimum.");
        }

        String strURI = createAdjacencyMatrixURI(coordinates);
        String strApiResult = performApiRequestSync(strURI);

        return createAdjacencyMatrixFromJson(strApiResult);
    }

    @Override
    public TspPath requestPathBetweenPoints(List<Point2D> coordinates) {
        if(coordinates.size() < 2) {
            throw new IllegalArgumentException("Too less points! 2 Points are minimum.");
        }

        String strURI = createRouteURI(coordinates);
        String strApiResult = performApiRequestSync(strURI);

        return createPathFromJson(strApiResult);
    }

    @Override
    public Point2D requestCoordinateFromAddress(String address) {
        String strURI = createGeocodingURI(address);
        String strApiResult = performApiRequestSync(strURI);

        return createCoordinatePointFromJson(strApiResult);
    }

    private String getBaseURI() {
        return "https://graphhopper.com/api/1/";
    }

    private String getMatrixBaseURI() {
        return getBaseURI() + "matrix?";
    }

    private String getRouteBaseURI() {
        return getBaseURI() + "route?";
    }

    private String getGeocodingBaseURI() {
        return getBaseURI() + "geocode?";
    }

    private String createPointListString(List<Point2D> points) {
        StringBuilder oStringBuilder = new StringBuilder();

        for(Point2D oPoint : points) {
            oStringBuilder.append("point=");
            oStringBuilder.append(oPoint.getX());
            oStringBuilder.append(",");
            oStringBuilder.append(oPoint.getY());
            oStringBuilder.append("&");
        }

        return oStringBuilder.toString();
    }

    private String createAdjacencyMatrixURI(List<Point2D> lstPoints) {
        return getMatrixBaseURI() +
                createPointListString(lstPoints) +
                "type=json&vehicle=car&debug=true&out_array=weights";
    }

    private String createRouteURI(List<Point2D> coordinates) {
        return getRouteBaseURI() +
                createPointListString(coordinates) +
                "&vehicle=car&locale=de&calc_points=true&points_encoded=false";
    }

    private String createGeocodingURI(String query) {
        String encodedQuery = null;
        try {
            encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }

        return getGeocodingBaseURI() +
                "q=" +
                encodedQuery +
                "&locale=de&debug=true";
    }

    private String performApiRequestSync(String strURI) {
        StringBuilder oStringBuilder = new StringBuilder();
        oStringBuilder.append(strURI);
        oStringBuilder.append("&key=");
        oStringBuilder.append(m_strApiKey);

        System.out.println("GraphHopper request: " + oStringBuilder.toString());

        try {
            URL url = new URL(oStringBuilder.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();

            InputStream inStream = connection.getInputStream();
            return new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();

        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private AdjacencyMatrix createAdjacencyMatrixFromJson(String strJson) {
        AdjacencyMatrix oMatrix;

        JSONParser oParser = new JSONParser(strJson);
        try {
            LinkedHashMap<String, Object> oRootObj = oParser.object();
            ArrayList<ArrayList<BigDecimal>> lstWeights2D = (ArrayList<ArrayList<BigDecimal>>) oRootObj.get("weights");
            oMatrix = new AdjacencyMatrix(lstWeights2D.size());

            int y = 0;
            for(ArrayList<BigDecimal> lstWeights : lstWeights2D) {
                int x = 0;
                for(BigDecimal dVal : lstWeights) {
                    oMatrix.setElement(y, x, dVal.doubleValue());
                    x++;
                }
                y++;
            }

            return oMatrix;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new AdjacencyMatrix(0);
    }

    private TspPath createPathFromJson(String strJson) {
        TspPath path = new TspPath();
        JSONParser oParser = new JSONParser(strJson);
        LinkedHashMap<String, Object> oRootObj = null;
        try {
            oRootObj = oParser.object();
        } catch (ParseException e) {
            e.printStackTrace();
            return path;
        }
        ArrayList<LinkedHashMap<String, Object>> lstPaths = (ArrayList<LinkedHashMap<String, Object>>) oRootObj.get("paths");

        if(lstPaths.size() < 1) {
            return path;
        }

        LinkedHashMap<String, Object> oPointsObj = (LinkedHashMap<String, Object>) lstPaths.get(0).get("points");
        ArrayList<ArrayList<BigDecimal>> lstCoordinates = (ArrayList<ArrayList<BigDecimal>>) oPointsObj.get("coordinates");

        for(ArrayList<BigDecimal> lstCoordinate : lstCoordinates) {
            double dLon = lstCoordinate.get(0).doubleValue();
            double dLat = lstCoordinate.get(1).doubleValue();

            path.addPoint(new Point2D.Double(dLat, dLon));
        }

        return path;
    }

    private Point2D createCoordinatePointFromJson(String strJson) {

        Point2D point = new Point2D.Double();

        JSONParser oParser = new JSONParser(strJson);
        LinkedHashMap<String, Object> oRootObj = null;
        try {
            oRootObj = oParser.object();
        } catch (ParseException e) {
            e.printStackTrace();
            return point;
        }
        ArrayList<LinkedHashMap<String, Object>> hits = (ArrayList<LinkedHashMap<String, Object>>) oRootObj.get("hits");

        if(hits.size() < 1) {
            return point;
        }

        LinkedHashMap<String, Object> bestHit = (LinkedHashMap<String, Object>) hits.get(0);
        LinkedHashMap<String, Object> coordinates = (LinkedHashMap<String, Object>) bestHit.get("point");
        String strLat = coordinates.get("lat").toString();
        String strLon = coordinates.get("lng").toString();
        double dLat = Double.parseDouble(strLat);
        double dLon = Double.parseDouble(strLon);

        point.setLocation(dLat, dLon);
        return point;
    }

    private final String m_strApiKey;
}
