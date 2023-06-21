package ecs.items;

import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

/**
 * Represents the Eternal Arrows, an ammo item for archers in the dungeon.
 */
public class EternalArrows extends ItemData {
    private static final int MAX_AMOUNT = 20;

    private int amount = 0;

    /**
     * Constructs a new EternalArrows item.
     */
    public EternalArrows() {
        super(
            ItemType.Ammo,
            new Animation(List.of("items/arrow.png"), 1),
            new Animation(List.of("items/arrow.png"), 1),
            "Eternal Arrows",
            "Essential ammunition for archers in the dungeon.",
            new ArrowCollect(),
            null,
            null,
            new DamageModifier());
    }

    public EternalArrows(int amount) {
        super(
            ItemType.Ammo,
            new Animation(List.of("items/arrow.png"), 1),
            new Animation(List.of("items/arrow.png"), 1),
            "Eternal Arrows",
            "Essential ammunition for archers in the dungeon.",
            new ArrowCollect(),
            null,
            null,
            new DamageModifier());
        this.amount = amount;
    }

    /**
     * Increases the amount of Eternal Arrows by 1.
     *
     * @return true if the increase was successful, false if the maximum amount has been reached.
     */
    public boolean increaseAmount() {
        if (amount + 1 > MAX_AMOUNT) {
            return false;
        }
        amount++;
        return true;
    }

    /**
     * Decreases the amount of Eternal Arrows by 1.
     *
     * @return true if the decrease was successful, false if the amount is already at the minimum.
     */
    public boolean decreaseAmount() {
        if (amount - 1 < 0) {
            return false;
        }
        amount--;
        return true;
    }

    /**
     * Returns the current amount of Eternal Arrows.
     *
     * @return the current amount of Eternal Arrows.
     */
    public int getAmount() {
        return amount;
    }
}
