# Origami Editor

_This a fork of Orihime オリヒメ and not affiliated with the original version._ Orihime is an awesome tool that is used by origami designers to design new origami models. Orihime is developed by MT777 and can be downloaded from  http://mt777.html.xdomain.jp/. Undertrox developed orihimeMod, which adds some extra features to the Orihime software, this version can be downloaded from https://github.com/undertrox/orihimeMod

## [Download the latest version from the Releases page.](https://github.com/qurben/origami-editor/releases)

[Download the latest development version.](https://nightly.link/qurben/origami-editor/workflows/maven/master/origami-editor-jar.zip)

This project started as an effort to translate the Orihime source code to English and is based on Orihime version 3.060. After that more changes were made to improve the performance of the application.

One of the goals of this project is to understand the underlying algorithms used in Orihime.

Translating is still a work in progress, some translations can be quite poor or in some cases completely wrong. When in doubt a logical name was used. Comments were translated using online translation services and as such can contain weirdly structured sentences.

## Building & Running

This version of the code requires a JDK (at least version 8) and [maven](https://maven.apache.org/) to build.

```bash
mvn clean package
```

After compiling and packaging the jar is placed in the `target` directory, double click it or execute it with the java command.

```bash
javaw -jar ./target/origami-editor-0.0.6-SNAPSHOT.jar
```

## Configuration

Configuration is saved to the following directory, called the _configuration directory_:

- `%APPDATA%\origami-editor` on Windows
- `~/Library/Application Support/origami-editor` on Mac
- `~/.origami-editor` on Linux and other systems

It should not be needed to access files in this directory yourself.

### Button configuration (hotkeys, names, help, tooltip)

Button configuration is found in properties files [`hotkey.properties`](./src/main/resources/hotkey.properties), [`name.properties`](./src/main/resources/name.properties), [`help.properties`](./src/main/resources/help.properties), [`tooltip.properties`](./src/main/resources/tooltip.properties). Placing these files in the configuration directory or in the same directory as the jar will override the values, allowing customization of hotkeys or translations.

Read the JavaDoc on [`javax.swing.KeyStroke.getKeyStroke(String)`](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String)) for more information on the format used for hotkeys. Wrong KeyStroke strings are reported to the consoleDialog.

### Application state

The application state is saved to `config.json` in the configuration directory. It contains information about the application and is restored when the application starts.

## Terminology

| Class Name | Original Name | Description |
|---|---|---|
| Line | Bou | A line between two points in a PointSet
| Face | Men | A collection of connected points in a PointSet
| Point | Ten | A point as x and y coordinates
| LineSegment | Senbun | A line consisting of two points
| Polygon | Takakukei | A polygon consisting of multiple points
| Grid | Kousi | The background grid
| StraightLine | Tyokusen | A line with `a,b,c` such that `a * x + b * y + c = 0`
| Circle | En | A circle with x and y for position and r for radius.
| SubFace | Smen | Stack of faces in the folded view
| Drawing_Worker | Egaki_Syokunin | Responsible for drawing and handling user input on the canvas.
| HierarchyList_Worker | Jyougehyou_Syokunin | Responsible for calculating the hierarchy of folded models.
| HierarchyList | Jyougehyou | Keeps track of the height of faces in a crease pattern
| FoldedFigure | Oriagari_Zu | Contains an estimated folded crease pattern
| EquivalenceCondition | Touka_Jyouken | 
| FoldLineSet | Orisensyuugou |
| PointSet | Tensyuugou 
| LineSegmentSet | Senbunsyuugou |
| BulletinBoard | Keijiban | Notice at the top of the canvas
| Drawing_Worker | Egaki_Syokunin
| Drawing_Worker_Toolbox | Egaki_Syukunin_Dougubako
| GuideMap | Annaisyo
| Overlapping_Permutation_generator | Jyuufuku_Jyunretu_hasseiki
| CreasePattern_Worker | Tenkaizu_Syokunin
| SortingBox_int_double | Narabebako_int_double
| Background_camera | Haikei_camera
| StringOp | Moji_sousa

## Changes made

### Translations
See also [TRANSLATIONS.md](TRANSLATIONS.md).

Classnames, variable names and comments are translated to English.

### Code Quality

Integers with only values 1 and 0 are replaced with booleans. Integers with a specific set of values are replaced with enums. Dead code is removed.

Deprecated `Thread.stop` is replaced by `Thread.interrupted` checks.

Saving and history is handled by Java Serialization, giving a performance boost. (This will be replaced by a text-based, more portable format for saving).

Different input modes are handled by multiple classes, making the code more readable.

### Gui Improvements

Left over `java.awt` components are replaced by their `javax.swing` counterparts, this reduces flickering when resizing and interacting. The Look and Feel is changed to a more modern one (FlatLaf). The Gui is build using GUI Designer in IntelliJ, this allows for easier updating and managing of the layout.

The state management of the Gui is handled by Java Beans' PropertyChangeSupport to keep the Gui in sync with the application state.

A menu, hotkeys and tooltips are added.

Help is based on text and is draggable.

## Notes

Extended Fushimi (Husimi) Theorem (used in FoldLineSet) is a version of [Kawasaki's Theorem](https://en.wikipedia.org/wiki/Kawasaki%27s_theorem) for flatfoldability of vertices in a crease pattern, specifically for 4-crease vertices.
