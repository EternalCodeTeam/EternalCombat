<div align="center">

![](/assets/readme-banner.png)

[![Available on SpigotMC](https://raw.githubusercontent.com/vLuckyyy/badges/main/available-on-spigotmc.svg)](https://www.spigotmc.org/resources/eternalcombat-%E2%9C%94%EF%B8%8F-enchance-your-combat-system-with-eternalcombat.109056/)
[![Available on Modrinth](https://raw.githubusercontent.com/vLuckyyy/badges/main/avaiable-on-modrinth.svg)](https://modrinth.com/plugin/eternalcombat)
[![Available on Hangar](https://raw.githubusercontent.com/vLuckyyy/badges/main/avaiable-on-hangar.svg)](https://hangar.papermc.io/EternalCodeTeam/eternalcombat)

[![Chat on Discord](https://raw.githubusercontent.com/vLuckyyy/badges/main//chat-with-us-on-discord.svg)](https://discord.com/invite/FQ7jmGBd6c)
[![Read the Docs](https://raw.githubusercontent.com/vLuckyyy/badges/main/read-the-documentation.svg)](https://docs.eternalcode.pl/eternalcombat/introduction)
[![Available on BStats](https://raw.githubusercontent.com/vLuckyyy/badges/main/available-on-bstats.svg)](https://bstats.org/plugin/bukkit/EternalCombat/17803)
</div>

### Information

EternalCombat 2.0 has been tested on Minecraft versions **1.17.1 to 1.21.4**, but it should work seamlessly on most
other versions too.
If you run into any compatibility issues, please let us know in
the [Issues tab](https://github.com/EternalCodeTeam/EternalCombat/issues).
The plugin requires **Java 17 or later**, so
ensure your server is ready.
Ready for action?
Install EternalCombat and dive in now!

### How Does EternalCombat Work?

Take your server’s PvP experience to epic heights with **EternalCombat 2.0**! Our robust combat logging system ensures
fair, heart-pounding battles that keep players on their toes. Here’s a rundown of what makes it stand out:

- **Combat Logging**  
  No more dodging fights by logging out! Once players are in combat, they’re committed until the showdown ends. Watch it
  in action:  
  ![](/assets/combatlog.gif)

- **Spawn Protection (Configurable)**  
  Stop players from fleeing to safety! Block access to spawn or safe zones during combat – tweak it to fit your server’s
  rules. See how it works:  
  ![](/assets/border.gif)

- **Fully Customizable Combat**  
  Tailor the combat experience to your liking with a ton of options! From disabling elytra to setting drop rates for
  defeated players, you’re in control. Here’s what you can tweak:

  | Feature              | Description                                                     |
  |----------------------|-----------------------------------------------------------------|
  | Elytra & Inventory   | Disable elytra or inventory access during combat.               |
  | Commands             | Whitelist or blacklist specific commands in combat.             |
  | Damage & Projectiles | Customize damage causes and projectile tagging settings.        |
  | Ender Pearls         | Add cooldowns to pearl usage in fights.                         |
  | Block Placement      | Enable or disable block placement during combat.                |
  | Drop Rates           | Set a percentage of items dropped by defeated players.          |
  | Temporary Effects    | Apply special effects to players in combat for added intensity. |
  | And More!            | Dive into the config for even more fine-tuning options!         |

Curious for more? Check out our quick and exciting YouTube presentation [here](https://youtu.be/5pELO5B0Hhk) to see
EternalCombat in full swing and learn why it’s a game-changer for your server!

### Permissions for EternalCombat

Control who can use EternalCombat’s powerful features with these permissions:

| Permission                     | Description                                                              |
|--------------------------------|--------------------------------------------------------------------------|
| `eternalcombat.status`         | Check a player’s combat status with `/combatlog status <player>`.        |
| `eternalcombat.tag`            | Start a fight between players using `/combatlog tag <player> [player2]`. |
| `eternalcombat.untag`          | Remove a player from combat with `/combatlog untag <player>`.            |
| `eternalcombat.reload`         | Reload the plugin with `/combatlog reload`.                              |
| `eternalcombat.receiveupdates` | Receive notifications about new plugin versions on join.                 |

## PlaceholderAPI

EternalCombat 2.0 fully supports **PlaceholderAPI**, letting you integrate placeholders into other compatible plugins.
Follow the [PlaceholderAPI instructions](https://wiki.placeholderapi.com/users/) to get started.
Here are the available
placeholders:

| Placeholder                         | Description                                                   |
|-------------------------------------|---------------------------------------------------------------|
| `%eternalcombat_opponent%`          | Returns the name of the player you’re fighting.               |
| `%eternalcombat_opponent_health%`   | Returns the opponent’s health in `00.00` format.              |
| `%eternalcombat_remaining_seconds%` | Returns seconds remaining until the player exits combat.      |
| `%eternalcombat_remaining_millis%`  | Returns milliseconds remaining until the player exits combat. |

If a player isn’t in combat, placeholders return an empty string.
If combat wasn’t triggered by another player,
opponent-related placeholders will also return empty.

### Developer API

#### 1. Add repository:

With Gradle:

```kts
maven {
    url = uri("https://repo.eternalcode.pl/releases")
}
```

With Maven:

```xml

<repository>
    <id>eternalcode-reposilite-releases</id>
    <name>EternalCode Repository</name>
    <url>https://repo.eternalcode.pl/releases</url>
</repository>
```

#### 2. Add dependency:

With Gradle:

```kts
compileOnly("com.eternalcode:eternalcombat-api:1.3.2")
```

With Maven:

```xml

<dependency>
    <groupId>com.eternalcode</groupId>
    <artifactId>eternalcombat-api</artifactId>
    <version>1.3.2</version>
    <scope>provided</scope>
</dependency>
```

### Contributing

We’d love your help to make EternalCombat even better!
Check out our [contributing guide](.github/CONTRIBUTING.md) for
details on how to get involved and our [code of conduct](./.github/CODE_OF_CONDUCT.md).

### Reporting Issues

Found a bug?
Report it in the [Issues tab](https://github.com/eternalcodeteam/eternalcombat/issues).
Please include as much detail as possible, like your Minecraft and plugin
versions, along with any error messages or logs.
Ready to transform your server’s combat experience?
Download EternalCombat 2.0 now and let the battles begin!
