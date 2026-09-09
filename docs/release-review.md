# Remaining code review

Static review of shared stack handling, curse targeting and release setup. Stack depletion is fixed and runtime-verified at the recorded scope; curse/taunt target leakage now has runtime evidence and a fix pending restart validation; nearest-enemy selection remains a static concern. This is not an exhaustive code audit. They are outside the completed HUD/helper refactor checks.

## Resolved stack depletion

The shared helper now removes the effect when the requested removal reaches or exceeds its stack count. Exact, partial, excess and Aegis 35-stack checks passed after restart; see [verification.md](verification.md#multi-stack-depletion-fix).

## Address before release

1. **Curse/taunt target leakage: fixed, runtime retest pending.** Two-husk traces reproduced shared target retention in Agony and Taunt. TauntedEffect now obtains a local target solely from the affected entity's sourced Taunt instance. AgonyEffect and TormentEffect no longer contain copied taunting ticks or shared entity fields; forced targeting belongs to Taunt, consistent with Torment's 30-point upgrade. Damage/healing hooks, durations, visuals and activation targeting are unchanged. No repository callers used the removed setters. Compilation and Gradle build passed. Verify source-less and restored Taunt cannot inherit another mob's caster, valid Taunt still selects its own caster, curses alone do not force a target, and existing curse combat hooks still work. Distinct-caster behavior remains pending. See verification notes for evidence limits.

## Resolve targeting intent

Agony and Torment choose the nearest living entity before applying the friendly-fire filter. A friendly entity closer than an enemy can cause the cast to fail instead of choosing the nearest eligible enemy. Their description says nearest enemy. Confirm the intended rule, then filter eligible enemies before selecting the minimum distance if that is the intended behavior. Test a friendly mob nearer than a hostile one.

## Release gates and lower-priority work

- Temporary `removeUnlockRestrictions` support defaults to false but remains in source. The project handoff requires removing it before release.
- Packaged-JAR installation, second-player visibility/interactions and optional compatibility checks remain pending.
- `capStatusEffect` uses translated names, amplifier-based caps and switch fall-through. Its apparent hook is in `SpellforgedEffect`, but SPELLFORGED currently registers `SoulshockEffect`; do not describe the cap hook as a confirmed live failure. Establish reachability and intended caps before fixing or removing it.
- Deprecated APIs, unchecked category lookups, unused helper methods and duplicated particle code remain cleanup candidates, as listed in [development.md](development.md). Treat possible behavior changes separately from mechanical refactors.

The code is ready for reviewer feedback with these findings disclosed. A successful packaged-JAR launch alone is not release approval.
