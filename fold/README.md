# Java Fold File Import / Export

This module imports and exports .fold files.

See the [specification](https://github.com/edemaine/fold/blob/main/doc/spec.md) of fold files for more information. 

## Inner workings

The [Jackson](https://github.com/FasterXML/jackson) library is used to convert a fold file (which is actually a JSON file) to a Java representation. This representation is found in the `fold.model.internal` package. The internal representation maps directly to a fold file and isn't a logical Java class. 

The internal representation is then mapped to another representation, found in the `fold.model` package. This representation is easier to use and makes sure that relations between the different values in the fold file is represented in a Java way.

## Extending

Use composition to create a fold file with your own properties. The custom properties map is untyped, so you will need to add additional validation here.

Example:

```java
import org.tinylog.Logger;

class MyFoldFile {
    private final FoldFile foldFile;

    public MyFoldFile(FoldFile foldfile) {
        this.foldFile = foldfile;
    }

    public void setMyProperty(MyProperty myProperty) {
        foldFile.setCustomProperty("my", "prop_x", myProperty.getX());
        foldFile.setCustomProperty("my", "prop_y", myProperty.getY());
    }

    public MyProperty getMyProperty() {
        Object x = foldFile.getCustomProperty("my", "prop_x");
        Object y = foldFile.getCustomProperty("my", "prop_y");

        if (x == null || y == null) {
            return null;
        }

        try {
            return new MyProperty(((BigDecimal) x).doubleValue(), ((BigDecimal) y).doubleValue());
        } catch (ClassCastException ex) {
            Logger.warn("Encountered error in fold file.", ex);
            return null;
        }
    }
}
```