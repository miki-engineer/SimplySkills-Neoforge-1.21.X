# Development handoff

## Current handoff

The main core, class and thirteen Ascendancy checks are complete at their recorded scopes. Normal casting animations are user-confirmed. Basic dedicated-development-server checks passed: login, skill trees, buff casting, projectile visibility and server-side damage. See [verification evidence](verification.md) for measurements and explicit gaps.

Developer maintainability cleanup and its focused runtime checks are complete: class/Ascendancy HUD and casting, plus single-stack Barrier consumption/depletion. All 23 signature and 13 Ascendancy mappings and priority orders matched the pre-refactor code; packet formats were preserved.

The shared multi-stack depletion bug is fixed and its planned runtime checks passed after restart: exact, partial and excess removal through Exhaustion decay, plus Aegis consuming 35 from both 40 and exactly 35 stacks. No probe errors; all agent probes removed. IntelliJ compilation and Java 21 Gradle build passed. Detailed evidence is in [verification.md](verification.md).

Curse/taunt target isolation and focused combat checks passed after the fix. Agony and Torment also passed nearest-eligible-target checks after restart: both selected the farther enemy in the closer-wolf setup, then both returned no eligible target during the friendly-only test. No probe errors; all agent probes removed. This targeting correction changes inherited original behavior to match the nearest-enemy description; see [porting notes](porting-notes.md). Remaining curse-only, restored-source and deferred distinct-caster coverage is listed in [release review](release-review.md).

Packaged-JAR verification is underway outside IntelliJ: the user installed the built JAR in their modpack, entered a world, and confirmed the skill menus/trees and icons looked correct. Projectile orientation: the reported sideways Arcane Bolt prompted an original-source audit. Its four variants and 23 other spell definitions now use TOWARDS_MOTION, matching the original Spell Engine default. Only the original held-item throw and the user-approved custom seeking-hammer spin retain ALONG_MOTION. JSON parsing, Gradle build and IntelliJ build passed. The user confirmed the UI remains normal and Arcane Bolt now looks correct in the modpack. Representative arrow/dagger, shield, meteor/comet and orb visuals remain pending after these edits; the Bolt report does not separately verify all four variants. The shield glimpse was inconclusive; the slowed test gave no usable observation. Further visual checks are deferred at the user's request. Post-removal Ascendancy/item paths remain pending. These are user observations; external modpack logs were not inspected. Dedicated-server packaged startup remains unverified. Existing gameplay evidence still counts.

Prominence integration has been removed at the user's request: alternate ability/tree branches, eight exclusive effects, compatibility config, assets, and the Immersive Melodies development dependency. Normal NeoForge branches are retained, including Bone Armor's armor bonus and 70-second base cooldown. IntelliJ compilation and Java 21 Gradle build passed; packaged inspection found no removed integration classes/assets or compatibility directory entries; leftover empty source/build folders were deleted and the JAR rebuilt successfully. Post-removal packaged startup, world loading and skill-menu display are user-confirmed. Ability HUD/casting, Bone Armor/Chainbreaker/Cyclonic Cleave, Skill Chronicle and Malevolent Manuscript remain pending. Then continue packaged-JAR verification. See the verification notes for scope.

Remaining release work:

- Build and test the packaged JAR outside IntelliJ, including dedicated-server startup.
- Temporary `removeUnlockRestrictions` support has been removed: config field, forced-unlock and click-toggle hooks, category bypass and obsolete translations. IntelliJ compilation, Java 21 Gradle build, translation JSON parsing and packaged-class inspection passed; normal unlock restrictions after this removal still need an in-game check. Existing test-world skill allocations are not reset by this change.
- Second-player visibility/interactions remain unverified and deferred until a two-client setup or external tester is available.
- Review the remaining specific lifecycle/performance concerns and deferred checks in the verification notes; avoid repeating completed shared mechanics.

Session paused for continuation at home; the local dedicated server was stopped through RCON. Its world, configs and command helper are ignored by Git; they must be recreated on another computer. Local RCON command access is described in the verification notes.

## Developer navigation

- [Development guide](development.md): where behavior lives and how to change it safely.
- [Verification evidence](verification.md): observed results, scope limitations and local server setup.
- [Porting notes](porting-notes.md): compatibility changes, intentional balance changes and known original-mod behavior.

Read this handoff before continuing. Update it in place after completed changes, then commit and push. Keep the public README focused on installation and player-facing differences; update it only when those change. Keep test measurements in the verification notes rather than adding a session or commit history here.

