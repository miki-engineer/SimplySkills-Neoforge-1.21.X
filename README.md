# Simply Skills - NeoForge 1.21.1 Port

This repository contains an in-progress NeoForge 1.21.1 port of [Simply Skills](https://github.com/Sweenus/SimplySkills/tree/1.20.1) by Sweenus and Timefall Development.

The goal is to preserve the original mod's behavior and style while updating its loader integrations, dependencies, data files, effects, rendering, and skill logic for Minecraft 1.21.1.

## Reviewer overview

- Original baseline: Simply Skills 1.7.2 for Fabric 1.20.1.
- Port target: Minecraft 1.21.1, NeoForge 21.1.248, and Java 21.
- Scope: loader migration, dependency updates, API replacements, data-schema updates, and fixes needed to restore original behavior.
- Current state: the port builds and runs; skill-tree main checks are complete at the recorded scopes, with final audits and regression still pending.

Most of the source difference comes from replacing Fabric and 1.20.1 APIs. These changes are not intended to redesign the mod. Actual gameplay differences are listed separately below so they can be reviewed without being mixed with compatibility work.

## Current handoff

The main core, class and thirteen Ascendancy checks are complete at their recorded scopes. Normal casting animations are user-confirmed. Basic dedicated-development-server checks passed: login, skill trees, buff casting, projectile visibility and server-side damage. See [verification evidence](docs/verification.md) for measurements and explicit gaps.

Developer maintainability cleanup and its focused runtime checks are complete: class/Ascendancy HUD and casting, plus single-stack Barrier consumption/depletion. All 23 signature and 13 Ascendancy mappings and priority orders matched the pre-refactor code; packet formats were preserved.

The shared multi-stack depletion bug is fixed and its planned runtime checks passed after restart: exact, partial and excess removal through Exhaustion decay, plus Aegis consuming 35 from both 40 and exactly 35 stacks. No probe errors; all agent probes removed. IntelliJ compilation and Java 21 Gradle build passed. Detailed evidence is in [verification.md](docs/verification.md).

Curse/taunt target isolation and focused combat checks passed after the fix. Agony and Torment also passed nearest-eligible-target checks after restart: both selected the farther enemy in the closer-wolf setup, then both returned no eligible target during the friendly-only test. No probe errors; all agent probes removed. This targeting correction changes inherited original behavior to match the nearest-enemy description; see [porting notes](docs/porting-notes.md). Remaining curse-only, restored-source and deferred distinct-caster coverage is listed in [release review](docs/release-review.md).

Packaged-JAR verification is underway outside IntelliJ: the user installed the built JAR in their modpack, entered a world, and confirmed the skill menus/trees and icons looked correct. Projectile orientation: the reported sideways Arcane Bolt prompted an original-source audit. Its four variants and 23 other spell definitions now use TOWARDS_MOTION, matching the original Spell Engine default. Only the original held-item throw and the user-approved custom seeking-hammer spin retain ALONG_MOTION. JSON parsing, Gradle build and IntelliJ build passed. The user confirmed the UI remains normal and Arcane Bolt now looks correct in the modpack. Representative arrow/dagger, shield, meteor/comet and orb visuals remain pending after these edits; the Bolt report does not separately verify all four variants. Then continue the post-removal Ascendancy/item paths. These are user observations; external modpack logs were not inspected. Dedicated-server packaged startup and removal of temporary testing support remain release work. Existing gameplay evidence still counts.

Prominence integration has been removed at the user's request: alternate ability/tree branches, eight exclusive effects, compatibility config, assets, and the Immersive Melodies development dependency. Normal NeoForge branches are retained, including Bone Armor's armor bonus and 70-second base cooldown. IntelliJ compilation and Java 21 Gradle build passed; packaged inspection found no removed integration classes/assets or compatibility directory entries; leftover empty source/build folders were deleted and the JAR rebuilt successfully. Post-removal packaged startup, world loading and skill-menu display are user-confirmed. Ability HUD/casting, Bone Armor/Chainbreaker/Cyclonic Cleave, Skill Chronicle and Malevolent Manuscript remain pending. Then continue packaged-JAR verification. See the verification notes for scope.

Remaining release work:

- Build and test the packaged JAR outside IntelliJ, including dedicated-server startup.
- Remove temporary `removeUnlockRestrictions` testing support before release.
- Second-player visibility/interactions remain unverified and deferred until a two-client setup or external tester is available.
- Review the remaining specific lifecycle/performance concerns and deferred checks in the verification notes; avoid repeating completed shared mechanics.

Session paused for continuation at home; the local dedicated server was stopped through RCON. Its world, configs and command helper are ignored by Git; they must be recreated on another computer. Local RCON command access is described in the verification notes.

## Developer navigation

- [Development guide](docs/development.md): where behavior lives and how to change it safely.
- [Verification evidence](docs/verification.md): observed results, scope limitations and local server setup.
- [Porting notes](docs/porting-notes.md): compatibility changes, intentional balance changes and known original-mod behavior.

Update this handoff in place after completed changes, then commit and push. Keep test measurements in the verification notes rather than adding a session or commit history here.

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
