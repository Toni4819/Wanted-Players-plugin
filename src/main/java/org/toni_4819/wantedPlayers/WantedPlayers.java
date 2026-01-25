/* Decompiler 37ms, total 264ms, lines 63 */
package org.toni_4819.wantedPlayers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.toni_4819.wantedPlayers.listeners.KillListener;
import org.toni_4819.wantedPlayers.placeholders.WantedExpansion;

public final class WantedPlayers extends JavaPlugin {
    private static WantedPlayers instance;
    private static Economy econ;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        if (!this.setupEconomy()) {
            this.getLogger().severe("Vault not found or no economy plugin hooked. Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            this.getServer().getPluginManager().registerEvents(new KillListener(this), this);
            this.getCommand("wantedreload").setExecutor((sender, command, label, args) -> {
                this.reloadConfig();
                String msg = this.getConfig().getString("messages.reload", "&aConfig reloaded!");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return true;
            });
            if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                (new WantedExpansion(this)).register();
                this.getLogger().info("WantedExpansion registered successfully!");
            } else {
                this.getLogger().warning("PlaceholderAPI not found, Wanted placeholders will not work.");
            }

        }
    }

    public void onDisable() {
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                econ = (Economy)rsp.getProvider();
                return econ != null;
            }
        }
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static WantedPlayers getInstance() {
        return instance;
    }
}