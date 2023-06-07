package ecs.items;

import ecs.components.InventoryComponent;
import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

public class ArrowQuiver extends ItemData {
    /**
     * Constructs an ArrowQuiver object.
     */
    public ArrowQuiver() {
        super(
            ItemType.Effect,
            new Animation(List.of("items/quiver.png"), 1),
            new Animation(List.of("items/quiver.png"), 1),
            "Arrow Quiver",
            "A quiver that allows the player to carry arrows as ammunition for the bow.",
            new QuiverCollect(),
            null,
            null,
            new DamageModifier());
    }
}
