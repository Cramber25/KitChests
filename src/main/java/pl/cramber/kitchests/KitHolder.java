package pl.cramber.kitchests;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class KitHolder implements InventoryHolder {
    private final String kitId;
    private Inventory inventory;

    public KitHolder(String kitId) {
        this.kitId = kitId;
    }

    public String getKitId() {
        return kitId;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}