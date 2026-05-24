---
name: verify
description: Run Checkstyle and tests in sequence. Use after making changes to confirm the build is clean before committing.
---

Run the following commands in order and report any failures:

1. `mvn validate` — runs both Checkstyle executions as configured in pom.xml: `lint-sources` (with `checkstyle-suppressions.xml`) and `lint-tests` (with `checkstyle-suppressions.xml` + `checkstyle-suppressions-tests.xml`). Do **not** use `mvn checkstyle:check` — that invokes the default CLI execution which ignores the `suppressionsLocation` configured in the pom.
2. `mvn test -Dcheckstyle.skip=true` — unit and integration tests (TestContainers will spin up real database containers). Checkstyle is skipped here because it already ran in step 1.

If Checkstyle fails, show the violation messages and the affected file/line. Do not proceed to tests if Checkstyle fails.

If tests fail, show the failed test names, the failure messages, and any relevant stack trace excerpts.

Report a clear pass/fail summary at the end.
