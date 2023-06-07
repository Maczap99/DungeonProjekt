package ecs.items;

import ecs.components.InventoryComponent;

public class ArrowQuiver extends ItemData {
    private InventoryComponent inventory;

    public ArrowQuiver() {
        //this.inventory = new InventoryComponent(this, 1);
    }

    public InventoryComponent getInventory() {
        return inventory;
    }
}
