package io.github.kirogamingkhdev.galaxypets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GalaxyPets extends JavaPlugin implements Listener {

    private final Map<UUID, LivingEntity> activePets = new HashMap<>();

    private final String GUI_TITLE =
            ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "GalaxyPets";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("pets").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            openPetsMenu(player);
            return true;
        });

        startPetFollowTask();

        getLogger().info("GalaxyPets has been enabled!");
    }

    @Override
    public void onDisable() {
        for (LivingEntity pet : activePets.values()) {
            if (pet != null && pet.isValid()) {
                pet.remove();
            }
        }

        activePets.clear();
    }

    private void openPetsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                GUI_TITLE
        );

        inventory.setItem(11, createItem(
                Material.WOLF_SPAWN_EGG,
                ChatColor.GREEN + "Wolf",
                ChatColor.GRAY + "Click to summon your Wolf!"
        ));

        inventory.setItem(13, createItem(
                Material.CAT_SPAWN_EGG,
                ChatColor.YELLOW + "Cat",
                ChatColor.GRAY + "Click to summon your Cat!"
        ));

        inventory.setItem(15, createItem(
                Material.RABBIT_SPAWN_EGG,
                ChatColor.GOLD + "Rabbit",
                ChatColor.GRAY + "Click to summon your Rabbit!"
        ));

        inventory.setItem(22, createItem(
                Material.BARRIER,
                ChatColor.RED + "Remove Pet",
                ChatColor.GRAY + "Click to remove your pet."
        ));

        player.openInventory(inventory);
    }

    private ItemStack createItem(
            Material material,
            String name,
            String lore
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(java.util.List.of(lore));
            item.setItemMeta(meta);
        }

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        switch (item.getType()) {
            case WOLF_SPAWN_EGG -> {
                player.closeInventory();
                summonWolf(player);
            }

            case CAT_SPAWN_EGG -> {
                player.closeInventory();
                summonCat(player);
            }

            case RABBIT_SPAWN_EGG -> {
                player.closeInventory();
                summonRabbit(player);
            }

            case BARRIER -> {
                player.closeInventory();
                removePet(player);
            }

            default -> {
            }
        }
    }

    private void summonWolf(Player player) {
        removePet(player);

        Location location = player.getLocation().add(1, 0, 1);

        Wolf wolf = (Wolf) player.getWorld().spawnEntity(
                location,
                org.bukkit.entity.EntityType.WOLF
        );

        wolf.setOwner(player);
        wolf.setTamed(true);
        wolf.setAdult();
        wolf.setCustomName(
                ChatColor.GREEN + player.getName() + "'s Wolf"
        );
        wolf.setCustomNameVisible(true);

        activePets.put(player.getUniqueId(), wolf);

        player.sendMessage(
                ChatColor.LIGHT_PURPLE + "GalaxyPets "
                        + ChatColor.GRAY + "» "
                        + ChatColor.GREEN + "Wolf summoned!"
        );
    }

    private void summonCat(Player player) {
        removePet(player);

        Location location = player.getLocation().add(1, 0, 1);

        Cat cat = (Cat) player.getWorld().spawnEntity(
                location,
                org.bukkit.entity.EntityType.CAT
        );

        cat.setOwner(player);
        cat.setTamed(true);
        cat.setAdult();
        cat.setCustomName(
                ChatColor.YELLOW + player.getName() + "'s Cat"
        );
        cat.setCustomNameVisible(true);

        activePets.put(player.getUniqueId(), cat);

        player.sendMessage(
                ChatColor.LIGHT_PURPLE + "GalaxyPets "
                        + ChatColor.GRAY + "» "
                        + ChatColor.YELLOW + "Cat summoned!"
        );
    }

    private void summonRabbit(Player player) {
        removePet(player);

        Location location = player.getLocation().add(1, 0, 1);

        Rabbit rabbit = (Rabbit) player.getWorld().spawnEntity(
                location,
                org.bukkit.entity.EntityType.RABBIT
        );

        rabbit.setAdult();
        rabbit.setCustomName(
                ChatColor.GOLD + player.getName() + "'s Rabbit"
        );
        rabbit.setCustomNameVisible(true);

        activePets.put(player.getUniqueId(), rabbit);

        player.sendMessage(
                ChatColor.LIGHT_PURPLE + "GalaxyPets "
                        + ChatColor.GRAY + "» "
                        + ChatColor.GOLD + "Rabbit summoned!"
        );
    }

    private void removePet(Player player) {
        LivingEntity pet = activePets.remove(player.getUniqueId());

        if (pet != null && pet.isValid()) {
            pet.remove();

            player.sendMessage(
                    ChatColor.LIGHT_PURPLE + "GalaxyPets "
                            + ChatColor.GRAY + "» "
                            + ChatColor.RED + "Pet removed!"
            );
        }
    }

    private void startPetFollowTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, LivingEntity> entry : activePets.entrySet()) {

                    Player player = Bukkit.getPlayer(entry.getKey());
                    LivingEntity pet = entry.getValue();

                    if (player == null || !player.isOnline()) {
                        continue;
                    }

                    if (pet == null || !pet.isValid()) {
                        continue;
                    }

                    if (!pet.getWorld().equals(player.getWorld())) {
                        pet.teleport(player.getLocation());
                        continue;
                    }

                    if (pet.getLocation().distance(player.getLocation()) > 12) {
                        Location target = player.getLocation()
                                .clone()
                                .add(-1, 0, -1);

                        pet.teleport(target);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePet(event.getPlayer());
    }
            }
