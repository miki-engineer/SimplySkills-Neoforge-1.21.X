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
- Ranger
- Rogue
- Wizard
- Necromancer

Still in progress:

- Ascendancy tree
- Final direct test of every registered effect after all trees are complete
- Final regression testing and performance cleanup

## Current session handoff (2026-09-07)

- Continued on the new computer; Git was synchronized and the Gradle build succeeded. The development client launched under IntelliJ's debugger using Java 21.0.12.
- The old test world and local configs were not transferred. A fresh test world needs Ascendancy unlocked using `/puffish_skills category unlock @s simplyskills:ascendancy`; testing points can be granted using `/puffish_skills points add @s simplyskills:ascendancy 40`.
- Ascendancy testing has started with Bone Armor. Runtime traces confirmed 1 spent Ascendancy point, amplifier 3 (4 layers), and 800 ticks (40 seconds). With a diamond chestplate, armor/toughness changed from 8/2.0 before casting to 12/2.8 at four layers, then 11/2.6, 10/2.4, and 9/2.2 as layers were consumed. The per-layer attribute bonuses are verified. Four layer-removal callbacks were recorded during the requested two-hit scenario; actual accepted-hit count was not established. A follow-up isolated `/damage @s 1 minecraft:generic` after a fresh cast produced exactly one layer-decrement callback at amplifier 3; no duplicate callback was observed for that command. Rapid-hit/hurt-immunity behavior remains unverified. Scaling progress and the next steps are recorded below.
- Confirmed the Bone Armor cooldown bug with runtime values: base cooldown after gem effects 70000 ms, haste 1.0, reduction modifier 1.6, outgoing cooldown 66800 ms. The shared signature/Ascendancy cooldown formula now subtracts only haste above the 1.0 baseline, preserving gem adjustments and the existing minimum cooldown. Normal haste now calculates 70000 ms. This is a Simply Skills fix; the Spell Power API is unchanged.
- Cooldown fix validation: `gradlew.bat build` passed with Java recompilation; no automated tests exist. After restarting the client, a successful Bone Armor cast confirmed haste 1.0 and an outgoing cooldown of exactly 70000 ms. After removing the completed cast/cooldown probes to reduce debugger overhead, the user confirmed the visible cooldown is correct. Baseline cooldown verification is complete. Bonus-haste testing and the remaining Bone Armor checks are still pending. The HUD rounds down whole seconds, so a valid 70-second cooldown may first display 69 after time has elapsed. An earlier brief freeze was reported; its cause was not established.
- User workflow: update this README and commit/push each completed project change so work can continue from another computer or session. See `AGENTS.md`.
- Natural expiration of seven intact layers is verified: at 30 spent points, the cast applied amplifier 6 for 800 ticks; the removal probe then recorded amplifier 6, `undying=null`, and `regeneration=null`, with no intervening layer-decrement events. Thus a full unused Bone Armor expiring grants neither follow-up effect. In the one-layer expiration check, six isolated damage callbacks consumed layers 7 through 2; the remaining layer expired without another damage callback and granted Undying and Regeneration IV for 160 ticks. The skill text says the last layer is "removed" and does not explicitly exclude expiration, so this alone is not classified as a bug. However, two final-removal hook outputs occurred about 108 ms apart, both showing 160-tick effects. Duplicate invocation cause/impact remains unresolved. A non-suspending final-layer probe now includes the entity tick and stack trace. Next: repeat the six-damage-then-expire scenario to identify both call paths before changing code.
- Bone Armor scaling follow-up: a cast recorded 11 spent Ascendancy points (not the requested 10), amplifier 4 (5 layers), 800 ticks, armor 13, and toughness 3.0, from a baseline of armor 8/toughness 2.0. This verifies scaling within the 10–19-point band, not the exact 9-to-10 boundary. The next cast confirmed exactly 20 spent points, amplifier 5 (6 layers), 800 ticks, armor 14, and toughness 3.2. At exactly 30 spent points, the cast applied amplifier 6 (7 layers), 800 ticks, armor 15, and toughness 3.4. Seven isolated damage commands produced seven decrement callbacks, with amplifiers 6 through 0 and the expected descending attributes. On the final removal, the debugger confirmed Undying and Regeneration IV, both with 160 ticks (8 seconds). Scaling at the tested totals and final-layer effect application are verified; exact threshold transitions, rapid-hit/hurt-immunity handling, natural expiration behavior, and the granted effects' actual protection/healing remain to be checked. Bonus-haste cooldown verification also remains pending.

## Main porting work

- Migrated the project to NeoForge 1.21.1 and Java 21.
- Updated the Gradle setup, mod metadata, registries, networking, mixins, events, attributes, effects, entities, and resource data for current APIs.
- Updated the active dependency set for Puffish Skills, Puffish Attributes, Spell Engine, Spell Power, RPG Series integrations, and supporting APIs.
- Updated skill definitions and resource schemas used by Puffish Skills and Spell Engine.
- Custom area spells use Spell Engine's friendly-target rules.
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
- Fan of Blades uses the same new orientation so its daggers point in their flight direction instead of rendering sideways.
- Skill icons, custom effect hooks, targeting, and projectile spawning were updated to restore their original behavior on NeoForge.
- Player-kill callbacks now use NeoForge's living-death event, restoring Bloodthirsty healing and the Ranger and Rogue Renewal upgrades after the original callback target was removed in 1.21.1.
- Evasion now uses NeoForge's incoming-damage event and respects Minecraft's normal hurt immunity before rolling again.
- Rage damage and receive-hit stack gain are applied after Minecraft accepts a hit, preventing repeated contact from bypassing normal hurt immunity, adding extra stacks, or causing continuous knockback.

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
- Opportunistic Mastery: the original applies poison without checking for Stealth even though every tier says it requires Stealth. The port performs that check.
- Fan of Blades and Siphoning Strikes: the original passes the configured stack count directly as an effect amplifier, displaying one extra stack. The port converts the count to the correct zero-based amplifier.
- Wizard upgrade tiers: the original checks the lower Ice Comet, Leap, Speed, and Meteoric Wrath Renewal upgrades first, preventing higher unlocked tiers from taking effect. The port checks the highest tier first.
- Frost Volley and Static Discharge Leap: the original passes their configured counts directly as effect amplifiers, producing one extra volley or leap. The port converts the count to the correct zero-based amplifier.
- Meteoric Wrath Renewal: the original comparison makes each configured chance one percent higher, and its default config produces 10%, 25%, and 40% despite the text promising 10%, 30%, and 50%. The port uses the exact chances shown in the skill text.
- Ability cooldowns: the original uses wall-clock time, so cooldowns expire while a single-player world is paused. The port advances cooldowns with active client ticks instead.
- Bladestorm: the original uses a separate random roll after each Fan of Blades pulse. Its text grants one stack for each enemy hit, so the port increments once for each valid target hit and keeps the 20-stack cap.
- Shadow Veil: the original schedules Resistance using the Regeneration frequency. The port uses the Resistance frequency from its config.
- Exploitation: the original raw yaw comparison fails when rotations cross the `-180/180` boundary. The port uses the wrapped angular difference while keeping the original 32-degree rear arc.
- Evasion no longer blocks `/kill` or void damage, and Rage no longer allows very large damage values to overflow into invalid player health.
- Winterborn now converts Frost spell power into the displayed number of Soulshock stacks without adding an extra level.
- Necrotic Fortification now grants exactly half of the player's Armor and Armor Toughness, or the full values to a Greater Dreadglare, without hidden base points.
- Shadow Aura now deals its stated normal damage. It also stops ticking after its minion dies, preventing Shadow Combust from triggering twice.
- Endless Servitude now starts at the stated 20% chance and still gains 5% for each harmful effect, up to 60%.
- Greater Dreadglare only gains Might when it has harmful effects, with one Might level for each harmful effect.

The original code and text used for this comparison are available in [AbilityLogic](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/AbilityLogic.java), [AbilityEffects](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/AbilityEffects.java), [BerserkerAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/BerserkerAbilities.java), [SpellbladeAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/SpellbladeAbilities.java), [RangerAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/RangerAbilities.java), [RogueAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/RogueAbilities.java), [WizardAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/WizardAbilities.java), [StaticChargeEffect](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/effects/StaticChargeEffect.java), [StealthEffect](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/effects/StealthEffect.java), and [the original English skill text](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/resources/assets/simplyskills/lang/en_us.json).

Review and testing support:

- When `removeUnlockRestrictions` is enabled, class and Ascendancy nodes can be clicked again to turn them off. This option is disabled by default.
- `removeUnlockRestrictions` is temporary testing support. It must be removed before release so the original path requirements and exclusive branches are fully restored.
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
- Verified the complete Arrow Rain branch, including its Elemental enhancement, five-wave rain, Minefield, and Elemental Artillery.
- Verified the complete Elemental Arrows branch, including all attunements, quantity and radius tiers, isolated and grouped targeting, and Renewal.
- Confirmed the staggered Arrow Rain removes the observed launch lag while keeping the original arrow count, elemental chances, and player-to-rain projectile path.
- Verified the complete Rogue tree, including Stealth requirements, rear-angle detection, Smoke Bomb, Shadow Veil, Evasion, Fan of Blades, Bladestorm, Siphoning Strikes, and their upgrades.
- Confirmed successful Evasion rolls cancel damage without starting the hurt animation, while failed rolls still behave as normal hits.
- Verified Fan of Blades and Siphoning Strikes start with the displayed stack counts, Bladestorm gains one stack per enemy hit, and Fan of Blades Renewal adds two stacks per kill.
- Rechecked the repaired kill callbacks: Bloodthirsty restores 25% maximum health, Elemental Arrows Renewal follows its 35% roll and adds one stack, and Fan of Blades Renewal adds two stacks up to 20.
- Verified the complete Wizard tree with Wizards RPG Series installed, including spell-power scaling, every signature branch, isolated and combined upgrades, projectile behavior, effect counts, renewal chances, and ability cooldown pausing.
- Confirmed the corrected combined Wizard tiers select Greater ++, 52 Static Discharge leaps, a 15% Speed chance, and exactly six Frost Volley shots.
- Verified the complete Necromancer tree, including minion attributes and limits, Wraith effects, harmful-effect transfers, defensive effects, death triggers, auras, life siphoning, resurrection chances, and Greater Dreadglare traits.

## Known review notes

- Ascendancy testing still remains.
- Lightning Ball rolls a 5% discharge chance per nearby entity every five ticks. Its original text only describes this as periodic.
- After every tree is complete, each registered effect will be tested directly to confirm that its actual mechanics work, not only that the effect icon or activation appears.
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
