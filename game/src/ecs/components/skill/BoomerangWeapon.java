package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

/***
 * This ist the Class for the boomerang settings
 */
public class BoomerangWeapon extends DamageProjectileSkill {
    /**
     * This Method set the boomerang settings
     * @param targetSelection
     */
    public BoomerangWeapon(ITargetSelection targetSelection) {
        super(
            "boomerang",
            "skills/boomerang/",
            0.15f,
            new Damage(3 + (Game.getLevelStage() / 5), DamageType.PHYSICAL, null),
            new Point(1, 1),
            targetSelection,
            4f,
            0f);
    }
}
