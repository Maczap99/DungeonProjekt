package ecs.items;

import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

/**
 * Represents the Magic Speed Boost Boots, a unique item that increases the hero's running speed by 30%.
 */
public class MagicSpeedBoostBoots extends ItemData {
    public static final float SPEED_BOOST = .3f;

    /**
     * Constructs a new MagicSpeedBoostBoots item.
     */
    public MagicSpeedBoostBoots() {
        super(
            ItemType.Effect,
            new Animation(List.of("items/boots.png"), 1),
            new Animation(List.of("items/boots.png"), 1),
            "Magic Speed Boost Boots",
            "Unique boots that increase the hero's running speed by 30%.",
            new BootsCollect(),
            null,
            null,
            new DamageModifier());
    }
}
