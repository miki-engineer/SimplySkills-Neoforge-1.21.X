# Session workflow

- Read README.md before continuing; it records port progress and the current testing handoff.
- After each completed project change, update README.md with the change, validation, and remaining work, then commit and push to origin. The user has explicitly requested this workflow; no additional push confirmation is needed.
- Keep README.md concise and current: edit the checkpoint and relevant sections in place. Do not append commit history, chronological session logs, or repeated progress entries.
- Never describe an in-game check as passed without runtime evidence. Keep pending checks explicit in the handoff.
- Reuse shared-effect verification from previously tested trees. Focus Ascendancy testing on its triggers, scaling, upgrades, and interactions; repeat shared mechanics only when a relevant change or concrete failure warrants it.
- Keep debugger probes in IntelliJ, not in mod source. Local worlds, runtime configs, and debugger state are not transferred by Git.
