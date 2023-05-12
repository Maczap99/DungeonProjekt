package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

import java.io.Serializable;


/**
 * This class is for the cure skill
 * if you use this skill, it set the timer of a slow trap on zero
 */
public class CureSkill extends BuffSkill implements Serializable {
    private float manaCost = 10f;

    public CureSkill() {
        super();
    }

    @Override
    public void execute(Entity entity) {
        // set the trap timer on 0
        Hero hero = (Hero) Game.getHero().get();
        if (hero.getTrapTimer() != null && !hero.getTrapTimer().isFinished()) {
            if (manaCost <= hero.getCurrentMana()) {

                System.out.println("Held wurde gesund");
                hero.startTrapTimer(0);

                // reduce mana
                hero.setCurrentMana(hero.getCurrentMana() - manaCost);
                System.out.println("Mana: " + (int) hero.getCurrentMana());
                
            } else {
                System.out.println("Nicht genug Mana!");
            }
        }else{
            System.out.println("Kein Effekt vorhanden!");
        }
    }
}
