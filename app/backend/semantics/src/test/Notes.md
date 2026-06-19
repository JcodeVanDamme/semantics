# Test Suite Notes

## Purpose

The Tests contained in this directory are **not intended to constitute a complete verification of this codebase and acknowledged to be incomprehensible**. Their primary Purpose was to support development by validating individual implementation steps and to ensuring Correctness in accordance to the underlying Papers.

In particular, many tests were written to:

- Verify intermediate Implementation Steps.
- Validate Assumptions made during the Development.
- Serve as regression Tests to make sure that refactorings didn't break previous Behaviour.

## Scope and Limitations

The Presence of a Test generally does **not mean that the given Feature, Class or Function has beend extensively tested**. The Absence of a Test should not be interpreted as an indication that a feature is deemed unimportant.

The current test suite does **not** attempt to provide:

- Extensive functional Coverage.
- Extensive Edge-Case Coverage.
- Performance or stress testing.

Many Tests focus on specific Examples derived from the underlying Papers and Note the relevant Pages where the Test-Data and Assertions were pulled from.