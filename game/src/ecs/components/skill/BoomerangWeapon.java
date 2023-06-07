package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

public class BoomerangWeapon extends DamageProjectileSkill {
    public BoomerangWeapon(ITargetSelection targetSelection) {
        super(
            "boomerang",
            "skills/boomerang/",
            0.15f,
            new Damage(1 + (Game.getLevelStage() / 5), DamageType.PHYSICAL, null),
            new Point(1, 1),
            targetSelection,
            4f,
            0f);
    }
}
