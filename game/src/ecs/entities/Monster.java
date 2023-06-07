package ecs.entities;

import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.collision.ICollide;
import ecs.components.xp.XPComponent;
import ecs.damage.Damage;
import ecs.damage.DamageType;


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


        ICollide collide =
            (a, b, from) -> {
                if (b != this) {
                    b.getComponent(HealthComponent.class)
                        .ifPresent(
                            hc -> {
                                ((HealthComponent) hc).receiveHit(new Damage(1, DamageType.PHYSICAL, this));
                            });
                }
            };

        new HitboxComponent(this);
        new AIComponent(this);
        new VelocityComponent(this);
        new AnimationComponent(this);
        new XPComponent(this);
    }
}
