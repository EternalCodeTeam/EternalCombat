# Repository Guidelines

## Project Structure & Module Organization
This repository is a Gradle multi-module Java project:
- `eternalcombat-plugin/` - main Bukkit/Paper plugin implementation (`src/main/java`), runtime config/resources, and shaded jar output.
- `eternalcombat-api/` - public API module for external integrations.
- `buildSrc/` - shared Gradle convention plugins (Java toolchain, test setup, publishing, repositories).
- `assets/` - README/media assets.
- `.github/workflows/` - CI and publishing pipelines.

Keep new code inside the appropriate module and package (`com.eternalcode...`). Avoid cross-module leakage: API contracts go in `eternalcombat-api`, implementation details stay in `eternalcombat-plugin`.

## Build, Test, and Development Commands
Use the Gradle wrapper from repo root:
- `./gradlew clean build` - full build for all modules.
- `./gradlew test` - runs unit tests (JUnit Platform).
- `./gradlew :eternalcombat-plugin:shadowJar` - builds the distributable plugin jar.
- `./gradlew :eternalcombat-plugin:runServer` - starts a local Paper test server with required plugins.

CI currently builds with `shadowJar`, so verify that task before opening a PR.

## Coding Style & Naming Conventions
- Follow `.editorconfig`: UTF-8, LF, 4-space indentation (YAML: 2 spaces).
- Java toolchain is configured in Gradle conventions; keep language features compatible with the configured `release`.
- Use clear, domain-oriented class names (`FightActionBlockerController`, `CombatPlugin`).
- Keep packages lowercase and class names `PascalCase`; methods/fields `camelCase`; constants `UPPER_SNAKE_CASE`.

## Testing Guidelines
Testing is configured through `buildSrc` with JUnit Jupiter and `useJUnitPlatform()`.
- Preferred test location: module-local `test/` directory (as defined by convention plugin).
- Name test classes `*Test` and methods by behavior (for example, `shouldBlockCommandDuringCombat`).
- Run `./gradlew test` before each PR. Add tests for bug fixes and behavior changes.

## Commit & Pull Request Guidelines
Recent history follows Conventional Commit-style prefixes (`feat:`, `refactor:`, `dependency:`). Use concise, imperative messages.

For pull requests:
- Describe what changed and why (required by `.github/PULL_REQUEST_TEMPLATE.md`).
- Link related issues when applicable.
- Include verification notes (commands run, test results, and, if relevant, gameplay screenshots/logs).

## Security & Configuration Tips
Publishing uses environment tokens (for example `MODRINTH_TOKEN`, `HANGAR_API_TOKEN`). Never commit secrets; use local environment variables or CI secrets only.
