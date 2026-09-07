# Session workflow

- Read README.md before continuing; it records port progress and the current testing handoff.
- After each completed project change, update README.md with the change, validation, and remaining work, then commit and push to origin. The user has explicitly requested this workflow; no additional push confirmation is needed.
- Never describe an in-game check as passed without runtime evidence. Keep pending checks explicit in the handoff.
- Keep debugger probes in IntelliJ, not in mod source. Local worlds, runtime configs, and debugger state are not transferred by Git.
