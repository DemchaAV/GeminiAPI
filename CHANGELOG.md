# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Maven Central publication metadata, signed release automation, and Maven Wrapper support.
- GitHub community health files, issue templates, Dependabot configuration, and release workflow scaffolding.
- Coverage reporting, static analysis tooling, and repository-level formatting standards.

### Changed

- Reworked the README into a publication-ready storefront with quick-start instructions, examples, and source-build guidance.
- Narrowed the logging implementation dependency to test scope so the library only exposes the SLF4J API to consumers.
- Added JPMS manifest metadata, source and Javadoc artifact generation, and Central Portal publishing configuration.

### Fixed

- Closed gaps in project metadata required for Maven Central validation.
- Tightened repository hygiene files so local build output, credentials, and editor state stay untracked.

[Unreleased]: https://github.com/DemchaAV/GeminiAPI/commits/master
