# Simply Skills - NeoForge 1.21.1 Port

This repository contains an in-progress NeoForge 1.21.1 port of [Simply Skills](https://github.com/Sweenus/SimplySkills/tree/1.20.1) by Sweenus and Timefall Development.

The goal is to preserve the original mod's behavior and style while updating its loader integrations, dependencies, data files, effects, rendering, and skill logic for Minecraft 1.21.1.

## Reviewer overview

- Original baseline: Simply Skills 1.7.2 for Fabric 1.20.1.
- Port target: Minecraft 1.21.1, NeoForge 21.1.248, and Java 21.
- Scope: loader migration, dependency updates, API replacements, data-schema updates, and fixes needed to restore original behavior.
- Current state: the port builds and runs; skill-tree main checks are complete at the recorded scopes, with final audits and regression still pending.

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
- Ascendancy main checks (scope limitations recorded below)

Still in progress:

- Final casting-animation audit
- Final direct test of every registered effect after all trees are complete
- Final regression testing and performance cleanup

## Resume checkpoint (2026-09-09)

All thirteen Ascendancy abilities have completed their main checks at the recorded scopes. Current phase: final animation audit; Bone Armor and Righteous Hammers casting gestures are now user-confirmed; registered-effect audit, regression, and testing-support cleanup follow. Skyward Sunder movement/animations and recovery were user-confirmed at 30 points; runtime showed 45 ticks, a roughly seven-block rise, and return to ground. Upward strike and final slam each dealt 11.232 at multiplier 1.8 (another cast dealt 10.8 as attributes changed). Fixed Death Mark to trigger at >=30 and Might-to-Barrier to use actual stacks. Post-restart probes confirmed Might III -> Barrier III with matching 1128-tick duration, and two charge hits at exactly 30 points each applied Death Mark for 60 ticks. No probe errors; completed probes removed. Midair damage, lower-point comparison, and nine-stack Barrier cap were not separately verified. Latest code passed IntelliJ compilation and Java 21 Gradle build. Final animation/effect audits and regression remain.

- Ascendancy: Bone Armor, Righteous Hammers, and Cyclonic Cleave main checks are complete at the recorded scope. Cleave's 30-point damage scaling, whirlwind animation, and pull against an AI-enabled zombie are confirmed. Do not repeat these without a relevant change or failure.
- **Magic Circle checks complete:** visible circle, immobilization/recovery, healing gesture, 241 ticks at 1 point and 270 ticks at 30 points, and +30% power in all six schools passed. Corrected the Echo bonus to require owning Wizard Spell Echo. Post-fix runtime checks confirmed 15% with the passive locked inside the circle, and 15% outside / 25% inside with it unlocked. Builds passed; probes removed. Arcane Slash checks are also complete at the scope below.
- Animation work: fixed the shared release-animation callback and moved 12 custom animations into `player_animations`; added Spell Engine's two-handed ground-release gesture to Summoning Ritual. User visually confirmed Cyclonic Cleave, Wizard Arcane Bolt, and Necromancer Summoning Ritual. The last code build passed. Other animation references were checked for asset existence, not all visually tested.
- **Casting-animation coverage:** reviewed all 23 class signatures and 13 Ascendancy activation paths, including effect-driven gestures. Added release gestures to the 15 class abilities that lacked an activation gesture, plus alternate Prominence Bone Armor/Dissonance. The shared server-only helper sends one gesture per successful direct activation; existing spell/effect sequences remain in place. Agony/Torment gestures are user-confirmed after restart, with no animation playback errors found in the client log. Bone Armor and Righteous Hammers gestures are user-confirmed. Selected dependency assets/internal names, IntelliJ compilation and Java 21 Gradle build passed. New class/compatibility gestures are not yet runtime-verified. No cast delay, movement lock, damage, cooldown or duration changes were added.
- Arcane Slash main checks complete at the accepted scope. Arcane Slash was tested at 30 spent Ascendancy points. Verified repaired animation/orientation, upgraded projectile branch, automatic repeats, 3 Attunement stacks per use and a hard 20-stack cap. Three aligned targets received sequential hits (initially 2.106432 damage each, confirmed by health loss). User accepted the practical larger-group piercing check; larger visual size was tentative. Exact damage coefficient/power correlation and the 10-target cutoff were not separately verified. Builds passed.
- Agony main checks complete at the accepted scope, tested with 30 spent points. Application duration verified at 201 ticks for 1 point and 230 ticks for 30 points. Four paired trigger traces confirmed 0.7175 bonus damage applied and 0.1025 health restored per hit (player health 12.933332 to 13.343332). No breakpoint errors. Main bonus-damage and 30-point healing mechanics verified for the same-player caster scenario; multiplayer caster ownership is unverified. User accepted the original healing amount and duration scaling; preserve 10% of Healing spell power per trigger and +1 tick per point. No balance change requested. Duration tooltip still says 8s while the original implementation uses a 200-tick baseline; wording correction remains pending. User requested shorter tests first. All Ascendancy main checks are now complete at their recorded scopes.
- Torment main 30-point checks are complete: curse and Taunt applied for 190 ticks (9.5 seconds); six redirected 3.0-damage hits reduced the zombie health (20 to 17.06 on the first, eventually 0), while player health remained 8.975332 through the sequence. No breakpoint errors. Applied damage is subject to target mitigation. Reuse prior shared Taunt coverage; lower-point comparison and post-expiration hit behavior were not separately checked here.
- Chainbreaker main 30-point mechanics verified after the fix: Weakness II and Slowness II were fully removed, Night Vision retained its exact 1006-tick duration, and Might IV, Marksmanship IV and Undying applied for 120 ticks (6 seconds). Health stayed 14.84; no breakpoint errors. The fix removes complete non-beneficial effects from a snapshot, resolving the previous one-level decrement. Shared helper unchanged; reuse earlier Undying survival coverage. Added the requested spell_engine:one_handed_shout_release gesture once per activation for caster and tracking players. Animation asset name checked, Gradle build passed, and user confirmed the shout animation in-game. Chainbreaker checks complete at this scope.
- Ghostwalk main checks complete at 30 spent points: starts at 60 ticks (3 seconds) with Soulshock IV. Eight paired hits captured; raw damage 8.7125, first zombie health 14 to 5.4269 after mitigation. First heal raised player health 12.965332 to 17.321583 (+4.356251, half raw damage); next hit reached the 20-health cap. Expiration reached the gravity-reset statement. User confirmed Wither damage stopped during Ghostwalk, resumed afterward, and normal movement returned. Wither was present in the start trace; damage cancellation itself was not captured by the mixin probe. Invulnerability/recovery acceptance is based on user observation. Lower-point scaling and multiplayer interactions were not separately tested.
- Rapidfire gameplay and projectile orientation checks complete at the recorded scope. Bow trace verified at 30 spent points: 150-tick duration (7.5 seconds), 23 projectile-dispatch calls, and one Marksmanship stack per third dispatch, reaching VII. No breakpoint errors. Crossbow trace also verified at 30 points using archers:netherite_rapid_crossbow: 150 ticks and 23 projectile-dispatch calls, reaching Marksmanship VIII. The previous bow cast left the arrow counter at 2, so the first crossbow arrow completed the next group of three; counter persistence across casts is confirmed. No breakpoint errors. User confirmed normal weapon use returns. Warden damage verified: entity 2523 health fell from 16.497253 to 7.7335033 between two arrow damage events, confirming 8.76375 health lost from the first hit. The second incoming amount was 9.640125 with Marksmanship I; final health/death was not captured. No probe errors. User confirmed the character animation and walking during Rapidfire, then reported the arrow projectile flying sideways. Changed rapidfire_projectile model orientation from ALONG_MOTION to TOWARDS_MOTION, following the verified Arcane Slash correction. JSON parsing and Gradle build passed; user confirmed the corrected arrow direction after a full restart. Launch velocity remains 8.9. Inspection of the installed Spell Engine damage calculation confirmed projectile speed does not multiply Rapidfire impact damage. Projectile definition uses 0.9 times physical ranged power before other modifiers/mitigation. Normal crossbow comparison captured two arrow events with no active shooter effects: each incoming amount was 10.25, and Warden health 490.775 to 480.525 confirmed 10.25 actual damage for the first. Tested Rapidfire baseline 8.76375 is 14.5% lower per arrow than this normal shot; this comparison is not a DPS measurement. Multiplayer counter isolation and lower-point behavior remain unverified.
- Weaker weapon comparison: archers:rapid_crossbow Rapidfire baseline 7.8412495 versus netherite 8.76375 (10.5% lower); Marksmanship I 8.625375 and VII 13.330124. All 23 hits reached Warden 3015; health 470.275 to 224.05983 confirms approximately 246.215 total burst damage. Five subsequent normal shots with no active effects registered 9.225, 8.2, 7.175, 9.225 and 8.2 (mean 8.405); successive health readings confirm the first four. Normal-shot variation was observed, not diagnosed; the earlier netherite normal sample contained only two 10.25 hits. No breakpoint errors. Weapon-dependent Rapidfire damage is verified; this is not a controlled normal-fire DPS comparison.
- Git transfers source/assets and this handoff, but not `run` worlds/configs or IDE debugger state. Pull `main`, use Java 21, run `gradlew.bat build`, then launch the IntelliJ `Client` configuration. Use a full client restart for resource changes. Recreate test-world unlocks if needed; do not assume the old world is present.
- HUD alignment: moved both ability frames one pixel right so their centers match the icons, cooldown overlays, and key labels. IntelliJ compilation passed and the user confirmed the alignment looks good after restarting.
- Animation-name repair: IDE console captured repeated null-animation exceptions from Spell Engine playback. Player Animator registers internal JSON names, and Arcane Slash requested arcane_slash_alt while its file declared one_handed_slash_horizontal_right. Corrected five custom internal names to match their resource IDs (arcane_slash, arcane_slash_alt, ground_cleave, shield_throw, upward_slash). All 12 custom files parsed and all 97 spell animation references resolved against internal names; Gradle build passed. After restart, the registry probe confirmed the requested slash name is present and the old name absent. No null-animation playback exceptions were found in the new IDE console; the remaining observed null-pointer messages were startup recipe-category warnings. User confirmed the visuals look good. Removed the completed registry probe; the earlier filename-only validation was insufficient.
- Righteous Shield at 30 points: passive Aegis generation verified every 400 player ticks. Base shield consumes low stacks; three casts directly hit three zombies with two ricochets and matching 0.96278244 health loss per hit. Fixed ricochet travel skipping collision checks and previously hit entities masking new targets; changes are scoped to the base/second-tier shield, with velocity/damage unchanged. Fixed upgrade thresholds to use actual stack counts (amplifier + 1), at 5/10/15. Gradle build passed. Exact five stacks selected the upgraded projectile; a subsequent six-stack throw hit six distinct zombies, verifying five ricochets. Healing tier verified at 11 stacks: tagged tamed wolf health 5 to 6.706625 (+1.706625), consuming 10 stacks and leaving one; no probe errors. Passive gain prevented an exact ten-stack boundary check. Exact fifteen-stack selection verified: zombie health 20 to 14.336106 (5.663894 lost), Death Mark present with 153 ticks remaining; no probe errors. One Aegis stack incorrectly remained after spending exactly 15. The shared decrement helper recreated an effect with amplifier -1; added shield-specific consumption handling that removes Aegis when no stacks remain, preserving surplus-stack behavior and leaving the shared helper unchanged. IntelliJ compilation and Gradle build passed. After full restart, exact fifteen- and ten-stack casts each selected their matching tier cost (15 and 10), with Aegis=null immediately after consumption; no probe errors. Exact ten-stack selection and depletion are verified. Five-stack retry gained a passive stack before release: before=6, cost=5, one stack remained, confirming surplus preservation after the fix. Exact five-stack depletion also passed after retry (before=5, cost=5, Aegis=null), with no probe errors. All three exact-tier selection/depletion checks are now verified. Six zombie melee-block traces confirmed takeShieldHit -> goldenAegis -> incrementStatusEffect, each with current=18, new=18, cap=18; no probe errors after correcting the debugger condition. Melee trigger and cap retention verified; below-cap gain from a melee block was not separately sampled (shared increment behavior reused). Main Righteous Shield checks complete at this scope. Larger final-tier visual still needs user confirmation.
- Startup performance: user reported world-entry lag. Current IDE log records a 4136 ms server backlog after joining and 2132 ms during the first shield cast. First flight probe events had a 6.5-second gap; later casts completed promptly. Debugger overhead is a possible contributor, not a proven cause. User reports the issue is no longer occurring after probe removal; causality is not established. No performance code change made. Revisit only if it recurs.
- Cataclysm main checks complete: user confirmed Fire/Frost visuals, channel animation, and movement recovery. At 30 points: 70 ticks, four dispatches every 17 ticks at distances 5/10/15/20, granting Spellforged I-IV with 60-tick refreshes. At 29 points: three dispatches every 18 ticks at distances 10/15/20 and final Spellforged=null after clearing it beforehand. Frost 7.584 > Fire 1.304 selected comets; Fire 10.432 > Frost 1.264 selected meteors. Falling-impact stacks and sequential Warden health confirmed Frost hits of 17.693604 and 14.423317 and a Fire hit of 39.038227. Later incoming amounts were captured without final health. Exact coefficient/dropoff correlation and frequency cap were not separately verified; no Cataclysm source changes were needed.
- Debugger handoff: all agent probes removed; user-owned probes retained. Current Client was launched normally for visual testing. Worlds/configs and skill selections are local only; do not assume the previous home setup is active here.

### Next tests on either computer

Bone Armor and Righteous Hammers casting gestures passed user visual confirmation after restart. Both code builds passed previously; the current client log contains no animation playback errors. Reuse their completed gameplay checks unless a new failure appears.

Agony and Torment now send spell_engine:one_handed_projectile_release once in their successful server activation branches, after applying the curse to a valid target. Existing effects, smoke, sounds, targeting and failed-cast behavior are unchanged. Shared activation/effect paths were checked for duplicate gestures, and the installed asset internal name was verified. IntelliJ compilation and Java 21 Gradle build passed. User confirmed both successful-cast gestures after full Client restart; no animation playback errors were found in the current client log. Casting without a valid nearby enemy remains an unverified negative check. Other class gesture gaps are now filled; use the checklist below after restart.

Casting-gesture visual checklist:

| Class | Abilities and gestures |
| --- | --- |
| Berserker | Rampage, Bloodthirsty, Berserking: one-handed shout |
| Rogue | Evasion and Preparation: outward area release; Siphoning Strikes: crossed weapons |
| Ranger | Disengage: outward area release; Elemental Arrows: healing/enchantment release; Arrow Rain: upward archery release |
| Spellblade | Elemental Surge: area release; Elemental Impact: forward release; Spellweaver: crossed weapons |
| Cleric | Anoint Weapon: healing/enchantment release |
| Crusader | Sacred Onslaught: forward release; Consecration: two-handed ground release |
| Ascendancy | Agony and Torment: forward release, visually confirmed after restart |
| Optional Prominence | Alternate Bone Armor: ground release; Dissonance: shout (dependency not installed locally) |

Existing coverage retained: Wizard Meteor Shower/Ice Comet/Static Discharge/Arcane Bolt; Cleric Divine Intervention/Sacred Orb; Crusader Heavensmith's Call; Necromancer Summoning Ritual; all other Ascendancy gestures, including delayed Rapidfire/Skyward Sunder sequences. Reuse prior visual evidence where recorded.

After one full Client restart, test the new gestures in third person by toggling nodes. Agony/Torment are confirmed; next test Berserker Rampage, Bloodthirsty and Berserking, one at a time using the signature key, then the remaining class groups above. Confirm a suitable gesture, normal pose recovery, and no playback errors; watch interactions with movement and effect-driven follow-up animations. Failed Agony/Torment casts should not gesture. Gameplay regression and multiplayer visibility remain pending; static coverage is not a visual pass.
For quick switching, enable local `removeUnlockRestrictions` and click the previous active node off before selecting the next. No repeated skill commands are needed when toggling works. Worlds/configs do not transfer through Git.

After animations, continue direct registered-effect checks using shared-effect evidence, resolve tooltip discrepancies (including Agony duration), and complete regression/performance review. Remove temporary `removeUnlockRestrictions` support before release. Keep pending checks explicit and push completed changes with this handoff.
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
- Ability cooldown reduction treats Spell Power haste 1.0 as the baseline, applying reduction only to the bonus above it. Bone Armor's normal-haste cooldown is verified at 70 seconds.
- Expiration follow-up hooks run after the current effect iteration to prevent duplicate callbacks when they add effects. Bone Armor follow-ups and Undying were rechecked; broader lifecycle regression testing remains pending.
- Rage damage and receive-hit stack gain are applied after Minecraft accepts a hit, preventing repeated contact from bypassing normal hurt immunity, adding extra stacks, or causing continuous knockback.

Deliberate balance change:

- Raging Javelin now throws every 20 ticks by default instead of every 8 ticks. The interval can be changed in the config.
- Ascendancy Righteous Hammers lasts 20 seconds instead of the original 40 seconds, at the user's request. Its base cooldown remains 60 seconds.

Requested visual addition:

- Righteous Hammers now has a Simply Skills first-person world-render hook, reusing the existing orbiting models. The user verified first-person visibility with five orbiting hammers. This does not change its damage or targeting.
- Seeking Righteous Hammer projectiles use a centered end-over-end spin in a vertical plane; visual verification is pending. Orbiting hammer orientation is unchanged.
- Custom player animations load from `player_animations`, and direct spell delivery sends the configured release gesture. Summoning Ritual also plays a Spell Engine two-handed casting gesture. Cleave, Arcane Bolt, and Summoning Ritual were visually confirmed.

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
- Shadow Aura now deals its stated normal damage and stops ticking after its minion dies. Shadow Combust also blocks recursive calls during its lethal self-damage, preventing the death handler from producing a second explosion.
- Endless Servitude now starts at the stated 20% chance and still gains 5% for each harmful effect, up to 60%.
- Greater Dreadglare only gains Might when it has harmful effects, with one Might level for each harmful effect.

The original code and text used for this comparison are available in [AbilityLogic](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/AbilityLogic.java), [AbilityEffects](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/AbilityEffects.java), [BerserkerAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/BerserkerAbilities.java), [SpellbladeAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/SpellbladeAbilities.java), [RangerAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/RangerAbilities.java), [RogueAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/RogueAbilities.java), [WizardAbilities](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/abilities/WizardAbilities.java), [StaticChargeEffect](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/effects/StaticChargeEffect.java), [StealthEffect](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/java/net/sweenus/simplyskills/effects/StealthEffect.java), and [the original English skill text](https://github.com/Sweenus/SimplySkills/blob/1.20.1/src/main/resources/assets/simplyskills/lang/en_us.json).

Review and testing support:

- For quick animation testing, enable `removeUnlockRestrictions: true` in `run/config/simplyskills/general.json5` before starting Client. Click an unlocked class/Ascendancy node again to turn it off, then click the next node to unlock it. Turn off the previous active ability before selecting another. Enabled on this computer; the local config is not transferred by Git and the source default remains false. Config parsing passed; click toggling still needs confirmation in the next client run.
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
- Retested the Necromancer corrections: Winterborn stacks, Fortification armor/toughness, normal Shadow Aura damage, Endless Servitude's 20% base chance, and Greater Dreadglare Might levels. Combustion regression confirmed one explosion for both aura-triggered and direct deaths after fixing recursive death-handler calls.

## Known review notes

- Ascendancy main checks are complete at the recorded scopes; final effect/animation audits and regression remain.
- Intermittent first-use input/cooldown behavior after world entry remains unresolved; an isolated rejoin test worked. Reproduce before changing input handling.
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
