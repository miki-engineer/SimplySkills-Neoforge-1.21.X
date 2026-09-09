# Port review documentation

This directory restores the development, porting, verification and release-review notes to the current branch. Reviewers do not need a cached branch or the private session handoff. The notes were previously removed in commit `890cd40`; these copies retain the recorded evidence and point to public review documents for remaining work.

- [Development guide](development.md): code navigation, build command and implementation conventions.
- [Porting notes](porting-notes.md): platform changes, balance changes and corrections to inherited behavior.
- [Verification evidence](verification.md): recorded debugger measurements, user observations and unverified cases.
- [Remaining code review](release-review.md): unresolved concerns and release checks.

The verification notes describe prior observations, not a fresh test of every path in the current revision. Later changes and pending retests are identified explicitly. Raw debugger state, local worlds and runtime configs are not included. The private `docs/handoff.md` is not required to review the code.

Keep these public notes current when implementation or validation changes. Keep the root README focused on players.
