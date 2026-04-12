package pl.cramber.kitchests;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {
    private final KitChests plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(KitChests plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create data.yml", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveBaseKit(String kitId, ItemStack[] items) {
        String path = "kits." + kitId;
        dataConfig.set(path, null);

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && !items[i].getType().isAir()) {
                dataConfig.set(path + "." + i, items[i]);
            }
        }
        save();
    }

    public ItemStack[] getBaseKit(String kitId, int size) {
        ItemStack[] items = new ItemStack[size];
        String path = "kits." + kitId;

        if (dataConfig.getConfigurationSection(path) != null) {
            for (String key : dataConfig.getConfigurationSection(path).getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    if (slot < size) {
                        items[slot] = dataConfig.getItemStack(path + "." + key);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return items;
    }

    public void savePlayerKitState(UUID playerUuid, String kitId, ItemStack[] items) {
        String path = "players." + playerUuid.toString() + "." + kitId;
        dataConfig.set(path, null);

        dataConfig.set(path + ".opened", true);

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && !items[i].getType().isAir()) {
                dataConfig.set(path + ".slots." + i, items[i].getAmount());
            }
        }

        save();
    }

    public ItemStack[] getPlayerKitState(UUID playerUuid, String kitId, int size) {
        String path = "players." + playerUuid.toString() + "." + kitId;

        if (!dataConfig.contains(path)) {
            return null;
        }

        ItemStack[] items = new ItemStack[size];
        ItemStack[] baseKit = getBaseKit(kitId, size);

        if (dataConfig.getConfigurationSection(path + ".slots") != null) {
            for (String key : dataConfig.getConfigurationSection(path + ".slots").getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    if (slot < size && baseKit[slot] != null) {
                        int amount = dataConfig.getInt(path + ".slots." + key);
                        ItemStack item = baseKit[slot].clone();
                        item.setAmount(amount);
                        items[slot] = item;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return items;
    }

    public void deleteKitData(String kitId) {
        dataConfig.set("kits." + kitId, null);

        if (dataConfig.getConfigurationSection("players") != null) {
            for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
                dataConfig.set("players." + uuidStr + "." + kitId, null);

                if (dataConfig.getConfigurationSection("players." + uuidStr) != null &&
                        dataConfig.getConfigurationSection("players." + uuidStr).getKeys(false).isEmpty()) {
                    dataConfig.set("players." + uuidStr, null);
                }
            }
        }

        save();
    }

    public void save() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save data.yml", e);
        }
    }
}