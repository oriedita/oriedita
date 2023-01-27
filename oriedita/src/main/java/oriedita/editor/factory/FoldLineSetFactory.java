package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import origami.crease_pattern.FoldLineSet;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides specific fold line sets
 */
@Module
public class FoldLineSetFactory {
    @Named("auxlines")
    @Provides
    @Singleton
    FoldLineSet auxLinesFoldLineSet() {
        return new FoldLineSet();
    }

    @Named("foldlines")
    @Provides
    @Singleton
    FoldLineSet foldLineSet() {
        return new FoldLineSet();
    }
}
