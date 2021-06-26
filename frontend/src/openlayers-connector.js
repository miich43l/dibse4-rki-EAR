import Map from 'ol/Map';
import View from 'ol/View';
import {Tile as TileLayer, Vector as VectorLayer} from 'ol/layer';
import Feature from 'ol/Feature';
import {Icon, Style, Circle, Fill, Text, Stroke} from 'ol/style';
import VectorSource from 'ol/source/Vector';
import {Point, LineString} from 'ol/geom';
import OSM from 'ol/source/OSM';
import {transform} from 'ol/proj.js';
import {fromLonLat} from 'ol/proj';
import Geolocation from 'ol/Geolocation';

window.Vaadin.Flow.openLayersConnector = {
    initLazy: function (c) {
        var centerLon = 11
        var centerLat = 47
        var zoom = 13

        // Check whether the connector was already initialized
        if (c.$connector) {
            return;
        }

        c.$connector = {
        };

        var mapView = new View({
            center: fromLonLat([centerLon, centerLat]),
            zoom: zoom
        })

        // GEOLOCATION:
        var geolocation = new Geolocation({
            trackingOptions: {
                enableHighAccuracy: true,
            },
            projection: mapView.getProjection(),
        });

        var accuracyFeature = new Feature();
        geolocation.on('change:accuracyGeometry', function () {
            accuracyFeature.setGeometry(geolocation.getAccuracyGeometry());
        });

        var positionFeature = new Feature();
        positionFeature.setStyle(
            new Style({
                image: new Circle({
                    radius: 6,
                    fill: new Fill({
                        color: '#3399CC',
                    }),
                    stroke: new Stroke({
                        color: '#fff',
                        width: 2,
                    }),
                }),
            })
        );

        geolocation.on('change:position', function () {
            c.updatePosition(geolocation.getPosition());
        });

        c.updatePosition = function(coordinates) {
            //console.log("update position: " + coordinates);
            positionFeature.setGeometry(coordinates ? new Point(coordinates) : null);

            if(c.positionLocked) {
                c.$connector.map.getView().setCenter(coordinates);
                c.$connector.map.getView().setZoom(15);
            }

            for(var poiId in c.$connector.mapPOIS) {
                var poi = c.$connector.mapPOIS[poiId];
                var diffX = poi[0] - coordinates[0];
                var diffY = poi[1] - coordinates[1];
                var distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

                if(distance < 50) {
                    if(c.$connector.currentPOIID == -1) {
                        c.$connector.currentPOIID = poiId;
                        console.log("visit POI: " + poiId);
                        var visitEvent = new CustomEvent("marker_visit", { detail: { markerId: poiId }});
                        c.dispatchEvent(visitEvent);

                        break;
                    }
                } else {
                    if(c.$connector.currentPOIID == poiId) {
                        console.log("leave POI: " + poiId);
                        var leaveEvent = new CustomEvent("marker_leave", { detail: { markerId: poiId }});
                        c.dispatchEvent(leaveEvent);

                        c.$connector.currentPOIID = -1;
                    }
                }
            }
        }

        var locationLayer = new VectorLayer({
            source: new VectorSource({
                features: [accuracyFeature, positionFeature],
            }),
        });

        geolocation.setTracking(true);

        c.$connector.map = new Map({
            target: c,
            layers: [
                new TileLayer({
                    source: new OSM()
                }),
                locationLayer
            ],
            view: mapView
        });

        c.$connector.mapLayers = {};
        c.$connector.mapPOIS = {};
        c.$connector.currentPOIID = -1;

        c.addMarker = function(json) {
            json = JSON.parse(json);
            var lat = json.coordinates.x;
            var lon = json.coordinates.y;
            var title = json.title;
            var id = json.id;
            var icon = json.icon;

            var pointFeature = new Feature({
                  geometry: new Point(fromLonLat([lon, lat])),
            });

            pointFeature.setStyle(
              new Style({
                text: new Text({text: title, offsetY: -28, padding: [2, 10, 2, 10], fill: new Fill({color: 'black'}), backgroundFill: new Fill({color: 'white'}), font: '16px sans-serif'}),
                image: new Icon({center: [0, 0], scale: 0.3, src: 'icons/' + icon})
              })
            );

            var newLayer = new VectorLayer({
                source: new VectorSource({
                    features: [pointFeature],
                }),
            });

            c.$connector.map.addLayer(newLayer);
            c.$connector.mapLayers[id] = newLayer;
            c.$connector.mapPOIS[id] = fromLonLat([lon, lat]);
        }

        c.removeMarker = function(json) {
            json = JSON.parse(json);
            var id = json.id;

            if(id in c.$connector.mapLayers) {
                c.$connector.map.removeLayer(c.$connector.mapLayers[id]);
                delete c.$connector.mapLayers[id];
            }

            if(id in c.$connector.mapPOIS) {
                delete c.$connector.mapPOIS[id];

                if(id == c.$connector.currentPOIID) {
                    c.$connector.currentPOIID = -1;
                }
            }
        }

        c.addRoute = function(json) {
            json = JSON.parse(json);
            var id = json.id;
            var points = [];
            for(var idx in json.points) {
                var point = json.points[idx]
                points.push([point.y, point.x])
            }

            var routeGeometry = new LineString(points);
            routeGeometry.transform('EPSG:4326', 'EPSG:3857');

            var routeFeature = new Feature({geometry: routeGeometry});
            routeFeature.setStyle(
              new Style({
                stroke : new Stroke({color: 'red', width: 3})
              })
            );

            var newLayer = new VectorLayer({
                source: new VectorSource({
                    features: [routeFeature],
                }),
            });

            c.$connector.map.addLayer(newLayer);
            c.$connector.mapLayers[id] = newLayer;
        }

        c.removeRoute = function(json) {
            json = JSON.parse(json);
            var id = json.id;

            if(id in c.$connector.mapLayers) {
                c.$connector.map.removeLayer(c.$connector.mapLayers[id]);
                delete c.$connector.mapLayers[id]
            }
        }

        c.setZoom = function(zoom) {
            c.$connector.map.getView().setZoom(zoom);
        }

        c.setCenter = function(lat, lon) {
            c.$connector.map.getView().setCenter(fromLonLat([lon, lat]));
        }

        c.setPositionSimulationRoute = function(json) {
            json = JSON.parse(json);
            var id = json.id;
            var points = [];
            for(var idx in json.points) {
                var point = json.points[idx]
                points.push([point.y, point.x])
            }

            c.positionSimulationPoints = points;
        }

        c.startPositionSimulation = function(interval) {
            c.positionSimulationTimer = setInterval(c.handlePositionSimulation, interval);
        }

        c.stopPositionSimulation = function() {
            clearInterval(c.positionSimulationTimer);
        }

        c.handlePositionSimulation = function() {
            if(c.positionSimulationPoints.length == 0) {
                c.stopPositionSimulation();
                return;
            }

            var coordinates = c.positionSimulationPoints.pop(0);
            c.updatePosition(fromLonLat([coordinates[0], coordinates[1]]));
        }

        c.lockPosition = function() {
            c.positionLocked = true;
        }

        c.unlockPosition = function() {
            c.positionLocked = false;
        }

        c.positionLocked = false;
        window.map = c.$connector.map;
        window.mapContainer = c;
    }
}