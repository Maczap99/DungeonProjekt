package level.generator;

import level.elements.ILevel;
import level.tools.DesignLabel;
import level.tools.LevelSize;

import java.util.List;

/**
 * Interface for generator strategies. A generator strategy is responsible for using a list of generators
 * to generate a level, according to some predetermined logic.
 */
public interface IGeneratorStrategy {

    /**
     * Generates a level using a list of generators and a design label and size.
     *
     * @param designLabel The design label to be used in the level generation.
     * @param size        The size of the level to be generated.
     * @param gens        The list of generators to be used in the level generation.
     * @return The generated level.
     */
    ILevel generateLevel(DesignLabel designLabel, LevelSize size, List<IGenerator> gens);
}
