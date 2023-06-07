package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

public class SwordWeapon extends DamageProjectileSkill {
    public SwordWeapon() {
        super(
            "sword",
            "skills/sword/sword_",
            0.6f,
            new Damage(3 + (Game.getLevelStage() / 5), DamageType.PHYSICAL, null),
            new Point(1, 1),
            null,
            1f,
            0f);
    }
}
