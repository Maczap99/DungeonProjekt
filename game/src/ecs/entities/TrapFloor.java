package ecs.entities;

import ecs.components.HitboxComponent;
import ecs.components.PositionComponent;
import ecs.components.collision.ICollide;
import level.elements.tile.Tile;
import starter.Game;
import tools.Point;
import tools.TrapTimer;

import java.util.Random;

public class TrapFloor extends Trap implements ICollide {

    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";


    public TrapFloor(Point position) {
        new PositionComponent(this, position);
        Point offset = new Point(0, 0);
        new HitboxComponent(
            this, offset, new Point(1, 1), this::onCollision, null);
    }

    @Override
    public void onInteraction(Entity entity) {
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

            if(hero.getTrapTimer() != null && !hero.getTrapTimer().isFinished()){
                TrapTimer t = hero.getTrapTimer();

                // wait random secounds
                System.out.println((time+t.getCurrentTimeInSec()) + " Sekunden verlangsamt");

                hero.startTrapTimer((time+t.getCurrentTimeInSec()) * 1000);
            }else{
                // wait random secounds
                System.out.println(time + " Sekunden verlangsamt");

                hero.startTrapTimer(time * 1000);
            }



            Game.setHero(hero);
            Game.systems.update();
        }
    }
}
