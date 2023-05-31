package ecs.components.ai.idle;

import ecs.entities.Entity;

public class NoWalk implements IIdleAI{
    @Override
    public void idle(Entity entity) {
        //Does not move.
    }
}
