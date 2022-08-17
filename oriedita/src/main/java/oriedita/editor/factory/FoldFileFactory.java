package oriedita.editor.factory;

import dagger.Module;
import oriedita.editor.export.Fold;

@Module
public abstract class FoldFileFactory {
    abstract Fold fold();
}
