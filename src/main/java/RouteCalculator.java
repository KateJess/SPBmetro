import core.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RouteCalculator {
    private final StationIndex stationIndex;

    private static final double INTER_STATION_DURATION = 2.5;
    private static final double INTER_CONNECTION_DURATION = 3.5;

    public RouteCalculator(StationIndex stationIndex) {
        this.stationIndex = stationIndex;
    }

    public List<Station> getShortestRoute(Station from, Station to) {
        List<Station> route = getRouteOnTheLine(from, to);
        if (!route.isEmpty()) {
            return route;
        }

        route = getRouteWithOneConnection(from, to);
        if (!route.isEmpty()) {
            return route;
        }

        route = getRouteWithTwoConnections(from, to);
        return route;
    }

    public static double calculateDuration(List<Station> route) {
        double duration = 0;
        Station previousStation = null;
        for (int i = 0; i < route.size(); i++) {
            Station station = route.get(i);
            if (i > 0) {
                duration += previousStation.line().equals(station.line()) ?
                        INTER_STATION_DURATION : INTER_CONNECTION_DURATION;
            }
            previousStation = station;
        }
        return duration;
    }

    private List<Station> getRouteOnTheLine(Station from, Station to) {
        if (!from.line().equals(to.line())) {
            return Collections.EMPTY_LIST;
        }
        List<Station> route = new ArrayList<>();
        List<Station> stations = from.line().getStations();
        int direction = 0;
        for (Station station : stations) {
            if (direction == 0) {
                if (station.equals(from)) {
                    direction = 1;
                } else if (station.equals(to)) {
                    direction = -1;
                }
            }

            if (direction != 0) {
                route.add(station);
            }

            if ((direction == 1 && station.equals(to)) ||
                    (direction == -1 && station.equals(from))) {
                break;
            }
        }
        if (direction == -1) {
            Collections.reverse(route);
        }
        return route;
    }

    private List<Station> getRouteWithOneConnection(Station from, Station to) {
        if (from.line().equals(to.line())) {
            return Collections.EMPTY_LIST;
        }

        List<Station> route = new ArrayList<>();

        List<Station> fromLineStations = from.line().getStations();
        List<Station> toLineStations = to.line().getStations();
        for (Station srcStation : fromLineStations) {
            for (Station dstStation : toLineStations) {
                if (isConnected(srcStation, dstStation)) {
                    ArrayList<Station> way = new ArrayList<>();
                    way.addAll(getRouteOnTheLine(from, srcStation));
                    way.addAll(getRouteOnTheLine(dstStation, to));
                    if (route.isEmpty() || route.size() > way.size()) {
                        route.clear();
                        route.addAll(way);
                    } else {
                        return way;
                    }
                }
            }
        }
        return route;
    }

    private boolean isConnected(Station station1, Station station2) {
        Set<Station> connected = stationIndex.getConnectedStations(station1);
        return connected.contains(station2);
    }

    private List<Station> getRouteViaConnectedLine(Station from, Station to) {
        Set<Station> fromConnected = stationIndex.getConnectedStations(from);
        Set<Station> toConnected = stationIndex.getConnectedStations(to);
        for (Station srcStation : fromConnected) {
            for (Station dstStation : toConnected) {
                if (srcStation.line().equals(dstStation.line())) {
                    return getRouteOnTheLine(srcStation, dstStation);
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    private List<Station> getRouteWithTwoConnections(Station from, Station to) {
        if (from.line().equals(to.line())) {
            return Collections.EMPTY_LIST;
        }

        ArrayList<Station> route = new ArrayList<>();

        List<Station> fromLineStations = from.line().getStations();
        List<Station> toLineStations = to.line().getStations();

        for (Station srcStation : fromLineStations) {
            for (Station dstStation : toLineStations) {
                List<Station> connectedLineRoute =
                        getRouteViaConnectedLine(srcStation, dstStation);
                if (connectedLineRoute.isEmpty()) {
                    continue;
                }
                List<Station> way = new ArrayList<>();
                way.addAll(getRouteOnTheLine(from, srcStation));
                way.addAll(connectedLineRoute);
                way.addAll(getRouteOnTheLine(dstStation, to));
                if (route.isEmpty() || route.size() > way.size()) {
                    route.clear();
                    route.addAll(way);
                } else {
                    return way;
                }
            }
        }

        return route;
    }
}
