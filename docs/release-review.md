# Remaining code review

Static review of shared stack handling, curse targeting and release setup. Stack depletion is fixed and runtime-verified at the recorded scope; curse/taunt target leakage is fixed with focused post-restart isolation and combat checks passed; nearest-enemy selection has a reproduced Agony failure and a fix pending restart validation. This is not an exhaustive code audit. They are outside the completed HUD/helper refactor checks.

## Resolved stack depletion

The shared helper now removes the effect when the requested removal reaches or exceeds its stack count. Exact, partial, excess and Aegis 35-stack checks passed after restart; see [verification.md](verification.md#multi-stack-depletion-fix).

## Address before release

1. **Curse/taunt target leakage: fixed, focused runtime checks passed.** Two-husk traces reproduced shared target retention in Agony and Taunt. TauntedEffect now obtains a local target solely from the affected entity's sourced Taunt instance. AgonyEffect and TormentEffect no longer contain copied taunting ticks or shared entity fields; forced targeting belongs to Taunt, consistent with Torment's 30-point upgrade. Damage/healing hooks, durations, visuals and activation targeting are unchanged. No repository callers used the removed setters. Compilation and Gradle build passed. Post-restart valid sourced Taunt and source-less isolation passed on two fresh husks. Torment redirection and Agony bonus damage/healing also passed after the fix. Restored-source isolation after seeding and remaining curse-only cases remain pending. Distinct-caster behavior remains pending. See verification notes for evidence limits.

## Nearest eligible target — fixed, retest pending

Agony selected the owned test wolf at squared distance 4 and returned false in two casts, despite the farther test husk setup. Both curses now filter living candidates through checkFriendlyFireAOE before selecting minimum distance, matching their nearest-enemy descriptions. Existing rules, search box, spell mechanics and failure handling are preserved. IntelliJ compilation and Java 21 Gradle build passed. After restart, verify Agony and Torment select the farther eligible husk while the owned wolf is closer, and fail normally when only friendly entities or no candidates remain. Torment selection failure was identified from the identical implementation, not separately reproduced before the fix.

## Release gates and lower-priority work

- Temporary `removeUnlockRestrictions` support defaults to false but remains in source. The project handoff requires removing it before release.
- Packaged-JAR installation remains pending. Second-player checks are deferred until a suitable setup or tester is available. Prominence compatibility testing is out of release scope at the user's request; existing integration remains unverified.
- `capStatusEffect` uses translated names, amplifier-based caps and switch fall-through. Its apparent hook is in `SpellforgedEffect`, but SPELLFORGED currently registers `SoulshockEffect`; do not describe the cap hook as a confirmed live failure. Establish reachability and intended caps before fixing or removing it.
- Deprecated APIs, unchecked category lookups, unused helper methods and duplicated particle code remain cleanup candidates, as listed in [development.md](development.md). Treat possible behavior changes separately from mechanical refactors.

The code is ready for reviewer feedback with these findings disclosed. A successful packaged-JAR launch alone is not release approval.
