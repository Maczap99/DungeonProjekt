package ecs.entities;

import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.xp.XPComponent;


/**
 * A base class for all Monsters.
 * The attributes of a Monster are set inside the MonsterBuilder Class
 */
public class Monster extends Entity {

    public Monster(){
        super();
        setup();
    }

    private void setup(){
        new PositionComponent(this);
        new HealthComponent(this);
        new HitboxComponent(this);
        new AIComponent(this);
        new XPComponent(this);
    }
}
