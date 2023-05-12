package ecs.components.skill;

import ecs.components.Component;
import ecs.components.xp.XPComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;
import tools.Constants;

import java.io.Serializable;
import java.util.Optional;

public class Skill implements Serializable {

    private ISkillFunction skillFunction;
    private int coolDownInFrames;
    private int currentCoolDownInFrames;
    private int levelNeed;

    /**
     * @param skillFunction Function of this skill
     */
    public Skill(ISkillFunction skillFunction, float coolDownInSeconds, int levelNeed) {
        this.skillFunction = skillFunction;
        this.coolDownInFrames = (int) (coolDownInSeconds * Constants.FRAME_RATE);
        this.currentCoolDownInFrames = 0;
        this.levelNeed = levelNeed;
    }

    /**
     * Execute the method of this skill
     *
     * @param entity entity which uses the skill
     */
    public void execute(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();
        Optional<Component> xp = hero.getComponent(XPComponent.class);

        if (xp.isPresent()) {
            XPComponent x = (XPComponent) xp.get();

            // check for enough mana and if the skill is unlocked
            if (!isOnCoolDown() && x.getCurrentLevel() >= levelNeed) {
                skillFunction.execute(entity);
                activateCoolDown();
            }
        }
    }

    /**
     * @return true if cool down is not 0, else false
     */
    public boolean isOnCoolDown() {
        return currentCoolDownInFrames > 0;
    }

    /**
     * activate cool down
     */
    public void activateCoolDown() {
        currentCoolDownInFrames = coolDownInFrames;
    }

    /**
     * reduces the current cool down by frame
     */
    public void reduceCoolDown() {
        currentCoolDownInFrames = Math.max(0, --currentCoolDownInFrames);
    }
}
