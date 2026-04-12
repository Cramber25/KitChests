package pl.cramber.kitchests;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class KitCommand implements CommandExecutor {
    private final KitChests plugin;
    private final NamespacedKey kitKey;
    private final NamespacedKey sizeKey;

    public KitCommand(KitChests plugin) {
        this.plugin = plugin;
        this.kitKey = new NamespacedKey(plugin, "kit_id");
        this.sizeKey = new NamespacedKey(plugin, "kit_size");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("kitchests.admin")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.no_permission", "<red>You do not have permission.</red>")));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.invalid_usage", "<red>Usage: /kitchest <create|remove></red>")));
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || !(targetBlock.getState() instanceof Container container)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.not_looking_at_chest", "<red>You must be looking at a chest.</red>")));
            return true;
        }

        String subCmd = args[0].toLowerCase();

        if (subCmd.equals("create")) {
            if (container.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.already_kitchest", "<red>This is already a KitChest.</red>")));
                return true;
            }

            String newId = UUID.randomUUID().toString();
            int inventorySize = container.getInventory().getSize();

            plugin.getDataManager().saveBaseKit(newId, container.getInventory().getContents());

            container.getPersistentDataContainer().set(kitKey, PersistentDataType.STRING, newId);
            container.getPersistentDataContainer().set(sizeKey, PersistentDataType.INTEGER, inventorySize);
            container.update();

            container.getInventory().clear();

            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.create_success", "<green>KitChest created successfully!</green>")));
            return true;
        }

        if (subCmd.equals("remove")) {
            if (!container.getPersistentDataContainer().has(kitKey, PersistentDataType.STRING)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.not_a_kitchest", "<red>This is not a KitChest.</red>")));
                return true;
            }

            String kitId = container.getPersistentDataContainer().get(kitKey, PersistentDataType.STRING);

            container.getPersistentDataContainer().remove(kitKey);
            container.getPersistentDataContainer().remove(sizeKey);
            container.update();

            if (kitId != null) {
                plugin.getDataManager().deleteKitData(kitId);
            }

            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.remove_success", "<green>KitChest removed.</green>")));
            return true;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("messages.invalid_usage", "<red>Usage: /kitchest <create|remove></red>")));
        return true;
    }
}