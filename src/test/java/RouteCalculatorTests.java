import core.Line;
import core.Station;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public class RouteCalculatorTests extends TestCase {
    private RouteCalculator calculator;
    private StationIndex metro;
    private List<Station> route;

    @Before
    @BeforeEach
    public void setUp(){
        metro = new StationIndex();
        calculator = new RouteCalculator(metro);
        route = new ArrayList<>();

        Line redLine = new Line(1, "Красная");
        Line blueLine = new Line(2, "Синяя");
        Line greenLine = new Line(3, "Зеленая");

        redLine.addStation(new Station("Чернышевская", redLine));
        redLine.addStation(new Station("Площадь Восстания", redLine));
        redLine.addStation(new Station("Владимирская", redLine));
        greenLine.addStation(new Station("Маяковская", greenLine));
        greenLine.addStation(new Station("Гостиный двор", greenLine));
        blueLine.addStation(new Station("Невский проспект", blueLine));
        blueLine.addStation(new Station("Горьковская", blueLine));

        metro.addLine(redLine);
        metro.addLine(blueLine);
        metro.addLine(greenLine);

        route.addAll(redLine.getStations());
        route.addAll(blueLine.getStations());
        route.addAll(greenLine.getStations());

        List<Station> firstConnection = List.of(metro.getStation("Площадь Восстания"),
                metro.getStation("Маяковская"));
        metro.addConnection(firstConnection);

        List<Station> secondConnection = List.of(metro.getStation("Гостиный двор"),
                metro.getStation("Невский проспект"));
        metro.addConnection(secondConnection);
    }

    public void testGetShortestRouteOnTheLine() {
        Station from = metro.getStation("Чернышевская");
        Station to = metro.getStation("Владимирская");

        List<Station> actual = calculator.getShortestRoute(from, to);

        List<Station> expected = List.of(from, metro.getStation("Площадь Восстания"), to);

        assertEquals(expected, actual);
    }

    public void testGetShortestRouteWithOneConnection() {
        Station from = metro.getStation("Чернышевская");
        Station to = metro.getStation("Гостиный двор");

        List<Station> actual = calculator.getShortestRoute(from, to);

        List<Station> expected = List.of(from, metro.getStation("Площадь Восстания"),
                metro.getStation("Маяковская"), to);

        assertEquals(expected, actual);
    }

    public void testGetShortestRouteWithTwoConnections() {
        Station from = metro.getStation("Площадь Восстания");
        Station to = metro.getStation("Горьковская");

        List<Station> actual = calculator.getShortestRoute(from, to);

        List<Station> expected = List.of(from, metro.getStation("Маяковская"),
                metro.getStation("Гостиный двор"), metro.getStation("Невский проспект"), to);

        assertEquals(expected, actual);
    }

    public void testCalculateDuration() {
        double actual = RouteCalculator.calculateDuration(route);
        double expected = 17.0;

        assertEquals(expected, actual, 0.0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
