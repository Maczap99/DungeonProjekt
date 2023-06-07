package ecs.items;

import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

public class EpicPowerfulShield extends ItemData {
    public static final int HEALTH_UP = 20;

    public EpicPowerfulShield() {
        super(
            ItemType.Effect,
            new Animation(List.of("items/shield.png"), 1),
            new Animation(List.of("items/shield.png"), 1),
            "Epic Powerful Shield",
            "Ein episches, mächtiges Schild, das die maximale Gesundheit des Spielers erhöht.",
            new ShieldCollect(),
            null,
            null,
            new DamageModifier());
    }
}
