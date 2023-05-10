package level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import graphic.Painter;
import graphic.PainterConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import level.elements.ILevel;
import level.elements.tile.Tile;
import level.generator.IGenerator;
import level.generator.IGeneratorStrategy;
import level.tools.DesignLabel;
import level.tools.LevelElement;
import level.tools.LevelSize;

/** Manages the level. */
public class LevelAPI {
    private final SpriteBatch batch;
    private final Painter painter;
    private final IOnLevelLoader onLevelLoader;
    private IGeneratorStrategy genStrategy;
    private List<IGenerator> gens;
    private ILevel currentLevel;
    private final transient Logger levelAPI_logger = Logger.getLogger(this.getClass().getName());

    /**
     * @param batch Batch on which to draw.
     * @param painter Who draws?
     * @param gens Level generators
     * @param onLevelLoader Object that implements the onLevelLoad method.
     */
    public LevelAPI(
            SpriteBatch batch,
            Painter painter,
            IGeneratorStrategy genStrategy,
            List<IGenerator> gens,
            IOnLevelLoader onLevelLoader) {
        this.genStrategy = genStrategy;
        this.gens = gens;
        this.batch = batch;
        this.painter = painter;
        this.onLevelLoader = onLevelLoader;
    }

    /**
     * Load a new Level
     *
     * @param size The size that the level should have
     * @param label The design that the level should have
     */
    public void loadLevel(LevelSize size, DesignLabel label) {
        currentLevel = genStrategy.generateLevel(label, size, gens);
        onLevelLoader.onLevelLoad();
        levelAPI_logger.info("A new level was loaded.");
    }

    /**
     * Load a new level with random size and the given desing
     *
     * @param designLabel The design that the level should have
     */
    public void loadLevel(DesignLabel designLabel) {
        loadLevel(LevelSize.randomSize(), designLabel);
    }

    /**
     * Load a new level with the given size and a random desing
     *
     * @param size wanted size of the level
     */
    public void loadLevel(LevelSize size) {
        loadLevel(size, DesignLabel.randomDesign());
    }

    /** Load a new level with random size and random design. */
    public void loadLevel() {
        loadLevel(LevelSize.randomSize(), DesignLabel.randomDesign());
    }

    /** Draw level */
    public void update() {
        drawLevel();
    }

    /**
     * @return The currently loaded level.
     */
    public ILevel getCurrentLevel() {
        return currentLevel;
    }

    protected void drawLevel() {
        Map<String, PainterConfig> mapping = new HashMap<>();

        Tile[][] layout = currentLevel.getLayout();
        for (int y = 0; y < layout.length; y++) {
            for (int x = 0; x < layout[0].length; x++) {
                Tile t = layout[y][x];
                if (t.getLevelElement() != LevelElement.SKIP) {
                    String texturePath = t.getTexturePath();
                    if (!mapping.containsKey(texturePath)) {
                        mapping.put(texturePath, new PainterConfig(texturePath));
                    }
                    painter.draw(
                            t.getCoordinate().toPoint(), texturePath, mapping.get(texturePath));
                }
            }
        }
    }

    /**
     * @return The currently used Level-Generators
     */
    public List<IGenerator> getGenerator() {
        return gens;
    }

    /**
     * Set the level generator
     *
     * @param generators new level generators
     */
    public void setGenerator(List<IGenerator> generators) {
        gens = generators;
    }

    /**
     * Sets the current level to the given level and calls onLevelLoad().
     *
     * @param level The level to be set.
     */
    public void setLevel(ILevel level) {
        currentLevel = level;
        onLevelLoader.onLevelLoad();
    }
}
