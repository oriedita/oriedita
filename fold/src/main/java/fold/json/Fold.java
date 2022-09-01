package fold.json;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.stree.JrSimpleTreeExtension;

public class Fold {
    public static JSON json = JSON.builder()
            .register(new JrSimpleTreeExtension())
            .register(new FoldJacksonJrExtension())
            .build();
}
