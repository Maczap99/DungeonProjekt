package ecs.components.skill;

import ecs.entities.Entity;

public class BuffSkill implements ISkillFunction {
    private int manaCost = 0;
    private float coolDown = 0.0f;
    private int levelRequirement = 0;

    @Override
    public void execute(Entity entity) {

    }
}
