# Session workflow

- Read README.md and docs/handoff.md before continuing; the latter records port progress and the current testing handoff.
- After each completed project change, update docs/handoff.md with the change, validation, and remaining work, then commit and push to origin. Update the public README.md only when player-facing information changes. The user has explicitly requested this workflow; no additional push confirmation is needed.
- Keep README.md short and public-facing; keep the checkpoint in docs/handoff.md current by editing relevant sections in place. Do not append commit history, chronological session logs, or repeated progress entries.
- Never describe an in-game check as passed without runtime evidence. Keep pending checks explicit in the handoff.
- Reuse shared-effect verification from previously tested trees. Focus Ascendancy testing on its triggers, scaling, upgrades, and interactions; repeat shared mechanics only when a relevant change or concrete failure warrants it.
- Keep debugger probes in IntelliJ, not in mod source. Local worlds, runtime configs, and debugger state are not transferred by Git.
