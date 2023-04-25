package level.generator.maze;

import ecs.entities.Chest;
import ecs.items.ItemData;
import level.elements.ILevel;
import level.elements.TileLevel;
import level.generator.IGenerator;
import level.tools.*;
import tools.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MazeGenerator implements IGenerator {
    private static final Random RANDOM = new Random();

    private static int OBSTACLE_THICKNESS = 3;
    private static int PATH_WIDTH = 3;
    private static int PATH_HEIGHT = 3;
    private static int PATH_CELL_AMOUNT_X = 6;
    private static int PATH_CELL_AMOUNT_Y = 6;
    private final boolean generateSurroundingWall;
    private final boolean placeChestInDeadEnds;
    private int mazeWidth;
    private int mazeHeight;

    public MazeGenerator(boolean generateWall, boolean placeChestsInDeadEnds) {
        this.generateSurroundingWall = generateWall;
        this.placeChestInDeadEnds = placeChestsInDeadEnds;
    }

    private static void setArea(Area area, LevelElement[][] layout, LevelElement element) {
        setArea(area.x, area.y, area.width + area.x, area.height + area.y, layout, element);
    }

    private static void setArea
        (int xStart, int yStart, int xEnd, int yEnd, LevelElement[][] layout, LevelElement value) {
        for (int x = xStart; x < xEnd; x++) {
            for (int y = yStart; y < yEnd; y++) {
                layout[y][x] = value;
            }
        }
    }

    private static Coordinate calculateNextPosition(int direction, Coordinate position) {
        return switch (direction) {
            case 0 -> new Coordinate(position.x, position.y + PATH_HEIGHT + OBSTACLE_THICKNESS);
            case 1 -> new Coordinate(position.x + PATH_WIDTH + OBSTACLE_THICKNESS, position.y);
            case 2 -> new Coordinate(position.x, position.y - (PATH_HEIGHT + OBSTACLE_THICKNESS));
            case 3 -> new Coordinate(position.x - (PATH_WIDTH + OBSTACLE_THICKNESS), position.y);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    private static Coordinate calculateObstaclePosition(int direction, Coordinate position) {
        return switch (direction) {
            case 0 -> new Coordinate(position.x, position.y + PATH_HEIGHT);
            case 1 -> new Coordinate(position.x + PATH_WIDTH, position.y);
            case 2 -> new Coordinate(position.x, position.y - OBSTACLE_THICKNESS);
            case 3 -> new Coordinate(position.x - OBSTACLE_THICKNESS, position.y);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    public static boolean getBooleanWithPercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        int randomNumber = RANDOM.nextInt(100) + 1;
        return randomNumber <= percentage;
    }

    private static Point calculateCellCenter(Coordinate coord) {
        float centerX = coord.x + PATH_WIDTH / 2;
        float centerY = coord.y + PATH_HEIGHT / 2;
        return new Point(centerX, centerY);
    }

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
        return switch (size) {
            case SMALL -> generateSmall();
            case MEDIUM -> generateMedium();
            case LARGE -> generateLarge();
        };
    }

    private boolean isOnTheEdge(Coordinate position) {
        return isOnTheEdge(position.x, position.y);
    }

    private boolean isOnTheEdge(int x, int y) {
        return (0 <= x && x <= OBSTACLE_THICKNESS - 1)
            || (0 <= y && y <= OBSTACLE_THICKNESS - 1)
            || (mazeWidth - 1 - (OBSTACLE_THICKNESS - 1) <= x && x <= mazeWidth - 1)
            || (mazeHeight - 1 - (OBSTACLE_THICKNESS - 1) <= y && y <= mazeHeight - 1);
    }

    private boolean isOnTheInnerEdge(Coordinate position) {
        return isOnTheInnerEdge(position.x, position.y);
    }

    private boolean isOnTheInnerEdge(int x, int y) {
        return (x == OBSTACLE_THICKNESS - 1
            || y == OBSTACLE_THICKNESS - 1
            || mazeWidth - 1 - (OBSTACLE_THICKNESS - 1) == x
            || mazeHeight - 1 - (OBSTACLE_THICKNESS - 1) == y);
    }

    private LevelElement[][] generateSmall() {
        OBSTACLE_THICKNESS = RANDOM.nextInt(2, 4);
        PATH_WIDTH = RANDOM.nextInt(2, 4);
        PATH_HEIGHT = RANDOM.nextInt(2, 4);
        PATH_CELL_AMOUNT_X = RANDOM.nextInt(2, 7);
        PATH_CELL_AMOUNT_Y = RANDOM.nextInt(2, 7);
        return generateMaze();
    }

    private LevelElement[][] generateMedium() {
        OBSTACLE_THICKNESS = RANDOM.nextInt(3, 5);
        PATH_WIDTH = RANDOM.nextInt(3, 5);
        PATH_HEIGHT = RANDOM.nextInt(3, 5);
        PATH_CELL_AMOUNT_X = RANDOM.nextInt(3, 8);
        PATH_CELL_AMOUNT_Y = RANDOM.nextInt(3, 8);
        return generateMaze();
    }

    private LevelElement[][] generateLarge() {
        OBSTACLE_THICKNESS = RANDOM.nextInt(3, 5);
        PATH_WIDTH = RANDOM.nextInt(4, 6);
        PATH_HEIGHT = RANDOM.nextInt(4, 6);
        PATH_CELL_AMOUNT_X = RANDOM.nextInt(4, 9);
        PATH_CELL_AMOUNT_Y = RANDOM.nextInt(4, 9);
        return generateMaze();
    }

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
                        new Chest(new ArrayList<>(), calculateCellCenter(currentPosition));
                    }
                    isBacktracking = true;
                }

                pathPositions.remove(pathPositions.size() - 1);
                currentPosition = pathPositions.get(pathPositions.size() - 1);
            }
        }

        return layout;
    }

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
        }
    }
}
