# Remaining code review

Static review of shared stack handling, curse targeting and release setup. Stack depletion now has runtime evidence and a pending-validation fix; targeting findings remain static observations. This is not an exhaustive code audit. They are outside the completed HUD/helper refactor checks.

## Address before release

1. **Multi-stack depletion can recreate an invalid effect.** In `HelperMethods.decrementStatusEffects`, the removal branch checks only whether the original amplifier is below 1. With 35 stacks (amplifier 34), removing 35 constructs a replacement with amplifier -1. Crusader Aegis permits exactly this boundary with its default 35-stack cost; Exhaustion decay also removes variable amounts. Righteous Shield has a local guard, but other callers do not. Runtime Exhaustion decay confirmed repeated partial removal by five, then 5 minus 5 left one stack (next call reported one); no probe errors. The Aegis 35-stack path was not reached because decay ran first. Changed the shared guard to remove when stacksRemoved >= currentAmplifier + 1. IntelliJ compilation and Java 21 Gradle build passed; post-restart traces confirmed exact removal twice: stacks=5, remove=5, then effect=null, with no probe errors. Partial removal also passed: 7 minus 5 left two stacks and preserved the 1182-tick duration. About one second later, excess removal (2 minus 5) produced effect=null; no probe errors. The Aegis 35-stack path remains pending. The passed Barrier test covers the earlier single-stack implementation only.

2. **Curse/taunt targeting retains entity state on shared effect objects.** `AgonyEffect` and `TauntedEffect` store a mutable `target` field. Registered effect objects are shared across affected entities. Agony reads the victim's TAUNTED instance instead of AGONY, then retains the last target when subsequent victims have no taunt source. A taunted-and-cursed mob can therefore seed targeting for another cursed mob; ordinary/restored taunt instances can also leave stale state. Resolve the intended Agony behavior and use per-instance/local source data without retaining a target on the registry object. Verify with distinct mobs and casters, including a missing/restored source.

## Resolve targeting intent

Agony and Torment choose the nearest living entity before applying the friendly-fire filter. A friendly entity closer than an enemy can cause the cast to fail instead of choosing the nearest eligible enemy. Their description says nearest enemy. Confirm the intended rule, then filter eligible enemies before selecting the minimum distance if that is the intended behavior. Test a friendly mob nearer than a hostile one.

## Release gates and lower-priority work

- Temporary `removeUnlockRestrictions` support defaults to false but remains in source. The project handoff requires removing it before release.
- Packaged-JAR installation, second-player visibility/interactions and optional compatibility checks remain pending.
- `capStatusEffect` uses translated names, amplifier-based caps and switch fall-through. Its apparent hook is in `SpellforgedEffect`, but SPELLFORGED currently registers `SoulshockEffect`; do not describe the cap hook as a confirmed live failure. Establish reachability and intended caps before fixing or removing it.
- Deprecated APIs, unchecked category lookups, unused helper methods and duplicated particle code remain cleanup candidates, as listed in [development.md](development.md). Treat possible behavior changes separately from mechanical refactors.

The code is ready for reviewer feedback with these findings disclosed. A successful packaged-JAR launch alone is not release approval.
