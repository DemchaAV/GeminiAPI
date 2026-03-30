# Contributing

Thanks for contributing to `gemini-client`.

## Development Setup

1. Install JDK 21 and Maven 3.9 or newer.
2. Clone the repository.
3. Run `mvn clean test` before opening a pull request.

## Live API Work

- Use `GEMINI_API_KEY` for any manual smoke testing against the real Gemini API.
- Keep live-network checks out of the default Maven test lifecycle.
- Store temporary outputs outside tracked source folders.

## Pull Requests

- Keep changes focused and easy to review.
- Add or update tests for behavior changes.
- Update README or examples when the public API changes.
- Do not commit IDE settings, generated artifacts, or local logs.
