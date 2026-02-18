# Contributing to SoniFlac

Thanks for your interest in contributing to SoniFlac! This guide will help you get started.

## Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/Soniflac.git
   ```
3. Create a branch for your change:
   ```bash
   git checkout -b feat/your-feature-name
   ```
4. Make your changes and test them
5. Push and open a pull request against `main`

## Development Setup

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35

Build the FOSS debug variant for development (no Play Billing dependency):

```bash
./gradlew assembleFossDebug
```

## Code Style

- **Formatting**: ktlint with default rules (enforced in CI)
- **Static analysis**: detekt with default + Compose rules
- **Naming**: standard Kotlin conventions, no Hungarian notation

Run lint checks locally before pushing:

```bash
./gradlew ktlintCheck detekt
```

## Commit Messages

We use [Conventional Commits](https://www.conventionalcommits.org/):

| Prefix | Use for |
|--------|---------|
| `feat:` | New feature |
| `fix:` | Bug fix |
| `docs:` | Documentation only |
| `test:` | Adding or updating tests |
| `ci:` | CI/CD changes |
| `refactor:` | Code change that neither fixes a bug nor adds a feature |
| `chore:` | Maintenance tasks |

Examples:
```
feat: add sleep timer to now playing screen
fix: radio stream not resuming after phone call
docs: update build instructions in README
test: add unit tests for QueueManager
```

## Pull Requests

- PRs must include tests for new functionality
- All CI checks must pass before merge
- At least 1 review is required
- Keep PRs focused — one feature or fix per PR

## Architecture

Please read [ARCHITECTURE.md](ARCHITECTURE.md) before making structural changes. Key rules:

- Feature modules (`feature/`) never depend on each other
- All shared state flows through `core/` modules
- Every screen follows the MVVM + unidirectional data flow pattern
- Use sealed `Result` types for error handling — no silent catch blocks
- Add KDoc to all public interfaces and complex functions

## Reporting Issues

- Use the **Bug Report** template for bugs
- Use the **Feature Request** template for new ideas
- Search existing issues before opening a new one

## License

By contributing, you agree that your contributions will be licensed under the [GPL-3.0 License](LICENSE).
