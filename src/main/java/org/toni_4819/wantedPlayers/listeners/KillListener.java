package org.toni_4819.wantedPlayers.listeners;

import java.util.Random;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.toni_4819.wantedPlayers.WantedPlayers;
import org.toni_4819.wantedPlayers.utils.WantedManager;

// MythicMobs (API moderne 5.x+)
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;

public class KillListener implements Listener {
    private final WantedPlayers plugin;
    private final Random random = new Random();

    public KillListener(WantedPlayers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        // Get killstreak with PlaceholderAPI
        String ksStr = PlaceholderAPI.setPlaceholders(killer,
                plugin.getConfig().getString("killstreak-placeholder", "%pvpstats_killstreak%"));
        int killstreak = 0;
        try {
            killstreak = Integer.parseInt(ksStr);
        } catch (NumberFormatException ignored) {}

        int wantedLevel = WantedManager.getWantedLevel(killstreak);
        if (wantedLevel <= 0) return;

        // Base reward
        int min = plugin.getConfig().getInt("rewards.ranges." + wantedLevel + ".min");
        int max = plugin.getConfig().getInt("rewards.ranges." + wantedLevel + ".max");
        int baseReward = min + random.nextInt(max - min + 1);

        // Multipliers
        double wantedMult = plugin.getConfig().getDouble("rewards.level-multipliers." + wantedLevel, 1.0D);
        double groupMult = 1.0D;
        String groupName = "default";

        try {
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(killer.getUniqueId());
            if (user != null && !user.getPrimaryGroup().isEmpty()) {
                groupName = user.getPrimaryGroup();
                groupMult = plugin.getConfig().getDouble("rewards.group-multipliers." + groupName,
                        plugin.getConfig().getDouble("rewards.group-multipliers.default", 1.0D));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not retrieve LuckPerms group for " + killer.getName());
        }

        // Mob multiplier: MythicMobs or vanilla
        String mobKey = event.getEntityType().name().toLowerCase();

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            try {
                ActiveMob mythicMob = MythicBukkit.inst()
                        .getMobManager()
                        .getActiveMob(event.getEntity().getUniqueId())
                        .orElse(null);
                if (mythicMob != null) {
                    mobKey = mythicMob.getType().getInternalName().toLowerCase();
                }
            } catch (Throwable ignored) {
                // Si MythicMobs n'est pas présent ou API incompatible, on ignore
            }
        }

        // Only reward if mob is listed
        if (!plugin.getConfig().getConfigurationSection("rewards.reward-sources").getKeys(false).contains(mobKey)) {
            return; // mob not listed → no reward
        }
        double sourceMult = plugin.getConfig().getDouble("rewards.reward-sources." + mobKey);

        // Final reward
        int reward = (int) (baseReward * wantedMult * groupMult * sourceMult);

        // Execute reward commands
        List<String> commands = plugin.getConfig().getStringList("rewards.reward-command");
        for (String cmd : commands) {
            String parsed = cmd
                    .replace("%player%", killer.getName())
                    .replace("%amount%", String.valueOf(reward))
                    .replace("%stars%", WantedManager.getStars(wantedLevel))
                    .replace("%group%", groupName);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }

        // Message
        String stars = WantedManager.getStars(wantedLevel);
        String msg = plugin.getConfig().getString("messages.kill-reward")
                .replace("%amount%", String.valueOf(reward))
                .replace("%stars%", stars)
                .replace("%group%", groupName);
        killer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
