package ecs.items;

import ecs.components.ItemComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

public class ArrowCollect implements IOnCollect {
    /**
     * Handles the collection of an arrow item by the player.
     *
     * @param worldItem     The entity representing the arrow item in the game world.
     * @param whoCollected  The entity that collected the arrow item.
     */
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

            hero.setCurrentAmmo(hero.getCurrentAmmo() + 1);
        }
    }
}
