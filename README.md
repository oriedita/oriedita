# Oriedita

Visit the [Oriedita website](https://oriedita.github.io) for more information.

_This a fork of Orihime オリヒメ and not affiliated with the original version._

Oriedita is a computer program used for drawing origami crease patterns. It comes with a wide range of tools to help you
draw crease patterns.

Oriedita can simulate folding a crease pattern and show if a crease pattern is flat-foldable, and if it is, show a
folded version of the crease pattern.

The software is based on Orihime, it adds performance, stability and usage improvements. Notable improvements are:

- [Making it possible to fold very complex crease patterns](https://origami.abstreamace.com/2021/10/13/fold-a-full-ryujin-3-5-with-orihime-algorithm/)
  that would be impossible to fold in Orihime.
- Dark mode and improved Look and Feel
- Improved file save handling and better .cp support
- .FOLD file support
- Persisting application state
- Easier installation on Windows

## Usage

**[Download the latest version from the Releases page.](https://github.com/oriedita/oriedita/releases)**

[Download the latest development version.](https://nightly.link/oriedita/oriedita/workflows/maven/master/oriedita-jar.zip)

After downloading the latest version you can use the installer to install the software or run the jar using Java (at
least version 11).

You should be presented with the main interface of Oriedita.

![](https://i.imgur.com/JxoL28Z.png)

Use the mouse to draw lines on the crease pattern, using the ![MVEA](https://i.imgur.com/4vm5CND.png) buttons to change
the direction of the fold line, and use the ![Fold](https://i.imgur.com/IDUV2Ss.png) button to try and fold the crease
pattern.

Clicking on buttons will update the help box with a help text for that button.

### Saving

<dl>
<dt>.ori</dt>
<dd>You can save files using the Oriedita file format .ori, this saves the creasepattern, customized colors and the camera position. This format is recommended when developing a new crease pattern as loading it brings you back to the same state as you left it.</dd>
<dt>.cp</dt>
<dd>You can also use the .cp format to save files, this file format only saves creases (so no circles or state). But this format is more lightweight and portable and should be used to share a crease pattern with someone else. This format can also be opened in other origami crease pattern software.</dd>
</dl>

## Advanced usage

This section describes advanced usage, such as custom configuration files. This should not be needed for normal usage.

## Configuration

Configuration is saved to the following directory, called the _configuration directory_:

- `%APPDATA%\oriedita` on Windows
- `~/Library/Application Support/oriedita` on Mac
- `~/.oriedita` on Linux and other systems

It should not be needed to access files in this directory yourself.

### Button configuration (hotkeys, names, help, tooltip)

Button configuration is found in properties files [`hotkey.properties`](./src/main/resources/hotkey.properties)
, [`name.properties`](./src/main/resources/name.properties), [`help.properties`](./src/main/resources/help.properties)
, [`tooltip.properties`](./src/main/resources/tooltip.properties). Placing these files in the configuration directory or
in the same directory as the jar will override the values, allowing customization of hotkeys or translations.

Read the JavaDoc
on [`javax.swing.KeyStroke.getKeyStroke(String)`](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String))
for more information on the format used for hotkeys. Wrong KeyStroke strings are reported to the consoleDialog.

### Application state

The application state is saved to `config.json` in the configuration directory. It contains information about the
application and is restored when the application starts.

## FAQ

### How do I reset Oriedita?

To reset all options in Oriedita to the default state remove all files in the _configuration directory_, see
the [Configuration](#Configuration) section to find this directory on your operating system.

### Oriedita does not fit on my screen

Some installations of windows use scaling to make everything larger and easier to use. This can cause Oriedita to become
larger than the screen. Disable ui scaling to prevent this.

When running Oriedita from the jar, execute the jar as follwos:

```bash
java -jar -D"sun.java2d.uiScale=1" oriedita-vX.X.X.jar
```

When using the installer or portable installation navigate to the directory of the installation (C:\Program
Files\Oriedita) and edit the `Oriedita.cfg` file in the `app` directory, add the following line at the end of the file.

```
java-options=-Dsun.java2d.uiScale=1
```

## Notes

### Orihime

Orihime is an awesome tool that is used by origami designers to design new origami models. Orihime is developed by MT777
and can be downloaded from  http://mt777.html.xdomain.jp/. Undertrox developed orihimeMod, which adds some extra
features to the Orihime software, this version can be downloaded from https://github.com/undertrox/orihimeMod

### Theory

Extended Fushimi (Husimi) Theorem (used in FoldLineSet) is a version
of [Kawasaki's Theorem](https://en.wikipedia.org/wiki/Kawasaki%27s_theorem) for flatfoldability of vertices in a crease
pattern, specifically for 4-crease vertices.
