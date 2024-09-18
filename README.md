<div align="center">

![](/assets/readme-banner.png)

[![Supports Paper](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/supported/paper_vector.svg)](https://papermc.io)
[![Supports Spigot](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/supported/spigot_vector.svg)](https://spigotmc.org)

[![Patreon](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/donate/patreon-plural_vector.svg)](https://www.patreon.com/eternalcode)
[![Website](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/documentation/website_vector.svg)](https://eternalcode.pl/)
[![Discord](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/social/discord-plural_vector.svg)](https://discord.gg/FQ7jmGBd6c)

[![Gradle](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/built-with/gradle_vector.svg)](https://gradle.org/)
[![Java](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/built-with/java17_vector.svg)](https://www.java.com/)
</div>

### Information

EternalCombat has been tested on Minecraft versions 1.17.1 to 1.21.1, but it should work on most other versions. If you
encounter any compatibility issues, please report them in the
[Issues tab](https://github.com/EternalCodeTeam/EternalCombat/issues). The plugin requires Java 17 or later, so please
make sure it is installed on your server.

## PlaceholderAPI

EternalCombat supports PlaceholderAPI, which allows you to use placeholders in other plugins that support it.
To use placeholders, follow PlaceholderAPI's [instructions](https://wiki.placeholderapi.com/users/) and use the placeholders provided by EternalCombat.
Provided placeholders:
- `%eternalcombat_opponent%` - Returns the name of the player with whom the player is fighting.
- `%eternalcombat_opponent_health%` - Returns opponent health in format `00.00`
- `%eternalcombat_remaining_seconds%` - Returns the number of seconds remaining until the player is no longer in combat.
- `%eternalcombat_remaining_millis%` - Returns the number of milliseconds remaining until the player is no longer in combat.

If the player is not in combat, the placeholders will return an empty string. 
If combat was not caused by other player, opponent placeholders will return empty string.

## Building
Build EternalCombat using:

`./gradlew shadowJar`

### Permissions for EternalCombat

- `eternalcombat.status` - Allows to check if the player is in combat `/combatlog status <player>`
- `eternalcombat.tag` - Allows to create fights between two
  players  `/combatlog tag <first_player> <optional_second_player>`
- `eternalcombat.untag` - Allows to remove player from fight `/combatlog untag <player>`
- `eternalcombat.reload` - Allows to reload plugin `/combatlog reload`
- `eternalcombat.receiveupdates` - Allows you to receive notifications about the new version of the plugin when
  attaching

### Developer API

#### 1. Add repository:

with Gradle:
```kts
maven {
    url = uri("https://repo.eternalcode.pl/releases")
}
```

with Maven:
```xml
<repository>
  <id>eternalcode-reposilite-releases</id>
  <name>EternalCode Repository</name>
  <url>https://repo.eternalcode.pl/releases</url>
</repository>
```

#### 2. Add dependency:

with Gradle:
```kts
compileOnly("com.eternalcode:eternalcombat-api:1.3.2")
```

with Maven:
```xml
<dependency>
  <groupId>com.eternalcode</groupId>
  <artifactId>eternalcombat-api</artifactId>
  <version>1.3.2</version>
  <scope>provided</scope>
</dependency>
```

### Contributing

We welcome all contributions to the EternalCombat project! Please check out [contributing](.github/CONTRIBUTING.md) for
more information on how to contribute and our [code of conduct](./.github/CODE_OF_CONDUCT.md)

### Reporting Issues

If you find an issue with the plugin, please report it in
the [Issues tab](https://github.com/EternalCodeTeam/EternalCombat/issues). Please provide as much information as
possible, including the version of Minecraft and the plugin you are using, as well as any error messages or logs
