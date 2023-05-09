package ecs.components.skill;

import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;


/**
 * This class is for the cure skill
 * if you use this skill, it set the timer of a slow trap on zero
 */
public class CureSkill extends BuffSkill{
    public CureSkill(){
        super();
    }

    @Override
    public void execute(Entity entity) {
        // set the trap timer on 0
        Hero hero = (Hero) Game.getHero().get();
        System.out.println("Held wurde gesund");
        hero.startTrapTimer(0);
    }
}
