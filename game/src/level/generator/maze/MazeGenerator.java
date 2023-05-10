package level.generator.maze;

import ecs.entities.TrapChest;
import ecs.entities.TrapFloor;
import level.elements.ILevel;
import level.elements.TileLevel;
import level.generator.IGenerator;
import level.tools.*;
import tools.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * A maze generator that generates floor layouts for levels with a specified size.
 */
public class MazeGenerator implements IGenerator {

    /**
     * The random number generator used to generate the maze.
     */
    private static final Random RANDOM = new Random();
    /**
     * The thickness of the walls between paths.
     */
    private static int OBSTACLE_THICKNESS = 3;
    /**
     * The width of each path in the maze.
     */
    private static int PATH_WIDTH = 3;
    /**
     * The height of each path in the maze.
     */
    private static int PATH_HEIGHT = 3;
    /**
     * The number of cells in the x direction of the maze.
     */
    private static int PATH_CELL_AMOUNT_X = 6;
    /**
     * The number of cells in the y direction of the maze.
     */
    private static int PATH_CELL_AMOUNT_Y = 6;
    /**
     * Whether to generate a surrounding wall around the maze.
     */
    private final boolean generateSurroundingWall;
    /**
     * Whether to place chests in dead ends of the maze.
     */
    private final boolean placeChestInDeadEnds;
    /**
     * The width of the maze.
     */
    private int mazeWidth;
    /**
     * The height of the maze.
     */
    private int mazeHeight;
    private LevelSize levelSize;

    /**
     * Creates a new MazeGenerator.
     *
     * @param generateWall          Whether to generate a surrounding wall around the maze.
     * @param placeChestsInDeadEnds Whether to place chests in dead ends of the maze.
     */
    public MazeGenerator(boolean generateWall, boolean placeChestsInDeadEnds) {
        this.generateSurroundingWall = generateWall;
        this.placeChestInDeadEnds = placeChestsInDeadEnds;
    }

    /**
     * Sets the given element at every point within the given area of the layout.
     *
     * @param area    The area to set the element in.
     * @param layout  The layout to set the element in.
     * @param element The element to set.
     */
    private static void setArea(Area area, LevelElement[][] layout, LevelElement element) {
        setArea(area.x, area.y, area.width + area.x, area.height + area.y, layout, element);
    }

    /**
     * Sets the given element at every point within the given rectangular area of the layout.
     *
     * @param xStart The starting x coordinate of the area to set the element in.
     * @param yStart The starting y coordinate of the area to set the element in.
     * @param xEnd   The ending x coordinate of the area to set the element in.
     * @param yEnd   The ending y coordinate of the area to set the element in.
     * @param layout The layout to set the element in.
     * @param value  The element to set.
     */
    private static void setArea(int xStart, int yStart, int xEnd, int yEnd, LevelElement[][] layout, LevelElement value) {
        for (int x = xStart; x < xEnd; x++) {
            for (int y = yStart; y < yEnd; y++) {
                layout[y][x] = value;
            }
        }
    }

    /**
     * Calculates the position of the next cell in the maze.
     *
     * @param direction The direction of the next cell.
     * @param position  The position of the current cell.
     * @return The position of the next cell.
     */
    private static Coordinate calculateNextPosition(int direction, Coordinate position) {
        return switch (direction) {
            case 0 -> new Coordinate(position.x, position.y + PATH_HEIGHT + OBSTACLE_THICKNESS);
            case 1 -> new Coordinate(position.x + PATH_WIDTH + OBSTACLE_THICKNESS, position.y);
            case 2 -> new Coordinate(position.x, position.y - (PATH_HEIGHT + OBSTACLE_THICKNESS));
            case 3 -> new Coordinate(position.x - (PATH_WIDTH + OBSTACLE_THICKNESS), position.y);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    /**
     * Calculates the position of the obstacle in a given direction from the given position
     *
     * @param direction direction in which to calculate the obstacle position (0 = north, 1 = east, 2 = south, 3 = west)
     * @param position  starting position for the obstacle
     * @return position of the obstacle
     */
    private static Coordinate calculateObstaclePosition(int direction, Coordinate position) {
        return switch (direction) {
            case 0 -> new Coordinate(position.x, position.y + PATH_HEIGHT);
            case 1 -> new Coordinate(position.x + PATH_WIDTH, position.y);
            case 2 -> new Coordinate(position.x, position.y - OBSTACLE_THICKNESS);
            case 3 -> new Coordinate(position.x - OBSTACLE_THICKNESS, position.y);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    /**
     * Generates a boolean value based on the given percentage. Returns true if a random number is less than or equal to the percentage, false otherwise.
     *
     * @param percentage percentage chance to return true
     * @return randomly generated boolean value
     * @throws IllegalArgumentException if percentage is not between 0 and 100 (inclusive)
     */
    public static boolean getBooleanWithPercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        int randomNumber = RANDOM.nextInt(100) + 1;
        return randomNumber <= percentage;
    }

    /**
     * Calculates the center point of a cell based on its top-left corner position
     *
     * @param coord position of the top-left corner of the cell
     * @return center point of the cell
     */
    private static Point calculateCellCenter(Coordinate coord) {
        float centerX = coord.x + PATH_WIDTH / 2;
        float centerY = coord.y + PATH_HEIGHT / 2;
        return new Point(centerX, centerY);
    }

    /**
     * Generates a level of a specified size based on the given design label
     *
     * @param designLabel design label for the level
     * @param size        size of the level to be generated
     * @return generated level
     */
    @Override
    public ILevel getLevel(DesignLabel designLabel, LevelSize size) {
        return new TileLevel(getLayout(size), designLabel);
    }

    /**
     * Generates the floor layout to a specified level size
     *
     * @param size size of the level to be generated
     * @return layout of the level
     */
    public LevelElement[][] getLayout(LevelSize size) {
        levelSize = size;
        return switch (size) {
            case SMALL -> generateSmall();
            case MEDIUM -> generateMedium();
            case LARGE -> generateLarge();
        };
    }

    /**
     * Determines if a position is on the edge of the maze
     *
     * @param position position to check
     * @return true if position is on the edge, false otherwise
     */
    private boolean isOnTheEdge(Coordinate position) {
        return isOnTheEdge(position.x, position.y);
    }

    /**
     * Determines if a given position is on the edge of the maze
     *
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @return true if position is on the edge, false otherwise
     */
    private boolean isOnTheEdge(int x, int y) {
        return (0 <= x && x <= OBSTACLE_THICKNESS - 1)
            || (0 <= y && y <= OBSTACLE_THICKNESS - 1)
            || (mazeWidth - 1 - (OBSTACLE_THICKNESS - 1) <= x && x <= mazeWidth - 1)
            || (mazeHeight - 1 - (OBSTACLE_THICKNESS - 1) <= y && y <= mazeHeight - 1);
    }

    /**
     * Determines if a position is on the inner edge of the maze
     *
     * @param position position to check
     * @return true if position is on the inner edge, false otherwise
     */
    private boolean isOnTheInnerEdge(Coordinate position) {
        return isOnTheInnerEdge(position.x, position.y);
    }

    /**
     * Determines if a given position is on the inner edge of the maze
     *
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @return true if position is on the inner edge, false otherwise
     */
    private boolean isOnTheInnerEdge(int x, int y) {
        return (x == OBSTACLE_THICKNESS - 1
            || y == OBSTACLE_THICKNESS - 1
            || mazeWidth - 1 - (OBSTACLE_THICKNESS - 1) == x
            || mazeHeight - 1 - (OBSTACLE_THICKNESS - 1) == y);
    }

    /**
     * Generates a floor layout for a small level
     *
     * @return generated floor layout
     */
    private LevelElement[][] generateSmall() {
        OBSTACLE_THICKNESS = RANDOM.nextInt(2, 4);
        PATH_WIDTH = RANDOM.nextInt(2, 4);
        PATH_HEIGHT = RANDOM.nextInt(2, 4);
        PATH_CELL_AMOUNT_X = RANDOM.nextInt(2, 6);
        PATH_CELL_AMOUNT_Y = RANDOM.nextInt(2, 6);
        return generateMaze();
    }

    /**
     * Generates a floor layout for a medium level
     *
     * @return generated floor layout
     */
    private LevelElement[][] generateMedium() {
        OBSTACLE_THICKNESS = RANDOM.nextInt(3, 5);
        PATH_WIDTH = RANDOM.nextInt(3, 4);
        PATH_HEIGHT = RANDOM.nextInt(3, 4);
        PATH_CELL_AMOUNT_X = RANDOM.nextInt(4, 8);
        PATH_CELL_AMOUNT_Y = RANDOM.nextInt(4, 8);
        return generateMaze();
    }

    /**
     * Generates a floor layout for a large level
     *
     * @return generated floor layout
     */
    private LevelElement[][] generateLarge() {
        OBSTACLE_THICKNESS = RANDOM.nextInt(3, 5);
        PATH_WIDTH = RANDOM.nextInt(4, 5);
        PATH_HEIGHT = RANDOM.nextInt(4, 5);
        PATH_CELL_AMOUNT_X = RANDOM.nextInt(6, 10);
        PATH_CELL_AMOUNT_Y = RANDOM.nextInt(6, 10);
        return generateMaze();
    }

    /**
     * Generates a floor layout for a level
     *
     * @return generated floor layout
     */
    private LevelElement[][] generateMaze() {
        mazeWidth = PATH_CELL_AMOUNT_X * (PATH_WIDTH + OBSTACLE_THICKNESS) + OBSTACLE_THICKNESS;
        mazeHeight = PATH_CELL_AMOUNT_Y * (PATH_HEIGHT + OBSTACLE_THICKNESS) + OBSTACLE_THICKNESS;

        LevelElement[][] layout = new LevelElement[mazeHeight][mazeWidth];

        var floorAreas = new ArrayList<Area>();
        var obstacleAreas = new ArrayList<Area>();
        generateLayoutBase(floorAreas, obstacleAreas, layout);

        var walkedPositions = new ArrayList<Coordinate>();
        var pathPositions = new ArrayList<Coordinate>();
        var currentArea = floorAreas.get(RANDOM.nextInt(0, floorAreas.size()));
        var currentPosition = new Coordinate(currentArea.x, currentArea.y);
        walkedPositions.add(currentPosition);

        var isBacktracking = true;
        while (walkedPositions.size() < PATH_CELL_AMOUNT_X * PATH_CELL_AMOUNT_Y) {
            var nextPosition = RemoveAndGetNextPosition
                (currentPosition, walkedPositions, obstacleAreas, layout);
            if (nextPosition != null) {
                currentPosition = nextPosition;
                walkedPositions.add(nextPosition);
                pathPositions.add(nextPosition);
                isBacktracking = false;
            } else {
                /*
                 * Places a chest in a dead end.
                 * Start and end of the maze are excluded.
                 * */
                if (!isBacktracking) {
                    if (placeChestInDeadEnds
                        && getBooleanWithPercentage(70)) {
                        new TrapChest(calculateCellCenter(currentPosition));
                    }
                    isBacktracking = true;
                }

                pathPositions.remove(pathPositions.size() - 1);
                currentPosition = pathPositions.get(pathPositions.size() - 1);
            }
        }

        return layout;
    }

    /**
     * Removes an obstacle area from the list of obstacle areas if the next position
     * calculated in a randomly chosen direction is valid and within an obstacle area.
     * The positions within the obstacle area are also set to LevelElement.FLOOR in the
     * level layout. Returns the next valid position, or null if all directions have
     * been checked and none are valid.
     *
     * @param currentAreaPosition The current position within an area.
     * @param walkedPositions A list of positions already walked.
     * @param obstacleAreas A list of obstacle areas.
     * @param layout The level layout.
     * @return The next valid position or null if there are no valid directions.
     */
    private Coordinate RemoveAndGetNextPosition
        (
            Coordinate currentAreaPosition,
            ArrayList<Coordinate> walkedPositions,
            ArrayList<Area> obstacleAreas,
            LevelElement[][] layout
        ) {
        var checkedDirections = new HashSet<Integer>();
        while (checkedDirections.size() < 4) {
            var direction = RANDOM.nextInt(0, 4);
            var obstaclePositionTest = calculateObstaclePosition(direction, currentAreaPosition);
            var nextPositionTest = calculateNextPosition(direction, currentAreaPosition);

            Area obstacleArea = null;
            for (var area : obstacleAreas) {
                if (!area.getPosition().equals(obstaclePositionTest)) continue;
                obstacleArea = area;
                break;
            }

            if (!isOnTheEdge(obstaclePositionTest)
                && !walkedPositions.contains(nextPositionTest)
                && obstacleAreas.contains(obstacleArea)) {
                assert obstacleArea != null;
                for (var position : obstacleArea.allPositionsWithin()) {
                    layout[position.y][position.x] = LevelElement.FLOOR;
                }

                obstacleAreas.remove(obstacleArea);

                return nextPositionTest;
            }

            checkedDirections.add(direction);
        }

        return null;
    }

    /**
     * Generates the base layout of the level. The layout consists of floor tiles,
     * holes (obstacle tiles), and walls. The positions of the floor and obstacle areas
     * are saved in separate lists. The layout is generated by looping through every
     * position in the layout and setting the appropriate LevelElement for that position.
     *
     * @param floorAreas A list to store the positions of the floor areas.
     * @param obstacleAreas A list to store the positions of the obstacle areas.
     * @param layout The level layout.
     */
    private void generateLayoutBase
        (
            ArrayList<Area> floorAreas,
            ArrayList<Area> obstacleAreas,
            LevelElement[][] layout
        ) {
        var pathSpawnPosition = new Coordinate(OBSTACLE_THICKNESS, OBSTACLE_THICKNESS);
        var horizontalObstacleSpawnPosition = new Coordinate(OBSTACLE_THICKNESS, OBSTACLE_THICKNESS + PATH_HEIGHT);
        var verticalObstacleSpawnPosition = new Coordinate(OBSTACLE_THICKNESS + PATH_WIDTH, OBSTACLE_THICKNESS);
        var horizontalStepPosition = OBSTACLE_THICKNESS;

        for (var x = 0; x < mazeWidth; x++) {
            for (var y = 0; y < mazeHeight; y++) {
                var currentCoord = new Coordinate(x, y);
                if (isOnTheEdge(currentCoord)) {
                    if (generateSurroundingWall && isOnTheInnerEdge(currentCoord)) {
                        layout[y][x] = LevelElement.WALL;
                    } else {
                        layout[y][x] = LevelElement.SKIP;
                    }
                } else if (currentCoord.equals(pathSpawnPosition)) {
                    setArea(x, y, PATH_WIDTH + x, PATH_HEIGHT + y, layout, LevelElement.FLOOR);
                    pathSpawnPosition.y += PATH_HEIGHT + OBSTACLE_THICKNESS;
                    floorAreas.add(new Area(currentCoord.x, currentCoord.y, PATH_WIDTH, PATH_HEIGHT));
                } else if (currentCoord.equals(horizontalObstacleSpawnPosition)) {
                    var horizontalObstacleArea = new Area(x, y, PATH_WIDTH, OBSTACLE_THICKNESS);
                    setArea(horizontalObstacleArea, layout, LevelElement.HOLE);
                    horizontalObstacleSpawnPosition.y += PATH_HEIGHT + OBSTACLE_THICKNESS;
                    obstacleAreas.add(horizontalObstacleArea);
                } else if (currentCoord.equals(verticalObstacleSpawnPosition)) {
                    var verticalObstacleArea = new Area(x, y, OBSTACLE_THICKNESS, PATH_HEIGHT);
                    setArea(verticalObstacleArea, layout, LevelElement.HOLE);
                    verticalObstacleSpawnPosition.y += PATH_HEIGHT + OBSTACLE_THICKNESS;
                    obstacleAreas.add(verticalObstacleArea);
                } else if (layout[y][x] == null) {
                    layout[y][x] = LevelElement.HOLE;
                }
            }

            if (horizontalStepPosition != x) continue;

            horizontalStepPosition += PATH_WIDTH + OBSTACLE_THICKNESS;
            pathSpawnPosition.x = horizontalStepPosition;
            pathSpawnPosition.y = OBSTACLE_THICKNESS;
            horizontalObstacleSpawnPosition.x = horizontalStepPosition;
            horizontalObstacleSpawnPosition.y = OBSTACLE_THICKNESS + PATH_HEIGHT;
            verticalObstacleSpawnPosition.x = horizontalStepPosition - OBSTACLE_THICKNESS;
            verticalObstacleSpawnPosition.y = OBSTACLE_THICKNESS;

            int divide = 0;

            switch (levelSize) {
                case SMALL -> divide = 1000;
                case MEDIUM -> divide = 1200;
                case LARGE -> divide = 1300;
            }

            int size = (mazeWidth * mazeHeight) / divide;

            for (int i = 0; i < size; i++) {
                Coordinate trapC = getRandomFloor(layout);
                layout[trapC.y][trapC.x] = LevelElement.TRAP;
                new TrapFloor(new Coordinate(trapC.x, trapC.y).toPoint());
            }
        }
    }

    private Coordinate getRandomFloor(LevelElement[][] layout) {
        var coordinate = new Coordinate(RANDOM.nextInt(layout[0].length), RANDOM.nextInt(layout.length));
        LevelElement randomTile = layout[coordinate.y][coordinate.x];
        if (randomTile == LevelElement.FLOOR) {
            return coordinate;
        } else {
            return getRandomFloor(layout);
        }
    }
}
