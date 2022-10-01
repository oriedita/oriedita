package fold.json;

import com.fasterxml.jackson.jr.ob.JacksonJrExtension;
import com.fasterxml.jackson.jr.ob.api.ExtensionContext;
import com.fasterxml.jackson.jr.ob.api.ReaderWriterProvider;
import com.fasterxml.jackson.jr.ob.api.ValueReader;
import com.fasterxml.jackson.jr.ob.api.ValueWriter;
import com.fasterxml.jackson.jr.ob.impl.JSONReader;
import com.fasterxml.jackson.jr.ob.impl.JSONWriter;
import fold.json.impl.FoldFileValueWriter;
import fold.json.impl.FoldFileValueReader;
import fold.json.impl.FoldFrameValueReader;
import fold.json.impl.FoldFrameValueWriter;
import fold.model.FoldFile;
import fold.model.FoldFrame;

public class FoldJacksonJrExtension extends JacksonJrExtension {
    @Override
    protected void register(ExtensionContext ctx) {
        ctx.appendProvider(new FoldHandlerProvider());
    }

    private static class FoldHandlerProvider extends ReaderWriterProvider {
        @SuppressWarnings("unchecked")
        public ValueReader findValueReader(JSONReader readContext, Class<?> type) {
            if (type == FoldFrame.class) {
                return new FoldFrameValueReader();
            }
            if (FoldFile.class.isAssignableFrom(type)) {
                return new FoldFileValueReader<>((Class<FoldFile>) type);
            }
            return null;
        }

        public ValueWriter findValueWriter(JSONWriter writeContext, Class<?> type) {
            if (type == FoldFrame.class) {
                return new FoldFrameValueWriter();
            }
            if (FoldFile.class.isAssignableFrom(type)) {
                return new FoldFileValueWriter();
            }
            return null;
        }
    }
}
