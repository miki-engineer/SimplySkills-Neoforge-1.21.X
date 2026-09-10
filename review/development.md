# Development guide

## Where to change behavior

| Area | Location | Responsibility |
| --- | --- | --- |
| Mod setup | [SimplySkills.java](../src/main/java/net/sweenus/simplyskills/SimplySkills.java), [registry/](../src/main/java/net/sweenus/simplyskills/registry/) | Registration and configuration wiring |
| Skill node IDs | `util/SkillReferencePosition.java` | IDs from the skill-tree data; IDs can repeat between categories |
| Activation and cooldown | `abilities/SignatureAbilities.java` | Select and dispatch the active ability, then send its cooldown |
| Class mechanics | `abilities/*Abilities.java` | Class-specific triggers, targeting and upgrades |
| Effect lifecycle | `effects/`, `mixins/` | Effect ticks, combat callbacks, expiry and removal hooks |
| Shared operations | `util/HelperMethods.java` | Target relations, stacks, attributes and common world operations |
| Active-skill HUD | `network/ModPacketHandler.java` | Ordered signature/Ascendancy metadata, payloads and icon selection |
| Spells and visuals | `src/main/resources/data/simplyskills/spell/`, `client/`, assets | Spell Engine definitions, rendering, models and animations |
| Skill trees and translations | `src/main/resources/data/`, `src/main/resources/assets/simplyskills/lang/` | Unlock data, rewards, descriptions and localized text |

Java locations above are relative to `src/main/java/net/sweenus/simplyskills/`.

## Conventions that matter

- Skill IDs are only unique within their category. Preserve both when matching a signature. The ordered lists in `ModPacketHandler` are also the priority order when multiple active nodes are unlocked; each entry owns its HUD sprite name.
- Minecraft effect durations are ticks (20 per second). Stack counts are usually amplifier + 1. Check each caller's units before changing a shared helper.
- `decrementStatusEffect` delegates to `decrementStatusEffects` with one stack. The latter now removes the effect when the requested removal reaches or exceeds the current stack count; positive remainders retain the existing duration and replacement behavior. Post-fix exact, partial and excess removal passed through Exhaustion decay; Righteous Shield retains its separate guard.
- Spell Power haste 1.0 is the baseline multiplier, not a bonus. The cooldown calculation subtracts only the excess above 1.0.
- Registry objects and client rendering must remain safe for dedicated-server loading. Keep packet identifiers and codec field order stable unless intentionally changing the network protocol.
- Some behavior comes from Spell Engine JSON rather than Java. Follow activation, dispatched spell, effect and projectile together before changing damage or animation.
- Default config changes do not overwrite saved runtime configs. Local client and dedicated-server settings live in different directories.

## Verification workflow

1. Read the [review index](README.md) and the relevant evidence in [verification.md](verification.md).
2. Make a focused change. Preserve existing formulas, priority and side effects during a refactor.
3. Run `./gradlew build` (Windows: `.\gradlew.bat build`) and checks appropriate to the change.
4. Restart affected Minecraft processes before claiming the changed Java code works in-game. Record static/build checks separately from user observations and debugger evidence.
5. Update the public review notes and relevant evidence in place with changes and validation limits. Keep private session handoffs separate.

Runtime probes belong in IntelliJ, not mod source. Existing shared-effect verification can be reused; request another in-game check only for a specific changed path or unresolved issue.

## Review separately from this refactor

`HelperMethods.capStatusEffect` matches translated display names and uses a fall-through switch. Its cap semantics need a focused behavior review before changing them. The shared multi-stack depletion guard was corrected after runtime reproduction; exact, excess and partial post-fix checks passed through Exhaustion decay; Aegis partial consumption (40 minus 35) and exact depletion (35 minus 35) also passed. These are potential behavior fixes, not formatting changes; they were preserved in this cleanup.

IDE inspection also reports unchecked category lookups in respecialisation/level helpers, deprecated attribute/experience APIs, unused ranged-attribute helpers and duplicated particle placement loops. These pre-existing warnings need caller and compatibility review before removal or behavior changes; no IDE errors were reported in the two refactored files.
