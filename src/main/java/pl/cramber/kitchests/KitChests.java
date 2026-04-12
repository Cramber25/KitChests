package pl.cramber.kitchests;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitChests extends JavaPlugin {
    private DataManager dataManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        dataManager = new DataManager(this);

        getServer().getPluginManager().registerEvents(new KitListener(this), this);

        PluginCommand kitCommand = getCommand("kitchest");
        if (kitCommand != null) {
            kitCommand.setExecutor(new KitCommand(this));
        }
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}