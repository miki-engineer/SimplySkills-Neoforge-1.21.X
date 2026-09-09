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

Current task: curse/taunt target leakage reproduced across two husks and fixed. Taunt now reads a local target from its own effect instance; Agony and Torment no longer run copied targeting ticks. IntelliJ compilation and Java 21 Gradle build passed. Post-restart Taunt isolation passed: sourced husk A targeted its caster, while source-less husk B retained no target. No probe errors. Torment damage redirection also passed (husk 20 to 17.06 on a 3-damage attempt; player 24.1 through the hook). Agony combat regression passed across four triggers (0.8384 bonus damage and approximately 0.208 healing each). Focused isolation and combat checks are complete at this scope; remaining curse-only, distinct-caster and restored-source checks are explicit in the review notes. All agent probes removed. Nearest-enemy failure reproduced twice: Agony selected the owned wolf at distance 2 and returned false. Agony and Torment now filter candidates through the existing friendly-fire rules before selecting the nearest. IntelliJ compilation and Java 21 Gradle build passed; restart and verify each curse skips the nearer wolf, then verify friendly-only/no-target behavior. Runtime validation of this selection fix is pending. Two-caster and restored-source checks remain pending; see [release-review.md](docs/release-review.md). Packaged-JAR verification also remains pending: install `build/libs/simplyskills-1.7.2+1.21.1-neoforge.jar` in a separate Minecraft 1.21.1 NeoForge instance with required dependencies, then verify startup, world loading, skill menu, one class cast and one Ascendancy cast. Existing gameplay evidence still counts.

Prominence compatibility testing is out of scope for this release at the user's request. Existing integration code is retained; compatibility remains unverified. This scope update changes documentation only; no runtime checks were performed.

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
