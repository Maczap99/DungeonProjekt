package ecs.items;

import ecs.components.ItemComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import starter.Game;

public class ShieldCollect implements IOnCollect {
    @Override
    public void onCollect(Entity worldItem, Entity whoCollected) {
        if (whoCollected.equals(Game.getHero().get())) {
            var hero = (Hero) whoCollected;
            var inventory = hero.getInventory();

            boolean bootsArePresent = false;
            for (ItemData itemData : inventory.getItems()) {
                if (itemData instanceof EpicPowerfulShield) {
                    return;
                }
            }

            if (!bootsArePresent) {
                var itemComponent = (ItemComponent) worldItem.getComponent(ItemComponent.class).get();
                var epicPowerfulShield = (EpicPowerfulShield) itemComponent.getItemData();

                inventory.addItem(epicPowerfulShield);

                hero.setHealth(epicPowerfulShield.HEALTH_UP);

                Game.removeEntity(worldItem);
            }
        }
    }
}
