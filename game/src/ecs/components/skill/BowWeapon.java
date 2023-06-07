package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

/***
 * This ist the Class for the bow settings
 */
public class BowWeapon extends DamageProjectileSkill {
    /**
     * This Method set the bow settings
     * @param targetSelection
     */
    public BowWeapon(ITargetSelection targetSelection) {
        super(
            "bow",
            "skills/bow/bow_",
            0.4f,
            new Damage(10 + (Game.getLevelStage() / 5), DamageType.PHYSICAL, null),
            new Point(1, 1),
            targetSelection,
            10f,
            0f);
    }
}
