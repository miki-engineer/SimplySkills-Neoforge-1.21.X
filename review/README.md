# Port review documentation

This directory restores the development, porting, verification and release-review notes to the current branch. Reviewers do not need a cached branch or the private session handoff. The notes were previously removed in commit `890cd40`; these copies retain the recorded evidence and point to public review documents for remaining work.

- [AI usage disclosure](../AI_USAGE.md): implementation involvement, review locations, original credits and authorship limits.
- [Development guide](development.md): code navigation, build command and implementation conventions.
- [Porting notes](porting-notes.md): platform changes, balance changes and corrections to inherited behavior.
- [Verification evidence](verification.md): recorded debugger measurements, user observations and unverified cases.
- [Remaining code review](release-review.md): unresolved concerns and release checks.

The verification notes describe prior observations, not a fresh test of every path in the current revision. Later changes and pending retests are identified explicitly. Raw debugger state, local worlds and runtime configs are not included. The private `docs/handoff.md` is not required to review the code.

## Continuing on another computer

Pull the current `main` branch before continuing. The public notes in this directory travel with Git; the private handoff, ignored runtime worlds/configs, RCON helper and IntelliJ debugger state do not. References to a running Client or local settings in the evidence describe the recorded test session, not the current computer.

Use the [development guide](development.md#verification-workflow) for build and validation steps, the [dedicated development server notes](verification.md#dedicated-development-server) for the recorded local setup, and the [release checks](release-review.md#release-gates-and-lower-priority-work) to select remaining tests. Reuse completed evidence at its stated scope and recreate any required local setup before testing.

Keep these public notes current when implementation or validation changes. Return to the [project README](../README.md) for the player overview.
