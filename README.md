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

Developer maintainability cleanup and its focused runtime checks are complete. Current task: post-restart exact depletion passed twice (5 stacks minus 5, effect=null); verify excess and partial removal and the Aegis boundary before packaged-JAR testing. Then install build/libs/simplyskills-1.7.2+1.21.1-neoforge.jar in a separate Minecraft 1.21.1 NeoForge instance with required dependencies. Verify startup, world loading, skill menu, one class cast and one Ascendancy cast. Existing gameplay checks still count; packaged-JAR verification is pending. The active-skill HUD metadata is consolidated and duplicate single-stack decrement logic delegates to the existing helper. Original comments are retained. Selection priority, category-qualified skill IDs, packet formats and gameplay formulas are preserved. Gradle build passed. All 23 signature and 13 Ascendancy mappings and priority orders were compared with the pre-refactor code and matched. Packet definitions are unchanged. Follow-up inspection found no IDE errors in the refactored files; stale duration/test notes and an unused import were corrected. Older helper warnings are listed in the development guide. Refactor build passed and the dedicated server was restarted; a fresh Client was launched. Runtime Barrier check passed via RCON: amplifier 1 (two stacks) -> amplifier 0 (one stack) -> effect absent, with both hits blocked and health 16.6 unchanged. A third hit after depletion reduced health to 15.6. User confirmed Arcane Bolt icon and casting work normally after restart. User also confirmed Bone Armor icon, armor activation and cooldown in the Ascendancy slot. Focused refactor checks passed: class HUD/cast, Ascendancy HUD/cast and single-stack consumption/depletion.

A focused static review identified shared multi-stack depletion and retained curse/taunt target state that should be addressed before release. See [remaining code review](docs/release-review.md) for triggers, evidence limits and validation plans. The depletion failure was subsequently reproduced through Exhaustion decay (5 stacks minus 5 left one). The shared removal guard is fixed; IntelliJ compilation and Java 21 Gradle build passed, with post-restart exact depletion confirmed twice; excess, partial removal and the Aegis boundary remain pending.

Remaining release work:

- Build and test the packaged JAR outside IntelliJ, including dedicated-server startup.
- Remove temporary `removeUnlockRestrictions` testing support before release.
- Verify second-player visibility/interactions and optional Prominence compatibility where supported.
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
