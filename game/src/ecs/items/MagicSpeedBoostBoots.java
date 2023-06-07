package ecs.items;

import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

public class MagicSpeedBoostBoots extends ItemData {
    public static final float SPEED_BOOST = .3f;

    public MagicSpeedBoostBoots() {
        super(
            ItemType.Effect,
            new Animation(List.of("items/boots.png"), 1),
            new Animation(List.of("items/boots.png"), 1),
            "Magic Speed Boost Boots",
            "Einzigartige Stiefel, die die Laufgeschwindigkeit des Helden um 30% erhöhen.",
            new BootsCollect(),
            null,
            null,
            new DamageModifier());
    }
}
