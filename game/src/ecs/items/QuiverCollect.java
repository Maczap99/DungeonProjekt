package ecs.items;

import ecs.components.InventoryComponent;
import ecs.components.ItemComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

public class QuiverCollect implements IOnCollect {
    /**
     * Called when a quiver item is collected by a player.
     *
     * @param worldItem     The entity representing the quiver item in the game world.
     * @param whoCollected  The entity representing the player who collected the quiver item.
     */
    @Override
    public void onCollect(Entity worldItem, Entity whoCollected) {
        if (whoCollected.equals(Game.getHero().get())) {
            var hero = (Hero) whoCollected;
            var inventory = hero.getInventory();

            boolean quiverIsPresent = false;
            for (ItemData itemData : inventory.getItems()) {
                if (itemData instanceof ArrowQuiver) {
                    return;
                }
            }

            if (!quiverIsPresent) {
                var itemComponent = (ItemComponent) worldItem.getComponent(ItemComponent.class).get();
                var arrowQuiver = (ArrowQuiver) itemComponent.getItemData();

                hero.setQuiver(new InventoryComponent(hero, 1));

                inventory.addItem(arrowQuiver);

                Game.removeEntity(worldItem);
            }
        }
    }
}
