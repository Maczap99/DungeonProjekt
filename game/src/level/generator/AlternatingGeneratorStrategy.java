package level.generator;

import level.elements.ILevel;
import level.tools.DesignLabel;
import level.tools.LevelSize;

import java.util.List;

public class AlternatingGeneratorStrategy implements IGeneratorStrategy {
    private int currentGeneratorIndex;

    @Override
    public ILevel generateLevel(DesignLabel designLabel, LevelSize size, List<IGenerator> gens) {
        IGenerator currentGenerator = gens.get(currentGeneratorIndex);
        ILevel level = currentGenerator.getLevel(designLabel, size);
        currentGeneratorIndex = (currentGeneratorIndex + 1) % gens.size();
        return level;
    }
}
