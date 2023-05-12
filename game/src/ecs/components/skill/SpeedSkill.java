package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import ecs.components.Component;
import ecs.components.MissingComponentException;
import ecs.components.VelocityComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

import java.util.Optional;

/**
 * This class is for the speedUp skill
 * This class get the velocity from the gero and set it higher for some seconds and set a thread timer
 * the skill cant use if you slow by a trap
 *
 * **/

public class SpeedSkill extends BuffSkill {
    private float manaCost = 10f;

    private transient Sound sound;

    public SpeedSkill() {
        super();
    }

    @Override
    public void execute(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();


        if (manaCost <= hero.getCurrentMana()) {

            // get the velocity component
            Optional<Component> ve = entity.getComponent(VelocityComponent.class);
            if (ve.isPresent()) {
                // if the timer is null or is finished
                if (hero.getTrapTimer() == null || hero.getTrapTimer().isFinished()) {

                    // set new velocity
                    VelocityComponent v = (VelocityComponent) ve.get();
                    v.setYVelocity(0.4f);
                    v.setXVelocity(0.4f);

                    // set seconds for timer
                    int startTime = 3;
                    int ebene = Game.getLevelStage();
                    int time = startTime + (ebene / 2);


                    if (time > 15) {
                        time = 15;
                    }

                    System.out.println(time + " Sekunden SpeedUP");
                    hero.startTrapTimer(time * 1000);

                    try {
                        // start menu soundtrack
                        sound = Gdx.audio.newSound(Gdx.files.internal("game/sounds/skill/speedUp.mp3"));
                        sound.play(0.3f);

                    } catch (Exception e) {
                        System.out.println("Sounddatei 'speedUp.mp3' konnte nicht gefunden werden");
                    }


                    if (time > 15) {
                        time = 15;
                    }

                    System.out.println(time + " Sekunden SpeedUP");
                    hero.startTrapTimer(time * 1000);

                    // reduce mana
                    hero.setCurrentMana(hero.getCurrentMana() - manaCost);
                    System.out.println("Mana: "+(int) hero.getCurrentMana() + " / " + (int) hero.getMana());


                } else {
                    System.out.println("SpeedUp nicht nutzbar, da eine Falle aktiviert ist");
                    hero.setCurrentMana(hero.getCurrentMana() + 10);
                }
            } else {
                throw new MissingComponentException("Player has no HealthComponent!");
            }
        }else{
            System.out.println("Nicht genug Mana!");
            System.out.println("Mana: "+(int) hero.getCurrentMana() + " / " + (int) hero.getMana());
        }
    }
}
