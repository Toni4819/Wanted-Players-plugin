package org.toni_4819.wantedPlayers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.toni_4819.wantedPlayers.listeners.KillListener;
import org.toni_4819.wantedPlayers.placeholders.WantedExpansion;

// Imports bStats
import org.bstats.bukkit.Metrics;

public final class WantedPlayers extends JavaPlugin {
    private static WantedPlayers instance;
    private static Economy econ;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        if (!this.setupEconomy()) {
            this.getLogger().severe("Vault not found or no economy plugin hooked. Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            // Register events
            this.getServer().getPluginManager().registerEvents(new KillListener(this), this);

            // Command /wantedreload
            this.getCommand("wantedreload").setExecutor((sender, command, label, args) -> {
                this.reloadConfig();
                String msg = this.getConfig().getString("messages.reload", "&aConfig reloaded!");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return true;
            });

            // PlaceholderAPI hook
            if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new WantedExpansion(this).register();
                this.getLogger().info("WantedExpansion registered successfully!");
            } else {
                this.getLogger().warning("PlaceholderAPI not found, Wanted placeholders will not work.");
            }

            // --- bStats Metrics ---
            int pluginId = 29042;
            Metrics metrics = new Metrics(this, pluginId);

           // metrics.addCustomChart(new SimplePie("wanted_mode", () -> "enabled"));

            this.getLogger().info("bStats metrics initialized!");
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("WantedPlayers plugin disabled.");
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static WantedPlayers getInstance() {
        return instance;
    }
}
