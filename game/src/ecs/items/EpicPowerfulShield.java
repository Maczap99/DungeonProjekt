package ecs.items;

import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

public class EpicPowerfulShield extends ItemData {
    public static final int HEALTH_UP = 120;

    /**
     * Constructs a new EpicPowerfulShield item.
     */
    public EpicPowerfulShield() {
        super(
            ItemType.Effect,
            new Animation(List.of("items/shield.png"), 1),
            new Animation(List.of("items/shield.png"), 1),
            "Epic Powerful Shield",
            "An epic powerful shield that increases the player's maximum health.",
            new ShieldCollect(),
            null,
            null,
            new DamageModifier());
    }
}
