# Contributing

## Building & Running

This version of the code requires a JDK (at least version 8) and [maven](https://maven.apache.org/) to build.

```bash
mvn clean package
```

After compiling and packaging the jar is placed in the `target` directory, double click it or execute it with the java command.

```bash
javaw -jar ./target/origami-editor-0.0.10-SNAPSHOT.jar
```

## (Technical) Changes made

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

## Terminology

When refering to the original Orihime Source code the following table can be used to translate class names.

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
| CreasePattern_Worker | Egaki_Syokunin | Responsible for drawing and handling user input on the canvas.
| FoldedFigure_Worker | Jyougehyou_Syokunin | Responsible for calculating the hierarchy of folded models.
| HierarchyList | Jyougehyou | Keeps track of the height of faces in a crease pattern
| FoldedFigure | Oriagari_Zu | Contains an estimated folded crease pattern
| EquivalenceCondition | Touka_Jyouken |
| FoldLineSet | Orisensyuugou |
| PointSet | Tensyuugou
| LineSegmentSet | Senbunsyuugou |
| BulletinBoard | Keijiban | Notice at the top of the canvas
| Drawing_Worker_Toolbox | Egaki_Syukunin_Dougubako
| GuideMap | Annaisyo
| ChainPermutationGenerator | Jyuufuku_Jyunretu_hasseiki
| CreasePattern_Worker | Tenkaizu_Syokunin
| SortingBox | Narabebako_int_double
| Background_camera | Haikei_camera
| StringOp | Moji_sousa