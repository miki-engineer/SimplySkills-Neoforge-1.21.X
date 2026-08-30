# Simply Skills - NeoForge 1.21.1 Port

This repository contains an in-progress NeoForge 1.21.1 port of [Simply Skills](https://www.curseforge.com/minecraft/mc-mods/simply-skills) by Sweenus and Timefall Development.

The goal is to preserve the original mod's behavior and style while updating its loader integrations, dependencies, data files, effects, rendering, and skill logic for Minecraft 1.21.1.

## Current status

The port builds and runs on Java 21 with NeoForge 21.1.248. Testing is being done in-game one skill tree at a time, with runtime traces used where an effect cannot be confirmed visually.

Completed testing:

- Core tree and shared passive effects
- Cleric
- Crusader
- Berserker
- Spellblade
- Ranger base passives and Disengage branch

Still in progress:

- Ranger Arrow Rain and Elemental Arrows branches
- Rogue
- Wizard
- Necromancer
- Final regression testing and performance cleanup

## Porting work completed

- Migrated the project to NeoForge 1.21.1 and Java 21.
- Updated the Gradle setup, mod metadata, registries, networking, mixins, events, attributes, effects, entities, and resource data for current APIs.
- Updated the active dependency set for Puffish Skills, Puffish Attributes, Spell Engine, Spell Power, RPG Series integrations, and supporting APIs.
- Updated skill definitions and resource schemas used by Puffish Skills and Spell Engine.
- Restored custom effect, projectile, HUD, model, and particle rendering affected by the port.
- Replaced legacy UUID-style attribute modifier names with stable readable resource IDs.
- Added a login migration that removes obsolete attribute modifiers left in existing test worlds.

## Notable fixes and verification

- Restored skill visibility and icons in the Puffish Skills interface.
- Fixed Raging Javelin targeting and throwing behavior, then reduced its repeated throw interval.
- Restored Swordfall, Havensmith, Judgment, and related overhead projectile/model behavior.
- Verified the main-tree passive statistics and combat triggers in-game.
- Corrected Berserker Challenge so one nearby enemy is sufficient.
- Restored Regeneration and Resistance granted by Rampage's Charge upgrade.
- Corrected Weapon Expert's Spellforged roll to its intended 5% chance.
- Corrected Elemental Surge Renewal to use a 15% roll and add 60 ticks per proc.
- Verified Cleric healing, sharing, cleansing, resistance, barrier, aura, and Undying interactions.
- Verified Crusader defensive, taunt, mark, hammer, consecration, and related upgrade behavior.
- Verified the complete Berserker and Spellblade trees, including their signature upgrades.
- Verified Ranger Reveal, Tamer, Bonded, Trained, Incognito, and the complete Disengage branch.

## Requirements

Core runtime requirements declared by the mod:

- Minecraft 1.21.1
- NeoForge 21.1.248 or newer
- Puffish Skills 0.18.3 or newer
- Puffish Attributes 0.8.3 or newer
- Spell Engine 1.10.2 or newer
- Spell Power 1.6.0 or newer

The development runtime also includes the RPG Series mods and APIs listed in `gradle.properties` and `build.gradle` so their integrations can be tested together.

## Building

Use Java 21 and run:

```shell
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

The built mod jar is placed in `build/libs`.

## License and credits

Simply Skills was created by Sweenus and Timefall Development. This port keeps the original project package structure, assets, credits, and license. See [LICENSE](LICENSE) for the applicable terms.
