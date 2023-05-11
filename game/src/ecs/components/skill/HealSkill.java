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
    private Sound sound;
    public HealSkill() {
        super();
    }

    @Override
    public void execute(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();

        // get the health component
        Optional<Component> he = entity.getComponent(HealthComponent.class);
        if (he.isPresent()) {
            HealthComponent hc = (HealthComponent) he.get();

            // set the curren health on maximal health
            hc.setCurrentHealthpoints(hc.getMaximalHealthpoints());
            System.out.println("Leben: " + hc.getCurrentHealthpoints());

            hero.setCurrentHealth(hero.getHealth());

            try{
                // start menu soundtrack
                sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/heal1.mp3"));
                sound.play(0.2f);

            }catch (Exception e){
                System.out.println("Sounddatei 'heal1.mp3' konnte nicht gefunden werden");
            }

        } else {
            throw new MissingComponentException("Player has no HealthComponent!");
        }
    }
}
