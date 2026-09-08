package io.github.kirogamingkhdev.galaxypets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GalaxyPets extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("GalaxyPets has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GalaxyPets has been disabled!");
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!command.getName().equalsIgnoreCase("pets")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        player.sendMessage("§d§lGalaxyPets §7» §fPets menu coming soon!");
        return true;
    }
}
