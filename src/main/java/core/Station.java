package core;

public record Station(String name, Line line) implements Comparable<Station> {

    @Override
    public int compareTo(Station station) {
        int lineComparison = line.compareTo(station.line());
        if (lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.name());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Station) obj) == 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
