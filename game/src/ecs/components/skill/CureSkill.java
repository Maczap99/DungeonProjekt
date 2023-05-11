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
    private Sound sound;

    public CureSkill(){
        super();
    }

    @Override
    public void execute(Entity entity) {
        // set the trap timer on 0
        Hero hero = (Hero) Game.getHero().get();
        System.out.println("Held wurde gesund");
        hero.startTrapTimer(0);

        try{
            // start menu soundtrack
            sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/cure1.mp3"));
            sound.play(0.3f);

        }catch (Exception e){
            System.out.println("Sounddatei 'cure1.mp3' konnte nicht gefunden werden");
        }

    }
}
