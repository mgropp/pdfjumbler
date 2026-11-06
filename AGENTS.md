# AGENTS.md

PdfJumbler is a Swing desktop application for rearranging, merging, deleting and rotating
pages of PDF files. It is written in Java 21 and built with Gradle (Kotlin DSL). PDF handling
uses Apache PDFBox, the look and feel is FlatLaf, and SVG icons are rendered with Batik.

## Commands

| Task | Command |
|---|---|
| Build the runnable fat jar (`build/libs/pdfjumbler.jar`) | `./gradlew` (default task `jar`) |
| Run tests | `./gradlew test` |
| Format sources | `./gradlew spotlessApply` |
| Check formatting | `./gradlew spotlessCheck` |
| Install the pre-push hook (runs `spotlessCheck`) | `./gradlew installGitHooks` |
| Run the app | `java -jar build/libs/pdfjumbler.jar [files.pdf...]` |

## Before every commit

1. Run `./gradlew spotlessApply` so the sources are formatted.
2. Run `./gradlew spotlessCheck test jar` and make sure it passes.

Keep commits small and focused, one logical change each, and make sure each one builds and passes
the tests on its own. When fixing a bug, add a test that fails without the fix and commit them
together.

### Commit Messages

Start the subject line with a [gitmoji](https://gitmoji.dev) as a Unicode emoji (not a `:code:`),
followed by a space and an imperative summary, for example `🐛 Keep inherited page attributes when saving`.
Pick the single gitmoji that best describes the change. The ones used in this repository are:

| Gitmoji | Use for |
|---|---|
| ✨ | New features |
| 🐛 | Bug fixes |
| ♻️ | Refactoring without behavior change |
| ✅ | Adding or updating tests |
| 📝 | Documentation |
| 💄 | UI and style |
| 💬 | Text and literals, including translations |
| ✏️ | Typos |
| 🎨 | Code formatting and structure |
| 🏷️ | Types and null-safety annotations |
| 🚨 | Compiler and linter warnings |
| 🔥 | Removing code or files |
| 🚚 | Moving or renaming files and packages |
| ⬆️ | Upgrading dependencies or the Java version |
| 🔧 | Build and tooling configuration |
| 👷 | CI workflows |
| 🙈 | `.gitignore` |

## Build Setup

- Dependency and plugin versions live in the version catalog `gradle/libs.versions.toml`.
  Reference them as `libs.…` in `build.gradle.kts`; never hard-code versions there.
- The jar task bundles the runtime classpath into a single fat jar with
  `io.gropp.pdfjumbler.PdfJumbler` as its main class.
- Java sources are formatted with Palantir Java Format via Spotless (4-space indentation).
  `build.gradle.kts` and `gradle/libs.versions.toml` are not covered by Spotless; `build.gradle.kts` uses tabs.
- Error Prone runs on every compilation. NullAway is configured as an error, so null-safety
  violations fail the build. Other Error Prone checks report warnings; keep the build free of them.

## Null Safety

Every package has a `package-info.java` annotated with JSpecify's `@NullMarked`, so all types
are non-null by default. New packages need a `package-info.java` with `@NullMarked` as well.

- Mark fields, parameters and return types that may hold `null` with
  `org.jspecify.annotations.Nullable`, using type-use placement (`private @Nullable String name;`).
- Prefer `Optional` or empty collections over returning `null` where that keeps the API clear.
- `equals` overrides take a `@Nullable Object`.

## Code Layout

All code is under `src/main/java/io/gropp/pdfjumbler/`:

| Package | Contents |
|---|---|
| `io.gropp.pdfjumbler` | Main window (`PdfJumbler`), page lists (`PdfList`, `PdfCellRenderer`), `DocumentManager`, icons, progress dialog |
| `…actions` | Swing `Action`s for toolbar/menu entries, background workers for opening and saving, `RotatePagesEdit` |
| `…jdragdroplist` | Generic drag-and-drop list (`JDragDropList`), undoable list and model (`UndoableList`, `UndoableListModel`), `ListOperations` |
| `…pdf` | Processing abstractions: `Page`, `PdfEditor`, `PdfRenderer`, plugin loading (`Plugin`, `PluginDiscovery`), `PdfProcessingFactory` |
| `…pdfbox` | PDFBox implementations of `PdfEditor` and `PdfRenderer`, `ImageRotation` |
| `…i18n` | `I18nKeys` and `ListResourceBundle` translations |

### Key Concepts

- **Pages:** `Page` is a mutable reference to a page (file, page index, rotation) and is not a copy
  of the page content. `DocumentManager` counts the pages of each file once and returns new,
  independent `Page` instances on every call, so the same file can be opened several times. It keeps
  track of all instances it handed out so that `moveAsideIfOpen` can redirect them to the moved file.
- **Undo:** all list changes go through `UndoableList`/`UndoableListModel`, which emit undoable edits
  to the main window's `UniqueUndoManager`. Group multi-step changes into one compound edit with
  `beginCompoundEdit`/`endCompoundEdit` so that a single undo reverts them.
- **Editors and renderers:** `PdfProcessingFactory.getInstance()` holds the active `PdfEditor` and
  `PdfRenderer`. They are selected from the built-in PDFBox classes plus classes from plugin jars,
  in this order: system property `pdfjumbler.editor` / `pdfjumbler.renderer`, then the stored user
  preference, then the first available. The startup wiring (plugin path, error dialogs,
  `System.exit`) is in `PdfProcessingSetup`; the factory itself can be constructed directly in tests.
- **Plugins:** jar files in the plugin directory (system property `pdfjumbler.pluginpath`; by default
  the directory containing `pdfjumbler.jar`). Each plugin jar contains a `pdfjumbler-plugin.properties`
  file with `pdfjumbler-required-version` (compared against `PdfJumbler.VERSION`) and
  comma-separated class names under `pdfjumbler-plugin-editor` and/or `pdfjumbler-plugin-renderer`.
- **Preferences:** user settings are stored via `java.util.prefs` under the `io.gropp.pdfjumbler`
  node; keys are in `ConfigKeys`.

### Translations

UI strings are keys in `I18nKeys` with values in `PdfJumblerResources` (English, default),
`PdfJumblerResources_de`, `PdfJumblerResources_es` and `PdfJumblerResources_ru`. When adding or
changing a string, update every translation. Keyboard accelerators are also stored in the resource
bundles.

### Resources

`src/main/resources/icons/` contains the SVG icons, loaded through `Icons`. The Windows icon
`pdfjumbler.ico` is generated from the SVGs with `make` (requires ImageMagick).
`src/main/resources/tinylog.properties` configures logging via tinylog.

## Tests

Tests are in `src/test/java`, mirror the main package structure, and use JUnit Jupiter and AssertJ.
They run headless (`java.awt.headless=true`), so they must not open windows or dialogs.

- Generate PDFs in tests with `TestPdfs` (each page has a distinct, index-dependent size so page
  identity can be checked after saving) and use JUnit's `@TempDir` for files.
- Never touch the real user preferences; pass `InMemoryPreferences` instead.
- Keep Swing-specific code thin, and put logic in classes that can be tested without a display
  (see `ListOperations`, `RotatePagesEdit` and `DocumentManager`).

## Versioning

- The application version is the Gradle property `version`. It defaults to `dev` in `gradle.properties`
  and is set with `-Pversion=…` for releases. It is written to the jar manifest as `Implementation-Version`.
- `AppVersion.current()` reads the version from the manifest and returns `dev` when there is none
  (for example when running from the IDE). The About dialog shows this version.
- `PdfJumbler.VERSION` is a separate integer that plugins check compatibility against
  (`pdfjumbler-required-version`). Only change it when the plugin API changes.

## CI and Releases

- `.github/workflows/ci.yml` runs `./gradlew spotlessCheck test jar` on every branch push and pull request.
- `.github/workflows/release.yml` runs when a tag of the form `v-<major>.<minor>.<patch>` is pushed
  (for example `v-0.1.0`). It runs the tests, builds the jar with the tag's version (`0.1.0`), builds
  the Windows installer with NSIS on Linux, and creates a GitHub release with `pdfjumbler.jar` and
  `setup-pdfjumbler.exe` attached.

## Packaging

- `pdfjumbler.nsi` is the NSIS script for the Windows installer. It expects `build/libs/pdfjumbler.jar`
  and writes `build/setup-pdfjumbler.exe`. Build it locally with `makensis pdfjumbler.nsi`.
- `setup/linux/install.sh` installs the latest GitHub release jar on Linux.
- `setup/linux/uninstall.sh` removes what `install.sh` installed, keeping `/opt/pdfjumbler` if it
  contains plugins.
