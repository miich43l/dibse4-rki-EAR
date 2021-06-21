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
            var coordinates = geolocation.getPosition();
            positionFeature.setGeometry(coordinates ? new Point(coordinates) : null);
        });

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

        c.$connector.mapLayers = {}

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
        }

        c.removeMarker = function(json) {
            json = JSON.parse(json);
            var id = json.id;

            if(id in c.$connector.mapLayers) {
                c.$connector.map.removeLayer(c.$connector.mapLayers[id]);
                delete c.$connector.mapLayers[id]
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

        window.map = c.$connector.map;
        window.mapContainer = c;
    }
}