package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import origami.crease_pattern.FoldLineSet;

/**
 * Provides specific fold line sets
 */
@ApplicationScoped
public class FoldLineSetFactory {
    @Named("auxlines")
    @Produces
    @ApplicationScoped
    FoldLineSet auxLinesFoldLineSet() {
        return new FoldLineSet();
    }

    @Named("foldlines")
    @Produces
    @ApplicationScoped
    FoldLineSet foldLineSet() {
        return new FoldLineSet();
    }
}
