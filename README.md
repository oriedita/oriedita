# Oriedita

Visit the [Oriedita website](https://oriedita.github.io) for more information.

_This a fork of Orihime オリヒメ and not affiliated with the original version._

Oriedita is a software used for drawing origami crease patterns. It comes with a wide range of tools to help you
draw crease patterns.

Oriedita can simulate folding a crease pattern and show if a crease pattern is flat-foldable, and show a
folded version of the crease pattern if it is.

The software is based on Orihime with added performance, stability and usage improvements. Notable improvements are:

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
least version 17).

You should be presented with the main interface of Oriedita.

![](https://imgur.com/dUT8JK6.png)

Use the mouse to draw lines on the crease pattern, using the ![MVEA](https://i.imgur.com/4vm5CND.png) buttons to change
the type of the fold line, and use the ![Fold](https://i.imgur.com/IDUV2Ss.png) button to try and fold the crease
pattern.

Clicking on buttons will update the help box with a help text for that button.

### Saving

<dl>
<dt>.ori</dt>
<dd>Oriedita file format .ori saves the crease pattern, customized colors and the camera position. Recommended when developing a new crease pattern as loading it brings you back to the same state as you left it.</dd>
<dt>.cp</dt>
<dd>The .cp file format only saves creases (no circles or state). More lightweight and portable and should be used when sharing with someone else. It can also be opened in other origami crease pattern softwares.</dd>
<dt>.fold</dt>
<dd>The .fold file format is more advanced format like .ori and can be used across different origami softwares.</dd>
</dl>

## Advanced usage

This section describes advanced topics such as custom configuration files. Not necessary for normal usage.

## Configuration

Configuration is saved to the following _configuration directories_:

- `%APPDATA%\oriedita` on Windows
- `~/Library/Application Support/oriedita` on Mac
- `~/.oriedita` on Linux and other systems

It should not be needed to access files in this directory yourself.

### Button configuration (hotkeys, names, help, tooltip)

Button configuration is found in these properties files: 
- [`hotkey.properties`](oriedita/src/main/resources/hotkey.properties)
- [`name.properties`](oriedita/src/main/resources/name.properties)
- [`help.properties`](oriedita/src/main/resources/help.properties)
- [`tooltip.properties`](oriedita/src/main/resources/tooltip.properties)
- [`icons.properties`](oriedita/src/main/resources/icons.properties)
- [`gif.properties`](oriedita/src/main/resources/gif.properties) 

Placing these files in the configuration directory or in the same directory as the jar will override the values, allowing customization of hotkeys or translations.

Read the JavaDoc
on [`javax.swing.KeyStroke.getKeyStroke(String)`](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String))
for more information on the format used for hotkeys. Wrong KeyStroke strings are reported to the consoleDialog.

### Application state

The application state is saved to `config.json` in the configuration directory. It contains information about the
application and is restored when the application starts.

## FAQ

### How do I reset Oriedita?

To reset all options in Oriedita to the default state, remove all files in the _configuration directory_, see
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
