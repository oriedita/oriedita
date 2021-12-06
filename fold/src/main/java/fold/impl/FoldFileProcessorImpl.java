package fold.impl;

import fold.FoldFileProcessor;
import fold.model.FoldFile;

import java.util.ArrayList;
import java.util.List;

public class FoldFileProcessorImpl implements FoldFileProcessor {
    @Override
    public void process(FoldFile save) {
        List<String> keys = new ArrayList<>(save.getCustomPropertyMap().keySet());
        for (String key : keys) {
            if (!key.contains(":")) {
                save.getCustomPropertyMap().remove(key);
            }
        }
    }
}
