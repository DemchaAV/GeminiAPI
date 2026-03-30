# Contributing

Thank you for investing time in `gemini-client`.

## Report Bugs

Use the GitHub issue chooser to open a bug report with the project template:

- https://github.com/DemchaAV/GeminiAPI/issues/new/choose

Include the library version, JDK version, operating system, a minimal reproduction, and any relevant stack traces.

## Suggest Features

Use the feature request template in the same issue chooser and describe:

- the problem you are solving
- the API shape you expect
- any alternatives you evaluated first

## Development Setup

1. Clone the repository.
2. Install JDK 21 and Maven 3.9 or newer.
3. Run `./mvnw clean verify`.
4. Set `GEMINI_API_KEY` only if you plan to run manual live API checks outside the default test suite.

## Code Style

- Java sources use UTF-8, LF line endings, and the shared rules in `.editorconfig`.
- Formatting is defined by Spotless and the `quality` Maven profile.
- Static checks live in `checkstyle.xml` and the `quality` Maven profile.
- Keep public APIs documented and validate public method inputs clearly.

## Branch Naming

Use a focused branch name that reflects the change:

- `feature/<short-description>`
- `fix/<short-description>`
- `docs/<short-description>`
- `chore/<short-description>`

## Commit Messages

Conventional Commits are recommended:

- `feat: add streaming helper for multipart responses`
- `fix: handle null usage metadata safely`
- `docs: clarify snapshot publishing steps`

## Pull Request Process

Before opening a pull request:

1. Rebase or merge from the latest default branch.
2. Run `./mvnw clean verify`.
3. Run `./mvnw -Pquality spotless:check checkstyle:check spotbugs:check`.
4. Update tests and documentation for any behavior change.
5. Add a `CHANGELOG.md` entry under `Unreleased` when the change is user-visible.

Each pull request should include:

- a concise description of the change
- linked issues when applicable
- notes about API, behavior, or compatibility impacts
- tests covering the new behavior or the bug fix

## Testing Expectations

- All pull requests must pass CI before merge.
- New features should include tests when practical.
- Bug fixes should include a regression test whenever the failure can be reproduced offline.
- Keep network-dependent checks out of the default Maven test lifecycle.

## Code of Conduct

By participating in this project, you agree to follow the guidelines in [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
