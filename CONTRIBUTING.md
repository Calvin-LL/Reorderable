# Contributing to Reorderable

Thank you for your interest in contributing to Reorderable.

## Development setup

Open this project with **Android Studio Preview**.

Install the [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) before opening the project.

## Running the demo app

### Android

Open the project in Android Studio and run the demo app.

### iOS

Open the `iosApp` project in Xcode and run the app, or add the iOS run configuration to Android Studio (requires the KMM plugin).

### Web

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

### Desktop

```bash
./gradlew :demoApp:composeApp:run
```

## Building the library

```bash
./gradlew :reorderable:compileKotlinJvm :demoApp:composeApp:compileDebugKotlinAndroid
```

## Pull requests

1. Fork the repository and create a feature branch from `main`.
2. Keep changes focused and include a clear description of the problem and solution.
3. Verify the demo app and library compile locally before opening a PR.
4. Link related issues when applicable.

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).
