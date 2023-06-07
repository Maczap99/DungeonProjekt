package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import ecs.components.HitboxComponent;
import ecs.components.PositionComponent;
import ecs.components.collision.ICollide;
import level.elements.tile.Tile;
import starter.Game;
import tools.Point;
import tools.TrapTimer;

import java.util.Random;
import java.util.logging.Logger;

/**
 * This ist a Class for the entity TrapFloor
 * If the Player have collision with the Trap he will be slow for random seconds
 *
 */
public class TrapFloor extends Entity implements ICollide {
    private transient Sound sound;
    private transient Logger trapFloorLogger;
    private transient Logger soundLogger;

    public TrapFloor(Point position) {
        super();

        setup(position);
    }

    public void setup(Point position) {
        new PositionComponent(this, position);
        Point offset = new Point(0, 0);
        new HitboxComponent(
            this, offset, new Point(1, 1), this::onCollision, null);
    }

    @Override
    public void onCollision(Entity a, Entity b, Tile.Direction from) {

        if (b.getClass().getName().equals("ecs.entities.Hero")) {

            // get hero
            Hero hero = (Hero) Game.getHero().get();

            // slow hero down
            hero.setySpeed(0.1f);
            hero.setxSpeed(0.1f);

            hero.setupVelocityComponent();

            // get random number
            Random rand = new Random();
            int time = rand.nextInt(10) + 6;

            trapFloorLogger = Logger.getLogger(this.getClass().getName());

            if(hero.getTrapTimer() != null && !hero.getTrapTimer().isFinished()){
                TrapTimer t = hero.getTrapTimer();

                // wait random seconds
                trapFloorLogger.info((time+t.getCurrentTimeInSec()) + " Sekunden verlangsamt");

                hero.startTrapTimer((time+t.getCurrentTimeInSec()) * 1000);
            }else{
                // wait random seconds
                trapFloorLogger.info(time + " Sekunden verlangsamt");

                hero.startTrapTimer(time * 1000);
            }

            try{
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/trap/trapFloor1.mp3"));
                sound.play(0.5f);

            }catch (Exception e){
                soundLogger = Logger.getLogger(this.getClass().getName());
                soundLogger.info("Sounddatei 'trapFloor1.mp3' konnte nicht gefunden werden");
            }

            // set new speed
            Game.setHero(hero);
            Game.systems.update();
        }
    }
}
