/* Decompiler 17ms, total 180ms, lines 37 */
package org.toni_4819.wantedPlayers.utils;

import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import org.toni_4819.wantedPlayers.WantedPlayers;

public class WantedManager {
    public static int getWantedLevel(int killstreak) {
        FileConfiguration config = WantedPlayers.getInstance().getConfig();
        int level = 0;
        Iterator var3 = config.getConfigurationSection("wanted-levels").getKeys(false).iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            int required = config.getInt("wanted-levels." + key);
            if (killstreak >= required) {
                level = Integer.parseInt(key);
            }
        }

        return level;
    }

    public static String getStars(int level) {
        FileConfiguration config = WantedPlayers.getInstance().getConfig();
        String full = config.getString("stars.full");
        String empty = config.getString("stars.empty");
        StringBuilder sb = new StringBuilder();

        for(int i = 1; i <= 5; ++i) {
            sb.append(i <= level ? full : empty);
        }

        return sb.toString();
    }
}