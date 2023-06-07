package ecs.items;

import ecs.components.stats.DamageModifier;
import graphic.Animation;

import java.util.List;

public class EternalArrows extends ItemData {
    private static final int MAX_AMOUNT = 20;

    private int amount = 0;

    public EternalArrows() {
        super(
            ItemType.Ammo,
            new Animation(List.of("items/arrow.png"), 1),
            new Animation(List.of("items/arrow.png"), 1),
            "Eternal Arrows",
            "Unverzichtbare Munition für Bogenschützen im Dungeon.",
            new ArrowCollect(),
            null,
            null,
            new DamageModifier());
    }

    public boolean increaseAmount() {
        if (amount + 1 > MAX_AMOUNT) {
            return false;
        }
        amount++;
        return true;
    }

    public boolean decreaseAmount() {
        if (amount - 1 < 0) {
            return false;
        }
        amount--;
        return true;
    }

    public int getAmount() {
        return amount;
    }
}
