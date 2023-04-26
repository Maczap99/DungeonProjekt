package level.generator;

import level.elements.ILevel;
import level.tools.DesignLabel;
import level.tools.LevelSize;

import java.util.List;

/**
 * A generator strategy that alternates between multiple generators.
 */
public class AlternatingGeneratorStrategy implements IGeneratorStrategy {

    /**
     * The index of the current generator to use.
     */
    private int currentGeneratorIndex;

    /**
     * Generates a level using the given design label, level size, and list of generators,
     * alternating between the generators to use.
     *
     * @param designLabel The design label to use for generating the level.
     * @param size The size of the level to generate.
     * @param gens The list of generators to alternate between.
     * @return The generated level.
     */
    @Override
    public ILevel generateLevel(DesignLabel designLabel, LevelSize size, List<IGenerator> gens) {
        IGenerator currentGenerator = gens.get(currentGeneratorIndex);
        ILevel level = currentGenerator.getLevel(designLabel, size);
        currentGeneratorIndex = (currentGeneratorIndex + 1) % gens.size();
        return level;
    }
}
