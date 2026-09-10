# Remaining code review

Static review of shared stack handling, curse targeting and release setup. Stack depletion is fixed and runtime-verified at the recorded scope; curse/taunt target leakage is fixed with focused post-restart isolation and combat checks passed; nearest-enemy selection is fixed with post-restart success and no-eligible-target paths verified. This is not an exhaustive code audit. They are outside the completed HUD/helper refactor checks.

## Resolved stack depletion

The shared helper now removes the effect when the requested removal reaches or exceeds its stack count. Exact, partial, excess and Aegis 35-stack checks passed after restart; see [verification.md](verification.md#multi-stack-depletion-fix).

## Address before release

1. **Curse/taunt target leakage: fixed, focused runtime checks passed.** Two-husk traces reproduced shared target retention in Agony and Taunt. TauntedEffect now obtains a local target solely from the affected entity's sourced Taunt instance. AgonyEffect and TormentEffect no longer contain copied taunting ticks or shared entity fields; forced targeting belongs to Taunt, consistent with Torment's 30-point upgrade. Damage/healing hooks, durations, visuals and activation targeting are unchanged. No repository callers used the removed setters. Compilation and Gradle build passed. Post-restart valid sourced Taunt and source-less isolation passed on two fresh husks. Torment redirection and Agony bonus damage/healing also passed after the fix. Follow-up curse-only and saved/reloaded source-less Taunt isolation passed on the development server. Distinct-player caster behavior remains untested. See verification notes for evidence limits.

## Nearest eligible target — fixed and verified

Agony selected the owned test wolf at squared distance 4 and returned false in two casts, despite the farther test husk setup. Both curses now filter living candidates through checkFriendlyFireAOE before selecting minimum distance, matching their nearest-enemy descriptions. Existing rules, search box, spell mechanics and failure handling are preserved. IntelliJ compilation and Java 21 Gradle build passed. After restart, both curses selected the farther tagged husk in the closer-owned-wolf setup and applied 190 ticks. Later friendly-only attempts each returned false with target=none; no probe errors. An earlier attempt found a leftover test husk and is not counted as a no-target pass. Torment selection failure was identified from the identical implementation, not separately reproduced before the fix.

## Release gates and lower-priority work

- **Progression regression: PASS.** Zero-point rejection, root prerequisites/exclusivity, the second-specialisation guard, Ascendancy unlock at 42 spent nodes and the 42-node cap were verified. Boundary setup used administrator-prefilled nodes.
- **Post-removal smoke checks: PASS.** Bone Armor, Chainbreaker, Cyclonic Cleave, Skill Chronicle XP/build restoration and both manuscripts passed at the recorded scopes. Projectile visual smoke checks are user-confirmed. Packaged-JAR installation, world loading, UI and Arcane Bolt are also user-confirmed. Second-player checks and packaged dedicated-server startup have not been run.
- `capStatusEffect` uses translated names, amplifier-based caps and switch fall-through. Its apparent hook is in `SpellforgedEffect`, but SPELLFORGED currently registers `SoulshockEffect`; do not describe the cap hook as a confirmed live failure. Runtime effect-add traces confirmed ordinary SPELLFORGED application uses SoulshockEffect; the cap helper probe did not fire in that test. No live cap failure was established. Intended caps remain a separate review question.
- Deprecated APIs, unchecked category lookups, unused helper methods and duplicated particle code remain cleanup candidates, as listed in [development.md](development.md). Treat possible behavior changes separately from mechanical refactors.

The code is ready for reviewer feedback with these findings disclosed. A successful packaged-JAR launch alone is not release approval.
