package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.AnimationComponent;
import ecs.components.HitboxComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.idle.NoWalk;
import ecs.components.ai.transition.RangeTransition;
import graphic.Animation;
import starter.Game;
import tools.Point;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Ghost extends Entity {

    private float secondsTillDespawnCheck;
    private final float despawnCheckFrequency = 5f;


    /**
     * Creates a Ghost at the given Position.
     * Components are initialized inside setup()
     * @param spawnpoint where the Ghost should be spawned
     */
    public Ghost(Point spawnpoint){
        super();
        setup(spawnpoint);
    }

    private void setup(Point spawnpoint){
        float xSpeed = 0.1f;
        float ySpeed = 0.1f;

        new PositionComponent(this)
            .setPosition(spawnpoint);

        Animation moveLeft = AnimationBuilder.buildAnimation("monster/ghost/idle");
        Animation moveRight = AnimationBuilder.buildAnimation("monster/ghost/idle");
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);

        Animation idleLeft = AnimationBuilder.buildAnimation("monster/ghost/idle");
        Animation idleRight = AnimationBuilder.buildAnimation("monster/ghost/idle");
        new AnimationComponent(this, idleLeft, idleRight);

        new HitboxComponent(this);

        AIComponent ai = new AIComponent(this);
        ai.setIdleAI(new NoWalk());
        ai.setTransitionAI(new RangeTransition(8f));
        ai.setFightAI(new CollideAI(0f));

        secondsTillDespawnCheck = despawnCheckFrequency;
    }

    /**
     * Gets called every frame by {@link Game}.
     * Decreases a timer by 1/30 of a second.
     * If the timer runs out there is a 1% chance for the Ghost to despawn.
     * Timer is reset Otherwise
     */
    public void despawnBehaviour(){
        if(secondsTillDespawnCheck <= 0f){
            Random random = new Random();
            int luck = random.nextInt(0, 100);
            if (luck == 0) {
                Game.removeEntity(this);
                Logger.getAnonymousLogger().log(new LogRecord(Level.INFO, "Ghost despawned due to random chance."));
            }
            else{
                secondsTillDespawnCheck = despawnCheckFrequency;
            }
        }
        else{
            secondsTillDespawnCheck -= 1f/30f;
        }
    }

}
