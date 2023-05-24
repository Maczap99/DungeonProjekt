package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.xp.XPComponent;
import graphic.Animation;

public class Monster extends Entity {

    public Monster(){
        setup();
    }

    private void setup(){
        new PositionComponent(this);
        new HealthComponent(this);
        new HitboxComponent(this);
        new AIComponent(this);
        new VelocityComponent(this);
        new AnimationComponent(this);
        new XPComponent(this);
    }
}
