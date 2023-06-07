package ecs.items;

import ecs.components.ItemComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

public class ArrowCollect implements IOnCollect {
    @Override
    public void onCollect(Entity worldItem, Entity whoCollected) {
        if (whoCollected.equals(Game.getHero().get())) {
            var hero = (Hero) whoCollected;
            var inventory = hero.getInventory();

            boolean arrowsArePresent = false;
            for (ItemData itemData : inventory.getItems()) {
                if (itemData instanceof EternalArrows) {
                    arrowsArePresent = true;
                    if (((EternalArrows) itemData).increaseAmount()) {
                        Game.removeEntity(worldItem);
                    }
                    break;
                }
            }

            if (!arrowsArePresent) {
                var itemComponent = (ItemComponent) worldItem.getComponent(ItemComponent.class).get();
                var eternalArrows = (EternalArrows) itemComponent.getItemData();

                if (eternalArrows.increaseAmount()) {
                    Game.removeEntity(worldItem);
                }

                inventory.addItem(eternalArrows);
            }

            hero.setAmmo(hero.getAmmo() + 1);
        }
    }
}
