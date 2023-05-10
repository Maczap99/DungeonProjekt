package ecs.components.skill;

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

        } else {
            throw new MissingComponentException("Player has no HealthComponent!");
        }
    }
}
