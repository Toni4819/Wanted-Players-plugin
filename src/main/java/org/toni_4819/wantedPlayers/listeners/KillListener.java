/* Decompiler 27ms, total 165ms, lines 66 */
package org.toni_4819.wantedPlayers.listeners;

import java.util.Random;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.toni_4819.wantedPlayers.WantedPlayers;
import org.toni_4819.wantedPlayers.utils.WantedManager;

public class KillListener implements Listener {
    private final WantedPlayers plugin;
    private final Random random = new Random();

    public KillListener(WantedPlayers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player var3 = event.getEntity().getKiller();
        if (var3 instanceof Player) {
            Player killer = var3;
            String ksStr = PlaceholderAPI.setPlaceholders(var3, "%pvpstats_killstreak%");
            int killstreak = 0;

            try {
                killstreak = Integer.parseInt(ksStr);
            } catch (NumberFormatException var18) {
            }

            int wantedLevel = WantedManager.getWantedLevel(killstreak);
            if (wantedLevel > 0) {
                int min = this.plugin.getConfig().getInt("rewards.ranges." + wantedLevel + ".min");
                int max = this.plugin.getConfig().getInt("rewards.ranges." + wantedLevel + ".max");
                int baseReward = min + this.random.nextInt(max - min + 1);
                double wantedMult = this.plugin.getConfig().getDouble("rewards.multipliers." + wantedLevel, 1.0D);
                double groupMult = 1.0D;
                String groupName = "default";

                try {
                    LuckPerms lp = LuckPermsProvider.get();
                    User user = lp.getUserManager().getUser(killer.getUniqueId());
                    if (user != null && !user.getPrimaryGroup().isEmpty()) {
                        groupName = user.getPrimaryGroup();
                        groupMult = this.plugin.getConfig().getDouble("rewards.group-multipliers." + groupName, this.plugin.getConfig().getDouble("rewards.group-multipliers.default", 1.0D));
                    }
                } catch (Exception var17) {
                    this.plugin.getLogger().warning("Could not retrieve LuckPerms group for " + killer.getName());
                }

                int reward = (int)((double)baseReward * wantedMult * groupMult);
                WantedPlayers.getEconomy().depositPlayer(killer, (double)reward);
                String stars = WantedManager.getStars(wantedLevel);
                String msg = this.plugin.getConfig().getString("messages.kill-reward").replace("%amount%", String.valueOf(reward)).replace("%stars%", stars).replace("%group%", groupName);
                killer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }
}