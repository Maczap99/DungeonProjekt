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
            2f,
            new Damage(1 + (Game.getLevelStage() / 5), DamageType.PHYSICAL, null),
            new Point(10, 10),
            targetSelection,
            3f,
            0f);
    }
}
