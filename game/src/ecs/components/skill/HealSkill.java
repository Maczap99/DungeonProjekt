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

/***
 * This class is for healing skill
 * This class get the max health from the hero and set it on the current skill
 *
 * **/

public class HealSkill extends BuffSkill {
    private float manaCost = 100f;
    private transient Sound sound;


    public HealSkill() {
        super();
    }

    @Override
    public void execute(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();

        if (manaCost <= hero.getCurrentMana()) {

            // get the health component
            Optional<Component> he = entity.getComponent(HealthComponent.class);
            if (he.isPresent()) {
                HealthComponent hc = (HealthComponent) he.get();

                if (hc.getCurrentHealthpoints() < hc.getMaximalHealthpoints()) {

                    // set the curren health on maximal health
                    hc.setCurrentHealthpoints(hc.getMaximalHealthpoints());
                    System.out.println("Leben: " + hc.getCurrentHealthpoints());


                    // reduce mana
                    hero.setCurrentMana(hero.getCurrentMana() - manaCost);
                    System.out.println("Mana: " + (int) hero.getCurrentMana());

                    hero.setCurrentHealth(hero.getHealth());

                    try {
                        // start menu soundtrack
                        sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/heal1.mp3"));
                        sound.play(0.5f);

                    } catch (Exception e) {
                        System.out.println("Sounddatei 'heal1.mp3' konnte nicht gefunden werden");
                    }
                } else {
                    System.out.println("Leben sind voll!");
                    hero.setCurrentMana(100);
                }

            } else {
                throw new MissingComponentException("Player has no HealthComponent!");
            }
        }else{
            System.out.println("Nicht genug Mana!");
        }
    }
}
