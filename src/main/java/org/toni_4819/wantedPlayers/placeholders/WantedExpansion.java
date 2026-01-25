/* Decompiler 42ms, total 203ms, lines 90 */
package org.toni_4819.wantedPlayers.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.toni_4819.wantedPlayers.WantedPlayers;
import org.toni_4819.wantedPlayers.utils.WantedManager;

public class WantedExpansion extends PlaceholderExpansion {
    private final WantedPlayers plugin;

    public WantedExpansion(WantedPlayers plugin) {
        this.plugin = plugin;
    }

    public String getIdentifier() {
        return "wanted";
    }

    public String getAuthor() {
        return "Toni_4819";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        } else {
            int killstreak = 0;

            try {
                String ksStr = PlaceholderAPI.setPlaceholders(player, "%pvpstats_killstreak%");
                killstreak = Integer.parseInt(ksStr);
            } catch (Exception var15) {
            }

            int wantedLevel = WantedManager.getWantedLevel(killstreak);
            String var5 = params.toLowerCase();
            byte var6 = -1;
            switch(var5.hashCode()) {
                case 102865796:
                    if (var5.equals("level")) {
                        var6 = 1;
                    }
                    break;
                case 109757537:
                    if (var5.equals("stars")) {
                        var6 = 0;
                    }
                    break;
                case 1265073601:
                    if (var5.equals("multiplier")) {
                        var6 = 2;
                    }
            }

            switch(var6) {
                case 0:
                    return WantedManager.getStars(wantedLevel);
                case 1:
                    return String.valueOf(wantedLevel);
                case 2:
                    double wantedMult = this.plugin.getConfig().getDouble("rewards.multipliers." + wantedLevel, 1.0D);
                    double groupMult = 1.0D;

                    try {
                        LuckPerms lp = LuckPermsProvider.get();
                        User user = lp.getUserManager().getUser(player.getUniqueId());
                        if (user != null && !user.getPrimaryGroup().isEmpty()) {
                            String groupName = user.getPrimaryGroup();
                            groupMult = this.plugin.getConfig().getDouble("rewards.group-multipliers." + groupName, this.plugin.getConfig().getDouble("rewards.group-multipliers.default", 1.0D));
                        }
                    } catch (Exception var14) {
                        this.plugin.getLogger().warning("Could not retrieve LuckPerms group for " + player.getName());
                    }

                    return String.valueOf(wantedMult * groupMult);
                default:
                    return "";
            }
        }
    }
}