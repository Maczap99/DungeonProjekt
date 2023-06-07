package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

/***
 * This ist the Class for the fireball settings
 */
public class FireballSkill extends DamageProjectileSkill {
    /**
     * This Method set the fireball settings
     * @param targetSelection
     */
    public FireballSkill(ITargetSelection targetSelection) {
        super(
            "fireball",
            "skills/fireball/fireBall_",
            0.5f,
            new Damage(8 + (Game.getLevelStage() / 5), DamageType.FIRE, null),
            new Point(3, 3),
            targetSelection,
            5f,
            10f);
    }
}
