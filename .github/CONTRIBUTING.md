# 👋 Contributing to EternalCombat

Thanks for taking the time to contribute! This page covers everything you need to get started.

## 🛠️ Requirements

- **JDK 21** (the project targets Java 21)
- Git

The Gradle wrapper is committed, so you don't need Gradle installed.

## 🔨 Building and testing

```bash
git clone https://github.com/EternalCodeTeam/EternalCombat.git
cd EternalCombat
./gradlew build
```

`./gradlew build` compiles both modules, runs the unit tests and produces the shaded plugin jar in
`eternalcombat-plugin/build/libs/`.

To run only the tests:

```bash
./gradlew test
```

To try your changes on a real server, start a Paper server with the plugin and its dependencies
already installed:

```bash
./gradlew runServer
```

## 📏 Code style

Formatting rules live in [`.editorconfig`](../.editorconfig) — 4 spaces, UTF-8, LF endings.
Enable EditorConfig support in your IDE and match the style of the surrounding code.

## 🚀 Pull requests

1. Fork the repository and create a branch off `master`.
2. Keep the change focused — one topic per pull request.
3. Add or update tests when you change behaviour that can be tested without a server.
4. Make sure `./gradlew build` passes before opening the PR.
5. Prefix the PR title with the type of change, for example
   `feat:`, `fix:`, `chore:`, `docs:`, or `GH-<issue>` when it closes an issue.
6. Describe what you changed and why — the pull request template is intentionally short.

## 📄 License

By contributing, you agree that your contributions will be licensed under the
[Apache License 2.0](../LICENSE).
