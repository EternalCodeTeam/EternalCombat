# 👋 Contributing to EternalCombat

First off, thanks for taking the time to contribute! 🎉
We love user contributions and want to make it as easy as possible for you to get involved.

## 🛠️ Development Setup

To start contributing, you'll need to set up your environment:

1.  **Java 21**: We use Java 21 for development. Ensure you have a JDK 21 installed.
2.  **IDE**: We recommend [IntelliJ IDEA](https://www.jetbrains.com/idea/).
3.  **Git**: Ensure you have Git installed.

### 📥 Getting the Code

```bash
# Fork the repository on GitHub first!
# https://github.com/EternalCodeTeam/EternalCombat/fork
git clone https://github.com/YOUR-USERNAME/EternalCombat.git
cd EternalCombat
```

### 🔨 Building the Project

Run this command in your terminal:

```bash
./gradlew build
```

## 🤝 How to Contribute

1.  **Fork & Clone**: Fork the repo to your own account and clone it.
2.  **Branch**: Create a new branch for your feature or fix.
    ```bash
    git checkout -b feat/my-awesome-feature
    ```
3.  **Code**: Hack away! 💻
4.  **Test**: Run manual test by `runServer` to ensure you haven't broken anything.
    ```bash
    ./gradlew runServer
    ```
5.  **Push**: Push your changes to your fork.
6.  **Pull Request**: Open a PR against the `master` branch.

## 📏 Code Style

Make sure that your code adheres to the plugin's existing coding style.

## 🐛 Reporting Bugs

If you find a bug, please use the **Bug Report** issue template.
-   Check if the issue already exists.
-   Provide a minimal reproduction if possible.
-   Include logs and screenshots.

## 💡 Feature Requests

Have an idea? Use the **Feature Request** issue template.
-   Explain *why* this feature is useful.
-   Describe how it should look or behave.

## 📄 License
By contributing, you agree that your contributions will be licensed under the project's [License](../LICENSE).
