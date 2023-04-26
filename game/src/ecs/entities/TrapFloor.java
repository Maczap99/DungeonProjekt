package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.HitboxComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.collision.ICollide;
import graphic.Animation;
import level.elements.tile.Tile;
import starter.Game;
import tools.Point;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class TrapFloor extends Trap implements ICollide {

    private final String pathToRunLeft = "knight/runLeft";
    private final String pathToRunRight = "knight/runRight";


    public TrapFloor(Point position) {
        new PositionComponent(this, position);
        Point offset = new Point(0, 0);
        new HitboxComponent(
            this, offset, new Point(1, 1), this::onCollision, null);
    }

    public TrapFloor() {

    }

    @Override
    public void onInteraction(Entity entity) {
    }

    @Override
    public void onCollision(Entity a, Entity b, Tile.Direction from) {

        if (b.getClass().getName().equals("ecs.entities.Hero")) {
            System.out.println("ja");

            Hero hero = (Hero) Game.getHero().get();
            hero.setySpeed(0.1f);
            hero.setxSpeed(0.1f);

            hero.setupVelocityComponent();

            // wait
            Random rand = new Random();
            int time = rand.nextInt(10) + 6;

            System.out.println(time + " Sekunden verlangsamt");
            hero.startTrapTimer(time * 1000);

            Game.setHero(hero);
            Game.systems.update();
        }
    }
}
