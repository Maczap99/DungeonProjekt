package level.generator;

import level.elements.ILevel;
import level.tools.DesignLabel;
import level.tools.LevelSize;

import java.util.List;

public interface IGeneratorStrategy {
    ILevel generateLevel(DesignLabel designLabel, LevelSize size, List<IGenerator> gens);
}
