package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

import java.io.Serializable;
import java.util.logging.Logger;


/**
 * This class is for the cure skill
 * if you use this skill, it set the timer of a slow trap on zero
 */
public class CureSkill extends BuffSkill implements Serializable {
    private float manaCost = 10f;

    private transient Sound sound;
    private transient Logger cureSkillLogger;
    private transient Logger soundLogger;


    public CureSkill() {
        super();
    }

    @Override
    public void execute(Entity entity) {
        cureSkillLogger = Logger.getLogger(this.getClass().getName());
        // set the trap timer on 0
        Hero hero = (Hero) Game.getHero().get();

        if (hero.getTrapTimer() != null && !hero.getTrapTimer().isFinished()) {
            if (manaCost <= hero.getCurrentMana()) {

                cureSkillLogger.info("Held wurde gesund");
                hero.startTrapTimer(0);

                // reduce mana
                hero.setCurrentMana(hero.getCurrentMana() - manaCost);
                cureSkillLogger.info("Mana: " + (int) hero.getCurrentMana() + " / " + (int) hero.getMana());

                try {
                    // start menu soundtrack
                    sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/cure1.mp3"));
                    sound.play(0.7f);

                } catch (Exception e) {
                    soundLogger = Logger.getLogger("Sounddatei 'cure1.mp3' konnte nicht gefunden werden");
                }


            } else {
                cureSkillLogger.info("Nicht genug Mana!");
                cureSkillLogger.info("Mana: " + (int) hero.getCurrentMana() + " / " + (int) hero.getMana());
            }
        } else {
            cureSkillLogger.info("Kein Effekt vorhanden!");
        }
    }
}
