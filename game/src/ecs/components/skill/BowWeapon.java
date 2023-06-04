package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

public class BowWeapon extends DamageProjectileSkill {
    public BowWeapon(ITargetSelection targetSelection) {
        super(
            "bow",
            "skills/arrow/",
            0.5f,
            new Damage(1 + (Game.getLevelStage() / 5), DamageType.PHYSICAL, null),
            new Point(10, 10),
            targetSelection,
            10f,
            0f);
    }
}
