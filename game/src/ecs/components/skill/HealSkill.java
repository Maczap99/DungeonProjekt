package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import ecs.components.Component;
import ecs.components.HealthComponent;
import ecs.components.MissingComponentException;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

import java.util.Optional;
import java.util.logging.Logger;

/***
 * This class is for healing skill
 * This class get the max health from the hero and set it on the current skill
 *
 * **/

public class HealSkill extends BuffSkill {
    private float manaCost = 100f;
    private transient Sound sound;
    private transient Logger healSkillLogger;
    private transient Logger soundLogger;

    /**
     * costructor
     */
    public HealSkill() {
        super();
    }

    /**
     * This is the execute Method for the heal Skill
     * @param entity which uses the skill
     */
    @Override
    public void execute(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();
        healSkillLogger = Logger.getLogger(this.getClass().getName());

        if (manaCost <= hero.getCurrentMana()) {

            // get the health component
            Optional<Component> he = entity.getComponent(HealthComponent.class);
            if (he.isPresent()) {
                HealthComponent hc = (HealthComponent) he.get();

                if (hc.getCurrentHealthpoints() < hc.getMaximalHealthpoints()) {

                    // set the curren health on maximal health
                    hc.setCurrentHealthpoints(hc.getMaximalHealthpoints());
                    healSkillLogger.info("Leben: " + hc.getCurrentHealthpoints());


                    // reduce mana
                    hero.setCurrentMana(hero.getCurrentMana() - manaCost);
                    healSkillLogger.info("Mana: "+(int) hero.getCurrentMana() + " / " + (int) hero.getMana());

                    hero.setCurrentHealth(hero.getHealth());

                    try {
                        // start menu soundtrack
                        sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/heal1.mp3"));
                        sound.play(0.5f);

                    } catch (Exception e) {
                        soundLogger = Logger.getLogger(this.getClass().getName());
                        soundLogger.info("Sounddatei 'heal1.mp3' konnte nicht gefunden werden");
                    }
                } else {
                    healSkillLogger.info("Leben sind voll!");
                    hero.setCurrentMana(100);
                }

            } else {
                throw new MissingComponentException("Player has no HealthComponent!");
            }
        }else{
            healSkillLogger = Logger.getLogger("Nicht genug Mana!");
            healSkillLogger = Logger.getLogger("Mana: "+(int) hero.getCurrentMana() + " / " + (int) hero.getMana());
        }
    }
}
