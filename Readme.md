# 🎯 WantedPlayers

WantedPlayers is a Paper plugin that adds a **Wanted Level system** based on player killstreaks.  
It rewards bounty hunters with **money via Vault** and displays Wanted stars using PlaceholderAPI.

---

## 🔌 Dependencies

- [Vault](https://www.spigotmc.org/resources/vault.34315/) – economy handling
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) – placeholders
- [LuckPerms](https://luckperms.net/) – groups and permissions  (optionnal)
- [PvSStats Plugin](https://www.spigotmc.org/resources/pvpstats-plugin.69984/) – killstreak tracking

---

## ⚙️ Configuration (`config.yml`)

```yaml
# ============================
# WantedPlayers Configuration
# ============================

# Killstreak thresholds for each Wanted level.
# Example: level 1 requires 1 killstreak, level 2 requires 3, etc.
wanted-levels:
  1: 1
  2: 3
  3: 5
  4: 10
  5: 20

# Symbols used to display stars in placeholders or messages.
stars:
  full: "★"   # Full star for achieved level
  empty: "☆"  # Empty star for levels not reached

rewards:
  # Base reward ranges for each Wanted level.
  # A random value between min and max will be chosen.
  ranges:
    1:
      min: 5
      max: 10
    2:
      min: 10
      max: 20
    3:
      min: 20
      max: 40
    4:
      min: 40
      max: 80
    5:
      min: 80
      max: 150

  # Multipliers applied based on Wanted level.
  # Example: level 3 multiplies the base reward by 2.0.
  multipliers:
    1: 1.0
    2: 1.5
    3: 2.0
    4: 2.5
    5: 3.0

  # Multipliers applied based on LuckPerms primary group.
  # Example: vip group multiplies the reward by 2.5.
  group-multipliers:
    default: 1.0
    vip: 2.5
    vip_plus: 3

messages:
  # Message sent to the killer when they receive coins.
  # Placeholders:
  #   %amount% = final reward amount
  #   %stars%  = star display for Wanted level
  #   %group%  = LuckPerms group name
  kill-reward: "&aYou earned %amount% coins thanks to your Wanted level (%stars%) and your group (%group%)!"

  # Message sent when the config is reloaded with /wantedreload.
  reload: "&aConfig reloaded successfully."

```

---

## 📜 Commands

- `/wantedreload` → reloads the configuration (`config.yml`)
    - Permission: `wanted.reload`

---

## 🪧 Placeholders (via PlaceholderAPI)
### 1.0 and 1.1
- `%wanted_level%` → current Wanted level (1–5)
- `%wanted_stars%` → star display (★/☆)
- `%wanted_multiplier%` → total multiplier (Wanted × group)

### 1.0 and 1.1
- `%wantedplayers_level%` → current Wanted level (1–5)
- `%wantedplayers_stars%` → star display (★/☆)
- `%wantedplayers_multiplier%` → total multiplier (Wanted × group)
---

## 🎮 How it works

1. Each kill increases your **killstreak** (via PvPStats Plugin).
2. Your **Wanted level** is calculated based on thresholds.
3. On each kill, you receive a random reward (min–max) × Wanted multiplier × group multiplier.
4. Coins are added directly to your balance via Vault.
5. A custom message is sent with the amount, stars, and group.  

