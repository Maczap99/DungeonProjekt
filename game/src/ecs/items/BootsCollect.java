package ecs.items;

import ecs.components.ItemComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

public class BootsCollect implements IOnCollect {
    /**
     * Handles the collection of a boots item by the player.
     *
     * @param worldItem     The entity representing the boots item in the game world.
     * @param whoCollected  The entity that collected the boots item.
     */
    @Override
    public void onCollect(Entity worldItem, Entity whoCollected) {
        if (whoCollected.equals(Game.getHero().get())) {
            var hero = (Hero) whoCollected;
            var inventory = hero.getInventory();

            boolean bootsArePresent = false;
            for (ItemData itemData : inventory.getItems()) {
                if (itemData instanceof MagicSpeedBoostBoots) {
                    return;
                }
            }

            if (!bootsArePresent) {
                var itemComponent = (ItemComponent) worldItem.getComponent(ItemComponent.class).get();
                var magicSpeedBoostBoots = (MagicSpeedBoostBoots) itemComponent.getItemData();

                inventory.addItem(magicSpeedBoostBoots);

                hero.setxSpeed(hero.getxSpeed() + (hero.getxSpeed() * magicSpeedBoostBoots.SPEED_BOOST));
                hero.setySpeed(hero.getySpeed() + (hero.getySpeed() * magicSpeedBoostBoots.SPEED_BOOST));

                Game.removeEntity(worldItem);
            }
        }
    }
}
