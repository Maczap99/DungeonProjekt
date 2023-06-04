package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import starter.Game;
import tools.Point;

public class FireballSkill extends DamageProjectileSkill {
    public FireballSkill(ITargetSelection targetSelection) {
        super(
            "fireball",
            "skills/fireball/fireBall_Down/",
            0.5f,
            new Damage(1 + (Game.getLevelStage() / 5), DamageType.FIRE, null),
            new Point(10, 10),
            targetSelection,
            5f,
            10f);
    }
}
