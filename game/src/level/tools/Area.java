package level.tools;

import tools.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**

 A rectangular area defined by its bottom-left corner and its width and height.
 */
public class Area {
    public final int x, y, width, height;

    /**
     * Constructs a new Area with the specified position and size.
     *
     * @param x the x-coordinate of the bottom-left corner of the area
     * @param y the y-coordinate of the bottom-left corner of the area
     * @param width the width of the area
     * @param height the height of the area
     */
    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if this area intersects with the given area.
     *
     * @param other the area to check intersection with
     * @return true if the areas intersect, false otherwise
     */
    public boolean intersects(Area other) {
        return this.x < other.x + other.width &&
            this.x + this.width > other.x &&
            this.y < other.y + other.height &&
            this.y + this.height > other.y;
    }

    /**
     * Returns the intersection of this area with the given area.
     *
     * @param other the area to intersect with
     * @return the intersection area or null if the areas don't intersect
     */
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

    /**
     * Checks if the specified point is inside this area.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return true if the point is inside the area, false otherwise
     */
    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width &&
            y >= this.y && y < this.y + this.height;
    }

    /**
     * Returns the bottom-left coordinate of this area.
     *
     * @return the bottom-left coordinate
     */
    public Coordinate getPosition() {
        return new Coordinate(x, y);
    }

    /**
     * Returns a list of all coordinates inside this area.
     *
     * @return the list of coordinates
     */
    public List<Coordinate> allPositionsWithin() {
        List<Coordinate> positions = new ArrayList<>();

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                positions.add(new Coordinate(i, j));
            }
        }

        return positions;
    }

    /**
     * Returns the center point of this area.
     *
     * @return the center point
     */
    public Point getCenterPoint() {
        float centerX = x + ((float) width / 2);
        float centerY = y + ((float) height / 2);
        return new Point(centerX, centerY);
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

