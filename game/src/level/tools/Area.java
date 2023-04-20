package level.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Area {
    public final int x, y, width, height;

    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(Area other) {
        return this.x < other.x + other.width &&
            this.x + this.width > other.x &&
            this.y < other.y + other.height &&
            this.y + this.height > other.y;
    }

    public Area intersection(Area other) {
        int newX = Math.max(this.x, other.x);
        int newY = Math.max(this.y, other.y);
        int newWidth = Math.min(this.x + this.width, other.x + other.width) - newX;
        int newHeight = Math.min(this.y + this.height, other.y + other.height) - newY;

        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        return new Area(newX, newY, newWidth, newHeight);
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width &&
            y >= this.y && y < this.y + this.height;
    }

    public Coordinate getPosition() {
        return new Coordinate(x, y);
    }

    public List<Coordinate> allPositionsWithin() {
        List<Coordinate> positions = new ArrayList<>();

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                positions.add(new Coordinate(i, j));
            }
        }

        return positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area area = (Area) o;
        return x == area.x &&
            y == area.y &&
            width == area.width &&
            height == area.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Area{" +
            "x=" + x +
            ", y=" + y +
            ", width=" + width +
            ", height=" + height +
            '}';
    }
}

