# Simply Skills - NeoForge 1.21.1 Port

This repository contains an in-progress NeoForge 1.21.1 port of [Simply Skills](https://github.com/Sweenus/SimplySkills/tree/1.20.1) by Sweenus and Timefall Development.

The goal is to preserve the original mod's behavior and style while updating its loader integrations, dependencies, data files, effects, rendering, and skill logic for Minecraft 1.21.1.

## Reviewer overview

- Original baseline: Simply Skills 1.7.2 for Fabric 1.20.1.
- Port target: Minecraft 1.21.1, NeoForge 21.1.248, and Java 21.
- Scope: loader migration, dependency updates, API replacements, data-schema updates, and fixes needed to restore original behavior.
- Current state: the port builds and runs, but class-tree testing is still in progress.

Most of the source difference comes from replacing Fabric and 1.20.1 APIs. These changes are not intended to redesign the mod. Actual gameplay differences are listed separately below so they can be reviewed without being mixed with compatibility work.

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
- Ascendancy tree
- Final regression testing and performance cleanup

## Main porting work

- Migrated the project to NeoForge 1.21.1 and Java 21.
- Updated the Gradle setup, mod metadata, registries, networking, mixins, events, attributes, effects, entities, and resource data for current APIs.
- Updated the active dependency set for Puffish Skills, Puffish Attributes, Spell Engine, Spell Power, RPG Series integrations, and supporting APIs.
- Updated skill definitions and resource schemas used by Puffish Skills and Spell Engine.
- Restored custom effect, projectile, HUD, model, and particle rendering affected by the port.
- Replaced legacy UUID-style attribute modifier names with stable readable resource IDs.
- Added a login migration that removes obsolete attribute modifiers left in existing test worlds.

## Differences from the original

The items in "Compatibility work" change the implementation but are meant to keep the original result. The other sections clearly separate a deliberate balance change from corrections where the original Java disagrees with its own skill text or values.

Loader and API changes:

- The mod now uses NeoForge 21.1 instead of Fabric.
- Minecraft was updated from 1.20.1 to 1.21.1, and Java was updated from 17 to 21.
- Puffish Skills, Spell Engine, item data, attributes, damage, particles, models, networking, and mixins were updated for their new APIs.
- The dependencies were replaced with their NeoForge 1.21.1 versions.
- Effect modifiers now have readable IDs instead of UUID-style names. Old `simplyskills:modifier_*` entries are removed when a player logs in.

Compatibility work:

- Skill definitions, spells, effects, projectiles, particles, and models were updated because the old data does not work correctly with the new APIs.
- Swordfall, Judgment, Havensmith's Call, and Righteous Hammers were adjusted to appear and move like they did originally.
- Elemental arrows use the new Spell Engine orientation needed to point in their flight direction. This fixes the sideways rendering caused by the API change; it is not a gameplay change.
- Skill icons, custom effect hooks, targeting, and projectile spawning were updated to restore their original behavior on NeoForge.

Deliberate balance change:

- Raging Javelin now throws every 20 ticks by default instead of every 8 ticks. The interval can be changed in the config.

Performance adjustment:

- Arrow Rain keeps the same radius, density, volley count, and elemental chances. Elemental projectiles launch together from the player when the skill triggers, while the visible normal rain is released in volleys four ticks apart.
- Elemental Artillery only creates a homing projectile after finding a valid enemy. Invalid homing projectiles are removed instead of remaining until their range expires.

Confirmed original code corrections:

- Challenge: the original checks for more than one enemy, while its text grants Haste for each nearby enemy. The port allows one enemy to grant the first stack.
- Rampage Charge: the original starts the charge but does not grant Regeneration or Resistance. Its text explicitly promises both effects, so the port adds them.
- Weapon Expert: the original sets `chance` to `5` but checks whether a random value is greater than `5`, which succeeds 94% of the time. The port reverses that comparison so the value acts as a 5% chance. The skill text only says "occasionally" and does not give a number.
- Elemental Surge Renewal: the original Java has no chance roll and adds the literal value `3` to a duration measured in ticks. This is 3 ticks, or 0.15 seconds, and is not caused by the port. The original skill text says 15% and 3 seconds, so the port uses a 15% roll and adds 60 ticks.
- Arrow Rain tiers: the original checks tier 1 before tiers 2 and 3, so the higher radius and volley values are never selected after their prerequisite is unlocked. The port checks the highest tier first, matching the `+1`, `+2`, and `+3` skill text.
- Elemental Arrows tiers: the original has the same radius-order problem and adds all three quantity bonuses together. The port uses only the highest unlocked tier, matching the radius and quantity values shown in the skill text.

The original code and text used for this comparison are available in [AbilityLogic](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/AbilityLogic.java), [AbilityEffects](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/AbilityEffects.java), [BerserkerAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/BerserkerAbilities.java), [SpellbladeAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/SpellbladeAbilities.java), [RangerAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/RangerAbilities.java), and [the original English skill text](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/resources/assets/simplyskills/lang/en_us.json).

Review and testing support:

- When `removeUnlockRestrictions` is enabled, class and Ascendancy nodes can be clicked again to turn them off. This option is disabled by default.
- Test traces are kept in the IntelliJ debugger. They are not included in the mod.

## In-game verification

- Restored skill visibility and icons in the Puffish Skills interface.
- Fixed Raging Javelin targeting and throwing behavior, then reduced its repeated throw interval.
- Restored Swordfall, Havensmith, Judgment, and related overhead projectile/model behavior.
- Verified the main-tree passive statistics and combat triggers in-game.
- Corrected Berserker Challenge so one nearby enemy is sufficient.
- Restored Regeneration and Resistance granted by Rampage's Charge upgrade.
- Corrected Weapon Expert's inverted Spellforged roll so its `chance = 5` value acts as 5%.
- Corrected Elemental Surge Renewal to match its original description: a 15% roll that adds 3 seconds.
- Verified Cleric healing, sharing, cleansing, resistance, barrier, aura, and Undying interactions.
- Verified Crusader defensive, taunt, mark, hammer, consecration, and related upgrade behavior.
- Verified the complete Berserker and Spellblade trees, including their signature upgrades.
- Verified Ranger Reveal, Tamer, Bonded, Trained, Incognito, and the complete Disengage branch.

## Known review notes

- Ranger Arrow Rain and Elemental Arrows are being tested now. Rogue, Wizard, Necromancer, and Ascendancy testing still remain.
- Final regression testing and performance cleanup will happen after the skill trees are complete.
- The development client can report missing Spell Engine conventional tags and Simply Swords recipes for optional mods that are not installed. These warnings do not stop the client and are not produced by Simply Skills logic.

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
