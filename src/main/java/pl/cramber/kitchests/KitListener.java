package pl.cramber.kitchests;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;

public class KitListener implements Listener {
    private final KitChests plugin;
    private final NamespacedKey kitKey;
    private final NamespacedKey sizeKey;

    public KitListener(KitChests plugin) {
        this.plugin = plugin;
        this.kitKey = new NamespacedKey(plugin, "kit_id");
        this.sizeKey = new NamespacedKey(plugin, "kit_size");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Container container) {
            if (container.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) {
                event.setCancelled(true);

                String kitId = container.getPersistentDataContainer().get(kitKey, PersistentDataType.STRING);
                if (kitId == null) return;

                int size = 27;
                if (container.getPersistentDataContainer().has(sizeKey, PersistentDataType.INTEGER)) {
                    Integer storedSize = container.getPersistentDataContainer().get(sizeKey, PersistentDataType.INTEGER);
                    if (storedSize != null) size = storedSize;
                }

                Player player = event.getPlayer();

                ItemStack[] items = plugin.getDataManager().getPlayerKitState(player.getUniqueId(), kitId, size);

                if (items == null) {
                    items = plugin.getDataManager().getBaseKit(kitId, size);
                }

                KitHolder holder = new KitHolder(kitId);
                Inventory inv = Bukkit.createInventory(holder, size, Component.text("Kit Chest"));
                holder.setInventory(inv);

                for (int i = 0; i < Math.min(items.length, size); i++) {
                    if (items[i] != null) {
                        inv.setItem(i, items[i].clone());
                    }
                }

                player.playSound(container.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
                player.openInventory(inv);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Container container) {
            if (container.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) {
                event.setCancelled(true);
                String msg = plugin.getConfig().getString("messages.cannot_break", "<red>You cannot break a KitChest! Use /kitchest remove first.</red>");
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(msg));
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getState() instanceof Container container) {
                if (container.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getState() instanceof Container container) {
                if (container.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof KitHolder) {
            Inventory topInv = event.getView().getTopInventory();

            if (event.getClickedInventory() != null && event.getClickedInventory().equals(topInv)) {
                if (event.getCursor() != null && !event.getCursor().getType().isAir()) {
                    event.setCancelled(true);
                }
            }

            if (event.isShiftClick() && event.getClickedInventory() != null && !event.getClickedInventory().equals(topInv)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof KitHolder) {
            for (int rawSlot : event.getRawSlots()) {
                if (rawSlot < event.getInventory().getSize()) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof KitHolder holder) {
            plugin.getDataManager().savePlayerKitState(event.getPlayer().getUniqueId(), holder.getKitId(), event.getInventory().getContents());
        }
    }
}